package cn.edu.chd.domain;

import android.graphics.Canvas;

/**
 * @author Rowand jj
 * 
 * ����ͼԪ(ֱ�ߣ�������...)��Ҫ�̳д˽ӿ�
 */
public interface Tuyuan
{
	/**
	 * ֻ�е��ƶ����볬�����ֵ�Ż��ͼ
	 */
	public static final float TOUCH_TOLERANCE = 4.0f;
	
	/**
	 * ����ͼԪ
	 */
	public void draw(Canvas canvas);

	/**
	 * ��ָ����
	 */
	public void touchDown(float x, float y);

	/**
	 * ��ָ�ƶ�
	 */
	public void touchMove(float x, float y);

	/**
	 * ��ָ�ɿ�
	 */
	public void touchUp(float x, float y);

	/**
	 * �Ƿ��Ѿ�������ͼԪ
	 */
	public boolean hasDraw();
	
	/**
	 * �Ƿ������(x,y)
	 */
	public boolean contains(float x,float y);
	
	/**
	 * ������ʾ
	 */
	public void setHighLight(Canvas canvas);
	
	/**
	 * ѡ��ͼԪ
	 */
	public void checked(Canvas canvas);
	
	/**
	 * ����
	 */
	public void scale(float offsetX,float offsetY);
	
	/**
	 * ƽ��
	 */
	public void translate(float offsetX,float offsetY);
	
	/**
	 * ��ת
	 */
	public void rotate(float degrees);
	/**
	 * ��䵱ǰͼԪ
	 */
	public void fill(int color);
	
	/**
	 * ��ǰͼԪ�Ƿ����
	 */
	public boolean isFilled();
	
	/**
	 * ������ǰͼԪ��������ͼԪ��״��ԭͼԪ��ͬ��λ��ΪԲͼԪ�����·�(+10,+10)
	 */
	public Tuyuan copy();
}



















