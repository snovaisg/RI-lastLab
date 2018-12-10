package medical.text.nxml;


/**
 * Created by IntelliJ IDEA.
 * User: apostolovaea
 * Date: 3/15/11
 * Time: 3:47 PM
 * To change this template use File | Settings | File Templates.
 */
public class TextRange {

    public int startIndex;
    public int endIndex;
    public String text;

    @Override
    public String toString() {
        return "TextRange{" +
                "startIndex=" + startIndex +
                ", endIndex=" + endIndex +
                ", text='" + text + '\'' +
                '}';
    }
}
