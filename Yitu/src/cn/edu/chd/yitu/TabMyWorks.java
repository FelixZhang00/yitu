package cn.edu.chd.yitu;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import cn.edu.chd.adapter.YiImageAdapter;
import cn.edu.chd.utils.YiUtils;
import cn.edu.chd.values.ApplicationValues;

/**
 * @author Rowand jj
 * 	 "�ҵ���Ʒ"ѡ� �������ڣ�onCreate->onCreateView
 */
public class TabMyWorks extends Fragment
{
	/**
	 * �Ƿ�����Ʒ
	 */
	public boolean has_works;
	private GridView mGridView = null;
	private static final int DEFAULT_WIDTH = 140;//Ĭ�Ͽ��
	private static final int DEFAULT_HEIGHT = 170;//Ĭ�ϸ߶�
	private static final String TAG = "TabMyWorks";
	public static final String POSITION = "position";
	/**
	 * ��Ʒ�ļ����µ�ͼƬ��
	 */
	private List<String> imageNames;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		has_works = getImageNames().size()==0 ? false : true;//�ж�ָ���ļ������Ƿ����ļ�����
		if (has_works)
		{
			imageNames = getImageNames();
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		View view = null;
		if (has_works)
		{
			view = inflater.inflate(R.layout.layout_tab_mine, null);
			mGridView = (GridView) view.findViewById(R.id.grid_view_tab_mine_works);
		} else
		{
			view = inflater.inflate(R.layout.layout_tab_mine_noworks, null);
		}
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
		if (has_works)
		{
			loadImage();
			mGridView.setOnItemClickListener(new OnItemClickListener()
			{
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id)
				{
					//TODO ���
					Intent intent = new Intent(TabMyWorks.this.getActivity(),BrowseWorks.class);
					intent.putExtra(POSITION,position+"");
					TabMyWorks.this.startActivity(intent);
					
				}
			});
			registerForContextMenu(mGridView);//ע�������Ĳ˵�
		}
	}
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo)
	{
		//���������Ĳ˵�
		super.onCreateContextMenu(menu, v, menuInfo);
		getActivity().getMenuInflater().inflate(R.menu.menu_works_item_selected, menu);
	}
	@Override
	public boolean onContextItemSelected(MenuItem item)
	{
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();//��Ҫ����ת��
		switch (item.getItemId())
		{
		case R.id.id_menu_works_delete:
			delete(info.position);
			return true;
		case R.id.id_menu_works_edit:
			edit(info.position);
			return true;
		default:
			return super.onContextItemSelected(item);
		}
		
	}
	
	/**
	 * ɾ����Ʒ
	 */
	private void delete(int position)
	{
		String fileName = imageNames.get(position);
		new File(fileName).delete();
		
		ChooseModel activity = (ChooseModel) getActivity();
		activity.getAdapter().reLoad();
	}

	/**
	 * �༭��Ʒ
	 * @param position
	 */
	private void edit(int position)
	{
		Intent intent = new Intent(getActivity(),CanvasPreview.class);
		intent.putExtra(ApplicationValues.Base.PREVIEW_TYPE, ApplicationValues.Base.TYPE_MY_WORKS);
		intent.putExtra(TabDIY.IMAGE_DATA,imageNames.get(position));
		startActivity(intent);
	}
	/**
	 * ��ȡָ���ļ����µ���ƷͼƬ���Ƽ���
	 * 
	 * @return
	 */
	private List<String> getImageNames()
	{
		String dir = null;
		List<String> imageNames = new ArrayList<String>();//��ֹnullָ��
		if (YiUtils.isSDCardAvailable())
		{
			dir = Environment.getExternalStorageDirectory().getAbsolutePath()+ ApplicationValues.Base.SAVE_PATH;
			String[] temp1 = YiUtils.traverseImages(dir);
			if (temp1 != null)
				imageNames = new ArrayList<String>(Arrays.asList(temp1));
		}
		dir = Environment.getDataDirectory().getAbsolutePath()+ ApplicationValues.Base.SAVE_PATH;
		String[] temp2 = YiUtils.traverseImages(dir);
		if (temp2 != null)
		{
			for (String str : temp2)
			{
				imageNames.add(str);
			}
		}
		Log.i(TAG,"---------->GET_IMAGE_NAMES RUN,NUM = "+imageNames.size());
		return imageNames;
	}

	/**
	 * ����ͼƬ����ʾ��GridView��
	 */
	private void loadImage()
	{
		Point point = new Point(DEFAULT_WIDTH,DEFAULT_HEIGHT);
		mGridView.setAdapter(new YiImageAdapter(getActivity(), imageNames, mGridView, point));
	}
}






