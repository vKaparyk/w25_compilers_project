package absyn;

public class ReadExp extends Exp {
  public VarExp input;

  public ReadExp( int row, int col, VarExp input ) {
    this.row = row;
    this.col = col;
    this.input = input;
  }

  public void accept( AbsynVisitor visitor, int level ) {
    visitor.visit( this, level );
  }
}
