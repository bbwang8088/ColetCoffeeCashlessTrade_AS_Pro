package tech.bbwang.www.downloader;

import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import tech.bbwang.www.activity.ColetApplication;
import tech.bbwang.www.util.ThreadPool;
import android.os.Environment;
import android.os.StatFs;

public class DownloadUtil {

	// public static final String TAG = "tech.bbwang.www.downloader";

	public static long getURLTotalSizeAsync(String[] urlArray) {
		long retValue = 0;
		for (String s : urlArray) {
			FutureTask<Integer> mtask = new FutureTask<Integer>(new SizeMeasurer(s));

			long tmp;
			try {
				ThreadPool.service.submit(mtask);
				tmp = mtask.get();
				if (tmp < 0) {
					return 0;
				} else {
					retValue += tmp;
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}

		}

		ColetApplication.getApp().logDebug("Total size: " + retValue + " bytes.");
		return retValue;

	}

	public static long getURLTotalSize(String[] urlArray) {
		long retValue = 0;
		for (String s : urlArray) {

			long tmp = getURLSize(s);
			if (tmp < 0) {
				return 0;
			} else {
				retValue += tmp;
			}
		}

//		ColetApplication.getApp().logDebug("Total size: " + retValue + " bytes.");
		return retValue;
	}

	/**
	 * 获取URL下载对象的大小 注意:HttpURLConnection的getContentLength最大可获取2G的文件大小,超过返回-1
	 * 
	 * @param fileURL
	 * @return
	 */
	public static int getURLSize(String fileURL) {
		HttpURLConnection httpConn = null;
		if (null == fileURL || fileURL.equals("")) {
			ColetApplication.getApp().logDebug("URL is null or empty string, stopping get size, return 0.");
			return 0;
		}
		try {
			// ColetApplication.getApp().logDebug("Checking url file size..., url: "+fileURL);
			URL url = new URL(fileURL);
			httpConn = (HttpURLConnection) url.openConnection();
			// 以GET方式连接
			httpConn.setRequestMethod("GET");
			httpConn.setRequestProperty("Accept-Encoding", "identity");
			if (httpConn.getResponseCode() == HttpURLConnection.HTTP_OK) {
				int t = httpConn.getContentLength();
				t = t > 0 ? t : 0;
				// ColetApplication.getApp().logDebug("HTTP status is ok. File size: "+t+" bytes.");
				return t;
			} else {
				ColetApplication.getApp().logDebug("HTTP status is not ok. Cheching is stopped.");
			}
		} catch (Exception ex) {
			// 得到异常棧的首个元素
			StackTraceElement stackTraceElement = ex.getStackTrace()[0];
			ColetApplication.getApp().logError(
					"File=" + stackTraceElement.getFileName() + "\r\nLine=" + stackTraceElement.getLineNumber() + "\r\nMethod="
							+ stackTraceElement.getMethodName());
			return -2;
		} finally {
			httpConn.disconnect();
		}
		return -1;
	}

	// /**
	// * Windows下使用
	// * @param path
	// * @return
	// */
	// public static long getDriverFreeSize(String path) {
	// File diskPartition = new File(path);
	// long freePartitionSpace = diskPartition.getFreeSpace();
	// return freePartitionSpace;
	// }

	/**
	 * 检测是否存在SD卡
	 * 
	 * @return
	 */
	public static boolean ExistSDCard() {
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			// ColetApplication.getApp().logDebug("Checking sd card exist..., result: exist!");
			return true;
		} else {
			ColetApplication.getApp().logDebug("Checking sd card exist..., result: not exist!");
			return false;
		}
	}

	/**
	 * 返回以byte为单位的SD卡空闲大小
	 * 
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static long getSDFreeSizeByte() {
		// 取得SD卡文件路径
		File path = Environment.getExternalStorageDirectory();
		StatFs sf = new StatFs(path.getPath());
		// 获取单个数据块的大小(Byte)
		long blockSize = sf.getBlockSize();
		// 空闲的数据块的数量
		long freeBlocks = sf.getAvailableBlocks();

		// ColetApplication.getApp().logDebug(
		// "Checking sd card size..., blockSize: " + blockSize + ", freeBlocks:"
		// + freeBlocks + ", total:" + (freeBlocks * blockSize));
		// 返回SD卡空闲大小
		return freeBlocks * blockSize; // 单位Byte
	}

	/**
	 * 返回以KB为单位的SD卡空闲大小
	 * 
	 * @return
	 */
	public static long getSDFreeSizeKByte() {
		return getSDFreeSizeByte() / 1024; // 单位KB
	}

	/**
	 * 返回以MB为单位的SD卡空闲大小
	 * 
	 * @return
	 */
	public static long getSDFreeSizeMByte() {
		return getSDFreeSizeByte() / 1024 / 1024; // 单位MB
	}

	/**
	 * 获取内置SD卡路径
	 * 
	 * @return
	 */
	public static String getInnerSDCrad() {
		return Environment.getExternalStorageDirectory().getPath() + "/ColetCoffee/";
	}
}
