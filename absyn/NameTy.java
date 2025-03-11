package absyn;

public class NameTy extends Absyn {
	public final static int BOOL = 0;
	public final static int INT = 1;
	public final static int VOID = 2;

	public int typ;

	public NameTy(int row, int column, int typ) {
		this.row = row;
		this.column = column;
		this.typ = typ;
	}

	public void accept(AbsynVisitor visitor, int level) {
		visitor.visit(this, level);
	}

	public String toString() {
		switch (typ){
			case 0:
				return "bool";
			case 1:
				return "int";
			case 2:
				return "void";
			default:
				return "err";
		}
	}
}
