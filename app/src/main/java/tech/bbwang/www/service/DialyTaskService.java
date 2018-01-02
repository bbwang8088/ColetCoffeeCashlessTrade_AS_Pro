package tech.bbwang.www.service;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import tech.bbwang.www.activity.ColetApplication;
import tech.bbwang.www.downloader.DownloadFile;
import tech.bbwang.www.downloader.DownloadManager;
import tech.bbwang.www.downloader.DownloadUtil;
import tech.bbwang.www.sqlite.DBMenu;
import tech.bbwang.www.sqlite.PrePayCard;
import tech.bbwang.www.util.ConfigFileUtil;
import tech.bbwang.www.util.SystemUtil;
import tech.bbwang.www.ws.ChargeCardList;
import tech.bbwang.www.ws.DataPrePayCardInfo;
import tech.bbwang.www.ws.Good;
import tech.bbwang.www.ws.MenuDetail;
import tech.bbwang.www.ws.Menu_Category;
import tech.bbwang.www.ws.UpdateInfo;
import tech.bbwang.www.ws.WSUtil;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class DialyTaskService extends Service {
	// private String data = "default message";
	private boolean serviceRunning = false;

	// 必须实现的方法，用于返回Binder对象
	@Override
	public IBinder onBind(Intent intent) {
		System.out.println("--onBind()--");
		return new MyBinder();
	}

	public class MyBinder extends Binder {
		public DialyTaskService getService() {
			return DialyTaskService.this;
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
						sleep(1000 * 60 * 1);
					} catch (InterruptedException e) {
						ColetApplication.getApp().logDebug(this.getClass().getName() + " InterruptedException:" + e.getMessage());
					}
					if (ColetApplication.getApp().isLegalTerminal() == false) {
						ColetApplication.getApp().logDebug("终端未取得激活状态,不可更新广告和菜单");
						continue;
					}
					Calendar c = Calendar.getInstance();
//					int hour = c.get(Calendar.HOUR_OF_DAY);
					int miniute = c.get(Calendar.MINUTE);
					// 时间处于2点至3点之间时发出更新通知
					// if (hour >= 2 && hour <= 3) {
					String heartbeatPluse = ColetApplication.getApp().getConfigFile().getHeartbeatPluse();
					int tmp = Integer.valueOf(heartbeatPluse);
					
					if ((miniute % tmp == 0)) {
						checkUpdate();
					}
					// }
				}
			};
		}.start();
		// checkUpdate();
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

	private void checkUpdate() {

		WSUtil ws = WSUtil.getInstance();
		ColetApplication app = ColetApplication.getApp();
		ConfigFileUtil confileFile = app.getConfigFile();
		String franchiseId = confileFile.getFranchiseId();
		String vipCardLisCode = confileFile.getVipCardListCode();
		
		if (this.dataCallback != null)
			this.dataCallback.dataChanged("检查储值卡列表、菜单版本信息");

		app.logDebug("------------------------------------------------更新任务启动------------------------------------------------");
		app.logDebug("尝试取得版本更新基础信息...");
		UpdateInfo updateInfo = ws.getUpdateInfo(franchiseId, SystemUtil.getIMEI(this), 0);
		String uptMuver = updateInfo.getData().getLast_menu_version();
		String uptVipCardVer = updateInfo.getData().getLast_charge_version();

		if (updateInfo.getStatus() == 0) {
			app.logDebug("版本更新基础信息取得成功:" + updateInfo.toString());
		} else {
			app.logDebug("版本更新基础信息取得失败,取消更新.");
			return;
		}

		app.logDebug("------------------------------------------------储值卡列表版本对比------------------------------------------------");
		app.logDebug("服务器储值卡列表最新版本号:" + uptVipCardVer + "  <->  本地储值卡列表最新版本号:" + vipCardLisCode);
		if (vipCardLisCode.equals(uptVipCardVer) == false) {
			app.logDebug("服务器储值卡列表版本与本地储值卡列表版本不一致,准备更新储值卡列表信息...");
			app.logDebug("尝试取得版本" + uptVipCardVer + "储值卡列表更新详细信息...");
			ChargeCardList vipCardList = ws.cardChargeList(franchiseId, SystemUtil.getIMEI(this));
			List<PrePayCard> cards = new ArrayList<PrePayCard>();
			for(DataPrePayCardInfo d: vipCardList.getData()){
				//String versionCode, String title, String list_price,String fresh_price, String vip_price, int index
				cards.add(new PrePayCard(uptVipCardVer,d.getTitle(),d.getList_price(),d.getFresh_price(),d.getVip_price(),Integer.valueOf(d.getIndex())));
			}
			boolean ret = ColetApplication.getApp().getprePaycardDao().insert(cards);
			if( ret == true ){
				confileFile.setVipCardListCode(uptVipCardVer);
				app.logDebug("版本" + uptVipCardVer + "储值卡列表信息更新成功");
			}else{
				app.logDebug("版本" + uptVipCardVer + "储值卡列表信息更新失败");
			}
		}else{
			app.logDebug("服务器储值卡列表版与本地储值卡列表版本相同,取消更新储值卡列表信息.");
		}
		
		List<DownloadFile> urlList = new ArrayList<DownloadFile>();
		long beforeTimer = 0, afterTimer = 0;
		String path = "";

//		app.logDebug("------------------------------------------------广告版本对比------------------------------------------------");
//		DBAd ad = (DBAd) app.getAddao().getLastAd();
//		app.logDebug("服务器广告最新版本号:" + uptAdVer + "  <->  本地广告最新版本号:" + ad.getAd_version());
//		if (String.valueOf(ad.getAd_version()).equals(uptAdVer) == false) {
//
//			app.logDebug("服务器广告版本与本地广告版本不一致,准备更新广告信息...");
//			app.logDebug("尝试取得版本" + uptAdVer + "广告更新详细信息...");
//
//			if (this.dataCallback != null)
//				this.dataCallback.dataChanged("广告数据升级开始:" + ad.getAd_version() + " -> " + uptAdVer + "");
//
//			tech.bbwang.www.ws.AdDetail serverAd = ws.getAdDetailInfo(franchiseId, SystemUtil.getIMEI(this), uptAdVer, 0);
//			String lastServerAdVersion = serverAd.getData().getAd_version();
//			if (serverAd.getStatus() == 0) {
//				app.logDebug("版本" + uptAdVer + "广告更新详细信息取得成功:" + serverAd.toString());
//				urlList.clear();
//				for (ADImage d : serverAd.getData().getAd_data_list()) {
//					app.logDebug("广告图片：" + d.getUrl());
//					urlList.add(new DownloadFile(d.getUrl(), SystemUtil.getSuffix(d.getUrl()), lastServerAdVersion
//							+ String.valueOf(d.getSequence_no())));
//				}
//				path = DownloadUtil.getInnerSDCrad() + File.separator + "ad" + File.separator + lastServerAdVersion;
//				DownloadManager dm = new DownloadManager(urlList, path);
//
//				app.logDebug("准备下载广告图片");
//				beforeTimer = System.currentTimeMillis();
//				dm.start();
//				afterTimer = System.currentTimeMillis();
//				app.logDebug("下载广告图片完毕,耗时:" + (afterTimer - beforeTimer) + " 毫秒");
//
//				app.logDebug("保存广告信息至数据库...");
//				if (app.getAddao().insert(serverAd) == true) {
//					confileFile.setLastAdVersion(lastServerAdVersion);
//					app.logDebug("保存广告信息至数据库成功.");
//					ColetApplication.messageHandler.sendEmptyMessage(ColetApplication.UPDATE_AD);
//				} else {
//					app.logDebug("保存广告信息至数据库失败.");
//				}
//
//			} else {
//				app.logDebug("版本" + ad.getAd_version() + "广告更新详细信息取得失败,取消更新.");
//			}
//
//		} else {
//			app.logDebug("服务器广告版与本地广告版本相同,取消更新广告信息.");
//		}

		app.logDebug("------------------------------------------------菜单版本对比------------------------------------------------");
		DBMenu mu = (DBMenu) app.getMdao().getLastMenu();
		app.logDebug("服务器菜单最新版本号:" + uptMuver + "  <->  本地菜单最新版本号:" + mu.getMenu_version());
		if (String.valueOf(mu.getMenu_version()).equals(uptMuver) == false) {
			app.logDebug("服务器菜单版本与本地菜单版本不一致,准备更新菜单信息...");
			app.logDebug("尝试取得版本" + uptMuver + "菜单更新详细信息...");

			if (this.dataCallback != null)
				this.dataCallback.dataChanged("菜单数据升级开始:" + mu.getMenu_version() + " -> " + uptMuver + "");

			MenuDetail serverMu = ws.getMenuDetailInfo(franchiseId, SystemUtil.getIMEI(this), uptMuver, 0);
			if (serverMu.getStatus() == 0) {
				app.logDebug("版本" + uptMuver + "菜单更新详细信息取得成功:" + serverMu.toString());
				urlList.clear();
				for (Menu_Category d : serverMu.getData().getMenu_data_list()) {
					if ((null == d.getCategory_image()) || d.getCategory_image().equals(""))
						d.setCategory_image("http://projectx.abrain.cn/static/upload/20170926083037_867.png");// For
																												// test
//					app.logDebug("品类图片:" + d.getCategory_image());
//					urlList.add(new DownloadFile(d.getCategory_image(), SystemUtil.getSuffix(d.getCategory_image()), serverMu.getData()
//							.getMenu_version()));
					for (Good g : d.getGoods_data_list()) {
						app.logDebug("商品图片：" + g.getImage());
						urlList.add(new DownloadFile(g.getImage(), SystemUtil.getSuffix(g.getImage()), serverMu.getData().getMenu_version()
								+ String.valueOf(g.getSequence_no())));
					}
				}
				path = DownloadUtil.getInnerSDCrad() + File.separator + "menu" + File.separator + serverMu.getData().getMenu_version();
				DownloadManager dm = new DownloadManager(urlList, path);

				app.logDebug("准备下载菜单图片");
				beforeTimer = System.currentTimeMillis();
				dm.start();
				afterTimer = System.currentTimeMillis();
				app.logDebug("下载菜单图片完毕,耗时:" + (afterTimer - beforeTimer) + " 毫秒");

				app.logDebug("保存菜单信息至数据库...");
				if (app.getMdao().insert(serverMu) == true) {
					confileFile.setLastMenuVersion(serverMu.getData().getMenu_version());
					app.logDebug("保存菜单信息至数据库成功.");
					ColetApplication.messageHandler.sendEmptyMessage(ColetApplication.UPDATE_MU);
				} else {
					app.logDebug("保存菜单信息至数据库失败.");
				}
			} else {
				app.logDebug("版本" + mu.getMenu_version() + "菜单更新详细信息取得失败,取消更新.");
			}

		} else {
			app.logDebug("服务器菜单版本与本地菜单版本相同,取消更新菜单信息.");
		}

	}
}
