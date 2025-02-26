package absyn;

public class NilExp extends Exp {
	public NilExp(int row, int column) {
		this.row = row;
		this.column = column;
	}

	public void accept(AbsynVisitor visitor, int level) {
		visitor.visit(this, level);
	}
}
