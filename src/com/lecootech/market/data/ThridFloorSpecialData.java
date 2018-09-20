package com.lecootech.market.data;

public class ThridFloorSpecialData {

	private String swid;
	private String name;
	private String download_num;
	private String imagepath;
	private String modifieddate;
	private String hit;

	public ThridFloorSpecialData() {

	}

	// swid
	public void setSwid(String swid) {
		this.swid = swid;
	}

	public String getSwid() {
		return swid;
	}

	// name

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	// modifieddate

	public void setModifyData(String data) {
		this.modifieddate = data;
	}

	public String getModifyData() {
		return modifieddate;
	}

	// imagepath
	public void setImagePath(String path) {
		this.imagepath = path;
	}

	public String getImagePath() {
		return imagepath;
	}

	// download_num
	public void setDownload_Num(String num) {
		this.download_num = num;
	}

	public String getDownload_Num() {
		return download_num;
	}

	// score
	public void setHit(String hit) {
		this.hit = hit;
	}

	public String getHit() {
		return this.hit;
	}

}
