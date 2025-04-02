package absyn;

import java.util.ArrayList;

public class FunctionDec extends Dec {
	public class IntWrapper {
		public int funaddr;

		public IntWrapper(int i) {
			funaddr = i;
		}
	}

	public VarDecList params;
	public Exp body;
	public IntWrapper funaddr = new IntWrapper(-1);

	public ArrayList<Integer> backpatchLocs = new ArrayList<Integer>();

	public FunctionDec(int row, int column, NameTy result, String func, VarDecList params, Exp body) {
		this.row = row;
		this.column = column;
		this.typ = result;
		this.name = func;
		this.params = params;
		this.body = body;
	}

	public void accept(AbsynVisitor visitor, int level, boolean flag) {
		visitor.visit(this, level, flag);
	}

	@Override
	public String toString() {
		return toString(body instanceof NilExp);
	}

	public String toString(boolean isPrototype) {
		return typ.toString() + " " + name + "(" + params.toString(", ", true) + ")" + ((isPrototype) ? ";" : " ...");
	}
}