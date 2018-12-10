package medical.text.main;

/**
 * Created by IntelliJ IDEA.
 * User: apostolovaea
 * Date: 3/23/11
 * Time: 1:56 PM
 * To change this template use File | Settings | File Templates.
 */
public class PmcFigureMention {

    private String figureId;

    private StringBuilder paragraph;

    public String getFigureId() {
        return figureId;
    }

    public void setFigureId(String figureId) {
        this.figureId = figureId;
    }

    public StringBuilder getParagraph() {
        return paragraph;
    }

    public void setParagraph(StringBuilder paragraph) {
        this.paragraph = paragraph;
    }

    @Override
    public String toString() {
        return "PmcFigureMention{" +
                "figureId='" + figureId + '\'' +
                ", paragraph=" + paragraph +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PmcFigureMention that = (PmcFigureMention) o;

        if (figureId != null ? !figureId.equals(that.figureId) : that.figureId != null) return false;
        if (paragraph != null ? !paragraph.equals(that.paragraph) : that.paragraph != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = figureId != null ? figureId.hashCode() : 0;
        result = 31 * result + (paragraph != null ? paragraph.hashCode() : 0);
        return result;
    }
}
