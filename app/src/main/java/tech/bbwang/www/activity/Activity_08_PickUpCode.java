package tech.bbwang.www.activity;

import tech.bbwang.www.R;
import tech.bbwang.www.util.CashlessConstants;
import tech.bbwang.www.util.ConfigFileUtil;
import tech.bbwang.www.util.SystemUtil;
import tech.bbwang.www.ws.CheckCode;
import tech.bbwang.www.ws.WSUtil;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
//import android.text.method.PasswordTransformationMethod;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class Activity_08_PickUpCode extends SuperActivity implements OnClickListener,OnTouchListener {

	ImageView button_pickup_ok = null;
	ImageView button_pickup_cancel = null;
	EditText pickup_code = null;
	
	ImageView input_no9 = null;
	ImageView input_no8 = null;
	ImageView input_no7 = null;
	ImageView input_no6 = null;
	ImageView input_no5 = null;
	ImageView input_no4 = null;
	ImageView input_no3 = null;
	ImageView input_no2 = null;
	ImageView input_no1 = null;
	ImageView input_no0 = null;
	ImageView input_allClear = null;
	ImageView input_delete = null;
	TextView codeCheckMsg = null;
	
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				// 移除所有的msg.what为0等消息，保证只有一个循环消息队列再跑
				handler.removeMessages(0);
				if( null != codeCheckMsg ){
					codeCheckMsg.setVisibility(View.INVISIBLE);
				}
				break;
			default:
				break;
			}
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		main = getLayoutInflater().inflate(R.layout.activity_08_pick_up_code, null);
//		main.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
//		main.setOnClickListener(this);
		setContentView(main);
		super.onCreate(savedInstanceState);
		codeCheckMsg = (TextView)this.findViewById(R.id.codeCheckMsg);
		codeCheckMsg.setVisibility(View.INVISIBLE);
		
		button_pickup_ok = ((ImageView) this.findViewById(R.id.button_pickup_ok));
		button_pickup_ok.setOnClickListener(this);
		button_pickup_cancel = ((ImageView) this.findViewById(R.id.button_pickup_cancel));
		button_pickup_cancel.setOnClickListener(this);
		pickup_code = ((EditText) this.findViewById(R.id.pickup_code));
		pickup_code.clearFocus();
		//pickup_code.setTransformationMethod(PasswordTransformationMethod.getInstance());
		initSoftKeyBoard();
	}

	
	private void initSoftKeyBoard(){
		((ImageView)this.findViewById(R.id.input_no9)).setOnClickListener(this);
		((ImageView)this.findViewById(R.id.input_no8)).setOnClickListener(this);
		((ImageView)this.findViewById(R.id.input_no7)).setOnClickListener(this);
		((ImageView)this.findViewById(R.id.input_no6)).setOnClickListener(this);
		((ImageView)this.findViewById(R.id.input_no5)).setOnClickListener(this);
		((ImageView)this.findViewById(R.id.input_no4)).setOnClickListener(this);
		((ImageView)this.findViewById(R.id.input_no3)).setOnClickListener(this);
		((ImageView)this.findViewById(R.id.input_no2)).setOnClickListener(this);
		((ImageView)this.findViewById(R.id.input_no1)).setOnClickListener(this);
		((ImageView)this.findViewById(R.id.input_no0)).setOnClickListener(this);
		((ImageView)this.findViewById(R.id.input_clear)).setOnClickListener(this);
		((ImageView)this.findViewById(R.id.input_delete)).setOnClickListener(this);
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.pick_up_code, menu);
		return true;
	}

	@Override
	public void onClick(View arg0) {
		
		Intent intent = new Intent();
		Bundle data = new Bundle();
		Class<?> target = null;
		switch (arg0.getId()) {
		case R.id.button_pickup_ok:
			String code = this.pickup_code.getText().toString();
			Message msg = new Message();
			if( null == code || code.length() == 0 ){
				codeCheckMsg.setVisibility(View.VISIBLE);
				codeCheckMsg.setText("请输入提货码");
				msg.what = 0;
				handler.sendMessageDelayed(msg, 3000);
				return;
			}
			
			ConfigFileUtil file = ColetApplication.getApp().getConfigFile();
			String terminal_code = SystemUtil.getIMEI(this);
			
			CheckCode ck = WSUtil.getInstance().checkCode(file.getFranchiseId(), terminal_code, code, 0);
			
			
			switch( ck.getStatus() ){
			case 0:
				data.clear();
				data.putString("outTradeNo", "-1");
				data.putInt("coffeeType", CashlessConstants.COFFEE_TYPE_ITALY);
				data.putInt("coffeeSize", CashlessConstants.CUP_SIZE_SMALL);
				data.putInt("needSuger", CashlessConstants.WANT_SUGER_NO);
				intent.putExtras(data);
				intent.setClass(Activity_08_PickUpCode.this, Activity_06_MakeCoffee.class);
				Activity_08_PickUpCode.this.startActivity(intent);
				break;
			case -1:
				codeCheckMsg.setVisibility(View.VISIBLE);
				codeCheckMsg.setText("无法连接服务器验证，请稍候重试");
				msg.what = 0;
				handler.sendMessageDelayed(msg, 3000);
				break;
			case 1:
			case 2:
			case 3:
			case 4:
				codeCheckMsg.setVisibility(View.VISIBLE);
				codeCheckMsg.setText(ck.getMessage());
				msg.what = 0;
				handler.sendMessageDelayed(msg, 3000);
				break;
			}
			
			
//			if( code.equals("665554") ){
//				data.clear();
//				data.putString("outTradeNo", "-1");
//				data.putInt("coffeeType", CashlessConstants.COFFEE_TYPE_ITALY);
//				data.putInt("coffeeSize", CashlessConstants.CUP_SIZE_SMALL);
//				data.putInt("needSuger", CashlessConstants.WANT_SUGER_NO);
//				intent.putExtras(data);
//				intent.setClass(Activity_08_PickUpCode.this, Activity_05_PutCup.class);
//				Activity_08_PickUpCode.this.startActivity(intent);
//			}
			break;
		case R.id.button_pickup_cancel:
			target = Activity_02_Welcome.class;
			intent.setClass(this, target);
			this.startActivity(intent);
			break;
		case R.id.input_no9:
			this.pickup_code.append("9");
			break;
		case R.id.input_no8:
			this.pickup_code.append("8");
			break;
		case R.id.input_no7:
			this.pickup_code.append("7");
			break;
		case R.id.input_no6:
			this.pickup_code.append("6");
			break;
		case R.id.input_no5:
			this.pickup_code.append("5");
			break;
		case R.id.input_no4:
			this.pickup_code.append("4");
			break;
		case R.id.input_no3:
			this.pickup_code.append("3");
			break;
		case R.id.input_no2:
			this.pickup_code.append("2");
			break;
		case R.id.input_no1:
			this.pickup_code.append("1");
			break;
		case R.id.input_no0:
			this.pickup_code.append("0");
			break;
		case R.id.input_clear:
			this.pickup_code.setText("");
			break;
		case R.id.input_delete:
			String tmp = this.pickup_code.getText().toString();
			if( tmp != null && tmp.length() > 0 ){
				this.pickup_code.setText(tmp.substring(0, tmp.length()-1));
			}
			break;
		default:
			break;

		}

	}


	@Override
	public boolean onTouch(View arg0, MotionEvent arg1) {
		return false;
	}

}
