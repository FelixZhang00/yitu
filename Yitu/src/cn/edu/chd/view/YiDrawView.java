package cn.edu.chd.view;

import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import cn.edu.chd.domain.Tuyuan;
import cn.edu.chd.domain.PaintStyle;
import cn.edu.chd.graphics.Bezier;
import cn.edu.chd.graphics.BrokenLine;
import cn.edu.chd.graphics.BrokenLine.OnTurnListener;
import cn.edu.chd.graphics.Free;
import cn.edu.chd.graphics.Line;
import cn.edu.chd.graphics.Oval;
import cn.edu.chd.graphics.Polygn;
import cn.edu.chd.graphics.Rectangle;
import cn.edu.chd.values.ApplicationValues;

/**
 * @author Rowand jj
 *
 *�ṩ��ͼ������view
 *
 *����Ϊ��ͼ�ĺ�����
 *
 */
public class YiDrawView extends ImageView implements OnGestureListener
{
	private static final String TAG = "YiDrawView";

	/**
	 * ����
	 */
	private Canvas mCanvas = null;
	
	/**
	 * ��ǰͼԪ
	 */
	private Tuyuan mCurrentPaint = null;
	
	/**
	 * ������ɫ
	 */
	private int mPenColor = ApplicationValues.PaintSettings.PENCOLOR_DEFAULT;;
	/**
	 * ���ʴ�С
	 */
	private int mPenSize = ApplicationValues.PaintSettings.PENSIZE_DEFAULT;
	
	/**
	 * ����͸����
	 */
	private int mPenAlpha = ApplicationValues.PaintSettings.PEN_ALPHA_DEFAULT;
	
	/**
	 * ��ͼ�ı�������������Դ���Դ�bitmap�ǲ��ɱ��,���Ҫ�������bitmap���봴���丱������tempBitmap
	 */
	private Bitmap mBitmap = null;
	
	/**
	 * mBitmap�ĸ���������ֱ�Ӳ���
	 */
	private Bitmap tempBitmap = null;
	
	/**
	 * ����ondraw�����л���bitmap
	 */
	private Paint mPaint = null;
	
	/**
	 * ����ģʽ(ֱ�ߣ�������...)
	 */
	private int mPaintMode = -1;
	
	/**
	 * ������
	 */
	private int mBitmapWidth = 0;
	/**
	 * ������
	 */
	private int mBitmapHeight = 0;
	
	/**
	 * �ṩredo,undo�Ȳ���
	 */
	private PaintStack mPaintStack = null;
	
	/**
	 *��ͼ�Ƿ��һ�α����� 
	 */
	private boolean isFirstCreated = true;
	
	private boolean isTouchUp = false;
	
	/**
	 * ������ʽ
	 */
	private int paintStyleValue = -1;
	
	/**
	 * �Ƿ���
	 * 	��������ʱ���𶯴��������۵�
	 */
	private boolean isShaked = false;
	
	/**
	 * ����ʶ����
	 */
	private GestureDetector mGestureDetector = null;
	
	/**
	 * ��ǰ��ѡ�е�ͼԪ
	 */
	private Tuyuan checkedElement = null;
	
	/**
	 * �Ƿ����϶�ͼԪ״̬
	 */
	private boolean shouldDrag = false;
	
	/**
	 * ����ͼԪ�����
	 */
	private float srcX,srcY;
	
	/**
	 * ��Ƥģʽ
	 */
	private boolean eraserMode = false;
	
	/**
	 * ���ģʽ
	 */
	private boolean fillMode = false;
	
	private Tuyuan copedTuyuan = null;
	
	private boolean pasteFlag = false;
	
	/**
	 * ����ģʽ
	 */
	private boolean zoomMode = false;
	
	/**
	 * ��תģʽ
	 */
	private boolean rotateMode = false;
	
	/* ������ */
	public YiDrawView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		mCanvas = new Canvas();
		mPaint = new Paint(Paint.DITHER_FLAG);
	
		//����Ĭ�ϻ�����ʽ
		paintStyleValue = ApplicationValues.PaintStyle.MODE_PLAIN_PEN;
		//Ĭ��Ϊ�����ֻ�ͼԪ
		setCurrentTuyuanType(ApplicationValues.TuyuanStyle.STYLE_FREE);
		mPaintStack = new PaintStack(this);
		
		mGestureDetector = new GestureDetector(context,this);
	}
	 
	/**
	 * �ⲿ���û����ӿڣ���ص���
	 */
	public void setImageBitmap(Bitmap image)
	{
		this.mBitmap = image;
		postInvalidate();
	}
	@Override
	public void setLayoutParams(LayoutParams params)
	{
		super.setLayoutParams(params);
		postInvalidate();
	}
	/**
	 * ��dispatchTouchEvent�����иı�touch�¼��ķַ�˳��������ʶ�����Ʋ�����
	 * ������Ʋ����ɹ��ˣ�ʹ��һ��ͼԪ�����϶�װ̬������ô��ֹ��ͼ������������move
	 * �¼���ͼԪ����ƽ�Ƶȱ任,�������ִ�л�ͼ����.
	 * */
	@Override
	public boolean dispatchTouchEvent(MotionEvent event)
	{
		mGestureDetector.onTouchEvent(event);//��ʶ������
		
		if(eraserMode)//�������Ƥģʽ,�����ػ�ͼ����
		{
			return false;
		}
		if(shouldDrag)//��������϶�״̬�����ֹ��ͼ����
		{
			switch (event.getAction())
			{
			case MotionEvent.ACTION_MOVE:
				deleteTuyuan(checkedElement);//ɾ��ԭ��ͼԪ
				checkedElement.translate(event.getX()-srcX,event.getY()-srcY);//ƽ��
				srcX = event.getX();
				srcY = event.getY();
				invalidate();
				Log.i(TAG,"=================MOVE");
				break;
			case MotionEvent.ACTION_UP:
				if(shouldDrag)
				{
					shouldDrag = false;//ÿ��up������϶�״̬
					checkedElement.draw(mCanvas);//��������λ���ϻ���ͼԪ
					mPaintStack.push(checkedElement);//����ջ��
					invalidate();
				}
			}
			return false;
		}
		return super.dispatchTouchEvent(event);
	}
	
	/**
	 * ɾ����ѡ��ͼԪ
	 */
	private void deleteTuyuan(Tuyuan c)
	{
		mPaintStack.deleteCommand(c);
	}
	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		return handleTouchForDraw(event);
	}
	
	/**
	 * �����ͼ���̵�touch�¼�
	 */
	private boolean handleTouchForDraw(MotionEvent event)
	{
		Log.i(TAG, "handleTouchForDraw");
		float x = event.getX();
		float y = event.getY();
		isTouchUp = false;
		switch (event.getAction())
		{
		case MotionEvent.ACTION_DOWN:
			srcX = event.getX();
			srcY = event.getY();
			mCurrentPaint = createNewTuyuan(mPaintMode);
			mCurrentPaint.touchDown(x, y);
			invalidate();
			break;
		case MotionEvent.ACTION_MOVE:
			mCurrentPaint.touchMove(x, y);
			isShaked = false;
			invalidate();
			break;
		case MotionEvent.ACTION_UP:
			mCurrentPaint.touchUp(x, y);
			mCurrentPaint.draw(mCanvas);//���Ƶ�bitmap��
			if(mCurrentPaint.hasDraw())
			{
				mPaintStack.push(mCurrentPaint);//����ջ��
			}
			invalidate();
			isTouchUp = true;
			if(Math.abs(srcX-x)>=2 && Math.abs(srcY-y)>=2)
			{
				checkedElement = mCurrentPaint;//��ǰ���ڻ��Ƶ�ͼԪĬ�ϱ�ѡ��
			}
			break;
		}
		return true;
	}
	
	@Override
	public boolean onDown(MotionEvent e)
	{
		Log.i(TAG,"onDown");
		/**
		 * ���ư����¼�
		 * */
  		//��ȡ��ָ��ǰ����λ��
		float x = e.getX();
		float y = e.getY();
		//��鵱ǰλ���Ƿ���ͼԪ�ܹ���ѡ��
		Tuyuan c = mPaintStack.check(x, y);
		
		if(c != null)//ѡ����ͼԪ
		{
			checkedElement = c;
			//�����ǰ����Ƥģʽ����ɾ����ѡ��ͼԪ
			if(eraserMode)
			{
				deleteTuyuan(c);
				checkedElement = null;
			}
			//�����ǰ�����ģʽ�������ѡ��ͼԪ
			if(fillMode)
			{
				checkedElement.fill(mPenColor);
			}
		}else
		{
			checkedElement = null;
		}
		invalidate();//�ػ�
		
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e)
	{
	}


	@Override
	public boolean onSingleTapUp(MotionEvent e)
	{
		return false;
	}


	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY)
	{
		return false;
	}

	
	
	@Override
	public void onLongPress(MotionEvent e)
	{
		shouldDrag(e);//�ж��Ƿ���Ҫ�л�Ϊ�϶�״̬
	}


	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY)
	{
		return false;
	}
	
	/**
	 * �ж��Ƿ���Ҫ�л�Ϊ�϶�״̬
	 */
	private void shouldDrag(MotionEvent e)
	{
		//��ȡ��ָ��ǰ����λ��
		float x = e.getX();
		float y = e.getY();
		//��鵱ǰλ���Ƿ���ͼԪ�ܹ���ѡ��
		Tuyuan c = mPaintStack.check(x, y);
		//�����ǰ�����ͼԪ������֮ǰѡ�е�ͼԪ��ô��ͼԪ��Ϊ�϶�״̬
		if(c != null && checkedElement != null && c.equals(checkedElement))
		{
			shouldDrag = true;
		}else
		{
			shouldDrag = false;
		}
		invalidate();
	}
	/**
	 * ��ͼ�ػ淽��
	 * */
	@Override
	protected void onDraw(Canvas canvas)
	{
		//TODO
		if(tempBitmap != null)
		{
			super.onDraw(canvas);
			canvas.drawBitmap(tempBitmap, 0, 0, mPaint);//����֮ǰ��·��
			if(!isTouchUp)
			{
				mCurrentPaint.draw(canvas);//��view�ϻ��Ƶ�ǰ·��
			}
		}
		
		//ѡ���ͼԪ��Ϊ��
		if(checkedElement != null)
		{
			if(shouldDrag)//�϶�״̬����Ҫ������ʾ
			{	
				checkedElement.setHighLight(canvas);
				checkedElement.draw(mCanvas);
//				checkedElement.draw(canvas);
			}else if(fillMode)//���ģʽ
			{
				checkedElement.draw(mCanvas);
	//			checkedElement.draw(canvas);
			}else if(zoomMode)//ͼԪ����ģʽ
			{
				checkedElement.checked(canvas);
				checkedElement.draw(mCanvas);
//				checkedElement.draw(canvas);
				zoomMode = false;
			}else if(rotateMode)//ͼԪ��תģʽ
			{
				checkedElement.checked(canvas);
				checkedElement.draw(mCanvas);
//				checkedElement.draw(canvas);
				rotateMode = false;
			}
			else//����ֻ��ѡ��״̬
			{
				checkedElement.checked(canvas);
			}
		}
		//�������Ƿ������ݣ���������������
		if(pasteFlag && copedTuyuan!=null)
		{
			Log.i(TAG,"=======ONDRAW_PASTE=======");
			copedTuyuan.draw(mCanvas);
			copedTuyuan = null;
			pasteFlag = false;
		}
	}
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh)
	{
		super.onSizeChanged(w, h, oldw, oldh);
		mBitmapWidth = w;
		mBitmapHeight = h;
		if(isFirstCreated)
		{
			createCanvasBitmap(mBitmapWidth, mBitmapHeight);
			isFirstCreated = false;
		}
	}
	/**
	 * @param type ͼԪ����
	 * @return ͼԪ
	 * 
	 * �����µ�ͼԪģʽ
	 */
	public Tuyuan createNewTuyuan(int type)
	{
		Tuyuan tool = null;
		switch (type)
		{
		case ApplicationValues.TuyuanStyle.STYLE_FREE://�����ֻ�
			tool = new Free(mPenSize, mPenColor,mPenAlpha,new PaintStyle(paintStyleValue));
			break;
		case ApplicationValues.TuyuanStyle.STYLE_LINE://ֱ��
			tool = new Line(mPenSize, mPenColor,mPenAlpha,new PaintStyle(paintStyleValue));
			break;
		case ApplicationValues.TuyuanStyle.STYLE_RECT://����
			tool = new Rectangle(mPenSize, mPenColor, mPenAlpha,new PaintStyle(paintStyleValue));
			break;
		case ApplicationValues.TuyuanStyle.STYLE_OVAL://��Բ
			tool = new Oval(mPenSize, mPenColor,mPenAlpha, new PaintStyle(paintStyleValue));
			break;
		case ApplicationValues.TuyuanStyle.STYLE_BEZIER://���α���������
			tool = new Bezier(mPenSize, mPenColor, mPenAlpha, new PaintStyle(paintStyleValue));
			break;
		case ApplicationValues.TuyuanStyle.STYLE_BROKEN_LINE://����
			tool = new BrokenLine(mPenSize,mPenColor,mPenAlpha, new PaintStyle(paintStyleValue));
			BrokenLine temp = (BrokenLine) tool;
			//ע�ᡰ����ת�۵㡱�¼�������������⵽�ζ�ʱ���������۵�
			temp.setOnTurnListener(new OnTurnListener()
			{
				@Override
				public boolean onTurn()
				{
					return isShaked;
				}
			});
			break;
		case ApplicationValues.TuyuanStyle.STYLE_POLYGN://�����
			tool = new Polygn(mPenSize, mPenColor, mPenAlpha, new PaintStyle(paintStyleValue));
			((Polygn)tool).setOnTurnListener(new Polygn.OnTurnListener()
			{
				@Override
				public boolean onTurn()
				{
					return isShaked;
				}
			});
			break;
			//TODO �������ģʽ
		}
		mPaintMode = type;
		return tool;
	}
	
	public boolean isShaked()
	{
		return isShaked;
	}

	public void setShaked(boolean isShaked)
	{
		this.isShaked = isShaked;
	}

	/**
	 * �����Ƿ�Ϊ��Ƥģʽ
	 */
	public void setEraserMode(boolean eraserMode)
	{
		this.eraserMode = eraserMode;
		if(fillMode)
		{
			fillMode = false;
		}
	}
	public boolean getEraserMode()
	{
		return this.eraserMode;
	}
	
	public boolean isFillMode()
	{
		return fillMode;
	}

	/**
	 * �����Ƿ�Ϊ���ģʽ
	 */
	public void setFillMode(boolean fillMode)
	{
		this.fillMode = fillMode;
		if(eraserMode)
		{
			eraserMode = false;
		}
	}
	
	/**
	 *����ͼԪ 
	 */
	public boolean copyTuyuan()
	{
		if(checkedElement != null)
		{
			Log.i(TAG,"copedTuyuan");
			copedTuyuan = checkedElement.copy();
			return true;
		}else
		{
			return false;
		}
	}
	
	/**
	 * ճ��ͼԪ
	 */
	public boolean pasteTuyuan()
	{
		if(mPaintStack != null && copedTuyuan != null)
		{
			Log.i(TAG,"PASTE Tuyuan......");
			mPaintStack.push(copedTuyuan);
			invalidate();
			pasteFlag = true;
			return true;
		}
		return false;
	}
	
	/**
	 * ͼԪ�Ŵ�
	 */
	public void enlarge()
	{
		if(checkedElement != null)
		{
			zoomMode = true;
			mPaintStack.deleteCommand(checkedElement);
			checkedElement.scale(1.1f, 1.1f);
			mPaintStack.push(checkedElement);
			invalidate();
		}
	}
	
	/**
	 * ͼԪ��С
	 */
	public void shrink()
	{
		if(checkedElement != null)
		{
			zoomMode = true;
			mPaintStack.deleteCommand(checkedElement);
			checkedElement.scale(0.9f, 0.9f);
			mPaintStack.push(checkedElement);
			invalidate();
		}
	}
	
	/**
	 * ������ת
	 */
	public void rotateLeft()
	{
		if(checkedElement != null)
		{
			rotateMode = true;
			mPaintStack.deleteCommand(checkedElement);
			checkedElement.rotate(30);
			mPaintStack.push(checkedElement);
			invalidate();
		}
	}
	
	/**
	 * ������ת
	 */
	public void rotateRight()
	{
		if(checkedElement != null)
		{
			rotateMode = true;
			mPaintStack.deleteCommand(checkedElement);
			checkedElement.rotate(-30);
			mPaintStack.push(checkedElement);
			invalidate();
		}
	}
	/**
	 * ���û�����ʽ
	 */
	public void setPaintStyle(int paintType)
	{
		this.paintStyleValue = paintType;
	}
	/**
	 * ���û�����ɫ
	 */
	public void setPenColor(int penColor)
	{
		this.mPenColor = penColor;
	}
	/**
	 * ���û��ʴ�С
	 */
	public void setPenSize(int penSize)
	{
		this.mPenSize = penSize;
	}
	
	/**
	 * ���û���͸����
	 */
	public void setPenAlpha(int penAlpha)
	{
		this.mPenAlpha = penAlpha;
	}
	/**
	 * ���õ�ǰͼԪ��ʽ
	 * @param type
	 */
	public void setCurrentTuyuanType(int type)
	{
		mCurrentPaint = createNewTuyuan(type);
		mPaintMode = type;
	}

	public int getPenAlpha()
	{
		return this.mPenAlpha;
	}
	
	public int getPenSize()
	{
		return this.mPenSize;
	}
	
	public int getPenColor()
	{
		return this.mPenColor;
	}
	
	public int getPaintStyle()
	{
		return this.paintStyleValue;
	}
	
	public int getTuyuanStyle()
	{
		return this.mPaintMode;
	}
	
	public int getCurrentTuyuanMode()
	{
		return mPaintMode;
	}
	/**
	 * Ϊ��������bitmap
	 */
	private void createCanvasBitmap(int w, int h)
	{
		tempBitmap = mBitmap.copy(Config.ARGB_8888,true);
		mCanvas.setBitmap(tempBitmap);
	}
	
	/**
	 * undo
	 */
	public void undo()
	{
		checkedElement = null;
		if(mPaintStack != null)
		{
			mPaintStack.undo();
		}
	}
	
	/**
	 * redo
	 */
	public void redo()
	{
		if(mPaintStack != null)
		{
			mPaintStack.redo();
		}
	}
	
	/**
	 * ��ջ���
	 */
	public void clearAll()
	{
		checkedElement = null;
		shouldDrag = false;
		
		if(mPaintStack != null)
		{
			mPaintStack.clear();
		}
		
		if(tempBitmap != null && !tempBitmap.isRecycled())
		{
			tempBitmap.recycle();
			tempBitmap = null;
		}
		//���軭��
		createCanvasBitmap(mBitmapWidth, mBitmapHeight);
		
		invalidate();
	}
	
	/**
	 * �Ƿ����������
	 */
	public boolean hasData()
	{
		return !mPaintStack.undoStack.isEmpty();
	}
	
	/**
	 * ��ȡ�û��Ѿ����Ƶ�����
	 * @return
	 */
	public Bitmap getDrawData()
	{
		return tempBitmap;
	}
	
	//---------------------------------------------------------------------------
	/**
	 * @author Rowand jj
	 *
	 *�ṩredo undo delete check���ܵ�ջ�ṹ
	 */
	public class PaintStack
	{
		private YiDrawView view = null;
		
		/**
		 * undoջ
		 */
		private List<Tuyuan> undoStack = new LinkedList<Tuyuan>();
		
		/**
		 * redoջ
		 */
		private List<Tuyuan> redoStack = new LinkedList<Tuyuan>();
		
		/**
		 * ������
		 */
		public PaintStack(YiDrawView view)
		{
			this.view = view;
		}
		
		/**
		 * ����
		 */
		public void redo()
		{
			Canvas c = view.mCanvas;
			if(redoStack != null && redoStack.size() > 0)
			{
				Tuyuan tool = redoStack.remove(redoStack.size() - 1);
				undoStack.add(tool);
				tool.draw(c);
			}
			view.invalidate();
		}
		
		/**
		 * ����
		 */
		public void undo()
		{
			if(view.tempBitmap != null && !view.tempBitmap.isRecycled())
			{
				view.tempBitmap.recycle();
				view.tempBitmap = null;
			}
			view.createCanvasBitmap(view.mBitmapWidth,view.mBitmapHeight);
			Canvas c = view.mCanvas;
			if(undoStack != null && undoStack.size() > 0)
			{
				Tuyuan tool = undoStack.remove(undoStack.size() - 1);
				redoStack.add(tool);
				
				for(Tuyuan t : undoStack)
				{
					t.draw(c);
				}
			}
			view.invalidate();
		}
		
		/**
		 * ��鵱ǰλ���Ƿ���ѡ��ͼԪ
		 */
		public Tuyuan check(float x,float y)
		{
			for(Tuyuan c : undoStack)
			{
				if(c.contains(x, y))
				{
					return c;
				}
			}
			return null;
		}
		/**
		 * ɾ��һ��ͼԪ
		 */
		public void deleteCommand(Tuyuan command)
		{
			if(view.tempBitmap != null && !view.tempBitmap.isRecycled())
			{
				view.tempBitmap.recycle();
				view.tempBitmap = null;
			}
			view.createCanvasBitmap(view.mBitmapWidth,view.mBitmapHeight);
			Canvas c = view.mCanvas;
			if(undoStack != null && undoStack.size() > 0)
			{
				if(undoStack.remove(command))
				{
					redoStack.add(command);
				}
				for(Tuyuan t : undoStack)
				{
					t.draw(c);
				}
			}
			view.invalidate();
		}
		
		/**
		 * ���û�����"ѹջ"
		 */
		public void push(Tuyuan tool)
		{
			if(undoStack != null)
			{
				undoStack.add(tool);
			}
		}
		
		/**
		 * ���ջ
		 */
		public void clear()
		{
			if(undoStack != null && redoStack != null)
			{
				undoStack.clear();
				redoStack.clear();
			}
		}
	}
}











































