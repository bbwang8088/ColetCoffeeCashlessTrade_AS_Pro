package tech.bbwang.www.downloader;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

import tech.bbwang.www.activity.ColetApplication;
import tech.bbwang.www.util.GsonUtil;

public class DownloadTask implements Callable<Integer> {

	private DownloadFile downloadFile = null;// 下载目标文件
	private RandomAccessFile targetFile = null;// 保存目标文件
	private CountDownLatch countDownLatch = null;// 线程同步器

	// private String downloadFileName = "";
	// private String downladFileUrl = "";

	private long tskId = -1;// 当前任务的编号
	private long startPos;// 当前任务的开始位置
	private long endPos;// 当前任务的结束位置

	private long totalSize;// 总的长度
	private int process = 0;// 下载进度
	private int processSize = 0;// 下载进度
	private long costTime = 0;// 消耗时间(毫秒)

	private int READ_BUFFER_SIZE = 1024 * 20;

	// private int TMP_BUFFER_SIZE = 1024 * 1024 * 24;// 1M

	private HttpURLConnection httpConn = null;
	private InputStream is = null;

	public DownloadTask(long tskid, long startPos, long endPos, DownloadFile downloadFile, RandomAccessFile targetFile, CountDownLatch countDownLatch) {

		this.tskId = tskid;
		this.startPos = startPos;
		this.endPos = endPos;
		this.downloadFile = downloadFile;
		this.targetFile = targetFile;
		this.countDownLatch = countDownLatch;
		this.totalSize = (this.endPos - this.startPos);

		this.startPos = this.startPos > 0 ? this.startPos : 0;
		this.endPos = this.endPos > 0 ? this.endPos : 0;
		this.totalSize = this.totalSize > 0 ? this.totalSize : 0;
	}

	@Override
	public Integer call() {

		long beforeTimer = 0, afterTimer = 0;
		try {
			beforeTimer = System.currentTimeMillis();
			this.is = this.getInputStream();
			this.targetFile.seek(this.startPos);

			byte[] by = new byte[READ_BUFFER_SIZE];
			int length = 0;
			processSize = 0;
			while (-1 != (length = this.is.read(by))) {

				processSize += length;
				this.targetFile.write(by, 0, length);
				this.process = (int) Math.round((((double) (processSize)) / (double) (totalSize * 1.0f)) * 100);
				// System.out.println(this.toString());
				afterTimer = System.currentTimeMillis();
				this.costTime = (afterTimer - beforeTimer);
			}

			this.process = 100;
			this.is.close();
			this.countDownLatch.countDown();
			// System.out.println(this.toString2());
//			ColetApplication.getApp().logDebug( this.toString2());
			afterTimer = System.currentTimeMillis();
			this.costTime = (afterTimer - beforeTimer);
			ColetApplication.getApp().logDebug("文件  "+this.downloadFile.getName()+this.downloadFile.getSuffix()+" 下载完毕,耗时  "+this.costTime+" 毫秒.");
		} catch (Exception ex) {
			ColetApplication.getApp().logError( "文件下载发生异常:" + ex.getMessage());
			ColetApplication.getApp().logError( this.toString());
			// System.out.println("Download eccoued exception:" +
			// ex.getMessage());
			// System.out.println(this.toString());
		} finally {
			this.httpConn.disconnect();
		}

		return (int) this.costTime;
	}

	/**
	 * 获取输入流
	 * 
	 * @return
	 */
	private InputStream getInputStream() {
		InputStream is = null;
		try {
			String tmpUrl = this.downloadFile.getUrl();
			if (tmpUrl != null && !"".equals(tmpUrl)) {
				URL url = new URL(tmpUrl);
				httpConn = (HttpURLConnection) url.openConnection();
				httpConn.setRequestMethod("GET"); // 以GET方式连接
				httpConn.setRequestProperty("Connection", "Keep-Alive"); // 保持一直连接
				httpConn.setConnectTimeout(60 * 1000 * 5); // 连接超时5分钟
				httpConn.setAllowUserInteraction(true);
				httpConn.setRequestProperty("Range", "bytes=" + this.startPos + "-" + this.endPos);// 指定读取的范围
				is = new BufferedInputStream(httpConn.getInputStream(), READ_BUFFER_SIZE);
			}
		} catch (Exception ex) {
			ColetApplication.getApp().logError( "文件下载发生异常:" + ex.getMessage());
			ColetApplication.getApp().logError( this.toString());
			// System.out.println("Download eccoued exception:" +
			// ex.getMessage());
			// System.out.println(this.toString());
		}
		return is;
	}

	public String toString2() {

		if (this.downloadFile == null) {
			return "No target download file!";
		}

		String tmp = System.getProperty("line.separator");
		String str = "";
		str = tmp + "File name: " + this.downloadFile.getName() + "." + this.downloadFile.getSuffix() + "  File url: " + this.downloadFile.getUrl()
				+ "  Start pos: " + this.startPos + " End pos: " + this.endPos + "  Process size: " + this.processSize + "  Process: " + this.process
				+ "%" + "  Cost time: " + this.costTime + "ms  Task id: " + this.tskId;
		return str;
	}

	public String toString() {

		if (this.downloadFile == null) {
			return "No target download file!";
		}

		return GsonUtil.gson.toJson(this);
	}

	public long getTskId() {
		return tskId;
	}

}
