package medical.text.model;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: apostolovaea
 * Date: 3/23/11
 * Time: 2:19 PM
 * To change this template use File | Settings | File Templates.
 */
public class FigureMention implements Serializable{

    /**
	 * 
	 */
	private static final long serialVersionUID = 4583516639990239963L;
	private String paragraph;

    public String getParagraph() {
        return paragraph;
    }

    public void setParagraph(String paragraph) {
        this.paragraph = paragraph;
    }

    @Override
    public String toString() {
        return "FigureMention{" +
                "paragraph='" + paragraph + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FigureMention that = (FigureMention) o;

        if (paragraph != null ? !paragraph.equals(that.paragraph) : that.paragraph != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return paragraph != null ? paragraph.hashCode() : 0;
    }
}
