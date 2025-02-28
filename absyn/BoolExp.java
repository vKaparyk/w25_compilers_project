package absyn;

public class BoolExp extends Exp {
	public Boolean value;

	public BoolExp(int row, int column, boolean value) {
		this.row = row;
		this.column = column;
		this.value = value;
	}

	public void accept(AbsynVisitor visitor, int level) {
		visitor.visit(this, level);
	}
}
