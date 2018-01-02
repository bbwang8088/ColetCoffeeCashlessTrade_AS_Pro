package tech.bbwang.www.util;

public class CashlessConstants {

	/**
	 * 咖啡种类
	 * @author wang-bingbing
	 *
	 */
	public final static int COFFEE_TYPE_CAPPUCCION = 0;//卡布基诺
	public final static int COFFEE_TYPE_HOT_AMERACAN = 1;//美式咖啡（热
	public final static int COFFEE_TYPE_ICE_AMERACAN = 2;//美式咖啡（冰）
	public final static int COFFEE_TYPE_ICE_LATTE = 3;//拿铁（冰）
	public final static int COFFEE_TYPE_HOT_LATTE = 4;//拿铁（热）
	public final static int COFFEE_TYPE_ITALY = 5;//意式咖啡
	
	
	public final static int COFFEE_TYPE_MILK = 6;//热牛奶
//	public final static int COFFEE_TYPE_ZQX = 7;//自清洗
//	public final static int COFFEE_TYPE_CG = 8;//除垢
//	public final static int COFFEE_TYPE_TOTAL = 9;//总数
	public final static int COFFEE_TYPE_MILKRATE = 10;//奶沫
//	public final static int COFFEE_TYPE_CANLE= 11;//取消
	
	/**
	 * 是否需要糖浆
	 * @author wang-bingbing
	 *
	 */
	public final static int WANT_SUGER_NO = 0;;
	public final static int WANT_SUGER_YES = 1;
	
	/**
	 * 咖啡杯量
	 * @author wang-bingbing
	 *
	 */
	public final static int CUP_SIZE_SMALL = 0;
	public final static int CUP_SIZE_NORMAL = 1;
	public final static int CUP_SIZE_BIG = 2;
	
	public final static boolean debug = true;

	//倒计时5秒钟
	public final static long COUNT_PAY_FAILD = 5;
	//倒计时1分钟
	public final static long COUNT_TIME = 90;
	
	public static String ALIPAY_PUBLIC_KEY = "";
	public static String ALIPAY_APPID = "";
	public static String ALIPAY_APPNAME = "";
	public static String ALIPAY_APP_PUBLIC_KEY = "";
	public static String ALIPAY_APP_PRIVATE_KEY="";
	
	public static final String RESULT_SUCCESS = "success";
	public static final String RESULT_WAIT_FOR_PAY = "wait_for_pay";
	public static final String RESULT_FAILED = "failed";
	public static final String RESULT_SYSTEM_ERROR = "system error";
	public static final String RESULT_ILLEGAL_PARAMETER = "illegal parameter";

	public static final String ACTION_CREATE_QRCODE = "createbarcode";
	public static final String ACTION_TRADE_QUERY = "query";
	public static final String ACTION_TRADE_CANCEL = "cancel";
	public static final String ACTION_TRADE_REFUND = "refund";
	
	public static final String TRADE_GATEWAY_ALIPAY = "alipay";
	public static final String TRADE_GATEWAY_WECHAT = "wechatpay";
	public static final String TRADE_GATEWAY_VIPCARD = "vipcard";
}
