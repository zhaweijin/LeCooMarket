package com.lecootech.market.data;

import android.app.Activity;
import android.widget.ImageView;

/**
 * @author zhaweijin 存放异步加载图标所需要的对象
 */
public class ImageData {

	private ImageView view;
	private String path;
	private String date;
	private String swid;
	private String name;
	private Activity activity;
	private String parentname;
	private boolean bigOrsmall;

	public void setImageView(ImageView view) {
		this.view = view;
	}

	public ImageView getImageView() {
		return this.view;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getPath() {
		return this.path;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	public void setParentName(String parentname) {
		this.parentname = parentname;
	}

	public String getParentName() {
		return this.parentname;
	}

	public void setSwid(String swid) {
		this.swid = swid;
	}

	public String getSwid() {
		return this.swid;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getDate() {
		return this.date;
	}

	public void setActivity(Activity activity) {
		this.activity = activity;
	}

	public Activity getActivity() {
		return activity;
	}

	public void setBigOrSmall(Boolean bigorsmall) {
		this.bigOrsmall = bigorsmall;
	}

	public boolean getBigOrSmall() {
		return bigOrsmall;
	}
}
