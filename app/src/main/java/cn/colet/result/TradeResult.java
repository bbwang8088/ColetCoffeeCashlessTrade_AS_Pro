package cn.colet.result;

public class TradeResult {

	//外部交易号，用以查询交易状态，取消交易等，调用任意接口时均返回。
	public String outTradeNo = "";
	//二维码字符串，用以生成二维码图片的字符串，只有调用二维码创建接口时才返回，否则是空值。
	public String qrCode = "";
	//标识本次调用的动作
	public String action = "";
	//标识本次动作的执行结果，当结果为success时其他属性才会有值
	public String result = "";
	//具体跟支付提供商相关的返回代码
	public String code = "";
	//交易路由
	public String tradeGateWay = "";
}
