package com.lecootech.market.data;

/**
 * @author zhaweijin 管理页面 异步检测是否有更新返回的数据集
 */
public class UpdateManagerData {

	private String version;
	private String apkpath;
	private String swid;
	private String name;
	private String packagename;
	private String size;

	public UpdateManagerData() {

	}

	// apkpath
	public void setapkPath(String path) {
		this.apkpath = path;
	}

	public String getapkPath() {
		return apkpath;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getVersion() {
		return version;
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

	// packagename
	public void setPackageName(String packagename) {
		this.packagename = packagename;
	}

	public String getPackageName() {
		return packagename;
	}

	// size
	public void setSize(String size) {
		this.size = size;
	}

	public String getSize() {
		return size;
	}
}
