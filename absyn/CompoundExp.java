package absyn;

public class CompoundExp extends Exp {
	public VarDecList decs;
	public ExpList exps;

	// dtype irrelevant

	public CompoundExp(int row, int column, VarDecList decs, ExpList exps) {
		this.row = row;
		this.column = column;
		this.decs = decs;
		this.exps = exps;
	}

	public void accept(AbsynVisitor visitor, int level, boolean flag) { visitor.visit(this, level, false); }

	@Override
	public String toString() { return "{" + decs.toString("; ") + "" + exps.toString("; ") + "}"; }
}
