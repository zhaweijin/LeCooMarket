package com.lecootech.xml;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class paeserXml {
	Document doc;

	public paeserXml(String xmlString) {
		if (xmlString == null || xmlString.length() <= 0
				|| !InitDocument(xmlString)) {
		}
	}

	public boolean InitDocument(String xmlString) {
		DocumentBuilderFactory docBuilderFactory;
		DocumentBuilder docBuilder;
		docBuilderFactory = DocumentBuilderFactory.newInstance();
		try {
			docBuilder = docBuilderFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			return false;
		}

		InputSource is = new InputSource(new StringReader(xmlString));

		try {
			doc = docBuilder.parse(is);
		} catch (SAXException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public ArrayList<SoftwareInfo> XMLToSoftwareInfo() {
		Element root = doc.getDocumentElement();
		String eleName = "SoftwareUpdateManager";

		NodeList nodeList = root.getElementsByTagName(eleName);
		ArrayList<SoftwareInfo> softList = new ArrayList<SoftwareInfo>();

		if (nodeList.getLength() == 0) {
			System.out.println("XMLToData nodeList is null");
			return softList;
		}

		int userNum = nodeList.getLength();
		for (int i = 0; i < userNum; i++) {
			SoftwareInfo soft = new SoftwareInfo();

			Node mNode = nodeList.item(i);
			NamedNodeMap ab = mNode.getAttributes();
			int len = ab.getLength();
			for (int index = 0; index < len; index++) {
				String name = ab.item(index).getNodeName();
				String value = ab.item(index).getNodeValue();
				if (value == null || value.length() <= 0) {
					soft = null;
					continue;
				}

				if (soft == null) {
					continue;
				}

				if (name.equals("name")) {
					soft.setName(value);
				}
				if (name.equals("downloadpath")) {
					soft.setDownloadpath(value);
				}
				if (name.equals("swid")) {
					soft.setSwid(value);
				}
				if (name.equals("packagename")) {
					soft.setPackagename(value);
				}
				if (name.equals("edition")) {
					soft.setEdition(value);
				}
			}

			if (soft != null) {
				softList.add(soft);
			}

		}

		return softList;
	}
}