package org.braheem.codingchallenge.matcher;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.json.JsonWriter;
import javax.json.JsonWriterFactory;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.braheem.codingchallenge.data.Listing;
import org.braheem.codingchallenge.data.Product;
import org.braheem.codingchallenge.data.Result;
import org.braheem.codingchallenge.index.CustomAnalyzer;
import org.braheem.codingchallenge.index.Indexer;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

public class Matcher {

	//results file to quickly skim over each match of listing to product
	private static File fvalidate = new File("./matchesforInspection.txt");
	//output file for submission
	private static File fout = new File("./output.txt");
	
	/* 	Here we declare the CustomAnalyzer with outputUnigrams set to True,
	  	this allows us to obtain matches against single-term fields of the 
	  	Product using the Listing title field							*/
	static Analyzer customAnalyzer = new CustomAnalyzer(2, 2, true);
	
	/* Match each listing object with at most 1 product object and print a listing object per line for validation
	 * Calls the recordResults() method to then print a result object per line to output file
	 */
	public static void matchListings(String listingFileName, List<Product> products) throws ParseException{
		
		//Writer to create the validation file first to be able to see each listing to product match
		BufferedReader br = null;
		JsonReader jsonReader;
		String currentline;
		br = new BufferedReader(new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream(listingFileName)));
		
		Multimap<String, Listing> matchMap = ArrayListMultimap.create();
		
		//try-with-resource
		try (FileOutputStream fos = new FileOutputStream(fvalidate);
				BufferedWriter bwmatch = new BufferedWriter(new OutputStreamWriter(fos))) {

			IndexReader ir = DirectoryReader.open(Indexer.ramDir);
			IndexSearcher searcher = new IndexSearcher(ir);
			
			//get the defined field strings for Product matching
			String model = Product.FieldNames.Model.toString();
			String manufacturer = Product.FieldNames.Manufacturer.toString();
			String family = Product.FieldNames.Family.toString();
			
			//model and manufacturer must match, other fields are optional
			Map<String, BooleanClause.Occur> fieldBcMap = new HashMap<String, BooleanClause.Occur>();
			fieldBcMap.put(model, BooleanClause.Occur.MUST);
			fieldBcMap.put(manufacturer, BooleanClause.Occur.MUST);
			fieldBcMap.put(family, BooleanClause.Occur.SHOULD);
			fieldBcMap.put("all", BooleanClause.Occur.SHOULD);

			//iterate through listings.txt file
			while ((currentline = br.readLine()) != null){	
				jsonReader = Json.createReader(new StringReader(currentline));
				JsonObject jsonObj = jsonReader.readObject();
				
				Listing listing = new Listing();
				listing.setTitle(jsonObj.getString("title"));
				listing.setManufacturer(jsonObj.getString("manufacturer"));
				listing.setPrice(jsonObj.getString("price"));
				listing.setCurrency(jsonObj.getString("currency"));

				//define list of terms for searching
				List<Term> termList = new ArrayList<Term>();
				
				termList.add(new Term(model, listing.getTitle())); //match listing title to product model
				termList.add(new Term(manufacturer, listing.getManufacturer())); //match manufacturer in listing to that of product
				termList.add(new Term(family, listing.getTitle())); //match listing title to product family
				termList.add(new Term("all", listing.getTitle())); 	//match listing title to 'all' field

				//builder to generate booleanquery that combines all queries
				BooleanQuery.Builder queryBuilder = new BooleanQuery.Builder();

				for (Term term : termList){
					//if term text is not empty, add query to booleanquery
					if (!term.text().isEmpty()){
						
						BooleanQuery.Builder innerQueryBuilder = new BooleanQuery.Builder();

						String queryString = term.text();
						String field = term.field();
						
						TokenStream ts = customAnalyzer.tokenStream(field ,new StringReader(queryString)); 
						CharTermAttribute termAtt = ts.addAttribute(CharTermAttribute.class); 
						ts.reset(); 
						while (ts.incrementToken()) { 
							String termText = termAtt.toString(); 
							innerQueryBuilder.add(new TermQuery(new Term(field, termText)), BooleanClause.Occur.SHOULD); 
						} 
						queryBuilder.add(innerQueryBuilder.build(), fieldBcMap.get(term.field()));
						ts.end();
						ts.close();
					}
				}
				//parent BooleanQuery that will combine all other queries into one
				BooleanQuery bquery = queryBuilder.build();
				TopDocs topresult = searcher.search(bquery, 1);
				
				int nhits = topresult.scoreDocs.length;
				
				if (nhits > 0){
						Document hitdoc = searcher.doc(topresult.scoreDocs[0].doc);
						String product = hitdoc.getField("product_name").stringValue();
						matchMap.put(product, listing);
						//use BufferedWriter to print listing match on each line for result skimming
						bwmatch.write("Listing: " + listing.getTitle() );
						bwmatch.write("  Matched with Product: " + product);
						bwmatch.newLine();
				}				
			} 
			recordResults(products, matchMap);
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}


	// method to build the output file with one result object per line
	public static void recordResults(List<Product> products, Multimap<String, Listing> matchMap){		
			
			JsonWriter jsonWriter = null;
			
			try (FileOutputStream fos = new FileOutputStream(fout); 
					BufferedWriter bwresult = new BufferedWriter(new OutputStreamWriter(fos))) {
				Map<String,Object> properties = new HashMap<String,Object>(1);
				JsonWriterFactory factory = Json.createWriterFactory(properties);
				boolean first = true;
				for (Product product : products){
					if (first) {
						first = false;
					}
					else {
						bwresult.newLine();
					}
					jsonWriter = factory.createWriter(bwresult);
					
					Result result = new Result();
					List<Listing> listings = new ArrayList<Listing>();
					listings.addAll(matchMap.get(product.getProduct_name()));
					
					result.setProduct_name(product.getProduct_name());
					result.setListings(listings);
					JsonObjectBuilder resultBuilder = Json.createObjectBuilder();
					JsonObjectBuilder listingBuilder = Json.createObjectBuilder();
					JsonArrayBuilder listingArrBuilder = Json.createArrayBuilder();
					
					for (Listing listing : listings){
						listingBuilder.add("title", listing.getTitle());
						listingBuilder.add("manufacturer", listing.getManufacturer());
						listingBuilder.add("currency", listing.getCurrency());
						listingBuilder.add("price", listing.getPrice());
						listingArrBuilder.add(listingBuilder.build());
					}
					resultBuilder.add("product_name", product.getProduct_name());
					resultBuilder.add("listings", listingArrBuilder.build());
					JsonObject resultObj = resultBuilder.build();
					jsonWriter.writeObject(resultObj);
				}	
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				jsonWriter.close();
			}
	}
}
