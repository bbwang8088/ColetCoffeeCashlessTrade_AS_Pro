package tech.bbwang.www.activity;

import tech.bbwang.www.R;
import tech.bbwang.www.util.SystemUtil;
import tech.bbwang.www.ws.CreateCard;
import tech.bbwang.www.ws.QueryCard;
import tech.bbwang.www.ws.WSUtil;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class Activity_vipcard_confirm extends Dialog implements
		View.OnClickListener, OnCheckedChangeListener {

	private RadioGroup cardConfirm = null;
	private TextView inputNumber = null;
	private TextView confirmCode = null;
	private Button cancelButton = null;
	private Button okButton = null;
	private Button sendConfirmCode = null;
	private TextView textView_confirmCode = null;

	private LinearLayout bigLayout = null;

	private int currentSelectId = -1;

	private static final int COUNT_DOWN = 60;
	private int countDown = 0;

	private String listPrice = "";
	private String freshPrice = "";
	private String vipPrice = "";

	public String getListPrice() {
		return listPrice;
	}

	public void setListPrice(String listPrice) {
		this.listPrice = listPrice;
	}

	public String getFreshPrice() {
		return freshPrice;
	}

	public void setFreshPrice(String freshPrice) {
		this.freshPrice = freshPrice;
	}

	public String getVipPrice() {
		return vipPrice;
	}

	public void setVipPrice(String vipPrice) {
		this.vipPrice = vipPrice;
	}

	public Handler handler = new Handler(Looper.getMainLooper()) {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:// 验证码按钮无效倒计时
				if (countDown > 0) {
					countDown--;
					sendConfirmCode.setText("发送验证码(" + countDown + ")");
					handler.sendEmptyMessageDelayed(0, 1000);
				} else {
					sendConfirmCode.setEnabled(true);
					sendConfirmCode.setText("发送验证码");
				}
				break;
			}
		}
	};

	@Override
	public void onClick(View view) {

		String phone = this.inputNumber.getText().toString();
		String code = this.confirmCode.getText().toString();
		String fId = ColetApplication.getApp().getConfigFile().getFranchiseId();
		String tCode = SystemUtil.getIMEI(this.getContext());

		switch (view.getId()) {
		case R.id.send_ConfirmCode:
			if (phone.length() < 11) {
				Toast.makeText(this.getContext(), "请输入有效的手机号码",
						Toast.LENGTH_SHORT).show();
				return;
			} else if (phone.length() == 11
					&& (SystemUtil.isMobileNO(phone) == false)) {
				Toast.makeText(this.getContext(), "请输入有效的手机号码",
						Toast.LENGTH_SHORT).show();
				return;
			}
			countDown = COUNT_DOWN;
			this.sendConfirmCode.setEnabled(false);
			this.sendConfirmCode.setText("发送验证码(" + countDown + ")");
			handler.sendEmptyMessageDelayed(0, 1000);
			WSUtil.getInstance().validate(fId, tCode, phone);
			Toast.makeText(this.getContext(), "验证码已发送,请注意查收",
					Toast.LENGTH_SHORT).show();
			break;
		case R.id.buttonCancel:
			dismiss();
			break;
		case R.id.buttonOK:
			CreateCard card = null;
			QueryCard cardInfo = null;

			int checkWithPhoneNumber = 0;// 0 无,1手机号,2会员卡号

			if (currentSelectId == R.id.RadioButton_notHaveCard) {// 新开卡客户

				if (phone.equals("")) {
					Toast.makeText(this.getContext(), "请输入有效的手机号码",
							Toast.LENGTH_SHORT).show();
					return;
				} else if (phone.length() < 11 || phone.length() > 11) {
					Toast.makeText(this.getContext(), "请输入有效的手机号码",
							Toast.LENGTH_SHORT).show();
					return;
				} else if (phone.length() == 11
						&& (SystemUtil.isMobileNO(phone) == false)) {
					Toast.makeText(this.getContext(), "请输入有效的手机号码",
							Toast.LENGTH_SHORT).show();
					return;
				}

				checkWithPhoneNumber = 1;

				if (code.length() == 0) {
					Toast.makeText(this.getContext(), "请输入验证码",
							Toast.LENGTH_SHORT).show();
					return;
				} else if (code.length() < 4) {
					Toast.makeText(this.getContext(), "请输入4位数字验证码",
							Toast.LENGTH_SHORT).show();
					return;
				}

				card = WSUtil.getInstance().createCard(fId, tCode, phone,
						"储值卡用户", code);
				if (card == null) {
					Toast.makeText(this.getContext(), "似乎有点小问题，请再试一遍",
							Toast.LENGTH_SHORT).show();
					return;
				} else if (card.getStatus() == -1) {
					Toast.makeText(this.getContext(), card.getMessage(),
							Toast.LENGTH_SHORT).show();
					return;
				} else if (card.getStatus() == 0) {
					Toast.makeText(this.getContext(), card.getMessage(),
							Toast.LENGTH_SHORT).show();

				}
			} else {// 老卡客户
				if (phone.equals("")) {
					Toast.makeText(this.getContext(), "请输入会员卡号或者手机号",
							Toast.LENGTH_SHORT).show();
					return;
				} else if (phone.length() > 0 && phone.length() < 6) {
					Toast.makeText(this.getContext(), "会员卡号长度不正确",
							Toast.LENGTH_SHORT).show();
					return;
				} else if ((phone.length() > 6 && phone.length() < 11)
						|| phone.length() > 11) {
					Toast.makeText(this.getContext(), "会员卡号或者手机号长度不正确",
							Toast.LENGTH_SHORT).show();
					return;
				} else if (phone.length() == 6) {// 会员卡号
					checkWithPhoneNumber = 2;
				} else if (phone.length() == 11) {
					if ((SystemUtil.isMobileNO(phone) == false)) {
						Toast.makeText(this.getContext(), "不是有效的手机号码",
								Toast.LENGTH_SHORT).show();
						return;
					} else {
						checkWithPhoneNumber = 1;
					}
				}
			}

			{// 不管老客户还是新客户

				if (checkWithPhoneNumber == 1) {// 手机号验证
					cardInfo = WSUtil.getInstance().card(fId, tCode, phone, "");
				} else if (checkWithPhoneNumber == 2) {// 会员卡号验证
					cardInfo = WSUtil.getInstance().card(fId, tCode, "", phone);
				} else {
					return;
				}
				if (cardInfo == null) {
					Toast.makeText(this.getContext(), "似乎有点小问题，请再试一遍",
							Toast.LENGTH_SHORT).show();
					return;
				} else if (cardInfo.getStatus() == -1) {
					Toast.makeText(this.getContext(), "取得会员卡信息失败",
							Toast.LENGTH_SHORT).show();
					return;
				} else if (cardInfo.getStatus() == 0) {
					Toast.makeText(this.getContext(), "取得会员卡信息成功，即将进行充值",
							Toast.LENGTH_SHORT).show();
					// return;
				}
			}

			Intent intent = new Intent();
			intent.setClass(this.getContext(), Activity_04_Pay_Card.class);
			Bundle data = new Bundle();
			data.putString("listPrice", this.listPrice);

			if (cardInfo != null) {
				if (cardInfo.getData().getFirst_charge().equals("0")) {
					data.putString("realPrice", this.freshPrice);
				} else {
					data.putString("realPrice", this.vipPrice);
				}
			}
			data.putString("cardNumber", cardInfo.getData().getId());
			data.putString("cardPhone", cardInfo.getData().getPhone());
			data.putString("cardRemain", cardInfo.getData().getRemain());
			intent.putExtras(data);
			this.getContext().startActivity(intent);
			break;
		default:
			break;
		}
	}

	private Activity_vipcard_confirm(Context context) {
		super(context, R.style.MyDialog);
		// super(context);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_vipcard_confirm);

		this.cardConfirm = (RadioGroup) this
				.findViewById(R.id.RadioGroup_CardConfirm);
		this.inputNumber = (TextView) this.findViewById(R.id.inputNumber);

		this.textView_confirmCode = (TextView) this
				.findViewById(R.id.textView_confirmCode);
		this.confirmCode = (TextView) this.findViewById(R.id.confirmCode);
		this.sendConfirmCode = (Button) this
				.findViewById(R.id.send_ConfirmCode);
		this.cancelButton = (Button) this.findViewById(R.id.buttonCancel);
		this.okButton = (Button) this.findViewById(R.id.buttonOK);

		this.bigLayout = (LinearLayout) this
				.findViewById(R.id.LinearLayout0001);
		this.cardConfirm.setOnCheckedChangeListener(this);
		this.sendConfirmCode.setOnClickListener(this);
		this.cancelButton.setOnClickListener(this);
		this.okButton.setOnClickListener(this);

		this.bigLayout
				.setLayoutParams(new android.widget.FrameLayout.LayoutParams(
						540, 320));
		this.textView_confirmCode.setVisibility(View.GONE);
		this.confirmCode.setVisibility(View.GONE);
		this.sendConfirmCode.setVisibility(View.GONE);

		this.inputNumber
				.setOnEditorActionListener(new TextView.OnEditorActionListener() {

					@Override
					public boolean onEditorAction(TextView v, int actionId,
							KeyEvent event) {
						if (currentSelectId == R.id.RadioButton_notHaveCard) {
							if (countDown == 0) {
								sendConfirmCode.performClick();
							}
							confirmCode.requestFocus();
						}
						return false;
					}
				});

		this.confirmCode
				.setOnEditorActionListener(new TextView.OnEditorActionListener() {

					@Override
					public boolean onEditorAction(TextView v, int actionId,
							KeyEvent event) {

						if (actionId == EditorInfo.IME_ACTION_DONE) {
							InputMethodManager inputMethodManager = (InputMethodManager) Activity_vipcard_confirm.this
									.getContext().getSystemService(
											Context.INPUT_METHOD_SERVICE);
							inputMethodManager.hideSoftInputFromWindow(
									v.getWindowToken(), 0);

						}
						okButton.performClick();
						return true;
					}
				});
	}

	@Override
	public void show() {
		super.show();
	}

	public static class Builder {

		private Activity_vipcard_confirm mDialog;

		public Builder(Context context) {
			mDialog = new Activity_vipcard_confirm(context);
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

		public Activity_vipcard_confirm create() {
			return mDialog;
		}

		public Builder SetOnClickListener(View.OnClickListener onClickListener) {
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

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		this.currentSelectId = checkedId;
		switch (checkedId) {
		case R.id.RadioButton_haveCard:
			this.bigLayout
					.setLayoutParams(new android.widget.FrameLayout.LayoutParams(
							540, 320));
			this.inputNumber.setHint("请输入6位数字会员卡号或11位数字手机号");
			this.confirmCode.setVisibility(View.GONE);
			this.sendConfirmCode.setVisibility(View.GONE);
			this.textView_confirmCode.setVisibility(View.GONE);
			break;
		case R.id.RadioButton_notHaveCard:
			this.bigLayout
					.setLayoutParams(new android.widget.FrameLayout.LayoutParams(
							540, 420));
			this.inputNumber.setHint("请输入11位数字手机号");
			this.confirmCode.setVisibility(View.VISIBLE);
			this.sendConfirmCode.setVisibility(View.VISIBLE);
			this.textView_confirmCode.setVisibility(View.VISIBLE);
			break;
		default:
			break;
		}
	}

}
