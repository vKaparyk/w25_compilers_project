package absyn;

public class WhileExp extends Exp {
	public Exp test;
	public Exp body;

	// dtype used for error printing
	public WhileExp(int row, int column, Exp test, Exp body) {
		this.row = row;
		this.column = column;
		this.test = test;
		this.body = body;
		this.dtype = new SimpleDec(row, column, new NameTy(row, column, NameTy.BOOL), "");
	}

	public void accept(AbsynVisitor visitor, int level, boolean flag) { visitor.visit(this, level, false); }

	@Override
	public String toString() { return "while (" + test.toString() + ") ..."; }
}
