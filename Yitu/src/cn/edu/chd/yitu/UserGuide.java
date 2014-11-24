package cn.edu.chd.yitu;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import cn.edu.chd.view.YiGuideView;
import cn.edu.chd.view.YiGuideView.OnGuideFinishListener;

/**
 * @author Rowand jj �û���������
 */
public class UserGuide extends Activity
{
	private YiGuideView ugv_guide = null;
	private static final int[] images =
	{
		R.raw.lead1,
		R.raw.lead2,
		R.raw.lead3,
		R.raw.lead4,
	};
	protected static final String TAG = "UserGuide";
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_user_guide);
		
		ugv_guide = (YiGuideView) findViewById(R.id.ugv_guide);
		ugv_guide.setImageData(images);
		
		if(isFirstIn())//��һ�ν���Ӧ�ó���ʱ�����������滬�������һ��ͼʱӦ���Զ�����
		{
			ugv_guide.setOnGuideFinishListener(new OnGuideFinishListener()
			{
				@Override
				public void onGuideFinish()
				{
					SharedPreferences sp = UserGuide.this.getSharedPreferences(SplashActivity.SP_SPLASH, Context.MODE_PRIVATE);
					Editor editor = sp.edit();
					editor.putBoolean(SplashActivity.IS_FIRST_IN, false);
					editor.commit();
					
					Intent intent = new Intent(UserGuide.this,ChooseModel.class);
					UserGuide.this.startActivity(intent);
					overridePendingTransition(R.anim.slide_remain, R.anim.alpha_1_to_0);
					UserGuide.this.finish();
				}
			});
		}else
		{
			ugv_guide.setOnGuideFinishListener(new OnGuideFinishListener()
			{
				@Override
				public void onGuideFinish()
				{
					//DO NOTHING
				}
			});
		}
	}
	
	/**
	 * �Ƿ��ǵ�һ�ν���Ӧ�ó���
	 * @return
	 */
	private boolean isFirstIn()
	{
		SharedPreferences sp = this.getSharedPreferences(SplashActivity.SP_SPLASH, Context.MODE_PRIVATE);
		return sp.getBoolean(SplashActivity.IS_FIRST_IN, true);
	}
	@Override
	public void onBackPressed()
	{
		super.onBackPressed();
		overridePendingTransition(R.anim.slide_remain, R.anim.out_right);
	}
}






























