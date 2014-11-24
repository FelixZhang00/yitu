package cn.edu.chd.values;

import android.graphics.Color;

/**
 * @author Rowand jj
 *
 *�洢ȫ�ֱ���
 */
public interface ApplicationValues
{
	/**
	 *����������Ϣ
	 */
	public interface Base
	{
		/**
		 * sharepref�д洢��xml��
		 */
		public static final String BASE_PREF = "base_pref";
		/**
		 * ��Ʒ�����·��
		 */
		public static final String SAVE_PATH = "/yitu";
		/**
		 * ��ʱ�ļ���
		 */
		public static final String TEMP_PATH = "/yituTemp";
		
		/**
		 * ��ǰͼƬ�Ĵ洢·��
		 */
		public static final String IMAGE_STORE_PATH = "image_store_path";
		/*intent����*/
		public static final String PREVIEW_TYPE = "preview_type";//Ԥ������
		public static final String TYPE_CAMERA = "type_camera";
		public static final String TYPE_GALLERY = "type_gallery";
		public static final String TYPE_MY_WORKS = "type_my_works";
		public static final String TYPE_NORMAL_MODEL = "type_normal_model";
		
		public static final int RADIUS = 8;
	}
	/**
	 *������Ϣ
	 */
	public interface Settings 
	{
		/**
		 * sharepref�д洢��xml��
		 */
		public static final String SETTING_PREF = "setting_pref";
		
		/**
		 * ��Ļ״̬���Ƿ�ͼʱ��Ļ������
		 */
		public static final String SCREEN_STATE = "screen_state";
		
		/**
		 * ҹ��ģʽ
		 */
		public static final String NIGHT_MODEL = "night_model";
		
		/**
		 * "ҡһҡ"����app
		 */
		public static final String SHAKE_MODEL = "shake_model";
		
		/**
		 * ������
		 */
		public static final String CANVAS_WIDTH = "canvas_width";
		
		/**
		 * ������
		 */
		public static final String CANVAS_HEIGHT = "canvas_height";
	}
	
	/**
	 * @author Rowand jj
	 *������ʽ
	 */
	public interface PaintStyle
	{
		/**
		 * Ǧ��
		 */
		public static final int MODE_PLAIN_PEN = 1;
		
		public static final int MODE_EMBOSS_PEN = 2;
		
		public static final int MODE_BLUR_PEN = 3;
		
		/**
		 * ��ˢ
		 */
		public static final int MODE_BRUSH = 4;
		
		public static final int MODE_SHADER_PEN = 5;
	}
	
	public interface TuyuanStyle
	{
		/**
		 * �����ֻ�
		 */
		public static final int STYLE_FREE = 1;
		
		/**
		 * ���α���������
		 */
		public static final int STYLE_BEZIER = 2;

		/**
		 * Բ/��Բ
		 */
		public static final int STYLE_OVAL = 3;
		
		/**
		 * ����
		 */
		public static final int STYLE_RECT = 4;
		
		/**
		 * ֱ��
		 */
		public static final int STYLE_LINE = 6;
		
		/**
		 * ����
		 */
		public static final int STYLE_BROKEN_LINE = 7;
		
		/**
		 * �����
		 */
		public static final int STYLE_POLYGN = 8;
	}
	
	public interface PaintSettings
	{
		/**
		 * ����Ĭ����ɫ
		 */
		public static final int PENCOLOR_DEFAULT = Color.BLACK;
		
		/**
		 * ����Ĭ�ϴ�С
		 */
		public static final int PENSIZE_DEFAULT = 4;
		
		/**
		 * ��Ƥ���
		 */
		public static final int ERASER_WIDER_DEFAULT = 3;
		
		/**
		 * Ĭ��͸����
		 */
		public static final int PEN_ALPHA_DEFAULT = 0;
	}
}













