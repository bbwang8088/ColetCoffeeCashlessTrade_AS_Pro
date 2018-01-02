package tech.bbwang.www.activity;

import tech.bbwang.www.R;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Activity_vipcard_pay extends Dialog{

	private TextView inputNumber = null;
	private Button cancelButton = null;
	private Button okButton = null;
	private Handler callBackHandler = null; 
	private String price = "";
	private String outTradeNo = "";
	
	
	private View.OnClickListener onDefaultClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View view) {
			dismiss();
		}
	};

	private View.OnClickListener onCancelListener = onDefaultClickListener;
	private View.OnClickListener onPayConfirmListener = onDefaultClickListener;
	
	public String getInputNumber(){
		return this.inputNumber.getText().toString();
	}
	
	
	
	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}
	
	public String getOutTradeNo() {
		return outTradeNo;
	}

	public void setOutTradeNo(String outTradeNo) {
		this.outTradeNo = outTradeNo;
	}


	public Handler getCallBackHandler() {
		return callBackHandler;
	}

	public void setCallBackHandler(Handler callBackHandler) {
		this.callBackHandler = callBackHandler;
	}

	public Handler handler = new Handler(Looper.getMainLooper()) {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:// 验证码按钮无效倒计时

				break;
			}
		}
	};

//	@Override
//	public void onClick(View view) {
//
//		String phone = this.inputNumber.getText().toString();
//		String fId = ColetApplication.getApp().getConfigFile().getFranchiseId();
//		String tCode = SystemUtil.getIMEI(this.getContext());
//
//		switch (view.getId()) {
//		case R.id.buttonCancel_pay:
//			dismiss();
//			break;
//		case R.id.buttonOK_pay:
//			QueryCard cardInfo = null;
//
//			int checkWithPhoneNumber = 0;// 0 无,1手机号,2会员卡号
//
//			if (phone.equals("")) {
//				Toast.makeText(this.getContext(), "请输入会员卡号或者手机号",
//						Toast.LENGTH_SHORT).show();
//				return;
//			} else if (phone.length() > 0 && phone.length() < 6) {
//				Toast.makeText(this.getContext(), "会员卡号长度不正确",
//						Toast.LENGTH_SHORT).show();
//				return;
//			} else if ((phone.length() > 6 && phone.length() < 11)
//					|| phone.length() > 11) {
//				Toast.makeText(this.getContext(), "会员卡号或者手机号长度不正确",
//						Toast.LENGTH_SHORT).show();
//				return;
//			} else if (phone.length() == 6) {// 会员卡号
//				checkWithPhoneNumber = 2;
//			} else if (phone.length() == 11) {
//				if ((SystemUtil.isMobileNO(phone) == false)) {
//					Toast.makeText(this.getContext(), "不是有效的手机号码",
//							Toast.LENGTH_SHORT).show();
//					return;
//				} else {
//					checkWithPhoneNumber = 1;
//				}
//			}
//
//			{// 不管老客户还是新客户
//
//				if ( checkWithPhoneNumber == 1) {// 手机号验证
//					cardInfo = WSUtil.getInstance().card(fId, tCode, phone, "");
//				} else if (checkWithPhoneNumber == 2) {// 会员卡号验证
//					cardInfo = WSUtil.getInstance().card(fId, tCode, "", phone);
//				} else {
//					return;
//				}
//				if (cardInfo == null) {
//					Toast.makeText(this.getContext(), "似乎有点小问题，请再试一遍",
//							Toast.LENGTH_SHORT).show();
//					return;
//				} else if (cardInfo.getStatus() == -1) {
//					Toast.makeText(this.getContext(), cardInfo.getMessage(),
//							Toast.LENGTH_SHORT).show();
//					return;
//				} else if (cardInfo.getStatus() == 0) {
//					// 验证余额是否足够
//					String p = Integer.valueOf(((Float)(Float.valueOf(price) * 100)).intValue()).toString();
//					if( Integer.valueOf(cardInfo.getData().getRemain()) < Integer.valueOf(p)){
//						Toast.makeText(this.getContext(), "会员卡余额不足，请更换其他支付方式或者充值后再购买",
//								Toast.LENGTH_SHORT).show();
//						this.dismiss();
//						return;
//					}else{
////						String p = Integer.valueOf((String.valueOf((Float.valueOf(price) * 100.00f)))).toString();
//						ChargeCard ret = WSUtil.getInstance().cardConsume(ColetApplication.getApp().getConfigFile().getFranchiseId(), 
//								SystemUtil.getIMEI(this.getContext()), p, "3", outTradeNo, cardInfo.getData().getId(), cardInfo.getData().getPhone());
//						//String franchise,String terminal_code,String trade_no,String payType
//						WSUtil.getInstance().updateTradeStatusPayed(ColetApplication.getApp().getConfigFile().getFranchiseId(), 
//								SystemUtil.getIMEI(this.getContext()),outTradeNo,"3");
//						if( ret.getStatus() == 0 ){
//							if(handler != null){
//								this.dismiss();
//								handler.sendEmptyMessage(1001);//取消另外的交易
//								handler.sendEmptyMessage(7);
//								handler.sendEmptyMessageDelayed(6, 500);
//								//return;
//							}
//						}else{
//							Toast.makeText(this.getContext(), ret.getMessage(),
//									Toast.LENGTH_SHORT).show();
//							return;
//						}
//						
//					}
//				}
//			}
//
//
//			
//			break;
//		default:
//			break;
//		}
//	}

	private Activity_vipcard_pay(Context context) {
		super(context, R.style.MyDialog);
		// super(context);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_vipcard_pay);

		this.inputNumber = (TextView) this.findViewById(R.id.inputNumber_pay);

		this.cancelButton = (Button) this.findViewById(R.id.buttonCancel_pay);
		this.okButton = (Button) this.findViewById(R.id.buttonOK_pay);

		this.cancelButton.setOnClickListener(onCancelListener);
		this.okButton.setOnClickListener(onPayConfirmListener);
	}

	@Override
	public void show() {
		super.show();
	}

	public static class Builder {

		private Activity_vipcard_pay mDialog;

		public Builder(Context context) {
			mDialog = new Activity_vipcard_pay(context);
		}

		public Builder setCancelable(boolean cancelable) {
			mDialog.setCancelable(cancelable);
			return this;
		}

		public Builder setOnCancelListener(OnCancelListener onCancelListener) {
			mDialog.setOnCancelListener(onCancelListener);
			return this;
		}

		public Builder setOnDismissListener(OnDismissListener onDismissListener) {
			mDialog.setOnDismissListener(onDismissListener);
			return this;
		}

		public Activity_vipcard_pay create() {
			return mDialog;
		}

		public Builder SetOnClickListener(View.OnClickListener onClickListener) {
			mDialog.onPayConfirmListener = onClickListener;
			return this;

		}

	}

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

}
