package medical.text.main;

import medical.text.nxml.TextRange;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.text.WordUtils;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 * Stax parser for PubMedCentral nxml files.
 * 
 * @author NLM iMedline project, contact ddemner@mail.nih.gov
 * @author http://archive.nlm.nih.gov/ridem/iti.html
 * @version 1.0 2011-01-07
 */

public class NxmlParser {

	static final String ArticleID = "article-id";
	static final String ADOI = "doi";
	static final String APMCID = "pmc";
	static final String APMID = "pmid";
	static final String ATITLE = "article-title";
	static final String ABSTRACT = "abstract";

	static final String CONTRIB = "contrib";
	static final String CONTRIB_SUR = "surname";
	static final String CONTRIB_GIV = "given-names";

	static final String IDTYPE = "pub-id-type";
	static final String FIGURE = "fig";
	static final String ALTERNATIVE = "alternatives";
	static final String MEDIA = "media";
	static final String FIGUREID = "id";
	static final String CAPTION = "caption";
	static final String BOLD = "bold";
	static final String ITALIC = "italic";
	static final String IRI = "graphic";
	static final String MIME = "mimetype";
	static final String HREF = "href";
	static final String LABEL = "label";
	static final String EXTLINK = "ext-link";
	static final String SELF_URI = "self-uri";
	static final String CONTENT_TYPE = "content-type";
	static final String PERMISSIONS = "permissions";
	static final String LICENSE = "license";
	static final String LICENSE_TYPE = "license-type";
	static final String COPYRIGHT = "copyright-statement";
	static final String COPYRIGHTHOLDER = "copyright-holder";
	static final String PARAGRPAPH = "p";
	static final String XREF = "xref";
	static final String PUBLISHER_NAME = "publisher-name";
	static final String JOURNAL_ID = "journal-id";
	static final String JOURNAL_META = "journal-meta";
	static final String JOURNAL_TITLE = "journal-title";
	static final String JOURNAL_ISSN = "issn";
	static final String PUB_DATE = "pub-date";
	static final String PUB_TYPE = "pub-type";
	static final String DAY = "day";
	static final String MONTH = "month";
	static final String YEAR = "year";
	static final String CREATIVE_COMMONS = "creativecommons";

	static final String SECTION = "sec";
	static final String BODY = "body";
	static final String LOCAL_TITLE = "title";
	static final String CATEGORIES = "article-categories";
	static final String SUBJECT = "subject";
	static final String SUBJECT_GROUP = "subj-group";
	
	static final String NAMED_CONTENT = "named-content";
	
	
	static final String TABLE_WRAP = "table-wrap";
	static final String DISP_FORMULA = "disp-formula";
	

	static final String KEYWORD_GROUP = "kwd-group";
	static final String KEYWORD = "kwd";

	static final String AMETA = "article-meta";
	boolean isAmeta = false;
	boolean isAbstract = false;
	boolean isContrib = false;
	boolean isContribSub = false;
	boolean isContribGiv = false;
	boolean isFigure = false;
	boolean isAlternative = false;
	boolean isCaption = false;
	boolean isLabel = false;
	boolean isTitle = false;
	boolean isPermission = false;
	boolean isLicense = false;
	boolean isCopyright = false;
	boolean isBold = false;
	boolean isItalic = false;
	boolean isParagraph = false;
	boolean isXRef = false;
	boolean isFigureXref = false;
	boolean isArticleXref = false;
	boolean isPublisherName = false;
	boolean isJournalMeta = false;
	boolean isJournalId = false;
	boolean isJournalTitle = false;
	boolean isJournalIssn = false;
	boolean isPubDate = false;
	boolean isDay = false;
	boolean isMonth = false;
	boolean isYear = false;
	
	boolean isNamedContent = false;
	
	boolean isTable = false;
	boolean isDispFormula = false; 

	boolean isSection = false;
	boolean isBody = false;
	boolean isLocalTitle = false;
	boolean isSubject = false;
	boolean isSubjectGroup = false;

	boolean isKeywordGroup = false;
	boolean isKeyword = false;

	public NxmlParser() {

	}

	public LinkedList<TextRange> getBoldAndItalicTextRanges(
			PmcFigureRecord pmcFigureRecord) {
		LinkedList<TextRange> textRanges = new LinkedList<TextRange>();
		textRanges.addAll(pmcFigureRecord.getBoldText());
		textRanges.addAll(pmcFigureRecord.getItalicText());
		return textRanges;
	}

	/**
	 * Parses a PubMedCentral nxml file. Extracts article meta information into
	 * a PmcMeta object. Extracts caption, figure ID,figure IRI, figure label,
	 * video info for each figure into a PmcFigureRecord object
	 * 
	 * @param infile
	 *            Path to a nxml file
	 * @param recordList
	 *            An ArrayList of PmcFigureRecords
	 * @param articleMeta
	 *            A PmcMeta object
	 * @return No return value
	 * @throws java.io.FileNotFoundException
	 * @throws javax.xml.stream.XMLStreamException
	 * 
	 */
	public void getPmcFields(String infile, List<PmcFigureRecord> recordList,
    		PmcFull articleMeta, List<PmcFigureMention> figureMentions) throws Exception {
        PmcFigureRecord record = null;
        String alternativesIri = null;
        String alternativesVideo = null;

        FileInputStream fis = new FileInputStream(infile);

        XMLInputFactory factory = XMLInputFactory
                .newInstance();
        XMLStreamReader staxXmlReader = factory
                .createXMLStreamReader(fis);
        Stack<String> nodeStack = new Stack<String>();

        StringBuilder currentParagraph = new StringBuilder();
        StringBuilder currentTitle = new StringBuilder();
        StringBuilder abstractText = new StringBuilder();
        StringBuilder fullText = new StringBuilder();
        StringBuilder captionText = new StringBuilder();
        List<String> categoriesList = new LinkedList<String>();
        categoriesList.add("Other");
        List<String> keywordList = new LinkedList<String>();
        String currentKeyword = "";
        
        StringBuilder authorGiv = new StringBuilder();
        StringBuilder authorSur = new StringBuilder();

        for (int event = staxXmlReader.next(); event != XMLStreamConstants.END_DOCUMENT; event = staxXmlReader
                .next()) {
            switch (event) {
                case XMLStreamConstants.START_ELEMENT:
                    String localName = staxXmlReader.getLocalName();
                    nodeStack.push(localName); // push the start element to the node stack
                    if (localName.equals(AMETA))
                        isAmeta = true;
                    if (localName.equals(ArticleID)) {
                        int end = staxXmlReader.getAttributeCount();
                        for (int i = 0; i < end; i++) {
                            if (staxXmlReader.getAttributeLocalName(i).equals(
                                    IDTYPE)) {
                                String value = staxXmlReader
                                        .getAttributeValue(i);
                                if (value.equals(ADOI)){
                                    articleMeta.setArticleDOI(staxXmlReader
                                    		.getElementText());
                                	nodeStack.pop(); // call pop after extracting element text
                                }
                                if (value.equals(APMCID)){
                                    articleMeta.setPmcid(staxXmlReader
                                            .getElementText());
                                	nodeStack.pop(); // call pop after extracting element text
                                }
                                if (value.equals(APMID)){
                                    articleMeta.setPmid(staxXmlReader
                                            .getElementText());
                                    nodeStack.pop(); // call pop after extracting element text
                                }
                            }
                        }
                    }
                    if (isAmeta && localName.equals(ATITLE))
                        isTitle = true;

                    if (localName.equals(EXTLINK) ) {
                    	// set Article URL if it does not contain "http://creativecommons.org/licenses/by/2.0"
                    	if(isAmeta && (articleMeta.getArticleURL() == null || articleMeta.getArticleURL() == "")){
                    		int posExtLink = nodeStack.search(EXTLINK);
                    		int posAMeta = nodeStack.search(AMETA);

                    		//only use ext-link tag directly below the article-meta tag
                    		if(posAMeta - posExtLink == 1){
                    			int end = staxXmlReader.getAttributeCount();
                    			for (int i = 0; i < end; i++) {
                    				if (staxXmlReader.getAttributeLocalName(i).endsWith(HREF)) {
                    					if (!staxXmlReader.getAttributeValue(i).contains(CREATIVE_COMMONS)) {
                    						articleMeta.setArticleURL(staxXmlReader.getAttributeValue(i));
                    					}
                    				}
                    			}
                    		}
                    	}
                    	if(isLicense){
                    		int posAmeta = nodeStack.search(AMETA);
                    		int posPermissions = nodeStack.search(PERMISSIONS);
                    		int posLicense = nodeStack.search(LICENSE);

                    		if(posAmeta - posPermissions == 1 && posPermissions - posLicense == 1){
                    			int end = staxXmlReader.getAttributeCount();
                    			for (int i = 0; i < end; i++) {
                    				if (staxXmlReader.getAttributeLocalName(i).endsWith(HREF)) {
                    					articleMeta.addLicenseURL(staxXmlReader.getAttributeValue(i));
                    				}
                    			}
                    		}
                    	}
                    }
                    
		        	if (isAmeta && localName.equals(ABSTRACT)) {
		        		int posAbstract = nodeStack.search(ABSTRACT);
		        		int posAMeta = nodeStack.search(AMETA);
		        		
		        		//only use self-uri tag directly below the article-meta tag
		        		if(posAMeta - posAbstract == 1){
			        		isAbstract = true;
		        		}
		        	}
		        	

		        	//TODO
		        	
		        	if (localName.equals(CONTRIB))
		        		isContrib = true;
		        	
		        	if (localName.equals(CONTRIB_GIV))
		        		isContribGiv = true;
		        	
		        	if (localName.equals(CONTRIB_SUR))
		        		isContribSub = true;
		        	
		        	if (localName.equals(TABLE_WRAP))
                        isTable = true;	
		        	
		        	if (localName.equals(DISP_FORMULA))
		        		isDispFormula = true;
		        			        		        	
		        	if (localName.equals(BODY))
                        isBody = true;
		        	
		        	if (isBody && localName.equals(SECTION))
                        isSection = true;
		        	
		        	if (isSection && localName.equals(LOCAL_TITLE)) {
		        		isLocalTitle = true;
                        currentTitle = new StringBuilder();
                    }
		        	
		        	if (isAmeta && localName.equals(CATEGORIES))
		        		isSubject = true;
		        	
		        	if (localName.equals(NAMED_CONTENT))
		        		isNamedContent = true;
		        	
		        	
		        	//TODO
                    
		        	if (isAmeta && localName.equals(SELF_URI)) {
		        		int posSelfLink = nodeStack.search(SELF_URI);
		        		int posAMeta = nodeStack.search(AMETA);
		        		
		        		//only use self-uri tag directly below the article-meta tag
		        		if(posAMeta - posSelfLink == 1){
	                    	int count = staxXmlReader.getAttributeCount();
			        		String contentType = "";
			        		String xlinkHref = "";
	                    	for (int i=0; i<count; i++) {
	                    		if (staxXmlReader.getAttributeLocalName(i).endsWith(HREF)) 
			                    	xlinkHref = staxXmlReader.getAttributeValue(i);
			                    if (staxXmlReader.getAttributeLocalName(i).equals(CONTENT_TYPE)) 
			                    	contentType = staxXmlReader.getAttributeValue(i);
			                }
			                
	                    	//ignore the self-uri if points to a pdf
			                if(!contentType.equalsIgnoreCase("pdf") && !xlinkHref.toLowerCase().endsWith(".pdf"))
			                	articleMeta.setArticleURL(xlinkHref);
		        		}
		        	}
                    
                    if (localName.equals(FIGURE)) {
                        record = new PmcFigureRecord();
                        isFigure = true;
                        int end = staxXmlReader.getAttributeCount();
                        for (int i = 0; i < end; i++) {
                            if (staxXmlReader.getAttributeLocalName(i)
                                    .equalsIgnoreCase(FIGUREID))
                                record.figureID = staxXmlReader
                                        .getAttributeValue(i);
                        }
                    }
                    if (isFigure && localName.equals(CAPTION))
                        isCaption = true;

                    if (isFigure && localName.equals(LABEL))
                        isLabel = true;
                    
                    
                    if (localName.equals(SUBJECT_GROUP)){
                    	isSubjectGroup = true;
                    	/*
                        for (int i = 0; i < end; i++) {
                        	
                            if (staxXmlReader.getAttributeLocalName(i).equals(
                                    "subj-group-type")) {
                                String value = staxXmlReader
                                        .getAttributeValue(i);
                                if (value.equalsIgnoreCase("heading"))
                                	isSubjectGroup = true;
                            }
                        }*/
                    }
                        

                    if(isFigure && localName.equals(ALTERNATIVE)) {
	        			isAlternative = true;
	        			alternativesIri = null;
	        			alternativesVideo = null;
	        		}

		        	if(isFigure && localName.equals(MEDIA)){
		        		int posFigure = nodeStack.search(FIGURE);
		        		int posAlternative = nodeStack.search(ALTERNATIVE);
		        		int posMedia = nodeStack.search(MEDIA);
		        		
		        		//use media tags only directly below the alternative tag, which in turn is directly under the figure tag
		        		//this is to avoid errors in extracting videos.
		        		if(posFigure - posAlternative == 1 && posAlternative - posMedia ==1){
		        			String videoName = "";
		        			String mime = "";
			                int end = staxXmlReader.getAttributeCount();
			                for (int i = 0; i < end; i++) {
			                    if (staxXmlReader.getAttributeLocalName(i).endsWith(HREF)) {
			                    	videoName = staxXmlReader.getAttributeValue(i);
			                    }
			                    if (staxXmlReader.getAttributeLocalName(i).endsWith(MIME)) {
			                    	mime = staxXmlReader.getAttributeValue(i); 
			                    }
			                }
			                if(mime.trim().equalsIgnoreCase("video")){
			                	alternativesVideo = FilenameUtils.getBaseName(videoName);
			                }
		        		}
		        	}
                    
                    if (isFigure && localName.equals(IRI)) {
		        		int posFigure = nodeStack.search(FIGURE);
		        		int posAlternative = nodeStack.search(ALTERNATIVE);
		        		int posIri = nodeStack.search(IRI);
		        		
		        		//use the graphic link only directly below the figure tag or alternative tag in case of media attached
		        		//this will avoid errors like extraction of links embedded in the caption sections etc.
		        		if((posFigure - posIri == 1) || (posFigure - posAlternative == 1 && posAlternative - posIri == 1)){
	                        int end = staxXmlReader.getAttributeCount();
	                        for (int i = 0; i < end; i++) {
	                            if (staxXmlReader.getAttributeLocalName(i)
	                                    .endsWith(HREF)){
	                                //record.iri = staxXmlReader.getAttributeValue(i);
	                            	alternativesIri = staxXmlReader.getAttributeValue(i);
	                            	record.addIri(alternativesIri);
	                            }
	                        }
		        		}
                    }
                   
                    if (localName.equals(PERMISSIONS))
                        isPermission = true;
                    
                    if (localName.equals(LICENSE)) {
                    	isLicense = true;
		        		int posAmeta = nodeStack.search(AMETA);
		        		int posPermissions = nodeStack.search(PERMISSIONS);
		        		int posLicense = nodeStack.search(LICENSE);
		        		
		        		if(posAmeta - posPermissions == 1 && posPermissions - posLicense == 1){
	                        int end = staxXmlReader.getAttributeCount();
	                        
	                        for (int i = 0; i < end; i++) {
	                            if (staxXmlReader.getAttributeLocalName(i)
	                                    .endsWith(LICENSE_TYPE)){
	                            	articleMeta.addLicenseType(staxXmlReader.getAttributeValue(i));
	                            }
	                            if (staxXmlReader.getAttributeLocalName(i)
	                                    .endsWith(HREF)){
	                            	articleMeta.addLicenseURL(staxXmlReader.getAttributeValue(i));
	                            }
	                        }
		        		}
                    }                

                    if (localName.equals(PARAGRPAPH)) {
                        isParagraph = true;
                        currentParagraph = new StringBuilder();
                    }
                                        
                    if (isCaption && localName.equals(BOLD)) {
                        isBold = true;
                    }


                    if (isCaption && localName.equals(ITALIC)) {
                        isItalic = true;
                    }

                    if (localName.equals(XREF)) {
                        int end = staxXmlReader.getAttributeCount();
                        isXRef = true;
                        isFigureXref = false;
                        isArticleXref = false;
                        
                        String refType = null;
                        String rid = null;
                        for (int i = 0; i < end; i++) {
                            if (staxXmlReader.getAttributeLocalName(i).equalsIgnoreCase("ref-type")) {
                                refType = staxXmlReader.getAttributeValue(i);
                            }
                            if (staxXmlReader.getAttributeLocalName(i).equalsIgnoreCase("rid")) {
                                rid = staxXmlReader.getAttributeValue(i);
                            }
                        }

                        if (refType != null && refType.equalsIgnoreCase("fig")) {
                            //todo create figureMention
                        	isFigureXref = true;
                            PmcFigureMention pmcFigureMention = new PmcFigureMention();
                            pmcFigureMention.setParagraph(currentParagraph);
                            pmcFigureMention.setFigureId(rid);
                            figureMentions.add(pmcFigureMention);
                        } else if (refType != null && refType.equalsIgnoreCase("bibr")) {
                        	isArticleXref = true;
                        }
                    }

                    if (localName.equals(PUBLISHER_NAME)) {
                        isPublisherName = true;
                    }

                    if (localName.equals(JOURNAL_META)) {
                        isJournalMeta = true;
                    }

                    if (localName.equals(JOURNAL_ID)) {
                        isJournalId = true;
                    }

                    if (localName.equals(JOURNAL_TITLE)) {
                        isJournalTitle = true;
                    }

                    if (localName.equals(JOURNAL_ISSN)) {
                        isJournalIssn = true;
                    }

                    if (localName.equals(PUB_DATE)) {
                    	isPubDate = true;
//                        int end = staxXmlReader.getAttributeCount();
//                        for (int i = 0; i < end; i++) {
//                            if (staxXmlReader.getAttributeLocalName(i)
//                                    .equalsIgnoreCase(PUB_TYPE)) {
//                                String s = staxXmlReader
//                                        .getAttributeValue(i);
//
//                                if (s.equals("epub")) {
//                                    isPubDate = true;
//                                }
//                            }
//                        }
                    }

                    if (localName.equals(DAY)) {
                        isDay = true;
                    }

                    if (localName.equals(MONTH)) {
                        isMonth = true;
                    }

                    if (localName.equals(YEAR)) {
                        isYear = true;
                    }
                    
                    if (isAmeta && localName.equals(KEYWORD_GROUP)){
                    	isKeywordGroup = true;
                    }
                    
                    if (isKeywordGroup && localName.equals(KEYWORD)){
                    	isKeyword = true;
                    }

                    break;
                case XMLStreamConstants.CHARACTERS:
                	
                	if (isContrib){
                		String text = staxXmlReader.getText();
                		if (isContribGiv){
                			authorGiv.append(text);
                		}
                		if (isContribSub){
                			authorSur.append(text);
                		}
                	}
                	
                    if (isCaption) {

                        if (isBold) {
                            String text = staxXmlReader.getText();
                            TextRange tr = new TextRange();
                            tr.text = text;
                            tr.startIndex = record.caption == null ? 0 : record.caption.length();
                            tr.endIndex = tr.startIndex + text.length();
                            record.boldText.add(tr);
                        }

                        if (isItalic) {
                            String text = staxXmlReader.getText();
                            TextRange tr = new TextRange();
                            tr.text = text;
                            tr.startIndex = record.caption == null ? 0 : record.caption.length();
                            tr.endIndex = tr.startIndex + text.length();
                            record.italicText.add(tr);
                        }

                        if (record.caption == null)
                            record.caption = staxXmlReader.getText();
                        else
                            record.caption += staxXmlReader.getText();
                    }
                    if (isLabel) {
                        if (record.label == null)
                            record.label = staxXmlReader.getText();
                        else
                            record.label += staxXmlReader.getText();

                    }
                    if (isTitle) {
                        if (articleMeta.title == null)
                            articleMeta.title = staxXmlReader.getText();
                        else
                            articleMeta.title += staxXmlReader.getText();
                    }

                    if (isParagraph) {
                    	//if (isArticleXref){
                    	//	currentParagraph.append(" [" + staxXmlReader.getText() + "]");
                    	//	System.out.println(" [" + staxXmlReader.getText() + "]");

                    	if (!isArticleXref && !isDispFormula){
                    		if (!staxXmlReader.getText().trim().isEmpty()){
                    			currentParagraph.append(staxXmlReader.getText().replace("\n", " "));
                    		}
                    		if (isNamedContent)
                    			currentParagraph.append(" ");
                    			
                    		//System.out.println(staxXmlReader.getText());
                    	}
                    		
                    	
                    }
                    
                    //TODO
                    if (isLocalTitle) {
                    	currentTitle.append(staxXmlReader.getText().replace("\n", " ") + " ");
                    }
                    
                    if (isSubjectGroup) {
                    	String category = WordUtils.capitalizeFully(staxXmlReader.getText());
                    	if (!categoriesList.contains(category)){
                    		if (categoriesList.get(0).equals("Other")){
	                    		if (category.contains("Case"))
	                    			categoriesList.set(0, "Case Report");
	                    		else if (category.contains("Original") || category.contains("Research") || category.contains("Article")) 
		                    			categoriesList.set(0, "Research Article");
	                    		else if (category.contains("Review")) 
	                    			categoriesList.set(0, "Review");
                    		}
                    	}
                		if (!categoriesList.contains(category)){
                			categoriesList.add(category);
                		}
                    		
                    }
                    //TODO
                    if (isKeyword){
                    	if (!staxXmlReader.getText().isEmpty())
                    		currentKeyword += staxXmlReader.getText().replace("\n", " ");
                    }
                    	

                    if (isPublisherName) {
                        articleMeta.publisherName = staxXmlReader.getText();
                    }
                    
                    if (isJournalMeta) {
                        if (isJournalId) {
                            articleMeta.setJournalId(staxXmlReader.getText());
                        }

                        if (isJournalIssn) {
                            articleMeta.setJournalIssn(staxXmlReader.getText());
                        }

                        if (isJournalTitle) {
                            articleMeta.setJournalTitle(staxXmlReader.getText());
                        }
                    }

                    if (isPubDate) {
                        if (isDay) {
                            articleMeta.setDay(staxXmlReader.getText());
                        }

                        if (isMonth) {
                            articleMeta.setMonth(staxXmlReader.getText());

                        }

                        if (isYear) {
                            articleMeta.setYear(staxXmlReader.getText());

                        }
                    }

                    break;
                case XMLStreamConstants.END_ELEMENT:
                    String localEndName = staxXmlReader.getLocalName();
                    nodeStack.pop(); // pop the end element out of the node stack
                    if (localEndName.equals(AMETA))
                        isAmeta = false;
                    
                    if (localEndName.equals(ABSTRACT)){
                        isAbstract = false;
                        articleMeta.setAbstractText(abstractText.toString().replaceAll(" +", " "));
                    }

                    if (localEndName.equals(PERMISSIONS))
                        isPermission = false;

                    if (localEndName.equals(LICENSE))
                        isLicense = false;

                    if (localEndName.equals(FIGURE)) {
                        recordList.add(record);
                        isFigure = false;
                    }
                    
                  //TODO
		        	if (localEndName.equals(SECTION))
                        isSection = false;
		        	
		        	if (localEndName.equals(SUBJECT_GROUP))
                        isSubjectGroup = false;
		        	
		        	if (localEndName.equals(SUBJECT))
                        isSubject = false;
		        	
		        	if (localEndName.equals(BODY)){
                        isBody = false;
                        articleMeta.setFullText(fullText.toString().replaceAll(" +", " "));   
		        	}
		        	
		        	if (localEndName.equals(CONTRIB_GIV)){
		        		isContribGiv = false;
                	}
		        	
		        	if (localEndName.equals(CONTRIB_SUR)){
		        		isContribSub = false;
		        	}
		        	
		        	if (localEndName.equals(CONTRIB)){
		        		isContrib = false;
		        		articleMeta.addAuthor(authorSur.toString().replace("\n", " ") + ", " + authorGiv.toString().replace("\n", " "));
		        		authorSur = new StringBuilder();
		        		authorGiv = new StringBuilder();
                	}
		        	
		        	if (localEndName.equals(NAMED_CONTENT)){
		        		isNamedContent = false;
		        	}
		        	//TODO
                    
                    

	        		if(localEndName.equals(ALTERNATIVE)) {
	        			if(alternativesIri != null && alternativesVideo != null)
	        				record.addIriVideoEntry(alternativesIri, alternativesVideo);
	        			
	        			alternativesIri = null;
	        			alternativesVideo = null;
                        isAlternative = false;
                    }

	        		if (localEndName.equals(CAPTION))
                        isCaption = false;

                    if (localEndName.equals(BOLD))
                        isBold = false;

                    if (localEndName.equals(ITALIC))
                        isItalic = false;

                    if (localEndName.equals(LABEL))
                        isLabel = false;

                    if (localEndName.equals(ATITLE))
                        isTitle = false;
                    
                    if (localEndName.equals(PARAGRPAPH)){
                        if(isAmeta && isAbstract){
                        	if(abstractText.length() == 0){
                        		abstractText.append(currentParagraph.toString());
                        	} else{
                        		abstractText.append(" " + currentParagraph.toString());
                        	}
                        }
                        //TODO                        	

                        if(isBody && !isFigure && !isTable && !isDispFormula){
                        	if(fullText.length() == 0){
                        		fullText.append(currentParagraph.toString());
                        		if (abstractText.length() == 0){
                        			abstractText.append(currentParagraph.toString());
                        			articleMeta.setAbstractText(abstractText.toString().replaceAll(" +", " "));
                        		}
                        	} else{
                        		fullText.append(" " + currentParagraph.toString());
                        	}
                        }
                        isParagraph = false;
                        currentParagraph = new StringBuilder();
                        //TODO
                        
                    }
                    //TODO
                    if (localEndName.equals(LOCAL_TITLE)){
                    	isLocalTitle = false;
                    	if(!isFigure && !isTable && !isDispFormula){
                        	if(fullText.length() == 0){
                        		fullText.append(currentTitle.toString());
                        	} else {
                        		fullText.append(" " + currentTitle.toString());
                        	}
                    	}
                    }
                                        
                    if (localEndName.equals(CATEGORIES)){
                    	isSubject = false;
                    	articleMeta.setSubjects(categoriesList);
                    }
                    
                    //TODO

                    if (localEndName.equals(DISP_FORMULA))
		        		isDispFormula = false;
                    
                    if (localEndName.equals(XREF)) {
                        isXRef = false;
                        isArticleXref = false;
                        isFigureXref = false;
                    }
                    
                    if (localEndName.equals(PUBLISHER_NAME)) {
                        isPublisherName = false;
                    }

                    if (localEndName.equals(JOURNAL_META)) {
                        isJournalMeta = false;
                    }

                    if (localEndName.equals(JOURNAL_ID)) {
                        isJournalId = false;
                    }

                    if (localEndName.equals(JOURNAL_TITLE)) {
                        isJournalTitle = false;
                    }

                    if (localEndName.equals(JOURNAL_ISSN)) {
                        isJournalIssn = false;
                    }

                    if (localEndName.equals(PUB_DATE)) {
                        isPubDate = false;
                    }

                    if (localEndName.equals(DAY)) {

                        isDay = false;
                    }

                    if (localEndName.equals(MONTH)) {
                        isMonth = false;

                    }

                    if (localEndName.equals(YEAR)) {
                        isYear = false;

                    }
                    
                    if (localEndName.equals(KEYWORD)) {
                        isKeyword = false;
                        String keyword = WordUtils.capitalizeFully(currentKeyword);
                        if (!currentKeyword.isEmpty() && !keywordList.contains(keyword))
                        	keywordList.add(keyword);
                        currentKeyword = "";
                    }
                    
                    if (localEndName.equals(KEYWORD_GROUP)) {
                        isKeywordGroup = false;
                        articleMeta.setKeywords(keywordList);
                    }
                    
                    if (localEndName.equals(TABLE_WRAP))
                        isTable = false;

                    break;
                default:
                    break;
            }
        }
        staxXmlReader.close();
        fis.close();

    }

	public void getPermissionsFields(String infile,
			Map<String, List<String>> permissions,
			Map<String, List<String>> holders, String name) throws Exception {

		FileInputStream fis = new FileInputStream(infile);

		XMLInputFactory factory = XMLInputFactory.newInstance();

		factory.setProperty(XMLInputFactory.IS_VALIDATING, Boolean.FALSE);
		factory.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, Boolean.FALSE);
		factory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES,
				Boolean.FALSE);
		factory.setProperty(XMLInputFactory.SUPPORT_DTD, Boolean.FALSE);
		XMLStreamReader staxXmlReader = factory.createXMLStreamReader(fis);

		String text = "";
		for (int event = staxXmlReader.next(); event != XMLStreamConstants.END_DOCUMENT; event = staxXmlReader
				.next()) {
			switch (event) {
			case XMLStreamConstants.START_ELEMENT:
				String localName = staxXmlReader.getLocalName();
				if (localName.equals(COPYRIGHTHOLDER)) {
					String h = staxXmlReader.getElementText();
					int i = h.toLowerCase().indexOf("licensee");
					if (i > -1)
						h = h.substring(i);
					if (h != null && h.length() > 2) {
						List<String> hold = holders.get(h);
						if (hold == null)
							hold = new ArrayList<String>();
						hold.add(name);
						holders.put(h, hold);
					}
				}
				if (localName.equals(AMETA))
					isAmeta = true;
				// if (isPermission) {
				if (localName.equals(LICENSE))
					isCopyright = true;
				if (localName.equals(COPYRIGHT))
					isCopyright = true;
				// }

				if (isAmeta && localName.equals(PERMISSIONS))
					isPermission = true;
				break;
			case XMLStreamConstants.CHARACTERS:
				if (isCopyright)
					text += staxXmlReader.getText();

				break;
			case XMLStreamConstants.END_ELEMENT:
				String localEndName = staxXmlReader.getLocalName();
				if (localEndName.equals(PERMISSIONS)) {
					isPermission = false;

				}
				if (localEndName.equals(AMETA))
					isAmeta = false;
				if (isCopyright && localEndName.equals(LICENSE)
						|| localEndName.equals(COPYRIGHT)) {
					if (!text.equals("")) {
						isCopyright = false;
						int i = text.toLowerCase().indexOf("this is an open");
						if (i > -1) {
							text = text.substring(i);
						}
						i = text.toLowerCase().indexOf("licensee");
						if (i > -1)
							text = text.substring(i);
						String key = text.replaceAll("\\W+", "").toLowerCase();
						if (key != null && key.length() > 1) {
							List<String> aList = permissions.get(key);
							if (aList == null) {
								aList = new ArrayList<String>();
								aList.add(text.trim());
							}
							aList.add(name);
							permissions.put(key, aList);
							text = "";
						}
					}
				}
				break;
			default:
				break;
			}
		}
		staxXmlReader.close();
		fis.close();

	}

}
