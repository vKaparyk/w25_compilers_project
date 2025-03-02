import java_cup.runtime.Symbol;

public class CustomParser extends parser {
    private String fileName;

    // Constructor to accept the filename and lexer
    public CustomParser(Lexer lexer, String fileName) {
        super(lexer); // Call the parent constructor with the lexer
        this.fileName = fileName; // Store the filename
    }




    // Override syntax_error to include the filename in error messages
    @Override
    public void syntax_error(Symbol info){
        String errMsg = this.fileName + ":" + (info.left+1) + ":" + info.right + ": ";
        if (info.value != null) {
            errMsg += "error: got " + info.value + ", ";
        }
        System.err.print(errMsg);
    }

    /* Change the method report_error so it will display the line and
       column of where the error occurred in the input as well as the
       reason for the error which is passed into the method in the
       String 'message'. */
    @Override
    public void report_error(String message, Object info) {
        System.err.println(message);
    }
}