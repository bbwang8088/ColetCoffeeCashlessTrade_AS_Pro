package tech.bbwang.www.ws;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.Callable;

import tech.bbwang.www.activity.ColetApplication;
import tech.bbwang.www.util.GsonUtil;

public class WSTask implements Callable<String> {

	private String url = null;// 下载目标文件
	private Map<String, String> paramsters = null;

	public WSTask(String url, Map<String, String> params) {

		this.url = url;
		this.paramsters = params;
	}

	@Override
	public String call() {
		return doPost(this.url);
	}

	private String doPost(String uri) {

		String msg = "";// "{\"error_msg\":\"error\"}";
		InputStream is = null;
		URL url = null;
		try {
			// 初始化URL
			url = new URL(uri);
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

			if (this.paramsters != null) {
				String data = "";

				int i = this.paramsters.size();
				int count = 0;
				for (String key : this.paramsters.keySet()) {
					data += (key + "=" + this.paramsters.get(key));
					count++;
					if( count < i   ){
						data += "&";
					}
				}
				// 得到输出流
				OutputStream out = conn.getOutputStream();
				out.write(data.getBytes());
				out.flush();
				out.close();
			}

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
			msg = GsonUtil._004_ERROR_JSON_EXCEPTION;
			ColetApplication.getApp().logError(e.getLocalizedMessage());
		} catch (IOException e) {
			msg = GsonUtil._002_ERROR_NETWORK_EXCEPTION;
			ColetApplication.getApp().logError(e.getLocalizedMessage());
		}
		return msg;

	}

}
