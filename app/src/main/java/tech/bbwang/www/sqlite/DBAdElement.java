package tech.bbwang.www.sqlite;

import tech.bbwang.www.util.GsonUtil;

/**
 * 广告素材
 * 
 * @author bbwang8088@126.com
 * 
 */
public class DBAdElement {

	private int primary_id = 0;
	private String adver = "0";
	private int sequence_no = 0;
	private String type = "";
	private String description = "";
	private int height = 0;
	private int width = 0;
	private int size = 0;
	private String url = "";

	public DBAdElement() {
		super();
	}

	//id, width, height, seqno, size, description, adver, url, type
	public DBAdElement(int primary_id, int width,  int height,int sequence_no, int size, String description,String adver,  String url ,String type) {
		super();
		this.primary_id = primary_id;
		this.adver = adver;
		this.sequence_no = sequence_no;
		this.type = type;
		this.description = description;
		this.height = height;
		this.width = width;
		this.size = size;
		this.url = url;
	}

	public int getPrimary_id() {
		return primary_id;
	}

	public void setPrimary_id(int primary_id) {
		this.primary_id = primary_id;
	}

	public String getAdver() {
		return adver;
	}

	public void setAdver(String adver) {
		this.adver = adver;
	}

	public int getSequence_no() {
		return sequence_no;
	}

	public void setSequence_no(int sequence_no) {
		this.sequence_no = sequence_no;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String toString() {
		return GsonUtil.gson.toJson(this);
	}
}
