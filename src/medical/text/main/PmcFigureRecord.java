package medical.text.main;
import medical.text.nxml.TextRange;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * PmcFigureRecord object contains figure id, iri, label and caption.
 *
 * @author NLM iMedline project, contact ddemner@mail.nih.gov
 * @author http://archive.nlm.nih.gov/ridem/iti.html
 * @version 1.0 2011-01-07
 *
 */

public class PmcFigureRecord {

	public PmcFigureRecord() {

	}

    @Override
    public String toString() {
        return "PmcFigureRecord{" +
                "iri='" + iri + '\'' +
                ", label='" + label + '\'' +
                ", figureID='" + figureID + '\'' +
                ", caption='" + caption + '\'' +
                ", boldText=" + boldText +
                ", italicText=" + italicText +
                '}';
    }

    public String getFigureID() {
		return figureID;
	}

	public void setFigureID(String figureID) {
		this.figureID = figureID;
	}

	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}

	public String getIri() {
		return iri;
	}
	
	public List<String> getIriList() {
		return iriList;
	}

	public void setIri(String iri) {
		this.iri = iri;
	}
	
	public void addIri(String iri) {
		this.iriList.add(iri);
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

    public List<TextRange> getBoldText() {
        return boldText;
    }

    public List<TextRange> getItalicText() {
        return italicText;
    }

    public void addBoldText(TextRange boldText) {
        this.boldText.add(boldText);
    }

    public void addItalicText(TextRange italicText) {
        this.italicText.add(italicText);
    }

	public void addIriVideoEntry(String iri, String video) {
		this.iriVideoMap.put(iri, video);
	}
	
	public Map<String, String> getIriVideoMap(){
		return this.iriVideoMap;
	}
    
    
    String iri = "";
	String label = "";
	String figureID = "";
	String caption = "";
	
	List<String> iriList = new ArrayList<String>();
	Map<String, String> iriVideoMap = new HashMap<String, String>();
	
    List<TextRange> boldText = new LinkedList<TextRange>();
    List<TextRange> italicText = new LinkedList<TextRange>();



}
