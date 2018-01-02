package tech.bbwang.www.util;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import tech.bbwang.www.service.CommLogService;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class LogUtil implements Runnable {

	private static final String TAG = "ColetCoffee";
	private static CommLogService commLogService = null;
	BlockingQueue<LogTemp> tmpLog = new LinkedBlockingQueue<LogTemp>(1000);

	public LogUtil() {

	}

//	public LogUtil(CommLogService service) {
//		commLogService = service;
//	}

	public void setLogService(CommLogService service) {
		commLogService = service;
	}

	public void e(String log) {
		if (log == null || log.length() == 0) {
			return;
		}
		Log.e(TAG, log);
		LogTemp t = new LogTemp();
		t.logContext = log;
		t.logTag = "error";
		try {
			tmpLog.put(t);
		} catch (InterruptedException e) {
		} catch (NullPointerException e) {
		}

		// Message msg = new Message();
		// Bundle data = new Bundle();
		// data.putString("log", log);
		// msg.setData(data);
		// logHandler.sendMessage(msg);
	}

	public void d(String log) {
		if (log == null || log.length() == 0) {
			return;
		}

		Log.d(TAG, log);
		LogTemp t = new LogTemp();
		t.logContext = log;
		t.logTag = "debug";
		try {
			tmpLog.put(t);
		} catch (InterruptedException e) {
		} catch (NullPointerException e) {
		}

		// Message msg = new Message();
		// Bundle data = new Bundle();
		// data.putString("log", log);
		// msg.setData(data);
		// logHandler.sendMessage(msg);
	}

	public static Handler logHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (commLogService != null) {
				commLogService.log2File(msg.getData().get("log").toString());
			}
		}
	};

	@Override
	public void run() {
		while (true) {
			try {
				Thread.sleep(1);
				if( commLogService == null ){
					continue;
				}
				LogTemp t = tmpLog.take();
				Message msg = new Message();
				Bundle data = new Bundle();
				data.putString("log", t.logContext);
				msg.setData(data);
				logHandler.sendMessage(msg);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}

	}
}

class LogTemp {
	public String logContext = "";
	public String logTag = "";
}
