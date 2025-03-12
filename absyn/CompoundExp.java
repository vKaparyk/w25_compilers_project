package absyn;

public class CompoundExp extends Exp {
	public VarDecList decs;
	public ExpList exps;
	public ExpList elsepart;

	// dtype irrelevant

	public CompoundExp(int row, int column, VarDecList decs, ExpList exps) {
		this.row = row;
		this.column = column;
		this.decs = decs;
		this.exps = exps;
	}

	public void accept(AbsynVisitor visitor, int level) { visitor.visit(this, level); }
}
