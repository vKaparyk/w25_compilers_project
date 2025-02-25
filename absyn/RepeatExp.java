package absyn;

public class RepeatExp extends Exp {
  public ExpList exps;
  public Exp test;

  public RepeatExp( int row, int col, ExpList exps, Exp test ) {
    this.row = row;
    this.col = col;
    this.exps = exps;
    this.test = test;
  }

  public void accept( AbsynVisitor visitor, int level ) {
    visitor.visit( this, level );
  }
}
