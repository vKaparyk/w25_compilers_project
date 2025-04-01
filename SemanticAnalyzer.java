import absyn.*;

public class SemanticAnalyzer implements AbsynVisitor {

	final static int SPACES = 4;

	private String current_function = "global";
	public boolean invalid_symbol_tabling = false;
	public boolean return_exists = false;

	private SymbolTable symbolTable = new SymbolTable();
	private String filename;

	public SemanticAnalyzer(String filename) {
		this.filename = filename;

		// when class is created, automatically add input and output
		symbolTable.addFunction(new Sym("input", new FunctionDec(-1, -1, new NameTy(-1, -1, NameTy.INT), "input",
				new VarDecList(null, null), new NilExp(-1, -1)), 0));
		symbolTable.addFunction(new Sym("output",
				new FunctionDec(-1, -1, new NameTy(-1, -1, NameTy.VOID), "output",
						new VarDecList(new SimpleDec(-1, -1, new NameTy(-1, -1, NameTy.INT), "char_output"), null),
						new NilExp(-1, -1)),
				0));
	}

	private void indent(int level) {
		for (int i = 0; i < level * SPACES; i++)
			System.out.print(" ");
	}

	// absyn

	public void visit(ArrayDec exp, int level, boolean flag) {
		if (exp.typ.typ == NameTy.VOID) {
			report_error("cannot declare void variable", exp);
			exp.typ.typ = NameTy.INT;
		}

		Sym s = new Sym(exp.name, exp, level);
		if (!symbolTable.addVariable(s)) {
			report_error("redeclaration of variable \'" + exp.name + "\'", exp);
		}
	}

	public void visit(AssignExp exp, int level, boolean flag) {
		level++;
		exp.lhs.accept(this, level, false);
		exp.rhs.accept(this, level, false);
		exp.dtype = exp.lhs.dtype;

		boolean isArrayFound = false;
		if (exp.lhs.dtype.isArray()) {
			isArrayFound = true;
			report_type_error(exp.lhs, exp);
		}
		if (exp.rhs.dtype.isArray()) {
			isArrayFound = true;
			report_type_error(exp.rhs, exp);
		}

		if (isArrayFound)
			return;

		if (isBool(exp.lhs.dtype) && isInt(exp.rhs.dtype))
			return;

		if (!same_exp_type(exp.lhs, exp.rhs))
			report_type_error(exp.rhs, exp);
	}

	public void visit(BoolExp exp, int level, boolean flag) {
		// do nothing
	}

	public void visit(CallExp exp, int level, boolean flag) {
		level++;
		exp.args.accept(this, level, false);
		Sym func_dec = symbolTable.lookupFunction(exp.func);

		if (func_dec == null) {
			report_error("function undefined", exp);
			exp.dtype = new FunctionDec(exp.row, exp.column, new NameTy(exp.row, exp.column, NameTy.VOID), "le bad",
					new VarDecList(null, null), new NilExp(exp.row, exp.column));
			return;
		}

		if (exp.dtype == null) {
			exp.dtype = new SimpleDec(func_dec.def.row, func_dec.def.column, func_dec.def.typ,
					func_dec.def.typ.toString());
		}

		if (!call_args_valid(exp.args, ((FunctionDec) func_dec.def).params))
			report_type_error(exp, (FunctionDec) func_dec.def);

		// if (func_dec.name.equals("input") || func_dec.name.equals("output"))
		// report_error(func_dec.name + "() call not allowed", exp);

		exp.def = (FunctionDec) func_dec.def;
	}

	public void visit(CompoundExp exp, int level, boolean flag) {
		level++;
		exp.decs.accept(this, level, false);
		exp.exps.accept(this, level, false);
	}

	public void visit(DecList exp, int level, boolean flag) {
		System.out.println("Entering the global scope:");
		symbolTable.enterScope();
		level++;
		if (exp.head == null) {
			return;
		}
		while (exp != null) {
			exp.head.accept(this, level, false);
			exp = exp.tail;
		}
		symbolTable.printTopScope(level);
		symbolTable.exitScope();
		System.out.println("Leaving the global scope ");
	}

	public void visit(ExpList expList, int level, boolean flag) {
		if (expList.head == null) {
			return;
		}
		while (expList != null) {
			if (expList.head instanceof CompoundExp) {
				indent(level);
				System.out.println("Entering a new block:");
				symbolTable.enterScope();
				level++;
			}
			expList.head.accept(this, level, false);

			if (expList.head instanceof CompoundExp) {
				symbolTable.printTopScope(level);
				symbolTable.exitScope();
				indent(--level);
				System.out.println("Leaving the block");
			}
			expList = expList.tail;
		}
	}

	public void visit(FunctionDec exp, int level, boolean flag) {
		Sym s = new Sym(exp.name, exp, level);

		if (!symbolTable.addFunction(s)) {
			report_error("redeclaration of function \'" + exp.name + "\'", exp);
		}

		if (!(exp.body instanceof NilExp)) { // only do this stuff if not function prototype
			indent(level);
			System.out.println("Entering the scope for function " + exp.name + ":");

			symbolTable.addFunction(s);
			symbolTable.enterScope();

			current_function = exp.name;

			level++;
			exp.typ.accept(this, level, false);
			exp.params.accept(this, level, false);
			exp.body.accept(this, level, false);

			if (!isVoid(exp) && !return_exists)
				report_error(
						"function " + exp.name + "() returns " + exp.typ.toString() + "; but no return statement found",
						exp);

			return_exists = false;
			symbolTable.printTopScope(level);
			symbolTable.exitScope();
			indent(--level);
			System.out.println("Leaving the function scope");
		}
	}

	public void visit(IfExp exp, int level, boolean flag) {
		indent(level);
		System.out.println("Entering a new if block");
		level++;
		symbolTable.enterScope();
		exp.test.accept(this, level, false);
		if (isVoid(exp.test.dtype) || exp.test.dtype.isArray()) {
			report_type_error(exp.test, exp);
			exp.test = new BoolExp(exp.test.row, exp.test.column, true);
		}
		exp.thenpart.accept(this, level, false);

		symbolTable.printTopScope(level);
		symbolTable.exitScope();
		indent(--level);
		System.out.println("Exiting the if block");

		if (!(exp.elsepart instanceof NilExp)) {
			indent(level);
			System.out.println("Entering a new else block");
			level++;
			symbolTable.enterScope();

			exp.elsepart.accept(this, level, false);

			symbolTable.printTopScope(level);
			symbolTable.exitScope();
			indent(--level);
			System.out.println("Exiting the else block");
		}
	}

	public void visit(IndexVar exp, int level, boolean flag) {
		level++;
		exp.index.accept(this, level, false);
		exp.def = (ArrayDec) symbolTable.lookupVariable(exp.name).def;
		if (!isInt(exp.index.dtype) || exp.index.dtype.isArray())
			report_type_error(exp.index, exp);
	}

	public void visit(IntExp exp, int level, boolean flag) {
		// do nothing
	}

	public void visit(NameTy exp, int level, boolean flag) {
		// do nothing
	}

	public void visit(NilExp exp, int level, boolean flag) {
		// do nothing
	}

	public void visit(OpExp exp, int level, boolean flag) {
		level++;
		// trat ints as bool; same as C

		exp.left.accept(this, level, false);
		exp.right.accept(this, level, false);
		boolean isArrayFound = false;
		if (exp.left.dtype.isArray()) {
			isArrayFound = true;
			report_type_error(exp.left, exp);
		}

		if (exp.right.dtype.isArray()) {
			isArrayFound = true;
			report_type_error(exp.right, exp);
		}

		if (isArrayFound)
			return;

		switch (exp.op) {
		case OpExp.UMINUS:
			if (!isInt(exp.right.dtype))
				report_type_error(exp.right, exp);
			break;
		case OpExp.NOT:
			if (isVoid(exp.right.dtype))
				report_type_error(exp.right, exp);
			break;
		case OpExp.PLUS:
		case OpExp.MINUS:
		case OpExp.TIMES:
		case OpExp.DIV:
		case OpExp.LT:
		case OpExp.LTE:
		case OpExp.GT:
		case OpExp.GTE:
		case OpExp.EQ:
		case OpExp.NEQ:
			if (!isInt(exp.left.dtype)) {
				report_type_error(exp.left, exp);
				exp.left.dtype = new SimpleDec(exp.left.row, exp.left.column,
						new NameTy(exp.left.row, exp.left.column, NameTy.INT), "int");
			}
			if (!isInt(exp.right.dtype)) {
				report_type_error(exp.right, exp);
				exp.right.dtype = new SimpleDec(exp.right.row, exp.right.column,
						new NameTy(exp.right.row, exp.right.column, NameTy.INT), "int");
			}
			break;
		case OpExp.AND:
		case OpExp.OR:
			if (isVoid(exp.left.dtype)) {
				report_type_error(exp.left, exp);
				exp.left.dtype = new SimpleDec(exp.left.row, exp.left.column,
						new NameTy(exp.left.row, exp.left.column, NameTy.BOOL), "bool");
			}
			if (isVoid(exp.right.dtype)) {
				report_type_error(exp.right, exp);
				exp.right.dtype = new SimpleDec(exp.right.row, exp.right.column,
						new NameTy(exp.right.row, exp.right.column, NameTy.BOOL), "bool");
			}
			break;
		default:
			break;
		}
	}

	public void visit(ReturnExp exp, int level, boolean flag) {
		level++;
		exp.exp.accept(this, level, false);
		exp.dtype = exp.exp.dtype;
		return_exists = true;

		Sym currentSym = symbolTable.lookupFunction(current_function);
		FunctionDec func_ret_type = (FunctionDec) currentSym.getDef();
		Dec ret_type = exp.exp.dtype;

		if (func_ret_type.isVoid() && !isVoid(exp.dtype)) {
			report_warning("function " + func_ret_type.name + "() returns void; but returning " + exp.exp.decString(),
					exp);
		} else if (ret_type.isArray()) {
			report_type_error(exp, func_ret_type);
			exp.dtype = new SimpleDec(exp.row, exp.column, func_ret_type.typ, func_ret_type.typ.toString());
		} else if (func_ret_type.typ.typ == NameTy.BOOL && ret_type.typ.typ == NameTy.VOID) {
			// bool func returns non-bool and non-int
			report_type_error(exp, func_ret_type);
			exp.dtype = new SimpleDec(exp.row, exp.column, func_ret_type.typ, func_ret_type.typ.toString());
		} else if (func_ret_type.typ.typ != ret_type.typ.typ) {
			report_type_error(exp, func_ret_type);
			exp.dtype = new SimpleDec(exp.row, exp.column, func_ret_type.typ, func_ret_type.typ.toString());
		}
	}

	public void visit(SimpleDec exp, int level, boolean flag) {
		if (exp.typ.typ == NameTy.VOID) {
			report_error("cannot declare void variable", exp);
			exp.typ.typ = NameTy.INT;
		}

		Sym s = new Sym(exp.name, exp, level);
		if (!symbolTable.addVariable(s)) {
			report_error("redeclaration of variable \'" + exp.name + "\'", exp);
		}
	}

	public void visit(SimpleVar exp, int level, boolean flag) {

	}

	public void visit(VarDecList exp, int level, boolean flag) {
		if (exp.head == null) {
			return;
		}
		while (exp != null) {
			exp.head.accept(this, level, false);
			exp = exp.tail;
		}
	}

	public void visit(VarExp exp, int level, boolean flag) {
		level++;
		Sym s = symbolTable.lookupVariable(exp.variable.name);
		if (s == null) {
			report_error(exp.variable.name + " not defined", exp);
			exp.dtype = new SimpleDec(exp.row, exp.column, new NameTy(exp.row, exp.column, NameTy.VOID), "undefined");
			return;
		}
		exp.variable.accept(this, level, false);
		Dec dtype = s.getDef();
		if (exp.variable instanceof IndexVar)
			exp.dtype = new SimpleDec(dtype.row, dtype.column, dtype.typ, dtype.name);
		else
			exp.dtype = dtype;

		exp.variable.def = (VarDec) symbolTable.lookupVariable(exp.variable.name).def;
	}

	public void visit(WhileExp exp, int level, boolean flag) {
		indent(level);
		level++;
		System.out.println("Entering a new while block");
		symbolTable.enterScope();

		exp.test.accept(this, level, false);
		if (isVoid(exp.test.dtype) || exp.test.dtype.isArray()) {
			report_type_error(exp.test, exp);
			exp.test = new BoolExp(exp.test.row, exp.test.column, true);
		}
		exp.body.accept(this, level, false);

		symbolTable.printTopScope(level);
		symbolTable.exitScope();
		indent(--level);
		System.out.println("Exiting the while block");
	}

	// Helpers
	// defaults to error
	public void report_error(String erorr, Absyn obj) {
		report_error(erorr, obj, 1);
	}

	public void report_warning(String erorr, Absyn obj) {
		report_error(erorr, obj, 2);
	}

	// report error w/ level
	public void report_error(String erorr, Absyn obj, int level) {
		invalid_symbol_tabling = true;
		System.err.println(filename + ":" + (obj.row + 1) + ":" + (obj.column + 1) + ":"
				+ ((level == 1) ? "error" : "warning") + ":\"" + obj.toString() + "\": " + erorr);
	}

	public void report_type_error(CallExp violator, FunctionDec src) {
		report_error("function call \'" + violator.toString() + "\'' do not match expected \'" + src.toString() + "\'",
				violator);
	}

	public void report_type_error(Exp violator, NameTy src) {
		report_error("\'" + violator.toString() + "\' evaluates to " + violator.decString() + "; " + src.toString()
				+ " expected", violator);
	}

	public void report_type_error(ReturnExp violator, FunctionDec src) {
		report_error("\'" + violator.toString() + "\' returns " + violator.decString() + "; \'" + src.toString(true)
				+ "\' expects " + src.typ.toString(), violator);
	}

	public void report_type_error(Exp violator, IndexVar src) {
		report_error("\'" + violator.toString() + "\' evaluates to " + violator.decString() + "; int expected", src);

	}

	public void report_type_error(Exp violator, Exp src) {
		if (violator.dtype.isArray())
			report_error("\'" + violator.toString() + "\' evaluates to " + violator.decString() + "; operations with "
					+ violator.decString() + " not allowed", src);
		else
			report_error("\'" + violator.toString() + "\' evaluates to " + violator.decString() + "; " + src.decString()
					+ " expected", src);
	}

	public boolean call_args_valid(ExpList call_args, VarDecList def_list) {
		ExpList temp = call_args;
		VarDecList other_temp = def_list;

		while (temp != null || other_temp != null) {
			if (temp == null || other_temp == null)
				return false;
			if (temp.head == null && other_temp.head == null)
				return true;
			if (temp.head == null && other_temp.head != null)
				return false;
			if (temp.head != null && other_temp.head == null)
				return false;

			if (!((VarDec) ((Exp) temp.head).dtype).equals(other_temp.head))
				return false;

			temp = temp.tail;
			other_temp = other_temp.tail;
		}
		return true;
	}

	// Type helpers
	public boolean same_exp_type(Exp lhs, Exp rhs) {
		return lhs.dtype.typ.typ == rhs.dtype.typ.typ && lhs.dtype.isArray() == rhs.dtype.isArray();
	}

	public boolean isVoid(Dec dtype) {
		return dtype.isVoid();
	}

	public boolean isBool(Dec dtype) {
		return dtype.isBool();
	}

	public boolean isInt(Dec dtype) {
		return dtype.isInt();
	}

	public void checkForMain() {
		// TODO: check all functions have implementation

		if (symbolTable.lookupFunction("main") == null) {
			invalid_symbol_tabling = true;
			System.err.println(filename + ":1:1:error: main() not defined");
		} else if (((FunctionDec) symbolTable.lookupFunction("main").def).body instanceof NilExp) {
			invalid_symbol_tabling = true;
			System.err.println(filename + ":error: main() prototype defined, but not implemented");

		} else if (!current_function.equals("main")) {
			invalid_symbol_tabling = true;
			System.err.println(filename + ":error: main() defined, but is not at the end of the file");
		}
	}
}
