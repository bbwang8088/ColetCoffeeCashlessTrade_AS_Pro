package tech.bbwang.www.downloader;

/**
 * 下载对象
 * 
 * @author bbwang8088@126.com
 * 
 */
public class DownloadFile {

	private String url = "";
	private String suffix = "";
	private String name = "";
//	private int process = 0;
//	private long costTime = 0;// 毫秒

	public DownloadFile(String url, String suffix, String name) {
		super();
		this.url = url;
		this.suffix = suffix;
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getSuffix() {
		return suffix;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

//	public int getProcess() {
//		return process;
//	}
//
//	public void setProcess(int process) {
//		this.process = process;
//	}
//
//	public long getCostTime() {
//		return costTime;
//	}
//
//	public void setCostTime(long costTime) {
//		this.costTime = costTime;
//	}

}
