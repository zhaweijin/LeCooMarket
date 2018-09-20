package com.lecootech.market.data;

import android.graphics.drawable.Drawable;

public class ManagerFloorData {

	private Drawable iconDrawable;
	private String bcid;
	private String name;
	private String size;
	private String packageName;
	private String version;
	private String sie;
	private boolean installlocation;           //false:donnt move,true can move
	private boolean installlocationsd;
	public ManagerFloorData() {

	}

	// icon
	public void setIconDrawable(Drawable drawable) {
		this.iconDrawable = drawable;
	}

	public Drawable getDrawable() {
		return iconDrawable;
	}
	//installlocation
	public void setinstalllocation(boolean installlocation){
		this.installlocation = installlocation;
	}
	
	public boolean getinstalllocation(){
		return installlocation;
		
	}
	//安装的位置
	public void setintstalllocationsd(boolean installlocationsd)
	{
		this.installlocationsd = installlocationsd;
	}
	
	public boolean getinstalllocationsd(){
		return installlocationsd;
		
	}
//	//设置里面的大小
//	public void setSize(String sie){
//		this.sie = sie;
//	}
//	
//	public String getSize(){
//		return sie;
//		
//	}
	// name

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
	
	

	// packagename
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getPackageName() {
		return packageName;
	}

	// size
	public void setSize(String size) {
		this.size = size;
	}

	public String getSize() {
		return size;
	}

	// version
	public void setVersion(String version) {
		this.version = version;
	}

	public String getVersion() {
		return version;
	}
}
