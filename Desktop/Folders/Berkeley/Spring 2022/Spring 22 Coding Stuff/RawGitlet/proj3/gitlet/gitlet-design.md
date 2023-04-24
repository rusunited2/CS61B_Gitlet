# Gitlet Design Document
**Name**: Jason Tjahjono

## Classes and Data Structures
### Main
* **Description**: Accepts the command and arguments, and call the appropriate function from Dashboard.
* **Data Structures**: N/A

### Dashboard
* **Description**: Hosts all of the methods and logic of Gitlet commands (add, commit, etc).
* **Data Structures**: N/A

### Commit
* **Description**: An object which represents one commit command.
* **Data Structures**:
  * **String parent**: stores the ID of the parent commit.
  * **String message**: stores the commit message.
  * **Date date**: stores the date when commit was created.
  * **String id**: stores a unique ID from serializing this object.
  * **ArrayList<String> files**: stores the IDs of the Blobs of files.

### Blob
* **Description**: An object which represents one file.
* **Data Structures**:
    * **String fileName**: stores the name of the file.
    * **String contents**: stores the contents of the file.
    * **String id**: stores a unique ID from serializing this object.
  
### Staging Area
* **Description**: An object which represents one file.
* **Data Structures**:
    * **HashMap<String, Blob> addition**: Represents the Addition Stage, which stores (Filename => Blob File).
    * **HashMap<String, Blob> removal**: Represents the Removal Stage, which stores (Filename => Blob File).

### Pointers
* **Description**: An object which stores all the branch pointers.
* **Data Structures**:
    * **HashMap<String, String> pointers**: stores all the pointers in the form (pointerName => commitID).
    * **String currentPointer**: stores the name of the current branch (Ex: master). 

## Algorithms



## Persistence
* **Pointers Object**: Keeps track of the pointers to the current branch and all the branches in the tree.
* **Commit Objects**: Keeps all the commits that has ever been committed.
* **Blob Objects**: Keeps all the blobs that corresponds to the commits. Other blobs created but not committed will not be saved.
* **StagingArea Object**: Keeps track of the Stage for Addition and Stage for Removal.

