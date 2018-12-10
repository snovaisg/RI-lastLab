package medical.text.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: apostolovaea
 * Date: 7/26/11
 * Time: 3:36 PM
 * To change this template use File | Settings | File Templates.
 */
public class Journal implements Serializable{
    /**
	 * 
	 */
	private static final long serialVersionUID = 4567864497133993877L;
	private String title;
    private String id;
    private String issn;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIssn() {
        return issn;
    }

    public void setIssn(String issn) {
        this.issn = issn;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Journal{" +
                "title='" + title + '\'' +
                ", id='" + id + '\'' +
                ", issn='" + issn + '\'' +
                '}';
    }
}
