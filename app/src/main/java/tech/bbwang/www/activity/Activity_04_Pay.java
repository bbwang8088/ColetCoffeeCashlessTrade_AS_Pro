package tech.bbwang.www.activity;

import java.io.File;

import tech.bbwang.www.R;
import tech.bbwang.www.util.CashlessConstants;
import tech.bbwang.www.util.QRCodeUtil;
import tech.bbwang.www.util.SystemUtil;
import tech.bbwang.www.util.ThreadPool;
import tech.bbwang.www.util.TradeServerUtil;
import tech.bbwang.www.util.UiEffectUtil;
import tech.bbwang.www.ws.ChargeCard;
import tech.bbwang.www.ws.QueryCard;
import tech.bbwang.www.ws.WSUtil;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageView.ScaleType;
import cn.colet.result.TradeResult;

import com.kanyuan.circleloader.CircleLoader;
import com.lid.lib.LabelImageView;

public class Activity_04_Pay extends SuperActivity implements OnClickListener,
		OnTouchListener {
	
	private Activity_vipcard_pay dialog = null;
	private ImageView alipay_qrcode = null;
	// private ImageView wechatpay_qrcode = null;
	private TextView pay_left_time = null;
	// private TextView pay_msg = null;
	private ImageView button_cancel_trade = null;

	private LabelImageView goodDetail = null;
	private CircleLoader circleloader = null;
	// private CircleLoader wechatpay = null;
	private ImageView button_alipay_trade = null;
	private ImageView button_wechatpay_trade = null;
	private ImageView button_card_trade = null;

	LinearLayout ly_cancel_trade = null;
	LinearLayout ly_alipay_trade = null;
	LinearLayout ly_wechat_trade = null;
	LinearLayout ly_card_trade = null;

	private TextView label_switch_pay_method = null;

	// 当前正在进行的交易号
	private static String outTradeNo = "";
	private static String subject = "";
	private static String price = "";
	private long curSec = CashlessConstants.COUNT_TIME;

	private static int coffeeType = CashlessConstants.COFFEE_TYPE_CAPPUCCION;
	private static String coffeeImage = "";
	private static int coffeeId = -1;
	// private static int needSuger = CashlessConstants.WANT_SUGER_NO;

	private boolean quering = false;
	private boolean doQuery = false;
	private static String current_gateway = CashlessConstants.TRADE_GATEWAY_ALIPAY;

	private String cardNumber = "";
	private String cardPhone = "";
	
	public Handler handler = new Handler(Looper.getMainLooper()) {
		public void handleMessage(android.os.Message msg) {
			Intent intent = new Intent();
			Bundle data = new Bundle();
			switch (msg.what) {
			case 8888:// 模拟点击微信支付
				button_wechatpay_trade.performClick();
				break;
			case 0:
				// 移除所有的msg.what为0等消息，保证只有一个循环消息队列再跑
				handler.removeMessages(0);
				// app的功能逻辑处理
				if (curSec == 0) {
					handler.sendEmptyMessageDelayed(5, 1000);
				} else {
					curSec = curSec - 1;
					pay_left_time.setText(curSec + "");//
					// 再次发出msg，循环更新
					handler.sendEmptyMessageDelayed(0, 1000);

					if (doQuery == true) {
						handler.sendEmptyMessage(2);
					}
				}
				break;
			case 11:
				handler.removeMessages(11);
				// app的功能逻辑处理
				if (curSec == 0) {
					ThreadPool.service
							.submit(new TradeCancelThread(
									CashlessConstants.TRADE_GATEWAY_ALIPAY,
									outTradeNo));
					ThreadPool.service
							.submit(new TradeCancelThread(
									CashlessConstants.TRADE_GATEWAY_WECHAT,
									outTradeNo));
					intent.setClass(Activity_04_Pay.this,
							Activity_09_Error.class);
					data.clear();
					data.putString("errorFlag", "appError");
					startActivity(intent);
					finish();
				} else {
					curSec = curSec - 1;
					// 再次发出msg,循环更新
					handler.sendEmptyMessageDelayed(11, 1000);
				}
				break;
			case 5:
				//ThreadPool.service.submit(new TradeCancelThread(CashlessConstants.TRADE_GATEWAY_VIPCARD,outTradeNo));
				ThreadPool.service.submit(new TradeCancelThread(
						CashlessConstants.TRADE_GATEWAY_ALIPAY, outTradeNo));
				ThreadPool.service.submit(new TradeCancelThread(
						CashlessConstants.TRADE_GATEWAY_WECHAT, outTradeNo));
				intent.setClass(Activity_04_Pay.this, Activity_09_Error.class);
				handler.sendEmptyMessage(1);// 停止计时Timer
				handler.sendEmptyMessage(3);// 停止查询Timer
//				handler.sendEmptyMessage(10);// 取消交易

				data.clear();
				data.putString("errorFlag", "payTimeOut");
				intent.putExtras(data);
				startActivity(intent);
				finish();
				break;
			case 6:
				data.clear();
				data.putString("outTradeNo", outTradeNo);
				data.putInt("coffeeType", coffeeType);
				// data.putInt("coffeeSize", coffeeSize);
				// data.putInt("needSuger", needSuger);
				data.putString("price", price);
				data.putString("cardNumber", cardNumber);
				data.putString("cardPhone", cardPhone);
				data.putString("current_gateway", current_gateway);
				intent.putExtras(data);
				intent.setClass(Activity_04_Pay.this,
						Activity_06_MakeCoffee.class);
				Activity_04_Pay.this.startActivity(intent);
				finish();
				break;
			case 1:
				// 直接移除，定时器停止
				handler.removeMessages(0);
				// handler.sendEmptyMessage(4);// 停止查询Timer
				handler.removeMessages(2);// 清空查询Timer
				curSec = CashlessConstants.COUNT_TIME;
				break;
			case 2:
				// 移除所有的msg.what为0等消息，保证只有一个循环消息队列再跑
				handler.removeMessages(2);
				// 前一次查询未结束本次查询忽略
				if (quering == false) {
					if (current_gateway
							.equals(CashlessConstants.TRADE_GATEWAY_ALIPAY)) {
						current_gateway = CashlessConstants.TRADE_GATEWAY_WECHAT;
					} else if (current_gateway
							.equals(CashlessConstants.TRADE_GATEWAY_WECHAT)) {
						current_gateway = CashlessConstants.TRADE_GATEWAY_ALIPAY;
					}
					ThreadPool.service.submit(new AlipayTradeQueryThread(
							current_gateway, outTradeNo));
				}
				// }
				break;
			case 3:
				// 直接移除，定时器停止
				handler.removeMessages(2);
				curSec = CashlessConstants.COUNT_TIME;
				break;
			case 7:
				button_cancel_trade.setVisibility(View.INVISIBLE);
				label_switch_pay_method.setText(R.string.message_pay_success);
				handler.sendEmptyMessage(1);
				break;
			case 8:
				// button_cancel_trade.setVisibility(View.INVISIBLE);
				label_switch_pay_method.setText(R.string.message_pay_failed);
				handler.sendEmptyMessage(1);
				break;
			case 9:// 取消交易并退回菜单画面
				//会员卡退款
				ThreadPool.service.submit(new TradeCancelThread(CashlessConstants.TRADE_GATEWAY_VIPCARD,outTradeNo));
				ThreadPool.service.submit(new TradeCancelThread(
						CashlessConstants.TRADE_GATEWAY_ALIPAY, outTradeNo));
				ThreadPool.service.submit(new TradeCancelThread(
						CashlessConstants.TRADE_GATEWAY_WECHAT, outTradeNo));
				// (new TradeCancelThread(outTradeNo)).start();
				// (new TradeCancelThread(outTradeNo,
				// CashlessConstants.TRADE_GATEWAY_WECHAT)).start();
				// intent.setClass(Activity_04_Pay.this,
				// Activity_02_Welcome.class);
				// Activity_04_Pay.this.startActivity(intent);
				// finish();
				break;
//			case 10:// 取消交易
//				ThreadPool.service.submit(new TradeCancelForVipCardThread(outTradeNo,price,cardNumber,cardPhone));
//				ThreadPool.service.submit(new TradeCancelThread(
//						CashlessConstants.TRADE_GATEWAY_ALIPAY, outTradeNo));
//				ThreadPool.service.submit(new TradeCancelThread(
//						CashlessConstants.TRADE_GATEWAY_WECHAT, outTradeNo));
//				// (new TradeCancelThread(outTradeNo)).start();
//				// (new TradeCancelThread(outTradeNo,
//				// CashlessConstants.TRADE_GATEWAY_WECHAT)).start();
//				break;
			case 1001:// 有条件取消交易
//				if (current_gateway
//						.equals(CashlessConstants.TRADE_GATEWAY_ALIPAY)) {
//					current_gateway = CashlessConstants.TRADE_GATEWAY_WECHAT;
//				} else if (current_gateway
//						.equals(CashlessConstants.TRADE_GATEWAY_WECHAT)) {
//					current_gateway = CashlessConstants.TRADE_GATEWAY_ALIPAY;
//				}else if (current_gateway
//						.equals(CashlessConstants.TRADE_GATEWAY_VIPCARD)) {
//					current_gateway = CashlessConstants.TRADE_GATEWAY_ALIPAY;
//					ThreadPool.service.submit(new TradeCancelForVipCardThread(outTradeNo,price,cardNumber,cardPhone));
//				}
//				
//				ThreadPool.service.submit(new TradeCancelThread(
//						current_gateway, outTradeNo));
				
				break;
			default:
				break;
			}
		};
	};

	
//	class TradeCancelForVipCardThread  implements Runnable {
//
//		String outTradeNo = "";
//		String price = "";
//		String cardNumber = "";
//		String phoneNumber = "";
//		public TradeCancelForVipCardThread(final String tradeNo,final String prs,String cardNum,String phone) {
//			this.outTradeNo = tradeNo;
//			this.price = prs;
//			this.cardNumber = cardNum;
//			this.phoneNumber = phone;
//		}
//
//		@Override
//		public void run() {
//			WSUtil.getInstance().cardRefund(ColetApplication.getApp().getConfigFile().getFranchiseId(), 
//					SystemUtil.getIMEI(Activity_04_Pay.this), outTradeNo,3 );
//
//		}
//	}
	
	class TradeCancelThread implements Runnable {

		String outTradeNo = "";
		String gateWay = "";
		boolean payForVipCard = false;

		public TradeCancelThread(final String gw, final String tradeNo) {
			this.outTradeNo = tradeNo;
			this.gateWay = gw;
		}

		@Override
		public void run() {
			Log.d("ColetCoffee_S", "TradeCancel at Activity_04_pay");
			ColetApplication.getApp().logDebug("Activity_04_pay,TradeCancel");
			TradeServerUtil.getInstance().tradeCancel(
					ColetApplication.getApp().getConfigFile().getFranchiseId(),
					SystemUtil.getIMEI(Activity_04_Pay.this), this.gateWay,
					SystemUtil.getIMEI(Activity_04_Pay.this), this.outTradeNo,
					payForVipCard);

		}
	}

	class AlipayTradeQueryThread implements Runnable {

		String outTradeNo = "";
		String gateWay = "";
		boolean payForVipCard = false;

		public AlipayTradeQueryThread(final String gw, final String tradeNo) {
			this.outTradeNo = tradeNo;
			this.gateWay = gw;
		}

		@Override
		public void run() {
			quering = true;

			TradeResult ret2 = TradeServerUtil.getInstance().tradeQuery(
					ColetApplication.getApp().getConfigFile().getFranchiseId(),
					SystemUtil.getIMEI(Activity_04_Pay.this), this.gateWay,
					SystemUtil.getIMEI(Activity_04_Pay.this), this.outTradeNo,
					payForVipCard);

			if (ret2 == null || ret2.result == null) {

			} else {
				// Log.d("PayActivity:", "tradeGateWay=" + ret2.tradeGateWay +
				// "result=" + ret2.result);
				if (ret2.result.equals(CashlessConstants.RESULT_SUCCESS)) {

					handler.sendEmptyMessage(1001);// 取消另外的交易
					handler.sendEmptyMessage(7);
					handler.sendEmptyMessageDelayed(6, 500);
				} else if (ret2.result
						.equals(CashlessConstants.RESULT_WAIT_FOR_PAY)) {
					// 再次发出msg，循环更新
					handler.sendEmptyMessageDelayed(2, 1000);
				} else if (ret2.result.equals(CashlessConstants.RESULT_FAILED)) {

					handler.sendEmptyMessage(8);
					handler.sendEmptyMessageDelayed(5, 500);
				}
			}
			// }
			quering = false;
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_04_pay);
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onResume() {
		super.onResume();
		resetQRCode();

		cardNumber = "";
		cardPhone = "";
		outTradeNo = "";
		quering = false;
		doQuery = false;

		goodDetail = (LabelImageView) this.findViewById(R.id.good_detail);
		alipay_qrcode = (ImageView) this.findViewById(R.id.qrcode);
		circleloader = (CircleLoader) this.findViewById(R.id.circleloader1);
		circleloader.setMessage("");

		alipay_qrcode.setVisibility(View.GONE);
		circleloader.setVisibility(View.VISIBLE);

		pay_left_time = (TextView) this.findViewById(R.id.pay_left_time);
		button_cancel_trade = (ImageView) this
				.findViewById(R.id.button_cancel_trade);
		button_cancel_trade.setOnClickListener(this);
		button_cancel_trade.setOnTouchListener(this);

		button_alipay_trade = (ImageView) this.findViewById(R.id.alipay_trade);
		button_wechatpay_trade = (ImageView) this
				.findViewById(R.id.wechatpay_trade);
		button_card_trade = (ImageView) this.findViewById(R.id.card_trade);
		label_switch_pay_method = (TextView) this
				.findViewById(R.id.label_switch_pay_method);

		button_alipay_trade.setOnTouchListener(this);
		button_wechatpay_trade.setOnTouchListener(this);
		button_card_trade.setOnTouchListener(this);
		button_alipay_trade.setOnClickListener(this);
		button_wechatpay_trade.setOnClickListener(this);
		button_card_trade.setOnClickListener(this);

		ly_cancel_trade = (LinearLayout) this
				.findViewById(R.id.ly_cancel_trade);
		ly_alipay_trade = (LinearLayout) this
				.findViewById(R.id.ly_alipay_trade);
		ly_wechat_trade = (LinearLayout) this
				.findViewById(R.id.ly_wechatpay_trade);
		ly_card_trade = (LinearLayout) this.findViewById(R.id.ly_card_trade);

		Intent intent = this.getIntent();
		Bundle bundle = intent.getExtras();
		if (bundle != null) {
			subject = bundle.getString("subject");
			price = bundle.getString("price");
			coffeeType = bundle.getInt("coffeeType");
			coffeeImage = bundle.getString("imagePath");
			coffeeId = bundle.getInt("imageId");
			// needSuger = bundle.getInt("needSuger");
			// ColetApplication.machineTradeNo = SystemUtil.getMachineTradeNo();
			// createQRCode(subject, price,
			// CashlessConstants.TRADE_GATEWAY_ALIPAY,ColetApplication.machineTradeNo);
			// createQRCode(subject, price,
			// CashlessConstants.TRADE_GATEWAY_WECHAT,ColetApplication.machineTradeNo);
			if (null == coffeeImage || coffeeImage.length() == 0) {
				goodDetail.setBackgroundResource(coffeeId);
			} else {
				Bitmap bitmap = SystemUtil.getLoacalBitmap(coffeeImage);
				goodDetail.setImageBitmap(bitmap);
				goodDetail.setScaleType(ScaleType.FIT_CENTER);
			}
			goodDetail.setLabelText(price + "元");
		}

		handler.removeMessages(0);
		handler.sendEmptyMessageDelayed(0, 1000);// 秒倒计时
		handler.sendEmptyMessage(8888);// 模拟点击微信支付
	}

	public String createQRCode(final String name, final String price,
			final String gateWay, String machineTradeNo) {
		final String filePath = getFileRoot(Activity_04_Pay.this)
				+ File.separator + "qr_" + System.currentTimeMillis() + ".jpg";

		// 二维码图片较大时，生成图片、保存文件的时间可能较长，因此放在新线程中
		ThreadPool.service.submit(new CreateQRCode(gateWay, name, filePath,
				machineTradeNo));

		return filePath;
	}

	class CreateQRCode implements Runnable {

		private String gw = "";
		private String na = "";
		private String fp = "";
		private String mtn = "";
		boolean payForVipCard = false;

		public CreateQRCode(String gateway, String tradeName,
				String qrCodePath, String machineTradeNo) {
			this.gw = gateway;
			this.na = tradeName;
			this.fp = qrCodePath;
			Log.d("ColetCoffee_S", "输入的类型为:" + gateway);
			this.mtn = machineTradeNo;
			Log.d("ColetCoffee_S", "输入的订单号为:" + mtn);
		}

		private String qrCodeString = "";

		public CreateQRCode(String gateway, String qrStr) {
			this.qrCodeString = qrStr;
		}

		public String getQrCodeString() {
			return this.qrCodeString;
		}

		@Override
		public void run() {

			if (!this.qrCodeString.equals("")) {
				int logoId = 0;
				if (this.gw.equals(CashlessConstants.TRADE_GATEWAY_ALIPAY)) {
					logoId = R.drawable.qrcode_creating;
				} else if (this.gw
						.equals(CashlessConstants.TRADE_GATEWAY_WECHAT)) {
					logoId = R.drawable.qrcode_creating;
				}
				boolean success = QRCodeUtil.createQRImage(this.qrCodeString,
						275, 275,
						BitmapFactory.decodeResource(getResources(), logoId),
						fp);

				if (success) {
					// outTradeNo = ret.outTradeNo;
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							// if
							// (gw.equals(CashlessConstants.TRADE_GATEWAY_ALIPAY))
							// {
							circleloader.setVisibility(View.GONE);
							alipay_qrcode.setVisibility(View.VISIBLE);

							alipay_qrcode.setImageBitmap(BitmapFactory
									.decodeFile(fp));
							// } else if
							// (gw.equals(CashlessConstants.TRADE_GATEWAY_WECHAT))
							// {
							// wechatpay.setVisibility(View.GONE);
							// wechatpay_qrcode.setVisibility(View.VISIBLE);

							// wechatpay_qrcode.setImageBitmap(BitmapFactory
							// .decodeFile(fp));
						}

						// title_alipay.setEnabled(true);
						// }
					});
					handler.sendEmptyMessage(0);// 开启倒计时Timer
				}
			} else {
				TradeResult ret = null;
				ret = TradeServerUtil.getInstance().createQRCode(
						ColetApplication.getApp().getConfigFile()
								.getFranchiseId(),
						SystemUtil.getIMEI(Activity_04_Pay.this), this.gw,
						SystemUtil.getIMEI(Activity_04_Pay.this), this.na,
						price,// this.na, price,
						"0.00", mtn, payForVipCard);
				if (ret != null
						&& ret.result.equals(CashlessConstants.RESULT_SUCCESS)) {

					int logoId = 0;
					if (ret.tradeGateWay
							.equals(CashlessConstants.TRADE_GATEWAY_ALIPAY)) {
						logoId = R.drawable.alipay_logo;
					} else if (ret.tradeGateWay
							.equals(CashlessConstants.TRADE_GATEWAY_WECHAT)) {
						logoId = R.drawable.wechat_logo;
					}
					boolean success = QRCodeUtil.createQRImage(ret.qrCode, 275,
							275, BitmapFactory.decodeResource(getResources(),
									logoId), fp);

					if (success) {
						Log.d("ColetCoffee_S", "输出的订单号为:" + ret.outTradeNo);
						outTradeNo = ret.outTradeNo;
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								// if
								// (gw.equals(CashlessConstants.TRADE_GATEWAY_ALIPAY))
								// {
								circleloader.setVisibility(View.GONE);
								alipay_qrcode.setVisibility(View.VISIBLE);

								alipay_qrcode.setImageBitmap(BitmapFactory
										.decodeFile(fp));

								if (gw.equals(CashlessConstants.TRADE_GATEWAY_ALIPAY)) {
									Activity_04_Pay.this.alipayQRcodeFilePathString = fp;
								} else if (gw
										.equals(CashlessConstants.TRADE_GATEWAY_WECHAT)) {
									Activity_04_Pay.this.weChatpayQRCodeFilePathString = fp;
								}

								// } else if
								// (gw.equals(CashlessConstants.TRADE_GATEWAY_WECHAT))
								// {
								// wechatpay.setVisibility(View.GONE);
								// wechatpay_qrcode.setVisibility(View.VISIBLE);

								// wechatpay_qrcode.setImageBitmap(BitmapFactory
								// .decodeFile(fp));
								// }

								// title_alipay.setEnabled(true);
							}
						});
						handler.sendEmptyMessage(0);// 开启倒计时Timer
					}
				}
			}
		}
	}

	// 文件存储根目录
	private String getFileRoot(Context context) {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			File external = context.getExternalFilesDir(null);
			if (external != null) {
				return external.getAbsolutePath();
			}
		}

		return context.getFilesDir().getAbsolutePath();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.pay, menu);
		return true;
	}

	// @Override
	public boolean onTouch(View v, MotionEvent event) {
		return UiEffectUtil.onTouch(v, event);
	}

	private String alipayQRcodeFilePathString = "";
	private String weChatpayQRCodeFilePathString = "";

	private void resetQRCode() {
		outTradeNo = "";
		alipayQRcodeFilePathString = "";
		weChatpayQRCodeFilePathString = "";
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.button_cancel_trade:
			ly_cancel_trade.setBackgroundColor(Color.GRAY);
			handler.sendEmptyMessage(1);// 停止所有Timer
			// handler.sendEmptyMessage(9);// 取消交易

			Intent intent = new Intent();
			intent.setClass(Activity_04_Pay.this, Activity_02_Welcome.class);
			Activity_04_Pay.this.startActivity(intent);
			finish();
			break;
		case R.id.alipay_trade:
			circleloader.setVisibility(View.VISIBLE);
			alipay_qrcode.setVisibility(View.GONE);

			ly_alipay_trade.setBackgroundColor(Color.GRAY);
			ly_wechat_trade.setBackgroundColor(Color.WHITE);
			ly_card_trade.setBackgroundColor(Color.WHITE);
			label_switch_pay_method.setText("请用支付宝扫码支付");

			if (alipayQRcodeFilePathString.equals("") == true) {
				if (outTradeNo.equals("")) {
					ColetApplication.machineTradeNo = SystemUtil
							.getMachineTradeNo() + SystemUtil.getIMEI(this);// ColetApplication.getApp().getConfigFile().getFranchiseId()+SystemUtil.getIMEI(this)+
					this.alipayQRcodeFilePathString = createQRCode(subject,
							price, CashlessConstants.TRADE_GATEWAY_ALIPAY,
							ColetApplication.machineTradeNo);
				} else {
					this.alipayQRcodeFilePathString = createQRCode(subject,
							price, CashlessConstants.TRADE_GATEWAY_ALIPAY,
							ColetApplication.machineTradeNo);
				}
			} else {
				circleloader.setVisibility(View.GONE);
				alipay_qrcode.setVisibility(View.VISIBLE);

				alipay_qrcode.setImageBitmap(BitmapFactory
						.decodeFile(this.alipayQRcodeFilePathString));
			}

			doQuery = true;
			current_gateway = CashlessConstants.TRADE_GATEWAY_ALIPAY;
			break;
		case R.id.wechatpay_trade:
			circleloader.setVisibility(View.VISIBLE);
			alipay_qrcode.setVisibility(View.GONE);

			ly_alipay_trade.setBackgroundColor(Color.WHITE);
			ly_wechat_trade.setBackgroundColor(Color.GRAY);
			ly_card_trade.setBackgroundColor(Color.WHITE);
			label_switch_pay_method.setText("请用微信扫一扫支付");

			if (weChatpayQRCodeFilePathString.equals("") == true) {
				if (outTradeNo.equals("")) {
					ColetApplication.machineTradeNo = SystemUtil
							.getMachineTradeNo() + SystemUtil.getIMEI(this);// ColetApplication.getApp().getConfigFile().getFranchiseId()++SystemUtil.getIMEI(this)
					this.weChatpayQRCodeFilePathString = createQRCode(subject,
							price, CashlessConstants.TRADE_GATEWAY_WECHAT,
							ColetApplication.machineTradeNo);
				} else {
					this.weChatpayQRCodeFilePathString = createQRCode(subject,
							price, CashlessConstants.TRADE_GATEWAY_WECHAT,
							ColetApplication.machineTradeNo);
				}
			} else {
				circleloader.setVisibility(View.GONE);
				alipay_qrcode.setVisibility(View.VISIBLE);
				alipay_qrcode.setImageBitmap(BitmapFactory
						.decodeFile(this.weChatpayQRCodeFilePathString));
			}

			doQuery = true;
			current_gateway = CashlessConstants.TRADE_GATEWAY_WECHAT;
			break;
		case R.id.card_trade:

			doQuery = false;
			circleloader.setVisibility(View.VISIBLE);
			alipay_qrcode.setVisibility(View.GONE);
			ly_alipay_trade.setBackgroundColor(Color.WHITE);
			ly_wechat_trade.setBackgroundColor(Color.WHITE);
			ly_card_trade.setBackgroundColor(Color.GRAY);
			label_switch_pay_method.setText("请用储值卡进行支付");

			
			dialog = new Activity_vipcard_pay.Builder(
					Activity_04_Pay.this).SetOnClickListener(new OnClickListener (){

						@Override
						public void onClick(View v) {
							String phone = dialog.getInputNumber();
							String fId = ColetApplication.getApp().getConfigFile().getFranchiseId();
							String tCode = SystemUtil.getIMEI(Activity_04_Pay.this);
					
								QueryCard cardInfo = null;
					
								int checkWithPhoneNumber = 0;// 0 无,1手机号,2会员卡号
					
								if (phone.equals("")) {
									Toast.makeText(Activity_04_Pay.this, "请输入会员卡号或者手机号",
											Toast.LENGTH_SHORT).show();
									return;
								} else if (phone.length() > 0 && phone.length() < 6) {
									Toast.makeText(Activity_04_Pay.this, "会员卡号长度不正确",
											Toast.LENGTH_SHORT).show();
									return;
								} else if ((phone.length() > 6 && phone.length() < 11)
										|| phone.length() > 11) {
									Toast.makeText(Activity_04_Pay.this, "会员卡号或者手机号长度不正确",
											Toast.LENGTH_SHORT).show();
									return;
								} else if (phone.length() == 6) {// 会员卡号
									checkWithPhoneNumber = 2;
								} else if (phone.length() == 11) {
									if ((SystemUtil.isMobileNO(phone) == false)) {
										Toast.makeText(Activity_04_Pay.this, "不是有效的手机号码",
												Toast.LENGTH_SHORT).show();
										return;
									} else {
										checkWithPhoneNumber = 1;
									}
								}
					
								{// 不管老客户还是新客户
					
									if ( checkWithPhoneNumber == 1) {// 手机号验证
										cardInfo = WSUtil.getInstance().card(fId, tCode, phone, "");
									} else if (checkWithPhoneNumber == 2) {// 会员卡号验证
										cardInfo = WSUtil.getInstance().card(fId, tCode, "", phone);
									} else {
										return;
									}
									if (cardInfo == null) {
										Toast.makeText(Activity_04_Pay.this, "似乎有点小问题，请再试一遍",
												Toast.LENGTH_SHORT).show();
										return;
									} else if (cardInfo.getStatus() == -1) {
										Toast.makeText(Activity_04_Pay.this, cardInfo.getMessage(),
												Toast.LENGTH_SHORT).show();
										return;
									} else if (cardInfo.getStatus() == 0) {
										
										cardNumber = cardInfo.getData().getId();
										cardPhone = cardInfo.getData().getPhone();
										
										// 验证余额是否足够
										String p = Integer.valueOf(((Float)(Float.valueOf(price) * 100)).intValue()).toString();
										if( Integer.valueOf(cardInfo.getData().getRemain()) < Integer.valueOf(p)){
											Toast.makeText(Activity_04_Pay.this, "会员卡余额不足，请更换其他支付方式或者充值后再购买",
													Toast.LENGTH_SHORT).show();
											return;
										}else{
//											String p = Integer.valueOf((String.valueOf((Float.valueOf(price) * 100.00f)))).toString();
											ChargeCard ret = WSUtil.getInstance().cardConsume(ColetApplication.getApp().getConfigFile().getFranchiseId(), 
													SystemUtil.getIMEI(Activity_04_Pay.this), p, "3", outTradeNo, cardInfo.getData().getId(), cardInfo.getData().getPhone());
											//String franchise,String terminal_code,String trade_no,String payType
											WSUtil.getInstance().updateTradeStatusPayed(ColetApplication.getApp().getConfigFile().getFranchiseId(), 
													SystemUtil.getIMEI(Activity_04_Pay.this),outTradeNo,"3");
											
											//ThreadPool.service.submit(new AlipayTradeQueryThread(CashlessConstants.TRADE_GATEWAY_VIPCARD, outTradeNo));
											if( ret.getStatus() == 0 ){
												//if(handler != null){
													//dialog.dismiss();
													handler.sendEmptyMessage(1001);//取消另外的交易
													handler.sendEmptyMessage(7);
													handler.sendEmptyMessageDelayed(6, 500);
													
													current_gateway = CashlessConstants.TRADE_GATEWAY_VIPCARD;
													//return;
												//}
											}else{
												Toast.makeText(Activity_04_Pay.this, ret.getMessage(),
														Toast.LENGTH_SHORT).show();
												return;
											}
											
										}
									}
								}
						}
						
					}).create();
			dialog.setPrice(price);

			if (outTradeNo.equals("")) {
				ColetApplication.machineTradeNo = SystemUtil
						.getMachineTradeNo() + SystemUtil.getIMEI(this);// ColetApplication.getApp().getConfigFile().getFranchiseId()++SystemUtil.getIMEI(this)
				createQRCode(subject, price,
						CashlessConstants.TRADE_GATEWAY_VIPCARD,
						ColetApplication.machineTradeNo);
			} else {
				createQRCode(subject, price,
						CashlessConstants.TRADE_GATEWAY_VIPCARD,
						ColetApplication.machineTradeNo);
			}

			dialog.setOutTradeNo(outTradeNo);
			dialog.setCallBackHandler(handler);

			dialog.show();

			// Toast.makeText(Activity_04_Pay.this, "该功能还未开发",
			// Toast.LENGTH_LONG).show();
			// AlertDialog.Builder builder = new AlertDialog.Builder(
			// Activity_04_Pay.this);
			// builder.setTitle("该功能还未开发").setPositiveButton("确定",
			// new DialogInterface.OnClickListener() {
			// public void onClick(DialogInterface dialog,
			// int which) {
			// }
			// });
			// builder.show();
			break;
		// case R.id.title_alipay:
		//
		// if( current_gateway.equals(CashlessConstants.TRADE_GATEWAY_ALIPAY)){
		// current_gateway = CashlessConstants.TRADE_GATEWAY_WECHAT;
		// }else if(
		// current_gateway.equals(CashlessConstants.TRADE_GATEWAY_WECHAT)){
		// current_gateway = CashlessConstants.TRADE_GATEWAY_ALIPAY;
		// }
		// handler.sendEmptyMessage(12);// 切换二维码
		// break;
		default:
			break;

		}
	}
}
