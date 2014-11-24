package cn.edu.chd.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Handler;
import android.os.Message;

/**
 * @author Rowand jj
 *
 *ͼƬ������
 *	���ڼ���ͼƬ���ȴӻ����м���ͼƬ�����û���ҵ������·������ͼƬ
 */
public class NativeImageLoader
{
	private static NativeImageLoader imageLoader = new NativeImageLoader();
	private static final int THREAD_NUM = 2;
	private static final int DEFAULT_WIDTH = 140;//Ĭ�Ͽ��
	private static final int DEFAULT_HEIGHT = 170;//Ĭ�ϸ߶�
	protected static final int LOAD_OK = 1;
	private ExecutorService threadPool = null;//�̳߳�
	
	private NativeImageLoader()
	{
	}

	public static NativeImageLoader getInstance()
	{
		return imageLoader;
	}

	/**
	 * ����ͼƬ
	 * @param path
	 * @param mPoint
	 * @param mCallBack
	 * @return
	 */
	public Bitmap loadNativeImage(final String path,Point mPoint, final NativeImageLoaderCallback mCallBack)
	{
		if(path == null)
		{
			return null;
		}
		if(mPoint == null)
		{
			mPoint = new Point(DEFAULT_WIDTH,DEFAULT_HEIGHT);
		}
		final int x = mPoint.x;
		final int y = mPoint.y;
		//�ӻ����ȡͼƬ
		Bitmap bitmap = BitmapLruCacheHelper.getInstance().getBitmapFromMemCache(path);
		if(bitmap != null)
		{
			return bitmap;
		}else
		{
			final Handler handler = new Handler()
			{
				@Override
				public void handleMessage(Message msg)
				{
					if(msg.what == LOAD_OK)
					{
						mCallBack.onImageLoad((Bitmap)msg.obj, path);
					}
				}
			};
			//�����̼߳���bitmap���������غõ�bitmap�ŵ���Ϣ������
			getThreadPool().execute(new Runnable()
			{
				@Override
				public void run()
				{
					//���ص��Ǿ����ü���bitmap
					Bitmap bitmap = BitmapUtils.decodeSampledBitmapFromFile(path, x, y);
					Message msg = Message.obtain(handler, LOAD_OK, bitmap);
					msg.sendToTarget();
					BitmapLruCacheHelper.getInstance().addBitmapToMemCache(path, bitmap);
				}
			});
		}
		return null;
	}
	/**
	 * �h��
	 */
	public synchronized void cancellTask()
	{
		if(threadPool != null)
        {
		  threadPool.shutdownNow();
		  threadPool = null;
        }
	}
	
	/**
     * ��ȡ�̳߳�ʵ��
     */
    public ExecutorService getThreadPool()
    {
        if (threadPool == null)
        {
            synchronized (ExecutorService.class)
            {
                if (threadPool == null)
                {
                	threadPool = Executors.newFixedThreadPool(THREAD_NUM);
                }
            }
        }
        return threadPool;
    }

	/**
	 * @author Rowand jj
	 *
	 *�ص��ӿ�
	 */
	public interface NativeImageLoaderCallback
	{
		public void onImageLoad(Bitmap bitmap,String path);
	}
}











