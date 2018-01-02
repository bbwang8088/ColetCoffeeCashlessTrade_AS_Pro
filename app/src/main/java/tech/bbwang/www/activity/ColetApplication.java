package tech.bbwang.www.activity;

import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import tech.bbwang.www.service.CommLogService;
import tech.bbwang.www.service.DialyTaskService;
import tech.bbwang.www.service.HeartBeatTaskService;
import tech.bbwang.www.sqlite.AdDAO;
import tech.bbwang.www.sqlite.DBAd;
import tech.bbwang.www.sqlite.DBMenu;
import tech.bbwang.www.sqlite.DatabaseHelper;
import tech.bbwang.www.sqlite.MenuDAO;
import tech.bbwang.www.sqlite.PrePayCardDAO;
import tech.bbwang.www.util.CashlessConstants;
import tech.bbwang.www.util.ColetCoffeeUtil;
import tech.bbwang.www.util.ConfigFileUtil;
import tech.bbwang.www.util.CrashHandler;
import tech.bbwang.www.util.DateUtil;
import tech.bbwang.www.util.LogUtil;
import tech.bbwang.www.util.SystemUtil;
import tech.bbwang.www.util.ThreadPool;
import tech.bbwang.www.ws.WSUtil;
import android.app.Activity;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
//import android.util.Log;

/**
 * 应用程序主类，用于保存全局存在的一些信息
 * 
 * @author bbwang8088@126.com
 * 
 */
public class ColetApplication extends Application {

	public static String currentOutTradeNo = "";
	public static String currentGateway = "";
	
	public static Map<String,Bitmap> menuImage = new HashMap<String,Bitmap>();
	public static final String LOG_TAG = "ColetCoffee";

	public static String machineTradeNo = "";
	ColetCoffeeUtil uart = null;

	private DatabaseHelper dbHelper = null;
	private SQLiteDatabase database = null;
	private AdDAO addao = null;
	private MenuDAO mdao = null;
	private PrePayCardDAO prePaycardDao = null;
	private LogUtil logger = null;
	private ConfigFileUtil configFile = null;

	private DialyTaskService dialyTaskService = null;
	private static CommLogService commLogService = null;
//	private HeartBeatTaskService heartBeatTaskService = null;
//	private GPSLocationTaskService gPSLocationTaskService = null;
	private CommLogServiceConnection commLogServiceConn = null;
	private DialyTaskServiceConnection dialyTaskServiceConn = null;
	private static ColetApplication app = null;

//	public static final int UPDATE_AD = 1;
	public static final int UPDATE_MU = 2;
	public static final int ERROR_STATUS = 3;// 服务商被停用，终端被冻结，停用，非法编号等

	
//	private LocationClient mLocationClient = null;
//	private BDLocationListener myListener = new MyLocationListener();
//	
//	public LocationClient getLocation(){
//		return this.mLocationClient;
//	}
//	
//	/**
//	 * 设置相关参数
//	 */
//	private void setLocationOption() {
//		LocationClientOption option = new LocationClientOption();
//		option.setOpenGps(true);
//		option.setIsNeedAddress(true);// 返回的定位结果包含地址信息
//		option.setAddrType("all");// 返回的定位结果包含地址信息
//		option.setCoorType("bd09ll");// 返回的定位结果是百度经纬度,默认值gcj02
//		option.setScanSpan(5000);// 设置发起定位请求的间隔时间为5000ms
//		option.disableCache(true);// 禁止启用缓存定位
//		option.setPoiNumber(5); // 最多返回POI个数
//		option.setPoiDistance(1000); // poi查询距离
//		option.setPoiExtraInfo(true); // 是否需要POI的电话和地址等详细信息
//		mLocationClient.setLocOption(option);
//	}
//
//	public class MyLocationListener implements BDLocationListener {
//		@Override
//		public void onReceiveLocation(BDLocation location) {
//			if (location == null)
//				return;
//			StringBuffer sb = new StringBuffer(256);
//			sb.append("当前时间 : ");
//			sb.append(location.getTime());
//			sb.append("\n错误码 : ");
//			sb.append(location.getLocType());
//			sb.append("\n纬度 : ");
//			sb.append(location.getLatitude());
//			sb.append("\n经度 : ");
//			sb.append(location.getLongitude());
//			sb.append("\n半径 : ");
//			sb.append(location.getRadius());
//			if (location.getLocType() == BDLocation.TypeGpsLocation) {
//				sb.append("\n速度 : ");
//				sb.append(location.getSpeed());
//				sb.append("\n卫星数 : ");
//				sb.append(location.getSatelliteNumber());
//			} else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
//				sb.append("\n地址 : ");
//				sb.append(location.getAddrStr());
//			}
//			ColetApplication.getApp().logDebug(
//					"onReceiveLocation " + sb.toString());
//
//			ColetApplication.getApp().getConfigFile()
//					.setLatitude(String.valueOf(location.getLatitude()));
//			ColetApplication.getApp().getConfigFile()
//					.setLongitude(String.valueOf(location.getLongitude()));
//
//		}
//
//		public void onReceivePoi(BDLocation poiLocation) {
//
//		}
//	}

	public static Handler messageHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
//			case UPDATE_AD:
//				if (welcomeActivity != null) {
//					welcomeActivity.updateAd();
//					app.logDebug("------------------------------------------------广告版本升级成功------------------------------------------------");
//				}
//				break;
			case UPDATE_MU:
				if (welcomeActivity != null) {
					welcomeActivity.updateMenuCategory();
					app.logDebug("------------------------------------------------欢迎菜单升级成功------------------------------------------------");
				}
				break;
			case ERROR_STATUS:
				int status = msg.getData().getInt("status");
				String message = (null == msg.getData().getString("message")) ? "" : msg.getData().getString("message");
				app.transferToErrorActivity(status, message,"");
				break;
			}
		}
	};

	public void transferToErrorActivity(int flag, String message,String error) {

		//stopSendCoffeeStatus();
		String errorFlag = "";
		Bundle data = new Bundle();
		// 跳转回广告欢迎页面,只在欢迎页面进行切换
		if (null != currentActivity) {
			if ((flag != 0) && (flag != 1)) {//currentActivity instanceof Activity_02_Welcome && 
				switch (flag) {
				case 2:
					// 冻结
					errorFlag = "terminal_freeze";
					break;
				case 3:
					// 停用
					errorFlag = "terminal_disabled";
					break;
				case 4:
					// 非法编号
					errorFlag = "terminal_illegal";
					break;
				case -9:
					// 服务商被停用
					errorFlag = "franchise_disabled";
					break;
				case 1000:
					errorFlag = "coffeeError";
					break;
				}
				
				//if(!lastError.equals(error)){
				//	lastError = error;
				//}
//				lastActivity = currentActivity;
				data.putString("errorFlag", errorFlag);
				data.putString("errorMsg", message);
				Intent intent = new Intent();
				intent.setClass(currentActivity, Activity_09_Error.class);
				intent.putExtras(data);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
			}
//			else if ( (flag == 0) && currentActivity instanceof Activity_09_Error) {
////				switch (flag) {
////				case 0:
////				case 1:
////					break;
////				}
//				Intent intent = new Intent();
//				intent.setClass(currentActivity, Activity_02_Welcome.class);
//				intent.putExtras(data);
//				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//				startActivity(intent);
//
//			}
		}
	}

	private void checkConfigFile(Context cxt) {

		configFile = new ConfigFileUtil(cxt);
		if (configFile.getFranchiseId().equals("0")) {
			configFile.setFranchiseId("1");
		}
		if (configFile.getLastAdVersion().equals("0.0.0")) {
			configFile.setLastAdVersion("0.0.1");
		}
		if (configFile.getLastMenuVersion().equals("0.0.0")) {
			configFile.setLastMenuVersion("0.0.1");
		}
		if (configFile.getApiUrl().equals("")) {
			configFile.setApiUrl("http://projectx.abrain.cn/admin/api/");
		}
		if (configFile.getHeartbeatPluse().equals("0")) {
			configFile.setHeartbeatPluse("2");
		}
		if ((configFile.getADSwitchTime() ==5) || (configFile.getADSwitchTime() == 0)) {
			configFile.setADSwitchTime(10);
		}
		if (configFile.getADSwitchEffect().equals("无")) {
			configFile.setADSwitchEffect("从左至右");
		}
		if (configFile.getAliPayUrl().equals("")) {
			//http://192.168.0.5:8080/CenterTradeServer/QRCodeTradeServlet
			//http://116.62.187.146/CenterTradeServer/QRCodeTradeServlet
			//192.168.3.63:8080
			configFile.setAliPayUrl("http://116.62.187.146:8080/CenterTradeServer/QRCodeTradeServlet");
		}
		if (configFile.getWechatPayUrl().equals("")) {
			//http://192.168.0.5:8080/CenterTradeServer/QRCodeTradeServlet
			//http://116.62.187.146/CenterTradeServer/QRCodeTradeServlet
			configFile.setWechatPayUrl("http://116.62.187.146:8080/CenterTradeServer/QRCodeTradeServlet");
		}
		
		this.configFile.setLastErrorTime(String.valueOf(((new Date()).getTime() - (6*1000*60))));
	}

	public ColetCoffeeUtil getUart() {
		return this.uart;
	}

	public static enum COFFEE_TYPE {
		NULL, HOT_AMRICAN, COLD_AMRICAN, ITALY, HOT_NATIE, COLD_NATIE,
	}

	/**
	 * 停止向欢迎画面发送正常销售信息
	 */
	public void stopSendOK() {
		sendOk = false;
	}

	/**
	 * 开始向欢迎画面发送正常销售信息
	 */
	public void startSendOK() {
		sendOk = true;
	}

	private boolean sendOk = false;

	/**
	 * 停止向咖啡进度画面发送正常销售信息
	 */
	public void stopSendCoffeeStatus() {
		sendCoffeeStatus = false;
	}

	/**
	 * 开始向咖啡进度画面发送正常销售信息
	 */
	public void startSendCoffeeStatus() {
		sendCoffeeStatus = true;
	}

	private boolean sendCoffeeStatus = false;
	
	public void OnUartReceived(byte[] data) {
		if( null == currentActivity)
		{
			return;
		}
		
		if (CashlessConstants.debug) {
			this.logDebug("通信数据:"+byte2HexStr(data, data.length));
		}

		Message msg = new Message();
		Bundle parameters = new Bundle();
		int value = 0;
		int tmp = 0;
		
		if(chechCommandEror(data[2]).length() > 0 ){
			return;
		}
		
		if(checkError(data[4]).length() > 0 ){
			return;
		}
		
		switch (data[2]) {
		case 0x01:// 通讯测试  // 机器就绪状态,可以接受命令

			if ( (currentActivity instanceof Activity_01_Connect) && (sendOk == true) ) {
				if(byte2HexStr(data[5]).equals("88")){
					if( null != Activity_01_Connect.welcomeHandler ){
						msg.what = 1;
						Activity_01_Connect.welcomeHandler.sendMessage(msg);
					}
				}else{
					// 获取进度
					if( null != Activity_01_Connect.welcomeHandler ){
						tmp = Integer.valueOf(byte2HexStr(data[5]));
						value = ((int) tmp) * 100 / 6 ;
						msg.what = 2;
						parameters.putString("progress", String.valueOf(value));
						msg.setData(parameters);
						Activity_01_Connect.welcomeHandler.sendMessage(msg);
					}
				}
			}
			else if((currentActivity instanceof Activity_09_Error)){
				if( null != Activity_01_Connect.welcomeHandler ){
					msg.what = 1;
					Activity_01_Connect.welcomeHandler.sendMessage(msg);
				}
			}
			break;
//		case 0x02:// 参数设置 // 开机自检中
//			// 获取进度
//			tmp = data[4];
//			value = ((int) tmp) * 10;
//			msg.what = 2;
//			parameters.putString("progress", String.valueOf(value));
//			msg.setData(parameters);
//			Activity_01_Connect.welcomeHandler.sendMessage(msg);
//			break;
		case 0x03:// 取消除垢 		 // 制作意式中
		case 0x04:// 热行关机 		 // 制作热美式中
			break;
		case 0x05:// 制做意式咖啡  	 // 制作冰美式中
		case 0x06:// 制做美式咖啡	 // 制作卡布奇诺中
		case 0x07:// 制做卡布奇诺咖啡  // 制作热拿铁中
		case 0x08:// 制作拿铁咖啡            // 制作冰拿铁中
		case 0x09:// 制作热牛奶
		case 0x10:// 制作热奶沫
		case 0x11:// 制作热水
			
			if (sendCoffeeStatus == true) {
				if( byte2HexStr(data[5]).equals("88")){
					parameters.putInt("progress", 100);
				}else{
					int steps = 7;
					switch(data[2]){
					case 0x05:// 制做意式咖啡
					case 0x06:// 制做美式咖啡
					case 0x09:// 制作热牛奶
						steps = 7;
						break;
					case 0x10:// 制作热奶沫
						steps = 7;
						break;
					case 0x11:// 制作热水
					case 0x07:// 制做卡布奇诺咖啡
					case 0x08:// 制作拿铁咖啡
						steps = 6;
						break;
					default:
						break;
					}
//					parameters.putInt("progress", value = ((int) data[5]) * 10 / steps / 10 );
//					msg.setData(parameters);
//					msg.what = 1;
//					Activity_06_MakeCoffee.makecoffeeHandler.sendMessage(msg);
					MakeCoffee(data[5],(int)data[2]);
				}
//				tmp = (int) data[3];
//				if (tmp == -86) {
//					tmp = data[4];
//					value = ((int) tmp) * 10;
//					if (value > 90) {
//						break;
//					}
//					parameters.putInt("progress", value);
//					msg.setData(parameters);
//					msg.what = 1;
//
//					Activity_06_MakeCoffee.makecoffeeHandler.sendMessage(msg);
//				} else {// 故障
//					parameters.putInt("error", tmp);
//					msg.setData(parameters);
//					msg.what = 2;
//					Activity_06_MakeCoffee.makecoffeeHandler.sendMessage(msg);
//				}
			}
			break;
		case 0x12:// 执行除垢 // 执行自清洗中
		case 0x13://执行自清洗
			break;
		default:
			break;

		}

	}

	
	//制作状态完成的分类判断
    public void MakeCoffee(byte data, int type) {
        int tmp = 0;
        int value = 0;
        Bundle parameters = new Bundle();
        Message msg = new Message();
//        if (shortClassName.equals("tech.bbwang.www.Activity_09_Error") && first) {
//            Intent intent = new Intent();
//            Bundle bundle = new Bundle();
//            bundle.putString("outTradeNo", "");
//            bundle.putInt("coffeeType", 100);
//            intent.putExtras(bundle);
//            intent.setClass(MyActivityManager.getInstance().getCurrentActivity(), Activity_06_MakeCoffee.class);
//            MyActivityManager.getInstance().getCurrentActivity().startActivity(intent);
//            MyActivityManager.getInstance().getCurrentActivity().finish();
//        } else if (shortClassName.equals("tech.bbwang.www.Activity_09_Error")) {
//            Intent intent = new Intent();
//            intent.setClass(MyActivityManager.getInstance().getCurrentActivity(), Activity_02_Welcome.class);
//            MyActivityManager.getInstance().getCurrentActivity().startActivity(intent);
//            MyActivityManager.getInstance().getCurrentActivity().finish();
//        }
        if (type==5 || type==6 || type==16 || type ==9){
            tmp = data;
            value = ((int) tmp) * 14;
            //Log.d("HotMilkTea","status: "+tmp+" "+" progress: "+String.valueOf(value)+"%. ");
            parameters.putInt("progress", value);
            msg.setData(parameters);
            msg.what = 1;
            if( null != Activity_06_MakeCoffee.makecoffeeHandler )
            Activity_06_MakeCoffee.makecoffeeHandler.sendMessage(msg);
        } else if(type==7||type==8) {
            tmp = data;
            value = ((int) tmp) * 10;
            parameters.putInt("progress", value);
            msg.setData(parameters);
            msg.what = 1;
            if( null != Activity_06_MakeCoffee.makecoffeeHandler )
            Activity_06_MakeCoffee.makecoffeeHandler.sendMessage(msg);
        } else {
            tmp = data;
            value = ((int) tmp) * 16;
            parameters.putInt("progress", value);
            msg.setData(parameters);
            msg.what = 1;
            if( null != Activity_06_MakeCoffee.makecoffeeHandler )
            Activity_06_MakeCoffee.makecoffeeHandler.sendMessage(msg);
        }
    }
	
	private String chechCommandEror(byte data){
		
		String error = "";
		String message = "";
		
		if(byte2HexStr(data).equals("90")){
			error = "E14";
			message= "咖啡机状态异常，请尝试重启";
		}
		
		if( error.equals("E14")){
			if(!(currentActivity instanceof Activity_09_Error)){
				if((currentActivity instanceof Activity_06_MakeCoffee) || (currentActivity instanceof Activity_04_Pay) ){
					
				}
				this.transferToErrorActivity(1000, message,error);
			}
			
		}

		return error;
	}
	/**
	 * 检查是否有异常
	 * @param data
	 */
	private String checkError(byte data){
		String error = "";
		String message = "";
		switch(data){
		case (byte) 0xaa:
			break;
		case 0x01:
			error = "E3";
			message= "缺水";
//			error = "E8";
//			message= "门1缺失";
			break;// 门1缺失
		case 0x02: 
			error = "E5";
			message= "缺豆";
//			error = "E9";
//			message= "门2缺失";
			break;// 门2缺失
//		case 0x03:
//			error = "E10";
//			message= "门3缺失";
//			break;// 门3缺失
		case 0x04:
			error = "E8";
			message= "门1缺失";
//			error = "E11";
//			message= "门4缺失";
			break;// 门4缺失
//		case 0x05:
//			error = "E3";
//			message= "缺水";
//			break;// 缺水
//		case 0x06:
//			error = "E7";
//			message= "出冰异常";
//			break;// 出冰异常
//		case 0x07:
//			error = "E6";
//			message= "冰桶异常";
//			break;// 冰桶异常
		case 0x08:
			error = "E4";
			message= "冲泡器缺失";
//			error = "E1";
//			message= "机器管路阻塞";
			break;// 机器管路阻塞
//		case 0x09:
//			error = "E5";
//			message= "缺豆";
//			break;// 缺豆
		case 0x0a:
			error = "E13";
			message= "缺渣盒";
			//error = "E4";
			//message= "冲泡器缺失";
			break;// 冲泡器缺失
//		case 0x0b:
//			error = "E12";
//			message= "冲泡器阻塞";
//			break;// 冲泡器阻塞
//		case 0x0c:
//			error = "E2";
//			message= "冰桶板未连接";
//			break;// 冰桶板未连接
		default:
			break;
		}
		
		long currentTime = (new Date()).getTime();
		long lastTime = Long.parseLong(this.configFile.getLastErrorTime());
		int miniute = (int) ((currentTime-lastTime)/1000/60);
		
//		if( lastActivity != null ){
//			this.logError("前一画面为:"+lastActivity.getClass().getName());
//			this.logError("当前画面为:"+currentActivity.getClass().getName());
//			this.logError("当前错误为:"+lastError);
//		}else{
//			this.logError("前一画面为:"+currentActivity.getClass().getName());
//			this.logError("当前画面为:"+currentActivity.getClass().getName());
//			this.logError("当前错误为:"+lastError);
//		}
		
		if( error.length() >= 2 ){
			this.logError("咖啡机故障： 代码="+error+" 错误="+message+" 距离上次通知="+miniute+"分钟");
			this.configFile.setLastErrorTime(String.valueOf(currentTime));
			
		}

		//不进行重复的错误通知,如果云端订阅了短信，重复通知会造成浪费
		if( error.length() >= 2 && this.configFile.getLastError().equals(error) && miniute <= 1 ){
//			if( (error.length() == 0) && (currentActivity instanceof Activity_09_Error)){
//				Activity_09_Error.handler.sendEmptyMessage(2);//切换到菜单界面
//			}else 
//				if( (error.length() == 0) && !(currentActivity instanceof Activity_09_Error)){
//			}else 
				if(error.length()==2 &&  !(currentActivity instanceof Activity_09_Error)){
				//退款排除门开故障
				if(currentActivity instanceof Activity_06_MakeCoffee && (!error.equals("E8"))){
					//发起退款
					ColetApplication.getApp().stopSendCoffeeStatus();
					Activity_06_MakeCoffee.makecoffeeHandler.sendEmptyMessage(1110);
				}
				this.transferToErrorActivity(1000, message,error);
			}
//			else if(error.length()==2 &&  (currentActivity instanceof Activity_09_Error)){
//				this.transferToErrorActivity(1000, message);
//			}
			
			return error;
		}
		
		if(error.length() >= 2){
			//通知云平台
			this.logError("咖啡机故障： 代码="+error+" 错误="+message);
			WSUtil.getInstance().uploadWarnCodes(configFile.getFranchiseId(), SystemUtil.getIMEI(this), error, DateUtil.getDateTime(DateUtil.sdf_yyyyMMddHHmmssSSS), 0);
			this.configFile.setLastError(error);
		}

		if( (error.length() == 0) && (currentActivity instanceof Activity_09_Error)){
			ColetApplication.getApp().stopSendCoffeeStatus();
			
			Bundle data2 = new Bundle();
			Intent intent = new Intent();
			intent.setClass(currentActivity, Activity_02_Welcome.class);
			intent.putExtras(data2);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
		}else if( (error.length() == 0) && !(currentActivity instanceof Activity_09_Error)){
		}else if(error.length()==2 &&  !(currentActivity instanceof Activity_09_Error)){
			
			//退款排除门开故障
			if(currentActivity instanceof Activity_06_MakeCoffee  && (!error.equals("E8"))){
				//发起退款
				ColetApplication.getApp().stopSendCoffeeStatus();
				Activity_06_MakeCoffee.makecoffeeHandler.sendEmptyMessage(1110);
			}
			this.transferToErrorActivity(1000, message,error);
		}else if(error.length()==2 &&  (currentActivity instanceof Activity_09_Error)){
			this.transferToErrorActivity(1000, message,error);
		}
		
		return error;
	}
	private final static char[] mChars = "0123456789ABCDEF".toCharArray();

	public static String byte2HexStr(byte[] b, int iLen) {
		StringBuilder sb = new StringBuilder();
		for (int n = 0; n < iLen; n++) {
			sb.append(mChars[(b[n] & 0xFF) >> 4]);
			sb.append(mChars[b[n] & 0x0F]);
			sb.append(' ');
		}
		return sb.toString().trim().toUpperCase(Locale.US);
	}
	
	public static String byte2HexStr(byte b) {
		StringBuilder sb = new StringBuilder();
			sb.append(mChars[(b & 0xFF) >> 4]);
			sb.append(mChars[b & 0x0F]);
			sb.append(' ');
		return sb.toString().trim().toUpperCase(Locale.US);
	}

	private static Activity_02_Welcome welcomeActivity = null;
	private static Activity currentActivity = null;
	//private static Activity lastActivity = null;
	//private static String lastError = "";
	ActivityLifecycleCallbacks alc = new ActivityLifecycleCallbacks() {
		@Override
		public void onActivityStopped(Activity activity) {
			//logDebug("onActivityStopped");
		}

		@Override
		public void onActivityStarted(Activity activity) {
			//logDebug("onActivityStarted");
		}

		@Override
		public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
			//logDebug("onActivitySaveInstanceState");
		}

		@Override
		public void onActivityResumed(Activity activity) {
			//logDebug("onActivityResumed");
			if (activity instanceof Activity_02_Welcome) {
				welcomeActivity = (Activity_02_Welcome) activity;
			}
			//if( null != currentActivity  ){
			//	lastActivity = currentActivity;
			//}
			currentActivity = activity;
		}

		@Override
		public void onActivityPaused(Activity activity) {
			//logDebug("onActivityPaused");
		}

		@Override
		public void onActivityDestroyed(Activity activity) {
			//logDebug("onActivityDestroyed");
		}

		@Override
		public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
			//logDebug("onActivityCreated");
		}
	};

	@Override
	public void onCreate() {

		super.onCreate();
		
//		mLocationClient = new LocationClient(getApplicationContext()); // 声明LocationClient类
//		mLocationClient.registerLocationListener(myListener); // 注册监听函数
//		setLocationOption();
//		mLocationClient.start();// 开始定位
		
		//异常崩溃日志捕获
		CrashHandler.getInstance().init(this);
		
		logger = new LogUtil();
		ThreadPool.service.submit(logger);

		this.registerActivityLifecycleCallbacks(alc);

		//uart = new ColetCoffeeUtil(this, "OnUartReceived");
		//uart.startRun();

		app = this;

		checkConfigFile(this);

		
		Intent commLogServiceIntent = new Intent(this, CommLogService.class);
		commLogServiceConn = new CommLogServiceConnection();
		startService(commLogServiceIntent);
		bindService(commLogServiceIntent, commLogServiceConn, Context.BIND_AUTO_CREATE);

		Intent dialyTaskServiceIntent = new Intent(this, DialyTaskService.class);
		dialyTaskServiceConn = new DialyTaskServiceConnection();
		startService(dialyTaskServiceIntent);
		bindService(dialyTaskServiceIntent, dialyTaskServiceConn, Context.BIND_AUTO_CREATE);

		Intent heartBeatTaskServiceIntent = new Intent(this, HeartBeatTaskService.class);
		startService(heartBeatTaskServiceIntent);

//		Intent GPSLocationTaskServiceIntent = new Intent(this, GPSLocationTaskService.class);
//		startService(GPSLocationTaskServiceIntent);
		
		dbHelper = new DatabaseHelper(this);

		database = dbHelper.getReadableDatabase();
		addao = new AdDAO(database);
		mdao = new MenuDAO(database);
		prePaycardDao = new PrePayCardDAO(database);
		addao.create();
		mdao.create();
		prePaycardDao.create();
		
		this.logDebug("------------------------------------------------程序启动完成------------------------------------------------");
		this.logDebug("开始自检...");
		this.logDebug("本机UUID：" + SystemUtil.getIMEI(this));
		this.logDebug("心跳时间:" + this.configFile.getHeartbeatPluse() + "分钟");
		this.logDebug("本机广告版本：" + ((DBAd) this.addao.getLastAd()).getAd_version());
		this.logDebug("本机菜单版本：" + ((DBMenu) this.mdao.getLastMenu()).getMenu_version());
		this.logDebug("本机储值卡版本：" + this.configFile.getVipCardListCode());
		this.logDebug("本机APP版本：" + SystemUtil.getVersionName(this));
		this.logDebug("自检完成");
	}

	@Override
	public void onTerminate() {
		super.onTerminate();

		if (this.uart != null) {
			this.uart.destoryThread();
		}

		if (this.commLogServiceConn != null) {
			this.unbindService(commLogServiceConn);
		}
		if (this.dialyTaskServiceConn != null) {
			this.unbindService(dialyTaskServiceConn);
		}

		if (commLogService != null) {
			Intent commLogServiceIntent = new Intent(this, CommLogService.class);
			this.stopService(commLogServiceIntent);
		}
		if (dialyTaskService != null) {
			Intent dialyTaskServiceIntent = new Intent(this, DialyTaskService.class);
			this.stopService(dialyTaskServiceIntent);
		}

	}

	public static ColetApplication getApp() {
		return app;
	}

	public boolean isLegalTerminal() {

		boolean ret = false;
		int status = this.configFile.getActiveStatus();

		switch (status) {
		case 0:
		case 1:
			ret = true;
			break;
		default:
			break;
		}

		return ret;
	}

	public void logDebug(String log) {
		if (this.logger != null) {
			this.logger.d(log);
		}else{
			Log.d("ColetCoffee", log);
		}
	}

	public void logError(String log) {
		if (this.logger != null) {
			this.logger.e(log);
		}else{
			Log.d("ColetCoffee", log);
		}
	}

	public ConfigFileUtil getConfigFile() {
		return this.configFile;
	}

	public AdDAO getAddao() {
		return addao;
	}

	public MenuDAO getMdao() {
		return mdao;
	}
	
	public PrePayCardDAO getprePaycardDao(){
		return prePaycardDao;
	}

	class DialyTaskServiceConnection implements ServiceConnection {
		// 服务被绑定成功之后执行
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			// IBinder service为onBind方法返回的Service实例
			// DialyTaskService.MyBinder dialyTaskServiceBinder =
			// (DialyTaskService.MyBinder) service;
			dialyTaskService = ((DialyTaskService.MyBinder) service).getService();
			dialyTaskService.setDataCallback(new DialyTaskService.DataCallback() {
				// 执行回调函数
				@Override
				public void dataChanged(String str) {

				}
			});

		}

		@Override
		public void onServiceDisconnected(ComponentName arg0) {

		}
	};

	class CommLogServiceConnection implements ServiceConnection {
		// 服务被绑定成功之后执行
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			// IBinder service为onBind方法返回的Service实例
			// CommLogService.MyBinder commLogServiceBinder =
			// (CommLogService.MyBinder) service;
			commLogService = ((CommLogService.MyBinder) service).getService();
			commLogService.setDataCallback(new CommLogService.DataCallback() {
				// 执行回调函数
				@Override
				public void dataChanged(String str) {
					// Log.d("DEMO", str);
				}
			});
			logger.setLogService(commLogService);
		}

		@Override
		public void onServiceDisconnected(ComponentName arg0) {

		}
	};

}
