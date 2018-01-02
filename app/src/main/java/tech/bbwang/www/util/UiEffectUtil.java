package tech.bbwang.www.util;

import android.graphics.ColorMatrixColorFilter;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

public class UiEffectUtil {

	static final float[] BG_PRESSED = new float[] { 1, 0, 0, 0, -100, 0, 1, 0, 0,
			-100, 0, 0, 1, 0, -100, 0, 0, 0, 1, 0 };
	static final float[] BG_NOT_PRESSED = new float[] { 1, 0, 0, 0, 0, 0, 1, 0, 0,
			0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0 };
	
	public static boolean onTouch(View v, MotionEvent event) {


		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			
			/**
			 * 通过设置滤镜来改变图片亮度 ,按下效果
			 */
			v.setDrawingCacheEnabled(true);
			((ImageView) v).setColorFilter(new ColorMatrixColorFilter(
					BG_PRESSED));
			if (null == v.getBackground()) {
				((ImageView) v).getDrawable().setColorFilter(
						new ColorMatrixColorFilter(BG_PRESSED));
			} else {
				v.getBackground().setColorFilter(
						new ColorMatrixColorFilter(BG_PRESSED));
			}
			
		} else {
			/**
			 * 不按下的效果，即原图效果
			 */
			((ImageView) v).setColorFilter(new ColorMatrixColorFilter(
					BG_NOT_PRESSED));
			if (null == v.getBackground()) {
				((ImageView) v).getDrawable().setColorFilter(
						new ColorMatrixColorFilter(BG_NOT_PRESSED));
			} else {
				v.getBackground().setColorFilter(
						new ColorMatrixColorFilter(BG_NOT_PRESSED));
			}
		}

		return false;
	}

}
