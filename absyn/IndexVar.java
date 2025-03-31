package absyn;

public class IndexVar extends Var {
	public Exp index;
	public ArrayDec def;

	public IndexVar(int row, int column, String name, Exp index) {
		this.row = row;
		this.column = column;
		this.name = name;
		this.index = index;
	}

	public void accept(AbsynVisitor visitor, int level, boolean flag) {
		visitor.visit(this, level, flag);
	}

	@Override
	public String toString() {
		return name + "[" + index.toString() + "]";
	}
}