package org.braheem.codingchallenge.data;

import java.util.List;

public class Result {
	private String product_name;
	private List<Listing> listings;
	
	public String getProduct_name() {
		return product_name;
	}
	public void setProduct_name(String product_name) {
		this.product_name = product_name;
	}
	public List<Listing> getListings() {
		return listings;
	}
	public void setListings(List<Listing> listings) {
		this.listings = listings;
	}
	
}
