package cn.edu.chd.graphics;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import cn.edu.chd.domain.Tuyuan;
import cn.edu.chd.domain.PaintStyle;
import cn.edu.chd.utils.PaintUtils;
import cn.edu.chd.values.ApplicationValues;

/**
 * @author Rowand jj
 *
 *����
 */
public class BrokenLine implements Tuyuan
{
	/**
	 * ������ʼ�������
	 */
	private float mCurrentX = 0.0f;
	/**
	 * ������ʼ��������
	 */
	private float mCurrentY = 0.0f;
	/**
	 * ����
	 */
	private Paint mPenPaint = null;
	/**
	 * ·��
	 */
	private Path mPath = new Path();
	
	/**
	 * ת�۵Ļص��ӿ�
	 */
	private OnTurnListener mListener = null;
	
	private boolean mHasDraw = false;
	
	private Path oldPath = new Path();
	
	/**
	 * �������·���ĵ㼯��
	 */
	private List<PointF> points = new ArrayList<PointF>();
	
	/**
	 * ����·��
	 */
	private Path finalPath = new Path();
	
	private boolean isTouchUp = false;
	
	private int penSize;
	private int penColor;
	private PaintStyle paintStyle;
	private int alpha;
	
	/**
	 * ͼԪ�Ƿ����
	 */
	private boolean isFilled = false;
	
	public BrokenLine(int penSize, int penColor,int alpha,PaintStyle paintStyle)
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
			if(isTouchUp)//���������Ѿ����ɣ���ôֱ����finalpath��֮ǰ��oldpath��mpath������
			{
				canvas.drawPath(finalPath, mPenPaint);
				return;
			}
			//���ƹ�ȥ��·��
			canvas.drawPath(oldPath, mPenPaint);
			canvas.drawPath(mPath, mPenPaint);
		}
	}

	@Override
	public void touchDown(float x, float y)
	{
		mPath.reset();
		mPath.moveTo(x, y);
		savePoint(x, y);
		
		points.add(new PointF(x, y));
	}

	@Override
	public void touchMove(float x, float y)
	{
		if(isMoved(x, y))
		{
			if(mListener!=null && mListener.onTurn())//�����ǰ����ת�۵�ͼ�¼��ǰ��λ�ã�����Ϊ����ʼ��
			{
				oldPath.addPath(mPath);
				savePoint(x, y);
				mPath = new Path();
				points.add(new PointF(x, y));
			}
			drawBrokenLine(x, y);
			mHasDraw = true;
		}
	}
	
	@Override
	public void touchUp(float x, float y)
	{
		drawBrokenLine(x, y);
		points.add(new PointF(x, y));
		isTouchUp = true;
		
		finalPath.addPath(mPath);
		//��������·��
		for(int i = 0; i < points.size(); i++)
		{
			if(i == 0)//��һ����
			{
				finalPath.moveTo(points.get(i).x,points.get(i).y);
			}
			else
			{
				finalPath.lineTo(points.get(i).x, points.get(i).y);
			}
		}
	}
	private void savePoint(float x, float y)
	{
		mCurrentX = x;
		mCurrentY = y;
	}

	private boolean isMoved(float x, float y)
	{
		float dx = Math.abs(x - mCurrentX);
		float dy = Math.abs(y - mCurrentY);
		boolean isMoved = dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE;
		return isMoved;
	}
	@Override
	public boolean hasDraw()
	{
		return mHasDraw;
	}
	/**
	 * ����ֱ��
	 */
	private void drawBrokenLine(float x,float y)
	{
		mPath.reset();
		mPath.moveTo(mCurrentX, mCurrentY);
		mPath.lineTo(x, y);
	}
	
	/**
	 * @param listener
	 * ����ע��ص��¼�
	 */
	public void setOnTurnListener(OnTurnListener listener)
	{
		this.mListener = listener;
	}
	/**
	 * @author Rowand jj
	 *ת�ۻص��¼�
	 */
	public interface OnTurnListener
	{
		public boolean onTurn();
	}
	@Override
	public boolean contains(float x, float y)
	{
		RectF bounds = new RectF();
		finalPath.computeBounds(bounds, true);//todo �������
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
		finalPath.computeBounds(bounds, true);
		canvas.drawRect(bounds, p);
		//���ƹؼ���
		Paint pTemp = new Paint();
		pTemp.setStyle(Style.FILL);
		pTemp.setColor(color);
		canvas.drawCircle(bounds.left, bounds.top,ApplicationValues.Base.RADIUS,pTemp);
		canvas.drawCircle(bounds.left, bounds.bottom,ApplicationValues.Base.RADIUS,pTemp);
		canvas.drawCircle(bounds.right, bounds.top,ApplicationValues.Base.RADIUS,pTemp);
		canvas.drawCircle(bounds.right, bounds.bottom,ApplicationValues.Base.RADIUS,pTemp);
	}
	
	@Override
	public void translate(float offsetX, float offsetY)
	{
		Matrix matrix = new Matrix();
		matrix.setTranslate(offsetX, offsetY);
		
		finalPath.transform(matrix);
	}
	@Override
	public void fill(int color)
	{
	}
	@Override
	public boolean isFilled()
	{
		return false;
	}
	private void setPath(Path mPath)
	{
		this.finalPath = mPath;
	}
	private void setTouchUp(boolean istouchup)
	{
		this.isTouchUp = istouchup;
	}
	@Override
	public void scale(float offsetX, float offsetY)
	{
		Matrix matrix = new Matrix();
		RectF bounds = new RectF();
		finalPath.computeBounds(bounds, true);
		matrix.setScale(offsetX, offsetY, (bounds.right+bounds.left)/2.0f,(bounds.top+bounds.bottom)/2.0f);
		finalPath.transform(matrix);
	}
	@Override
	public void rotate(float degrees)
	{
		Matrix matrix = new Matrix();
		RectF bounds = new RectF();
		finalPath.computeBounds(bounds, true);
		matrix.setRotate(degrees,(bounds.right+bounds.left)/2.0f,(bounds.top+bounds.bottom)/2.0f);
		finalPath.transform(matrix);
	}
	@Override
	public Tuyuan copy()
	{
		BrokenLine copedTuyuan = new BrokenLine(penSize, penColor, alpha, paintStyle.newInstance());
		if(isFilled)
		{
			copedTuyuan.fill(mPenPaint.getColor());
		}
		copedTuyuan.setPath(new Path(finalPath));
		copedTuyuan.translate(40,40);
		copedTuyuan.setTouchUp(true);
		return copedTuyuan;
	}
}














