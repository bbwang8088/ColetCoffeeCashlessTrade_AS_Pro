package tech.bbwang.www.ws;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import tech.bbwang.www.activity.ColetApplication;
import tech.bbwang.www.util.DateUtil;
import tech.bbwang.www.util.GsonUtil;
import tech.bbwang.www.util.SystemUtil;
import tech.bbwang.www.util.ThreadPool;

/**
 * 访问云管理服务器类
 * 
 * @author wang-bingbing
 * 
 */
public class WSUtil {

	static WSUtil cloudServer = null;

	private WSUtil() {
	}

	public static WSUtil getInstance() {
		if (cloudServer == null) {
			cloudServer = new WSUtil();
		}
		return cloudServer;
	}

	/**
	 * 告警登记
	 * 
	 * @param franchise
	 * @param terminal_code
	 * @param warn_codes
	 * @param terminal_time
	 * @return
	 */
	public ErrorCode uploadWarnCodes(String franchise, String terminal_code, String warn_code, String terminal_time, int count) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("franchise", franchise);
		params.put("terminal_code", terminal_code);
//		for (String code : warn_codes) {
			params.put("warn_codes", warn_code);
//		}
		params.put("terminal_time", terminal_time);
		String data = getPostResult(1000*30,ColetApplication.getApp().getConfigFile().getApiUrl() + "uploadWarnCodes", params);
		ErrorCode ret = new ErrorCode();
		if( data.equals("") ){
			return ret;
		}
		
		try{
			ret = GsonUtil.gson.fromJson(data, ErrorCode.class);
		}catch(Exception e){
			ColetApplication.getApp().logError("请求的api返回值格式不正确: " + data );
		}
		
		count++;
		if ((ret.getStatus() == -1)) {
			if( count <= MAX_RETRY ){
				ColetApplication.getApp().logError("第" + count + "次告警登记失败,"+RETRY_TIME+"秒后重试...");
				try {
					Thread.sleep(1000 * RETRY_TIME);
					ret = uploadWarnCodes(franchise, terminal_code, warn_code, terminal_time, count);
				} catch (InterruptedException e) {
				}
			}else{
				ColetApplication.getApp().logError(MAX_RETRY+"次告警登记失败，取消本次操作...");
			}
		}else if(ret.getStatus() == -3 ){
			ColetApplication.getApp().logDebug("网络异常，告警登记失败");
		}else{
			ColetApplication.getApp().logDebug("告警登记成功");
		}

		return ret;
	}

	/**
	 * 激活终端号
	 * 
	 * @param franchise
	 * @param terminal_code
	 * @return
	 */
	public ActiveInfo registTerminal(String franchise, String terminal_code, int count) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("franchise", franchise);
		params.put("terminal_code", terminal_code);
		String data = getPostResult(1000*15,ColetApplication.getApp().getConfigFile().getApiUrl() + "registTerminal", params);
		ActiveInfo ret = new ActiveInfo();
		if( data.equals("") ){
			return ret;
		}
		
		try{
			ret = GsonUtil.gson.fromJson(data, ActiveInfo.class);
		}catch(Exception e){
			ColetApplication.getApp().logError("请求的api返回值格式不正确: " + data );
		}
		
		count++;
		if ((ret.getStatus() == -1)) {
			if( count <= MAX_RETRY ){
				ColetApplication.getApp().logError("第" + count + "次激活终端号失败,"+RETRY_TIME+"秒后重试...");
				try {
					Thread.sleep(1000 * RETRY_TIME);
					ret = registTerminal(franchise, terminal_code, count);
				} catch (InterruptedException e) {
				}
			}else{
				ColetApplication.getApp().logError(MAX_RETRY+"次激活终端号失败，取消本次操作...");
			}
		}else if(ret.getStatus() == -3 ){
			ColetApplication.getApp().logDebug("网络异常，激活终端号失败");
		}else{
			ColetApplication.getApp().logDebug("激活终端号成功");
		}

		return ret;
	}

	/**
	 * 咖啡机心跳事件
	 * 
	 * @param franchise
	 * @param terminal_code
	 * @param ad_ver
	 * @param menu_ver
	 * @param terminal_time
	 * @param app_ver
	 * @param heartbeat_pluse
	 * @param server_url
	 * @return
	 */
	public HeartBeat heartbeat(String franchise, String terminal_code, String ad_ver, String menu_ver, String terminal_time, String app_ver,
			String heartbeat_pluse, String server_url,String longitude,String latitude, int count) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("franchise", franchise);
		params.put("terminal_code", terminal_code);
		params.put("ad_ver", ad_ver);
		params.put("menu_ver", menu_ver);
		params.put("terminal_time", terminal_time);
		params.put("app_ver", app_ver);
		params.put("heartbeat_pluse", heartbeat_pluse);
		params.put("server_url", server_url);
		params.put("longitude", longitude);
		params.put("latitude", latitude);
		
		String data = getPostResult(1000*30,ColetApplication.getApp().getConfigFile().getApiUrl() + "heartbeat", params);
		HeartBeat ret = new HeartBeat();
		if( data.equals("") ){
			return ret;
		}
		
		try{
			ret = GsonUtil.gson.fromJson(data, HeartBeat.class);
		}catch(Exception e){
			ColetApplication.getApp().logError("请求的api返回值格式不正确: " + data );
		}
		
		count++;
		if ((ret.getStatus() == -1)) {
			if( count <= MAX_RETRY ){
				ColetApplication.getApp().logError("第" + count + "次咖啡机心跳事件失败,"+RETRY_TIME+"秒后重试...");
				try {
					Thread.sleep(1000 * RETRY_TIME);
					ret = heartbeat(franchise, terminal_code, ad_ver, menu_ver, terminal_time, app_ver, heartbeat_pluse, server_url,longitude,latitude, count);
				} catch (InterruptedException e) {
				}
			}else{
				ColetApplication.getApp().logError(MAX_RETRY+"次咖啡机心跳事件失败，取消本次操作...");
			}
		}else if((ret.getStatus() == -9)){
			ColetApplication.getApp().logDebug("咖啡机心跳事件失败");
			ColetApplication.getApp().logDebug(ret.getMessage());
		}else if(ret.getStatus() == -3 ){
			ColetApplication.getApp().logDebug("网络异常，咖啡机心跳事件失败");
		}else{
			ColetApplication.getApp().logDebug("咖啡机心跳事件成功");
		}
		
		
		return ret;
	}

	/**
	 * 提货码校验
	 * 
	 * @param franchise
	 * @param code
	 * @return
	 */
	public CheckCode checkCode(String franchise, String terminal_code, String code, int count) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("franchise", franchise);
		params.put("terminal_code", terminal_code);
		params.put("code", code);
		String data = getPostResult(1000*10,ColetApplication.getApp().getConfigFile().getApiUrl() + "checkCode", params);
		
		CheckCode ret = new CheckCode();
		if( data.equals("") ){
			return ret;
		}
		
		try{
			ret = GsonUtil.gson.fromJson(data, CheckCode.class);
		}catch(Exception e){
			ColetApplication.getApp().logError("请求的api返回值格式不正确: " + data );
		}
		
//		count++;
//		if ((ret.getStatus() == -1)) {
//			if( count <= MAX_RETRY ){
//				ColetApplication.getApp().logError("第" + count + "次获取提货码校验失败,"+RETRY_TIME+"秒后重试...");
//				try {
//					Thread.sleep(1000 * RETRY_TIME);
//					ret = checkCode(franchise, terminal_code, code, count);
//				} catch (InterruptedException e) {
//				}
//			}else{
//				ColetApplication.getApp().logError(MAX_RETRY+"次获取提货码校验失败，取消本次操作...");
//			}
//		}else{
//			ColetApplication.getApp().logError("获取提货码校验成功");
//		}
		
		return ret;
	}

	/**
	 * 获取特定版本Android程序
	 * 
	 * @param franchise
	 * @param code
	 * @return
	 */
	public GetAPK getAPK(String franchise, String terminal_code, String version, int count) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("franchise", franchise);
		params.put("terminal_code", terminal_code);
		params.put("version", version);
		
		String data = getPostResult(1000*30,ColetApplication.getApp().getConfigFile().getApiUrl() + "getAPK", params);
		GetAPK ret = new GetAPK();
		if( data.equals("") ){
			return ret;
		}
		
		try{
			ret = GsonUtil.gson.fromJson(data, GetAPK.class);
		}catch(Exception e){
			ColetApplication.getApp().logError("请求的api返回值格式不正确: " + data );
		}
		
		count++;
		if ((ret.getStatus() == -1)) {
			if( count <= MAX_RETRY ){
				ColetApplication.getApp().logError("第" + count + "次获取特定版本Android程序失败,"+RETRY_TIME+"秒后重试...");
				try {
					Thread.sleep(1000 * RETRY_TIME);
					ret = getAPK(franchise, terminal_code, version, count);
				} catch (InterruptedException e) {
				}
			}else{
				ColetApplication.getApp().logError(MAX_RETRY+"次获取特定版本Android程序失败，取消本次操作...");
			}
		}else if(ret.getStatus() == -3 ){
			ColetApplication.getApp().logDebug("网络异常，获取特定版本Android程序失败");
		}else{
			ColetApplication.getApp().logDebug("获取特定版本Android程序成功");
		}
		
		return ret;
	}

	/**
	 * 获取菜单详细信息
	 * 
	 * @param franchise
	 * @param version
	 * @return
	 */
	public MenuDetail getMenuDetailInfo(String franchise,  String terminal_code,String version, int count) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("franchise", franchise);
		params.put("terminal_code",terminal_code);
		params.put("version", version);
		String data = getPostResult(1000*30,ColetApplication.getApp().getConfigFile().getApiUrl() + "getMenuDetailInfo", params);
		MenuDetail ret = new MenuDetail();
		if( data.equals("") ){
			return ret;
		}
		
		try{
			ret = GsonUtil.gson.fromJson(data, MenuDetail.class);
		}catch(Exception e){
			ColetApplication.getApp().logError("请求的api返回值格式不正确: " + data );
		}
		
		count++;
		if ((ret.getStatus() == -1)) {
			if( count <= MAX_RETRY ){
				ColetApplication.getApp().logError("第" + count + "次获取菜单详细信息失败,"+RETRY_TIME+"秒后重试...");
				try {
					Thread.sleep(1000 * RETRY_TIME);
					ret = getMenuDetailInfo(franchise, terminal_code,version, count);
				} catch (InterruptedException e) {
				}
			}else{
				ColetApplication.getApp().logError(MAX_RETRY+"次获取菜单详细信息失败，取消本次操作...");
			}
		}else if(ret.getStatus() == -3 ){
			ColetApplication.getApp().logDebug("网络异常，获取菜单详细信息失败");
		}else{
			ColetApplication.getApp().logDebug("获取菜单详细信息成功");
		}
		
		return ret;
	}

	/**
	 * 获取广告详细信息
	 * 
	 * @param franchise
	 * @param version
	 * @return
	 */
	public AdDetail getAdDetailInfo(String franchise, String terminal_code,String version, int count) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("franchise", franchise);
		params.put("terminal_code",terminal_code);
		params.put("version", version);
		String data = getPostResult(1000*30,ColetApplication.getApp().getConfigFile().getApiUrl() + "getAdDetailInfo", params);
		AdDetail ret = new AdDetail();
		if( data.equals("") ){
			return ret;
		}
		
		try{
			ret = GsonUtil.gson.fromJson(data, AdDetail.class);
		}catch(Exception e){
			ColetApplication.getApp().logError("请求的api返回值格式不正确: " + data );
		}
		count++;
		if ((ret.getStatus() == -1)) {
			if( count <= MAX_RETRY ){
				ColetApplication.getApp().logError("第" + count + "次获取广告详细信息失败,"+RETRY_TIME+"秒后重试...");
				try {
					Thread.sleep(1000 * RETRY_TIME);
					ret = getAdDetailInfo(franchise, terminal_code,version, count);
				} catch (InterruptedException e) {
				}
			}else{
				ColetApplication.getApp().logError(MAX_RETRY+"次获取广告详细信息失败，取消本次操作...");
			}
		}else if(ret.getStatus() == -3 ){
			ColetApplication.getApp().logDebug("网络异常，获取广告详细信息失败");
		}else{
			ColetApplication.getApp().logDebug("获取广告详细信息成功");
		}
		
		return ret;
	}

	/**
	 * 获取更新信息
	 * 
	 * @param franchise
	 * @return
	 */
	public UpdateInfo getUpdateInfo(String franchise,String terminal_code, int count) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("franchise", franchise);
		params.put("terminal_code",terminal_code);
		String data = getPostResult(1000*30,ColetApplication.getApp().getConfigFile().getApiUrl() + "getUpdateInfo", params);
		UpdateInfo ret = new UpdateInfo();
		if( data.equals("") ){
			return ret;
		}
		
		try{
		ret = GsonUtil.gson.fromJson(data, UpdateInfo.class);
		}catch(Exception e){
			ColetApplication.getApp().logError("请求的api返回值格式不正确: " + data );
		}
		count++;
		if ((ret.getStatus() == -1)) {
			if( count <= MAX_RETRY ){
				ColetApplication.getApp().logError("第" + count + "次获取更新信息失败,"+RETRY_TIME+"秒后重试...");
				try {
					Thread.sleep(1000 * RETRY_TIME);
					ret = getUpdateInfo(franchise, terminal_code,count);
				} catch (InterruptedException e) {
				}
			}else{
				ColetApplication.getApp().logError(MAX_RETRY+"次获取更新信息失败，取消本次操作...");
			}
		}else if(ret.getStatus() == -3 ){
			ColetApplication.getApp().logDebug("网络异常，获取更新信息失败");
		}else{
			ColetApplication.getApp().logDebug("获取更新信息成功");
		}
		
		return ret;
	}

	
	/**
	 * 给手机发验证短信
	 * 
	 * @param franchise
	 * @param version
	 * @return
	 */
	public ValidateSMS validate(String franchise, String terminal_code,String phone) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("franchise", franchise);
		params.put("terminal_code",terminal_code);
		params.put("phone", phone);
		String data = getPostResult(1000*10,ColetApplication.getApp().getConfigFile().getApiUrl() + "validate", params);
		ValidateSMS ret = new ValidateSMS();
		if( data.equals("") ){
			return ret;
		}
		
		try{
			ret = GsonUtil.gson.fromJson(data, ValidateSMS.class);
		}catch(Exception e){
			ColetApplication.getApp().logError("请求的api返回值格式不正确: " + data );
		}
		if ((ret.getStatus() == -1)) {
			ColetApplication.getApp().logDebug("给手机发验证短信失败");
		}else if(ret.getStatus() == -3 ){
			ColetApplication.getApp().logDebug("网络异常，给手机发验证短信失败");
		}else{
			ColetApplication.getApp().logDebug("给手机发验证短信成功");
		}
		
		return ret;
	}
	
	/**
	 * 储值卡（开卡）
	 * 验证手机通过之后，接口直接创建一张储值卡、金额为0、卡号从600001开始编码（做法：将主键设置为600001开始编码，自增）。
	 * 状态为正常的储值卡
	 * @param franchise
	 * @param version
	 * @return
	 */
	public CreateCard createCard(String franchise, String terminal_code,String phone,String name,String validate_code) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("franchise", franchise);
		params.put("terminal_code",terminal_code);
		params.put("phone", phone);
		params.put("name", name);
		params.put("validate_code", validate_code);
		String data = getPostResult(1000*10,ColetApplication.getApp().getConfigFile().getApiUrl() + "createCard", params);
		CreateCard ret = new CreateCard();
		if( data.equals("") ){
			return ret;
		}
		
		try{
			ret = GsonUtil.gson.fromJson(data, CreateCard.class);
		}catch(Exception e){
			ColetApplication.getApp().logError("请求的api返回值格式不正确: " + data );
		}
		if ((ret.getStatus() == -1)) {
			ColetApplication.getApp().logDebug("开卡失败");
		}else if(ret.getStatus() == -3 ){
			ColetApplication.getApp().logDebug("网络异常，开卡失败");
		}else{
			ColetApplication.getApp().logDebug("开卡成功,卡号"+ret.getData().getCard_no());
		}
		
		return ret;
	}
	
	/**
	 * 储值卡（查询）
	 * 接口返回指定储值卡的详细信息（包括卡号、手机号、余额、卡片状态等必要的信。以及消费记录-最近10笔）
	 * @param franchise
	 * @param version
	 * @return
	 */
	public QueryCard card(String franchise, String terminal_code,String phone,String card_id) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("franchise", franchise);
		params.put("terminal_code",terminal_code);
		params.put("phone", phone);
		params.put("card_id", card_id);
		String data = getPostResult(1000*10,ColetApplication.getApp().getConfigFile().getApiUrl() + "card", params);
		QueryCard ret = new QueryCard();
		if( data.equals("") ){
			return ret;
		}
		
		try{
			ret = GsonUtil.gson.fromJson(data, QueryCard.class);
		}catch(Exception e){
			ColetApplication.getApp().logError("请求的api返回值格式不正确: " + data );
		}
		if ((ret.getStatus() == -1)) {
			ColetApplication.getApp().logDebug("查卡失败");
		}else if(ret.getStatus() == -3 ){
			ColetApplication.getApp().logDebug("网络异常，查卡失败");
		}else{
			ColetApplication.getApp().logDebug("查卡成功,信息："+ret.toString());
		}
		
		return ret;
	}
	
	/**
	 * 储值卡（查询）
	 * 接口返回指定储值卡的详细信息（包括卡号、手机号、余额、卡片状态等必要的信。以及消费记录-最近10笔）
	 * @param franchise
	 * @param version
	 * @return
	 */
	
	/**
	 * 充值
	 * 
	 * @param franchise 运营商编号
	 * @param terminal_code 终端编号
	 * @param amount 金额（单位：分）*消费时请设置为负数
	 * @param pay_type 交易的支付类型（1 支付宝、2 微信、3 储值卡）
	 * @param pay_serial 充值交易的在线流水号（对账用）
	 * @param refundFlag 退款标记（1：退款 0：普通充值或消费）
	 * @param card_id 卡号（和手机号二选一）
	 * @param phone 手机号（和卡号二选一）
	 * @param timestamp 时间戳（为了防止重复提交，每次都请重新生成）。建议使用UNIX时间戳：例如1510282330
	 * @return
	 */
	public ChargeCard cardCharge(String franchise, String terminal_code,String amount,String pay_type,String pay_serial,String card_id,String phone) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("franchise", franchise);
		params.put("terminal_code",terminal_code);
		params.put("amount", amount);
		params.put("pay_type", pay_type);	
		params.put("pay_serial", pay_serial);
		params.put("refund", "0");
		params.put("card_id", card_id);
		params.put("phone", phone);
		params.put("timestamp", DateUtil.getDateTime(DateUtil.sdf_yyyyMMddHHmmssSSS));
		String data = getPostResult(1000*10,ColetApplication.getApp().getConfigFile().getApiUrl() + "cardCharge", params);
		ChargeCard ret = new ChargeCard();
		if( data.equals("") ){
			return ret;
		}
		
		try{
			ret = GsonUtil.gson.fromJson(data, ChargeCard.class);
		}catch(Exception e){
			ColetApplication.getApp().logError("请求的api返回值格式不正确: " + data );
		}
		if ((ret.getStatus() == -1)) {
			ColetApplication.getApp().logDebug("储值卡充值失败");
		}else if(ret.getStatus() == -3 ){
			ColetApplication.getApp().logDebug("网络异常，储值卡充值失败");
		}else{
			ColetApplication.getApp().logDebug("储值卡充值成功,信息："+ret.toString());
		}
		
		return ret;
	}
	
	/**
	 * 消费
	 * 
	 * @param franchise 运营商编号
	 * @param terminal_code 终端编号
	 * @param amount 金额（单位：分）*消费时请设置为负数
	 * @param pay_type 交易的支付类型（1 支付宝、2 微信、3 储值卡）
	 * @param pay_serial 充值交易的在线流水号（对账用）
	 * @param refundFlag 退款标记（1：退款 0：普通充值或消费）
	 * @param card_id 卡号（和手机号二选一）
	 * @param phone 手机号（和卡号二选一）
	 * @param timestamp 时间戳（为了防止重复提交，每次都请重新生成）。建议使用UNIX时间戳：例如1510282330
	 * @return
	 */
	public ChargeCard cardConsume(String franchise, String terminal_code,String amount,String pay_type,String pay_serial,String card_id,String phone) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("franchise", franchise);
		params.put("terminal_code",terminal_code);
		params.put("amount", "-"+amount);
		params.put("pay_type", pay_type);
		params.put("pay_serial", pay_serial);
		params.put("refund", "0");
		params.put("card_id", card_id);
		params.put("phone", phone);
		params.put("timestamp", DateUtil.getDateTime(DateUtil.sdf_yyyyMMddHHmmssSSS));
		String data = getPostResult(1000*10,ColetApplication.getApp().getConfigFile().getApiUrl() + "cardCharge", params);
		ChargeCard ret = new ChargeCard();
		if( data.equals("") ){
			return ret;
		}
		
		try{
			ret = GsonUtil.gson.fromJson(data, ChargeCard.class);
		}catch(Exception e){
			ColetApplication.getApp().logError("请求的api返回值格式不正确: " + data );
		}
		if ((ret.getStatus() == -1)) {
			ColetApplication.getApp().logDebug("储值卡消费失败");
		}else if(ret.getStatus() == -3 ){
			ColetApplication.getApp().logDebug("网络异常，储值卡消费失败");
		}else{
			ColetApplication.getApp().logDebug("储值卡消费成功,信息："+ret.toString());
		}
		
		return ret;
	}
	
	
	
//	/**
//	 * 储值卡退款
//	 * @param franchise
//	 * @param terminal_code
//	 * @param trade_no
//	 */
//	public RefundCard cardRefund(String franchise,String terminal_code,String trade_no, int count){
//		Map<String, String> params = new HashMap<String, String>();
//		params.put("franchise", franchise);
//		params.put("terminal_code",terminal_code);
//		params.put("trade_no",trade_no);
////		params.put("timestamp",DateUtil.getDateTime(DateUtil.sdf_yyyyMMddHHmmssSSS));//已退款
////		System.out.println("打印退款参数 franchise:"+franchise+", terminal_code:"+terminal_code+", trade_no:"+trade_no);
//		String data = getPostResult(1000*30,ColetApplication.getApp().getConfigFile().getApiUrl() + "cardRefund", params);
////		System.out.println("打印退款详情:"+data);
//		
//		RefundCard ret = new RefundCard();
//		if( data.equals("") ){
//			return ret;
//		}
//		
//		count++;
//		try{
//			ret = GsonUtil.gson.fromJson(data, RefundCard.class);
//		}catch(Exception e){
//			ColetApplication.getApp().logError("请求的api返回值格式不正确: " + data );
//		}
//		if ( ret.getStatus() < 0 ) {
//			ColetApplication.getApp().logDebug("储值卡退款失败");
//			if( count <= 3 ){
//				try {
//					Thread.sleep(1000 * 3);
//				cardRefund(franchise,terminal_code,trade_no,count);
//				}catch (InterruptedException e) {
//				}
//			}else{
//				ColetApplication.getApp().logError(3+"次储值卡退款失败，取消本次操作...");
//			}
//		}else if(ret.getStatus() == -3 ){
//			ColetApplication.getApp().logDebug("网络异常，储值卡退款失败");
//		}else{
//			ColetApplication.getApp().logDebug("储值卡退款成功,信息："+ret.toString());
//		}
//		
//		return ret;
//	}
//	
	
//	/**
//	 * 退款
//	 * 
//	 * @param franchise 运营商编号
//	 * @param terminal_code 终端编号
//	 * @param amount 金额（单位：分）*消费时请设置为负数
//	 * @param pay_type 交易的支付类型（1 支付宝、2 微信、3 储值卡）
//	 * @param pay_serial 充值交易的在线流水号（对账用）
//	 * @param refundFlag 退款标记（1：退款 0：普通充值或消费）
//	 * @param card_id 卡号（和手机号二选一）
//	 * @param phone 手机号（和卡号二选一）
//	 * @param timestamp 时间戳（为了防止重复提交，每次都请重新生成）。建议使用UNIX时间戳：例如1510282330
//	 * @return
//	 */
//	public ChargeCard cardRefund(String franchise, String terminal_code,String amount,String pay_type,String pay_serial,String card_id,String phone) {
//		Map<String, String> params = new HashMap<String, String>();
//		params.put("franchise", franchise);
//		params.put("terminal_code",terminal_code);
//		params.put("amount", amount);
//		params.put("pay_type", pay_type);
//		params.put("pay_serial", pay_serial);
//		params.put("refund", "1");
//		params.put("card_id", card_id);
//		params.put("phone", phone);
//		params.put("timestamp", DateUtil.getDateTime(DateUtil.sdf_yyyyMMddHHmmssSSS));
//		String data = getPostResult(1000*5,ColetApplication.getApp().getConfigFile().getApiUrl() + "cardCharge", params);
//		ChargeCard ret = new ChargeCard();
//		if( data.equals("") ){
//			return ret;
//		}
//		
//		try{
//			ret = GsonUtil.gson.fromJson(data, ChargeCard.class);
//		}catch(Exception e){
//			ColetApplication.getApp().logError("请求的api返回值格式不正确: " + data );
//		}
//		if ((ret.getStatus() == -1)) {
//			ColetApplication.getApp().logDebug("储值卡退款失败");
//		}else if(ret.getStatus() == -3 ){
//			ColetApplication.getApp().logDebug("网络异常，储值卡退款失败");
//		}else{
//			ColetApplication.getApp().logDebug("储值卡退款成功,信息："+ret.toString());
//		}
//		
//		return ret;
//	}
//	
	
	
	/**
	 * 退款
	 * 
	 * @param franchise 运营商编号
	 * @param terminal_code 终端编号
	 * @param amount 金额（单位：分）*消费时请设置为负数
	 * @param pay_type 交易的支付类型（1 支付宝、2 微信、3 储值卡）
	 * @param pay_serial 充值交易的在线流水号（对账用）
	 * @param refundFlag 退款标记（1：退款 0：普通充值或消费）
	 * @param card_id 卡号（和手机号二选一）
	 * @param phone 手机号（和卡号二选一）
	 * @param timestamp 时间戳（为了防止重复提交，每次都请重新生成）。建议使用UNIX时间戳：例如1510282330
	 * @return
	 */
	public ChargeCardList cardChargeList(String franchise, String terminal_code) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("franchise", franchise);
		params.put("terminal_code",terminal_code);
		String data = getPostResult(1000*10,ColetApplication.getApp().getConfigFile().getApiUrl() + "cardChargeList", params);
		ChargeCardList ret = new ChargeCardList();
		if( data.equals("") ){
			return ret;
		}
		
		try{
			ret = GsonUtil.gson.fromJson(data, ChargeCardList.class);
		}catch(Exception e){
			ColetApplication.getApp().logError("请求的api返回值格式不正确: " + data );
		}
		if ((ret.getStatus() == -1)) {
			ColetApplication.getApp().logDebug("储值卡销售列表取得失败");
		}else if(ret.getStatus() == -3 ){
			ColetApplication.getApp().logDebug("网络异常，储值卡销售列表取得失败");
		}else{
			ColetApplication.getApp().logDebug("储值卡销售列表取得成功,信息："+ret.toString());
		}
		
		return ret;
	}
	
	/**
	 * 支付成功
	 * @param franchise
	 * @param terminal_code
	 * @param trade_no
	 * @param payType
	 */
	public void updateTradeStatusPayed(String franchise,String terminal_code,String trade_no,String payType){
		String url = "http://projectx.abrain.cn/admin/api/updateTradeStatus";
		Map<String, String> params = new HashMap<String, String>();
		params.put("franchise", franchise);
		params.put("terminal_code",terminal_code);
		params.put("trade_no",trade_no);
		params.put("pay_type",payType);
		params.put("pay_status","2");//支付成功
		params.put("status","3");//支付成功
		String data = getPostResult(1000*10,url, params);
		System.out.println(data);
		
		//params.remove("pay_status");
		//data = getPostResult(1000*5,url, params);
		//System.out.println(data);
		
	}
	
	/**
	 * 已退款
	 * @param franchise
	 * @param terminal_code
	 * @param trade_no
	 * @param payType
	 */
	public void updateTradeStatusRefund(String franchise,String terminal_code,String trade_no,String payType){
		String url = "http://projectx.abrain.cn/admin/api/updateTradeStatus";
		Map<String, String> params = new HashMap<String, String>();
		params.put("franchise", franchise);
		params.put("terminal_code",terminal_code);
		params.put("trade_no",trade_no);
		params.put("pay_type",payType);
		params.put("pay_status","5");//已退款
		
		String data = getPostResult(1000*10,url, params);
		System.out.println(data);
	}
	
	/**
	 * 支付关闭
	 * @param franchise
	 * @param terminal_code
	 * @param trade_no
	 * @param payType
	 */
	public void updateTradeStatusCancel(String franchise,String terminal_code,String trade_no,String payType){
		String url = "http://projectx.abrain.cn/admin/api/updateTradeStatus";
		Map<String, String> params = new HashMap<String, String>();
		params.put("franchise", franchise);
		params.put("terminal_code",terminal_code);
		params.put("trade_no",trade_no);
		params.put("pay_type",payType);
		params.put("pay_status","4");//支付关闭
		
		String data = getPostResult(1000*10,url, params);
		System.out.println(data);
	}
	
	// 重试3回取不到则放弃
	static final int MAX_RETRY = 6;
	static final int RETRY_TIME = 10;
	
	private String getPostResult(int timeout,String url, Map<String, String> params) {

		FutureTask<String> mtask = new FutureTask<String>(new WSTask(url, params));
		FutureTask<String> mResult = new FutureTask<String>(new WSAsynResult(mtask));
		ThreadPool.service.submit(mtask);
		ThreadPool.service.submit(mResult);

		String data = "";

		try {
			// 等待30000毫秒
			data = mtask.get(timeout, TimeUnit.MILLISECONDS);//1000*30
		} catch (InterruptedException e) {
			data = GsonUtil._003_ERROR_THREAD_EXCEPTION;
			ColetApplication.getApp().logError(e.getMessage());
		} catch (ExecutionException e) {
			data = GsonUtil._003_ERROR_THREAD_EXCEPTION;
			ColetApplication.getApp().logError(e.getMessage());
		} catch (TimeoutException e) {
			data = GsonUtil._001_ERROR_TIME_OUT;
			ColetApplication.getApp().logError(e.getMessage());
		}

		return data;

	}
}
