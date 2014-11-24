package cn.edu.chd.yitu;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import cn.edu.chd.adapter.YiPageAdapter;
import cn.edu.chd.adapter.YiPageAdapter.OnReloadListener;
import cn.edu.chd.view.YiTitleBar;
import cn.edu.chd.view.YiTitleBar.LeftButtonClickListener;

/**
 * @author Rowand jj ѡ��ͼģ��
 */
public class ChooseModel extends FragmentActivity implements
		OnPageChangeListener, OnTouchListener, OnClickListener
{
	/**
	 * �Զ��������
	 */
	private YiTitleBar title_bar = null;

	/**
	 * ����������������С����
	 */
	private static final int mTouchSlop = 100;

	private ViewPager vPager = null;
	/**
	 * ѡ��ı���
	 */
	private TextView title1, title2, title3;

	/**
	 * ѡ��µĺ���
	 */
	private ImageView tab_cursor = null;

	/**
	 * ѡ��»��߳���
	 */
	private static int lineWidth = 0;
	/**
	 * ƫ���� ���ֻ���Ļ���/3-ѡ����ȣ�/2
	 */
	private static int offset = 0;
	/**
	 * ѡ�����
	 */
	private static final int TAB_COUNT = 3;
	private static final String TAG = "ChooseModel";
	/**
	 * ��ǰ��ʾ��ѡ�λ��
	 */
	private int current_index = 0;

	/**
	 * �»��ߴ�һ��λ�û�������һ��λ�õ�ƫ����
	 */
	private int one = 0;

	/**
	 * ѡ����⼯��
	 */
	private TextView[] titles;

	/**
	 * ������
	 */
	private YiPageAdapter adapter = null;

	private long exitTime = 0;
	/**
	 * ҳ�����ݼ���
	 */
	private List<Fragment> fgs = null;
	private int downX = 0, upX = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_choose_model);
		
		Log.i(TAG,"ONCREATE....");
		initTitleBar();
		initImageView();
		title1 = (TextView) findViewById(R.id.choose_model_title1);
		title2 = (TextView) findViewById(R.id.choose_model_title2);
		title3 = (TextView) findViewById(R.id.choose_model_title3);
		titles = new TextView[]
		{ title1, title2, title3 };
		vPager = (ViewPager) findViewById(R.id.choose_model_vPager);

		fgs = new ArrayList<Fragment>();
		fgs.add(new TabNormalModel());
		fgs.add(new TabMyWorks());
		fgs.add(new TabDIY());
		adapter = new YiPageAdapter(getSupportFragmentManager(), fgs);

		adapter.setOnReloadListener(new OnReloadListener()// ������������ʱ����
		{
			@Override
			public void onReload()
			{
			//	fgs = null;
				List<Fragment> newdata = new ArrayList<Fragment>();
				newdata.add(new TabNormalModel());
				newdata.add(new TabMyWorks());
				newdata.add(new TabDIY());
				adapter.setPagerItems(newdata);// ��������ҳ������<ҳ������̫�࣬���ַ�ʽ���˷��ڴ�>
			}
		});
		vPager.setAdapter(adapter);
		vPager.setOnPageChangeListener(this);// ����ҳ��ı�Ļص��¼�
		vPager.setOnTouchListener(this);// ���ô���ʱ�Ļص��¼�

		title1.setOnClickListener(this);
		title2.setOnClickListener(this);
		title3.setOnClickListener(this);
	}
	/**
	 * ��ʼ��������
	 */
	private void initTitleBar()
	{
		title_bar = (YiTitleBar) findViewById(R.id.ytb_choose_model);
		title_bar.setOnLeftButtonClickListener(new LeftButtonClickListener()
		{
			@Override
			public void leftButtonClick()
			{
				goSetting();
			}
		});
	}

	/**
	 * ��ʼ��ѡ��»��߼����ʼ��ʾλ��
	 */
	private void initImageView()
	{
		tab_cursor = (ImageView) findViewById(R.id.tab_cursor);
		// ��ȡͼƬ���
		lineWidth = BitmapFactory.decodeResource(getResources(),
				R.drawable.line).getWidth();
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		// ��ȡ��Ļ���
		int screenWidth = dm.widthPixels;
		Matrix matrix = new Matrix();
		offset = (int) ((screenWidth / (float) TAB_COUNT - lineWidth) / 2);
		matrix.postTranslate(offset, 0);
		// ���ó�ʼλ��
		tab_cursor.setImageMatrix(matrix);
		one = offset * 2 + lineWidth;
	}

	/**
	 * ǰ�����ý���
	 */
	private void goSetting()
	{
		Intent intent = new Intent(ChooseModel.this, Settings.class);
		ChooseModel.this.startActivity(intent);
		overridePendingTransition(R.anim.in_left, R.anim.slide_remain);// �����л�����
	}

	@Override
	public void onPageSelected(int index)
	{
		// �л�ѡ�ʱ���ñ�����ɫ�仯�Լ��»��ߵ��ƶ�Ч��
		Animation animation = new TranslateAnimation(one * current_index, one* index, 0, 0);
		animation.setFillAfter(true);
		animation.setDuration(300);
		tab_cursor.startAnimation(animation);
		titles[current_index].setTextColor(Color.BLACK);
		titles[index].setTextColor(Color.RED);
		current_index = index;

		Log.i(TAG, "INDEX = " + current_index);
	}

	public YiPageAdapter getAdapter()
	{
		return adapter;
	}
	@Override
	public boolean onTouch(View v, MotionEvent event)
	{
		if (current_index != 0)
		{
			return false;
		} else
		{
			switch (event.getAction())
			{
			case MotionEvent.ACTION_DOWN://down�¼�������
				downX = (int) event.getRawX();
				break;
			case MotionEvent.ACTION_UP:
				upX = (int) event.getRawX();
				Log.i(TAG,"downX = "+downX+",upX="+upX);
				if (upX - downX > mTouchSlop)
				{
					goSetting();
				}
				break;
			}
			return false;
		}
	}
	@Override
	public void onBackPressed()
	{
		if ((System.currentTimeMillis() - exitTime) > 2000)
		{
			Toast.makeText(this, "�ٰ�һ���˳�����", Toast.LENGTH_SHORT).show();
			exitTime = System.currentTimeMillis();
		} else
		{
			super.onBackPressed();
		}
	}
	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
		case R.id.choose_model_title1:
			vPager.setCurrentItem(0, false);
			break;
		case R.id.choose_model_title2:
			vPager.setCurrentItem(1, false);
			break;
		case R.id.choose_model_title3:
			vPager.setCurrentItem(2, false);
			break;
		}
	}
	@Override
	public void onPageScrollStateChanged(int arg0)
	{
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2)
	{
	}
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev)
	{
		if(ev.getAction() == MotionEvent.ACTION_DOWN)
		{
			this.onTouch(vPager, ev);
		}
		return super.dispatchTouchEvent(ev);
	}
	@Override
	protected void onResume()
	{
		super.onResume();
		//TODO ÿ�ζ������¼��أ��е㲻����
		adapter.reLoad();
	}
	
}





















