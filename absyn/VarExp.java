package absyn;

public class VarExp extends Exp {
	public Var variable;

	public VarExp(int row, int column, Var variable) {
		this.row = row;
		this.column = column;
		this.variable = variable;
	}

	public void accept(AbsynVisitor visitor, int level, boolean flag) { visitor.visit(this, level, false); }

	@Override
	public String toString() { return variable.toString(); }
}
