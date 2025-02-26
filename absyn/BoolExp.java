package absyn;

public class BoolExp extends Exp {
	public Boolean value;

	public BoolExp(int pos, Boolean value) {
		this.pos = pos;
		this.value = value;
	}

	public void accept(AbsynVisitor visitor, int level) {
		visitor.visit(this, level);
	}
}
