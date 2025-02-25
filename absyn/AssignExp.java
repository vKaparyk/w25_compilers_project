package absyn;

public class AssignExp extends Exp {
  public VarExp lhs;
  public Exp rhs;

  public AssignExp( int pos, VarExp lhs, Exp rhs ) {
    this.pos = pos;
    this.lhs = lhs;
    this.rhs = rhs;
  }
  
  public void accept( AbsynVisitor visitor, int level ) {
    visitor.visit( this, level );
  }
}
