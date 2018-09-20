package com.lecootech.market.data;


public class CommentData {

	private String name;
	private String time;
	private String rating;
	private String contain;

	public CommentData() {

	}

	// name

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	// contain
	public void setContain(String contain) {
		this.contain = contain;
	}

	public String getContain() {
		return contain;
	}

	// rating
	public void setRating(String rating) {
		this.rating = rating;
	}

	public String getRatin() {
		return rating;
	}

	// size
	public void setTime(String time) {
		this.time = time;
	}

	public String getTime() {
		return time;
	}
	
}
