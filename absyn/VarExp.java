package absyn;

public class VarExp extends Exp {
	public String name;

	public VarExp(int row, int column, String name) {
		this.row = row;
		this.column = column;
		this.name = name;
	}

	public void accept(AbsynVisitor visitor, int level) {
		visitor.visit(this, level);
	}
}
