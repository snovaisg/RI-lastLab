package medical.text.model;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: apostolovaea
 * Date: 7/28/11
 * Time: 3:56 PM
 * To change this template use File | Settings | File Templates.
 */
public class PublicationDate implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 2223319515056133276L;
	private String type;
    private String date;
    private String month;
    private String year;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    @Override
    public String toString() {
    	try {
    		return String.format("%04d-%02d-%02d", Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(date));
    	} catch (Exception e) {
    		return "";
    	}
    }
}
