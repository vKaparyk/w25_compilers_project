package absyn;

public class IntExp extends Exp {
	public String value;

	public IntExp(int row, int column, String value) {
		this.row = row;
		this.column = column;
		this.value = value;
	}

	public void accept(AbsynVisitor visitor, int level) {
		visitor.visit(this, level);
	}
}
