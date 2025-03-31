package absyn;

public class BoolExp extends Exp {
	public Boolean value;

	public BoolExp(int row, int column, boolean value) {
		this.row = row;
		this.column = column;
		this.value = value;
		this.dtype = new SimpleDec(row, column, new NameTy(row, column, NameTy.BOOL), this.value.toString());
	}

	public void accept(AbsynVisitor visitor, int level, boolean flag) {
		visitor.visit(this, level, flag);
	}

	@Override
	public String toString() {
		return value.toString();
	}
}
