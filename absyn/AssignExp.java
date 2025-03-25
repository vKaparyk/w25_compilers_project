package absyn;

public class AssignExp extends Exp {
	public VarExp lhs;
	public Exp rhs;

	public AssignExp(int row, int column, VarExp lhs, Exp rhs) {
		this.row = row;
		this.column = column;
		this.lhs = lhs;
		this.rhs = rhs;
	}

	public void accept(AbsynVisitor visitor, int level, boolean flag) { visitor.visit(this, level, false); }

	@Override
	public String toString() { return lhs.toString() + " = " + rhs.toString(); }
}
