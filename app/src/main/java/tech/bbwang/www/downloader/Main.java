package tech.bbwang.www.downloader;

import java.util.ArrayList;
import java.util.List;

public class Main {

	public static void main(String[] args) {
		List<DownloadFile> urlList = new ArrayList<DownloadFile>();
		urlList.add(new DownloadFile("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1504871862100&di=8e12986cc53dfbd9733a7326e0f522b9&imgtype=0&src=http%3A%2F%2Fimgsrc.baidu.com%2Fforum%2Fw%253D580%2Fsign%3D99fdcf6f00087bf47dec57e1c2d3575e%2F0981b8cb39dbb6fdd60e15020b24ab18972b371c.jpg", "JPG", "_MG_0345"));
//		urlList.add(new DownloadFile("http://localhost:8080/_MG_0346.JPG", "JPG", "_MG_0346"));
//		urlList.add(new DownloadFile("http://localhost:8080/_MG_0347.JPG", "JPG", "_MG_0347"));
//		urlList.add(new DownloadFile("http://localhost:8080/_MG_0348.JPG", "JPG", "_MG_0348"));
//		urlList.add(new DownloadFile("http://localhost:8080/_MG_0349.JPG", "JPG", "_MG_0349"));
//		urlList.add(new DownloadFile("http://localhost:8080/_MG_0350.JPG", "JPG", "_MG_0350"));
		// DownloadFile dnFile2 = new
		// DownloadFile("http://localhost:8080/_MG_0346.JPG", "JPG",
		// "_MG_0346");
//		ExecutorService service = Executors.newFixedThreadPool(10);
//		CountDownLatch countDownLatch = new CountDownLatch(1);
		long beforeTimer = 0, afterTimer = 0;
		beforeTimer = System.currentTimeMillis();
		
		DownloadManager dm = new DownloadManager(urlList,DownloadUtil.getInnerSDCrad()+"\\BBwangDownloader");
		dm.start();
//		RandomAccessFile rac = null;
//		try {
//
//			int size = DownloadUtil.getURLSize(dnFile.getUrl());
//			rac = new RandomAccessFile(new File("D:\\abc", dnFile.getName() + "." + dnFile.getSuffix()),"rwd");
//			rac.setLength(size);
//			FutureTask<Integer> futureTask = new FutureTask<Integer>(new DownloadTask(10001, 0, size , dnFile, rac, countDownLatch));
//			service.submit(futureTask);
////			service.execute(new DownloadTask(10000, size / 2+1, size, dnFile, new RandomAccessFile(new File("D:\\abc", dnFile.getName() + "."
////					+ dnFile.getSuffix()), "rwd"), countDownLatch));
//			countDownLatch.await();
//			// randomAccessFile.close();
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//
//		service.shutdown();
		
		
		afterTimer = System.currentTimeMillis();

		System.out.println("--------------------------" + (afterTimer - beforeTimer) / 1000.0f + "s-------------------------");
	}

}
