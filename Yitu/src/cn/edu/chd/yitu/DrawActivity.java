package cn.edu.chd.yitu;

import java.io.File;
import java.io.FileOutputStream;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Toast;
import cn.edu.chd.utils.BitmapLruCacheHelper;
import cn.edu.chd.utils.BitmapUtils;
import cn.edu.chd.utils.YiUtils;
import cn.edu.chd.values.ApplicationValues;
import cn.edu.chd.view.PaintPreview;
import cn.edu.chd.view.YiDrawView;
import cn.edu.chd.view.YiRotateMenu;
import cn.edu.chd.view.YiRotateMenu.OnMenuItemClickListener;

import com.polites.android.GestureImageView;

/**
 *
 *��ͼ
 */
public class DrawActivity extends Activity implements android.view.View.OnClickListener,OnSeekBarChangeListener
{
	private static final String TAG = "DrawActivity";
	/**
	 * ������
	 */
	private int canvas_width;
	/**
	 * ������
	 */
	private int canvas_height;
	
	private YiDrawView dv_canvas = null;
	
	/**
	 * ���ܲ˵�
	 */
	private YiRotateMenu mRotateMenu = null;
	
	/**
	 * �޼������ť
	 */
	private ImageView iv_browser = null;
	
	/**
	 * ͼԪ���ư�ť
	 */
	private ImageView iv_copy = null;
	
	/**
	 * ͼԪճ����ť
	 */
	private ImageView iv_paste = null;
	
	/**
	 *ͼԪ�Ŵ� 
	 */
	private ImageView iv_tuyuan_large = null;
	
	/**
	 * ͼԪ��С
	 */
	private ImageView iv_tuyuan_small = null;
	
	/**
	 * ͼԪ����
	 */
	private ImageView iv_tuyuan_rotate_left = null;
	
	/**
	 * ͼԪ����
	 */
	private ImageView iv_tuyuan_rotate_right = null;
	
	private FrameLayout fl_change = null;
	
	private GestureImageView mGestureImageView = null;
	
	/**
	 * ��ǰģʽ(��ͼ/�޼����������)
	 */
	private boolean isPaintMode = true;
	/**
	 * �ļ�����λ��
	 */
	private File file = new File(YiUtils.getPath(),YiUtils.getCurrentDate()+".jpg");
	/**
	 * ����ͼ
	 */
	private Bitmap background_pic = null;
	
	/**
	 * ��Ļ���
	 */
	private int screenWidth;
	private int screenHeight;
	
	/**
	 * ����ͼƬ��λ��
	 */
	private String bgPath = null;
	
	private PopupWindow mPopupWindow_pen = null;
	
	private PopupWindow mPoputWindow_color = null;
	
	/*��ɫ��ť*/
	private ImageView iv_color1 = null;
	private ImageView iv_color2 = null;
	private ImageView iv_color3 = null;
	private ImageView iv_color4 = null;
	private ImageView iv_color5 = null;
	private ImageView iv_color6 = null;
	private ImageView iv_color7 = null;
	private ImageView iv_color8 = null;
	private ImageView iv_color9 = null;
	
	/*���ʰ�ť*/
	private ImageView iv_pen1 = null;
	private ImageView iv_pen2 = null;
	private ImageView iv_pen3 = null;
	private ImageView iv_pen4 = null;
	
	/*ͼԪ*/
	private ImageView tuyuan_bezier = null;
	private ImageView tuyuan_shouhui = null;
	private ImageView tuyuan_cicle = null;
	private ImageView tuyuan_line = null;
	private ImageView tuyuan_polygon = null;
	private ImageView tuyuan_square = null;
	private ImageView tuyuan_zhexian = null;
	
	
	private PaintPreview ppView = null;
	
	/*���ƻ��ʴ�С��͸����*/
	private SeekBar sb_pen_size = null;
	private SeekBar sb_pen_alpha = null;
	
	/*��ɫ��ARGBֵ*/
	private static final int[] colors = {
		0xFFDE5510,0xFF21AAE7,
		0xFFA51410,0xFF008242,
		0xFFFF415A,0xFFFFBA00,
		0xFF9C71EF,0xFF638A08,
		0xFF000000
		};
	
	/**
	 * ����������
	 */
	public SensorManager mSensorManager = null;
	
	/**
	 * �������ص��ӿ�
	 */
	public SensorEventListener mSensorListener = null;
	
	/**
	 * ���ٶȴ�����
	 */
	public Sensor mAccSensor = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_draw);
		bgPath = getIntent().getStringExtra(ApplicationValues.Base.IMAGE_STORE_PATH);
		
		initConfiguration();
		initComponent();
		initPenAndTuyuanWindow();
		initColorWindow();
		initSensor();
	}
	@Override
	protected void onResume()
	{
		super.onResume();
		//ע�ᴫ�����ص��¼�
		mSensorManager.registerListener(mSensorListener, mAccSensor, SensorManager.SENSOR_DELAY_GAME);
	}
	@Override
	protected void onStop()
	{
		super.onStop();
		//��󴫸���
		mSensorManager.unregisterListener(mSensorListener, mAccSensor);
	}
	@Override
	protected void onDestroy()
	{
		//����bitmap
		super.onDestroy();
		if(background_pic != null)
		{
			background_pic.recycle();
			background_pic = null;
		}
		//ɾ����ʱ�ļ�
		String type = getIntent().getStringExtra(ApplicationValues.Base.PREVIEW_TYPE);
		if(ApplicationValues.Base.TYPE_CAMERA.equals(type))
		{
			new File(bgPath).delete();
		}
	}
	/**
	 * ��ʼ��������
	 */
	private void initSensor()
	{
		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		mAccSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		mSensorListener = new SensorEventListener()
		{
			@Override
			public void onSensorChanged(SensorEvent event)
			{
				float[] values = event.values;
				float x = Math.abs(values[0]);
//				float y = Math.abs(values[1]);
//				float z = Math.abs(values[2]);
				float value = 9;
//				if(x>=value || y>=value || z>=value+9.8)
				if(x>=value)
				{
					dv_canvas.setShaked(true);
				}
			}
			@Override
			public void onAccuracyChanged(Sensor sensor, int accuracy)
			{
			}
		};
	}
	/**
	 * �ؼ�������ĳ�ʼ��
	 */
	private void initComponent()
	{
		dv_canvas = (YiDrawView) findViewById(R.id.dv_canvas);
		
		mRotateMenu = (YiRotateMenu) findViewById(R.id.rotate_menu);
		
		iv_browser = (ImageView) findViewById(R.id.iv_browser);
		iv_browser.setOnClickListener(this);
		
		iv_copy = (ImageView) findViewById(R.id.iv_copy);
		iv_copy.setOnClickListener(this);
		
		iv_paste = (ImageView) findViewById(R.id.iv_paste);
		iv_paste.setOnClickListener(this);
		
		iv_tuyuan_large = (ImageView) findViewById(R.id.iv_tuyuan_large);
		iv_tuyuan_large.setOnClickListener(this);
		
		iv_tuyuan_small = (ImageView) findViewById(R.id.iv_tuyuan_small);
		iv_tuyuan_small.setOnClickListener(this);
		
		iv_tuyuan_rotate_left = (ImageView) findViewById(R.id.iv_tuyuan_ratate_left);
		iv_tuyuan_rotate_left.setOnClickListener(this);
		
		iv_tuyuan_rotate_right = (ImageView) findViewById(R.id.iv_tuyuan_rotate_right);
		iv_tuyuan_rotate_right.setOnClickListener(this);
		
		
		fl_change = (FrameLayout) findViewById(R.id.fl_change);
		
		//Ϊ�˵����õ���¼�
		mRotateMenu.setOnMenuItemClickListener(new OnMenuItemClickListener()
		{
			@Override
			public void OnItemClick(View view)
			{
				switch (view.getId())
				{
				case R.id.second_left://undo
					undo();
					break;
				case R.id.second_right://redo
					redo();
					break;
				case R.id.third_1://����
					saveWorks();
					break;
				case R.id.third_2://��ջ���
					clearCanvas();
					break;
				case R.id.third_3://��
					updatePreview();
					mPopupWindow_pen.showAtLocation(findViewById(R.id.fl_root), Gravity.CENTER,0,-50);
					break;
				case R.id.third_4://���
					setFillMode();
					break;
				case R.id.third_5://��Ƥ
					chooseEraser();
					break;
				case R.id.third_6://��Ч
					imageEffect();
					break;
				case R.id.third_7://��ɫ
					mPoputWindow_color.showAtLocation(findViewById(R.id.fl_root), Gravity.RIGHT|Gravity.BOTTOM,0,0);
					break;
				}
			}
		});
		//��Ļ���
		screenWidth = getWindowManager().getDefaultDisplay().getWidth();
		screenHeight = getWindowManager().getDefaultDisplay().getHeight();
		
		String type = getIntent().getStringExtra(ApplicationValues.Base.PREVIEW_TYPE);
		if(ApplicationValues.Base.TYPE_NORMAL_MODEL.equals(type))
		{
			background_pic = Bitmap.createBitmap(canvas_width, canvas_height,Config.ARGB_8888);
			background_pic.eraseColor(getIntent().getIntExtra(TabNormalModel.COLOR, 0));
		
		}else if(ApplicationValues.Base.TYPE_CAMERA.equals(type))
		{
			background_pic = BitmapUtils.decodeSampledBitmapFromFile(bgPath, screenWidth, screenHeight);
		}else if(ApplicationValues.Base.TYPE_GALLERY.equals(type))
		{
			background_pic = BitmapUtils.decodeSampledBitmapFromFile(bgPath, screenWidth, screenHeight);
		}
		else if(ApplicationValues.Base.TYPE_MY_WORKS.equals(type))
		{
			background_pic = BitmapUtils.decodeSampledBitmapFromFile(bgPath, screenWidth, screenHeight);
			file = new File(bgPath);
		}
		//�ܹؼ������û���
		dv_canvas.setImageBitmap(background_pic);
	}
	/**
	 * ͼƬ��Ч
	 */
	private void imageEffect()
	{
		Intent intent = new Intent(this,ImageEffect.class);
		intent.putExtra("IMAGE",combine());
		this.startActivity(intent);
		this.finish();
	}
	
	/**
	 * ��ʼ������
	 */
	private void initConfiguration()
	{
		SharedPreferences sp = getSharedPreferences(ApplicationValues.Settings.SETTING_PREF, Context.MODE_PRIVATE);
		boolean isScreenAlwaysOn = sp.getBoolean(ApplicationValues.Settings.SCREEN_STATE,false);
		
		if(isScreenAlwaysOn)
		{
			//������Ļ����
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
			Log.i(TAG,"------->SCREEN ON...");
		}
		//��ȡ�������
		canvas_width = Integer.parseInt(sp.getString(ApplicationValues.Settings.CANVAS_WIDTH,getWindowManager().getDefaultDisplay().getWidth()+""));
		canvas_height = Integer.parseInt(sp.getString(ApplicationValues.Settings.CANVAS_HEIGHT,getWindowManager().getDefaultDisplay().getHeight()+""));
		
		Log.i(TAG,"CANVAS,WIDTH = "+canvas_width+",HEIGHT="+canvas_height);
	}
	
	/**
	 * ��ʼ��ͼԪ�ͻ���ѡ��˵�
	 */
	private void initPenAndTuyuanWindow()
	{
		View v = getLayoutInflater().inflate(R.layout.popupwindow_pen_tuyuan,null);
		
		mPopupWindow_pen = new PopupWindow(v, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);
		
		iv_pen1 = (ImageView) v.findViewById(R.id.pen_pen1);
		iv_pen2 = (ImageView) v.findViewById(R.id.pen_pen2);
		iv_pen3 = (ImageView) v.findViewById(R.id.pen_pen3);
		iv_pen4 = (ImageView) v.findViewById(R.id.pen_pen4);
		
		tuyuan_bezier = (ImageView) v.findViewById(R.id.tuyuan_bezier);
		tuyuan_cicle = (ImageView) v.findViewById(R.id.tuyuan_oval);
		tuyuan_line = (ImageView) v.findViewById(R.id.tuyuan_line);
		tuyuan_polygon = (ImageView) v.findViewById(R.id.tuyuan_polygon);
		tuyuan_square = (ImageView) v.findViewById(R.id.tuyuan_rect);
		tuyuan_shouhui = (ImageView) v.findViewById(R.id.tuyuan_shouhui);
		tuyuan_zhexian = (ImageView) v.findViewById(R.id.tuyuan_broken_line);
		
		ppView = (PaintPreview) v.findViewById(R.id.pp_view);
		
		tuyuan_bezier.setOnClickListener(this);
		tuyuan_cicle.setOnClickListener(this);
		tuyuan_line.setOnClickListener(this);
		tuyuan_polygon.setOnClickListener(this);
		tuyuan_square.setOnClickListener(this);
		tuyuan_shouhui.setOnClickListener(this);
		tuyuan_zhexian.setOnClickListener(this);

		iv_pen1.setOnClickListener(this);
		iv_pen2.setOnClickListener(this);
		iv_pen3.setOnClickListener(this);
		iv_pen4.setOnClickListener(this);

		sb_pen_size = (SeekBar) v.findViewById(R.id.sb_pen_size);
		sb_pen_alpha = (SeekBar) v.findViewById(R.id.sb_pen_alpha);
		
		sb_pen_alpha.setOnSeekBarChangeListener(this);
		sb_pen_size.setOnSeekBarChangeListener(this);
		
		sb_pen_size.setProgress(dv_canvas.getPenSize());
		sb_pen_alpha.setProgress(dv_canvas.getPenAlpha());
		
		mPopupWindow_pen.setAnimationStyle(R.style.popup_window_style);
		mPopupWindow_pen.setTouchable(true);
		mPopupWindow_pen.setOutsideTouchable(true);
		mPopupWindow_pen.setBackgroundDrawable(new BitmapDrawable());
	}	
	
	/**
	 * ��ʼ����ɫѡ��˵�
	 */
	private void initColorWindow()
	{
		View v = getLayoutInflater().inflate(R.layout.popupwindow_color,null);
		
		mPoputWindow_color = new PopupWindow(v, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);
		
		iv_color1 = (ImageView) v.findViewById(R.id.iv_color1);
		iv_color2 = (ImageView) v.findViewById(R.id.iv_color2);
		iv_color3 = (ImageView) v.findViewById(R.id.iv_color3);
		iv_color4 = (ImageView) v.findViewById(R.id.iv_color4);
		iv_color5 = (ImageView) v.findViewById(R.id.iv_color5);
		iv_color6 = (ImageView) v.findViewById(R.id.iv_color6);
		iv_color7 = (ImageView) v.findViewById(R.id.iv_color7);
		iv_color8 = (ImageView) v.findViewById(R.id.iv_color8);
		iv_color9 = (ImageView) v.findViewById(R.id.iv_color9);
		
		iv_color1.setOnClickListener(this);
		iv_color2.setOnClickListener(this);
		iv_color3.setOnClickListener(this);
		iv_color4.setOnClickListener(this);
		iv_color5.setOnClickListener(this);
		iv_color6.setOnClickListener(this);
		iv_color7.setOnClickListener(this);
		iv_color8.setOnClickListener(this);
		iv_color9.setOnClickListener(this);
		
		mPoputWindow_color.setTouchable(true);
		mPoputWindow_color.setOutsideTouchable(true);
		mPoputWindow_color.setBackgroundDrawable(new BitmapDrawable());
		mPoputWindow_color.setAnimationStyle(R.style.popup_window_style);
	}
	/**
	 * ��Ϊ��ɫģʽ
	 */
	private void setFillMode()
	{
		dv_canvas.setFillMode(!dv_canvas.isFillMode());
	}
	/**
	 * ѡ����Ƥ
	 */
	private void chooseEraser()
	{
		dv_canvas.setEraserMode(!dv_canvas.getEraserMode());
	}
	
	/**
	 * redo
	 */
	private void redo()
	{
		dv_canvas.redo();
	}
	
	/**
	 * undo
	 */
	private void undo()
	{
		dv_canvas.undo();
	}
	
	private String combine()
	{
		try
		{
			if(file.exists())
			{
				file.delete();
			}
			dv_canvas.getDrawData().compress(CompressFormat.JPEG, 100, new FileOutputStream(file));
			BitmapLruCacheHelper.getInstance().removeBitmapFromMemCache(file.getAbsolutePath());
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return file.getAbsolutePath();
	}
	/**
	 * ������Ʒ
	 */
	public void saveWorks()
	{
		combine();
		Toast.makeText(this,R.string.str_has_saved,Toast.LENGTH_SHORT).show();
	}
	/**
	 * ��ջ���
	 */
	public void clearCanvas()
	{
		//��û�л�ͼ���ݣ�ֱ�ӷ���
		if(!dv_canvas.hasData())
		{
			return;
		}
		showDialog();
		//���򵯳��Ի���
	}
	/**
	 * �ı����ģʽ����ͼ/�޼����������ţ�
	 */
	private void changeOperateMode()
	{
		isPaintMode = !isPaintMode;
		if(isPaintMode)//��ͼģʽ
		{
			dv_canvas.setVisibility(View.VISIBLE);//��ʾ��ͼ����
			mRotateMenu.setVisibility(View.VISIBLE);
			if(mGestureImageView != null)
			{
				fl_change.removeView(mGestureImageView);//ɾ����mGestureImageView
				mGestureImageView = null;
			}
		}else//���ģʽ
		{	//��̬���һ��GestureImageView
			dv_canvas.setVisibility(View.GONE);//���ػ�ͼ����
			mRotateMenu.setVisibility(View.GONE);//���ز˵�
			mGestureImageView = new GestureImageView(this);
			mGestureImageView.setImageBitmap(dv_canvas.getDrawData());//����mGestureImageView��������ͼƬ
			fl_change.addView(mGestureImageView);//��ӵ����ָ��ڵ���
		}
	}
	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
		case R.id.iv_browser:
			changeOperateMode();
			break;
		case R.id.iv_copy:
			if(dv_canvas.copyTuyuan())
				Toast.makeText(this, "��ѡ���ͼԪ�Ѿ����Ƶ�������!", Toast.LENGTH_SHORT).show();
			else
				Toast.makeText(this, "��û��ѡ���κ�ͼԪ!", Toast.LENGTH_SHORT).show();
			break;
		case R.id.iv_paste:
			if(!dv_canvas.pasteTuyuan())
				Toast.makeText(this, "������������!", Toast.LENGTH_SHORT).show();
			break;
		case R.id.iv_tuyuan_large:
			dv_canvas.enlarge();
			break;
		case R.id.iv_tuyuan_small:
			dv_canvas.shrink();
			break;
		case R.id.iv_tuyuan_ratate_left:
			dv_canvas.rotateLeft();
			break;
		case R.id.iv_tuyuan_rotate_right:
			dv_canvas.rotateRight();
			break;
			
		//-----------------------------------------------------------
		case R.id.iv_color1:
			dv_canvas.setPenColor(colors[0]);
			mPoputWindow_color.dismiss();
			break;
		case R.id.iv_color2:
			dv_canvas.setPenColor(colors[1]);
			mPoputWindow_color.dismiss();
			break;
		case R.id.iv_color3:
			dv_canvas.setPenColor(colors[2]);
			mPoputWindow_color.dismiss();
			break;
		case R.id.iv_color4:
			dv_canvas.setPenColor(colors[3]);
			mPoputWindow_color.dismiss();
			break;
		case R.id.iv_color5:
			dv_canvas.setPenColor(colors[4]);
			mPoputWindow_color.dismiss();
			break;
		case R.id.iv_color6:
			dv_canvas.setPenColor(colors[5]);
			mPoputWindow_color.dismiss();
			break;
		case R.id.iv_color7:
			dv_canvas.setPenColor(colors[6]);
			mPoputWindow_color.dismiss();
			break;
		case R.id.iv_color8:
			dv_canvas.setPenColor(colors[7]);
			mPoputWindow_color.dismiss();
			break;
		case R.id.iv_color9:
			dv_canvas.setPenColor(colors[8]);
			mPoputWindow_color.dismiss();
			break;
		//--------------------------------------------------
		case R.id.pen_pen1://��ͨ
			dv_canvas.setPaintStyle(ApplicationValues.PaintStyle.MODE_PLAIN_PEN);
			updatePreview();
			break;
		case R.id.pen_pen2://����
			dv_canvas.setPaintStyle(ApplicationValues.PaintStyle.MODE_EMBOSS_PEN);
			updatePreview();
			break;
		case R.id.pen_pen3://ģ��
			dv_canvas.setPaintStyle(ApplicationValues.PaintStyle.MODE_BLUR_PEN);
			updatePreview();
			break;
		case R.id.pen_pen4:
			dv_canvas.setPaintStyle(ApplicationValues.PaintStyle.MODE_SHADER_PEN);
			updatePreview();
			break;
		//--------------------------------------------------
		case R.id.tuyuan_shouhui://�ֻ�
			dv_canvas.setCurrentTuyuanType(ApplicationValues.TuyuanStyle.STYLE_FREE);
			updatePreview();
			break;
		case R.id.tuyuan_bezier://���α���������
			dv_canvas.setCurrentTuyuanType(ApplicationValues.TuyuanStyle.STYLE_BEZIER);
			updatePreview();
			break;
		case R.id.tuyuan_oval://Բ/��Բ
			dv_canvas.setCurrentTuyuanType(ApplicationValues.TuyuanStyle.STYLE_OVAL);
			updatePreview();
			break;
		case R.id.tuyuan_line://ֱ��
			dv_canvas.setCurrentTuyuanType(ApplicationValues.TuyuanStyle.STYLE_LINE);
			updatePreview();
			break;
		case R.id.tuyuan_polygon://�����
			dv_canvas.setCurrentTuyuanType(ApplicationValues.TuyuanStyle.STYLE_POLYGN);
			updatePreview();
			break;
		case R.id.tuyuan_rect://����
			dv_canvas.setCurrentTuyuanType(ApplicationValues.TuyuanStyle.STYLE_RECT);
			updatePreview();
			break;
		case R.id.tuyuan_broken_line://����
			dv_canvas.setCurrentTuyuanType(ApplicationValues.TuyuanStyle.STYLE_BROKEN_LINE);
			updatePreview();
			break;
		}
	}
	
	/**
	 * ����Ԥ����ͼ
	 */
	private void updatePreview()
	{
		ppView.setAttrs(dv_canvas.getPenColor(), dv_canvas.getPenAlpha(),dv_canvas.getPenSize(),dv_canvas.getTuyuanStyle(), dv_canvas.getPaintStyle());
	}
	
	/**
	 * ��ʾ�Ի���
	 */
	private void showDialog()
	{
		LayoutInflater inflater = this.getLayoutInflater();
		View view = inflater.inflate(R.layout.layout_dialog,null);
		
		final Dialog d = new AlertDialog.Builder(this).create();
		d.show();
		d.setContentView(view);
		
		Button but_pos = (Button) view.findViewById(R.id.but_clear_positive);
		Button but_negative = (Button) view.findViewById(R.id.but_clear_negative);
		
		but_pos.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				dv_canvas.clearAll();
				Toast.makeText(DrawActivity.this,"�Ѿ����..",Toast.LENGTH_SHORT).show();
				d.dismiss();
			}
		});
		but_negative.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				d.dismiss();
			}
		});
	}
	
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser)
	{
		int id = seekBar.getId();
		switch (id)
		{
		case R.id.sb_pen_size:
				if(progress == 0)
					progress = 1;
				dv_canvas.setPenSize(progress);
				updatePreview();
			break;
		case R.id.sb_pen_alpha:
				dv_canvas.setPenAlpha(progress);
				updatePreview();
			break;
		}
	}
	@Override
	public void onStartTrackingTouch(SeekBar seekBar)
	{
	}
	@Override
	public void onStopTrackingTouch(SeekBar seekBar)
	{
	}
}
























