package cn.edu.chd.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import cn.edu.chd.utils.BitmapLruCacheHelper;
import cn.edu.chd.utils.NativeImageLoader;
import cn.edu.chd.utils.NativeImageLoader.NativeImageLoaderCallback;
import cn.edu.chd.yitu.R;

/**
 * @author Rowand jj
 *
 *ͼƬ������
 *	���ܣ�1.���ȴӻ����л�ȡͼƬ�������ȡ�����ٴ�·���м���
 *		2.����ʱֹͣ���أ�����������ʼ����	
 */		
public class YiImageAdapter extends BaseAdapter implements OnScrollListener
{
	private static final String TAG = "YiImageAdapter";
	/**
	 * �󶨵�GridView����
	 */
	private GridView g;
	/**
	 * ������
	 */
	private Context context;
	/**
	 * ����
	 */
	private List<String> data;
	/**
	 * ͼƬ������
	 */
	private NativeImageLoader imageLoader;
	/**
	 * �Ƿ��һ�ν���
	 */
	private boolean isFirstEnter = true;
	
	private Point mPoint;
	
	private int mFirstVisibleItem;
	
	private int mVisibleItemCount;

	public YiImageAdapter(Context context, List<String> data, GridView g,
			Point point)
	{
		this.context = context;
		this.data = data;
		this.g = g;
		this.mPoint = point;
		//��ʼ��ͼƬ������
		this.imageLoader = NativeImageLoader.getInstance();
		
		//�󶨹����¼�������
		g.setOnScrollListener(this);
	}

	@Override
	public int getCount()
	{
		return data.size();
	}

	@Override
	public Object getItem(int position)
	{
		return data.get(position);
	}

	@Override
	public long getItemId(int position)
	{
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		final ViewHolder holder;
		final ImageView imageView;
		String path = data.get(position);
		if (convertView == null)
		{
			holder = new ViewHolder();
			//�Ӳ����ļ��м��ز���
			convertView = LayoutInflater.from(context).inflate(R.layout.gridview_item_my_works,null);
			holder.imageView = (ImageView) convertView.findViewById(R.id.my_works_item_image);
			convertView.setTag(holder);
		} else
		{
			holder = (ViewHolder) convertView.getTag();
		}
		imageView = holder.imageView;
		imageView.setTag(path);
		Bitmap bitmap = BitmapLruCacheHelper.getInstance().getBitmapFromMemCache(path);
		if (bitmap != null)
		{
			imageView.setImageBitmap(bitmap);
		} else
		{
			imageView.setImageResource(R.drawable.default_bg);
			
		}
		return convertView;
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState)
	{
		Log.i(TAG,"SCROLL STATE CHANGE..");
		if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE)// ����ֹͣʱ��������ͼƬ
		{
			showImage(mFirstVisibleItem, mVisibleItemCount);
		} else
		{
			cancellTask();
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount)
	{
		mFirstVisibleItem = firstVisibleItem;
		mVisibleItemCount = visibleItemCount;

		if (isFirstEnter && visibleItemCount > 0)
		{
			showImage(firstVisibleItem, visibleItemCount);
			isFirstEnter = false;
		}
	}

	/**
	 * ��ʾͼƬ���ȴӻ������ң����û�ҵ��Ϳ����߳�����
	 * 
	 * @param firstVisibleItem
	 *            ��һ���ɼ����id
	 * @param visibleItemCount
	 *            �ɼ��������
	 */
	private void showImage(int firstVisibleItem, int visibleItemCount)
	{
		for (int i = firstVisibleItem; i < firstVisibleItem + visibleItemCount; i++)
		{
			String mImageUrl = data.get(i);
			final ImageView mImageView = (ImageView) g.findViewWithTag(mImageUrl);
			imageLoader.loadNativeImage(mImageUrl, mPoint,new NativeImageLoaderCallback()
			{
				@Override
				public void onImageLoad(Bitmap bitmap, String path)
				{
					if(mImageView != null && bitmap!=null)
                    {
                        mImageView.setImageBitmap(bitmap);//���غ�ֱ�����õ�view������
                    }
				}
			});
		}
	}

	/**
	 * ȡ������
	 */
	public void cancellTask()
	{
		imageLoader.cancellTask();
	}
	private static class ViewHolder
	{
		private ImageView imageView;
	}
}
