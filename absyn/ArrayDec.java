package absyn;

public class ArrayDec extends VarDec {
	public int size;

	public ArrayDec(int row, int column, NameTy typ, String name, int size) {
		this.row = row;
		this.column = column;
		this.typ = typ;
		this.name = name;
		this.size = size;
	}

	public void accept(AbsynVisitor visitor, int level, boolean flag) { visitor.visit(this, level, false); }

	@Override
	public String toString() { return typ.toString() + " " + name + "[" + size + "]"; }

	@Override
	public boolean isArray() { return true; }
}
