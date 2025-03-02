import java_cup.runtime.Symbol;

public class CustomParser extends parser {
    private String fileName;

    public CustomParser(Lexer lexer, String fileName) {
        super(lexer); 
        this.fileName = fileName;
    }

    public void syntax_error(Symbol info) {
        System.err.print(this.fileName + ":"
                + (info.left + 1) + ":"
                + (info.right + 1) + ": ");
    }

    public void report_error(String message, Object info) {
        System.err.println(message);
    }
}