package absyn;

public class CallExp extends Exp {
	public String func;
	public ExpList args;
	public FunctionDec def;

	public CallExp(int row, int column, String func, ExpList args) {
		this.row = row;
		this.column = column;
		this.func = func;
		this.args = args;
	}

	public void accept(AbsynVisitor visitor, int level, boolean flag) { visitor.visit(this, level, false); }

	@Override
	public String toString() { return func + "(" + args.toString() + ")"; }
}
