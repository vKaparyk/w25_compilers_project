package absyn;

public class OpExp extends Exp {
  public final static int PLUS   = 0;
  public final static int MINUS  = 1;
  public final static int TIMES  = 2;
  public final static int DIV    = 3;
  public final static int EQ     = 4;
  public final static int NEQ    = 5;
  public final static int LT     = 6;
  public final static int LTE    = 7;
  public final static int GT     = 8;
  public final static int GTE    = 9;
  public final static int NOT    = 10;
  public final static int AND    = 11;
  public final static int OR     = 12;
  public final static int UMINUS = 13;

  public Exp left;
  public int op;
  public Exp right;

  public OpExp( int pos, Exp left, int op, Exp right ) {
    this.pos = pos;
    this.left = left;
    this.op = op;
    this.right = right;
  }

  public void accept( AbsynVisitor visitor, int level ) {
    visitor.visit( this, level );
  }
}
