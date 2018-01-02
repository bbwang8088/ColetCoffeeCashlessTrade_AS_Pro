package tech.bbwang.www.activity;

import tech.bbwang.www.R;
import tech.bbwang.www.util.SystemUtil;
import android.app.Activity;
import android.content.Intent;
//import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class SuperActivity extends Activity implements OnClickListener {

	protected View main;
	final static int COUNTS = 5;// 点击次数
	final static long DURATION = 5 * 1000;// 规定有效时间
	long[] mHits = new long[COUNTS];

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// onWindowFocusChanged();
		// hideBottomUIMenu();
		TypefaceUtil.replaceFont(this, "fonts/fzcqt.ttf");
		//点击logo五下,进入工厂模式
		ImageView iv = ((ImageView) this.findViewById(R.id.logo));
		if (null != iv) {
			iv.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
					mHits[mHits.length - 1] = SystemClock.uptimeMillis();
					if (mHits[0] >= (SystemClock.uptimeMillis() - DURATION)) {
						Intent intent = new Intent(SuperActivity.this,
								AdminActivity.class);
						startActivity(intent);
						finish();
					}
				}

			});
		}
		TextView no = ((TextView) findViewById(R.id.terminal_code));
		if( null != no ){
			no.setText("编号：" + SystemUtil.getIMEI(this));
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.connect, menu);
		return true;
	}

	// protected void hideBottomUIMenu() {
	// if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower
	// api
	// View v = this.getWindow().getDecorView();
	// v.setSystemUiVisibility(View.GONE);
	// } else if (Build.VERSION.SDK_INT >= 19) {
	// //for new api versions.
	// View decorView = getWindow().getDecorView();
	// int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
	// | View.SYSTEM_UI_FLAG_FULLSCREEN;
	// decorView.setSystemUiVisibility(uiOptions);
	// }
	// }

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if (hasFocus) {
			this.getWindow()
					.getDecorView()
					.setSystemUiVisibility(
							View.SYSTEM_UI_FLAG_LAYOUT_STABLE
									| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
									| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
									| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
									| View.SYSTEM_UI_FLAG_FULLSCREEN
									| View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
		}
	}

	@Override
	public void onClick(View arg0) {
		// int i = main.getSystemUiVisibility();
		// if (i == View.SYSTEM_UI_FLAG_HIDE_NAVIGATION) {
		// main.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
		// } else if (i == View.SYSTEM_UI_FLAG_VISIBLE){
		// main.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
		// } else if (i == View.SYSTEM_UI_FLAG_LOW_PROFILE) {
		// main.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
		// }
	}

}
