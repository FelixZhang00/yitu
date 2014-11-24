package cn.edu.chd.yitu;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;
import cn.edu.chd.view.SlidingFinishLayout;
import cn.edu.chd.view.SlidingFinishLayout.OnSlidingFinishListener;
import cn.edu.chd.view.YiTitleBar;
import cn.edu.chd.view.YiTitleBar.LeftButtonClickListener;

/**
 * @author Rowand jj
 *
 *���ڽ��棬�����ǲ���webview��ʽ
 */
public class CopyRight extends Activity
{
	/**
	 * ������
	 */
	private YiTitleBar ytb_about = null;
	private WebView mWebView = null;
	/**
	 * ������ɾ�����ܵĲ���
	 */
	private SlidingFinishLayout msFinishLayout = null;
	/**
	 * htmlλ��
	 */
	private static final String path = "file:///android_asset/about.html";
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_about);
		initTitleBar();
		
		mWebView = (WebView) findViewById(R.id.webview_about);
		mWebView.loadUrl(path);
		msFinishLayout = (SlidingFinishLayout) findViewById(R.id.slide_finish_cr);
		msFinishLayout.setTouchView(mWebView);
		//���û����¼�
		msFinishLayout.setOnSlidingFinishListener(new OnSlidingFinishListener()
		{
			@Override
			public void onSlidingFinish()
			{
				CopyRight.this.finish();//���ٵ�ǰactivity
			}
		});
	}
	private void initTitleBar()
	{
		ytb_about = (YiTitleBar) findViewById(R.id.ytb_about);
		ytb_about.setTitleName(R.string.str_about);
		ytb_about.setLeftButtonBGResource(R.drawable.setting_title_bar_selector);
		ytb_about.setOnLeftButtonClickListener(new LeftButtonClickListener()
		{
			@Override
			public void leftButtonClick()
			{
				CopyRight.this.finish();
				overridePendingTransition(R.anim.slide_remain, R.anim.out_left);
			}
		});
	}
	@Override
	public void onBackPressed()
	{
		super.onBackPressed();
		overridePendingTransition(R.anim.slide_remain, R.anim.out_right);
	}
	
}






















