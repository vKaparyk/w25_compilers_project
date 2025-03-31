import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;

import absyn.*;

public class CodeGenerator implements AbsynVisitor {
	public boolean failedGeneration = false;

	private final int pc = 7;
	private final int gp = 6;
	private final int fp = 5;
	private final int ac = 0;
	private final int ac1 = 1;

	private final int retFO = -1;

	enum RO {
		HALT, IN, OUT, ADD, SUB, MUL, DIV
	}

	enum RM {
		LD, LDA, LDC, ST, JLT, JLE, JGT, JGE, JEQ, JNE
	}

	private final int NO_REGS = 8;
	private final int C_REG = 7;

	private boolean inGlobalScope;
	private String filename;

	int mainEntry; // absolute address for main
	int globalOffset = 0; // next available loc after global frame

	int ofpFO = 0; // old frame pointer Frame Offset

	int emitLoc = 0; // "number of instructions"; PC but counting as outputting
	int highEmitLoc = 0; //	for backpatching, TODO: fugure out

	int inputEntry = 0;
	int outputEntry = 0;

	int frameOffset = -2; // bottom of allocated variables in frame; "next empty stop (probably)" after allocated variables; non-temporary
							// points to where temporraies willl start
							// frameOffset: size that the current frame is taking up; ends up pointing to where remporaries will start
							// OR where new frame will be created

	// TODO: possible problem that ocmes up later; we'll see
	PrintWriter code;

	/********************* Workers *********************/

	// Constructor to set up the output writer
	public CodeGenerator(String filename) {
		this.filename = filename;
		try {
			code = new PrintWriter(new FileWriter(filename));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void closeWriter() { code.close(); }

	/**
	 * Emit Register-Only (Register-To-Register) instruction
	 * 
	 * @param op str: {ADD, SUB, MUL, DIV} r,s,t;
	 * @param r  int: src register number
	 * @param s  int: LHS operand register number
	 * @param t  int: RHS operand register number
	 * @param c  str: comment
	 */
	public void emitRO(RO op, int r, int s, int t, String c) {
		code.printf("%3d: ", emitLoc);
		switch (op) {
		case HALT:
			code.printf("%5s ", "HALT");
			break;
		case IN:
			code.printf("%5s ", "IN");
			break;
		case OUT:
			code.printf("%5s ", "OUT");
			break;
		case ADD:
			code.printf("%5s ", "ADD");
			break;
		case SUB:
			code.printf("%5s ", "SUB");
			break;
		case MUL:
			code.printf("%5s ", "MUL");
			break;
		case DIV:
			code.printf("%5s ", "DIV");
			break;
		default:
			break;
		}
		code.printf("%d,%d,%d", r, s, t);
		code.printf("\t%s\n", c);
		++emitLoc;
		if (highEmitLoc < emitLoc) {
			highEmitLoc = emitLoc;
		}
	}

	/**
	 * Emit Register-Only (Register-To-Register) instruction
	 * 
	 * @param op str: {IN, OUT}
	 * @param r  int: src register number
	 * @param c  str: comment
	 */
	public void emitRO(RO op, int r, String c) { emitRO(op, r, 0, 0, c); }

	/**
	 * Emit Register-Only (Register-To-Register) instruction
	 * 
	 * @param op str: HALT
	 * @param c  str: comment
	 */
	public void emitRO(RO op, String c) { emitRO(op, 0, 0, 0, c); }

	/**
	 * Emit Register-Memorty (RM) instruction (a = d + reg[s])
	 * 
	 * @param op str: {LD, LDA, LDC, ST, JLT, JLE, JGT, JGE, JEQ, JNE}
	 * @param r  int: src register number
	 * @param d  int: memory offset
	 * @param s  int: register (holding value) for offset
	 * @param c  str: comment
	 */
	public void emitRM(RM op, int r, int d, int s, String c) {
		code.printf("%3d: ", emitLoc);
		switch (op) {
		case LD:
			code.printf("%5s ", "LD");
			break;
		case LDA:
			code.printf("%5s ", "LDA");
			break;
		case LDC:
			code.printf("%5s ", "LDC");
			break;
		case ST:
			code.printf("%5s ", "ST");
			break;
		case JLT:
			code.printf("%5s ", "JLT");
			break;
		case JLE:
			code.printf("%5s ", "JLE");
			break;
		case JGT:
			code.printf("%5s ", "JGT");
			break;
		case JGE:
			code.printf("%5s ", "JGE");
			break;
		case JEQ:
			code.printf("%5s ", "JEQ");
			break;
		case JNE:
			code.printf("%5s ", "JNE");
			break;
		default:
			break;
		}
		code.printf("%d,%d(%d)", r, d, s);
		code.printf("\t%s\n", c);
		++emitLoc;
		if (highEmitLoc < emitLoc) {
			highEmitLoc = emitLoc;
		}
	}

	public void emitRM_Abs(RM op, int r, int a, String c) {
		// TODO: is it actually that offset, and not just a?
		emitRM(op, r, a - (emitLoc + 1), pc, c);
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
	public void emitRestore() { emitLoc = highEmitLoc; }

	// Generate a comment line
	public void emitComment(String c) { code.printf("* %s\n", c); }

	// Optional: getter methods for emitLoc and highEmitLoc
	public int getEmitLoc() { return emitLoc; }

	public int getHighEmitLoc() { return highEmitLoc; }

	public void visit(Absyn trees) {
		// generate the prelude
		emitComment("C-Minus Compilation to TM Code");
		emitComment("File: " + filename);
		emitComment("Standard prelude:");

		emitRM(RM.LD, gp, 0, ac, "load gp with maxaddress");
		emitRM(RM.LDA, fp, 0, gp, "copy gp to fp");
		emitRM(RM.ST, ac, 0, ac, "clear location 0");
		int skip = emitSkip(1);

		// generate the i/o routines
		emitComment("Jump around i/o routines here");
		// input
		emitComment("code for input routine");
		inputEntry = getEmitLoc();
		emitRM(RM.ST, ac, retFO, fp, "store return");
		emitRO(RO.IN, ac, "input");
		emitRM(RM.LD, pc, retFO, fp, "return to caller");
		// input
		emitComment("code for output routine");
		emitRM(RM.ST, ac, retFO, fp, "store return");
		emitRM(RM.LD, ac, -2, fp, "load output value");
		emitRO(RO.OUT, ac, "output");
		emitRM(RM.LD, pc, retFO, fp, "return to caller");
		emitBackup(skip);
		emitRM_Abs(RM.LDA, pc, getHighEmitLoc(), "jump around i/o code");
		emitRestore();
		emitComment("End of standard prelude.");

		// make a request to the visit method for DecList
		trees.accept(this, 0, false);

		// generate finale
		emitRM(RM.ST, fp, globalOffset + ofpFO, fp, "push ofp");
		emitRM(RM.LDA, fp, globalOffset, fp, "push frame");
		emitRM(RM.LDA, ac, 1, pc, "load ac with ret ptr");
		emitRM_Abs(RM.LDA, pc, mainEntry, "jump to main loc");
		emitRM(RM.LD, fp, ofpFO, fp, "pop frame");
		emitRO(RO.HALT, "end program");
	}

	/**************** Absyn Visitor **********************/

	// absyn functions
	public void visit(ArrayDec exp, int offset, boolean isAddress) {
		exp.nestLevel = inGlobalScope ? 0 : 1;
		exp.offset = offset;

		// 
		emitRM(RM.ST, ac1, -1 + offset + (exp.size * -1), fp, "store ac1 contents into temp");
		emitRM(RM.LDC, ac1, exp.size, 0, "get array size into ac1");
		emitRM(RM.ST, ac1, offset - exp.size, fp, "store aray size into stack");
		emitRM(RM.LD, ac1, -1 + offset + (exp.size * -1), fp, "load contents of ac1 from temp back");
	}

	public void visit(AssignExp exp, int offset, boolean isAddress) {
		exp.lhs.accept(this, offset, true);
		exp.rhs.accept(this, offset, false);
	}

	public void visit(BoolExp exp, int offset, boolean isAddress) {

	}

	public void visit(CallExp exp, int offset, boolean isAddress) {
		int curr_offset = offset - 2;

		ArrayList<Exp> all_args = exp.args.createIterable();
		for (Exp arg : all_args) {
			arg.accept(this, curr_offset, isAddress);
			emitRM(RM.ST, ac, curr_offset, fp, "store arg" + arg.toString() + "to stack");
			// TODO: pass in array address, instead of acxtqaul array contents
			curr_offset -= 1;
		}
		emitRM(RM.ST, fp, offset, fp, "store old FP");
		emitRM(RM.LDA, fp, offset, fp, "push new FP");
		emitRM(RM.LDA, ac, 1, pc, "save ret addr into AC");

		int dest_func = -1;
		if (exp.func == "input")
			dest_func = inputEntry;
		else if (exp.func == "output")
			dest_func = outputEntry;
		else
			dest_func = exp.def.funaddr;
		// TODO: indirect recursion logixc ghere, what the fuck
		emitRM_Abs(RM.LDA, pc, dest_func, "jump to function call");
		emitRM(RM.LD, fp, 0, fp, "load old FP");
		if (!exp.def.isVoid())
			emitRM(RM.ST, ac, offset, fp, "store return value into stack offset");
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
			if (!(exp.head instanceof VarDec))
				inGlobalScope = false;

			exp.head.accept(this, globalOffset, false);
			if (exp.head instanceof VarDec) {
				if (exp.head instanceof SimpleDec)
					globalOffset += 1;
				else
					globalOffset += (((ArrayDec) (exp.head)).size + 1);
			}

			exp = exp.tail;
			inGlobalScope = true;
		}
	}

	public void visit(FunctionDec exp, int offset, boolean isAddress) {
		// TODO: deal with prototypes

		// if main, set main entry accordingly
		int tempFO = frameOffset;
		frameOffset = -2;
		int saveLoc = emitSkip(1);

		if (exp.name == "main" && !(exp.body instanceof NilExp))
			mainEntry = getHighEmitLoc();
		exp.funaddr = getHighEmitLoc();

		emitRM(RM.ST, ac, retFO, fp, "store rturn address into ret");
		exp.typ.accept(this, frameOffset, false);
		exp.params.accept(this, frameOffset, false);

		exp.body.accept(this, frameOffset, false);
		frameOffset = tempFO;
		emitRM(RM.LD, pc, retFO, fp, "load return addr into PC");

		// backpatch a jump for the defintion
		emitBackup(saveLoc);
		emitRM_Abs(RM.LD, pc, getHighEmitLoc(), "skip function execution");
		emitRestore();
	}

	public void visit(IndexVar exp, int offset, boolean isAddress) {
		// TODO: offset
		// TODO: nestLevel
		// TODO: implementation details: 11w:16
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

	public void visit(NameTy exp, int offset, boolean isAddress) {

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
		// emitRM(RM.LDC, ac1, offset + 17, "");
		// emitRM(RM.ST, ac1, offset, fp, "store const for testing");
	}

	public void visit(SimpleVar exp, int offset, boolean isAddress) {
		// TODO: implemetntion 11w:42
		// TODO: offset
		// TODO: nestLevel(?)
	}

	public void visit(VarDecList exp, int offset, boolean isAddress) {
		if (exp.head == null) {
			return;
		}

		while (exp != null) {
			exp.head.accept(this, offset, false);
			if (exp.head instanceof SimpleDec) {
				offset -= 1;
				frameOffset -= 1;
			}
			// TODO: arraydec
			exp = exp.tail;
		}
	}

	public void visit(VarExp exp, int offset, boolean isAddress) {
		// brug
		exp.variable.accept(this, offset, false);
	}

	public void visit(WhileExp exp, int offset, boolean isAddress) {
		exp.test.accept(this, offset, false);
		exp.body.accept(this, offset, false);
	}
}
