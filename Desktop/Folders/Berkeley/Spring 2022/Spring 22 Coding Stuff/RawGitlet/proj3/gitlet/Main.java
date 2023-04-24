package gitlet;

import java.io.File;

/** Driver class for Gitlet, the tiny stupid version-control system.
 *  @author Jason Tjahjono
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND> .... */
    public static void main(String... args) {
        if (args.length == 0) {
            System.out.println("Please enter a command.");
        } else {
            if (args[0].equals("init") && (args.length == 1)) {
                if (new File(".gitlet").exists()) {
                    System.out.println(
                        "A Gitlet version-control system already"
                        + " exists in the current directory."
                    );
                } else {
                    Dashboard.init();
                }
            } else {
                if (!(new File(".gitlet").exists())) {
                    System.out.println(
                        "Not in an initialized Gitlet directory."
                    );
                } else {
                    menu(args);
                }
            }
        }
    }

    /** Usage: the logic for the various command choices.
     *  @param args : Arguments passed from main */
    public static void menu(String... args) {
        if (args[0].equals("add") && (args.length == 2)) {
            Dashboard.add(args[1]);
        } else if (args[0].equals("commit") && (args.length == 2)) {
            Dashboard.commit(args[1]);
        } else if (args[0].equals("rm") && (args.length == 2)) {
            Dashboard.rm(args[1]);
        } else if (args[0].equals("log") && (args.length == 1)) {
            Dashboard.log();
        } else if (args[0].equals("global-log")
                && (args.length == 1)) {
            Dashboard.globalLog();
        } else if (args[0].equals("find") && (args.length == 2)) {
            Dashboard.find(args[1]);
        } else if (args[0].equals("status") && (args.length == 1)) {
            Dashboard.status();
        } else if (args[0].equals("checkout")
                && (args.length == 3)) {
            if (args[1].equals("--")) {
                Dashboard.checkout(1, null, args[2], null);
            } else {
                System.out.println("Incorrect operands.");
            }
        } else if (args[0].equals("checkout")
                && (args.length == 4)) {
            if (args[2].equals("--")) {
                Dashboard.checkout(2, args[1], args[3], null);
            } else {
                System.out.println("Incorrect operands.");
            }
        } else if (args[0].equals("checkout")
                && (args.length == 2)) {
            Dashboard.checkout(3, null, null, args[1]);
        } else if (args[0].equals("branch") && (args.length == 2)) {
            Dashboard.branch(args[1]);
        } else if (args[0].equals("rm-branch")
                && (args.length == 2)) {
            Dashboard.rmBranch(args[1]);
        } else if (args[0].equals("reset") && (args.length == 2)) {
            Dashboard.reset(args[1]);
        } else if (args[0].equals("merge") && (args.length == 2)) {
            Dashboard.merge(args[1]);
        } else {
            System.out.println("No command with that name exists.");
        }
    }
}
