package tech.bbwang.www.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import tech.bbwang.www.activity.ColetApplication;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.telephony.TelephonyManager;

public class SystemUtil {

	/**
	 * 加载本地图片
	 * 
	 * @param url
	 * @return
	 */
	public static Bitmap getLoacalBitmap(String url) {
		try {
			FileInputStream fis = new FileInputStream(url);
			return BitmapFactory.decodeStream(fis); // /把流转化为Bitmap图片

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 获取手机IMEI号
	 */
	public static String getIMEI(Context ctx) {
		return getMyUUID(ctx);
	}

	private static String getMyUUID(Context ctx) {

		final TelephonyManager tm = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
		final String tmDevice, tmSerial, androidId;// tmPhone,
		tmDevice = "" + tm.getDeviceId();
		tmSerial = "" + tm.getSimSerialNumber();
		androidId = "" + android.provider.Settings.Secure.getString(ctx.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
		UUID deviceUuid = new UUID(androidId.hashCode(), ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
		String uniqueId = deviceUuid.toString();
		String ret = (uniqueId.substring(9, 9 + 4) + uniqueId.substring(14, 14 + 4) + uniqueId.substring(19, 19 + 2)).toUpperCase();
		// String ret =
		// uniqueId.substring(uniqueId.length()-10,uniqueId.length()).toUpperCase();
		ColetApplication.getApp().logDebug("Local machine uuid: " + ret);

		return ret;
		// return "COLET_DEMO_001";

	}

	public static String getVersionName(Context ctx) {
		// 获取packagemanager的实例
		PackageManager packageManager = ctx.getPackageManager();
		// getPackageName()是你当前类的包名，0代表是获取版本信息
		PackageInfo packInfo = null;
		String version = "", pkgName = "";
		try {
			pkgName = ctx.getPackageName();
			packInfo = packageManager.getPackageInfo(pkgName, 0);
			version = packInfo.versionName;
			ColetApplication.getApp().logDebug("Get application version success. Version:" + version);
		} catch (NameNotFoundException e) {
			version = "0.0.0";
			ColetApplication.getApp().logDebug("Get application version failed. Not found packagename:" + pkgName + ". Return " + version + ".");
		}
		return version;
	}

	public static String getSuffix(String url) {
		String ret = "";
		if (null == url || url.equals("")) {
			return ret;
		}

		String suffixes = "avi|mpeg|3gp|mp3|mp4|wav|jpeg|gif|jpg|png|apk|exe|pdf|rar|zip|docx|doc";
		Pattern pat = Pattern.compile("[\\w]+[\\.](" + suffixes + ")");// 正则判断
		Matcher mc = pat.matcher(url);// 条件匹配
		while (mc.find()) {
			ret = mc.group();// 截取文件名后缀名
		}

		return ret;
	}

	private static final SimpleDateFormat SDF_yyyyMMddHHmmssSSS = new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.CHINA);
	private static final Random random = new Random();

	/**
	 * 获取外部交易号
	 * 
	 * @return
	 */
	public static String getMachineTradeNo() {
		return SDF_yyyyMMddHHmmssSSS.format(new Date()) + getRandom(10000, 20000);
	}

	public static int getRandom(int max, int min) {
		return random.nextInt(max) % (max - min + 1) + min;
	}

	public static String startAnimationForAd(int animationId) {

		String msg = "";
		switch (animationId) {
		case 0:
			msg = "百叶窗";
			break;
		case 1:
			msg = "擦除";
			break;
		case 2:
			msg = "盒装";
			break;
		case 3:
			msg = "阶梯";
			break;
		case 4:
			msg = "菱形";
			break;
		case 5:
			msg = "轮子";
			break;
		case 6:
			msg = "劈裂";
			break;
		case 7:
			msg = "棋盘";
			break;
		case 8:
			msg = "切入";
			break;
		case 9:
			msg = "扇形展开";
			break;
		case 10:
			msg = "十字扩展";
			break;
		case 11:
			msg = "随机线条";
			break;
		case 12:
			msg = "向内溶解";
			break;
		case 13:
			msg = "圆形扩展";
			break;
		default:
			msg = "百叶窗（默认）";
			break;
		}

		return msg;
	}

	/**
	 * 检测手机号是否正确
	 * @param mobiles
	 * @return
	 */
	public static boolean isMobileNO(String mobiles) {
		Pattern p = Pattern.compile("^((13[0-9])|(15[0-9])|(18[0-9])|(17[0-9]))\\d{8}$");
		Matcher m = p.matcher(mobiles);
		return m.matches();
	}
	
	public static String txfloat(float x) {
	    DecimalFormat df=new DecimalFormat("0.00");
	    return df.format(x);
	}
}
