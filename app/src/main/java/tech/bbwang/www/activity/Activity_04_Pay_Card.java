package tech.bbwang.www.activity;

import java.io.File;

import tech.bbwang.www.R;
import tech.bbwang.www.util.CashlessConstants;
import tech.bbwang.www.util.QRCodeUtil;
import tech.bbwang.www.util.SystemUtil;
import tech.bbwang.www.util.ThreadPool;
import tech.bbwang.www.util.TradeServerUtil;
import tech.bbwang.www.util.UiEffectUtil;
import tech.bbwang.www.ws.WSUtil;
import android.content.Context;
import android.content.Intent;
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
import cn.colet.result.TradeResult;

import com.kanyuan.circleloader.CircleLoader;

public class Activity_04_Pay_Card extends SuperActivity implements
		OnClickListener, OnTouchListener {
	private ImageView alipay_qrcode = null;
	private TextView pay_left_time = null;
	private ImageView button_cancel_trade = null;

	private CircleLoader circleloader = null;
	private ImageView button_alipay_trade = null;
	private ImageView button_wechatpay_trade = null;
//	private ImageView button_card_trade = null;

	LinearLayout ly_cancel_trade = null;
	LinearLayout ly_alipay_trade = null;
	LinearLayout ly_wechat_trade = null;

	private TextView cardAppendMoney = null;
	private TextView cardRealPayMoney = null;
//	private TextView cardNumber = null;
	private TextView cardPhone = null;
	private TextView cardRemain = null;
	private TextView label_switch_pay_method = null;

	// 当前正在进行的交易号
	private static String outTradeNo = "";
	private long curSec = CashlessConstants.COUNT_TIME;

	private boolean quering = false;
	private boolean doQuery = false;
	private static String current_gateway = CashlessConstants.TRADE_GATEWAY_ALIPAY;
	
	private static String listPrice = "";
	private static String realPrice = "";
	private static String cardNum = "";
	private static String cardPho = "";
	private static String cardRem = "";

	public Handler handler = new Handler(Looper.getMainLooper()) {
		public void handleMessage(android.os.Message msg) {
			Intent intent = new Intent();
			Bundle data = new Bundle();
			switch (msg.what) {
			case 8888:// 模拟点击微信支付
				handler.removeMessages(8888);
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
			case 11:// 接受咖啡机错误,并处理
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
					intent.setClass(Activity_04_Pay_Card.this,
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
			case 5:// 执行退款操作
				ThreadPool.service.submit(new TradeCancelThread(
						CashlessConstants.TRADE_GATEWAY_ALIPAY, outTradeNo));
				ThreadPool.service.submit(new TradeCancelThread(
						CashlessConstants.TRADE_GATEWAY_WECHAT, outTradeNo));
				intent.setClass(Activity_04_Pay_Card.this,
						Activity_09_Error.class);
				handler.sendEmptyMessage(1);// 停止计时Timer
				handler.sendEmptyMessage(3);// 停止查询Timer
				handler.sendEmptyMessage(10);// 取消交易

				data.clear();
				data.putString("errorFlag", "payTimeOut");
				intent.putExtras(data);
				startActivity(intent);
				finish();
				break;
//			case 6:// 支付成功执行相关操作
				//data.clear();
				//data.putString("outTradeNo", outTradeNo);
				//intent.putExtras(data);
				// TODO
				//intent.setClass(Activity_04_Pay_Card.this,
				//		Activity_06_MakeCoffee.class);
				//Activity_04_Pay_Card.this.startActivity(intent);
				//finish();
//				cardRem = String.valueOf(Integer.valueOf(cardRem)+Integer.valueOf(realPrice));
//				cardRemain.setText("余额:"
//						+ SystemUtil.txfloat(Float.valueOf(cardRem) / 100.00f));
//				break;
			case 1:// 中止
					// 直接移除，定时器停止
				handler.removeMessages(0);
				handler.removeMessages(2);// 清空查询Timer
				curSec = CashlessConstants.COUNT_TIME;
				break;
			case 2:// 查询支付结果
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
			case 7://支付成功
				
//				String price =  String.valueOf(SystemUtil.txfloat(Float.valueOf(realPrice) / 100.00f));
				String payType = "0";// 1支付宝、2 微信
				if (current_gateway
						.equals(CashlessConstants.TRADE_GATEWAY_ALIPAY)) {
					payType = "1";
				} else if (current_gateway
						.equals(CashlessConstants.TRADE_GATEWAY_WECHAT)) {
					payType = "2";
				}
				
				WSUtil.getInstance().cardCharge(ColetApplication.getApp().getConfigFile().getFranchiseId(), 
						SystemUtil.getIMEI(Activity_04_Pay_Card.this), listPrice , payType, outTradeNo, cardNum, cardPho);
				button_cancel_trade.setVisibility(View.INVISIBLE);
				label_switch_pay_method.setText(R.string.message_pay_success);
				handler.sendEmptyMessage(1);
				cardRem = String.valueOf(Integer.valueOf(cardRem)+Integer.valueOf(listPrice));
				cardRemain.setText("余额:  "
						+ SystemUtil.txfloat(Float.valueOf(cardRem) / 100.00f));
				
				handler.sendEmptyMessageDelayed(1002, 1000*3);//三秒后迁移至欢迎画面，完成购买流程
				break;
			case 8:
				label_switch_pay_method.setText(R.string.message_pay_failed);
				handler.sendEmptyMessage(1);
				break;
			case 9:// 取消交易并退回菜单画面
				ThreadPool.service.submit(new TradeCancelThread(
						CashlessConstants.TRADE_GATEWAY_ALIPAY, outTradeNo));
				ThreadPool.service.submit(new TradeCancelThread(
						CashlessConstants.TRADE_GATEWAY_WECHAT, outTradeNo));
				intent.setClass(Activity_04_Pay_Card.this,
						Activity_10_vipcard2.class);
				Activity_04_Pay_Card.this.startActivity(intent);
				finish();
				break;
			case 10:// 取消交易
				ThreadPool.service.submit(new TradeCancelThread(
						CashlessConstants.TRADE_GATEWAY_ALIPAY, outTradeNo));
				ThreadPool.service.submit(new TradeCancelThread(
						CashlessConstants.TRADE_GATEWAY_WECHAT, outTradeNo));
				break;
			case 1001:// 有条件取消交易
				if (current_gateway
						.equals(CashlessConstants.TRADE_GATEWAY_ALIPAY)) {
					current_gateway = CashlessConstants.TRADE_GATEWAY_WECHAT;
				} else if (current_gateway
						.equals(CashlessConstants.TRADE_GATEWAY_WECHAT)) {
					current_gateway = CashlessConstants.TRADE_GATEWAY_ALIPAY;
				}
				ThreadPool.service.submit(new TradeCancelThread(
						current_gateway, outTradeNo));
				break;
			case 1002:// 支付成功信息显示后迁移画面之欢迎画面
				intent.setClass(Activity_04_Pay_Card.this,
						Activity_02_Welcome.class);
				Activity_04_Pay_Card.this.startActivity(intent);
				finish();
				break;
			default:
				break;
			}
		};
	};

	class TradeCancelThread implements Runnable {

		String outTradeNo = "";
		String gateWay = "";
		boolean payForVipCard = true;
		
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
					SystemUtil.getIMEI(Activity_04_Pay_Card.this),
					this.gateWay,
					SystemUtil.getIMEI(Activity_04_Pay_Card.this),
					this.outTradeNo,payForVipCard);

		}
	}

	class AlipayTradeQueryThread implements Runnable {

		String outTradeNo = "";
		String gateWay = "";
		boolean payForVipCard = true;
		
		public AlipayTradeQueryThread(final String gw, final String tradeNo) {
			this.outTradeNo = tradeNo;
			this.gateWay = gw;
		}

		@Override
		public void run() {
			quering = true;

			TradeResult ret2 = TradeServerUtil.getInstance().tradeQuery(
					ColetApplication.getApp().getConfigFile().getFranchiseId(),
					SystemUtil.getIMEI(Activity_04_Pay_Card.this),
					this.gateWay,
					SystemUtil.getIMEI(Activity_04_Pay_Card.this),
					this.outTradeNo,payForVipCard);

			if (ret2 == null || ret2.result == null) {

			} else {
				if (ret2.result.equals(CashlessConstants.RESULT_SUCCESS)) {

					handler.sendEmptyMessage(1001);// 取消另外的交易
					handler.sendEmptyMessage(7);
					handler.sendEmptyMessageDelayed(6, 1000);
				} else if (ret2.result
						.equals(CashlessConstants.RESULT_WAIT_FOR_PAY)) {
					// 再次发出msg，循环更新
					handler.sendEmptyMessageDelayed(2, 1500);
				} else if (ret2.result.equals(CashlessConstants.RESULT_FAILED)) {

					handler.sendEmptyMessage(8);
					handler.sendEmptyMessageDelayed(5, 1000);
				}
			}
			quering = false;
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_04_pay_card);
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onResume() {
		super.onResume();
		resetQRCode();

		listPrice = "";//分单位
		realPrice = "";//分单位
		cardNum = "";
		cardPho = "";
		cardRem = "";

		outTradeNo = "";
		quering = false;
		doQuery = false;

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
//		button_card_trade = (ImageView) this.findViewById(R.id.card_trade);
		label_switch_pay_method = (TextView) this
				.findViewById(R.id.label_switch_pay_method);

		button_alipay_trade.setOnTouchListener(this);
		button_wechatpay_trade.setOnTouchListener(this);
//		button_card_trade.setOnTouchListener(this);
		button_alipay_trade.setOnClickListener(this);
		button_wechatpay_trade.setOnClickListener(this);
//		button_card_trade.setOnClickListener(this);

		ly_cancel_trade = (LinearLayout) this
				.findViewById(R.id.ly_cancel_trade);
		ly_alipay_trade = (LinearLayout) this
				.findViewById(R.id.ly_alipay_trade);
		ly_wechat_trade = (LinearLayout) this
				.findViewById(R.id.ly_wechatpay_trade);

		cardAppendMoney = (TextView) this.findViewById(R.id.cardAppendMoney);
		cardRealPayMoney = (TextView) this.findViewById(R.id.cardRealPayMoney);
//		cardNumber = (TextView) this.findViewById(R.id.cardNumber);
		cardPhone = (TextView) this.findViewById(R.id.cardPhone);
		cardRemain = (TextView) this.findViewById(R.id.cardRemain);

		Intent intent = this.getIntent();
		Bundle bundle = intent.getExtras();
		if (bundle != null) {
			listPrice = bundle.getString("listPrice");
			realPrice = bundle.getString("realPrice");
			cardNum = bundle.getString("cardNumber");
			cardPho = bundle.getString("cardPhone");
			cardRem = bundle.getString("cardRemain");

			cardAppendMoney.setText("充值金额: " + SystemUtil.txfloat(Float.valueOf(listPrice) / 100.00f));
//			if (firstCharge.equals("0")) {// 首充
//				if (freshPrice.length() > 0) {// 有新人价
//					fee = freshPrice;
//				} else if (vipPrice.length() > 0) {
//					fee = vipPrice;
//				} else {
//					fee = listPrice;
//				}
//			} else {// 非首充
//				if (vipPrice.length() > 0) {
//					fee = vipPrice;
//				} else {
//					fee = listPrice;
//				}
//			}
			cardRealPayMoney.setText("实付金额: "
					+ SystemUtil.txfloat(Float.valueOf(realPrice) / 100.00f));
//			cardNumber.setText("卡号: " + cardNum);
			String tmp = "";
			if( null != cardPho && cardPho.length()==11){
				tmp = cardPho.substring(0, 3)+"****"+cardPho.substring(7, 11);
			}else{
				tmp = (cardPho==null)?"":cardPho;;
			}
			cardPhone.setText("手机: " + tmp);
			// TODO 要把分转换成元
			cardRemain.setText("余额: "
					+ SystemUtil.txfloat(Float.valueOf(cardRem) / 100.00f));
		}

		handler.removeMessages(0);
		handler.sendEmptyMessageDelayed(0, 1000);
		handler.sendEmptyMessage(8888);
	}

	public String createQRCode(final String name, final String price,
			final String gateWay, String machineTradeNo) {
		final String filePath = getFileRoot(Activity_04_Pay_Card.this)
				+ File.separator + "qr_" + System.currentTimeMillis() + ".jpg";

		// 二维码图片较大时，生成图片、保存文件的时间可能较长，因此放在新线程中
		ThreadPool.service.submit(new CreateQRCode(gateWay, name, filePath,
				machineTradeNo, SystemUtil.txfloat(Float.valueOf(realPrice) / 100.00f)));

		return filePath;
	}

	class CreateQRCode implements Runnable {

		private String gw = "";
		private String na = "";
		private String fp = "";
		private String mtn = "";
		private String price = "";
		boolean payForVipCard = true;
		
		public CreateQRCode(String gateway, String tradeName,
				String qrCodePath, String machineTradeNo, String pri) {
			this.gw = gateway;
			this.na = tradeName;
			this.fp = qrCodePath;
			this.price = pri;
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
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							circleloader.setVisibility(View.GONE);
							alipay_qrcode.setVisibility(View.VISIBLE);

							alipay_qrcode.setImageBitmap(BitmapFactory
									.decodeFile(fp));
						}
					});
					handler.sendEmptyMessage(0);// 开启倒计时Timer
				}
			} else {
				TradeResult ret = null;
				ret = TradeServerUtil.getInstance().createQRCode(
						ColetApplication.getApp().getConfigFile()
								.getFranchiseId(),
						SystemUtil.getIMEI(Activity_04_Pay_Card.this), this.gw,
						SystemUtil.getIMEI(Activity_04_Pay_Card.this), this.na,
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
									Activity_04_Pay_Card.this.alipayQRcodeFilePathString = fp;
								} else if (gw
										.equals(CashlessConstants.TRADE_GATEWAY_WECHAT)) {
									Activity_04_Pay_Card.this.weChatpayQRCodeFilePathString = fp;
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
		alipayQRcodeFilePathString = "";
		weChatpayQRCodeFilePathString = "";
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.button_cancel_trade:
			ly_cancel_trade.setBackgroundColor(Color.GRAY);
			handler.sendEmptyMessage(1);// 停止所有Timer
			handler.sendEmptyMessage(9);// 取消交易
			break;
		case R.id.alipay_trade:
			circleloader.setVisibility(View.VISIBLE);
			alipay_qrcode.setVisibility(View.GONE);

			ly_alipay_trade.setBackgroundColor(Color.GRAY);
			ly_wechat_trade.setBackgroundColor(Color.WHITE);
			// ly_card_trade.setBackgroundColor(Color.WHITE);
			label_switch_pay_method.setText("请用支付宝扫码支付");

			if (alipayQRcodeFilePathString.equals("") == true) {
				if (outTradeNo.equals("")) {
					ColetApplication.machineTradeNo = SystemUtil
							.getMachineTradeNo() + SystemUtil.getIMEI(this);// ColetApplication.getApp().getConfigFile().getFranchiseId()+SystemUtil.getIMEI(this)+
					this.alipayQRcodeFilePathString = createQRCode("储值卡充值",
							realPrice, CashlessConstants.TRADE_GATEWAY_ALIPAY,
							ColetApplication.machineTradeNo);
				} else {
					this.alipayQRcodeFilePathString = createQRCode("储值卡充值",
							realPrice, CashlessConstants.TRADE_GATEWAY_ALIPAY,
							ColetApplication.machineTradeNo);
				}
			} else {
				circleloader.setVisibility(View.GONE);
				alipay_qrcode.setVisibility(View.VISIBLE);

				alipay_qrcode.setImageBitmap(BitmapFactory
						.decodeFile(this.alipayQRcodeFilePathString));
			}

			doQuery = true;
			break;
		case R.id.wechatpay_trade:
			circleloader.setVisibility(View.VISIBLE);
			alipay_qrcode.setVisibility(View.GONE);

			ly_alipay_trade.setBackgroundColor(Color.WHITE);
			ly_wechat_trade.setBackgroundColor(Color.GRAY);
			label_switch_pay_method.setText("请用微信扫一扫支付");

			if (weChatpayQRCodeFilePathString.equals("") == true) {
				if (outTradeNo.equals("")) {
					ColetApplication.machineTradeNo = SystemUtil
							.getMachineTradeNo() + SystemUtil.getIMEI(this); // ColetApplication.getApp().getConfigFile().getFranchiseId()++SystemUtil.getIMEI(this)
					this.weChatpayQRCodeFilePathString = createQRCode("储值卡充值",
							realPrice, CashlessConstants.TRADE_GATEWAY_WECHAT,
							ColetApplication.machineTradeNo);
				} else {
					this.weChatpayQRCodeFilePathString = createQRCode("储值卡充值",
							realPrice, CashlessConstants.TRADE_GATEWAY_WECHAT,
							ColetApplication.machineTradeNo);
				}
			} else {
				circleloader.setVisibility(View.GONE);
				alipay_qrcode.setVisibility(View.VISIBLE);
				alipay_qrcode.setImageBitmap(BitmapFactory
						.decodeFile(this.weChatpayQRCodeFilePathString));
			}

			doQuery = true;
			break;

		default:
			break;

		}
	}
}
