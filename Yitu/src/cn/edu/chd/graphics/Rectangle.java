package cn.edu.chd.graphics;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.RectF;
import cn.edu.chd.domain.Tuyuan;
import cn.edu.chd.domain.PaintStyle;
import cn.edu.chd.utils.PaintUtils;
import cn.edu.chd.values.ApplicationValues;

/**
 * @author Rowand jj
 *
 *���ƾ���
 */
public class Rectangle implements Tuyuan
{
	private float mSrcX = 0.0f;
	private float mSrcY = 0.0f;
	/**
	 * ����
	 */
	private Paint mPenPaint = null;
	/**
	 * ·��
	 */
	private Path mPath = new Path();
	
	private boolean mHasDraw = false;
	
	/**
	 * ͼԪ�Ƿ����
	 */
	private boolean isFilled = false;
	
	private int penSize;
	private int penColor;
	private PaintStyle paintStyle;
	private int alpha;
	
	public Rectangle(int penSize, int penColor,int alpha,PaintStyle paintStyle)
	{
		mPenPaint = paintStyle.getPaintStyle();
		
		mPenPaint.setStrokeWidth(penSize);
		mPenPaint.setColor(penColor);
		mPenPaint.setAlpha(255-alpha);
		
		this.penSize = penSize;
		this.penColor = penColor;
		this.alpha = alpha;
		this.paintStyle = paintStyle;
	}
	@Override
	public void draw(Canvas canvas)
	{
		if(canvas != null)
		{
			canvas.drawPath(mPath, mPenPaint);
		}
	}

	@Override
	public void touchDown(float x, float y)
	{
		mPath.reset();
		mPath.moveTo(x, y);
		savePoint(x, y);
	}

	@Override
	public void touchMove(float x, float y)
	{
		if(isMoved(x, y))
		{
			drawRect(x, y);
			mHasDraw = true;
		}
	}

	@Override
	public void touchUp(float x, float y)
	{
	 	drawRect(x, y);
	}

	@Override
	public boolean hasDraw()
	{
		return mHasDraw;
	}
	
	private void savePoint(float x, float y)
	{
		mSrcX = x;
		mSrcY = y;
	}

	private boolean isMoved(float x, float y)
	{
		float dx = Math.abs(x - mSrcX);
		float dy = Math.abs(y - mSrcY);
		boolean isMoved = dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE;
		return isMoved;
	}
	
	/**
	 * ���ƾ���
	 */
	private void drawRect(float x,float y)
	{
		mPath.reset();
		if(x>mSrcX && y<mSrcY)//��һ����
		{
			mPath.addRect(mSrcX, y, x, mSrcY, Path.Direction.CW);
		}else if(x<mSrcX && y<mSrcY)//�ڶ�����
		{
			mPath.addRect(x, y, mSrcX, mSrcY, Path.Direction.CW);
		}else if(x<mSrcX && y>mSrcY)//��������
		{
			mPath.addRect(x, mSrcY, mSrcX, y, Path.Direction.CW);
		}else//��������
		{
			mPath.addRect(mSrcX,mSrcY,x,y,Path.Direction.CW);
		}
	}
	@Override
	public boolean contains(float x, float y)
	{
		RectF bounds = new RectF();
		mPath.computeBounds(bounds, true);//todo �������
		return bounds.contains(x, y);
	}
	@Override
	public void setHighLight(Canvas canvas)
	{
		checked(canvas, Color.YELLOW);
	}
	@Override
	public void checked(Canvas canvas)
	{
		checked(canvas,Color.BLACK);
	}
	
	private void checked(Canvas canvas,int color)
	{
		Paint p = PaintUtils.getDashedPaint();
		p.setColor(color);
		RectF bounds = new RectF();
		
		//����߽�
		mPath.computeBounds(bounds, true);
		canvas.drawRect(bounds, p);
		//���ƹؼ���
		Paint pTemp = new Paint();
		pTemp.setStyle(Style.FILL);
		pTemp.setColor(color);
		canvas.drawCircle(bounds.left, bounds.top,ApplicationValues.Base.RADIUS,pTemp);
		canvas.drawCircle(bounds.left, bounds.bottom,ApplicationValues.Base.RADIUS,pTemp);
		canvas.drawCircle(bounds.right, bounds.top,ApplicationValues.Base.RADIUS,pTemp);
		canvas.drawCircle(bounds.right, bounds.bottom,ApplicationValues.Base.RADIUS,pTemp);
		
		canvas.drawCircle(bounds.left, bounds.top + (bounds.bottom-bounds.top)/2.0f,ApplicationValues.Base.RADIUS,pTemp);
		canvas.drawCircle(bounds.right, bounds.top + (bounds.bottom-bounds.top)/2.0f,ApplicationValues.Base.RADIUS,pTemp);
		canvas.drawCircle(bounds.left+(bounds.right-bounds.left)/2.0f, bounds.top,ApplicationValues.Base.RADIUS,pTemp);
		canvas.drawCircle(bounds.left+(bounds.right-bounds.left)/2.0f, bounds.bottom,ApplicationValues.Base.RADIUS,pTemp);
	}
	
	@Override
	public void translate(float offsetX, float offsetY)
	{
		Matrix matrix = new Matrix();
		matrix.setTranslate(offsetX, offsetY);
		mPath.transform(matrix);
	}
	@Override
	public void rotate(float degrees)
	{
		Matrix matrix = new Matrix();
		RectF bounds = new RectF();
		mPath.computeBounds(bounds, true);
		//�����ĵ���ת
		matrix.setRotate(degrees,(bounds.right+bounds.left)/2.0f,(bounds.top+bounds.bottom)/2.0f);
		mPath.transform(matrix);
	}
	@Override
	public void fill(int color)
	{
		//��ν����书����ʵ���ǽ����ʵ���ʽ��Ϊ���
		mPenPaint.setStyle(Style.FILL);
		mPenPaint.setColor(color);
		isFilled = true;
	}
	@Override
	public boolean isFilled()
	{
		return isFilled;
	}
	@Override
	public void scale(float offsetX, float offsetY)
	{
		Matrix matrix = new Matrix();
		RectF bounds = new RectF();
		mPath.computeBounds(bounds, true);
		matrix.setScale(offsetX, offsetY, (bounds.right+bounds.left)/2.0f,(bounds.top+bounds.bottom)/2.0f);
		mPath.transform(matrix);
	}
	private void setPath(Path mPath)
	{
		this.mPath = mPath;
	}
	
	@Override
	public Tuyuan copy()
	{
		Rectangle copedTuyuan = new Rectangle(penSize, penColor, alpha, paintStyle.newInstance());
		if(isFilled)
		{
			copedTuyuan.fill(mPenPaint.getColor());
		}
		copedTuyuan.setPath(new Path(mPath));
		copedTuyuan.translate(40,40);
		return copedTuyuan;
	}
}















