package com.lecootech.market.common;

import android.view.WindowManager.LayoutParams;
import android.widget.LinearLayout;

public class ApplicationConstants {

	public final static int main = 0x111;
	public final static int update = 0x222;
	public final static int cancel = 0x333;
	public final static int webview = 0x444;
	public final static int gallery = 0x555;
	public final static int search = 0x666;
	public final static int webTimeout = 0x777;
	public final static int webTimeout2 = 98765;
	public final static int exception = 0x888;
	public final static int add_foot_view = 0x254;
	public final static int remove_foot_view = 0x256;
	public final static int dismiss = 0x259;
	public final static int main2 = 0x260;
	public final static int update2 = 0x261;
	public final static int sdcarde = 0x263;

	public final static LinearLayout.LayoutParams LP_FW = new LinearLayout.LayoutParams(
			LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);

	public final static LinearLayout.LayoutParams LP_FF = new LinearLayout.LayoutParams(
			LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
	
	public final static LinearLayout.LayoutParams LP_WW = new LinearLayout.LayoutParams(
			LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

	public final static int perPageSize = 10;

	public final static String store_download_apk_path = "/LeCooMarket/download/";
	public final static String store_download_pic_path = "/LeCooMarket/picture/";

	public final static String system_download_apk_path = "/data/data/com.lecootech.market/download/";
	public final static String system_download_pic_path = "/data/data/com.lecootech.market/picture/";

}
