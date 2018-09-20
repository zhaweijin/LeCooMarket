package com.lecootech.market.data;

public class FourFloorData {

	private String name;
	private String filepath;
	private String download_num;
	private String description;
	private String software_size;
	private String usemobile;
	private String language;
	private String hits;
	private String createddate;
	private String modifydate;
	private String imagepath;

	private String version;
	private String score;
	private String package_name;
	private String recoment;
	private String category_name;
	private String category_id;
	private String favority;
	private String bcid;
	private String swid;
	// about_developer
	private String developer_autor_name = "";
	private String developer_phone = "";
	private String developer_email = "";
	private String develper_webpath = "";

	// filepath
	public void setFilepath(String path) {
		this.filepath = path;
	}

	public String getFilepath() {
		return filepath;
	}

	// name

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	// hits
	public void setHits(String hits) {
		this.hits = hits;
	}

	public String getHits() {
		return hits;
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

	// language
	public void setLanguage(String language) {
		this.language = language;
	}

	public String getLanguage() {
		return language;
	}

	// usemobile
	public void setUseMobile(String mobile) {
		this.usemobile = mobile;
	}

	public String getUseMobile() {
		return usemobile;
	}

	// sotfware_size
	public void setSoftwareSize(String size) {
		this.software_size = size;
	}

	public String getSoftwareSize() {
		return software_size;
	}

	// createData
	public void setCreateData(String data) {
		this.createddate = data;
	}

	public String getCreateData() {
		return createddate;
	}

	// modifyDate
	public void setModifyDate(String date) {
		this.modifydate = date;
	}

	public String getModifyDate() {
		return modifydate;
	}

	// description
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	// recoment
	public String getRecoment() {
		return recoment;
	}

	public void setRecoment(String recoment) {
		this.recoment = recoment;
	}

	// version
	public void setVersion(String version) {
		this.version = version;
	}

	public String getVersion() {
		return version;
	}

	// score
	public void setScroe(String score) {
		this.score = score;
	}

	public String getScore() {
		return this.score;
	}

	// package_name
	public void setPackage_Name(String name) {
		this.package_name = name;
	}

	public String getPackage_Name() {
		return package_name;
	}

	// developer_autor_name
	public void setDeveloper_Autor_Name(String name) {
		this.developer_autor_name = name;
	}

	public String getDeveloper_Autor_Name() {
		return this.developer_autor_name;
	}

	// developer_phone
	public void setDeveloper_Phone(String phone) {
		this.developer_phone = phone;
	}

	public String getDeveloper_Phone() {
		return this.developer_phone;
	}

	// developer_email
	public void setDeveloper_Email(String email) {
		this.developer_email = email;
	}

	public String getDeveloper_Email() {
		return this.developer_email;
	}

	// developer_webpath
	public void setDeveloper_Webpath(String webpath) {
		this.develper_webpath = webpath;
	}

	public String getDeveloper_Webpath() {
		return develper_webpath;
	}

	// category_name
	public void setCategory_Name(String name) {
		this.category_name = name;
	}

	public String getCategory_Name() {
		return this.category_name;
	}

	// favority
	public void setFavority(String favority) {
		this.favority = favority;
	}

	public String getFavority() {
		return this.favority;
	}

	// bcid
	public void setBcid(String bcid) {
		this.bcid = bcid;
	}

	public String getBcid() {
		return bcid;
	}

	// swid
	public void setSwid(String swid) {
		this.swid = swid;
	}

	public String getSwid() {
		return swid;
	}

	// category id
	public void setCategoryID(String categoryid) {
		this.category_id = categoryid;
	}

	public String getCagegoryID() {
		return category_id;
	}
}
