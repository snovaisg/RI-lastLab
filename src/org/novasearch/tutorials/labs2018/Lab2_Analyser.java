/**
 * 
 */
package org.novasearch.tutorials.labs2018;

import java.util.List;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.charfilter.HTMLStripCharFilter;
import org.apache.lucene.analysis.commongrams.CommonGramsFilter;
import org.apache.lucene.analysis.core.WhitespaceTokenizer;
import org.apache.lucene.analysis.snowball.SnowballFilter;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.standard.UAX29URLEmailTokenizer;
import org.apache.lucene.analysis.synonym.SynonymGraphFilter;
import org.apache.lucene.analysis.synonym.SynonymMap;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.util.CharsRef;
import org.apache.lucene.util.CharsRefBuilder;
import org.apache.lucene.analysis.ngram.EdgeNGramTokenFilter;
import org.apache.lucene.analysis.ngram.NGramTokenFilter;
import org.apache.lucene.analysis.shingle.ShingleFilter;

/**
 * @author jmag
 *
 */
public class Lab2_Analyser extends Analyzer {

	/**
	 * An unmodifiable set containing some common English words that are not
	 * usually useful for searching.
	 */
//	static List<String> stopWords = Arrays.asList("shouldn", "about", "yourselves", "should've", "out", "most", "now", "very", "who", "re", "and", "why", "can", "she's", "our", "it's", "herself", "where", "we", "that'll", "having", "if", "do", "i", "y", "himself", "few", "wasn", "aren't", "itself", "me", "whom", "here", "in", "again", "shouldn't", "own", "myself", "ain", "just", "because", "of", "don't", "below", "will", "be", "mightn", "theirs", "had", "m", "won", "from", "hadn", "you're", "more", "above", "only", "over", "she", "not", "mightn't", "other", "wouldn't", "mustn't", "haven't", "don", "all", "these", "your", "ourselves", "into", "or", "a", "wasn't", "didn", "that", "between", "both", "shan", "each", "been", "an", "hasn't", "isn", "couldn", "for", "ours", "are", "isn't", "should", "as", "couldn't", "there", "against", "doing", "at", "when", "it", "t", "such", "her", "they", "haven", "while", "d", "did", "you'll", "by", "s", "my", "to", "down", "ma", "being", "off", "what", "so", "their", "how", "weren", "was", "same", "weren't", "nor", "am", "needn", "during", "them", "you", "under", "yours", "were", "before", "the", "too", "then", "up", "after", "until", "his", "he", "wouldn", "through", "which", "this", "with", "o", "doesn", "didn't", "once", "aren", "ve", "has", "mustn", "him", "on", "is", "hers", "shan't", "yourself", "any", "you've", "than", "themselves", "hasn", "does", "hadn't", "have", "needn't", "no", "those", "doesn't", "some", "you'd", "its", "but", "ll", "won't", "further");
//	static List<String> stopWords = Arrays.asList("a","about","above","after","again","against","ain","all","am","an",
//            "and","any","are","aren", "aren't","as","at","be","because","been","before", "being", "below", "between",
//            "both", "but", "by", "can", "couldn", "couldn't", "d", "did", "didn", "didn't", "do", "does", "doesn", "doesn't",
//            "doing", "don", "don't", "down", "during", "each", "few", "for", "from", "further", "had", "hadn", "hadn't",
//            "has", "hasn", "hasn't", "have","haven", "haven't", "having", "he", "her", "here", "hers", "herself", "him",
//            "himself", "his", "how", "i", "if", "in", "into", "is", "isn", "isn't", "it", "it's", "its", "itself", "just",
//            "ll", "m", "ma", "me", "mightn", "mightn't", "more", "most", "mustn", "mustn't", "my", "myself", "needn",
//            "needn't", "no", "nor", "not", "now", "o", "of", "off", "on", "once", "only", "or", "other", "our", "ours", "ourselves",
//            "out", "over", "own", "re", "s", "same", "shan", "shan't", "she", "she's", "should", "should've", "shouldn", "shouldn't",
//            "so", "some", "such", "t", "than", "that", "that'll", "the", "their", "theirs", "them", "themselves",
//            "then", "there", "these", "they", "this", "those", "through", "to", "too", "under", "until", "up", "ve", "very",
//            "was", "wasn", "wasn't", "we", "were", "weren", "weren't", "what", "when", "where", "which", "while", "who", "whom", "why",
//            "will", "with", "won", "won't", "wouldn", "wouldn't", "y", "you", "you'd", "you'll", "you're", "you've",
//            "your", "yours", "yourself", "yourselves");
	
	static List<String> stopWords = Arrays.asList("0","1","2","3","4","5","6","7","8","9","patient", "report","year","case","clinic","disease", "diseases", "disorder", "symptom", "symptoms", "drug", "drugs", "problems", "problem","prob", "probs", "med", "meds",
			"pill", "pills", "medicine", "medicines", "medication", "medications", "treatment", "treatments", "caps", "capsules", "capsule",
			"tablet", "tablets", "tabs", "doctor", "dr", "dr.", "doc", "physician", "physicians", "test", "tests", "testing", "specialist", "specialists",
			"side-effect", "side-effects", "pharmaceutical", "pharmaceuticals", "pharma", "diagnosis", "diagnose", "diagnosed", "exam",
			"challenge", "device", "condition", "conditions", "suffer", "suffering" ,"suffered", "feel", "feeling", "prescription", "prescribe",
			"prescribed", "over-the-counter", "otc", "a", "able", "about", "above", "abroad", "according", "accordingly", "across", "actually", "adj", "after", "afterwards", "again", "against", "ago", "ahead", "ain't", "all", "allow", "allows", "almost", "alone", "along", "alongside", "already", "also", "although", "always", "am", "amid", "amidst", "among", "amongst", "an", "and", "another", "any", "anybody", "anyhow", "anyone", "anything", "anyway", "anyways", "anywhere", "apart", "appear", "appreciate", "appropriate", "are", "aren't", "around", "as", "a's", "aside", "ask", "asking", "associated", "at", "available", "away", "awfully", "b", "back", "backward", "backwards", "be", "became", "because", "become", "becomes", "becoming", "been", "before", "beforehand", "begin", "behind", "being", "believe", "below", "beside", "besides", "best", "better", "between", "beyond", "both", "brief", "but", "by", "c", "came", "can", "cannot", "cant", "can't", "caption", "cause", "causes", "certain", "certainly", "changes", "clearly", "c'mon", "co", "co.", "com", "come", "comes", "concerning", "consequently", "consider", "considering", "contain", "containing", "contains", "corresponding", "could", "couldn't", "course", "c's", "currently", "d", "dare", "daren't", "definitely", "described", "despite", "did", "didn't", "different", "directly", "do", "does", "doesn't", "doing", "done", "don't", "down", "downwards", "during", "e", "each", "edu", "eg", "eight", "eighty", "either", "else", "elsewhere", "end", "ending", "enough", "entirely", "especially", "et", "etc", "even", "ever", "evermore", "every", "everybody", "everyone", "everything", "everywhere", "ex", "exactly", "example", "except", "f", "fairly", "far", "farther", "few", "fewer", "fifth", "first", "five", "followed", "following", "follows", "for", "forever", "former", "formerly", "forth", "forward", "found", "four", "from", "further", "furthermore", "g", "get", "gets", "getting", "given", "gives", "go", "goes", "going", "gone", "got", "gotten", "greetings", "h", "had", "hadn't", "half", "happens", "hardly", "has", "hasn't", "have", "haven't", "having", "he", "he'd", "he'll", "hello", "help", "hence", "her", "here", "hereafter", "hereby", "herein", "here's", "hereupon", "hers", "herself", "he's", "hi", "him", "himself", "his", "hither", "hopefully", "how", "howbeit", "however", "hundred", "i", "i'd", "ie", "if", "ignored", "i'll", "i'm", "immediate", "in", "inasmuch", "inc", "inc.", "indeed", "indicate", "indicated", "indicates", "inner", "inside", "insofar", "instead", "into", "inward", "is", "isn't", "it", "it'd", "it'll", "its", "it's", "itself", "i've", "j", "just", "k", "keep", "keeps", "kept", "know", "known", "knows", "l", "last", "lately", "later", "latter", "latterly", "least", "less", "lest", "let", "let's", "like", "liked", "likely", "likewise", "little", "look", "looking", "looks", "low", "lower", "ltd", "m", "made", "mainly", "make", "makes", "many", "may", "maybe", "mayn't", "me", "mean", "meantime", "meanwhile", "merely", "might", "mightn't", "mine", "minus", "miss", "more", "moreover", "most", "mostly", "mr", "mrs", "much", "must", "mustn't", "my", "myself", "n", "name", "namely", "nd", "near", "nearly", "necessary", "need", "needn't", "needs", "neither", "never", "neverf", "neverless", "nevertheless", "new", "next", "nine", "ninety", "no", "nobody", "non", "none", "nonetheless", "noone", "no-one", "nor", "normally", "not", "nothing", "notwithstanding", "novel", "now", "nowhere", "o", "obviously", "of", "off", "often", "oh", "ok", "okay", "old", "on", "once", "one", "ones", "one's", "only", "onto", "opposite", "or", "other", "others", "otherwise", "ought", "oughtn't", "our", "ours", "ourselves", "out", "outside", "over", "overall", "own", "p", "particular", "particularly", "past", "per", "perhaps", "placed", "please", "plus", "possible", "presumably", "probably", "provided", "provides", "q", "que", "quite", "qv", "r", "rather", "rd", "re", "really", "reasonably", "recent", "recently", "regarding", "regardless", "regards", "relatively", "respectively", "right", "round", "s", "said", "same", "saw", "say", "saying", "says", "second", "secondly", "see", "seeing", "seem", "seemed", "seeming", "seems", "seen", "self", "selves", "sensible", "sent", "serious", "seriously", "seven", "several", "shall", "shan't", "she", "she'd", "she'll", "she's", "should", "shouldn't", "since", "six", "so", "some", "somebody", "someday", "somehow", "someone", "something", "sometime", "sometimes", "somewhat", "somewhere", "soon", "sorry", "specified", "specify", "specifying", "still", "sub", "such", "sup", "sure", "t", "take", "taken", "taking", "tell", "tends", "th", "than", "thank", "thanks", "thanx", "that", "that'll", "thats", "that's", "that've", "the", "their", "theirs", "them", "themselves", "then", "thence", "there", "thereafter", "thereby", "there'd", "therefore", "therein", "there'll", "there're", "theres", "there's", "thereupon", "there've", "these", "they", "they'd", "they'll", "they're", "they've", "thing", "things", "think", "third", "thirty", "this", "thorough", "thoroughly", "those", "though", "three", "through", "throughout", "thru", "thus", "till", "to", "together", "too", "took", "toward", "towards", "tried", "tries", "truly", "try", "trying", "t's", "twice", "two", "u", "un", "under", "underneath", "undoing", "unfortunately", "unless", "unlike", "unlikely", "until", "unto", "up", "upon", "upwards", "us", "use", "used", "useful", "uses", "using", "usually", "v", "value", "various", "versus", "very", "via", "viz", "vs", "w", "want", "wants", "was", "wasn't", "way", "we", "we'd", "welcome", "well", "we'll", "went", "were", "we're", "weren't", "we've", "what", "whatever", "what'll", "what's", "what've", "when", "whence", "whenever", "where", "whereafter", "whereas", "whereby", "wherein", "where's", "whereupon", "wherever", "whether", "which", "whichever", "while", "whilst", "whither", "who", "who'd", "whoever", "whole", "who'll", "whom", "whomever", "who's", "whose", "why", "will", "willing", "wish", "with", "within", "without", "wonder", "won't", "would", "wouldn't", "x", "y", "yes", "yet", "you", "you'd", "you'll", "your", "you're", "yours", "yourself", "yourselves", "you've", "z", "zero");
	static CharArraySet stopSet = new CharArraySet(stopWords, false);
	
	/** Default maximum allowed token length */
	private int maxTokenLength = 25;

	/**
	 * Builds an analyzer with the default stop words ({@link #STOP_WORDS_SET}).
	 */
	public Lab2_Analyser() {
		
	}
	
//	private CharArraySet CreateStopWordsArray() {
//		
//		List<String> stopWords = new ArrayList<String>(); 
//		String file = "./data/stopwords.txt"; 
//		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
//		    String line;
//		    while ((line = br.readLine()) != null) {
//		    	stopWords.add(line);				
//				}
//		    	br.close();
//		    } catch(IOException e) {
//		}
//		
//		for (int j = 0; j < stopWords.size(); j++) {
//			System.out.println(stopWords.get(j));
//		}
//		CharArraySet stopSet = new CharArraySet(stopWords, false);
//		
//		System.out.println(stopSet.toString());
//		
//		return stopSet;
//	}
	
	@Override
	protected TokenStreamComponents createComponents(final String fieldName) {

		// THE FIELD IS IGNORED 
		// ___BUT___ 
		// you can provide different TokenStremComponents according to the fieldName
		
//		final StandardTokenizer src = new StandardTokenizer();
//		final WhitespaceTokenizer src = new  WhitespaceTokenizer();
		final UAX29URLEmailTokenizer  src = new  UAX29URLEmailTokenizer();
		
		TokenStream tok = null;
		tok = new StandardFilter(src);					// text into non punctuated text
		tok = new LowerCaseFilter(tok);					// changes all texto into lowercase
		tok = new StopFilter(tok, stopSet);				// removes stop words
		tok = new SnowballFilter(tok, "English");		// stems words according to the specified language
//		tok = new ShingleFilter(tok, 2, 2);				// creates word-grams with neighboring works
//		tok = new CommonGramsFilter(tok, stopSet);	// creates word-grams with stopwords

// 		tok = new NGramTokenFilter(src,2,5);			// creates unbounded n-grams
//		tok = new EdgeNGramTokenFilter(src,2,5);		// creates word-bounded n-grams

//		SynonymMap.Builder builder = new SynonymMap.Builder(true);
//	    builder.add(new CharsRef("Facebook"), new CharsRef("FaceB00k, FB"), true);
//	    builder.add(new CharsRef("Suzie"), new CharsRef("Susan"), false);
//	    SynonymMap map = null;
//	    try {
//	        map = builder.build();
//	    } catch (IOException e) {
//	        e.printStackTrace();
//	    }
//	    
//	    tok = new SynonymGraphFilter(src, map, true); // Ignore case True
	    
		return new TokenStreamComponents(src, tok) {
			@Override
			protected void setReader(final Reader reader) {
//				src.setMaxTokenLength(Lab2_Analyser.this.maxTokenLength);
				super.setReader(new HTMLStripCharFilter(reader));
//				super.setReader(reader);
			}
		};
	}

	
	@Override
	protected TokenStream normalize(String fieldName, TokenStream in) {
		TokenStream result = new StandardFilter(in);
//		result = new LowerCaseFilter(result);
//		result = new StopFilter(result, stopSet);
//		result = new SnowballFilter(result, "English");
//		result = new ShingleFilter(result, 2, 3);

		return result;
	}
	
	// ===============================================
	// Test the different filters
	public static void main(String[] args) throws IOException {

		final String text = "Sample Samples samples sample";
		
		Lab2_Analyser analyzer = new Lab2_Analyser();
		TokenStream stream = analyzer.tokenStream("field", new StringReader(text));

		// get the CharTermAttribute from the TokenStream
		CharTermAttribute termAtt = stream.addAttribute(CharTermAttribute.class);

		try {
			stream.reset();

			// print all tokens until stream is exhausted
			while (stream.incrementToken()) {
				System.out.println(termAtt.toString());
				
			}

			stream.end();
		} finally {
			stream.close();
			analyzer.close();
		}
	}
}
