package absyn;

public class IfExp extends Exp {
	public Exp test;
	public ExpList thenpart;
	public ExpList elsepart;

	public IfExp(int row, int column, Exp test, ExpList thenpart, ExpList elsepart) {
		this.row = row;
		this.column = column;
		this.test = test;
		this.thenpart = thenpart;
		this.elsepart = elsepart;
	}

	public void accept(AbsynVisitor visitor, int level) {
		visitor.visit(this, level);
	}
}
