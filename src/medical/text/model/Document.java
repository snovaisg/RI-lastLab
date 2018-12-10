package medical.text.model;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: apostolovaea
 * Date: 3/17/11
 * Time: 10:39 AM
 * To change this template use File | Settings | File Templates.
 */
public class Document implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = -4467732720030293582L;
	private String title;
    private String pmid;
    private String articleURL;
    public List<String> getSubjects() {
		return subjects;
	}

	public void setSubjects(List<String> subjects) {
		this.subjects = subjects;
	}

	private String articleDOI;
    private String pmcid;
    private String localFileName;
    private String publisherName;
    private String abstractText;
    private Journal journal;
    private PublicationDate publicationDate;
    private List<String> licenseTypes;
    private List<String> licenseURLs;
    private List<String> categories;
    private List<String> subjects;
    private List<String> keywords;
    private List<String> authors;
    public List<String> getAuthors() {
		return authors;
	}

	private String fullText;

    public String getFullText() {
		return fullText;
	}

	public void setFullText(String fullText) {
		this.fullText = fullText;
	}

	public List<String> getCategories() {
		return categories;
	}

	public void setCategories(List<String> categories) {
		this.categories = categories;
	}

	private List<Figure> figures;


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getLicenseTypes() {
        return licenseTypes;
    }

    public void addLicenseType(String licenseType) {
        this.licenseTypes.add(licenseType);
    }

    public void setLicenseTypes(List<String> licenseTypes) {
        this.licenseTypes = licenseTypes;
    }

    public List<String> getLicenseURLs() {
        return licenseURLs;
    }

    public void addLicenseURL(String licenseURL) {
        this.licenseURLs.add(licenseURL);
    }

    public void setLicenseURLs(List<String> licenseURLs) {
        this.licenseURLs = licenseURLs;
    }

    public String getPmid() {
        return pmid;
    }

    public void setPmid(String pmid) {
        this.pmid = pmid;
    }

    public String getArticleURL() {
        return articleURL;
    }

    public void setArticleURL(String articleURL) {
        this.articleURL = articleURL;
    }

    public String getArticleDOI() {
        return articleDOI;
    }

    public void setArticleDOI(String articleDOI) {
        this.articleDOI = articleDOI;
    }

    public String getPmcid() {
        return pmcid;
    }

    public void setPmcid(String pmcid) {
        this.pmcid = pmcid;
    }

    public String getLocalFileName() {
        return localFileName;
    }

    public void setLocalFileName(String localFileName) {
        this.localFileName = localFileName;
    }

    public List<Figure> getFigures() {
        return figures;
    }

    public void setFigures(List<Figure> figures) {
        this.figures = figures;
    }


    public String getPublisherName() {
        return publisherName;
    }

    public void setPublisherName(String publisherName) {
        this.publisherName = publisherName;
    }

    public String getAbstractText() {
		return abstractText;
	}

	public void setAbstractText(String abstractText) {
		this.abstractText = abstractText;
	}

	public Journal getJournal() {
        return journal;
    }

    public void setJournal(Journal journal) {
        this.journal = journal;
    }

    public PublicationDate getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(PublicationDate publicationDate) {
        this.publicationDate = publicationDate;
    }

    @Override
    public String toString() {
        return "Document{" +
                "title='" + title + '\'' +
                ", pmid='" + pmid + '\'' +
                ", articleURL='" + articleURL + '\'' +
                ", articleDOI='" + articleDOI + '\'' +
                ", pmcid='" + pmcid + '\'' +
                ", localFileName='" + localFileName + '\'' +
                ", publisherName='" + publisherName + '\'' +
                ", journal=" + journal +
                ", publicationDate=" + publicationDate +
                ", figures=" + figures +
                '}';
    }

	public List<String> getKeywords() {
		return keywords;
	}

	public void setKeywords(List<String> keywords) {
		this.keywords = keywords;
	}

	public void setAuthors(List<String> authors) {
		this.authors = authors;
		
	}

	

	
}
