package absyn;

public class IntExp extends Exp {
  public String value;

  public IntExp( int pos, String value ) {
    this.pos = pos;
    this.value = value;
  }

  public void accept( AbsynVisitor visitor, int level ) {
    visitor.visit( this, level );
  }
}
