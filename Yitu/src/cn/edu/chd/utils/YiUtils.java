package cn.edu.chd.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Locale;

import cn.edu.chd.values.ApplicationValues;

import android.os.Environment;
import android.text.format.DateFormat;
import android.util.Log;

public class YiUtils
{
	private static final String TAG = "YiUtils";

	/**
	 * �ж�sd���Ƿ����
	 * @return
	 */
	public static boolean isSDCardAvailable()
	{
		return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
	}
	/**
	 * ��ȡ��ǰʱ��
	 * 
	 * @return
	 */
	public static String getCurrentDate()
	{
		return DateFormat.format("yyyyMMdd_hhmmss",Calendar.getInstance(Locale.CHINA)).toString();
	}
	
	/**
	 * ����ָ���ļ����µ�����ͼƬ,����ͼƬ������
	 *	<���������ļ���>
	 */
	public static String[] traverseImages(String dir)
	{
		/*�����ж�*/
		if(dir == null)
			return null;
		File path = new File(dir);
		if(!path.exists() || !path.isDirectory())
		{
			return null;
		}
		
		String[] imageNames = path.list(new FilenameFilter()
		{
			@Override
			public boolean accept(File dir, String filename)
			{
				return filename.endsWith("jpg") || filename.endsWith("jpeg")||filename.endsWith("bmp")||filename.endsWith("png");
			}
		});
		if(imageNames == null)
		{
			return null;
		}
		String[] data = new String[imageNames.length];
		for(int i = 0; i < imageNames.length; i++)
		{
			data[i] = dir+"/"+imageNames[i];
			Log.i(TAG,"FILENAME:"+data[i]);
		}
		return data;
	}
	/**
	 * ��ȡ�洢·��
	 * @return
	 */
	public static String getPath()
	{
		String path = null;
		if(YiUtils.isSDCardAvailable())
		{
			path = Environment.getExternalStorageDirectory().getAbsolutePath()+ApplicationValues.Base.SAVE_PATH;
		}else
		{
			path = Environment.getDataDirectory().getAbsolutePath()+ApplicationValues.Base.SAVE_PATH;
		}
		File dir = new File(path);
		if(!dir.exists())
		{
			dir.mkdirs();
		}
		return path;
	}
	/**
	 * @return
	 */
	public static String getTempPath()
	{
		String path = null;
		if(YiUtils.isSDCardAvailable())
		{
			path = Environment.getExternalStorageDirectory().getAbsolutePath()+ApplicationValues.Base.TEMP_PATH;
		}else
		{
			path = Environment.getDataDirectory().getAbsolutePath()+ApplicationValues.Base.TEMP_PATH;
		}
		File dir = new File(path);
		if(!dir.exists())
		{
			dir.mkdirs();
		}
		return path;
	}
	/**
	 * �����е�ͼƬ������ָ��λ��
	 * @param in
	 * @param file
	 */
	public static void copyBitmapFromStream(InputStream in,File file)
	{
		FileOutputStream fout = null;
		try
		{
			fout = new FileOutputStream(file);
			byte[] buffer = new byte[1024];
			int len = 0;
			while((len = in.read(buffer)) != -1)
			{
				fout.write(buffer,0,len);
			}
			fout.close();
			in.close();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}






