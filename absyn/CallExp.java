package absyn;

public class CallExp extends Exp {
	public String func;
	public ExpList args;

	// TODO: dtype. HOLY SHIT, WHAT, HOW
	// find it's functionDec, and use that
	// will probably have to be done at SymbolTable level

	public CallExp(int row, int column, String func, ExpList args) {
		this.row = row;
		this.column = column;
		this.func = func;
		this.args = args;
	}

	public void accept(AbsynVisitor visitor, int level) { visitor.visit(this, level); }
}
