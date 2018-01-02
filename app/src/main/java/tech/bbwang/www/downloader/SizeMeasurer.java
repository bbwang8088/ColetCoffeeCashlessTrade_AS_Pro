package tech.bbwang.www.downloader;

import java.util.concurrent.Callable;

public class SizeMeasurer implements Callable<Integer> {

	String[] urlArray = null;

	public SizeMeasurer(String[] array) {
		this.urlArray = array;
	}

	public SizeMeasurer(String array) {
		this.urlArray = new String[1];
		this.urlArray[0] = array;
	}
	
	@Override
	public Integer call() throws Exception {
		return (int) DownloadUtil.getURLTotalSize(urlArray);
	}

}
