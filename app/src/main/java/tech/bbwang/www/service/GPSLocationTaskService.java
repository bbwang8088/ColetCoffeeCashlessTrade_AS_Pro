package tech.bbwang.www.service;

import tech.bbwang.www.activity.ColetApplication;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.baidu.location.LocationClient;

public class GPSLocationTaskService extends Service {

	private boolean serviceRunning = false;
	// private LocationManager locationManager;

	public GPSLocationTaskService(){
		
	}
	// 必须实现的方法，用于返回Binder对象
	@Override
	public IBinder onBind(Intent intent) {
		System.out.println("--onBind()--");
		return new MyBinder();
	}

	public class MyBinder extends Binder {
		public GPSLocationTaskService getService() {
			return GPSLocationTaskService.this;
		}

		public void setData(String data) {
			// DialyTaskService.this.data = data;
		}
	}

	// 创建Service时调用该方法，只调用一次
	@Override
	public void onCreate() {
		super.onCreate();
		ColetApplication.getApp().logDebug(
				this.getClass().getName() + " onCreate");
		// locationManager = (LocationManager)
		// getSystemService(Context.LOCATION_SERVICE);

		serviceRunning = true;
		new Thread() {
			@Override
			public void run() {
				while (serviceRunning) {
					try {
						Thread.sleep(1000 * 5 * 1);
					} catch (InterruptedException e) {
						ColetApplication.getApp().logDebug(
								this.getClass().getName()
										+ " InterruptedException:"
										+ e.getMessage());
					}
					if (ColetApplication.getApp().isLegalTerminal() == false) {
						ColetApplication.getApp().logDebug(
								"终端未取得激活状态,不可获取GPS坐标信息");
						continue;
					}
//					LocationClient mLocationClient = ColetApplication.getApp().getLocation();
//					if (mLocationClient != null && mLocationClient.isStarted())
//						mLocationClient.requestLocation();
				}

				// getGPSLocation();
			};
		}.start();

		// getGPSLocation();
	}

	
	// private void getGPSLocation() {
	// if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
	// getLocation();
	// //gps已打开
	// } else {
	// toggleGPS();
	// new Handler(Looper.getMainLooper()) {}.postDelayed(new Runnable() {
	// @Override
	// public void run() {
	// getLocation();
	// }
	// }, 2000);
	//
	// }
	// }
	//
	// private void toggleGPS() {
	// Intent gpsIntent = new Intent();
	// gpsIntent.setClassName("com.android.settings",
	// "com.android.settings.widget.SettingsAppWidgetProvider");
	// gpsIntent.addCategory("android.intent.category.ALTERNATIVE");
	// gpsIntent.setData(Uri.parse("custom:3"));
	// try {
	// PendingIntent.getBroadcast(this, 0, gpsIntent, 0).send();
	// } catch (PendingIntent.CanceledException e) {
	// e.printStackTrace();
	// locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
	// 1000, 1, locationListener);
	// Location location1 =
	// locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
	// if (location1 != null) {
	// latitude = location1.getLatitude(); // 经度
	// longitude = location1.getLongitude(); // 纬度
	// ColetApplication.getApp().getConfigFile().setLatitude(String.valueOf(latitude));
	// ColetApplication.getApp().getConfigFile().setLongitude(String.valueOf(longitude));
	// ColetApplication.getApp().logDebug("获取坐标数据 : Lat: " + latitude + " Lng: "
	// + longitude);
	// }
	// }
	// }
	//
	// private void getLocation() {
	// List<String> providers = locationManager.getProviders(true);
	// String locationProvider = "";
	// if(providers.contains(LocationManager.GPS_PROVIDER)){
	// //如果是GPS
	// ColetApplication.getApp().logDebug("开始采用GPS定位");
	// locationProvider = LocationManager.GPS_PROVIDER;
	// }else if(providers.contains(LocationManager.NETWORK_PROVIDER)){
	// //如果是Network
	// ColetApplication.getApp().logDebug("开始采用AGPS定位");
	// locationProvider = LocationManager.NETWORK_PROVIDER;
	// }else if(providers.contains(LocationManager.PASSIVE_PROVIDER)){
	// //如果是Network
	// ColetApplication.getApp().logDebug("开始采用无线WIFI定位");
	// locationProvider = LocationManager.PASSIVE_PROVIDER;
	// }else{
	// ColetApplication.getApp().logDebug("无法获取位置提供器，坐标获取失败");
	// return ;
	// }
	//
	// Location location =
	// locationManager.getLastKnownLocation(locationProvider);
	// if (location != null) {
	// latitude = location.getLatitude();
	// longitude = location.getLongitude();
	// ColetApplication.getApp().getConfigFile().setLatitude(String.valueOf(latitude));
	// ColetApplication.getApp().getConfigFile().setLongitude(String.valueOf(longitude));
	// ColetApplication.getApp().logDebug("获取坐标数据 : Lat: " +
	// location.getLatitude() + " Lng: " + location.getLongitude());
	// } else {
	//
	// locationManager.requestLocationUpdates(locationProvider, 1000, 1,
	// locationListener);
	// }
	// }
	//
	// LocationListener locationListener = new LocationListener() {
	// // Provider的状态在可用、暂时不可用和无服务三个状态直接切换时触发此函数
	// @Override
	// public void onStatusChanged(String provider, int status, Bundle extras) {
	// }
	//
	// // Provider被enable时触发此函数，比如GPS被打开
	// @Override
	// public void onProviderEnabled(String provider) {
	// }
	//
	// // Provider被disable时触发此函数，比如GPS被关闭
	// @Override
	// public void onProviderDisabled(String provider) {
	// }
	//
	// // 当坐标改变时触发此函数，如果Provider传进相同的坐标，它就不会被触发
	// @Override
	// public void onLocationChanged(Location location) {
	// if (location != null) {
	// latitude = location.getLatitude(); // 经度
	// longitude = location.getLongitude(); // 纬度
	// ColetApplication.getApp().getConfigFile().setLatitude(String.valueOf(latitude));
	// ColetApplication.getApp().getConfigFile().setLongitude(String.valueOf(longitude));
	// ColetApplication.getApp().logDebug("获取坐标数据 : Lat: " +
	// location.getLatitude() + " Lng: " + location.getLongitude());
	//
	// }else{
	// ColetApplication.getApp().logError("获取坐标失败!");
	// }
	// }
	// };
	//
	//
	//
	// // 打开和关闭gps第二种方法
	// private void openGPSSettings() {
	// //获取GPS现在的状态（打开或是关闭状态）
	// boolean gpsEnabled =
	// Settings.Secure.isLocationProviderEnabled(getContentResolver(),
	// LocationManager.GPS_PROVIDER);
	// if (gpsEnabled) {
	// //关闭GPS
	// Settings.Secure.setLocationProviderEnabled(getContentResolver(),
	// LocationManager.GPS_PROVIDER, false);
	// } else {
	// //打开GPS
	// Settings.Secure.setLocationProviderEnabled(getContentResolver(),
	// LocationManager.GPS_PROVIDER, true);
	// }
	// }

	// 每次启动Servcie时都会调用该方法
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		ColetApplication.getApp().logDebug(
				this.getClass().getName() + " onStartCommand");
		// data = intent.getStringExtra("data");
		flags = START_STICKY;
		return super.onStartCommand(intent, flags, startId);
	}

	// 解绑Servcie调用该方法
	@Override
	public boolean onUnbind(Intent intent) {
		ColetApplication.getApp().logDebug(
				this.getClass().getName() + " onUnbind");
		return super.onUnbind(intent);
	}

	// 退出或者销毁时调用该方法
	@Override
	public void onDestroy() {
		serviceRunning = false;
		ColetApplication.getApp().logDebug(
				this.getClass().getName() + " onDestroy");
		super.onDestroy();
	}

}
