package absyn;

public class OpExp extends Exp {
	public final static int PLUS = 0;
	public final static int MINUS = 1;
	public final static int TIMES = 2;
	public final static int DIV = 3;
	public final static int EQ = 4;
	public final static int NEQ = 5;
	public final static int LT = 6;
	public final static int LTE = 7;
	public final static int GT = 8;
	public final static int GTE = 9;
	public final static int NOT = 10;
	public final static int AND = 11;
	public final static int OR = 12;
	public final static int UMINUS = 13;

	public Exp left;
	public int op;
	public Exp right;

	public OpExp(int row, int column, Exp left, int op, Exp right) {
		this.row = row;
		this.column = column;
		this.left = left;
		this.op = op;
		this.right = right;

		NameTy op_exp_dec = null;
		switch (op) {
		case OpExp.PLUS:
		case OpExp.MINUS:
		case OpExp.TIMES:
		case OpExp.DIV:
		case OpExp.UMINUS:
			op_exp_dec = new NameTy(row, column, NameTy.INT);
			break;
		case OpExp.EQ:
		case OpExp.NEQ:
		case OpExp.LT:
		case OpExp.LTE:
		case OpExp.GT:
		case OpExp.GTE:
		case OpExp.NOT:
		case OpExp.AND:
		case OpExp.OR:
			op_exp_dec = new NameTy(row, column, NameTy.BOOL);
			break;
		default:
			op_exp_dec = new NameTy(row, column, NameTy.VOID);
			break;
		}
		this.dtype = new SimpleDec(row, column, op_exp_dec, op_exp_dec.toString());

	}

	public void accept(AbsynVisitor visitor, int level) { visitor.visit(this, level); }
}
