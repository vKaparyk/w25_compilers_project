package absyn;

public class AssignExp extends Exp {
	public VarExp lhs;
	public Exp rhs;

	// dtype irrelevant; just need to make sure 2 dtypes are equal

	public AssignExp(int row, int column, VarExp lhs, Exp rhs) {
		this.row = row;
		this.column = column;
		this.lhs = lhs;
		this.rhs = rhs;
	}

	public void accept(AbsynVisitor visitor, int level) { visitor.visit(this, level); }
}
