package tech.bbwang.www.sqlite;

import tech.bbwang.www.util.GsonUtil;

/**
 * 广告素材
 * 
 * @author bbwang8088@126.com
 * 
 */
public class DBGood {

	private int primary_id = 0;
	private String menuVer = "0";
	private String category = "";
	private String name = "";
	private int listPrice = 0;
	private String image = "";
	private int promPrice = 0;
	private String promType = "";
	private String categoryImage = "";
	private String sequenceNo = "";
	
	public DBGood() {
		super();
	}

	public DBGood(int primary_id, String menuVer, int listPrice, String category,
			String name, int promPrice, String promType, String image,String categoryImage,String sequenceNo) {
		super();
		this.primary_id = primary_id;
		this.menuVer = menuVer;
		this.listPrice = listPrice;
		this.category = category;
		this.name = name;
		this.promPrice = promPrice;
		this.promType = promType;
		this.image = image;
		this.categoryImage = categoryImage;
		this.sequenceNo = sequenceNo;
	}

	public int getPrimary_id() {
		return primary_id;
	}

	public void setPrimary_id(int primary_id) {
		this.primary_id = primary_id;
	}

	public String getMenuVer() {
		return menuVer;
	}

	public void setMenuVer(String menuVer) {
		this.menuVer = menuVer;
	}

	public int getListPrice() {
		return listPrice;
	}

	public void setListPrice(int listPrice) {
		this.listPrice = listPrice;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getPromPrice() {
		return promPrice;
	}

	public void setPromPrice(int promPrice) {
		this.promPrice = promPrice;
	}

	public String getPromType() {
		return promType;
	}

	public void setPromType(String promType) {
		this.promType = promType;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}
	

	public String getCategoryImage() {
		return categoryImage;
	}

	public void setCategoryImage(String categoryImage) {
		this.categoryImage = categoryImage;
	}

	public String getSequenceNo() {
		return sequenceNo;
	}

	public void setSequenceNo(String sequenceNo) {
		this.sequenceNo = sequenceNo;
	}

	public String toString() {
		return GsonUtil.gson.toJson(this);
	}

}
