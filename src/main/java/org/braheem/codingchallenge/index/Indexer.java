package org.braheem.codingchallenge.index;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.braheem.codingchallenge.data.Product;
import org.braheem.codingchallenge.main.Main;

public class Indexer {

	public static Analyzer analyzer = new CustomAnalyzer(2, 2, false);
	public static Path jarPath;
	public static Directory ramDir;
	
	public Indexer(){
		try {
			jarPath = Paths.get(URLEncoder.encode(Main.class.getProtectionDomain().getCodeSource().getLocation().getPath(), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	/*  method that takes in JSON input and populates a List<Product> to be used for lucene index creation */
	public static List<Product> genLuceneIndex(String productFileName){
		
		List<Product> products = new ArrayList<Product>();
		BufferedReader br = null;
		JsonReader jsonReader;

		IndexWriterConfig indexcfg = new IndexWriterConfig(analyzer);
		IndexWriter writer = null;

		try {
			//Directory indexDir = FSDirectory.open(Indexer.jarPath);
			ramDir = new RAMDirectory();
			writer = new IndexWriter(ramDir, indexcfg);
			
			String currentline;
			br = new BufferedReader(new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream(productFileName)));
			
			String productname = Product.FieldNames.ProductName.toString();
			String family = Product.FieldNames.Family.toString();
			String model = Product.FieldNames.Model.toString();
			String manufacturer = Product.FieldNames.Manufacturer.toString();
			String announceddate = Product.FieldNames.AnnouncedDate.toString();

			while ((currentline = br.readLine()) != null){
				List<IndexableField> fieldList = new ArrayList<IndexableField>();
				
				jsonReader = Json.createReader(new StringReader(currentline));
				JsonObject jsonObj = jsonReader.readObject();
				
				Product product = new Product();
				product.setProduct_name(jsonObj.getString(productname, null));
				product.setFamily(jsonObj.getString(family, null));
				product.setModel(jsonObj.getString(model, null));
				product.setManufacturer(jsonObj.getString(manufacturer, null));
				product.setAnnounced_date(jsonObj.getString(announceddate,null));
				
				TextField productField = new TextField(productname, product.getProduct_name(), Field.Store.YES);
				TextField familyField = new TextField(family, product.getFamily(), Field.Store.YES);
				TextField modelField = new TextField(model, product.getModel(), Field.Store.YES);
				TextField manufacturerField = new TextField(manufacturer, product.getManufacturer(), Field.Store.YES);
				//index a field that combines both family, model and manufacturer fields of the product
				TextField allField = new TextField("all", product.getManufacturer() + " " + product.getFamily() + " " + product.getModel(), Field.Store.YES);

				StringField announcedDateField = new StringField(announceddate, product.getAnnounced_date(), Field.Store.YES);
					
				fieldList.add(productField);
				fieldList.add(familyField);
				fieldList.add(modelField);
				fieldList.add(manufacturerField);
				fieldList.add(allField);
				fieldList.add(announcedDateField);
				
				products.add(product);
				writer.addDocument(fieldList);
			}
			writer.commit();
			writer.close();
		} catch (IOException i) {
			i.printStackTrace();
		} 
		return products;
	}
	
}
