package absyn;

public class IfExp extends Exp {
	public Exp test;
	public Exp thenpart;
	public Exp elsepart;

	// dtype used for error printing
	public IfExp(int row, int column, Exp test, Exp thenpart, Exp elsepart) {
		this.row = row;
		this.column = column;
		this.test = test;
		this.thenpart = thenpart;
		this.elsepart = elsepart;
		this.dtype = new SimpleDec(row, column, new NameTy(row, column, NameTy.BOOL), "");
	}

	public void accept(AbsynVisitor visitor, int level) { visitor.visit(this, level); }

	@Override
	public String toString() { return "if (" + test.toString() + ") ..."; }
}
