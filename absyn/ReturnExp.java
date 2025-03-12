package absyn;

public class ReturnExp extends Exp {
	public Exp exp;

	public ReturnExp(int row, int column, Exp exp) {
		this.row = row;
		this.column = column;
		this.exp = exp;
		this.dtype = exp.dtype;
	}

	public void accept(AbsynVisitor visitor, int level) { visitor.visit(this, level); }
}
