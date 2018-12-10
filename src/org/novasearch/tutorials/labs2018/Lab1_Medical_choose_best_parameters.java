package org.novasearch.tutorials.labs2018;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.LMDirichletSimilarity;
import org.apache.lucene.search.similarities.LMSimilarity;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.search.similarities.SimilarityBase;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import medical.driver.Driver;

public class Lab1_Medical_choose_best_parameters {

	String indexPath = "/Users/simaonovais/Desktop/Mestrado/RI/Java/RI-lab3/Indexes/BM25IndexMedical";
	String docPath = "/Users/simaonovais/Desktop/Mestrado/RI/Java/RI-lab3/data/docs";
	String trecPath = "/Users/simaonovais/Desktop/Mestrado/RI/trec_eval/trec_eval";
	String qrelsPath = "/Users/simaonovais/Desktop/Mestrado/RI/Java/RI-lab3/eval/qrels_2016_trec_eval.txt";
	String path_queries="/Users/simaonovais/Desktop/Mestrado/RI/Java/RI-lab3/eval/topics2016.xml";
	String dir_eval = "./eval";
	String results_path = "/Users/simaonovais/Desktop/Mestrado/RI/Java/RI-lab3/results";
	String result_filename = "results.txt";
	//batch_path is where a batch of trec scores will be stored in files.
	String batch_path = "/Users/simaonovais/Desktop/Mestrado/RI/Java/RI-lab3/results/batch";
	boolean create = true;

	private IndexWriter idx;

	public static void main(String[] args)	{

//		Analyzer analyzer = new StandardAnalyzer(); //https://www.tutorialspoint.com/lucene/lucene_standardanalyzer.htm
		//write_to_file();
////	
		//Similarity sim = new BM25Similarity(0.5f,0.5f);
		Lab1_Medical_choose_best_parameters baseline = new Lab1_Medical_choose_best_parameters();
////
		Lab2_Analyser analyzer = new Lab2_Analyser();
		//Similarity sim = new LMDirichletSimilarity(300);
	//	 Create a new index
//		baseline.openIndex(analyzer, sim);
//		baseline.indexDocuments();
//		baseline.close();
		// Search the index
		String indexPath = "/Users/simaonovais/Desktop/Mestrado/RI/Java/RI-lab3/Indexes/BM25IndexMedical";
		String similarity = "BM";
		String batch_path = "/Users/simaonovais/Desktop/Mestrado/RI/Java/RI-lab3/results/batch";
		for (float b = 0.1f;b<=1;b=b+0.1f) {
			for (float k = 0.1f; k <=10;k=k+0.3f) {
				Similarity sim = new BM25Similarity(k,b);
				String file_results = String.format("%s_%s_%s",similarity,String.valueOf(b),String.valueOf(k));
				baseline.indexSearch_trec_eval(analyzer, sim,indexPath,batch_path,file_results);
			}
		}
	}
        

	//Just configures the index object, adds the similarity and the path
	//In case it's not created....
	public void openIndex(Analyzer analyzer, Similarity similarity) {
		try {
			// ====================================================
			// Configure the index to be created/opened
			//
			// IndexWriterConfig has many options to be set if needed.
			//
			// Example: for better indexing performance, if you
			// are indexing many documents, increase the RAM
			// buffer. But if you do this, increase the max heap
			// size to the JVM (eg add -Xmx512m or -Xmx1g):
			//
			// iwc.setRAMBufferSizeMB(256.0);
			IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
			iwc.setSimilarity(similarity);
			if (create) {
				// Create a new index, removing any
				// previously indexed documents:
				iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
			} else {
				// Add new documents to an existing index:
				iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
			}

			// ====================================================
			// Open/create the index in the specified location
			Directory dir = FSDirectory.open(Paths.get(indexPath));
			idx = new IndexWriter(dir, iwc);

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	//indexes a document to the index (Answer.csv)
	public void indexDocuments() {
		if (idx == null)
			return;
		File folder = new File(docPath);
		File[] listOfFiles = folder.listFiles();

		for (int i = 0; i < listOfFiles.length; i++) {
		  if (listOfFiles[i].isFile()) {
			String filePath = listOfFiles[i].getPath();
		    indexDoc(filePath);
		    
		  } else if (listOfFiles[i].isDirectory()) {
		    System.out.println(listOfFiles[i].getName());
		  }
		}
	}

	public void indexDoc(String rawDocument) {
		Driver d = new Driver();
		Document doc = d.parseNxmlToLuceneceDoc(rawDocument);
	       
		try {
			
		// ====================================================
		// Add the document to the index
			if (idx.getConfig().getOpenMode() == IndexWriterConfig.OpenMode.CREATE) {
				System.out.println("adding " + doc.getField("PMCID"));
				
				idx.addDocument(doc);
			} else {
				idx.updateDocument(new Term("PMID", doc.getField("PMCID").toString()), doc);
			}
		} catch (IOException e) {
			System.out.println("Error adding document " + doc.getField("PMCID").toString());
		} catch (Exception e) {
			System.out.println("Error parsing document " + doc.getField("PMCID").toString());
		}
	}
	// ====================================================
	// Comment and refactor this method yourself

	
	
	public void indexSearch_trec_eval(Analyzer analyzer, Similarity similarity, String idxPath,String batch_file_path,String resultname){

		//begin: variables for query expansion
//		QueryExpansion qexp = new QueryExpansion();
//		int mu = 2000;
//		int retrieve = 200; // retrive top 1000 docs
//		int threshold = 10;
//		int TopDocs = 20;
//		float relative_weight = (float) 0.3;
//		float relative_orig_weight = (float) 1.0 - relative_weight;
		//end: variables for query expansion
		
		int retrieve = 200;
		IndexReader reader = null;
		
		try {
			reader = DirectoryReader.open(FSDirectory.open(Paths.get(idxPath)));
			IndexSearcher searcher = new IndexSearcher(reader);
			searcher.setSimilarity(similarity);
			
			QueryParser parser = new QueryParser("Body", analyzer);

			File results_file = new File(results_path, result_filename);
			FileWriter archivo = new FileWriter(results_file);
			archivo.write(String.format("%20s %20s %20s %20s %20s %20s \r\n", "TOPIC_NO", "Q0", "PMCID", "RANK", "SCORE", "RUN_NAME"));
			
			File fXmlFile = new File(path_queries);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			org.w3c.dom.Document docXml = dBuilder.parse(fXmlFile);

			NodeList nList = docXml.getElementsByTagName("topic");

			for (int temp = 0; temp < nList.getLength(); temp++) {
				

				Node nNode = nList.item(temp);
						
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {

					Element eElement = (Element) nNode;

					String id_query = eElement.getAttribute("number").toString();
			    	String query_line = eElement.getElementsByTagName("summary").item(0).getTextContent().toString();
			    	
			    	System.out.println(id_query + " : " + query_line + "\n");
			    	Query query;
			    	
					try {
						query = parser.parse(query_line);
						//System.out.println("Processed query: " + query.toString() + "\n");
//						query = new CustomScoreQuery(query, newFunc);
					} catch (org.apache.lucene.queryparser.classic.ParseException e) {
						System.out.println("Error parsing query string.");
						continue;
					}
					
//			    	//begin query expansion
////					String query_str = query.toString();
//					Map<String, Integer> expansionTop = qexp.getExpansionTermsDocs(query.toString(), retrieve, 0, TopDocs, analyzer, similarity, searcher);
//					System.out.println("now or never");
//					System.out.println(expansionTop);
//					Map<String, Integer> expansionBottom = qexp.getExpansionTermsDocs(query.toString(), retrieve, 998, 999, analyzer, similarity, searcher);
////					Map<String, Integer> candidates = qexp.joinMaps(expansionTop, expansionBottom, 10, 2);
////					Map<String, Integer> topCandidates = qexp.filterMap(candidates, threshold);
//					String orig_query_str_weighted = qexp.putTermsQuery(query_line,relative_orig_weight); // add weights to original query
//					//Map<String, Double> normalizedWeights = qexp.getWeights(topCandidates, query_str, relative_weight);
////					System.out.println("top:\n");
////					System.out.println(query_str);
////					
//
//					Map<String, Integer> resultado = new HashMap<String, Integer>();
//					for (Map.Entry<String, Integer> top : expansionTop.entrySet()) {
//						if(top.getValue()>=TopDocs-18) {
//							resultado.put(top.getKey(), top.getValue());							
//						}
//					}
//					
//					Map<String, Integer> resultado_com_threshold = qexp.filterMapTemp(resultado,threshold);
//					System.out.println("trheshold\n");
//					System.out.println(resultado_com_threshold);
//					
//					//Map<String, Double> bestTerms = qexp.getWeightedTerms(mu, reader,query.toString(), 100, 0, TopDocs, analyzer, similarity, searcher, resultado, numberTerms);
//					String expanded_query_weighted = qexp.queryExpanded(resultado_com_threshold, orig_query_str_weighted,relative_weight);
//					System.out.println("Processed query: " + expanded_query_weighted + "\n");
//					try {
//						query = parser.parse(expanded_query_weighted);
////						query = new CustomScoreQuery(query, newFunc);
//					} catch (org.apache.lucene.queryparser.classic.ParseException e) {
//						System.out.println("Error parsing query string.");
//						continue;
//					}
//////			    	//finish query expansion

					TopDocs results = searcher.search(query, retrieve);
					ScoreDoc[] hits = results.scoreDocs;
					
					int numTotalHits = results.totalHits;
					
					for (int j = 0; j < retrieve; j++) {
						Document docLuc = searcher.doc(hits[j].doc);
						Integer pmcid = docLuc.getField("PMCID").numericValue().intValue();
//						TOPIC_NO  Q0  PMCID  RANK  SCORE  RUN_NAME
						archivo.write(String.format("%20s %20s %20s %20s %20s %20s \r\n", id_query, "Q0", pmcid, String.valueOf(j+1), hits[j].score, "RUN_NAME"));
						
					}

				}
			}
			String[] cmd = {trecPath, qrelsPath, String.format("%s/%s", results_path,result_filename)};
			Process p = Runtime.getRuntime().exec(cmd);
			//System.out.println(trecPath + " " + qrelsPath + " " +  results_path + "/" +result_filename + " > " + batch_path);
			//Process p = Runtime.getRuntime().exec(trecPath + " " + qrelsPath + " " +  results_path + "/" +result_filename + " > " + batch_path);
			//Process p = Runtime.getRuntime().exec("cmd /c \"cd eval && .\\trec_eval qrels_2016_trec_eval.txt results.txt -q\""); 
	        BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
	        String readline;
	        String pathBatchResults = String.format("%s/%s.txt", batch_file_path,resultname);
        
			FileWriter archivos = new FileWriter(pathBatchResults);
	        while ((readline = r.readLine()) != null) {
	        	System.out.println(readline);
	        	archivos.write(String.format(readline + "\n"));
	        }
			archivos.close();
			System.out.println("DONE");
			archivo.close();
			reader.close();
			
		} catch (IOException | ParserConfigurationException | SAXException e) {
			try {
				reader.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
	}
	
	public double DecayFunction(int days) {
		double val = 1/(Math.pow(2,days));
		return val;
	}
	
	public long sumArray(List<Float> array) {
		long sum = 0;
	    for (int i = 0; i < array.size(); i++) {
	        sum += array.get(i);
	    }
	    return sum;
	}
	public double std(List<Float> array, float mean) {
		double std = 0;
	    for (int i = 0; i < array.size(); i++) {
	        std += Math.pow((array.get(i) - mean), 2);
	    }
	    return Math.sqrt(std/array.size());
	}
	
	public void close() {
		try {
			idx.close();
		} catch (IOException e) {
			System.out.println("Error closing the index.");
		}
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
	
}