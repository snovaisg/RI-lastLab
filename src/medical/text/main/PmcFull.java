package medical.text.main;

import java.util.LinkedList;
import java.util.List;

public class PmcFull extends PmcMeta {

	String fullText;
	List<String> subjects;
	List<String> keywords;
	List<String> authors;
	
	public List<String> getAuthors() {
		return authors;
	}

	public void setAuthors(List<String> authors) {
		this.authors = authors;
	}
	
	public void addAuthor(String author) {
		if (!authors.contains(author))
			this.authors.add(author);
	}

	public List<String> getKeywords() {
		return keywords;
	}

	public void setKeywords(List<String> keywords) {
		this.keywords = keywords;
	}

	public PmcFull() {
		subjects = new LinkedList<String>();
		keywords = new LinkedList<String>();
		authors = new LinkedList<String>();
		fullText = "";
	}

	public List<String> getSubjects() {
		return subjects;
	}

	public void setSubjects(List<String> subjects) {
		this.subjects = subjects;
	}
	
	public String getFullText() {
		return fullText;
	}

	public void setFullText(String fullText) {
		this.fullText = fullText;
	}
	

}
