package absyn;

public class CompoundExp extends Exp {
	public VarDecList decs;
	public ExpList exps;
	public ExpList elsepart;

	public CompoundExp(int pos, VarDecList decs, ExpList exps) {
		this.pos = pos;
		this.decs = decs;
		this.exps = exps;
	}

	public void accept(AbsynVisitor visitor, int level) {
		visitor.visit(this, level);
	}
}
