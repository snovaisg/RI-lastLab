package org.novasearch.tutorials.labs2018;

import static java.util.stream.Collectors.toMap;

import java.io.IOException;
import java.io.StringReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.Similarity; 

public class QueryExpansion {
	
	//Creates dictionary of all the terms and their count between 2 indexes of the ordered list of retrieved documents
	public Map<String, Integer> termsIndex(int idx_start, int idx_finish, ScoreDoc[] hits, Analyzer analyzer, IndexSearcher searcher) {
		
		Map<String, Integer> topTerms = new HashMap<String, Integer>();
		Map<String, Integer> results = new HashMap<String, Integer>();

		try {
						
			for (int j = idx_start, i = 0; j <= idx_finish; j++, i++) {
	            Document doc = searcher.doc(hits[j].doc);
	            String answer = doc.get("Body");
	
	            TokenStream stream = analyzer.tokenStream("field", new StringReader(answer));
	
	            // get the CharTermAttribute from the TokenStream
	            CharTermAttribute termAtt = stream.addAttribute(CharTermAttribute.class);
	
	            try {
	                stream.reset();
	
	                // print all tokens until stream is exhausted
	                while (stream.incrementToken()) {
	                    String term = termAtt.toString();
	                    Integer termCount = topTerms.get(term);
	                    if (termCount == null)
	                        topTerms.put(term, 1);
	                }
	                stream.end();
	            } finally {
	                stream.close();
	            }
	            if (i==0) {
	            	results = topTerms;
	            } else {
	            	results = sumMaps(results, topTerms);
		            topTerms = new HashMap<String, Integer>();	            	
	            }
	            
	        }
		} catch (IOException e) {
            e.printStackTrace();
            
        }		
		results  = results
                .entrySet()
                .stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .collect(
                    toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2,
                        LinkedHashMap::new));
		
		return results;
	}
	
	// receives the query and returns the terms' counts  of all the documents
	// between idx_start and idx_finish of the ordered list of retrived documents
	public Map<String, Integer> getExpansionTermsDocs(String queryString, int retrieved, int idx_start, int idx_finish, Analyzer analyzer, Similarity similarity, IndexSearcher searcher) {

        Map<String, Integer> topTerms = new HashMap<String, Integer>();


        try {
            QueryParser parser = new QueryParser("Body", analyzer);
            Query query;
            try {
                query = parser.parse(queryString);
            } catch (org.apache.lucene.queryparser.classic.ParseException e) {
                System.out.println("Error parsing query string.");
                return null;
            }

            TopDocs results = searcher.search(query, retrieved);
            ScoreDoc[] hits = results.scoreDocs;
            
            topTerms =  termsIndex(idx_start, idx_finish, hits, analyzer, searcher);
                                    
        } catch (IOException e) {
            e.printStackTrace();
        }
        return topTerms;
    }
	
//selects only the terms from the top that don't appear as often on the bottom
	public Map<String, Integer> sumMaps(Map<String, Integer> topTerms, Map<String, Integer> bottomTerms) {
		
		Map<String, Integer> result = new HashMap<String, Integer>();
        for (Map.Entry<String, Integer> top : topTerms.entrySet()) {
            for (Map.Entry<String, Integer> bottom : bottomTerms.entrySet()) {
            	//System.out.println(String.format("TopTerm: %s,%d\nWorstTerm: %s,%d", top.getKey().toString(),top.getValue(),worst.getKey().toString(),worst.getValue()));
            	if(top.getKey().equals(bottom.getKey())) {
            		result.put(top.getKey(), top.getValue() + bottom.getValue());
        			break;
            	}
            }
        }
        
        for (Map.Entry<String, Integer> top : topTerms.entrySet()) {
            for (Map.Entry<String, Integer> bottom : bottomTerms.entrySet()) {
            	//System.out.println(String.format("TopTerm: %s,%d\nWorstTerm: %s,%d", top.getKey().toString(),top.getValue(),worst.getKey().toString(),worst.getValue()));
            	if(top.getKey().equals(bottom.getKey())) {
            		result.put(top.getKey(), top.getValue() + bottom.getValue());
        			break;
            	}
            }
        }
        
        return result;
	}
	
	
	
	//selects only the terms from the top that don't appear as often on the bottom
	public Map<String, Integer> joinMaps(Map<String, Integer> topTerms, Map<String, Integer> bottomTerms,int topDocumentsCount,int bottomDocumentsCount) {
		
		Map<String, Integer> result = new HashMap<String, Integer>();
        for (Map.Entry<String, Integer> top : topTerms.entrySet()) {
            for (Map.Entry<String, Integer> bottom : bottomTerms.entrySet()) {
            	//System.out.println(String.format("TopTerm: %s,%d\nWorstTerm: %s,%d", top.getKey().toString(),top.getValue(),worst.getKey().toString(),worst.getValue()));
            	if(top.getKey().equals(bottom.getKey())) {
            		if(algo(top.getValue(),topDocumentsCount,bottom.getValue(),bottomDocumentsCount) >=1) {
            			//System.out.println("Yes " + top.getKey() + "\n");
            			result.put(top.getKey(), top.getValue() + bottom.getValue());
            			break;
            		}
            	}
            }
        }
        
        return result;
	}
	
	public float algo(int topTermCount,int topDocCound, int bottomTermCount, int bottomDocumentCount) {
		return (float) ((1.0 * topTermCount/topDocCound) / (bottomTermCount/bottomDocumentCount));
	}
	
	//return the top <threshold> potencial expansion terms by count
	public Map<String, Integer> filterMapTemp(Map<String, Integer> resultTerms, int threshold) {
		
		Map<String, Integer> result = new HashMap<String, Integer>();
		//sort values in dic in descending order
		resultTerms  = resultTerms
                .entrySet()
                .stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .collect(
                    toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2,
                        LinkedHashMap::new));
		int i = 0;
        for (Map.Entry<String, Integer> top : resultTerms.entrySet()) {
        	if(i<threshold) {
        		result.put(top.getKey(), top.getValue());       		
        	}
        	i++;
        }
       
        return result;	
        
	}
	
	//return the top <threshold> potencial expansion terms by count
		public Map<String, Double> filterMap(Map<String, Double> resultTerms, int threshold) {
			
			Map<String, Double> result = new HashMap<String, Double>();
			//sort values in dic in descending order
			resultTerms  = resultTerms
	                .entrySet()
	                .stream()
	                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
	                .collect(
	                    toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2,
	                        LinkedHashMap::new));
			int i = 0;
	        for (Map.Entry<String, Double> top : resultTerms.entrySet()) {
	        	if(i<threshold) {
	        		result.put(top.getKey(), top.getValue());       		
	        	}
	        	i++;
	        }
	       
	        return result;	
	        
		}
	
	
	
	
	//inputs: top terms to be used in expansion
	// returns: relative weights for each term
	public Map<String, Double> getWeights(Map<String, Integer> resultTerms,String queryWeight,float relativeWeight) {
		
		int orgQueryCount = getQueryTermsCount(queryWeight);
    	float normalize = orgQueryCount*relativeWeight;
		
		Map<String, Double> result = new HashMap<String, Double>();
		Double sumTotal = 0.0;
				
		for (Map.Entry<String, Integer> top : resultTerms.entrySet()) {
			sumTotal += top.getValue();       	
        }

		for (Map.Entry<String, Integer> top : resultTerms.entrySet()) {
			result.put(top.getKey(), (double) (top.getValue()/sumTotal*normalize));
		}
		
		Map<String, Double> sorted = result
                .entrySet()
                .stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .collect(
                    toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2,
                        LinkedHashMap::new));
		return sorted;
		
	}
    
	//put weight in original query
    public String putTermsQuery(String query,double originalTotalWeight) {
    
    	String[] parts = query.split(" ");
    	String new_string="";
        float weight = (float) (originalTotalWeight/parts.length); //So alterar aqui isto
        for (int i=0; i<parts.length; i++) {
        	new_string = new_string + parts[i] + "^"+ weight+" " ;	
        }
        return new_string;   
    }
    
    public int getQueryTermsCount(String query) {
    	String[] parts = query.split(" ");
        return parts.length;
    }
    
    //relative weight: percentage of weight relative to the original query
    public String queryExpanded(Map<String, Integer> weightTerms,String origQueryWeight,float totalExpWeight) {
    	int size = weightTerms.size();
    	float weight = (float) totalExpWeight / (float) size;
				
        for (Map.Entry<String, Integer> top : weightTerms.entrySet()) {
        	//origQueryWeight = origQueryWeight + top.getKey() + "^1.0 ";
        	origQueryWeight = origQueryWeight + top.getKey() + "^" + weight + " ";
        }
        
        return origQueryWeight;	   
	}
    
    
    
    //LMD formula
    public Map<String, Double> getWeightedTerms(double mu, IndexReader reader,String queryString,  int retrieved, int idx_start, int idx_finish, Analyzer analyzer, Similarity similarity, IndexSearcher searcher, Map<String, Integer> terms, int numberTerms) {
    	Map<String, Double> TermsFinal  = new HashMap<String, Double>();
    	Map<String, Double> aux  = new HashMap<String, Double>();
    	
    	try {
            QueryParser parser = new QueryParser("Body", analyzer);
            Query query;
            try {
                query = parser.parse(queryString);
            } catch (org.apache.lucene.queryparser.classic.ParseException e) {
                System.out.println("Error parsing query string.");
                return null;
            }

            TopDocs results = searcher.search(query, retrieved);
            ScoreDoc[] hits = results.scoreDocs;
                        
            try {
            	double weight = 0;	
    	            for (Map.Entry<String, Integer> top : terms.entrySet()) {
    	            	for (int j = idx_start; j <= idx_finish; j++) {
    	    	            Document doc = searcher.doc(hits[j].doc);
    	    	            String answer = doc.get("Body");
    	            		weight += (getLMTerm(answer,top.getKey(),mu, reader) * hits[j].score);	
//    	    	            weight += Math.log(getLMTerm(answer,top.getKey(),mu, reader));	
					}
    	            aux.put(top.getKey(), weight);	
				}

    		} catch (IOException e) {
                e.printStackTrace();
                
            }
            
            TermsFinal = filterMap(aux, numberTerms);
            TermsFinal = normalized(TermsFinal);
                         
        } catch (IOException e) {
            e.printStackTrace();
        }
    	return TermsFinal;
    }
    
    public Map<String, Double> normalized(Map<String, Double> terms) {
    	Map<String, Double> normalized = new HashMap<String, Double>();
    	
    	Double sumTotal = 0.0;
		
		for (Map.Entry<String, Double> top : terms.entrySet()) {
			sumTotal += top.getValue();       	
        }
    	
//    	double maxValueInMap=(Collections.max(terms.values()));
//    	double minValueInMap=(Collections.min(terms.values()));
//    	
    	for (Map.Entry<String, Double> top : terms.entrySet()) {
    		normalized.put(top.getKey(), 0.4* (top.getValue()/sumTotal));
        }
    	normalized = normalized
                .entrySet()
                .stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .collect(
                    toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2,
                        LinkedHashMap::new));
    	
    	return normalized;
    }
    
    public float getTFfromDoc(String str, String term) {
		String a[] = str.split(" "); 
		  
	    // search for pattern in a 
	    int count = 0; 
	    for (int i = 0; i < a.length; i++)  
	    { 
	    // if match found increase count 
	    if (term.equals(a[i])) 
	        count++; 
	    } 
	  
	    return count/a.length;
	}

    public double getLMTerm(String answer,String term ,double mu, IndexReader reader) {
    	Term t = new Term("Body", term);
		long totalDocsTermContain = 0;
		try {
			totalDocsTermContain = reader.totalTermFreq(t);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String a[] = answer.split(" "); 
		double weight = 0;
		double tf = (double) getTFfromDoc(answer, term);
		double docLen = a.length;
    	//weight = Math.log((tf + (mu * totalDocsTermContain))/mu + docLen);
		weight = (tf + (mu * totalDocsTermContain))/(mu + docLen);
    	return weight;
    }
}
