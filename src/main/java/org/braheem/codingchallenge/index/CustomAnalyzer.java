package org.braheem.codingchallenge.index;


import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.WhitespaceTokenizer;
import org.apache.lucene.analysis.miscellaneous.WordDelimiterFilter;
import org.apache.lucene.analysis.shingle.ShingleFilter;

/*
 * CustomAnalyzer - A custom analyzer that uses ShingleFilter and WordDelimiterFilter
 * Users can specify the minimum/maximum number of shingles as well as whether or not to output unigrams
 */
public final class CustomAnalyzer extends Analyzer {
	
	private int minShingle;
	private int maxShingle;
	private boolean outputUnigrams;
	
	public CustomAnalyzer(int minShingle, int maxShingle, boolean outputUnigrams){
		this.minShingle = minShingle;
		this.maxShingle = maxShingle;
		this.outputUnigrams = outputUnigrams;
	}
	
	@Override
	protected TokenStreamComponents createComponents(String arg0) {
		WhitespaceTokenizer tokenizer = new WhitespaceTokenizer();	
		int flags = generateDelimiterFlags();
		TokenStream delimfilter = new WordDelimiterFilter(tokenizer, flags, null);
		
		TokenStream lfilter = new LowerCaseFilter(delimfilter);
		
		ShingleFilter shingle = new ShingleFilter(lfilter);
		shingle.setOutputUnigramsIfNoShingles(true);
		shingle.setMaxShingleSize(maxShingle);
		shingle.setMinShingleSize(minShingle);
		shingle.setOutputUnigrams(outputUnigrams);
		shingle.setTokenSeparator(ShingleFilter.DEFAULT_TOKEN_SEPARATOR);
		
		
		return new TokenStreamComponents(tokenizer, shingle);
	}

	private int generateDelimiterFlags(){
		return WordDelimiterFilter.CATENATE_ALL 
				| WordDelimiterFilter.PRESERVE_ORIGINAL
				| WordDelimiterFilter.GENERATE_WORD_PARTS
				| WordDelimiterFilter.SPLIT_ON_NUMERICS;
	}

}
