package absyn;

public class ArrayDec extends VarDec {
	public NameTy typ;
	public String name;
	public int size;

	public ArrayDec(int row, int column, NameTy typ, String name, int size) {
		this.row = row;
		this.column = column;
		this.typ = typ;
		this.name = name;
		this.size = size;
	}

	public void accept(AbsynVisitor visitor, int level) { visitor.visit(this, level); }

	@Override
	public String toString() { return typ.toString() + " " + name + "[" + size + "]"; }
}
