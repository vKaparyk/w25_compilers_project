import java.util.*;

import absyn.*;

public class SemanticAnalyzer implements AbsynVisitor {

    final static int SPACES = 4;

    private SymbolTable symbolTable = new SymbolTable();
    private String filename;


    public SemanticAnalyzer(String filename) {
        this.filename = filename;
    }

	private void indent(int level) {
		for (int i = 0; i < level * SPACES; i++)
			System.out.print(" ");
	}

    public void visit(ArrayDec exp, int level) {
        Sym s = new Sym(exp.name, exp, level);
        if(!symbolTable.addVariable(s)) {
            System.err.println(filename + ":" + (exp.row + 1) + ":" + (exp.column + 1) + ": error: redeclaration of variable \'" + exp.name + "\'");
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
    }

    public void visit(AssignExp exp, int level) {
        level++;
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

        Sym s = new Sym(exp.func, exp, level);

        if(!symbolTable.addFunction(s)) {
            System.err.println(filename + ":" + (exp.row + 1)  + ":" +(exp.column + 1) + ": error: redeclaration of variable \'" + exp.func + "\'");
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
        level++;
        System.out.println("if block");
        symbolTable.enterScope();
        exp.test.accept(this, level);
        exp.thenpart.accept(this, level);
        
        symbolTable.exitScope();

        if (!(exp.elsepart instanceof NilExp)) {
            System.out.println("else block");
            symbolTable.enterScope();
            exp.elsepart.accept(this, level);
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
        if (!(exp.left instanceof NilExp))
            exp.left.accept(this, level);
        exp.right.accept(this, level);
    }

    public void visit(ReturnExp exp, int level) {
        level++;
        exp.exp.accept(this, level);
    }

    public void visit(SimpleDec exp, int level) {
        Sym s = new Sym(exp.name, exp, level);
        
        if(!symbolTable.addVariable(s)) {
            System.err.println(filename + ":" + (exp.row + 1)  + ":" +(exp.column + 1) + ": error: redeclaration of variable \'" + exp.name + "\'");
        }
    }

    public void visit(SimpleVar exp, int level) { 
        // do nothing
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
    }

    public void visit(WhileExp exp, int level) {
        level++;
        System.out.println("Entering a new while block");
        symbolTable.enterScope();
        exp.test.accept(this, level);
        exp.body.accept(this, level);
        symbolTable.exitScope();
        System.out.println("Exiting the while block");
    }
}
