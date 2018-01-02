package tech.bbwang.www.activity;

import tech.bbwang.www.R;
import tech.bbwang.www.util.CashlessConstants;
import tech.bbwang.www.util.SystemUtil;
import tech.bbwang.www.util.ThreadPool;
import tech.bbwang.www.util.TradeServerUtil;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class Activity_06_MakeCoffee extends SuperActivity {

	private static int coffeeType = CashlessConstants.COFFEE_TYPE_CAPPUCCION;
	private static int coffeeSize = CashlessConstants.CUP_SIZE_SMALL;
	private static int needSuger = CashlessConstants.WANT_SUGER_NO;
	private static TextView message = null;
	private static ImageView coffee_make_progress_image = null;
	private static String outTradeNo = "";
	private static Activity_06_MakeCoffee myself = null;
//	private static String cardNumber = "";
//	private static String cardPhone ="";
	private static String current_gateway = "";
//	private static String price = "";
	
//	private static boolean isRefundForVipCard = false;
	//10分钟咖啡最大制作时间
//	private static final int MAX_COFFEE_MAKING_TIME = 60 * 5;
//	private static int coffee_marking_count = 0;
//	private static boolean isMakeCoffeeSuccess = false;
	public static Handler makecoffeeHandler = new Handler(Looper.getMainLooper()) {
	
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			switch (msg.what) {
			case 1110://退款
				//if( isRefundForVipCard == false ){
					//isRefundForVipCard = true;
					//ThreadPool.service.submit(myself.new TradeCancelThread2(CashlessConstants.TRADE_GATEWAY_VIPCARD,outTradeNo));
				//}else	
				 ColetApplication.getApp().stopSendCoffeeStatus();
				if (current_gateway.equals(CashlessConstants.TRADE_GATEWAY_ALIPAY)) {
					//先关微信，再退支付宝
					ThreadPool.service.submit(myself.new TradeCancelThread2(CashlessConstants.TRADE_GATEWAY_WECHAT,outTradeNo));
					ThreadPool.service.submit(myself.new TradeCancelThread2(CashlessConstants.TRADE_GATEWAY_ALIPAY,outTradeNo));
				} else if (current_gateway.equals(CashlessConstants.TRADE_GATEWAY_WECHAT)) {
					ThreadPool.service.submit(myself.new TradeCancelThread2(CashlessConstants.TRADE_GATEWAY_ALIPAY,outTradeNo));
					ThreadPool.service.submit(myself.new TradeCancelThread2(CashlessConstants.TRADE_GATEWAY_WECHAT,outTradeNo));
				}else if(current_gateway.equals(CashlessConstants.TRADE_GATEWAY_VIPCARD)){
					ThreadPool.service.submit(myself.new TradeCancelThread2(CashlessConstants.TRADE_GATEWAY_VIPCARD,outTradeNo));
					ThreadPool.service.submit(myself.new TradeCancelThread2(CashlessConstants.TRADE_GATEWAY_ALIPAY,outTradeNo));
					ThreadPool.service.submit(myself.new TradeCancelThread2(CashlessConstants.TRADE_GATEWAY_WECHAT,outTradeNo));
				}
				
				myself.finish();
			case 110://提示放置杯子
				makecoffeeHandler.removeMessages(110);
				if( null != message ){
					if(message.getVisibility() == View.GONE){
						message.setVisibility(View.VISIBLE);
					}else if(message.getVisibility() == View.VISIBLE){
						message.setVisibility(View.GONE);
					}
					makecoffeeHandler.sendEmptyMessageDelayed(110, 600);
				}
				break;
//			case 99://5分钟做不完咖啡制作超时
//				if (isMakeCoffeeSuccess == true){
//					break;
//				}
//				coffee_marking_count++;
//				if( coffee_marking_count >= MAX_COFFEE_MAKING_TIME ){
//					ColetApplication.getApp().stopSendCoffeeStatus();// 停止受信
//					myself.new TradeCancelThread(outTradeNo).start();// 取消交易
//					Intent intent = new Intent();
//					Bundle data = new Bundle();
//					data.putString("errorFlag", "coffeeMakeTimeOut");
//					intent.putExtras(data);
//					intent.setClass(myself, Activity_09_Error.class);
//					myself.startActivity(intent);
//				}else{
//					Message newMsg2 = new Message();
//					newMsg2.what = 99;
//					makecoffeeHandler.sendMessageDelayed(newMsg2, 1000);
//				}
//				break;
			case 0://UI transfer
				Intent intent1 = new Intent();
				intent1.setClass(myself, Activity_07_Thanks.class);
				myself.startActivity(intent1);
				myself.finish();
				break;
			case 1:// 咖啡制作进度
//				int progress = 0;
//				progress = msg.getData().getInt("progress");
//
//				if (progress == 100) {
//					coffee_make_progress_image
//							.setImageResource(R.drawable.cup100);
//					makecoffeeHandler.sendEmptyMessageDelayed(0, 3000);
//				} else if (progress == 90) {
//					coffee_make_progress_image
//							.setImageResource(R.drawable.cup90);
//					ColetApplication.getApp().stopSendCoffeeStatus();
//					Message newMsg = new Message();
//					Bundle data = new Bundle();
//					data.putInt("progress", 100);
//					newMsg.setData(data);
//					newMsg.what = 1;
//					makecoffeeHandler.sendMessageDelayed(newMsg, 500);
//				} else if (progress >= 80) {
//					coffee_make_progress_image
//							.setImageResource(R.drawable.cup80);
//				} else if (progress >= 70) {
//					coffee_make_progress_image
//							.setImageResource(R.drawable.cup70);
//				} else if (progress >= 60) {
//					coffee_make_progress_image
//							.setImageResource(R.drawable.cup60);
//				} else if (progress == 50) {
//					coffee_make_progress_image
//							.setImageResource(R.drawable.cup50);
//				} else if (progress == 40) {
//					coffee_make_progress_image
//							.setImageResource(R.drawable.cup40);
//				} else if (progress == 30) {
//					coffee_make_progress_image
//							.setImageResource(R.drawable.cup30);
//				} else if (progress == 20) {
//					coffee_make_progress_image
//							.setImageResource(R.drawable.cup20);
//				} else if (progress == 10) {
//					coffee_make_progress_image
//							.setImageResource(R.drawable.cup10);
//				} else if (progress == 0) {
//					coffee_make_progress_image
//							.setImageResource(R.drawable.cup30);
//				}
				
				int progress = 0;
                progress = msg.getData().getInt("progress");
                if (progress >= 90) {
                    coffee_make_progress_image.setImageResource(R.drawable.cup100);
                    ColetApplication.getApp().stopSendCoffeeStatus();
//                    NewCoffeeApplication.first = false;
                  makecoffeeHandler.sendEmptyMessageDelayed(2,3000);
                } else if (progress >= 80) {
                    coffee_make_progress_image.setBackgroundResource(R.drawable.cup90);
                } else if (progress >= 70) {
                    coffee_make_progress_image.setBackgroundResource(R.drawable.cup80);
                } else if (progress >= 60) {
                    coffee_make_progress_image.setBackgroundResource(R.drawable.cup70);
                } else if (progress >= 50) {
                    coffee_make_progress_image.setBackgroundResource(R.drawable.cup60);
                } else if (progress >= 40) {
                    coffee_make_progress_image.setBackgroundResource(R.drawable.cup50);
                } else if (progress >= 30) {
                    coffee_make_progress_image.setBackgroundResource(R.drawable.cup40);
                } else if (progress >= 20) {
                    coffee_make_progress_image.setBackgroundResource(R.drawable.cup30);
                } else if (progress >= 10) {
                    coffee_make_progress_image.setBackgroundResource(R.drawable.cup20);
                } else {
                    coffee_make_progress_image.setBackgroundResource(R.drawable.cup10);
                }
				break;
            case 2:
//            	isMakeCoffeeSuccess = true;
                Intent intent = new Intent();
                intent.setClass(myself, Activity_07_Thanks.class);
                myself.startActivity(intent);
                myself.finish();
                break;
//			case 2:// 故障
//				int error = msg.getData().getInt("error");
//				String errorMsg = "";
//				boolean isContinue = true;// 是否继续可以制作咖啡
//				switch (error) {
//				case 0x01:
//					errorMsg = "门1缺失";
//					break;
//				case 0x02:
//					errorMsg = "门2缺失";
//					break;
//				case 0x03:
//					errorMsg = "门3缺失";
//					break;
//				case 0x04:
//					errorMsg = "门4缺失";
//					break;
//				case 0x05:
//					errorMsg = "缺水";
//					isContinue = false;
//					break;
//				case 0x06:
//					errorMsg = "出冰异常";
//					break;
//				case 0x07:
//					errorMsg = "冰桶异常";
//					break;
//				case 0x08:
//					errorMsg = "机器管路阻塞";
//					isContinue = false;
//					break;
//				case 0x09:
//					errorMsg = "缺豆";
//					break;
//				case 0x0A:
//					errorMsg = "冲泡器缺失";
//					isContinue = false;
//					break;
//				case 0x0B:
//					errorMsg = "冲泡器阻塞";
//					break;
//				case 0x0C:
//					errorMsg = "冰桶板未连接";
//					isContinue = false;
//					break;
//				default:
//					break;
//				}
//
//				if (isContinue == false) {
//					ColetApplication.getApp().stopSendCoffeeStatus();// 停止受信
//					myself.new TradeCancelThread(outTradeNo).start();// 取消交易
//					Intent intent = new Intent();
//					Bundle data = new Bundle();
//					data.putString("errorFlag", "coffeeError");
//					data.putString("errorMsg", errorMsg);
//					intent.putExtras(data);
//					intent.setClass(myself, Activity_09_Error.class);
//					myself.startActivity(intent);
//				}
//
//				break;
			default:
				break;
			}

		}

	};

//	class TradeCancelForVipCardThread2  implements Runnable {
//
//		String outTradeNo = "";
//		String price = "";
//		String cardNumber = "";
//		String phoneNumber = "";
//		public TradeCancelForVipCardThread2(final String tradeNo,final String prs,String cardNum,String phone) {
//			this.outTradeNo = tradeNo;
//			this.price = prs;
//			this.cardNumber = cardNum;
//			this.phoneNumber = phone;
//		}
//
//		@Override
//		public void run() {
//			String p = String.valueOf((((Float)(Float.valueOf(price)*100f)).intValue()));
//			RefundCard ret = WSUtil.getInstance().cardRefund(ColetApplication.getApp().getConfigFile().getFranchiseId(), 
//					SystemUtil.getIMEI(Activity_06_MakeCoffee.this), outTradeNo, 3 );
//			
//			if( ret.getStatus() == 0){
//				isRefundForVipCard = true;
//			}
//
//		}
//	}
	
	class TradeCancelThread extends Thread {

		String outTradeNo = "";
		boolean payForVipCard = false;
		
		public TradeCancelThread(String otn) {
			outTradeNo = otn;
		}

		@Override
		public void run() {
			ColetApplication.getApp().logDebug("Activity_06_MakeCoffee,TradeCancel");
			TradeServerUtil.getInstance().tradeCancel(
					ColetApplication.getApp().getConfigFile().getFranchiseId(),
					SystemUtil.getIMEI(Activity_06_MakeCoffee.this),
					CashlessConstants.TRADE_GATEWAY_ALIPAY,
					SystemUtil.getIMEI(Activity_06_MakeCoffee.this),
					this.outTradeNo,payForVipCard);
			super.run();

		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_06_make_coffee);
		super.onCreate(savedInstanceState);
		message = (TextView) this
				.findViewById(R.id.message);
		coffee_make_progress_image = (ImageView) this
				.findViewById(R.id.coffee_make_progress_image);
		outTradeNo = this.getIntent().getExtras().getString("outTradeNo");
		coffeeType = this.getIntent().getExtras().getInt("coffeeType");
//		cardNumber = this.getIntent().getExtras().getString("cardNumber");
//		cardPhone = this.getIntent().getExtras().getString("cardPhone");
		current_gateway = this.getIntent().getExtras().getString("current_gateway");
//		price = this.getIntent().getExtras().getString("price");
//		coffeeSize = this.getIntent().getExtras().getInt("coffeeSize");
//		needSuger = this.getIntent().getExtras().getInt("needSuger");

		ColetApplication.currentOutTradeNo = outTradeNo;
		ColetApplication.currentGateway = current_gateway;
		
		ColetApplication.getApp().getUart()
				.sendCoffeeCommand(coffeeType, coffeeSize, needSuger);
		ColetApplication.getApp().startSendCoffeeStatus();
//		makecoffeeHandler.sendEmptyMessage(1110);
		
//		coffee_marking_count = 0;
//		Message newMsg = new Message();
//		newMsg.what = 99;
//		makecoffeeHandler.sendMessageDelayed(newMsg, 1000);
		makecoffeeHandler.sendEmptyMessageDelayed(110, 600);

		myself = this;
	}

//	@Override
//	protected void onResume() {
//		super.onResume();
//		isMakeCoffeeSuccess = false;
//	}



	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.make_coffee, menu);
		return true;
	}

	
	class TradeCancelThread2  implements Runnable {

		String outTradeNo = "";
		String gateWay = "";
		boolean payForVipCard = false;

		public TradeCancelThread2(final String gw,final String tradeNo) {
			this.outTradeNo = tradeNo;
			this.gateWay = gw;
		}

		@Override
		public void run() {
			Log.d("ColetCoffee_S", "TradeCancel at Activity_06_MakeCoffee");
			ColetApplication.getApp().logDebug("ColetCoffee_S,TradeCancel at makecoffee");
			TradeServerUtil.getInstance().tradeCancel(ColetApplication.getApp().getConfigFile().getFranchiseId(),SystemUtil.getIMEI(Activity_06_MakeCoffee.this),this.gateWay,
					SystemUtil.getIMEI(Activity_06_MakeCoffee.this), this.outTradeNo,payForVipCard);

		}
	}
}
