package com.lecootech.market.data;


public class InstallNecessaryData {

	private String swid;
	private String name;
	private String hits;
	private String download_num;
	private String imagepath;
	private String software_iamge_path;
	private String categoryid;
	private String modifieddate;
	private String packagename;
	private String score;
	private String author;
	private String size;
	private String software_download_path;
	private String introuce;

	public InstallNecessaryData() {

	}

	// categoryid
	public void setCategoryID(String id) {
		this.categoryid = id;
	}

	public String getCategoryID() {
		return categoryid;
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

	// hit
	public void setHits(String hits) {
		this.hits = hits;
	}

	public String getHits() {
		return hits;
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
	public void setScroe(String score) {
		this.score = score;
	}

	public String getScore() {
		return this.score;
	}

	// software_iamge_path
	public void setSoftwareImagePath(String path) {
		this.software_iamge_path = path;
	}

	public String getSoftwareImagePath() {
		return software_iamge_path;
	}

	// author
	public void setAuthor(String author) {
		this.author = author;
	}

	public String getAuthor() {
		return author;
	}

	// size
	public void setSize(String size) {
		this.size = size;
	}

	public String getSize() {
		return size;
	}

	// downloadpath
	public void setDownloadPath(String path) {
		this.software_download_path = path;
	}

	public String getDownloadPath() {
		return software_download_path;
	}

	// packagename
	public void setPackageName(String packagename) {
		this.packagename = packagename;
	}

	public String getPackageName() {
		return packagename;
	}

	// introduce
	public void setIntroduce(String introduce) {
		this.introuce = introduce;
	}

	public String getIntroduce() {
		return introuce;
	}

}
