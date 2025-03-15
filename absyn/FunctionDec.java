package absyn;

public class FunctionDec extends Dec {
	public VarDecList params;
	public Exp body;

	public FunctionDec(int row, int column, NameTy result, String func, VarDecList params, Exp body) {
		this.row = row;
		this.column = column;
		this.typ = result;
		this.name = func;
		this.params = params;
		this.body = body;
	}

	public void accept(AbsynVisitor visitor, int level) { visitor.visit(this, level); }

	@Override
	public String toString() { return toString(false); }

	public String toString(boolean isPrototype) {
		return typ.toString() + " " + name + "(" + params.toString(", ", true) + ")"
				+ ((isPrototype) ? ";" : " {...}");
	}
}