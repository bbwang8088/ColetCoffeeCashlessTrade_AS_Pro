package tech.bbwang.www.activity;

import tech.bbwang.www.R;
import tech.bbwang.www.util.CashlessConstants;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
//import android.util.Log;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;

public class Activity_09_Error extends SuperActivity {

	ImageView errorImage = null;
	TextView errorMessage = null;
	
	// 静止状态时倒计时
	private long countTime = CashlessConstants.COUNT_PAY_FAILD;
	
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			Intent intent = new Intent();
			switch (msg.what) {
			case 0:
				// 移除所有的msg.what为0等消息，保证只有一个循环消息队列再跑
				handler.removeMessages(0);
				// 倒计时结束
				if (countTime == 0) {
//					if (CashlessConstants.debug) {
//						ColetApplication.getApp().logDebug("Activity_09_Error:倒计时结束，画面跳回Activity_02_Welcome");
//					}
					// 跳转回广告欢迎页面
					intent.setClass(Activity_09_Error.this, Activity_02_Welcome.class);
					Activity_09_Error.this.startActivity(intent);
					Activity_09_Error.this.finish();
				} else {
//					if (CashlessConstants.debug) {
//						Log.d("Activity_09_Error:", "count=" + countTime);
//					}
					countTime = countTime - 1;
					handler.sendEmptyMessageDelayed(0, 1000);
				}
				break;
			default:
				break;
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_09_error);
		errorImage = (ImageView)this.findViewById(R.id.error_image);
		errorMessage = (TextView)this.findViewById(R.id.error_message);
		Bundle data = this.getIntent().getExtras();
		if( data != null ){
			String errorFlag = data.getString("errorFlag");
			String errorMsg = data.getString("errorMsg");
			if( errorFlag != null && errorFlag.length() > 0){
				if( errorFlag.equals("coffeeMakeTimeOut")){
					errorImage.setImageResource(R.drawable.timeout);
					errorMessage.setText(R.string.message_coffee_make_timeout);
					handler.sendEmptyMessageDelayed(0, 1000);
				}else if( errorFlag.equals("payTimeOut")){
					errorImage.setImageResource(R.drawable.timeout);
					errorMessage.setText(R.string.message_pay_timeout);
					handler.sendEmptyMessageDelayed(0, 1000);
				}else if(errorFlag.equals("appError")){
					errorMessage.setText(R.string.message_system_error);
					handler.sendEmptyMessageDelayed(0, 1000);
				}else if(errorFlag.equals("coffeeError")){
					errorMessage.setText(errorMsg);
				}else if(errorFlag.equals("franchise_disabled")){
					errorImage.setImageResource(R.drawable.disabled);
					errorMessage.setText(R.string.message_franchise_disabled);
				}else if(errorFlag.equals("terminal_disabled")){
					errorImage.setImageResource(R.drawable.disabled);
					errorMessage.setText(R.string.message_terminal_disabled);
				}else if(errorFlag.equals("terminal_illegal")){
					errorImage.setImageResource(R.drawable.disabled);
					errorMessage.setText(R.string.message_terminal_disabled);
				}else if(errorFlag.equals("terminal_freeze")){
					errorImage.setImageResource(R.drawable.disabled);
					errorMessage.setText(R.string.message_terminal_disabled);
				}
			}
			super.onCreate(savedInstanceState);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.error, menu);
		return true;
	}

}
