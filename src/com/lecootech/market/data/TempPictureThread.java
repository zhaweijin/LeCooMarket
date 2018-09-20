package com.lecootech.market.data;

import com.lecootech.market.download.ImagePageTask;

/**
 * @author zhaweijin
 * @function 存储之前加载过的图片对象
 */
public class TempPictureThread {

	private ImagePageTask task;

	public TempPictureThread() {

	}

	// task
	public void setTask(ImagePageTask task) {
		this.task = task;
	}

	public ImagePageTask getTask() {
		return this.task;
	}

}
