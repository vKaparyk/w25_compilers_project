package absyn;

public class VarExp extends Exp {
	public Var variable;

	// TODO: dtype
	// will be set dynamically (mid symbol table scan)

	public VarExp(int row, int column, Var variable) {
		this.row = row;
		this.column = column;
		this.variable = variable;
	}

	public void accept(AbsynVisitor visitor, int level) {
		visitor.visit(this, level);
	}
}
