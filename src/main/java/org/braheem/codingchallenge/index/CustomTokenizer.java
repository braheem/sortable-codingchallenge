package org.braheem.codingchallenge.index;


import org.apache.lucene.analysis.util.CharTokenizer;
import org.apache.lucene.util.AttributeFactory;

/*
 * REPLACED BY WhiteSpaceTokenizer with WordDelimiterFilter
 */
public class CustomTokenizer extends CharTokenizer {

	public enum CharType {
		LETTER,
		DIGIT,
		ALPHANUMERIC
	}
	public CharType charType;
	
	public CustomTokenizer(CharType t) {
		this.charType = t;
	}

	public CustomTokenizer(AttributeFactory factory) {
		super(factory);
	}

	@Override
	protected boolean isTokenChar(int c) {
		
		if (this.charType == CharType.ALPHANUMERIC) {
			if (Character.isLetterOrDigit(c)){
					return true;
			}
		}
		else if (this.charType == CharType.DIGIT) {
			if (Character.isDigit(c)){
				return true;
			}
		}
		else if (this.charType == CharType.LETTER) {
			if (Character.isLetter(c)){
				return true;
			}
		}
		return false;
	}
	
}
