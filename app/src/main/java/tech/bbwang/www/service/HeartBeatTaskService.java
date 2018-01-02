package tech.bbwang.www.service;

import java.util.Calendar;

import tech.bbwang.www.activity.ColetApplication;
import tech.bbwang.www.util.DateUtil;
import tech.bbwang.www.util.SystemUtil;
import tech.bbwang.www.ws.HeartBeat;
import tech.bbwang.www.ws.WSUtil;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;

public class HeartBeatTaskService extends Service {
	
	private boolean serviceRunning = false;

	// 必须实现的方法，用于返回Binder对象
	@Override
	public IBinder onBind(Intent intent) {
		System.out.println("--onBind()--");
		return new MyBinder();
	}

	public class MyBinder extends Binder {
		public HeartBeatTaskService getService() {
			return HeartBeatTaskService.this;
		}

		public void setData(String data) {
			// DialyTaskService.this.data = data;
		}
	}

	// 创建Service时调用该方法，只调用一次
	@Override
	public void onCreate() {
		super.onCreate();
		ColetApplication.getApp().logDebug(this.getClass().getName() + " onCreate");
		serviceRunning = true;
		new Thread() {
			@Override
			public void run() {
				while (serviceRunning) {
					
					try {
						Thread.sleep(1000 * 60 * 1);
					} catch (InterruptedException e) {
						ColetApplication.getApp().logDebug(this.getClass().getName() + " InterruptedException:" + e.getMessage());
					}
					if (ColetApplication.getApp().isLegalTerminal() == false) {
						ColetApplication.getApp().logDebug("终端未取得激活状态,不可心跳");
						continue;
					}
					
					doHeartBeat();
				}
			};
		}.start();
		
		//doHeartBeat();
	}
	
	private void doHeartBeat(){

		Calendar c = Calendar.getInstance();
		int miniute = c.get(Calendar.MINUTE);
		
		String heartbeatPluse = ColetApplication.getApp().getConfigFile().getHeartbeatPluse();
		int tmp = Integer.valueOf(heartbeatPluse);
		
		if ((miniute % tmp == 0)) {
			String franchise = ColetApplication.getApp().getConfigFile().getFranchiseId();
			String terminal_code = SystemUtil.getIMEI(HeartBeatTaskService.this);
			String app_ver = SystemUtil.getVersionName(HeartBeatTaskService.this);
			String ad_ver = ColetApplication.getApp().getConfigFile().getLastAdVersion();
			String menu_ver = ColetApplication.getApp().getConfigFile().getLastMenuVersion();
			String server_url = ColetApplication.getApp().getConfigFile().getApiUrl();
			String terminal_time = DateUtil.getDateTime(DateUtil.sdf_yyyyMMddHHmmss);
			String la = ColetApplication.getApp().getConfigFile().getLatitude();
			String lon = ColetApplication.getApp().getConfigFile().getLongitude();
			HeartBeat hb = WSUtil.getInstance().heartbeat(franchise, terminal_code, ad_ver, menu_ver, terminal_time, app_ver, heartbeatPluse,
					server_url, la,lon,0);
			
			if( hb.getStatus() != -1){
				Message msg = new Message();
				msg.what = ColetApplication.ERROR_STATUS;
				Bundle data = new Bundle();
				data.putInt("status", hb.getStatus());
				data.putString("message", hb.getMessage());
				msg.setData(data);
				ColetApplication.messageHandler.sendMessage(msg);
			}
		}
	}

	// 每次启动Servcie时都会调用该方法
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		ColetApplication.getApp().logDebug(this.getClass().getName() + " onStartCommand");
		// data = intent.getStringExtra("data");
		flags = START_STICKY;
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

}
