package absyn;

public class ReturnExp extends Exp {
	public Exp exp;

	public ReturnExp(int row, int column, Exp exp) {
		this.row = row;
		this.column = column;
		this.exp = exp;
	}

	public void accept(AbsynVisitor visitor, int level, boolean flag) { visitor.visit(this, level, false); }

	@Override
	public String toString() { return "return " + exp.toString() + ";"; }
}
