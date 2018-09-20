package com.lecootech.market.handle;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.lecootech.market.common.Util;
import com.lecootech.market.data.CommentData;
import com.lecootech.market.data.FourFloorData;
import com.lecootech.market.data.ImagesPush;
import com.lecootech.market.data.InstallNecessaryData;
import com.lecootech.market.data.NotificationData;
import com.lecootech.market.data.SearchHotData;
import com.lecootech.market.data.SecondFloorData;
import com.lecootech.market.data.ThridFloorData;
import com.lecootech.market.data.UpdateManagerData;
import com.lecootech.market.data.UpdateMarketData;

/**
 * @author zhaweijin
 * @function 网络数据的解析 --->软件、游戏
 */
public class DataHandle extends DefaultHandler {

	private DataSet dataSet;

	@Override
	public void startDocument() throws SAXException {
		dataSet = new DataSet();
	}

	public DataSet getDataSet() {
		return this.dataSet;
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		try {
            //Util.print("localName", localName);
			/**
			 * @fucntion 小分类数据--二级层
			 */
			if (localName.equals("Cate")) {
				dataSet.getSecondCates().add(new SecondFloorData());
				dataSet.getSecondCate().setCategoryID(
						attributes.getValue("categoryid"));
				dataSet.getSecondCate().setBcid(attributes.getValue("bcid"));
				dataSet.getSecondCate().setName(attributes.getValue("name"));
				dataSet.getSecondCate().setModifyData(
						attributes.getValue("modifieddate"));
				dataSet.getSecondCate().setImagePath(
						attributes.getValue("imagepath"));
			}
			/**
			 * @fucntion 具体数据---三级层
			 */
			else if (localName.equals("DataSoft")) {
				dataSet.getThreeCates().add(new ThridFloorData());
				dataSet.getThreeCate().setSwid(attributes.getValue("swid")); // swid
																				// 0
				dataSet.getThreeCate().setName(attributes.getValue("name")); // name
																				// 1
				dataSet.getThreeCate().setSize(
						attributes.getValue("softwaresize"));
				dataSet.getThreeCate().setDownloadPath(
						attributes.getValue("downloadpath"));// apkpath
				dataSet.getThreeCate().setImagePath(
						attributes.getValue("imagepath")); // imagepath 4
				dataSet.getThreeCate().setCategoryID(
						attributes.getValue("categoryid")); // categoryid 5
				dataSet.getThreeCate().setModifyData(
						attributes.getValue("modifieddate")); // modifieddate 6
				dataSet.getThreeCate().setScroe(attributes.getValue("score")); // score
																				// 7
				dataSet.getThreeCate().setSoftwareImagePath(
						attributes.getValue("softwareBigIcon")); // software_img
																	// 8
				dataSet.getThreeCate().setPackageName(
						attributes.getValue("packageName")); // package
				dataSet.getThreeCate().setVersion(attributes.getValue("Versiondata"));
			}
			/*
			 * software manager update data
			 */
			else if (localName.equals("SoftwareUpdateManager")) {
				dataSet.getUpdateManagerDatas().add(new UpdateManagerData());
				dataSet.getUpdateManagerData().setName(
						attributes.getValue("name"));
				dataSet.getUpdateManagerData().setSwid(
						attributes.getValue("swid"));
				dataSet.getUpdateManagerData().setapkPath(
						attributes.getValue("downloadpath"));
				dataSet.getUpdateManagerData().setVersion(
						attributes.getValue("edition"));
				dataSet.getUpdateManagerData().setSize(
						attributes.getValue("softwaresize"));
				dataSet.getUpdateManagerData().setPackageName(
						attributes.getValue("packagename"));
			}
			/*
			 * special softwarelist introduce
			 */
			else if (localName.equals("DataSoftSpecialIntroduce")) {
				dataSet.setSpecialSoftwareIntroduce(attributes
						.getValue("Introduce"));
			}
			/*
			 * search hot
			 */
			else if (localName.equals("DataSoftHot")) {
				dataSet.getSearchHotDatas().add(new SearchHotData());
				dataSet.getSearchHotData().setSwid(attributes.getValue("swid"));
				dataSet.getSearchHotData().setName(attributes.getValue("name"));
				dataSet.getSearchHotData().setColor(
						attributes.getValue("searchFontColor"));
				dataSet.getSearchHotData().setSize(
						attributes.getValue("searchFontSize"));
			}
			/*
			 * update market data
			 */
			else if (localName.equals("LeCoolpack")) {
				dataSet.setMarketData(new UpdateMarketData());
				dataSet.getMarketData().setContent(
						attributes.getValue("content"));
				dataSet.getMarketData().setVersion(
						attributes.getValue("version"));
				dataSet.getMarketData().setDownloadPath(
						attributes.getValue("upurl"));
				dataSet.getMarketData().setStatement(attributes.getValue("statement"));
			}
			/**
			 * @fucntion 详细页数据
			 */
			else if (localName.equals("SoftShow")) {
				dataSet.setSoftwareInfo(new FourFloorData());
			} else if (localName.equals("SoftwareCount")) {
				
				dataSet.currentListSize = Integer.parseInt(attributes
						.getValue("count"));
				Util.print("set size", "set size"+dataSet.currentListSize);
			}
			/**
			 * @fucntion 各列表数据的总数
			 */
			// else if(localName.equals("total"))
			// {
			// dataSet.getTotals().add(new Total());
			// dataSet.getTotal().setTotal(attributes.getValue(0));
			// }
			/**
			 * @fucntion 评论页数据
			 */
			else if (localName.equals("dataMsg")) {
				dataSet.getPingRuns().add(new CommentData());
				
				dataSet.getPingRun().setName(attributes.getValue("username"));
				dataSet.getPingRun().setContain(attributes.getValue("content"));
				dataSet.getPingRun().setRating(
						attributes.getValue("soft_score"));
				dataSet.getPingRun()
						.setTime(attributes.getValue("createddate"));
			}
			/*
			 * 安装必备
			 */
			else if (localName.equals("InstallNecessaryDataSoft")) {
				dataSet.getInstallNecessaryDatas().add(
						new InstallNecessaryData());
				dataSet.getInstallNecessaryData().setSwid(
						attributes.getValue("swid")); // swid 0
				dataSet.getInstallNecessaryData().setName(
						attributes.getValue("name")); // name 1
				dataSet.getInstallNecessaryData().setSize(
						attributes.getValue("softwaresize"));
				dataSet.getInstallNecessaryData().setDownloadPath(
						attributes.getValue("downloadpath"));// apkpath
				dataSet.getInstallNecessaryData().setImagePath(
						attributes.getValue("imagepath")); // imagepath 4
				dataSet.getInstallNecessaryData().setCategoryID(
						attributes.getValue("categoryid")); // categoryid 5
				dataSet.getInstallNecessaryData().setModifyData(
						attributes.getValue("modifieddate")); // modifieddate 6
				dataSet.getInstallNecessaryData().setScroe(
						attributes.getValue("score")); // score 7
				dataSet.getInstallNecessaryData().setPackageName(
						attributes.getValue("packageName")); // package
				dataSet.getInstallNecessaryData().setIntroduce(
						attributes.getValue("description"));

			}
			// 以下都是软件详细页面要获取的数据
			else if (localName.equals("name")) {
				dataSet.getSoftwareInfo().setName(attributes.getValue("data"));
			} else if (localName.equals("filepath")) {
				dataSet.getSoftwareInfo().setFilepath(
						attributes.getValue("data"));
			} else if (localName.equals("imagepath")) {
				dataSet.getSoftwareInfo().setImagePath(
						attributes.getValue("data"));
			} else if (localName.equals("download_num")) {
				dataSet.getSoftwareInfo().setDownload_Num(
						attributes.getValue("data"));
			} else if (localName.equals("language")) {
				dataSet.getSoftwareInfo().setLanguage(
						attributes.getValue("data"));
			} else if (localName.equals("hits")) {
				dataSet.getSoftwareInfo().setHits(attributes.getValue("data"));
			} else if (localName.equals("usemobile")) {
				dataSet.getSoftwareInfo().setUseMobile(
						attributes.getValue("data"));
			} else if (localName.equals("swid")) {
				dataSet.getSoftwareInfo().setSwid(attributes.getValue("data"));
			} else if (localName.equals("createddate")) {
				dataSet.getSoftwareInfo().setModifyDate(
						attributes.getValue("data"));
			} else if (localName.equals("sotfware_size")) {
				dataSet.getSoftwareInfo().setSoftwareSize(
						attributes.getValue("data"));
			} else if (localName.equals("imageArray")) {
				Util.print("----------dd", "dd");
				dataSet.getImageArraysID().add(
						new String(attributes.getValue("imageid")));
				dataSet.getImageArrays().add(
						new String(attributes.getValue("imagepath")));
			} else if (localName.equals("description")) {
				dataSet.getSoftwareInfo().setDescription(
						attributes.getValue("data"));
			} else if (localName.equals("edition")) {
				dataSet.getSoftwareInfo().setVersion(
						attributes.getValue("data"));
			} else if (localName.equals("score")) {
				dataSet.getSoftwareInfo().setScroe(attributes.getValue("data"));
			} else if (localName.equals("package_name")) {
				dataSet.getSoftwareInfo().setPackage_Name(
						attributes.getValue("data"));
			} else if (localName.equals("category_name")) {
				dataSet.getSoftwareInfo().setCategory_Name(
						attributes.getValue("data"));
			} else if (localName.equals("recomment")) {
				dataSet.getSoftwareInfo().setRecoment(
						attributes.getValue("data"));
			} else if (localName.equals("author_name")) {
				dataSet.getSoftwareInfo().setDeveloper_Autor_Name(
						attributes.getValue("data"));
			} else if (localName.equals("phone")) {
				dataSet.getSoftwareInfo().setDeveloper_Phone(
						attributes.getValue("data"));
			} else if (localName.equals("email")) {
				dataSet.getSoftwareInfo().setDeveloper_Email(
						attributes.getValue("data"));
			} else if (localName.equals("webpath")) {
				dataSet.getSoftwareInfo().setDeveloper_Webpath(
						attributes.getValue("data"));
			} else if (localName.equals("favority")) {
				dataSet.getSoftwareInfo().setFavority(
						attributes.getValue("data"));
			} else if (localName.equals("bcid")) {
				dataSet.getSoftwareInfo().setBcid(attributes.getValue("data"));
			} else if (localName.equals("categoryid")) {
				dataSet.getSoftwareInfo().setCategoryID(
						attributes.getValue("data"));
			}
			
			//编辑首页数据
			else if(localName.equals("ImagesPush"))
			{
				dataSet.getImagesPushs().add(new ImagesPush());
				
			    dataSet.getiImagesPush().setName(attributes.getValue("name"));
			    dataSet.getiImagesPush().setSwid(attributes.getValue("swid"));
				dataSet.getiImagesPush().setModifieddate(attributes.getValue("modifieddate"));
				dataSet.getiImagesPush().setSoftwareBigIcon(attributes.getValue("softwareBigIcon"));
				dataSet.getiImagesPush().setSort(attributes.getValue("Sort"));
				dataSet.getiImagesPush().setCategoryid(attributes.getValue("categoryid"));
			}
			//开机显示的广告数据
			else if(localName.equals("NotificationData"))
			{
				 dataSet.setNotificationData(new NotificationData());
				 dataSet.getNotificationData().setShortname(attributes.getValue("shortname"));
				 dataSet.getNotificationData().setLongname(attributes.getValue("longname"));
				 dataSet.getNotificationData().setCategoryid(attributes.getValue("Categoryid"));
				 dataSet.getNotificationData().setSwid(attributes.getValue("swid"));
				 dataSet.getNotificationData().setSound(attributes.getValue("sound"));
				 dataSet.getNotificationData().setShake(attributes.getValue("shake"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {

	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {

	}

	@Override
	public void endDocument() throws SAXException {
		super.endDocument();
	}

}
