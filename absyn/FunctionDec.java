package absyn;

public class FunctionDec extends Dec {
	public VarDecList params;
	public Exp body;
	public int funaddr;

	public FunctionDec(int row, int column, NameTy result, String func, VarDecList params, Exp body) {
		this.row = row;
		this.column = column;
		this.typ = result;
		this.name = func;
		this.params = params;
		this.body = body;
		
		this.funaddr = -1;
	}

	public void accept(AbsynVisitor visitor, int level, boolean flag) { visitor.visit(this, level, false); }

	@Override
	public String toString() { return toString(body instanceof NilExp); }

	public String toString(boolean isPrototype) {
		return typ.toString() + " " + name + "(" + params.toString(", ", true) + ")" + ((isPrototype) ? ";" : " ...");
	}
}