package org.braheem.codingchallenge.main;

import java.util.List;

import org.apache.lucene.queryparser.classic.ParseException;
import org.braheem.codingchallenge.data.Product;
import org.braheem.codingchallenge.index.Indexer;
import org.braheem.codingchallenge.matcher.Matcher;

public class Main {

	public static void main(String args[]) throws ParseException{
		if (args.length != 2){
			System.out.println("Please ensure that the name of the Listing and Product text files are defined");
			return;
		}
		String productFileName = args[0];
		String listingFileName = args[1];
		
		List<Product> products = Indexer.genLuceneIndex(productFileName);
		Matcher.matchListings(listingFileName, products);
	}
	
}
