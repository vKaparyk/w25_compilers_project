package absyn;

public class SimpleVar extends Var {
	public String name;

	public SimpleVar(int pos, String name) {
		this.pos = pos;
		this.name = name;
	}

	public void accept(AbsynVisitor visitor, int level) {
		visitor.visit(this, level);
	}
}