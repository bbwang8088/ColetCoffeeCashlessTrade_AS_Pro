package tech.bbwang.www.downloader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;

import java.util.concurrent.FutureTask;

import tech.bbwang.www.activity.ColetApplication;
import tech.bbwang.www.util.ThreadPool;

public class DownloadManager {

	private CountDownLatch countDownLatch = null;

	private List<DownloadFile> waitList = null;
	Map<Long, FutureTask<Integer>> processTaskMap = null;
	private String targetPath = "";

	public DownloadManager(List<DownloadFile> downloadFileList, String targetPath) {

		waitList = downloadFileList;
		this.targetPath = targetPath;
	}

	/**
	 * 总的进度百分比.
	 * 
	 * @return
	 */
	public int getTotalProcess() {
		int finishedSize = 0;
		if (null != this.processTaskMap && this.processTaskMap.size() > 0) {
			for (FutureTask<Integer> tsk : this.processTaskMap.values()) {
				try {
					if (tsk.get() == 100) {
						finishedSize++;
					}
				} catch (InterruptedException e) {
				} catch (ExecutionException e) {
				}
			}
		}
		return finishedSize * ((int) 100) / this.waitList.size();

	}

	//
	// /**
	// * 返回未完成下载的对象列表
	// *
	// * @return
	// */
	// public List<DownloadFile> getProcessingList() {
	// List<DownloadFile> twaitList = new ArrayList<DownloadFile>();
	// if (null != this.processTaskMap && this.processTaskMap.size() > 0) {
	//
	// }
	//
	// return twaitList;
	// }

	// /**
	// * 返回等待下载的对象列表
	// *
	// * @return
	// */
	// public String[] getWaitNameList() {
	//
	// String[] nameList = new String[this.waitList.size()];
	// for (int i = 0; i < this.waitList.size(); i++) {
	// DownloadFile tmp = this.waitList.get(i);
	// nameList[i] = tmp.getName() + File.separator + tmp.getSuffix();
	// }
	// return nameList;
	// }
	//
	/**
	 * 返回等待下载的对象URL列表
	 * 
	 * @return
	 */
	public String[] getURLList() {

		String[] urlList = new String[this.waitList.size()];
		for (int i = 0; i < this.waitList.size(); i++) {
			DownloadFile tmp = this.waitList.get(i);
			urlList[i] = tmp.getUrl();
		}
		return urlList;
	}

	//
	// /**
	// * 返回已经完成下载的对象列表
	// *
	// * @return
	// */
	// public List<DownloadFile> getFinishedList() {
	// return this.finishedList;
	// }
	//
	// /**
	// * 返回已经完成下载的对象列表
	// *
	// * @return
	// */
	// public String[] getFinishedNameList() {
	// String[] nameList = new String[this.finishedList.size()];
	// for (int i = 0; i < this.finishedList.size(); i++) {
	// nameList[i] = this.finishedList.get(i).getUrl();
	// }
	// return nameList;
	// }

	
	private void checkFloder(){
		File f = new File( this.targetPath );
		if( f.exists() == false ){
//			ColetApplication.getApp().logDebug("Floder not exist: "+this.targetPath);
			if(f.mkdirs() == true ){
//				ColetApplication.getApp().logDebug("Create floder: "+this.targetPath);
			}
		}else{
//			ColetApplication.getApp().logDebug("Floder exist: "+this.targetPath);
		}
	}
	
	/**
	 * 开始下载文件
	 */
	public void start() {

		if (DownloadUtil.getURLTotalSizeAsync(this.getURLList()) >= DownloadUtil.getSDFreeSizeByte()) {
//			System.out.println("Target disk driver space is not enough!" + System.getProperty("line.separator") + "Download is cancel!");
			ColetApplication.getApp().logDebug("Target disk driver space is not enough!" + System.getProperty("line.separator") + "Download is cancel!" );
		} else 
		{
			checkFloder();
			this.processTaskMap = new HashMap<Long, FutureTask<Integer>>(this.waitList.size());
			this.countDownLatch = new CountDownLatch(this.waitList.size());
			try {
				for (int i = 0; i < this.waitList.size(); i++) {
					DownloadFile obj = this.waitList.get(i);
					FutureTask<Integer> mtask = new FutureTask<Integer>(new SizeMeasurer(obj.getUrl()));
					ThreadPool.service.submit(mtask);//DownloadUtil.getURLSize(obj.getUrl());
					int tmpSize = mtask.get();
					
					ColetApplication.getApp().logDebug(obj.getName() + "." + obj.getSuffix()+"的尺寸为"+(tmpSize)+"bytes");
					// 创建临时文件
					RandomAccessFile rac = new RandomAccessFile(new File(this.targetPath, obj.getName() + "." + obj.getSuffix()), "rwd");
					// 立即设置最大size,占据空间,以防空间不足
					rac.setLength(tmpSize);

					DownloadTask ttsk = new DownloadTask(i, 0, tmpSize, obj, rac, this.countDownLatch);
					FutureTask<Integer> futureTask = new FutureTask<Integer>(ttsk);
					this.processTaskMap.put(ttsk.getTskId(), futureTask);
					ThreadPool.service.submit(futureTask);
				}
				this.countDownLatch.await();
			} catch (FileNotFoundException e) {
				ColetApplication.getApp().logError( e.getMessage());
			} catch (IOException e) {
				ColetApplication.getApp().logError( e.getMessage());
			} catch (InterruptedException e) {
				ColetApplication.getApp().logError( e.getMessage());
			} catch (ExecutionException e) {
				ColetApplication.getApp().logError( e.getMessage());
			}

		}
	}

	/**
	 * 关闭下载,如果已经有文件下载完成则保留
	 */
	public void stop() {
		if (null != this.processTaskMap && this.processTaskMap.size() > 0) {
			for (FutureTask<Integer> tsk : this.processTaskMap.values()) {
				tsk.cancel(true);
			}
		}
	}

	// /**
	// * 暂停下载,支持恢复后可断点下载
	// */
	// public void suspend() {
	//
	// }

	// /**
	// * 重启下载,支持断点下载
	// */
	// public void restart() {
	//
	// }

	// /**
	// * 取消下载,如有下载完成则删除
	// */
	// public void cancel() {
	//
	// }
}
