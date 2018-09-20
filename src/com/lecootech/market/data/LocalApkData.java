package com.lecootech.market.data;

import android.graphics.drawable.Drawable;

public class LocalApkData {

	private String filepath;
	private String version;
	private String size;
	private Drawable icon;
	private String name;
	
	private String packagename;
	
	
	
	//filepath 
	public void setFilepath(String filepath)
	{
		this.filepath = filepath;
	}
	public String getFilepath()
	{
		return filepath;
	}
	
	//name 
	public void setName(String name)
	{
		this.name = name;
	}
	public String getName()
	{
		return name;
	}
	
	
	//设置里面的大小
	public void setSize(String size){
		this.size = size;
	}
	
	public String getSize(){
		return size;
		
	}
	
	
	// packagename
	public void setPackageName(String packageName) {
		this.packagename = packageName;
	}

	public String getPackageName() {
		return packagename;
	}
	
	
	// icon
	public void setIconDrawable(Drawable drawable) {
		this.icon = drawable;
	}

	public Drawable getDrawable() {
		return icon;
	}
	
	
	// version
	public void setVersion(String version) {
		this.version = version;
	}

	public String getVersion() {
		return version;
	}
}
