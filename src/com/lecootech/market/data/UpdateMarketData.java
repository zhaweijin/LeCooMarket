package com.lecootech.market.data;

public class UpdateMarketData {

	private String name;
	private String version;
	private String date;
	private String content;
	private String downloadpath;
	private String statement;

	public UpdateMarketData() {

	}

	// name

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	// contain
	public void setVersion(String version) {
		this.version = version;
	}

	public String getVersion() {
		return version;
	}

	// rating
	public void setDate(String date) {
		this.date = date;
	}

	public String getDate() {
		return date;
	}

	// size
	public void setContent(String content) {
		this.content = content;
	}

	public String getContent() {
		return content;
	}

	// path
	public void setDownloadPath(String path) {
		this.downloadpath = path;
	}

	public String getDownloadPath() {
		return downloadpath;
	}
	
	//statement
	public void setStatement(String statement)
	{
		this.statement = statement;
	}
	public String getStatement()
	{
		return statement;
	}
}
