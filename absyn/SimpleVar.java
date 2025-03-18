package absyn;

public class SimpleVar extends Var {

	public SimpleVar(int row, int column, String name) {
		this.row = row;
		this.column = column;
		this.name = name;
	}

	public void accept(AbsynVisitor visitor, int level) { visitor.visit(this, level); }

	@Override
	public String toString() { return name; }
}