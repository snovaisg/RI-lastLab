package medical.driver;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.lucene.document.Field;
import org.apache.lucene.document.IntPoint;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.TextField;

import medical.text.main.NxmlParser;
import medical.text.main.PmcFigureMention;
import medical.text.main.PmcFigureRecord;
import medical.text.main.PmcFull;
import medical.text.model.Document;
import medical.text.model.Figure;
import medical.text.model.FigureMention;
import medical.text.model.Journal;
import medical.text.model.PublicationDate;
import medical.text.nxml.TextRange;

/*
 * Example class to demonstrate the usage of the NXML Parser
 */
public class Driver {

//	public static final String DATA_PATH = "/home/amourao/code/jmedical/iclefj/data/mesh/";

	public static void main(String args[]) throws IOException {
		// Provide the path to NXML file on disk.
		
		Driver driv = new Driver();
//		driv.SaveCSV("./data/docs");
		
		String inputFileName = "/Users/simaonovais/Desktop/Mestrado/RI/Java/RI-lab3/data/docs/1033556.nxml";
		
		File inputFile = new File(inputFileName);

		NxmlParser nxml = new NxmlParser();
		PmcFull articleMeta = new PmcFull();
		articleMeta.setLocalFileName(inputFile.getName());

		LinkedList<PmcFigureRecord> recordList = new LinkedList<PmcFigureRecord>();
		LinkedList<PmcFigureMention> mentionList = new LinkedList<PmcFigureMention>();

		try {
			nxml.getPmcFields(inputFile.getPath(), recordList, articleMeta,
					mentionList);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out
					.println("Error parsing the input NXML: " + inputFileName);
			e.printStackTrace();
			System.exit(0);
		}

		// Create a unique set of figure mentions.
		HashMap<String, List<FigureMention>> figureIdToMention = new HashMap<String, List<FigureMention>>();
		for (int i = 0; i < mentionList.size(); i++) {
			PmcFigureMention pmcFigureMention = mentionList.get(i);
			FigureMention figureMention = new FigureMention();
			figureMention.setParagraph(pmcFigureMention.getParagraph()
					.toString());
			String figureId = pmcFigureMention.getFigureId().trim();

			if (!figureIdToMention.containsKey(figureId)) {
				figureIdToMention.put(figureId, new ArrayList<FigureMention>());
			}

			List<FigureMention> figureMentions = figureIdToMention
					.get(figureId);

			if (!figureMentions.contains(figureMention)) {
				figureMentions.add(figureMention);
			}
		}

		// Create a Document object and populate it with extracted data.
		Document document = new Document();
		document.setArticleDOI(articleMeta.getArticleDOI());
		document.setArticleURL(articleMeta.getArticleURL());
		document.setLocalFileName(inputFile.getAbsolutePath());
		document.setTitle(articleMeta.getTitle().trim());
		document.setPmcid(articleMeta.getPmcid());
		document.setPmid(articleMeta.getPmid());
		document.setPublisherName(articleMeta.getPublisherName());
		document.setLicenseTypes(articleMeta.getLicenseTypes());
		document.setLicenseURLs(articleMeta.getLicenseURLs());
		document.setAbstractText(articleMeta.getAbstractText());
		document.setCategories(articleMeta.getSubjects());
		document.setKeywords(articleMeta.getKeywords());
		document.setAuthors(articleMeta.getAuthors());
		document.setFullText(articleMeta.getFullText());
		// Populate Journal info if the data is available.
		if (articleMeta.getJournalId() != null
				|| articleMeta.getJournalIssn() != null
				|| articleMeta.getJournalTitle() != null) {
			Journal journal = new Journal();
			journal.setId(articleMeta.getJournalId());
			journal.setIssn(articleMeta.getJournalIssn());
			journal.setTitle(articleMeta.getJournalTitle());
			document.setJournal(journal);
		}

		// Populate the date field if data is available.
		if ((articleMeta.getDay() != null && !articleMeta.getDay().trim()
				.equals(""))
				|| (articleMeta.getMonth() != null && !articleMeta.getMonth()
						.trim().equals(""))
				|| (articleMeta.getYear() != null && !articleMeta.getYear()
						.trim().equals(""))) {
			PublicationDate publicationDate = new PublicationDate();
			document.setPublicationDate(publicationDate);
			publicationDate.setDate(articleMeta.getDay());
			publicationDate.setMonth(articleMeta.getMonth());
			publicationDate.setYear(articleMeta.getYear());
			publicationDate.setType("epub");
		}

		// Link the parsed images with captions and mentions.
		LinkedList<Figure> figures = new LinkedList<Figure>();
		for (int i = 0; i < recordList.size(); i++) {

			Figure figure = new Figure();

			figures.add(figure);
			PmcFigureRecord pmcFigureRecord = recordList.get(i);

			figure.setFigureID(pmcFigureRecord.getFigureID().trim());
			figure.setIriList(pmcFigureRecord.getIriList());
			figure.setIriVideoMap(pmcFigureRecord.getIriVideoMap());
			figure.setLabel(pmcFigureRecord.getLabel().trim());

			LinkedList<TextRange> textRanges = nxml.getBoldAndItalicTextRanges(pmcFigureRecord);
			String caption = "";
			if (pmcFigureRecord.getCaption() != null) {
				caption = pmcFigureRecord.getCaption().trim().replace("\n", " ");
			}
			figure.setCaption(caption);

			List<FigureMention> figureMentions = figureIdToMention.get(figure
					.getFigureID());

			if (figureMentions == null) {
				figureMentions = new ArrayList<FigureMention>();
			}
			figure.setFigureMentions(figureMentions);
		}
		// Add figures to the Document object.
		document.setFigures(figures);

		// Print article info.
		System.out
				.println("-------------------- Article Metadata --------------------");
		System.out.println("Article PMCID: " + document.getPmcid());
		System.out.println("Article PMID: " + document.getPmid());
		System.out.println("Article Title: " + document.getTitle());
		System.out.println("Article Pub. date: "
				+ document.getPublicationDate());
		System.out.println("Article authors: " + articleMeta.getAuthors());

		System.out.print("Article Categories: ");
		for (String cat : document.getCategories())
			System.out.print(cat + "; ");
		System.out.println();

		System.out.print("Article Keywords: ");
		for (String cat : document.getKeywords())
			System.out.print(cat + "; ");
		System.out.println();

		System.out.println("Article Title: " + document.getTitle());
		System.out.println("Article Authors: " + document.getAuthors());
		if (document.getJournal() != null) {
			System.out.println("Article Journal ID: "
					+ document.getJournal().getId());
			System.out.println("Article Journal ISSN: "
					+ document.getJournal().getIssn());
			System.out.println("Article Journal Title: "
					+ document.getJournal().getTitle());
		}
		System.out.println("Article Publisher: " + document.getPublisherName());
		System.out.println("Article Abstract: " + document.getAbstractText());
		System.out
				.println("Number of Figures: " + document.getFigures().size());
		System.out.println("");

		// Print article image info.
		if (document.getFigures().size() > 0) {
			System.out
					.println("-------------------- Figure List --------------------");
		}
		for (Figure figure : document.getFigures()) {
			if (!figure.getIriList().isEmpty()) {
				System.out.println("Figure ID: " + figure.getFigureID());
				System.out.println("Figure Label: " + figure.getLabel());
				System.out.println("Figure Identifier: "
						+ figure.getIriList().get(0));
				System.out.println("Figure Caption: " + figure.getCaption());
				StringBuilder mentionBuilder = new StringBuilder();
				for (FigureMention mention : figure.getFigureMentions()) {
					if (mentionBuilder.length() == 0) {
						mentionBuilder.append(mention.getParagraph());
					} else {
						mentionBuilder.append(" " + mention.getParagraph());
					}
				}
				System.out.println("Figure Mention: "
						+ mentionBuilder.toString());
				System.out.println("----------");
				System.out.println("");
			}
		}

		System.out
				.println("-------------------- SDFSFSDt --------------------");
		System.out.println(document.getFullText());
	}
	
	public org.apache.lucene.document.Document parseNxmlToLuceneceDoc(String docPath) {
		org.apache.lucene.document.Document d = new org.apache.lucene.document.Document();
		File inputFile = new File(docPath);

		NxmlParser nxml = new NxmlParser();
		PmcFull articleMeta = new PmcFull();
		articleMeta.setLocalFileName(inputFile.getName());

		LinkedList<PmcFigureRecord> recordList = new LinkedList<PmcFigureRecord>();
		LinkedList<PmcFigureMention> mentionList = new LinkedList<PmcFigureMention>();

		try {
			nxml.getPmcFields(inputFile.getPath(), recordList, articleMeta,
					mentionList);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out
					.println("Error parsing the input NXML: " + docPath);
			e.printStackTrace();
			System.exit(0);
		}

		// Create a unique set of figure mentions.
		HashMap<String, List<FigureMention>> figureIdToMention = new HashMap<String, List<FigureMention>>();
		for (int i = 0; i < mentionList.size(); i++) {
			PmcFigureMention pmcFigureMention = mentionList.get(i);
			FigureMention figureMention = new FigureMention();
			figureMention.setParagraph(pmcFigureMention.getParagraph()
					.toString());
			String figureId = pmcFigureMention.getFigureId().trim();

			if (!figureIdToMention.containsKey(figureId)) {
				figureIdToMention.put(figureId, new ArrayList<FigureMention>());
			}

			List<FigureMention> figureMentions = figureIdToMention
					.get(figureId);

			if (!figureMentions.contains(figureMention)) {
				figureMentions.add(figureMention);
			}
		}

		// Create a Document object and populate it with extracted data.
		Document document = new Document();
		document.setArticleDOI(articleMeta.getArticleDOI());
		document.setArticleURL(articleMeta.getArticleURL());
		document.setLocalFileName(inputFile.getAbsolutePath());
		document.setTitle(articleMeta.getTitle().trim());
		document.setPmcid(articleMeta.getPmcid());
		document.setPmid(articleMeta.getPmid());
		document.setPublisherName(articleMeta.getPublisherName());
		document.setLicenseTypes(articleMeta.getLicenseTypes());
		document.setLicenseURLs(articleMeta.getLicenseURLs());
		document.setAbstractText(articleMeta.getAbstractText());
		document.setCategories(articleMeta.getSubjects());
		document.setKeywords(articleMeta.getKeywords());
		document.setAuthors(articleMeta.getAuthors());
		document.setFullText(articleMeta.getFullText());
		// Populate Journal info if the data is available.
		if (articleMeta.getJournalId() != null
				|| articleMeta.getJournalIssn() != null
				|| articleMeta.getJournalTitle() != null) {
			Journal journal = new Journal();
			journal.setId(articleMeta.getJournalId());
			journal.setIssn(articleMeta.getJournalIssn());
			journal.setTitle(articleMeta.getJournalTitle());
			document.setJournal(journal);
		}

		// Populate the date field if data is available.
		if ((articleMeta.getDay() != null && !articleMeta.getDay().trim()
				.equals(""))
				|| (articleMeta.getMonth() != null && !articleMeta.getMonth()
						.trim().equals(""))
				|| (articleMeta.getYear() != null && !articleMeta.getYear()
						.trim().equals(""))) {
			PublicationDate publicationDate = new PublicationDate();
			document.setPublicationDate(publicationDate);
			publicationDate.setDate(articleMeta.getDay());
			publicationDate.setMonth(articleMeta.getMonth());
			publicationDate.setYear(articleMeta.getYear());
			publicationDate.setType("epub");
		}

		// Link the parsed images with captions and mentions.
		LinkedList<Figure> figures = new LinkedList<Figure>();
		for (int i = 0; i < recordList.size(); i++) {

			Figure figure = new Figure();

			figures.add(figure);
			PmcFigureRecord pmcFigureRecord = recordList.get(i);

			figure.setFigureID(pmcFigureRecord.getFigureID().trim());
			figure.setIriList(pmcFigureRecord.getIriList());
			figure.setIriVideoMap(pmcFigureRecord.getIriVideoMap());
			figure.setLabel(pmcFigureRecord.getLabel().trim());

			LinkedList<TextRange> textRanges = nxml.getBoldAndItalicTextRanges(pmcFigureRecord);
			String caption = "";
			if (pmcFigureRecord.getCaption() != null) {
				caption = pmcFigureRecord.getCaption().trim().replace("\n", " ");
			}
			figure.setCaption(caption);

			List<FigureMention> figureMentions = figureIdToMention.get(figure
					.getFigureID());

			if (figureMentions == null) {
				figureMentions = new ArrayList<FigureMention>();
			}
			figure.setFigureMentions(figureMentions);
		}
		// Add figures to the Document object.
		
		
		document.setFigures(figures);
		d.add(new IntPoint("PMCID", Integer.parseInt(document.getPmcid())));
		d.add(new StoredField("PMCID", Integer.parseInt(document.getPmcid())));
		d.add(new TextField("Body", document.getFullText().toString(), Field.Store.YES));
		d.add(new TextField("Title", document.getTitle().toString(), Field.Store.YES));
		d.add(new TextField("Keywords", document.getKeywords().toString(), Field.Store.YES));
		d.add(new TextField("Abstract", document.getAbstractText().toString(), Field.Store.YES));
		if (!(figures.isEmpty())) {
			d.add(new TextField("Fig1Caption", document.getFigures().get(0).getCaption(), Field.Store.YES));
		}
		return d;
	}
	
	
	public void SaveCSV(String docPath) {

		PrintWriter pw;
		try {
			pw = new PrintWriter(new File("dataAnalysis.csv"));
			 StringBuilder sb = new StringBuilder();
			 
		        sb.append("PMCID");
		        sb.append(',');
		        sb.append("Body");
		        sb.append(',');
		        sb.append("Title");
		        sb.append(',');
		        sb.append("Keywords");
		        sb.append(',');
		        sb.append("Abstract");
		        sb.append(',');
		        sb.append("Fig1Caption");
		        sb.append('\n');
			 
				File folder = new File(docPath);
				File[] listOfFiles = folder.listFiles();

				for (int i = 0; i < listOfFiles.length; i++) {
				  if (listOfFiles[i].isFile()) {
					String filePath = listOfFiles[i].getPath();
					
					org.apache.lucene.document.Document d = new org.apache.lucene.document.Document();
					File inputFile = new File(filePath);

					NxmlParser nxml = new NxmlParser();
					PmcFull articleMeta = new PmcFull();
					articleMeta.setLocalFileName(inputFile.getName());

					LinkedList<PmcFigureRecord> recordList = new LinkedList<PmcFigureRecord>();
					LinkedList<PmcFigureMention> mentionList = new LinkedList<PmcFigureMention>();

					try {
						nxml.getPmcFields(inputFile.getPath(), recordList, articleMeta,
								mentionList);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						System.out
								.println("Error parsing the input NXML: " + docPath);
						e.printStackTrace();
						System.exit(0);
					}

					// Create a unique set of figure mentions.
					HashMap<String, List<FigureMention>> figureIdToMention = new HashMap<String, List<FigureMention>>();
					for (int i1 = 0; i1 < mentionList.size(); i1++) {
						PmcFigureMention pmcFigureMention = mentionList.get(i1);
						FigureMention figureMention = new FigureMention();
						figureMention.setParagraph(pmcFigureMention.getParagraph()
								.toString());
						String figureId = pmcFigureMention.getFigureId().trim();

						if (!figureIdToMention.containsKey(figureId)) {
							figureIdToMention.put(figureId, new ArrayList<FigureMention>());
						}

						List<FigureMention> figureMentions = figureIdToMention
								.get(figureId);

						if (!figureMentions.contains(figureMention)) {
							figureMentions.add(figureMention);
						}
					}

					// Create a Document object and populate it with extracted data.
					Document document = new Document();
					document.setArticleDOI(articleMeta.getArticleDOI());
					document.setArticleURL(articleMeta.getArticleURL());
					document.setLocalFileName(inputFile.getAbsolutePath());
					document.setTitle(articleMeta.getTitle().trim());
					document.setPmcid(articleMeta.getPmcid());
					document.setPmid(articleMeta.getPmid());
					document.setPublisherName(articleMeta.getPublisherName());
					document.setLicenseTypes(articleMeta.getLicenseTypes());
					document.setLicenseURLs(articleMeta.getLicenseURLs());
					document.setAbstractText(articleMeta.getAbstractText());
					document.setCategories(articleMeta.getSubjects());
					document.setKeywords(articleMeta.getKeywords());
					document.setAuthors(articleMeta.getAuthors());
					document.setFullText(articleMeta.getFullText());
					// Populate Journal info if the data is available.
					if (articleMeta.getJournalId() != null
							|| articleMeta.getJournalIssn() != null
							|| articleMeta.getJournalTitle() != null) {
						Journal journal = new Journal();
						journal.setId(articleMeta.getJournalId());
						journal.setIssn(articleMeta.getJournalIssn());
						journal.setTitle(articleMeta.getJournalTitle());
						document.setJournal(journal);
					}

					// Populate the date field if data is available.
					if ((articleMeta.getDay() != null && !articleMeta.getDay().trim()
							.equals(""))
							|| (articleMeta.getMonth() != null && !articleMeta.getMonth()
									.trim().equals(""))
							|| (articleMeta.getYear() != null && !articleMeta.getYear()
									.trim().equals(""))) {
						PublicationDate publicationDate = new PublicationDate();
						document.setPublicationDate(publicationDate);
						publicationDate.setDate(articleMeta.getDay());
						publicationDate.setMonth(articleMeta.getMonth());
						publicationDate.setYear(articleMeta.getYear());
						publicationDate.setType("epub");
					}

					// Link the parsed images with captions and mentions.
					LinkedList<Figure> figures = new LinkedList<Figure>();
					for (int i1 = 0; i1 < recordList.size(); i1++) {

						Figure figure = new Figure();

						figures.add(figure);
						PmcFigureRecord pmcFigureRecord = recordList.get(i1);

						figure.setFigureID(pmcFigureRecord.getFigureID().trim());
						figure.setIriList(pmcFigureRecord.getIriList());
						figure.setIriVideoMap(pmcFigureRecord.getIriVideoMap());
						figure.setLabel(pmcFigureRecord.getLabel().trim());

						LinkedList<TextRange> textRanges = nxml.getBoldAndItalicTextRanges(pmcFigureRecord);
						String caption = "";
						if (pmcFigureRecord.getCaption() != null) {
							caption = pmcFigureRecord.getCaption().trim().replace("\n", " ");
						}
						figure.setCaption(caption);

						List<FigureMention> figureMentions = figureIdToMention.get(figure
								.getFigureID());

						if (figureMentions == null) {
							figureMentions = new ArrayList<FigureMention>();
						}
						figure.setFigureMentions(figureMentions);
					}
					// Add figures to the Document object.
					
					
					document.setFigures(figures);
					d.add(new IntPoint("PMCID", Integer.parseInt(document.getPmcid())));
					d.add(new StoredField("PMCID", Integer.parseInt(document.getPmcid())));
					d.add(new TextField("Body", document.getFullText().toString(), Field.Store.YES));
					d.add(new TextField("Title", document.getTitle().toString(), Field.Store.YES));
					d.add(new TextField("Keywords", document.getKeywords().toString(), Field.Store.YES));
					d.add(new TextField("Abstract", document.getAbstractText().toString(), Field.Store.YES));
			        sb.append(document.getPmcid().toString());
			        sb.append(',');
			        sb.append(document.getFullText().toString());
			        sb.append(document.getTitle().toString());
			        sb.append(',');
			        sb.append(document.getKeywords().toString());
			        sb.append(',');
			        sb.append(document.getAbstractText().toString());
			        sb.append(',');
					if (!(figures.isEmpty())) {
						d.add(new TextField("Fig1Caption", document.getFigures().get(0).getCaption(), Field.Store.YES));
						sb.append(document.getFigures().get(0).getCaption());
				        
					}
					else {
						sb.append("Fig1Caption");
					}
					sb.append('\n');
				  } else if (listOfFiles[i].isDirectory()) {
				    System.out.println(listOfFiles[i].getName());
				  }
				}
		        pw.write(sb.toString());
		        pw.close();
		        System.out.println("done!");
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	public static PmcFull getInfo(String inputFile) {
		// Provide the path to NXML file on disk.

		NxmlParser nxml = new NxmlParser();
		PmcFull articleMeta = new PmcFull();
		articleMeta.setLocalFileName(inputFile);

		LinkedList<PmcFigureRecord> recordList = new LinkedList<PmcFigureRecord>();
		LinkedList<PmcFigureMention> mentionList = new LinkedList<PmcFigureMention>();

		try {
			nxml.getPmcFields(inputFile, recordList, articleMeta, mentionList);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("Error parsing the input NXML: " + inputFile);
			e.printStackTrace();
			// System.exit(0);
		}

		// Create a unique set of figure mentions.
		HashMap<String, List<FigureMention>> figureIdToMention = new HashMap<String, List<FigureMention>>();
		for (int i = 0; i < mentionList.size(); i++) {
			PmcFigureMention pmcFigureMention = mentionList.get(i);
			FigureMention figureMention = new FigureMention();
			figureMention.setParagraph(pmcFigureMention.getParagraph()
					.toString());
			String figureId = pmcFigureMention.getFigureId().trim();

			if (!figureIdToMention.containsKey(figureId)) {
				figureIdToMention.put(figureId, new ArrayList<FigureMention>());
			}

			List<FigureMention> figureMentions = figureIdToMention
					.get(figureId);

			if (!figureMentions.contains(figureMention)) {
				figureMentions.add(figureMention);
			}
		}

		// Create a Document object and populate it with extracted data.
		Document document = new Document();
		document.setArticleDOI(articleMeta.getArticleDOI());
		document.setArticleURL(articleMeta.getArticleURL());
		document.setLocalFileName(inputFile);
		document.setTitle(articleMeta.getTitle().trim());
		document.setPmcid(articleMeta.getPmcid());
		document.setPmid(articleMeta.getPmid());
		document.setPublisherName(articleMeta.getPublisherName());
		document.setLicenseTypes(articleMeta.getLicenseTypes());
		document.setLicenseURLs(articleMeta.getLicenseURLs());
		document.setAbstractText(articleMeta.getAbstractText());

		// Populate Journal info if the data is available.
		if (articleMeta.getJournalId() != null
				|| articleMeta.getJournalIssn() != null
				|| articleMeta.getJournalTitle() != null) {
			Journal journal = new Journal();
			journal.setId(articleMeta.getJournalId());
			journal.setIssn(articleMeta.getJournalIssn());
			journal.setTitle(articleMeta.getJournalTitle());
			document.setJournal(journal);
		}

		// Populate the date field if data is available.
		if ((articleMeta.getDay() != null && !articleMeta.getDay().trim()
				.equals(""))
				|| (articleMeta.getMonth() != null && !articleMeta.getMonth()
						.trim().equals(""))
				|| (articleMeta.getYear() != null && !articleMeta.getYear()
						.trim().equals(""))) {
			PublicationDate publicationDate = new PublicationDate();
			document.setPublicationDate(publicationDate);
			publicationDate.setDate(articleMeta.getDay());
			publicationDate.setMonth(articleMeta.getMonth());
			publicationDate.setYear(articleMeta.getYear());
			publicationDate.setType("epub");
		}

		// Link the parsed images with captions and mentions.
		LinkedList<Figure> figures = new LinkedList<Figure>();
		for (int i = 0; i < recordList.size(); i++) {

			Figure figure = new Figure();

			figures.add(figure);
			PmcFigureRecord pmcFigureRecord = recordList.get(i);

			figure.setFigureID(pmcFigureRecord.getFigureID().trim());
			figure.setIriList(pmcFigureRecord.getIriList());
			figure.setIriVideoMap(pmcFigureRecord.getIriVideoMap());
			figure.setLabel(pmcFigureRecord.getLabel().trim());

			LinkedList<TextRange> textRanges = nxml.getBoldAndItalicTextRanges(pmcFigureRecord);
			String caption = "";
			if (pmcFigureRecord.getCaption() != null) {
				caption = pmcFigureRecord.getCaption().trim().replace("\n", " ");
			}
			figure.setCaption(caption);

			List<FigureMention> figureMentions = figureIdToMention.get(figure
					.getFigureID());

			if (figureMentions == null) {
				figureMentions = new ArrayList<FigureMention>();
			}
			figure.setFigureMentions(figureMentions);
		}
		// Add figures to the Document object.
		document.setFigures(figures);

		return articleMeta;

	}

	public static Document getDocInfo(String inputFile) {
		// Provide the path to NXML file on disk.

		NxmlParser nxml = new NxmlParser();
		PmcFull articleMeta = new PmcFull();
		articleMeta.setLocalFileName(inputFile);

		LinkedList<PmcFigureRecord> recordList = new LinkedList<PmcFigureRecord>();
		LinkedList<PmcFigureMention> mentionList = new LinkedList<PmcFigureMention>();

		try {
			nxml.getPmcFields(inputFile, recordList, articleMeta, mentionList);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("Error parsing the input NXML: " + inputFile);
			e.printStackTrace();
			// System.exit(0);
		}

		// Create a unique set of figure mentions.
		HashMap<String, List<FigureMention>> figureIdToMention = new HashMap<String, List<FigureMention>>();
		for (int i = 0; i < mentionList.size(); i++) {
			PmcFigureMention pmcFigureMention = mentionList.get(i);
			FigureMention figureMention = new FigureMention();
			figureMention.setParagraph(pmcFigureMention.getParagraph()
					.toString());
			String figureId = pmcFigureMention.getFigureId().trim();

			if (!figureIdToMention.containsKey(figureId)) {
				figureIdToMention.put(figureId, new ArrayList<FigureMention>());
			}

			List<FigureMention> figureMentions = figureIdToMention
					.get(figureId);

			if (!figureMentions.contains(figureMention)) {
				figureMentions.add(figureMention);
			}
		}

		// Create a Document object and populate it with extracted data.
		Document document = new Document();
		document.setArticleDOI(articleMeta.getArticleDOI());
		document.setArticleURL(articleMeta.getArticleURL());
		document.setLocalFileName(inputFile);
		document.setTitle(articleMeta.getTitle().trim());
		document.setPmcid(articleMeta.getPmcid());
		document.setPmid(articleMeta.getPmid());
		document.setPublisherName(articleMeta.getPublisherName());
		document.setLicenseTypes(articleMeta.getLicenseTypes());
		document.setLicenseURLs(articleMeta.getLicenseURLs());
		document.setAbstractText(articleMeta.getAbstractText());
		document.setCategories(articleMeta.getSubjects());
		document.setKeywords(articleMeta.getKeywords());
		document.setFullText(articleMeta.getFullText());
		document.setAuthors(articleMeta.getAuthors());
		// Populate Journal info if the data is available.
		if (articleMeta.getJournalId() != null
				|| articleMeta.getJournalIssn() != null
				|| articleMeta.getJournalTitle() != null) {
			Journal journal = new Journal();
			journal.setId(articleMeta.getJournalId());
			journal.setIssn(articleMeta.getJournalIssn());
			journal.setTitle(articleMeta.getJournalTitle());
			document.setJournal(journal);
		}

		// Populate the date field if data is available.
		if ((articleMeta.getDay() != null && !articleMeta.getDay().trim()
				.equals(""))
				|| (articleMeta.getMonth() != null && !articleMeta.getMonth()
						.trim().equals(""))
				|| (articleMeta.getYear() != null && !articleMeta.getYear()
						.trim().equals(""))) {
			PublicationDate publicationDate = new PublicationDate();
			document.setPublicationDate(publicationDate);
			publicationDate.setDate(articleMeta.getDay());
			publicationDate.setMonth(articleMeta.getMonth());
			publicationDate.setYear(articleMeta.getYear());
			publicationDate.setType("epub");
		}

		// Link the parsed images with captions and mentions.
		LinkedList<Figure> figures = new LinkedList<Figure>();
		for (int i = 0; i < recordList.size(); i++) {

			Figure figure = new Figure();

			figures.add(figure);
			PmcFigureRecord pmcFigureRecord = recordList.get(i);

			figure.setFigureID(pmcFigureRecord.getFigureID().trim());
			figure.setIriList(pmcFigureRecord.getIriList());
			figure.setIriVideoMap(pmcFigureRecord.getIriVideoMap());
			figure.setLabel(pmcFigureRecord.getLabel().trim());

			LinkedList<TextRange> textRanges = nxml.getBoldAndItalicTextRanges(pmcFigureRecord);
			String caption = "";
			if (pmcFigureRecord.getCaption() != null) {
				caption = pmcFigureRecord.getCaption().trim().replace("\n", " ");
			}
			figure.setCaption(caption);

			List<FigureMention> figureMentions = figureIdToMention.get(figure
					.getFigureID());

			if (figureMentions == null) {
				figureMentions = new ArrayList<FigureMention>();
			}
			figure.setFigureMentions(figureMentions);
		}
		// Add figures to the Document object.
		document.setFigures(figures);

		return document;

	}
}
