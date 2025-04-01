package absyn;

public class SimpleVar extends Var {
	public VarDec def;

	public SimpleVar(int row, int column, String name) {
		this.row = row;
		this.column = column;
		this.name = name;
	}

	public void accept(AbsynVisitor visitor, int level, boolean flag) {
		visitor.visit(this, level, flag);
	}

	@Override
	public String toString() {
		return name;
	}
}