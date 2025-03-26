import java.io.FileWriter;
import java.io.PrintWriter;

import absyn.*;

public class CodeGenerator implements AbsynVisitor {
	public boolean failedGeneration = false;
	
	private final int pc = 7;
	private final int gp = 6;
	private final int fp = 5;
	private final int ac = 0;
	private final int ac1 = 1;

	private final int IADDR_SIZE = 1024;
	private final int DADDR_SIZE = 1024;
	private final int NO_REGS = 8;
	private final int C_REG = 7;

	private boolean inGlobalScope;
	private String filename;

	int mainEntry; 			// absolute address for main
	int globalOffset;		// next available loc after global frame
	
	int ofpFO;

	int emitLoc = 0;		
    int highEmitLoc = 0;	
    PrintWriter code;

    // Constructor to set up the output writer
    public CodeGenerator(String filename) {
        this.filename = filename;
		try {
			code = new PrintWriter(new FileWriter(filename));
		}
		catch (Exception e) {
			e.printStackTrace();
		}
    }

	public void closeWriter() {
		code.close();
	}

    // Emit Register-Only (RO) instruction
    public void emitRO(String op, int r, int s, int t, String c) {
        code.printf("%3d: %5s %d, %d, %d", emitLoc, op, r, s, t);
        code.printf("\t%s\n", c);
        ++emitLoc;
        if (highEmitLoc < emitLoc) {
            highEmitLoc = emitLoc;
        }
    }

	// Format: opcode r, d(s)
	// r: register
	// s: 
	// a: address
	// d: 

    // Emit Register-Memory (RM) instruction
    public void emitRM(String op, int r, int d, int s, String c) {
        code.printf("%3d: %5s %d, %d(%d)", emitLoc, op, r, d, s);
        code.printf("\t%s\n", c);
        ++emitLoc;
        if (highEmitLoc < emitLoc) {
            highEmitLoc = emitLoc;
        }
    }

    // Emit Register-Memory Absolute (RM_Abs) instruction
    public void emitRM_Abs(String op, int r, int a, String c) {
        code.printf("%3d: %5s %d, %d(%d) ", emitLoc, op, r, a - (emitLoc + 1), pc);
        code.printf("\t%s\n", c);
        ++emitLoc;
        if (highEmitLoc < emitLoc) {
            highEmitLoc = emitLoc;
        }
    }

    // Skip specified distance in emit location
    public int emitSkip(int distance) {
        int i = emitLoc;
        emitLoc += distance;
        if (highEmitLoc < emitLoc) {
            highEmitLoc = emitLoc;
        }
        return i;
    }

    // Backup to a specific location
    public void emitBackup(int loc) {
        if (loc > highEmitLoc) {
            emitComment("BUG in emitBackup");
        }
        emitLoc = loc;
    }

    // Restore emit location to highest emitted location
    public void emitRestore() {
        emitLoc = highEmitLoc;
    }

    // Generate a comment line
    public void emitComment(String c) {
        code.printf("* %s\n", c);
    }

    // Optional: getter methods for emitLoc and highEmitLoc
    public int getEmitLoc() {
        return emitLoc;
    }

    public int getHighEmitLoc() {
        return highEmitLoc;
    }

	public void visit(Absyn trees) {
		// generate the prelude
		emitComment("C-Minus Compilation to TM Code");
		emitComment("File: " + filename);
		emitComment("Standard prelude:");
		
		emitRM("LD", gp, 0, ac, "load gp with maxaddress");
		emitRM("LDA", fp, 0, gp, "copy gp to fp");
		emitRM("ST", ac, 0, ac, "clear location 0");
		
		// generate the i/o routines
		emitComment("Jump around i/o routines here");
		// TODO: i/o routines
		
		// make a request to the visit method for DecList
		trees.accept(this, 0, false);
		
		// generate finale
		emitRM( "ST", fp, globalOffset+ofpFO, fp, "push ofp" );
		emitRM( "LDA", fp, globalOffset, fp, "push frame" );
		emitRM( "LDA", ac, 1, pc, "load ac with ret ptr" );
		emitRM_Abs( "LDA", pc, mainEntry, "jump to main loc" );
		emitRM( "LD", fp, ofpFO, fp, "pop frame" );
		emitRO( "HALT", 0, 0, 0, "" );
	}

	// absyn functions
	public void visit(ArrayDec exp, int offset, boolean isAddress) {
		exp.nestLevel = inGlobalScope ? 0 : 1;
		exp.offset = offset;
	}

	public void visit(NameTy exp, int offset, boolean isAddress) {
		
	}

	public void visit(BoolExp exp, int offset, boolean isAddress) {
		
	}

	public void visit(CallExp exp, int offset, boolean isAddress) {
		exp.args.accept(this, offset, false);
	}

	public void visit(AssignExp exp, int offset, boolean isAddress) {
		exp.lhs.accept(this, offset, false);
		exp.rhs.accept(this, offset, false);
	}

	public void visit(CompoundExp exp, int offset, boolean isAddress) {
		exp.decs.accept(this, offset, false);
		exp.exps.accept(this, offset, false);
	}

	public void visit(DecList exp, int offset, boolean isAddress) {
		if (exp.head == null) {
			return;
		}
		
		inGlobalScope = true;
		while (exp != null) {
			if (!(exp.head instanceof VarDec)) inGlobalScope = false;
			
			exp.head.accept(this, offset, false);
			exp = exp.tail;
			inGlobalScope = true;
		}
	}

	public void visit(FunctionDec exp, int offset, boolean isAddress) {
		exp.typ.accept(this, offset, false);
		exp.params.accept(this, offset, false);
		exp.body.accept(this, offset, false);
	}

	public void visit(IndexVar exp, int offset, boolean isAddress) {		
		exp.index.accept(this, offset, false);
	}

	public void visit(ExpList expList, int offset, boolean isAddress) {
		if (expList.head == null) {
			return;
		}
		while (expList != null) {
			expList.head.accept(this, offset, false);
			expList = expList.tail;
		}
	}

	public void visit(IfExp exp, int offset, boolean isAddress) {

		exp.test.accept(this, offset, false);
		exp.thenpart.accept(this, offset, false);

		if (!(exp.elsepart instanceof NilExp)) {
			exp.elsepart.accept(this, offset, false);
		}
	}

	public void visit(IntExp exp, int offset, boolean isAddress) {
		
	}

	public void visit(NilExp exp, int offset, boolean isAddress) {

	}

	public void visit(OpExp exp, int offset, boolean isAddress) {
		offset++;
		
		if (!(exp.left instanceof NilExp))
			exp.left.accept(this, offset, false);
		
		exp.right.accept(this, offset, false);
	}

	public void visit(ReturnExp exp, int offset, boolean isAddress) {
		offset++;
		
		exp.exp.accept(this, offset, false);
	}

	public void visit(SimpleDec exp, int offset, boolean isAddress) {
		exp.nestLevel = inGlobalScope ? 0 : 1;
		exp.offset = offset;
	}

	public void visit(SimpleVar exp, int offset, boolean isAddress) {
		
	}

	public void visit(VarDecList exp, int offset, boolean isAddress) {
		if (exp.head == null) {
			return;
		}
		
		while (exp != null) {
				
			exp.head.accept(this, offset, false);
			exp = exp.tail;
		}
	}

	public void visit(VarExp exp, int offset, boolean isAddress) {
		exp.variable.accept(this, offset, false);
	}

	public void visit(WhileExp exp, int offset, boolean isAddress) {
		exp.test.accept(this, offset, false);
		exp.body.accept(this, offset, false);
	}
}
