package absyn;

public class ReturnExp extends Exp {
	public Exp exp;

	public ReturnExp(int pos, Exp exp) {
		this.pos = pos;
		this.exp = exp;
	}

	public void accept(AbsynVisitor visitor, int level) {
		visitor.visit(this, level);
	}
}
