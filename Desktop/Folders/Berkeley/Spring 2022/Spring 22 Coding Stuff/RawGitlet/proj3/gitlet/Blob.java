package gitlet;

import java.io.Serializable;

/** A Blob contains an ID, Filename, and the Contents
 *  of the file.
 *  @author Jason Tjahjono
 */
public class Blob implements Serializable {

    /** Filename (String). */
    private final String _fileName;

    /** Contents (String). */
    private final String _contents;

    /** Unique ID (String). */
    private final String _id;

    /** Constructor: initializes the filename and contents
     *  and serializes the object into a unique id.
     *  @param fileName : String of file name.
     *  @param contents : String of contents. */
    Blob(String fileName, String contents) {
        _fileName = fileName;
        _contents = contents;
        _id = Utils.sha1(Utils.serialize(this));
    }

    /** Returns a boolean value after comparing the
     *  filename and contents of 2 blobs.
     *  @param b : A second blob to compare this blob with. */
    public boolean isEqual(Blob b) {
        return this._fileName.equals(b.getFileName())
            && this._contents.equals(b.getContents());
    }

    /** Returns a string of the blob's id. */
    public String getId() {
        return _id;
    }

    /** Returns a string of the blob's filename. */
    public String getFileName() {
        return _fileName;
    }

    /** Returns a string of the blob's contents. */
    public String getContents() {
        return _contents;
    }

}
