import absyn.*;

public class SemanticAnalyzer implements AbsynVisitor {

	final static int SPACES = 4;

	// TODO: cross reference C1 error handling and how it'd do here

	private SymbolTable symbolTable = new SymbolTable();
	private String filename;

	public SemanticAnalyzer(String filename) { this.filename = filename; }

	// TODO: pretty printing
	private void indent(int level) {
		for (int i = 0; i < level * SPACES; i++)
			System.out.print(" ");
	}

	public void visit(ArrayDec exp, int level) {
		// TODO: type checking
		// if type is void, le bad
		Sym s = new Sym(exp.name, exp, level);
		if (!symbolTable.addVariable(s)) {
			report_error("redeclaration of variable \'" + exp.name + "\'", exp);
		}
	}

	public void visit(NameTy exp, int level) {
		// do nothing
	}

	public void visit(BoolExp exp, int level) {
		// do nothing
	}

	public void visit(CallExp exp, int level) {
		level++;
		exp.args.accept(this, level);
		// TODO: type checking
		// make sure arg types match to function def
		// TODO: type checking
		// set Dec dtype
	}

	public void visit(AssignExp exp, int level) {
		level++;
		// TODO: type checking
		// verify lhs can accept rhs
		exp.lhs.accept(this, level);
		exp.rhs.accept(this, level);
	}

	public void visit(CompoundExp exp, int level) {
		level++;
		exp.decs.accept(this, level);
		exp.exps.accept(this, level);
	}

	public void visit(DecList exp, int level) {
		System.out.println("Entering the global scope:");
		symbolTable.enterScope();
		level++;
		if (exp.head == null) {
			return;
		}
		while (exp != null) {
			exp.head.accept(this, level);
			exp = exp.tail;
		}
		symbolTable.printTopScope(level);
		symbolTable.exitScope();
		System.out.println("Leaving the global scope ");
	}

	public void visit(FunctionDec exp, int level) {
		// TODO: ask: if FunctionDec has return, do we need to verify it has return statement
		Sym s = new Sym(exp.func, exp, level);

		if (!symbolTable.addFunction(s)) {
			report_error("redeclaration of function \'" + exp.func + "\'", exp);
		}

		if (!(exp.body instanceof NilExp)) { // only do this stuff if not function prototype
			indent(level);
			System.out.println("Entering the scope for function " + exp.func + ":");

			symbolTable.addFunction(s);
			symbolTable.enterScope();

			level++;
			exp.result.accept(this, level);
			exp.params.accept(this, level);
			exp.body.accept(this, level);

			symbolTable.printTopScope(level);
			symbolTable.exitScope();
			indent(--level);
			System.out.println("Leaving the function scope");
		}
	}

	public void visit(IndexVar exp, int level) {
		level++;
		exp.index.accept(this, level);
		// TODO: type checking
		// validate exp evaluates to int
		// TODO: type checking
		// if dec == null, set it accordingly
	}

	public void visit(ExpList expList, int level) {
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
			expList.head.accept(this, level);

			if (expList.head instanceof CompoundExp) {
				symbolTable.printTopScope(level);
				symbolTable.exitScope();
				indent(--level);
				System.out.println("Leaving the block");
			}
			expList = expList.tail;
		}
	}

	public void visit(IfExp exp, int level) {
		indent(level);
		System.out.println("Entering a new if block");
		level++;
		symbolTable.enterScope();
		exp.test.accept(this, level);
		// TODO: type checking
		// check if test exp evaluates to bool
		exp.thenpart.accept(this, level);

		symbolTable.printTopScope(level);
		symbolTable.exitScope();
		indent(--level);
		System.out.println("Exiting the if block");

		if (!(exp.elsepart instanceof NilExp)) {
			indent(level);
			System.out.println("Entering a new else block");
			level++;
			symbolTable.enterScope();

			exp.elsepart.accept(this, level);

			symbolTable.printTopScope(level);
			symbolTable.exitScope();
			indent(--level);
			System.out.println("Exiting the else block");
		}
	}

	public void visit(IntExp exp, int level) {
		// do nothing
	}

	public void visit(NilExp exp, int level) {
		// do nothing
	}

	public void visit(OpExp exp, int level) {
		level++;
		// TODO: cehck if some custom rule applies to bools (if int is 0, it can implicitly convvert to 'bool false')
		if (!(exp.left instanceof NilExp))
			exp.left.accept(this, level);
		exp.right.accept(this, level);
		// TODO: type checking
		// verify both lhs and rhs are of the OpExp dtype
		// TODO: consider UMINUS and NOT (they don't have lhs)
	}

	public void visit(ReturnExp exp, int level) {
		level++;
		// TODO: type checking
		// check that return datatype matches function return type
		// TODO: type checking
		// return from global not allowed

		// TODO: what the fuck
		// must get function name, cross referecne function name with symbol table, get it's return type, and compare to exp return type

		// implementation: when visiting FunctionDec's body, store functionname as class-wide variable called "current context"
		// refer t othat via symbol table when type checking

		exp.exp.accept(this, level);
	}

	public void visit(SimpleDec exp, int level) {
		// TODO: type checking
		// if type is void, le bad
		Sym s = new Sym(exp.name, exp, level);

		if (!symbolTable.addVariable(s)) {
			report_error("redeclaration of variable \'" + exp.name + "\'", exp);
		}
	}

	public void visit(SimpleVar exp, int level) {
		// do nothing
		// TODO: type checking
		// set it's Dec by cross-referencing symbol table
	}

	public void visit(VarDecList exp, int level) {
		if (exp.head == null) {
			return;
		}
		while (exp != null) {
			exp.head.accept(this, level);
			exp = exp.tail;
		}
	}

	public void visit(VarExp exp, int level) {
		level++;
		exp.variable.accept(this, level);
		// TODO: ***AFTER*** visit, set Dec
	}

	public void visit(WhileExp exp, int level) {
		indent(level);
		level++;
		System.out.println("Entering a new while block");
		symbolTable.enterScope();

		exp.test.accept(this, level);
		// TODO: type checking
		// chekc if test exp is of Bool type
		exp.body.accept(this, level);

		symbolTable.printTopScope(level);
		symbolTable.exitScope();
		indent(--level);
		System.out.println("Exiting the while block");
	}

	// Helpers

	public void report_error(String erorr, Absyn obj) {
		System.err.println(filename + ":" + (obj.row + 1) + ":" + (obj.column + 1) + ": error: " + erorr);
	}

}
