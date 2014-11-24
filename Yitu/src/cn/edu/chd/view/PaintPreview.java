package cn.edu.chd.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.ImageView;
import cn.edu.chd.domain.PaintStyle;
import cn.edu.chd.values.ApplicationValues;

/**
 * @author Rowand jj
 *
 *Ԥ���û���ѡ���ͼԪ��������ʽ
 */
public class PaintPreview extends ImageView
{
	private Paint mPaint = null;
	private int mColor = ApplicationValues.PaintSettings.PENCOLOR_DEFAULT;
	private int mAlpha = ApplicationValues.PaintSettings.PEN_ALPHA_DEFAULT;
	private Path mPath = new Path();
	private int paintType = ApplicationValues.PaintStyle.MODE_PLAIN_PEN;
	private int tuyuanType = ApplicationValues.TuyuanStyle.STYLE_FREE;
	private int mSize = ApplicationValues.PaintSettings.PENSIZE_DEFAULT;
	
	private float startX = 0;
	private float startY = 0;
	
	/* ������ */
	public PaintPreview(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
		drawOperation(canvas);
		
	}
	
	/**
	 * ����Ԥ��Ч��
	 */
	private void drawOperation(Canvas canvas)
	{
		if(canvas == null)
			return;
		mPath.reset();
		init();
		canvas.drawPath(mPath, mPaint);
	}
	
	private void init()
	{
		//��ʼ�����ʷ��
		mPaint = new PaintStyle(paintType).getPaintStyle();
		//������ɫ
		mPaint.setColor(mColor);
		//����͸����
		mPaint.setAlpha(255-mAlpha);
		//���ô�С
		mPaint.setStrokeWidth(mSize);
		//����·��
		mPath = initPath(tuyuanType);
	}
	
	private Path initPath(int tuyuanType)
	{
		switch (tuyuanType)
		{
		case ApplicationValues.TuyuanStyle.STYLE_FREE://�ֻ�
			drawFree();
			break;
		case ApplicationValues.TuyuanStyle.STYLE_LINE://ֱ��
			drawLine();
			break;
		case ApplicationValues.TuyuanStyle.STYLE_RECT://����
			drawRect();
			break;
		case ApplicationValues.TuyuanStyle.STYLE_OVAL://��Բ
			drawOval();
			break;
		case ApplicationValues.TuyuanStyle.STYLE_BEZIER://���α���������
			drawBazier();
			break;
		case ApplicationValues.TuyuanStyle.STYLE_BROKEN_LINE://����
			drawBrokenLine();
			break;
		case ApplicationValues.TuyuanStyle.STYLE_POLYGN://�����
			drawPolygn();
			break;
		default:
			break;
		}
		return mPath;
	}
	
	private void drawLine()
	{
		startX = getWidth() / 6.0f;
		startY = getHeight() / 2.0f;
		
		mPath.moveTo(startX,startY);
		mPath.lineTo(getWidth()*5/6.0f,startY);
	}
	private void drawFree()
	{
		startX = getWidth() / 6.0f;
		startY = getHeight() / 2.0f;
		
		mPath.moveTo(startX, startY);
		mPath.quadTo(getWidth()/2.0f,getHeight()*3/4.0f,getWidth()*5/6.0f , startY);
	}
	private void drawBazier()
	{
		startX = getWidth() / 6.0f;
		startY = getHeight() / 2.0f;
		mPath.moveTo(startX, startY);
		mPath.cubicTo(getWidth()/3.0f,getHeight()/8.0f,getWidth()*2/3,getHeight()*7/8.0f, getWidth()*5/6.0f,startY);
	}
	private void drawOval()
	{
		startX = getWidth() / 6.0f;
		startY = getHeight() / 4.0f;
		mPath.addOval(new RectF(startX, startY, getWidth()*5/6.0f, getHeight()*3/4.0f),Path.Direction.CW);
	}
	private void drawRect()
	{
		startX = getWidth() / 6.0f;
		startY = getHeight() / 4.0f;
		mPath.addRect(new RectF(startX, startY, getWidth()*5/6.0f, getHeight()*3/4.0f),Path.Direction.CW);
	}
	private void drawBrokenLine()
	{
		startX = getWidth() / 6.0f;
		startY = getHeight() / 2.0f;
		
		float temp1x = getWidth()/3.0f;
		float temp1y = getHeight()/4.0f;
		float temp2x = getWidth()*2/3;
		float temp2y = getHeight()*3/4.0f;
		float temp3x = getWidth()*5/6.0f;
		float temp3y = startY;
		
		mPath.moveTo(startX,startY);
		mPath.lineTo(temp1x, temp1y);
		mPath.moveTo(temp1x, temp1y);
		mPath.lineTo(temp2x, temp2y);
		mPath.moveTo(temp2x, temp2y);
		mPath.lineTo(temp3x, temp3y);
	}
	private void drawPolygn()
	{
		startX = getWidth() / 6.0f;
		startY = getHeight() / 2.0f;
		float temp1x = getWidth()/2.0f;
		float temp1y = getHeight()/4.0f;
		
		float temp2x = getWidth()*5/6.0f;
		float temp2y = startY;
		
		float temp3x = getWidth()*2/3.0f;
		float temp3y = getHeight()*3/4.0f;
		
		float temp4x = getWidth()/3.0f;
		float temp4y = getHeight()*3/4.0f;
		mPath.moveTo(startX,startY);
		mPath.lineTo(temp1x, temp1y);
		mPath.moveTo(temp1x, temp1y);
		mPath.lineTo(temp2x, temp2y);
		mPath.moveTo(temp2x, temp2y);
		mPath.lineTo(temp3x, temp3y);
		mPath.moveTo(temp3x, temp3y);
		mPath.lineTo(temp4x, temp4y);
		mPath.moveTo(temp4x, temp4y);
		mPath.lineTo(startX, startY);
	}
	/**
	 * ��������
	 * @param color ��ɫ
	 * @param alpha ͸����
	 * @param tuyuanType ͼԪ����
	 * @param paintType ������ʽ
	 */
	public void setAttrs(int color,int alpha,int size,int tuyuanType,int paintType)
	{
		this.mColor = color;
		this.mAlpha = alpha;
		this.mSize = size;
		this.tuyuanType = tuyuanType;
		this.paintType = paintType;
		
		invalidate();
	}
}

















