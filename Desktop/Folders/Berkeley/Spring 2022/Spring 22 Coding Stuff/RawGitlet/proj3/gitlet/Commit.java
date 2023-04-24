package gitlet;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Formatter;

/** Represents one Commit made by the user.
 *  @author Jason Tjahjono
 */
public class Commit implements Serializable {

    /** Parent ID (String). */
    private final String _parent;

    /** Commit Message (String). */
    private final String _message;

    /** Date (Date). */
    private final Date _date;

    /** Unique ID (String). */
    private final String _id;

    /** Files ID (ArrayList of Strings). */
    private final ArrayList<String> _files;

    /** Constructor: Creates a Commit object.
     *  @param message : String of commit message.
     *  @param parent : String of parent id.
     *  @param fileIds : ArrayList of Strings of the file ids.
     *  @param init : boolean whether it is the init or normal commit. */
    Commit(String message, String parent,
           ArrayList<String> fileIds, boolean init) {
        if (init) {
            _parent = null;
            _message = "initial commit";
            _date = new Date(0);
            _files = new ArrayList<>();
            _id = Utils.sha1(Utils.serialize(this));
        } else {
            _parent = parent;
            _message = message;
            _date = new Date();
            _files = fileIds;
            _id = Utils.sha1(Utils.serialize(this));
        }
    }

    /** Returns a string of the ID. */
    public String getId() {
        return _id;
    }

    /** Returns an ArrayList of the file IDs (String). */
    public ArrayList<String> getFileIds() {
        return _files;
    }

    /** Returns a String of the parent ID. */
    public String getParent() {
        return _parent;
    }

    /** Returns a String of formatted date of the commit. */
    public String getDate() {
        Formatter f = new Formatter();
        f.format("%ta %tb %te %tT %tY", _date, _date, _date, _date, _date);
        return f.toString();
    }

    /** Returns a String of the message. */
    public String getMessage() {
        return _message;
    }
}
