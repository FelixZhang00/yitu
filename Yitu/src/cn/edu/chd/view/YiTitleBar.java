package cn.edu.chd.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import cn.edu.chd.yitu.R;

/**
 * @author Rowand jj
 *�Զ���ı�����
 *	<��һ�������һ����ť>
 */
public class YiTitleBar extends FrameLayout implements OnClickListener
{
	private ImageButton but = null;
	private TextView tv = null;
	private LeftButtonClickListener mListener = null;
	
	public YiTitleBar(Context context, AttributeSet attrs)
	{
		this(context, attrs, 0);
	}
	public YiTitleBar(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		LayoutInflater.from(getContext()).inflate(R.layout.view_yi_title_bar,this);
		but = (ImageButton) findViewById(R.id.title_bar_but_left);
		tv = (TextView) findViewById(R.id.title_bar_tv_center);
		but.setOnClickListener(this);
	}

	@Override
	public void onClick(View v)
	{
		if(v.getId() == R.id.title_bar_but_left)
		{
			if(mListener != null)
			{
				mListener.leftButtonClick();
			}
		}
	}
	/**
	 * ������ఴť�����ʱ�����Ļص��¼�
	 * @param listener
	 */
	public void setOnLeftButtonClickListener(LeftButtonClickListener listener)
	{
		this.mListener = listener;
	}
	
	/**
	 * ���ñ���
	 * @param title
	 */
	public void setTitleName(String title)
	{
		if(title!=null)
		{
			tv.setText(title);
		}
	}
	public void setTitleName(int resId)
	{
		tv.setText(resId);
	}
	/**
	 * ������ఴť����
	 * @param resId ��Դid
	 */
	public void setLeftButtonBGResource(int resId)
	{
		but.setBackgroundResource(resId);
	}
	public interface LeftButtonClickListener
	{
		public void leftButtonClick();
	}
}











