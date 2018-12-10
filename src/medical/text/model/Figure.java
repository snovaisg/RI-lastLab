package medical.text.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: apostolovaea
 * Date: 3/17/11
 * Time: 10:39 AM
 * To change this template use File | Settings | File Templates.
 */
public class Figure implements Serializable{

    /**
	 * 
	 */
	private static final long serialVersionUID = -4716511142347482017L;
	private String iri = "";
    private List<String> iriList = null;
    private Map<String, String> iriVideoMap = null;
	private String label = "";
	private String figureID = "";

    private String caption;

    private List<FigureMention> figureMentions;


    public void setIriList(List<String> iris) {
    	this.iriList = iris;
    }
    
    public List<String> getIriList() {
    	return this.iriList;
    }
    
    public void setIriVideoMap(Map<String, String> iriVideoMap) {
    	this.iriVideoMap = iriVideoMap;
    }
    
    public Map<String, String> getIriVideoMap() {
    	return this.iriVideoMap;
    }
    
    
    public String getIri() {
        return iri;
    }

    public void setIri(String iri) {
        this.iri = iri;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
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

    public List<FigureMention> getFigureMentions() {
        return figureMentions;
    }

    public void setFigureMentions(List<FigureMention> figureMentions) {
        this.figureMentions = figureMentions;
    }

    @Override
    public String toString() {
        return "Figure{" +
                "iri='" + iri + '\'' +
                ", label='" + label + '\'' +
                ", figureID='" + figureID + '\'' +
                ", caption=" + caption +
                ", figureMentions=" + figureMentions +
                '}';
    }
}
