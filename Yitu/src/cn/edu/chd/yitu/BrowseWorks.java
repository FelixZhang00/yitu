package cn.edu.chd.yitu;

import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher.ViewFactory;
import cn.edu.chd.utils.BitmapUtils;
import cn.edu.chd.utils.YiUtils;

/**
 * @author Rowand jj
 *
 *��Ʒ�������
 */
public class BrowseWorks extends Activity implements OnGestureListener,ViewFactory
{
	private GestureDetector mGestureDetector = null;
	private int index = -1;
	/**
	 * ͼƬ·������
	 */
	private String[] mImageNames = null;
	
	/**
	 * ��ʾ��ǰ���ȣ���12/20
	 */
	private TextView mTextView = null;
	private ImageSwitcher mImageSwitcher = null;
	
	private static String TOTAL = null;
	
	int reqWidth,reqHeight;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_browse_works);
		
		initComponent();
		
		mImageNames = YiUtils.traverseImages(YiUtils.getPath());
		mGestureDetector = new GestureDetector(this,this);
		
		TOTAL = "/"+mImageNames.length;
		index = Integer.parseInt(getIntent().getStringExtra(TabMyWorks.POSITION));
		mTextView.setText(index+1+TOTAL);
		reqWidth = getWindowManager().getDefaultDisplay().getWidth();
	    reqHeight = getWindowManager().getDefaultDisplay().getHeight();
	    Drawable drawable = new BitmapDrawable(BitmapUtils.decodeSampledBitmapFromFile(mImageNames[index], reqWidth, reqHeight));
		mImageSwitcher.setFactory(this);
		mImageSwitcher.setImageDrawable(drawable);
	}
	
	private void initComponent()
	{
		mTextView = (TextView) findViewById(R.id.tv_browse);
		mImageSwitcher = (ImageSwitcher) findViewById(R.id.is_browse);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		return mGestureDetector.onTouchEvent(event);
	}
	@Override
	public View makeView()
	{
		ImageView iv = new ImageView(this);
	    iv.setScaleType(ScaleType.FIT_XY);
	    FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
	    iv.setLayoutParams(lp);
	    return iv;
	}
	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY)
	{
		 if(e1.getX() - e2.getX() > 120)//��ʾ��һ��
	     {
           if(index != mImageNames.length-1)
           {
                index++;
                Animation in_right = AnimationUtils.loadAnimation(this,R.anim.in_right);
                Animation out_left = AnimationUtils.loadAnimation(this,R.anim.out_left);
                mImageSwitcher.setInAnimation(in_right);
                mImageSwitcher.setOutAnimation(out_left);
                
                mImageSwitcher.setImageDrawable(new BitmapDrawable(BitmapUtils.decodeSampledBitmapFromFile(mImageNames[index], reqWidth, reqHeight)));
            }else
            {
                Toast.makeText(this,"�Ѿ������һ��..",Toast.LENGTH_SHORT).show();
            }
           mTextView.setText(index+1+TOTAL);
        }else if(e1.getX() - e2.getX() < -120)//��ʾ��һ��
        {
            if(index != 0)
            {
                index--;
                Animation in_left = AnimationUtils.loadAnimation(this,R.anim.in_left);
                Animation out_right = AnimationUtils.loadAnimation(this,R.anim.out_right);
                mImageSwitcher.setInAnimation(in_left);
                mImageSwitcher.setOutAnimation(out_right);
                
                mImageSwitcher.setImageDrawable(new BitmapDrawable(BitmapUtils.decodeSampledBitmapFromFile(mImageNames[index], reqWidth, reqHeight)));
            }else
            {
                Toast.makeText(this,"�Ѿ��ǵ�һ��..",Toast.LENGTH_SHORT).show();
            }
            mTextView.setText(index+1+TOTAL);
	    }
		return true;
	}
	@Override
	public boolean onDown(MotionEvent e)
	{
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
	}

	
}




















