package absyn;

public class NameTy extends absyn {
	final static int BOOL = 0;
	final static int INT = 1;
	final static int VOID = 2;

	public int pos;
	public int typ;

	public NameTy(int pos, int typ) {
		this.pos = pos;
		this.typ = typ;
	}

	public void accept(AbsynVisitor visitor, int level) {
		visitor.visit(this, level);
	}
}
