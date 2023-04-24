package gitlet;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/** A Pointer class to store all the branches.
 *  @author Jason Tjahjono
 *  */
public class Pointers implements Serializable {

    /** A Hashmap of pointer (String) and where
     *  they are pointing to (String). */
    private HashMap<String, String> _pointers = new HashMap<>();

    /** A String which indicates the current branch. */
    private String _currentPointer;

    /** Constructor: initializes master branch and HEAD
     *  and sets the current pointer to master.
     *  @param id : String of the commit ID. */
    Pointers(String id) {
        _pointers.put("master", id);
        _pointers.put("HEAD", id);
        _currentPointer = "master";
    }

    /** Returns a current pointer. */
    public String getCurrentPointer() {
        return _currentPointer;
    }

    /** Sets the current pointer.
     *  @param pointer : String of the pointer. */
    public void setCurrentPointer(String pointer) {
        _currentPointer = pointer;
    }

    /** Moves a pointer to the specified pointsTo.
     *  @param pointer : String of the pointer.
     *  @param pointsTo : String of the associated pointsTo. */
    public void move(String pointer, String pointsTo) {
        if (_pointers.containsKey(pointer)) {
            _pointers.put(pointer, pointsTo);
        } else {
            System.out.println("Pointer does not exist.");
        }
    }

    /** Adds a specified pointer from the hashmap.
     *  @param pointer : String of the pointer.
     *  @param pointsTo : String of the associated pointsTo. */
    public void add(String pointer, String pointsTo) {
        if (_pointers.containsKey(pointer)) {
            System.out.println("A branch with that name already exists.");
        } else {
            _pointers.put(pointer, pointsTo);
        }
    }

    /** Removes a specified pointer from the hashmap.
     *  @param pointer : String of the pointer. */
    public void remove(String pointer) {
        if (!_pointers.containsKey(pointer)) {
            System.out.println("A branch with that name does not exist.");
        } else if (_currentPointer.equals(pointer)) {
            System.out.println("Cannot remove the current branch.");
        } else {
            _pointers.remove(pointer);
        }
    }

    /** Returns a hashmap of pointers and its pointing to. */
    public HashMap<String, String> getPointers() {
        return _pointers;
    }

    /** Returns a String of the commit ID of the specified pointer.
     *  @param pointer : String of the pointer */
    public String getPointTo(String pointer) {
        if (_pointers.containsKey(pointer)) {
            return _pointers.get(pointer);
        } else {
            throw Utils.error("No such branch exists.");
        }

    }

    /** Returns an ArrayList of pointers except for HEAD. */
    public ArrayList<String> getBranches() {
        ArrayList<String> branches = new ArrayList<>();
        for (String key : _pointers.keySet()) {
            if (!key.equals("HEAD")) {
                branches.add(key);
            }
        }
        Collections.sort(branches);

        return branches;
    }

    /** Prints out all pointers with its associated pointsTo. */
    public void getAllInfo() {
        for (Map.Entry<String, String> set : _pointers.entrySet()) {
            System.out.println(set.getKey() + " = " + set.getValue());
        }
    }
}
