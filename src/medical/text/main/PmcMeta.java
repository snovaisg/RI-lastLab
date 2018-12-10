package medical.text.main;

import java.util.ArrayList;
import java.util.List;

/**
 * PmcMeta object contains PubMedCentral article id, pmid, url and title.
 *
 * @author NLM iMedline project, contact ddemner@mail.nih.gov
 * @author http://archive.nlm.nih.gov/ridem/iti.html
 * @version 1.0 2011-01-07
 *
 */
public class PmcMeta {
	static final String PMCURI = "http://www.ncbi.nlm.nih.gov/pmc/articles/";

	public PmcMeta() {
	}

	String title = "";
	String pmid = "";
	String articleURL = "";
	String articleDOI = "";
	String pmcid = "";
	String localFileName = "";
	String publisherName = "";
    String journalId = "";
    String journalTitle = "";
    String journalIssn = "";
    String month = "";
    String day = "";
    String year = "";
    String abstractText = "";
    
	List<String> licenseTypes = new ArrayList<String>();
    List<String> licenseURLs = new ArrayList<String>();
    

    @Override
    public String toString() {
        return "PmcMeta{" +
                "title='" + title + '\'' +
                ", pmid='" + pmid + '\'' +
                ", articleURL='" + articleURL + '\'' +
                ", articleDOI='" + articleDOI + '\'' +
                ", pmcid='" + pmcid + '\'' +
                ", localFileName='" + localFileName + '\'' +
                ", publisherName='" + publisherName + '\'' +
                '}';
    }

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
	
    public List<String> getLicenseURLs() {
		return licenseURLs;
	}

	public void addLicenseURL(String licenseURL) {
		this.licenseURLs.add(licenseURL);
	}
	
	
	public void setLicenseType(List<String> licenseTypes) {
		this.licenseTypes = licenseTypes;
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

	public static String getPmcuri() {
		return PMCURI;
	}

	public String getLocalFileName() {
		return localFileName;
	}

	public void setLocalFileName(String localFileName) {
		this.localFileName = localFileName;
	}

    public String getPublisherName() {
        return publisherName;
    }

    public void setPublisherName(String publisherName) {
        this.publisherName = publisherName;
    }

    public String getJournalId() {
        return journalId;
    }

    public void setJournalId(String journalId) {
        this.journalId = journalId;
    }

    public String getJournalTitle() {
        return journalTitle;
    }

    public void setJournalTitle(String journalTitle) {
        this.journalTitle = journalTitle;
    }

    public String getJournalIssn() {
        return journalIssn;
    }

    public void setJournalIssn(String journalIssn) {
        this.journalIssn = journalIssn;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getAbstractText() {
		return abstractText;
	}

	public void setAbstractText(String abstractText) {
		this.abstractText = abstractText;
	}

}
