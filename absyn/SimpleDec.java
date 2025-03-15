package absyn;

public class SimpleDec extends VarDec {

	public SimpleDec(int row, int column, NameTy typ, String name) {
		this.row = row;
		this.column = column;
		this.typ = typ;
		this.name = name;
	}

	public void accept(AbsynVisitor visitor, int level) { visitor.visit(this, level); }

	@Override
	public String toString() { return typ.toString() + " " + name; }
}
