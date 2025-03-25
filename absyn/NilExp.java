package absyn;

public class NilExp extends Exp {
	public NilExp(int row, int column) {
		this.row = row;
		this.column = column;
		this.dtype = new SimpleDec(row, column, new NameTy(row, column, NameTy.VOID), "null");
	}

	public void accept(AbsynVisitor visitor, int level, boolean flag) { visitor.visit(this, level, false); }

	@Override
	public String toString() { return ""; }
}
