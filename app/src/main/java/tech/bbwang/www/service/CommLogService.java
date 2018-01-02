package tech.bbwang.www.service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import tech.bbwang.www.activity.ColetApplication;
import tech.bbwang.www.util.DateUtil;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import de.mindpipe.android.logging.log4j.LogConfigurator;

public class CommLogService extends Service {

	final LogConfigurator logConfigurator = new LogConfigurator();
	private Logger gLogger = null;

	private boolean serviceRunning = false;
	List<String> logList = null;

	// 创建Service时调用该方法，只调用一次
	@Override
	public void onCreate() {
		super.onCreate();
		logList = new ArrayList<String>(10000);
		configlogger2File();
		ColetApplication.getApp().logDebug(this.getClass().getName() + " onCreate");
		serviceRunning = true;
		new Thread() {
			@Override
			public void run() {
				while (serviceRunning) {
					if (logList.size() > 0) {
						log2File(logList.remove(0));
					}
				}
			};
		}.start();
	}

	public void configlogger2File() {

		String time = DateUtil.getDateTime(DateUtil.sdf_yyyy_MM_HH);
		logConfigurator.setFileName(Environment.getExternalStorageDirectory() + File.separator + "ColetCoffee" + File.separator + "logs" + "_" + time
				+ "_.log");
		// Set the root log level
		logConfigurator.setRootLevel(Level.DEBUG);
		// Set log level of a specific logger
		logConfigurator.setLevel("org.apache", Level.ERROR);
		logConfigurator.configure();
		logConfigurator.setMaxBackupSize(30);
		// gLogger = Logger.getLogger(this.getClass());
		gLogger = Logger.getLogger("Log");
	}

	public void log2File(String message) {
		if (this.gLogger != null) {
			this.gLogger.debug(message);
		}
	}

	// 必须实现的方法，用于返回Binder对象
	@Override
	public IBinder onBind(Intent intent) {
		ColetApplication.getApp().logDebug(this.getClass().getName() + " onBind");
		return new MyBinder();
	}

	public class MyBinder extends Binder {
		public CommLogService getService() {
			return CommLogService.this;
		}

		public void setData(String data) {
		}
	}

	// 每次启动Servcie时都会调用该方法
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		ColetApplication.getApp().logDebug(this.getClass().getName() + " onStartCommand");
		return super.onStartCommand(intent, flags, startId);
	}

	// 解绑Servcie调用该方法
	@Override
	public boolean onUnbind(Intent intent) {
		ColetApplication.getApp().logDebug(this.getClass().getName() + " onUnbind");
		return super.onUnbind(intent);
	}

	// 退出或者销毁时调用该方法
	@Override
	public void onDestroy() {
		serviceRunning = false;
		ColetApplication.getApp().logDebug(this.getClass().getName() + " onDestroy");
		super.onDestroy();
	}

	DataCallback dataCallback = null;

	public DataCallback getDataCallback() {
		return dataCallback;
	}

	public void setDataCallback(DataCallback dataCallback) {
		this.dataCallback = dataCallback;
	}

	public void removeDataCallback() {
		this.dataCallback = null;
	}

	// 通过回调机制，将Service内部的变化传递到外部
	public interface DataCallback {
		void dataChanged(String str);
	}
}
