package absyn;

public class FunctionDec extends Dec {
	public NameTy result;
	public String func;
	public VarDecList params;
	public Exp body;

	public FunctionDec(int pos, NameTy result, String func, VarDecList params, Exp body) {
		this.result = result;
		this.func = func;
		this.params = params;
		this.body = body;
	}

	public void accept(AbsynVisitor visitor, int level) {
		visitor.visit(this, level);
	}
}
