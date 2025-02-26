package absyn;

public class WhileExp extends Exp {
	public Exp test;
	public Exp body;

	public WhileExp(int row, int column, Exp test, Exp body) {
		this.row = row;
		this.column = column;
		this.test = test;
		this.body = body;
	}

	public void accept(AbsynVisitor visitor, int level) {
		visitor.visit(this, level);
	}
}
