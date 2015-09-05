package org.braheem.codingchallenge.data;

/*
 * Simple Object class for Product.
 */
public class Product {

	public enum FieldNames {
		Model("model"),
		Family("family"),
		Manufacturer("manufacturer"),
		ProductName("product_name"),
		AnnouncedDate("announced_date");
		
		private final String fieldString;
		
		private FieldNames(final String fieldString) {
			this.fieldString = fieldString;
		}
		
		public String toString() {
			return fieldString;
		}
	}
	
	private String product_name;
	private String manufacturer;
	private String family;
	private String model;
	private String announced_date;
	
	public String getProduct_name() {
		if (product_name == null){
			product_name = new String();
		}
		return product_name;
	}
	public void setProduct_name(String product_name) {
		this.product_name = product_name;
	}
	public String getManufacturer() {
		if (manufacturer == null) {
			manufacturer = new String();
		}
		return manufacturer;
	}
	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}
	public String getFamily() {
		if (family == null){
			family = new String();
		}
		return family;
	}
	public void setFamily(String family) {
		this.family = family;
	}
	public String getModel() {
		if (model == null){
			model = new String();
		}
		return model;
	}
	public void setModel(String model) {
		this.model = model;
	}
	public String getAnnounced_date() {
		if (announced_date == null){
			announced_date = new String();
		}
		return announced_date;
	}
	public void setAnnounced_date(String announced_date) {
		this.announced_date = announced_date;
	}
}
