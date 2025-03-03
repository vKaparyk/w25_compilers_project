import java.util.ArrayList;

import absyn.*;

public class ShowTreeVisitor implements AbsynVisitor {

	private ArrayList<Boolean> indents = new ArrayList<Boolean>();

	private void indent(int level) {
		// System.err.println(indents);
		if (indents.size() < level)
			indents.add(true);
		for (int i = 0; i < (level - 1); i++)
			System.out.print(indents.get(i) ? "  │ " : "    ");

		if (level != 0) {
			System.out.print("  └─");
		}
	}

	private void start_block(int level) {
		if (indents.size() < (level + 1))
			this.indents.add(true);

		if (level != 0)
			this.indents.set(level - 1, true);
	}

	private void end_block(int level) {
		if (indents.size() < (level + 1))
			this.indents.add(false);
		if (level != 0)
			this.indents.set(level - 1, false);
	}

	public void visit(ArrayDec exp, int level) {
		indent(level);
		System.out.print("ArrayDec: ");
		switch (exp.typ.typ) {
			case NameTy.BOOL:
				System.out.print("bool");
				break;
			case NameTy.INT:
				System.out.print("int");
				break;
			case NameTy.VOID:
				System.out.print("void");
				break;
		}
		System.out.println(" " + exp.name + "[" + exp.size + "]");
	}

	public void visit(NameTy exp, int level) {
		indent(level);
		System.out.print("NameTy: ");
		switch (exp.typ) {
			case NameTy.BOOL:
				System.out.println("BOOL");
				break;
			case NameTy.INT:
				System.out.println("INT");
				break;
			case NameTy.VOID:
				System.out.println("VOID");
				break;
			default:
				System.out.println("Unrecognized operator at position (row: " + exp.row + ", col: " + exp.column + ")");
		}
	}

	public void visit(BoolExp exp, int level) {
		indent(level);
		System.out.println("BoolExp: " + exp.value);
	}

	public void visit(CallExp exp, int level) {
		indent(level);
		System.out.println("CallExp: " + exp.func);
		level++;
		end_block(level);
		exp.args.accept(this, level);
	}

	public void visit(AssignExp exp, int level) {
		indent(level);
		System.out.println("AssignExp:");
		level++;
		start_block(level);
		exp.lhs.accept(this, level);
		end_block(level);
		exp.rhs.accept(this, level);
	}

	public void visit(CompoundExp exp, int level) {
		indent(level);
		System.out.println("CompoundExp:");
		level++;
		start_block(level);
		exp.decs.accept(this, level);
		end_block(level);
		exp.exps.accept(this, level);
	}

	public void visit(DecList exp, int level) {
		if (exp.head == null) {
			indent(level);
			System.out.println("");
			return;
		}
		start_block(level);
		while (exp != null) {
			if (exp.tail == null)
				end_block(level);
			exp.head.accept(this, level);
			exp = exp.tail;
		}
	}

	public void visit(FunctionDec exp, int level) {
		indent(level);
		System.out.println("FunctionDec: " + exp.func);
		level++;
		start_block(level);
		exp.result.accept(this, level);
		exp.params.accept(this, level);
		end_block(level);
		exp.body.accept(this, level);
	}

	public void visit(IndexVar exp, int level) {
		indent(level);
		System.out.println("IndexVar: " + exp.name);
		level++;
		end_block(level);
		exp.index.accept(this, level);
	}

	public void visit(ExpList expList, int level) {
		if (expList.head == null) {
			indent(level);
			System.out.println("");
			return;
		}
		start_block(level);
		while (expList != null) {
			if (expList.tail == null)
				end_block(level);
			expList.head.accept(this, level);
			expList = expList.tail;
		}
	}

	public void visit(IfExp exp, int level) {
		indent(level);
		System.out.println("IfExp:");
		level++;
		start_block(level);
		exp.test.accept(this, level);
		if (exp.elsepart instanceof NilExp)
			end_block(level);
		exp.thenpart.accept(this, level);

		// TODO: never null, maybe NilExp
		if (!(exp.elsepart instanceof NilExp)) {
			end_block(level);
			exp.elsepart.accept(this, level);
		}
	}

	public void visit(IntExp exp, int level) {
		indent(level);
		System.out.println("IntExp: " + exp.value);
	}

	public void visit(NilExp exp, int level) {
		indent(level);
		System.out.println("NilExp");
	}

	public void visit(OpExp exp, int level) {
		indent(level);
		System.out.print("OpExp:");
		switch (exp.op) {
			case OpExp.PLUS:
				System.out.println(" + ");
				break;
			case OpExp.MINUS:
				System.out.println(" - ");
				break;
			case OpExp.TIMES:
				System.out.println(" * ");
				break;
			case OpExp.DIV:
				System.out.println(" / ");
				break;
			case OpExp.EQ:
				System.out.println(" = ");
				break;
			case OpExp.NEQ:
				System.out.println(" != ");
				break;
			case OpExp.LT:
				System.out.println(" < ");
				break;
			case OpExp.LTE:
				System.out.println(" <= ");
				break;
			case OpExp.GT:
				System.out.println(" > ");
				break;
			case OpExp.GTE:
				System.out.println(" >= ");
				break;
			case OpExp.NOT:
				System.out.println(" ~");
				break;
			case OpExp.AND:
				System.out.println(" && ");
				break;
			case OpExp.OR:
				System.out.println(" || ");
				break;
			case OpExp.UMINUS:
				System.out.println(" -");
				break;
			default:
				System.out.println("Unrecognized operator at position (row: " + exp.row + ", col: " + exp.column + ")");
		}
		level++;
		start_block(level);
		if (!(exp.left instanceof NilExp))
			exp.left.accept(this, level);
		end_block(level);
		exp.right.accept(this, level);
	}

	public void visit(ReturnExp exp, int level) {
		indent(level);
		System.out.println("ReturnExp:");
		level++;
		end_block(level);
		exp.exp.accept(this, level);
	}

	public void visit(SimpleDec exp, int level) {
		indent(level);
		System.out.print("SimpleDec: ");
		switch (exp.typ.typ) {
			case NameTy.BOOL:
				System.out.print("bool");
				break;
			case NameTy.INT:
				System.out.print("int");
				break;
			case NameTy.VOID:
				System.out.print("void");
				break;
		}
		System.out.println(" " + exp.name);
	}

	public void visit(SimpleVar exp, int level) {
		indent(level);
		System.out.println("SimpleVar: " + exp.name);
	}

	public void visit(VarDecList exp, int level) {
		if (exp.head == null) {
			indent(level);
			System.out.println("");
			return;
		}
		start_block(level);
		while (exp != null) {
			if (exp.tail == null)
				end_block(level);
			exp.head.accept(this, level);
			exp = exp.tail;
		}
	}

	public void visit(VarExp exp, int level) {
		indent(level);
		System.out.println("VarExp:");
		level++;
		end_block(level);
		exp.variable.accept(this, level);
	}

	public void visit(WhileExp exp, int level) {
		indent(level);
		System.out.println("WhileExp:");
		level++;
		start_block(level);
		exp.test.accept(this, level);
		end_block(level);
		exp.body.accept(this, level);
	}
}
