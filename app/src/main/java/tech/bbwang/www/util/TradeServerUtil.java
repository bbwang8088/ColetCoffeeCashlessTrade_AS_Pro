package tech.bbwang.www.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import tech.bbwang.www.activity.ColetApplication;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import cn.colet.result.TradeResult;

/**
 * 访问微支付交易服务器类
 * 
 * @author wang-bingbing
 * 
 */
public class TradeServerUtil {

	/* 微支付服务器请求链接 */
//	private final static String LOGIN_URL = "http://116.62.187.146/CenterTradeServer/QRCodeTradeServlet";
//	private final static String LOGIN_URL_WEChAT = "http://116.62.187.146/CenterTradeServer/QRCodeTradeServlet";
	private final static String ENCODE_UTF8 = "UTF-8";
	private static Gson gson = new Gson();
	private StringBuffer buffer = new StringBuffer();
	private Map<String, String> parameters = new HashMap<String, String>();
	static TradeServerUtil tradeServer = null;

	private TradeServerUtil() {
	}

	public static TradeServerUtil getInstance() {
		if (tradeServer == null) {
			tradeServer = new TradeServerUtil();
		}
		return tradeServer;
	}

	/**
	 * 创建二维码接口方法
	 * 
	 * @param tradeGateway
	 *            交易路由，指支付宝或者微信支付等名称
	 * @param machineId
	 *            机器编号
	 * @param subject
	 *            交易名称
	 * @param totalFee
	 *            交易金额
	 * @param discountFee
	 *            打折金额
	 * @return
	 */
	public synchronized TradeResult createQRCode(String franchise,String terminal_code,String tradeGateway, String machineId, String subject, String totalFee,
			String discountFee,String machineTradeNo,boolean payForVipCard) {
		TradeResult ret = null;
		Log.d("ColetCoffee_S", "输入的订单号2为:"+machineTradeNo);
		parameters.clear();
		parameters.put("franchise",franchise );
		parameters.put("terminal_code",terminal_code );
		parameters.put("action", CashlessConstants.ACTION_CREATE_QRCODE);
		parameters.put("tradeGateway", tradeGateway);
		parameters.put("machineId", machineId);
		parameters.put("subject", subject);//
		parameters.put("totalFee", totalFee);
		parameters.put("discountFee", discountFee);
		parameters.put("machineTradeNo", machineTradeNo);
		if( payForVipCard == true ){
			parameters.put("vip_card", "vip_card");
		}else{
			parameters.put("vip_card", "");
		}
		
		ret = doPost(parameters,tradeGateway);

		return ret;
	}

	/**
	 * 交易查询
	 * 
	 * @param tradeGateway
	 *            交易路由，指支付宝或者微信支付等名称
	 * @param machineId
	 *            机器编号
	 * @param outTradeNo
	 *            外部交易号，从createQRCode接口返回
	 * @return
	 */
	public synchronized TradeResult tradeQuery(String franchise,String terminal_code,String tradeGateway, String machineId, String outTradeNo,boolean payForVipCard) {

		TradeResult ret = null;

		parameters.clear();
		parameters.put("franchise",franchise );
		parameters.put("terminal_code",terminal_code );
		parameters.put("action", CashlessConstants.ACTION_TRADE_QUERY);
		parameters.put("tradeGateway", tradeGateway);
		parameters.put("machineId", machineId);
		parameters.put("outTradeNo", outTradeNo);
		if( payForVipCard == true ){
			parameters.put("pay_for_vip_card", "vip_card");
		}else{
			parameters.put("pay_for_vip_card", "");
		}
		
		ret = doPost(parameters,tradeGateway);

		return ret;

	}

	/**
	 * 交易查询
	 * 
	 * @param tradeGateway
	 *            交易路由，指支付宝或者微信支付等名称
	 * @param machineId
	 *            机器编号
	 * @param outTradeNo
	 *            外部交易号，从createQRCode接口返回
	 * @return
	 */
	public synchronized TradeResult tradeCancel(String franchise,String terminal_code,String tradeGateway, String machineId, String outTradeNo,boolean payForVipCard) {
		TradeResult ret = null;

		parameters.clear();
		parameters.put("franchise",franchise );
		parameters.put("terminal_code",terminal_code );
		parameters.put("action", CashlessConstants.ACTION_TRADE_CANCEL);
		parameters.put("tradeGateway", tradeGateway);
		parameters.put("machineId", machineId);
		parameters.put("outTradeNo", outTradeNo);
		if( payForVipCard == true ){
			parameters.put("pay_for_vip_card", "vip_card");
		}else{
			parameters.put("pay_for_vip_card", "");
		}
		
		ret = doPost(parameters,tradeGateway);

		return ret;
	}

	/**
	 * 交易退款
	 * 
	 * @param tradeGateway
	 *            交易路由，指支付宝或者微信支付等名称
	 * @param machineId
	 *            机器编号
	 * @param outTradeNo
	 *            外部交易号，从createQRCode接口返回
	 * @param refundFee
	 *            需要退款的金额
	 * @return
	 */
	public synchronized TradeResult tradeRefund(String franchise,String terminal_code,String tradeGateway, String machineId, String outTradeNo, String refundFee,boolean payForVipCard) {
		TradeResult ret = null;

		parameters.clear();
		parameters.put("franchise",franchise );
		parameters.put("terminal_code",terminal_code );
		parameters.put("action", CashlessConstants.ACTION_TRADE_REFUND);
		parameters.put("tradeGateway", tradeGateway);
		parameters.put("machineId", machineId);
		parameters.put("outTradeNo", outTradeNo);
		parameters.put("refundFee", refundFee);
		if( payForVipCard == true ){
			parameters.put("pay_for_vip_card", "vip_card");
		}else{
			parameters.put("pay_for_vip_card", "");
		}
		
		ret = doPost(parameters,tradeGateway);

		return ret;
	}

	private TradeResult doPost(Map<String, String> parameters,String gateWay) {

		TradeResult ret = null;
		// 我们请求的数据
		String tmp;
		tmp = getParametersString(parameters);
		if( tmp != null ){
		Log.d("TradeServerUtil请求数据:", tmp);}
		tmp = doPost(tmp,gateWay);
		try {
			if( tmp != null ){
			Log.d("TradeServerUtil反馈数据:", tmp);
			ret = gson.fromJson(tmp, TradeResult.class);
			}
		} catch (JsonSyntaxException e) {
		}
		return ret;

	}

	private String getParametersString(Map<String, String> parameters) {
		String ret = "";
		if (parameters == null || parameters.size() == 0) {
			return ret;
		}
		int i = 1;
		this.buffer.setLength(0);// 索引归零清空数据
		try {
			for (String key : parameters.keySet()) {
				if (i == 1) {
					this.buffer.append(key + "=" + URLEncoder.encode(parameters.get(key), ENCODE_UTF8));
				} else {
					this.buffer.append("&" + key + "=" + URLEncoder.encode(parameters.get(key), ENCODE_UTF8));
				}
				i++;
			}
		} catch (UnsupportedEncodingException e) {
			// e.printStackTrace();
		}
		ret = this.buffer.toString();
		return ret;
	}

	private String doPost(String data,String tradeWay) {
		String aliPayUrl  = ColetApplication.getApp().getConfigFile().getAliPayUrl();
		String wechatPayUrl  = ColetApplication.getApp().getConfigFile().getWechatPayUrl();
		String msg = "";
		InputStream is = null;
		URL url = null;
		try {
			// 初始化URL
			if( tradeWay.equals(CashlessConstants.TRADE_GATEWAY_ALIPAY)){
				url = new URL(aliPayUrl);
			}			else if( tradeWay.equals(CashlessConstants.TRADE_GATEWAY_WECHAT)){
				url = new URL(wechatPayUrl);
			}else if( tradeWay.equals(CashlessConstants.TRADE_GATEWAY_VIPCARD)){
				url = new URL(aliPayUrl);
			}
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			// 设置请求方式
			conn.setRequestMethod("POST");

			// 设置超时信息
			conn.setReadTimeout(10000);
			conn.setConnectTimeout(10000);
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
			conn.setRequestProperty("Accept-Charset", "UTF-8");
			// 设置允许输入
			conn.setDoInput(true);
			// 设置允许输出
			conn.setDoOutput(true);

			// post方式不能设置缓存，需手动设置为false
			conn.setUseCaches(false);

			// 得到输出流
			OutputStream out = conn.getOutputStream();
			out.write(data.getBytes());
			out.flush();
			out.close();
			conn.connect();

			// 正常返回
			if (conn.getResponseCode() == 200) {
				is = conn.getInputStream();
				ByteArrayOutputStream message = new ByteArrayOutputStream();
				int len = 0;
				byte buffer[] = new byte[1024];
				while ((len = is.read(buffer)) != -1) {
					message.write(buffer, 0, len);
				}
				is.close();
				message.close();
				msg = new String(message.toByteArray());

				return msg;
			}

		} catch (MalformedURLException e) {
			msg = e.getLocalizedMessage();
		} catch (IOException e) {
			msg = e.getLocalizedMessage();
		}
		return msg;

	}
}
