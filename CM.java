import java.io.*;
import absyn.*;

import java.nio.file.Path;
import java.nio.file.Paths;

class CM {
	public static boolean SHOW_TREE = false;
	public static boolean SHOW_TABLE = false;
	public static boolean SHOW_ASS = false;

	public static String getPathWithoutExtension(String filename) {
		Path path = Paths.get(filename);
		String fileName = path.getFileName().toString();
	
		// Remove the extension from the file name
		int lastDotIndex = fileName.lastIndexOf('.');
		String fileNameWithoutExtension = (lastDotIndex == -1) ? fileName // No extension found
				: fileName.substring(0, lastDotIndex);
	
		// Reconstruct the full path without the extension
		Path parentPath = path.getParent();
		if (parentPath != null) {
			return parentPath.resolve(fileNameWithoutExtension).toString();
		} else {
			return fileNameWithoutExtension;
		}
	}

	static public void main(String argv[]) {
		if (argv.length == 0) {
			System.err.println("Error: File name is required.");
			System.exit(1);
		}

		String fileName = "";

		// Parse optional flags
		// TODO: verify only 1 flag can appear total (project Overview.pdf; cmd-line
		// options)
		// TODO: what if no flags
		for (int i = 0; i < argv.length; i++) {
			switch (argv[i]) {
			case "-a":
				SHOW_TREE = true;
				break;
			case "-s":
				SHOW_TABLE = true;
				SHOW_TREE = true;
				break;
			case "-c":
				SHOW_TABLE = true;
				SHOW_TREE = true;
				SHOW_ASS = true;
				break;
			default:
				fileName = argv[i];
			}
		}

		/* Start the parser */
		try {
			Lexer l = new Lexer(new FileReader(fileName), fileName);
			CustomParser p = new CustomParser(l, fileName);
			Absyn result = (Absyn) (p.parse().value);
			AbsynVisitor visitor = new ShowTreeVisitor();;
			PrintStream filePrintStream;
			if (SHOW_TREE && result != null) {
				String absynFile = getPathWithoutExtension(fileName) + ".abs";

				filePrintStream = new PrintStream(absynFile);
				System.setOut(filePrintStream);

				System.out.println("The abstract syntax tree is:");
				result.accept(visitor, 0, false);
				if (l.invalid_lex || p.invalid_parse) {
					System.err.println("Errors encountered during Lexical/Syntatic analysis. exiting.");
					return;
				}
			}

			if (SHOW_TABLE && result != null) {
				String symbolFile = getPathWithoutExtension(fileName) + ".sym";

				filePrintStream = new PrintStream(symbolFile);
				System.setOut(filePrintStream);

				System.out.println("The symbol table is:");
				visitor = new SemanticAnalyzer(fileName);
				result.accept(visitor, 0, false);
				((SemanticAnalyzer) visitor).checkForMain();

				if (((SemanticAnalyzer) visitor).invalid_symbol_tabling) {
					System.err.println(
							"Errors encountered during Symbol Table Generation/Type Checking analysis. exiting.");
					return;
				}
			}

			if (SHOW_ASS && result != null) {
				String tmFile = getPathWithoutExtension(fileName) + ".tm";

				visitor = new CodeGenerator(tmFile);
				((CodeGenerator)visitor).visit(result);
				((CodeGenerator)visitor).closeWriter();

				if (((CodeGenerator) visitor).failedGeneration) {
					System.err.println(
							"Errors encountered during assembly generation. exiting.");
					return;
				}
			}
		} catch (Exception e) {
			/* do cleanup here -- possibly rethrow e */
			e.printStackTrace();
		}
	}
}
