/*
  Created by: Fei Song
  File Name: Main.java
  To Build: 
  After the Scanner.java, tiny.flex, and tiny.cup have been processed, do:
    javac Main.java
  
  To Run: 
    java -classpath /usr/share/java/cup.jar:. Main gcd.tiny

  where gcd.tiny is an test input file for the tiny language.
*/

import java.io.*;
import absyn.*;

import java.nio.file.Path;
import java.nio.file.Paths;

class Main {
    public static boolean SHOW_TREE = false;
    public static boolean SHOW_TABLE = false;

    public static String getPathWithoutExtension(String filename) {
        Path path = Paths.get(filename);
        String fileName = path.getFileName().toString();

        // Remove the extension from the file name
        int lastDotIndex = fileName.lastIndexOf('.');
        String fileNameWithoutExtension = (lastDotIndex == -1)
            ? fileName // No extension found, return the original file name
            : fileName.substring(0, lastDotIndex);

        // Reconstruct the full path without the extension
        Path pathWithoutExtension = path.getParent().resolve(fileNameWithoutExtension);

        return pathWithoutExtension.toString();
    }

    static public void main(String argv[]) {
        if (argv.length == 0) {
            System.err.println("Error: File name is required.");
            System.exit(1);
        }

        String fileName = argv[0];

        // Parse optional flags
        for (int i = 1; i < argv.length; i++) {
            switch (argv[i]) {
                case "-a":
                    SHOW_TREE = true;
                    break;
                case "-s":
                    SHOW_TABLE = true;
                    break;
                default:
                    System.err.println("Error: Unknown option " + argv[i]);
                    System.exit(1);
            }
        }

        /* Start the parser */
        try {
            parser p = new CustomParser(new Lexer(new FileReader(fileName), fileName), fileName);
            Absyn result = (Absyn) (p.parse().value);
            PrintStream filePrintStream;
            if (SHOW_TREE && result != null) {
                String absynFile = getPathWithoutExtension(fileName) + "_absyn.txt";
                
                filePrintStream = new PrintStream(absynFile);
                System.setOut(filePrintStream);

                System.out.println("The abstract syntax tree is:");
                AbsynVisitor visitor = new ShowTreeVisitor();
                result.accept(visitor, 0);
            }
            if (SHOW_TABLE && result != null) {
                String symbolFile = getPathWithoutExtension(fileName) + "_SymTable.txt";
                
                filePrintStream = new PrintStream(symbolFile);
                System.setOut(filePrintStream);
                
                System.out.println("The symbol table is:");
                AbsynVisitor visitor = new SemanticAnalyzer(fileName);
                result.accept(visitor, 0);
            }
        } catch (Exception e) {
            /* do cleanup here -- possibly rethrow e */
            e.printStackTrace();
        }
    }
}
