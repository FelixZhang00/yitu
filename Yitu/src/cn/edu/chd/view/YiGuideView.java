package cn.edu.chd.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ViewSwitcher.ViewFactory;
import cn.edu.chd.utils.BitmapLruCacheHelper;
import cn.edu.chd.utils.BitmapUtils;
import cn.edu.chd.yitu.R;

/**
 * @author Rowand jj
 * 
 *         Ӧ�õ�һ�ΰ�װʱ����һ���������棬��ؽ���ͼƬ�����л�չʾ�� �����Դ˶��������˷�װ
 */
public class YiGuideView extends FrameLayout implements OnTouchListener,
		ViewFactory
{
	private OnGuideFinishListener mListener = null;
	/**
	 * ��ǰ�����λ��
	 */
	private int current_pos = 0;
	/**
	 * ��ָ����ʱ��x����
	 */
	private float touchDownX = 0;
	/**
	 * ��ָ�ſ�ʱ��x����
	 */
	private float touchUpX = 0;
	private static final int SIZE_NEED_CHANGE = 120;
	private static final String TAG = "UserGuideView";
	private int[] imageData;
	private Context context;
	private static int reqWidth;
	private static int reqHeight;
	
	private ImageSwitcher mImageSwitcher = null;
	private LinearLayout mViewGroup = null;
	
	private ImageView[] mTips = null;

	public YiGuideView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		this.context = context;
		
		
		LayoutInflater.from(getContext()).inflate(R.layout.yi_guide_view,this);
		mImageSwitcher =  (ImageSwitcher) findViewById(R.id.yi_guide_vs);
		mViewGroup = (LinearLayout) findViewById(R.id.yi_guide_viewgroup);
		
		
		WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		reqWidth = manager.getDefaultDisplay().getWidth();
		reqHeight = manager.getDefaultDisplay().getHeight();
		Log.i(TAG, "reqWidth = " + reqWidth + ",reqHeight =" + reqHeight);
		mImageSwitcher.setOnTouchListener(this);
		// ������ͼ����
		mImageSwitcher.setFactory(this);

	}

	/**
	 * ���ô���ʾ��ͼƬ��Դ
	 * 
	 * @param data
	 */
	public void setImageData(int[] data)
	{
		if (data != null && data.length > 0)
		{
			this.imageData = data;
			// ��ʼ��ʱ��ʾ��һ��ͼƬ
			mImageSwitcher.setImageDrawable(new BitmapDrawable(this.loadBitmap(imageData[0] + "", reqWidth, reqHeight)));
			
			mTips = new ImageView[data.length];
			for(int i  = 0; i < mTips.length; i++)
			{
				ImageView iv = new ImageView(context);
				mTips[i] = iv;
				LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(new ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));         
	            layoutParams.rightMargin = 7;  
	            layoutParams.leftMargin = 7;  
	            iv.setBackgroundResource(R.drawable.page_indicator_unfocused);  
				mViewGroup.addView(iv,layoutParams);
			}
			setImageBackground(current_pos);
		}
	}
	private void setImageBackground(int selectItems)
	{    
        for(int i = 0; i < mTips.length; i++)
        {    
            if(i == selectItems)
            {    
                mTips[i].setBackgroundResource(R.drawable.page_indicator_focused);    
            }else
            {    
            	mTips[i].setBackgroundResource(R.drawable.page_indicator_unfocused);    
            }    
        }    
	}
	/**
	 * @param listener
	 *            ������������ִ����ɺ�Ļص�����
	 */
	public void setOnGuideFinishListener(OnGuideFinishListener listener)
	{
		this.mListener = listener;
	}

	public interface OnGuideFinishListener
	{
		public void onGuideFinish();
	}

	@Override
	public boolean onTouch(View v, MotionEvent event)
	{
		switch (event.getAction())
		{
		case MotionEvent.ACTION_DOWN:
			touchDownX = event.getX();
			break;
		case MotionEvent.ACTION_UP:
			touchUpX = event.getX();
			if (touchDownX - touchUpX > SIZE_NEED_CHANGE)// ��ָ�������󻬶�,��ʾ��һ��ͼƬ
			{
				if(current_pos == imageData.length - 1 )
				{
					if(mListener!=null)
					{
						mListener.onGuideFinish();//ִ�лص�
					}
				}else
				{
					current_pos = current_pos + 1;
					// ����ͼƬ����ͻ����Ķ���
					Animation in_right = AnimationUtils.loadAnimation(context,R.anim.in_right);
					Animation out_left = AnimationUtils.loadAnimation(context,R.anim.out_left);
					mImageSwitcher.setInAnimation(in_right);
					mImageSwitcher.setOutAnimation(out_left);
					mImageSwitcher.setImageDrawable(new BitmapDrawable(getResources(),loadBitmap(imageData[current_pos] + "", reqWidth,reqHeight)));
					setImageBackground(current_pos);				
				}
				
			} else if (touchUpX - touchDownX > SIZE_NEED_CHANGE)// ��ָ�������һ�������ʾ��һ��ͼƬ
			{
				if(current_pos == 0)
				{
					break;
				}else
				{
					current_pos--;
					setImageBackground(current_pos);				
					Animation in_left = AnimationUtils.loadAnimation(context,R.anim.in_left);
					Animation out_right = AnimationUtils.loadAnimation(context,R.anim.out_right);
					mImageSwitcher.setInAnimation(in_left);
					mImageSwitcher.setOutAnimation(out_right);
					mImageSwitcher.setImageDrawable(new BitmapDrawable(getResources(),loadBitmap(imageData[current_pos] + "", reqWidth,reqHeight)));
				}
			}
			break;
		}
		return true;
	}

	@Override
	public View makeView()
	{
		ImageView iv = new ImageView(context);
		iv.setLayoutParams(new LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,FrameLayout.LayoutParams.MATCH_PARENT));
		iv.setScaleType(ScaleType.FIT_XY);
		return iv;
	}

	/**
	 * ����һ��ͼƬ �ȴӻ����в��ң����û���ڴ�ָ����Դ·����Ѱ��
	 * 
	 * @param resId
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 */
	private Bitmap loadBitmap(String resId, int reqWidth, int reqHeight)
	{
		Bitmap bitmap = BitmapLruCacheHelper.getInstance().getBitmapFromMemCache(resId);
		if (bitmap == null)
		{
			bitmap = BitmapUtils.decodeSampledBitmapFromResource(getResources(), Integer.parseInt(resId), reqWidth,reqHeight);
			BitmapLruCacheHelper.getInstance().addBitmapToMemCache(resId,bitmap);
		}
		return bitmap;
	}
}





