package tech.bbwang.www.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GsonUtil {

	public static GsonBuilder gsonBuilder = new GsonBuilder();
	public static Gson gson = gsonBuilder.create();

	public static String _001_ERROR_TIME_OUT = " {\"status\": -2,\"message\": \"连接超时\",\"franchise\": \"0\" }";
	public static String _002_ERROR_NETWORK_EXCEPTION = " {\"status\": -3,\"message\": \"网络异常\",\"franchise\": \"0\" }";
	public static String _003_ERROR_THREAD_EXCEPTION = " {\"status\": -4,\"message\": \"异常\",\"franchise\": \"0\" }";
	public static String _004_ERROR_JSON_EXCEPTION = " {\"status\": -2,\"message\": \"数据异常\",\"franchise\": \"0\" }";
	
//	public static String _001_ERROR_TIME_OUT = "{\"status\":\"Thread execute time out\"}";
//	public static String _002_ERROR_NETWORK_EXCEPTION = "{\"error_msg\":\"Network exception\"}";
//	public static String _003_ERROR_THREAD_EXCEPTION = "{\"error_msg\":\"Thread exception\"}";
//	public static String _004_ERROR_JSON_EXCEPTION = "{\"error_msg\":\"Not valid json string\"}";
}
