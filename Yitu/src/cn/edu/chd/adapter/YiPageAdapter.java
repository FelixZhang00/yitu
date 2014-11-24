package cn.edu.chd.adapter;

import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

/**
 * @author Rowand jj
 * ҳ��������
 */
public class YiPageAdapter extends FragmentPagerAdapter
{
	private static final String TAG = "YiPageAdapter";
	/**
	 * ҳ�����ݼ���
	 */
	private List<Fragment> fgs = null;
	private FragmentManager mFragmentManager;
	/**
	 * �����ݷ����ı�ʱ�Ļص��ӿ�
	 */
	private OnReloadListener mListener;

	public YiPageAdapter(FragmentManager fm, List<Fragment> fgs)
	{
		super(fm);
		this.fgs = fgs;
		mFragmentManager = fm;
	}

	@Override
	public Fragment getItem(int index)
	{
		Log.i(TAG,"ITEM CREATED...");
		return fgs.get(index);
	}

	@Override
	public int getCount()
	{
		return fgs.size();// ����ѡ�����
	}

	@Override
	public int getItemPosition(Object object)
	{
		return POSITION_NONE;
	}

	/**
	 * ��������ҳ������
	 * @param items
	 */
	public void setPagerItems(List<Fragment> items)
	{
		if (items != null)
		{
			for (int i = 0; i < fgs.size(); i++)
			{
				mFragmentManager.beginTransaction().remove(fgs.get(i)).commit();
			}
			fgs = items;
		}
		this.notifyDataSetChanged();
	}
	/**
	 *��ҳ�����ݷ����ı�ʱ����Ե��ô˷���
	 * 
	 * �����������ݣ�����������Ϣ�ɻص�����ʵ��
	 */
	public void reLoad()
	{
		if(mListener != null)
		{
			mListener.onReload();
		}
	}
	public void setOnReloadListener(OnReloadListener listener)
	{
		this.mListener = listener;
	}
	/**
	 * @author Rowand jj
	 *�ص��ӿ�
	 */
	public interface OnReloadListener
	{
		public void onReload();
	}
}





