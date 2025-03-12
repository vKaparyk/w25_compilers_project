package absyn;

public class IndexVar extends Var {
	public String name;
	public Exp index;

	public IndexVar(int row, int column, String name, Exp index) {
		this.row = row;
		this.column = column;
		this.name = name;
		this.index = index;
	}

	public void accept(AbsynVisitor visitor, int level) { visitor.visit(this, level); }

	@Override
	public String toString() { return name + "[" + index.toString() + "]"; }
}