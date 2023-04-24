package gitlet;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/** The main object which houses the logic of all the commands.
 *  @author Jason Tjahjono
 *  */
public class Dashboard {

    /** Creates a .gitlet folder and creates a first commit. */
    public static void init() {
        File newFile1 = new File(".gitlet");
        newFile1.mkdir();
        File newFile2 = new File(".gitlet/commits");
        newFile2.mkdir();
        File newFile3 = new File(".gitlet/blobs");
        newFile3.mkdir();
        Commit c = new Commit(null, null, null, true);
        Utils.writeObject(new File(".gitlet/commits/" + c.getId()), c);

        Pointers p = new Pointers(c.getId());
        Utils.writeObject(new File(".gitlet/pointers"), p);

        StagingArea s = new StagingArea();
        Utils.writeObject(new File(".gitlet/stagingarea"), s);
    }

    /** Adds a file to the stage for Addition.
     *  @param filePath : String of the path of the file. */
    public static void add(String filePath) {
        StagingArea s = Utils.readObject(
            new File(".gitlet/stagingarea"), StagingArea.class
        );
        String[] pathSegments = filePath.split("/");
        String fileName = pathSegments[pathSegments.length - 1];
        if (!Utils.plainFilenamesIn(
            System.getProperty("user.dir")).contains(fileName)
        ) {
            System.out.println("File does not exist.");
        } else {
            String contents = Utils.readContentsAsString(new File(filePath));
            Blob fileBlob = new Blob(fileName, contents);

            Pointers p = Utils.readObject(
                new File(".gitlet/pointers"), Pointers.class
            );
            Commit lastCommit = Utils.readObject(new File(
                ".gitlet/commits/" + p.getPointers().get("HEAD")
            ), Commit.class);

            ArrayList<String> blobIds = lastCommit.getFileIds();
            Blob compareBlob;
            boolean addToStaging = true;
            for (String id : blobIds) {
                compareBlob = Utils.readObject(
                    new File(".gitlet/blobs/" + id), Blob.class
                );
                if (compareBlob.isEqual(fileBlob)) {
                    addToStaging = false;
                }
            }

            if (addToStaging) {
                s.stage("addition", fileName, fileBlob);
            } else if (s.getFileNames("addition").contains(fileName)) {
                s.unstage("addition", fileName);
            } else if (s.getFileNames("removal").contains(fileName)) {
                s.unstage("removal", fileName);
            }

            Utils.writeObject(new File(".gitlet/stagingarea"), s);
            Utils.writeObject(
                new File(".gitlet/blobs/" + fileBlob.getId()), fileBlob
            );
        }
    }

    /** Creates a Commit object to be saved in .gitlet folder.
     *  @param message : String of commit message. */
    public static void commit(String message) {
        StagingArea s = Utils.readObject(
            new File(".gitlet/stagingarea"), StagingArea.class
        );
        Pointers p = Utils.readObject(
            new File(".gitlet/pointers"), Pointers.class
        );
        Commit lastCommit = Utils.readObject(new File(
            ".gitlet/commits/" + p.getPointers().get("HEAD")
        ), Commit.class);

        ArrayList<String> blobIds = lastCommit.getFileIds();
        ArrayList<String> fileNamesInAddition = s.getFileNames("addition");
        ArrayList<String> fileNamesInRemoval = s.getFileNames("removal");

        if (fileNamesInAddition.size() == 0 && fileNamesInRemoval.size() == 0) {
            System.out.println("No changes added to the commit.");
        } else if (message.equals("")) {
            System.out.println("Please enter a commit message.");
        } else {
            Blob commitBlob;
            ArrayList<String> removeId = new ArrayList<>();
            for (String id : blobIds) {
                commitBlob = Utils.readObject(
                    new File(".gitlet/blobs/" + id), Blob.class
                );
                if (fileNamesInAddition.contains(commitBlob.getFileName())
                    || fileNamesInRemoval.contains(commitBlob.getFileName())) {
                    removeId.add(id);
                }
            }

            for (String id : removeId) {
                blobIds.remove(id);
            }

            blobIds.addAll(s.getFileIds("addition"));
            Commit c = new Commit(message, lastCommit.getId(), blobIds, false);
            p.move("HEAD", c.getId());
            p.move(p.getCurrentPointer(), c.getId());
            s.clear();

            Utils.writeObject(new File(".gitlet/stagingarea"), s);
            Utils.writeObject(new File(".gitlet/pointers"), p);
            Utils.writeObject(new File(".gitlet/commits/" + c.getId()), c);
        }
    }

    /** Removes a file from the working directory and stage.
     *  @param fileName : String of file name to remove. */
    public static void rm(String fileName) {
        StagingArea s = Utils.readObject(
            new File(".gitlet/stagingarea"), StagingArea.class
        );
        if (s.getFileNames("addition").contains(fileName)) {
            s.unstage("addition", fileName);
        } else {
            Pointers p = Utils.readObject(
                new File(".gitlet/pointers"), Pointers.class
            );
            Commit lastCommit = Utils.readObject(new File(
                    ".gitlet/commits/" + p.getPointers().get("HEAD")
            ), Commit.class);
            Blob b;
            boolean deleted = false;
            for (String id : lastCommit.getFileIds()) {
                b = Utils.readObject(
                    new File(".gitlet/blobs/" + id), Blob.class
                );
                if (b.getFileName().equals(fileName)) {
                    s.stage("removal", fileName, b);
                    Utils.restrictedDelete(fileName);
                    deleted = true;
                }
            }
            if (!deleted) {
                System.out.println("No reason to remove the file");
            }
        }
        Utils.writeObject(new File(".gitlet/stagingarea"), s);
    }

    /** Displays all the commits from the current HEAD to init. */
    public static void log() {
        Pointers p = Utils.readObject(
            new File(".gitlet/pointers"), Pointers.class
        );
        String lastCommitId = p.getPointTo("HEAD");
        Commit lastCommit = Utils.readObject(
            new File(".gitlet/commits/" + lastCommitId), Commit.class
        );

        while (lastCommit.getParent() != null) {
            displayLog(lastCommit);
            lastCommit = Utils.readObject(new File(
                    ".gitlet/commits/" + lastCommit.getParent()
            ), Commit.class);
        }
        displayLog(lastCommit);
    }

    /** Helper function to display a log.
     *  @param c : the Commit object for the log. */
    public static void displayLog(Commit c) {
        String line1 = "commit " + c.getId();
        String d = c.getDate();
        String line2 = "Date: " + d + " -0800";
        String line3 = c.getMessage();

        System.out.println("===");
        System.out.println(line1);
        System.out.println(line2);
        System.out.println(line3);
        System.out.println();
    }

    /** Displays all the commits ever committed to the repo. */
    public static void globalLog() {
        Commit c;
        for (String id : Utils.plainFilenamesIn(".gitlet/commits")) {
            c = Utils.readObject(
                new File(".gitlet/commits/" + id), Commit.class
            );
            displayLog(c);
        }
    }

    /** Finds the commits with the specified commit message.
     *  @param commitmsg : String of commit message. */
    public static void find(String commitmsg) {
        Commit c;
        boolean found = false;
        for (String id : Utils.plainFilenamesIn(".gitlet/commits")) {
            c = Utils.readObject(
                new File(".gitlet/commits/" + id), Commit.class
            );
            if (c.getMessage().equals(commitmsg)) {
                found = true;
                System.out.println(id);
            }
        }
        if (!found) {
            System.out.println("Found no commit with that message.");
        }
    }

    /** Displays the status of the gitlet repo. */
    public static void status() {
        Pointers p = Utils.readObject(
            new File(".gitlet/pointers"), Pointers.class
        );
        ArrayList<String> branches = p.getBranches();

        System.out.println("=== Branches ===");
        for (String b : branches) {
            if (p.getCurrentPointer().equals(b)) {
                System.out.println("*" + b);
            } else {
                System.out.println(b);
            }
        }
        System.out.println();

        StagingArea s = Utils.readObject(
            new File(".gitlet/stagingarea"), StagingArea.class
        );
        ArrayList<String> stagedFilesAddition =
            s.getFileNamesSorted("addition");
        ArrayList<String> stagedFilesRemoval =
            s.getFileNamesSorted("removal");

        System.out.println("=== Staged Files ===");
        for (String f : stagedFilesAddition) {
            System.out.println(f);
        }
        System.out.println();

        System.out.println("=== Removed Files ===");
        for (String f : stagedFilesRemoval) {
            System.out.println(f);
        }
        System.out.println();

        HashMap<String, ArrayList<String>> untracked = checkUntrackedStatus();
        ArrayList<String> newFile = untracked.get("newFile");
        ArrayList<String> modified = untracked.get("modified");

        Collections.sort(newFile);
        Collections.sort(modified);


        System.out.println("=== Modifications Not Staged For Commit ===");
        for (String f : modified) {
            System.out.println(f);
        }
        System.out.println();
        System.out.println("=== Untracked Files ===");
        for (String f : newFile) {
            System.out.println(f);
        }
        System.out.println();
    }

    /** Returns the untracked files which are new, modified, or deleted.
     *  @return : a key-value pair of ArrayList of Strings.
     *  Example: "newFile" = ArrayList of untracked new files. */
    public static HashMap<String, ArrayList<String>> checkUntrackedStatus() {
        StagingArea s = Utils.readObject(
                new File(".gitlet/stagingarea"), StagingArea.class
        );
        Pointers p = Utils.readObject(
                new File(".gitlet/pointers"), Pointers.class
        );
        ArrayList<Blob> untracked = checkUntracked();
        ArrayList<String> modified = new ArrayList<>();
        ArrayList<String> newFile = new ArrayList<>();
        Commit lastCommit = Utils.readObject(
                new File(".gitlet/commits/"
                + p.getPointTo(p.getCurrentPointer())), Commit.class
        );
        Blob b2;
        for (Blob b1 : untracked) {
            boolean found = false;
            if (!s.getFileNames("removal").contains(b1.getFileName())) {
                if (!Utils.plainFilenamesIn(System.getProperty("user.dir"))
                    .contains(b1.getFileName())) {
                    modified.add(b1.getFileName() + " (deleted)");
                } else {
                    for (String id : lastCommit.getFileIds()) {
                        b2 = Utils.readObject(
                                new File(".gitlet/blobs/" + id), Blob.class
                        );
                        if (b1.getFileName().equals(b2.getFileName())) {
                            modified.add(b1.getFileName() + " (modified)");
                            found = true;
                        }
                    }
                    if (!found && !s.getFileNames("addition")
                        .contains(b1.getFileName())) {
                        newFile.add(b1.getFileName());
                    }
                }
            }
        }
        HashMap<String, ArrayList<String>> result = new HashMap<>();
        result.put("modified", modified);
        result.put("newFile", newFile);
        return result;
    }

    /** Method Checkout: copying files or/and moving branches.
     *  1. Copy a file from the most recent commit.
     *  2. Copy a file from a specified commit id.
     *  3. Checkout to a specified branch.
     *  @param fileName : String of file name to copy.
     *  @param branchName : String of branch to checkout to.
     *  @param choice : Integer to choose from 1-3.
     *  @param shortId : String of the (possibly short) commit id to
     *  copy the file from. */
    public static void checkout(int choice,
        String shortId, String fileName, String branchName) {
        if (choice == 1) {
            Pointers p = Utils.readObject(
                new File(".gitlet/pointers"), Pointers.class
            );
            String lastCommitId = p.getPointTo("HEAD");
            Commit lastCommit = Utils.readObject(
                new File(".gitlet/commits/" + lastCommitId), Commit.class
            );
            findAndCopyFile(lastCommit, fileName);
        } else if (choice == 2) {
            String commitId = findLongCommitId(shortId);
            if (commitId.equals("")) {
                System.out.println("No commit with that id exists.");
            } else {
                Commit c = Utils.readObject(
                    new File(".gitlet/commits/" + commitId), Commit.class
                );
                findAndCopyFile(c, fileName);
            }
        } else if (choice == 3) {
            Pointers p = Utils.readObject(
                new File(".gitlet/pointers"), Pointers.class
            );
            if (!p.getPointers().containsKey(branchName)) {
                System.out.println("No such branch exists.");
            } else if (p.getCurrentPointer().equals(branchName)) {
                System.out.println("No need to checkout the current branch.");
            } else {
                ArrayList<Blob> untracked = checkOverwrite(branchName);
                if (untracked == null) {
                    System.out.println(
                        "There is an untracked file in the way; "
                        + "delete it, or add and commit it first."
                    );
                } else {
                    String branchCommitId = p.getPointTo(branchName);

                    p.move("HEAD", branchCommitId);
                    p.setCurrentPointer(branchName);

                    checkoutFiles(untracked, branchCommitId);
                    Utils.writeObject(new File(".gitlet/pointers"), p);
                }
            }
        }
    }

    /** Delete tracked files and copy files from the
     *  most recent commit.
     *  @param untrkd : ArrayList of Blobs of untracked files.
     *  @param commitId : The commit ID of the branch we want to
     *  copy from. */
    public static void
        checkoutFiles(ArrayList<Blob> untrkd, String commitId) {
        Commit branchCommit = Utils.readObject(new File(
                ".gitlet/commits/" + commitId
        ), Commit.class);
        List<String> filesInCurrDir = Utils.plainFilenamesIn(
                System.getProperty("user.dir")
        );
        ArrayList<String> filesToDelete = new ArrayList<>();
        filesToDelete.addAll(filesInCurrDir);
        for (Blob b : untrkd) {
            filesToDelete.remove(b.getFileName());
        }
        for (String s : filesToDelete) {
            Utils.restrictedDelete(s);
        }

        Blob b;
        for (String id : branchCommit.getFileIds()) {
            try {
                b = Utils.readObject(
                        new File(".gitlet/blobs/" + id), Blob.class
                );
                File f = new File(b.getFileName());

                if (f.exists()) {
                    f.delete();
                    File fnew = new File(b.getFileName());
                    Utils.writeContents(fnew, b.getContents());
                    fnew.createNewFile();
                } else {
                    Utils.writeContents(f, b.getContents());
                    f.createNewFile();
                }
            } catch (IOException e) {
                System.out.println(
                        "File does not exist in that commit"
                );
            }
        }
    }

    /** Finds a file from a commit and copies it
     *  to the working directory.
     *  @param fileName : String of file name to find and copy.
     *  @param c : the Commit object to find the file. */
    public static void findAndCopyFile(Commit c, String fileName) {
        boolean found = false;
        Blob b;
        for (String id : c.getFileIds()) {
            try {
                b = Utils.readObject(
                    new File(".gitlet/blobs/" + id), Blob.class
                );
                if (b.getFileName().equals(fileName)) {
                    found = true;
                    File f = new File(fileName);
                    if (f.exists()) {
                        f.delete();
                        File fnew = new File(fileName);
                        Utils.writeContents(fnew, b.getContents());
                        fnew.createNewFile();
                    } else {
                        Utils.writeContents(f, b.getContents());
                        f.createNewFile();
                    }
                }
            } catch (IOException e) {
                throw Utils.error("File does not exist in that commit");
            }
        }
        if (!found) {
            System.out.println("File does not exist in that commit.");
        }
    }

    /** Checks the current directory for untracked files that
     *  might be overwritten by a new checkout.
     *  @return : ArrayList of Blob of the untracked files */
    public static ArrayList<Blob>
        checkUntracked() {
        Pointers p = Utils.readObject(
                new File(".gitlet/pointers"), Pointers.class
        );
        String lastCommitId = p.getPointTo(p.getCurrentPointer());
        Commit lastCommit = Utils.readObject(
            new File(".gitlet/commits/" + lastCommitId), Commit.class
        );

        List<String> currentDirFileNames = Utils.plainFilenamesIn(
            System.getProperty("user.dir")
        );

        ArrayList<Blob> currentDirBlobs = new ArrayList<>();
        for (String f : currentDirFileNames) {
            currentDirBlobs.add(new Blob(
                f, Utils.readContentsAsString(new File(f))
            ));
        }

        Blob b;
        ArrayList<Blob> untracked = new ArrayList<>();
        boolean tracked;
        for (Blob currDirBlob : currentDirBlobs) {
            tracked = false;
            for (String id : lastCommit.getFileIds()) {
                b = Utils.readObject(
                    new File(".gitlet/blobs/" + id), Blob.class
                );
                if (b.isEqual(currDirBlob)) {
                    tracked = true;
                }
            }
            if (!tracked) {
                untracked.add(currDirBlob);
            }
        }

        for (String id : lastCommit.getFileIds()) {
            tracked = false;
            b = Utils.readObject(
                    new File(".gitlet/blobs/" + id), Blob.class
            );
            for (Blob currDirBlob : currentDirBlobs) {
                if (b.getFileName().equals(currDirBlob.getFileName())) {
                    tracked = true;
                }
            }
            if (!tracked) {
                untracked.add(b);
            }
        }
        return untracked;
    }

    /** Checks if checking out will overwrite untracked files.
     *  @param branchName : String of the branch to check.
     *  @return : ArrayList of untracked file blobs */
    public static ArrayList<Blob> checkOverwrite(String branchName) {
        Pointers p = Utils.readObject(
                new File(".gitlet/pointers"), Pointers.class
        );
        ArrayList<Blob> untracked = checkUntracked();
        String branchCommitId = p.getPointTo(branchName);
        Commit branchCommit = Utils.readObject(
                new File(".gitlet/commits/" + branchCommitId), Commit.class
        );
        Blob b;
        for (String id : branchCommit.getFileIds()) {
            b = Utils.readObject(new File(".gitlet/blobs/" + id), Blob.class);
            for (Blob b2 : untracked) {
                if (b2.getFileName().equals(b.getFileName())) {
                    return null;
                }
            }
        }
        return untracked;
    }

    /** Creates a branch to the commit tree.
     *  @param branchName : String of the branch to create. */
    public static void branch(String branchName) {
        Pointers p = Utils.readObject(
            new File(".gitlet/pointers"), Pointers.class
        );
        p.add(branchName, p.getPointTo("HEAD"));
        Utils.writeObject(new File(".gitlet/pointers"), p);
    }

    /** Removes a branch from the commit tree.
     *  @param branchName : String of the branch to remove. */
    public static void rmBranch(String branchName) {
        Pointers p = Utils.readObject(
            new File(".gitlet/pointers"), Pointers.class
        );
        p.remove(branchName);
        Utils.writeObject(new File(".gitlet/pointers"), p);
    }

    /** Checks out to a certain commitID (copy files and move branch).
     *  @param shortId : String of the (possibly short) commit ID. */
    public static void reset(String shortId) {
        String commitId = findLongCommitId(shortId);
        if (commitId.equals("")) {
            System.out.println("No commit with that id exists.");
        } else {
            Pointers p = Utils.readObject(
                new File(".gitlet/pointers"), Pointers.class
            );
            StagingArea s = Utils.readObject(
                new File(".gitlet/stagingarea"), StagingArea.class
            );

            String currPtr = p.getCurrentPointer();
            p.add("temp", commitId);

            Utils.writeObject(new File(".gitlet/pointers"), p);
            checkout(3, null, null, "temp");
            s.clear();

            p.remove("temp");
            p.move("HEAD", commitId);
            p.move(currPtr, commitId);

            Utils.writeObject(new File(".gitlet/pointers"), p);
            Utils.writeObject(new File(".gitlet/stagingarea"), s);
        }
    }

    /** Returns a String of the long commit id.
     *  @param commitId : String of the (possibly short) commit ID. */
    public static String findLongCommitId(String commitId) {
        if ((commitId.length() == Utils.UID_LENGTH)
            && Utils.plainFilenamesIn(".gitlet/commits").contains(commitId)) {
            return commitId;
        } else {
            for (String s : Utils.plainFilenamesIn(".gitlet/commits")) {
                if (s.startsWith(commitId)) {
                    return s;
                }
            }
            return "";
        }
    }

    /** Merge the current branch with the specified branch.
     *  @param branchName : String of the branch to merge to. */
    public static void merge(String branchName) {
        if (mergeNoErrors(branchName)) {
            Pointers p = Utils.readObject(
                    new File(".gitlet/pointers"), Pointers.class
            );
            String splitPoint = findSplitPoint(branchName);
            if (splitPoint.equals(p.getPointTo(branchName))) {
                System.out.println(
                    "Given branch is an ancestor of the current branch."
                );
            } else if (splitPoint.equals(p.getPointTo(p.getCurrentPointer()))) {
                reset(p.getPointTo(branchName));
                System.out.println("Current branch fast-forwarded");
            }
        }
    }

    /** Returns whether there are errors (branchName/untracked files).
     *  @param branchName : String of the branch to merge to. */
    public static boolean mergeNoErrors(String branchName) {
        Pointers p = Utils.readObject(
                new File(".gitlet/pointers"), Pointers.class
        );
        StagingArea s = Utils.readObject(
                new File(".gitlet/stagingarea"), StagingArea.class
        );
        if (!s.isEmpty()) {
            System.out.println("You have uncommitted changes.");
            return false;
        } else if (!p.getPointers().containsKey(branchName)) {
            System.out.println("A branch with that name does not exist.");
            return false;
        } else if (p.getCurrentPointer().equals(branchName)) {
            System.out.println("Cannot merge a branch with itself.");
            return false;
        }
        ArrayList<Blob> b = checkOverwrite(branchName);
        if (b == null) {
            System.out.println("There is an untracked file in the way; "
                    + "delete it, or add and commit it first.");
            return false;
        }
        return true;
    }

    /** Finds the split point of the current branch and the specified branch.
     *  @param branchName : String of the branch to merge to.
     *  @return : String of the commit ID of the split point. */
    public static String findSplitPoint(String branchName) {
        Pointers p = Utils.readObject(
                new File(".gitlet/pointers"), Pointers.class
        );
        String lastCommitId = p.getPointTo(p.getCurrentPointer());
        Commit lastCommit = Utils.readObject(
                new File(".gitlet/commits/" + lastCommitId), Commit.class
        );
        String branchCommitId = p.getPointTo(branchName);
        Commit branchCommit = Utils.readObject(
                new File(".gitlet/commits/" + branchCommitId), Commit.class
        );

        ArrayList<String> lastCommitIds = new ArrayList<>();
        ArrayList<String> branchCommitIds = new ArrayList<>();
        while (lastCommit.getParent() != null) {
            lastCommitIds.add(lastCommit.getId());
            lastCommit = Utils.readObject(new File(
                    ".gitlet/commits/" + lastCommit.getParent()
            ), Commit.class);
        }
        lastCommitIds.add(lastCommit.getId());

        while (branchCommit.getParent() != null) {
            branchCommitIds.add(branchCommit.getId());
            branchCommit = Utils.readObject(new File(
                    ".gitlet/commits/" + branchCommit.getParent()
            ), Commit.class);
        }
        branchCommitIds.add(branchCommit.getId());

        for (String id1 : lastCommitIds) {
            for (String id2 : branchCommitIds) {
                if (id1.equals(id2)) {
                    return id1;
                }
            }
        }
        return "";
    }
}
