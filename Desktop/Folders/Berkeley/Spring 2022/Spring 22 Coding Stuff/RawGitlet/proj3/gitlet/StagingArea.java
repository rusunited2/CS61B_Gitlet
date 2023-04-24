package gitlet;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/** A StagingArea class, consists of Addition and Removal Stage.
 *  @author Jason Tjahjono
 */
public class StagingArea implements Serializable {

    /** Addition Stage:
     *  key: String fileName.
     *  value: Blob file. */
    private HashMap<String, Blob> _addition = new HashMap<>();

    /** Removal Stage:
     *  key: String fileName.
     *  value: Blob file. */
    private HashMap<String, Blob> _removal = new HashMap<>();

    /** Constructor, does nothing. */
    StagingArea() {

    }

    /** returns an ArrayList of file names from a stage.
     *  @param stage : String of "additon" or "removal". */
    public ArrayList<String> getFileNames(String stage) {
        if (stage.equals("addition")) {
            return new ArrayList<>(_addition.keySet());
        } else if (stage.equals("removal")) {
            return new ArrayList<>(_removal.keySet());
        }
        return null;
    }

    /** returns an ArrayList of file names from a stage, sorted.
     *  @param stage : String of "additon" or "removal". */
    public ArrayList<String> getFileNamesSorted(String stage) {
        ArrayList<String> sorted = getFileNames(stage);
        Collections.sort(sorted);
        return sorted;
    }

    /** gets the file ID from either addition or removal stage.
     *  @param stage : String of "additon" or "removal".
     *  @return : ArrayList of Strings of the file IDs in stage. */
    public ArrayList<String> getFileIds(String stage) {
        if (stage.equals("addition")) {
            ArrayList<String> results = new ArrayList<>();
            for (Blob b : _addition.values()) {
                results.add(b.getId());
            }
            return results;
        } else if (stage.equals("removal")) {
            ArrayList<String> results = new ArrayList<>();
            for (Blob b : _removal.values()) {
                results.add(b.getId());
            }
            return results;
        }
        return null;
    }

    /** stages a blob from either addition or removal stage.
     *  @param stage : String of "additon" or "removal".
     *  @param fileName : String of the file name.
     *  @param blob : Blob that is associated with the file. */
    public void stage(String stage, String fileName, Blob blob) {
        if (stage.equals("addition")) {
            _addition.put(fileName, blob);
        } else if (stage.equals("removal")) {
            _removal.put(fileName, blob);
        }
    }

    /** unstages a blob from either addition or removal stage.
     *  @param stage : String of "additon" or "removal".
     *  @param fileName : String of the file name. */
    public void unstage(String stage, String fileName) {
        if (stage.equals("addition")) {
            _addition.remove(fileName);
        } else if (stage.equals("removal")) {
            _removal.remove(fileName);
        }
    }

    /** clears the addition and removal stage. */
    public void clear() {
        _addition.clear();
        _removal.clear();
    }

    /** Returns whether the stages are empty.
     *  @return ture if stages are empty, false otherwise */
    public boolean isEmpty() {
        return _addition.isEmpty() && _removal.isEmpty();
    }
}
