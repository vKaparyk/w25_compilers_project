import java_cup.runtime.Symbol;

public class CustomParser extends parser {
    private String fileName;

    public CustomParser(Lexer lexer, String fileName) {
        super(lexer);
        this.fileName = fileName;
    }

    public void syntax_error(Symbol info) {
        // System.err.print(this.fileName + ":"
        // + (info.left + 1) + ":"
        // + (info.right + 1) + ": ");
        return;
    }

    public void report_error(String message, Object info) {
        // // System.err.println(message);
        // // }

        // public void report_error(String message, Symbol info) {
        // System.err.println(info);
        if (info instanceof Symbol) {
            Symbol info2 = (Symbol) info;
            System.err.print(this.fileName + ":"
                    + (info2.left + 1) + ":"
                    + (info2.right + 1) + ": ");
        }
        System.err.println(message);
    }
}