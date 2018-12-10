package org.novasearch.tutorials.labs2018;

import org.jsoup.*;
import org.jsoup.helper.*;
import org.jsoup.nodes.*;
import org.jsoup.select.*;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.*;
import org.apache.lucene.queries.CustomScoreQuery;
import org.apache.lucene.queries.function.FunctionQuery;
import org.apache.lucene.queries.function.ValueSource;
import org.apache.lucene.queries.function.valuesource.DoubleFieldSource;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.SortField.Type;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Period;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

import javax.print.Doc;

public class Lab1_Baseline {

	String indexPath = "./Indexes/Shingle24";
	String docPath = "./data/Answers.csv";
	
	String path_queries="./eval/queries.offline.txt";
	String dir_eval = "./eval";
	String file_results = "results.txt";


	boolean create = true;

	private IndexWriter idx;

	public static void main(String[] args) {

//		Analyzer analyzer = new StandardAnalyzer(); //https://www.tutorialspoint.com/lucene/lucene_standardanalyzer.htm
		//write_to_file();
		
		Similarity similarity = new ClassicSimilarity();
////	
		Lab1_Baseline baseline = new Lab1_Baseline();
////
		Lab2_Analyser analyzer = new Lab2_Analyser();

		 //Create a new index
		baseline.openIndex(analyzer, similarity);
		baseline.indexDocuments();
		baseline.close();

		// Search the index
		baseline.indexSearch_trec_eval(analyzer, similarity, "qualquer.txt","./Indexes/teste");
//		baseline.indexSearch(analyzer, similarity);
		
		
	}
        
    public static void write_to_file() {
    	

    	
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

		// ====================================================
		// Parse the Answers data
		try (BufferedReader br = new BufferedReader(new FileReader(docPath))) {
			StringBuilder sb = new StringBuilder();
			String line = br.readLine(); // The first line is dummy
			line = br.readLine();

			// ====================================================
			// Read documents
			while (line != null) {
				int i = line.length();

				// Search for the end of document delimiter
				if (i != 0)
					sb.append(line);
				sb.append(System.lineSeparator());
				if (((i >= 2) && (line.charAt(i - 1) == '"') && (line.charAt(i - 2) != '"')) 
						|| ((i == 1) && (line.charAt(i - 1) == '"'))) {
					// Index the document
					indexDoc(sb.toString());

					// Start a new document
					sb = new StringBuilder();
				}
				line = br.readLine();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void indexDoc(String rawDocument) {

		Document doc = new Document();

		// ====================================================
		// Each document is organized as:
		// Id,OwnerUserId,CreationDate,ParentId,Score,Body
		Integer AnswerId = 0;
		try {

			// Extract field Id
			Integer start = 0;
			Integer end = rawDocument.indexOf(',');
			String aux = rawDocument.substring(start, end);
			AnswerId = Integer.decode(aux);

			// Index _and_ store the AnswerId field
			doc.add(new IntPoint("AnswerId", AnswerId));
			doc.add(new StoredField("AnswerId", AnswerId));

			// Extract field OwnerUserId
			start = end + 1;
			end = rawDocument.indexOf(',', start);
			aux = rawDocument.substring(start, end);
			Integer OwnerUserId = Integer.decode(aux);
			doc.add(new IntPoint("OwnerUserId", OwnerUserId));

			// Extract field CreationDate
			try {
				start = end + 1;
				end = rawDocument.indexOf(',', start);
				aux = rawDocument.substring(start, end);
				Date creationDate;
				creationDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").parse(aux);
				//System.out.println(String.format("Date: {%s}", creationDate));
				doc.add(new LongPoint("CreationDate", creationDate.getTime()));
			} catch (ParseException e1) {
				System.out.println("Error parsing date for document " + AnswerId);
			}

			// Extract field ParentId
			start = end + 1;
			end = rawDocument.indexOf(',', start);
			aux = rawDocument.substring(start, end);
			Integer ParentId = Integer.decode(aux);
			doc.add(new IntPoint("ParentId", ParentId));

			// Extract field Score
			start = end + 1;
			end = rawDocument.indexOf(',', start);
			aux = rawDocument.substring(start, end);
			Integer Score = Integer.decode(aux);
			doc.add(new IntPoint("Score", Score));

			// Extract field Body
			String body = rawDocument.substring(end + 1);
			org.jsoup.nodes.Document doc2 = Jsoup.parse(body);
			String body2 = doc2.body().text();

			doc.add(new TextField("Body", body2, Field.Store.YES));
			
		// ====================================================
		// Add the document to the index
			if (idx.getConfig().getOpenMode() == IndexWriterConfig.OpenMode.CREATE) {
				System.out.println("adding " + AnswerId);
				
				idx.addDocument(doc);
			} else {
				idx.updateDocument(new Term("AnswerId", AnswerId.toString()), doc);
			}
		} catch (IOException e) {
			System.out.println("Error adding document " + AnswerId);
		} catch (Exception e) {
		System.out.println("Error parsing document " + AnswerId);
		}
	}
	

	// ====================================================
	// Comment and refactor this method yourself
	public void indexSearch(Analyzer analyzer, Similarity similarity) {

		IndexReader reader = null;
		try {
			reader = DirectoryReader.open(FSDirectory.open(Paths.get(indexPath)));
			IndexSearcher searcher = new IndexSearcher(reader);
			searcher.setSimilarity(similarity);

			BufferedReader in = null;
			in = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));

			QueryParser parser = new QueryParser("Body", analyzer);
			while (true) {
				System.out.println("Enter query: ");

				String line = in.readLine();

				if (line == null || line.length() == -1) {
					break;
				}

				line = line.trim();
				if (line.length() == 0) {
					break;
				}

				Query query;
				try {
					query = parser.parse(line);
				} catch (org.apache.lucene.queryparser.classic.ParseException e) {
					System.out.println("Error parsing query string.");
					continue;
				}

				TopDocs results = searcher.search(query, 100);
				ScoreDoc[] hits = results.scoreDocs;

				int numTotalHits = results.totalHits;
				System.out.println(numTotalHits + " total matching documents");

				for (int j = 0; j < hits.length; j++) {
					Document doc = searcher.doc(hits[j].doc);
					String answer = doc.get("Body");
					Integer AnswerId = doc.getField("AnswerId").numericValue().intValue();
					System.out.println("------------------------------------------");
					System.out.println("AnswerId: " + AnswerId);
					System.out.println("Answer: " + answer);
					System.out.println();
				}

				if (line.equals("")) {
					break;
				}
			}
			reader.close();
		} catch (IOException e) {
			try {
				reader.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
	}
	
	public void indexSearch_trec_eval(Analyzer analyzer, Similarity similarity, String resultname, String iPath) {

		IndexReader reader = null;
		try {
			reader = DirectoryReader.open(FSDirectory.open(Paths.get(iPath)));
			IndexSearcher searcher = new IndexSearcher(reader);
			searcher.setSimilarity(similarity);
						
			QueryParser parser = new QueryParser("Body", analyzer);
			
			// get decay field called "DaysPassed"
//			ValueSource days = new DoubleFieldSource("DaysPassed");
//			FunctionQuery newFunc = new FunctionQuery(days);
			
			List<Float> qMeanWords = new ArrayList<Float>();
			File results_file = new File(dir_eval, file_results);
			FileWriter archivo = new FileWriter(results_file);
			archivo.write(String.format("%20s %20s %20s %20s %20s %20s \r\n", "QueryID", "Q0", "DocID", "Rank", "Score", "RunID"));
			try(BufferedReader br = new BufferedReader(new FileReader(path_queries))) {
			    for(String line; (line = br.readLine()) != null; ) {
					float meanWords = 0;
					int words = 0;
			    	String id_query = line.substring(0, line.indexOf(':'));
			    	String query_line = line.substring(line.indexOf(':')+1, line.length());
			    	
//			    	System.out.println("Number " + id_query + " Line " + query_line + "\n");
			    	
			    	Query query;
					try {
						query = parser.parse(query_line);
//						query = new CustomScoreQuery(query, newFunc);
					} catch (org.apache.lucene.queryparser.classic.ParseException e) {
						System.out.println("Error parsing query string.");
						continue;
					}
				
					// fazer sort nos docs por field
//					Sort sorter = new Sort(); // new sort object
//					String field = "CreationDate"; // enter the field to sort by
//					Type type = Type.STRING; // since your field is long type
//					boolean descending = false; // ascending by default
//					SortField sortField = new SortField(field, type, descending);
//					sorter.setSort(sortField); // now set the sort field
					
					TopDocs results = searcher.search(query, 2000);
					ScoreDoc[] hits = results.scoreDocs;
					
					int numTotalHits = results.totalHits;
					//System.out.println(numTotalHits + " total matching documents");

					for (int j = 0; j < hits.length; j++) {
						Document doc = searcher.doc(hits[j].doc);
						Integer AnswerId = doc.getField("AnswerId").numericValue().intValue();
						archivo.write(String.format("%20s %20s %20s %20s %20s %20s \r\n", id_query, "Q0", AnswerId, String.valueOf(j+1), (hits[j].score)*100, "run-1"));

						//System.out.println("QueryID: "+ id_query);
						//System.out.println("DocID: " + AnswerId);
						//System.out.println("Rank: " + j+1);
						//System.out.println("Score " + (hits[j].score)*100);
						//System.out.println("DocID: " + AnswerId);
						
//						String explain = searcher.explain(query, hits[j].doc).toString();
//						System.out.println(explain); //https://chrisperks.co/2017/06/06/explaining-lucene-explain/
						// 1� somar todas as palavras dos 100 documentos
						String lines = doc.getField("Body").stringValue();
						StringTokenizer st = new StringTokenizer(lines);
//						System.out.println(String.format("Doc {%d} num words :{%d}", doc.getField("AnswerId").numericValue().intValue(),st.countTokens()));
						words += st.countTokens();
					}
					meanWords = words/100;
//					System.out.println(String.format("Query {%s} mean words :{%f}\n\n", id_query,meanWords));
					qMeanWords.add(meanWords);
					
			    }
			    long sum = sumArray(qMeanWords);
			    float meanAllQueries = sum/31;
			    double std = std(qMeanWords,meanAllQueries);
			    File statsFile = new File(dir_eval, String.format("{%s}{%s}Stats.txt", similarity,analyzer));
				FileWriter f = new FileWriter(statsFile);
				f.write(String.format("Words mean over all queries: {%f} - Std: {%f}\n", meanAllQueries,std));
				f.close();
		    	Process p = Runtime.getRuntime().exec("cmd /c \"cd eval && .\\trec_eval qrels.offline.txt results.txt -q\""); 
		        BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
		        String readline;
		        String pathResults = String.format("{%s}.txt", resultname);
		        
		        File results_test = new File(dir_eval, pathResults);
				FileWriter archivos = new FileWriter(results_test);
		        while ((readline = r.readLine()) != null) {
		        	archivos.write(String.format(readline + "\n"));
//		        	System.out.println(readline);
		        }
				archivos.close();
				System.out.println("DONE");
			}
			archivo.close();
			reader.close();
		} catch (IOException e) {
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

}