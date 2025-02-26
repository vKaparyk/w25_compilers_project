package absyn;

public class NameTy extends Absyn {
	final static int BOOL = 0;
	final static int INT = 1;
	final static int VOID = 2;

	public int typ;

	public NameTy(int row, int column, int typ) {
		this.row = row;
		this.column = column;
		this.typ = typ;
	}

	public void accept(AbsynVisitor visitor, int level) {
		visitor.visit(this, level);
	}
}
