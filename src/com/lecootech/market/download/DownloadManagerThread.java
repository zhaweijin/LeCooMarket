package com.lecootech.market.download;

import com.lecootech.market.data.DownloadSoftwareData;

public class DownloadManagerThread extends Thread {
	public String name;
	public  boolean isrun;
	DownloadSoftwareData softwareData;

	public DownloadManagerThread(DownloadSoftwareData softwareData) {
		this.softwareData = softwareData;
		
	}

	@Override
	public void run() {
		super.run();
		isrun = true;
		name = softwareData.getSoftwareName();
				new FileDownloader(softwareData);
		isrun = false;
	}
}
