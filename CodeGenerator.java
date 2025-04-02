import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;

import absyn.*;

public class CodeGenerator implements AbsynVisitor {
	public boolean failedGeneration = false;

	// @formatter:off
	private final int pc = 7; 		// program counter, stores address of the next instruction to be executed, automatically incremented after each instruction
	private final int gp = 6;		// global pointer, points to the start of the global data/static memory area
	private final int fp = 5;		// frame pointer, points to the current stack frame
	private final int ac = 0;  		// accumulator (primary), register for arithmetic/logical operations
	private final int ac1 = 1; 		// accumulator (secondary), same as ac, might be used when two operands are needed

	private final int ofpFO = 0; 	// old frame pointer Frame Offset
	private final int retFO = -1;	// return address Frame Offset
	private final int initFO = -2; 	// initial frame offset, points to the first local variable in the current stack frame
	// @formatter:on

	enum RO {
		HALT, IN, OUT, ADD, SUB, MUL, DIV
	}

	enum RM {
		LD, LDA, LDC, ST, JLT, JLE, JGT, JGE, JEQ, JNE
	}

	private boolean inGlobalScope;
	private String filename;

	int mainEntry; // absolute address for main
	int globalOffset = 0; // next available loc after global frame

	int emitLoc = 0; // "number of instructions"; PC but counting as outputting
	int highEmitLoc = 0; // for backpatching

	int inputEntry = 0;
	int outputEntry = 0;

	int frameOffset = initFO; // bottom of allocated variables in frame; "next empty stop (probably)" after
								// allocated variables; non-temporary
								// points to where temporraies willl start
								// frameOffset: size that the current frame is taking up; ends up pointing to
								// where remporaries will start
								// OR where new frame will be created
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

	public void closeWriter() {
		code.close();
	}

	/**
	 * Emit Register-Only (Register-To-Register) instruction
	 * 
	 * @param op str: {HALT}; {IN, OUT} r; {ADD, SUB, MUL, DIV} r,s,t;
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
	 * Emit Register-Memorty (RM) instruction (a = d + reg[s])
	 * 
	 * @param op str: {LD, LDA, LDC, ST, JLT, JLE, JGT, JGE, JEQ, JNE}
	 * @param r  int: src register number
	 * @param d  int: displacement / memory offset
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
		emitRM(RM.LD, gp, 0, ac, "load gp with maxaddress");
		emitRM(RM.LDA, fp, 0, gp, "copy gp to fp");
		emitRM(RM.ST, ac, 0, ac, "clear location 0");

		// generate the i/o routines
		emitComment("Jump around i/o routines here");
		int skip = emitSkip(1);
		// input
		emitComment("code for input routine");
		inputEntry = getHighEmitLoc();
		emitRM(RM.ST, ac, retFO, fp, "store return");
		emitRO(RO.IN, ac, 0, 0, "input");
		emitRM(RM.LD, pc, retFO, fp, "return to caller");
		// output
		emitComment("code for output routine");
		outputEntry = getHighEmitLoc();
		emitRM(RM.ST, ac, retFO, fp, "store return");
		emitRM(RM.LD, ac, -2, fp, "load output value");
		emitRO(RO.OUT, ac, 0, 0, "output");
		emitRM(RM.LD, pc, retFO, fp, "return to caller");
		emitBackup(skip);
		emitRM_Abs(RM.LDA, pc, getHighEmitLoc(), "jump around i/o code");
		emitRestore();
		emitComment("End of standard prelude.");

		// make a request to the visit method for DecList
		trees.accept(this, 0, false);

		// generate finale
		emitComment("Finale");
		emitRM(RM.ST, fp, globalOffset + ofpFO, fp, "push ofp");
		emitRM(RM.LDA, fp, globalOffset, fp, "push frame");
		emitRM(RM.LDA, ac, 1, pc, "load ac with ret ptr");
		emitRM_Abs(RM.LDA, pc, mainEntry, "jump to main loc");
		emitRM(RM.LD, fp, ofpFO, fp, "pop frame");
		emitRO(RO.HALT, 0, 0, 0, "end program");
	}

	/**************** Absyn Visitor **********************/

	// absyn functions
	public void visit(ArrayDec exp, int offset, boolean isAddress) {
		exp.nestLevel = inGlobalScope ? 0 : 1;
		exp.offset = (offset - exp.size + ((exp.size == 0) ? 0 : 1));
		if (exp.size != 0) { // is defined normally
			emitRM(RM.LDC, ac, exp.size, 0, "get array size into ac1");
			emitRM(RM.ST, ac, offset - exp.size, fp, "store aray size into stack");
		}
	}

	public void visit(AssignExp exp, int offset, boolean isAddress) {
		emitComment("-> Assign");
		int lhsAddressOffset = offset - 1;
		exp.lhs.accept(this, lhsAddressOffset, true);

		int rhsValueOffset = offset - 2;
		exp.rhs.accept(this, rhsValueOffset, false);

		emitRM(RM.LD, ac, lhsAddressOffset, fp, "load lhs address");
		emitRM(RM.LD, ac1, rhsValueOffset, fp, "load rhs value");
		emitRM(RM.ST, ac1, 0, ac, "assign: store value");

		// Store result at original offset (for expression value)
		emitRM(RM.ST, ac1, offset, fp, "store result into stack");
		emitComment("<- Assign");
	}

	public void visit(BoolExp exp, int offset, boolean isAddress) {
		emitComment("-> BoolExp");
		emitRM(RM.LDC, ac, ((exp.value) ? 1 : 0), 0, "load truth into AC");
		emitRM(RM.ST, ac, offset, fp, "store truth on stack");
		emitComment("<- BoolExp");
	}

	public void visit(CallExp exp, int offset, boolean isAddress) {
		int curr_offset = offset - 2;

		ArrayList<Exp> all_args = exp.args.createIterable();
		for (Exp arg : all_args) {
			arg.accept(this, curr_offset, isAddress);
			curr_offset -= 1;
		}
		emitRM(RM.ST, fp, offset, fp, "store old FP");
		emitRM(RM.LDA, fp, offset, fp, "push new FP");
		emitRM(RM.LDA, ac, 1, pc, "save ret addr into AC");

		int dest_func = -1;
		if (exp.func.equals("input"))
			dest_func = inputEntry;
		else if (exp.func.equals("output"))
			dest_func = outputEntry;
		else
			dest_func = exp.def.funaddr.funaddr;

		if (dest_func == -1)
			exp.def.backpatchLocs.add(emitSkip(1)); // if function not enountered, backpatch
		else
			emitRM_Abs(RM.LDA, pc, dest_func, "jump to function call");
		emitRM(RM.LD, fp, 0, fp, "load old FP");
		if (!exp.def.isVoid())
			emitRM(RM.ST, ac, offset, fp, "store return value into stack offset");
	}

	public void visit(CompoundExp exp, int offset, boolean isAddress) {
		exp.decs.accept(this, frameOffset, false);
		exp.exps.accept(this, frameOffset, false);
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
					globalOffset -= 1;
				else
					globalOffset -= (((ArrayDec) (exp.head)).size + 1);
			}

			exp = exp.tail;
			inGlobalScope = true;
		}

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

	public void visit(FunctionDec exp, int offset, boolean isAddress) {
		if (exp.body instanceof NilExp)
			return;

		emitComment("-> funDec " + exp.name);
		// if main, set main entry accordingly
		int tempFO = frameOffset;
		frameOffset = initFO;
		int saveLoc = emitSkip(1);

		exp.typ.accept(this, frameOffset, false);
		exp.params.accept(this, frameOffset, false);

		if (exp.name.equals("main") && !(exp.body instanceof NilExp))
			mainEntry = getHighEmitLoc();
		exp.funaddr.funaddr = getHighEmitLoc();
		for (int backpatch : exp.backpatchLocs) {
			emitBackup(backpatch);
			emitRM_Abs(RM.LDA, pc, getHighEmitLoc(), "jump to func" + exp.name);
			emitRestore();
		}
		emitRM(RM.ST, ac, retFO, fp, "store return address into ret");
		exp.body.accept(this, frameOffset, false);
		emitRM(RM.LD, pc, retFO, fp, "load return addr into PC");

		frameOffset = tempFO;
		// backpatch a jump for the defintion
		emitBackup(saveLoc);
		emitRM_Abs(RM.LDA, pc, getHighEmitLoc(), "skip function execution");
		emitRestore();
		emitComment("<- funDec " + exp.name);
	}

	public void visit(IndexVar exp, int offset, boolean isAddress) {
		emitComment("-> IndexVar");
		exp.index.accept(this, offset - 1, false);
		emitRM(RM.LD, ac, offset - 1, fp, "load index");

		emitRM(((ArrayDec) exp.def).size == 0 ? RM.LD : RM.LDA, ac1, exp.def.offset, (exp.def.nestLevel == 0) ? gp : fp,
				"load array address");

		emitRM(RM.LD, ac1, -1, ac1, "load array size");

		// handle error
		emitRM(RM.JGE, ac, 3, pc, "jump over error on non-negative index");
		emitRM(RM.LDC, ac, -1000000, 0, "\"display large negative value for 'out of range below\"");
		emitRM(RM.ST, ac, offset, fp, "\"display large negative value for 'out of range below\"");
		emitRO(RO.HALT, 0, 0, 0, "error: negative index");

		emitRO(RO.SUB, ac, ac, ac1, "prep for JLT");
		emitRM(RM.JLT, ac, 3, pc, "jump over error on index < size");
		emitRM(RM.LDC, ac, -2000000, 0, "\"display large negative value for 'out of range above\"");
		emitRM(RM.ST, ac, offset, fp, "\"display large negative value for 'out of range above\"");
		emitRO(RO.HALT, 0, 0, 0, "error: index out of bounds");

		// error handling done
		emitRM(((ArrayDec) exp.def).size == 0 ? RM.LD : RM.LDA, ac1, exp.def.offset, (exp.def.nestLevel == 0) ? gp : fp,
				"load array address");
		emitRM(RM.LD, ac, offset - 1, fp, "load index");

		emitRO(RO.ADD, ac1, ac1, ac, "addr + index => arr[i]");

		emitRM((isAddress) ? RM.LDA : RM.LD, ac, 0, ac1, "load return into ac");
		emitRM(RM.ST, ac, offset, fp, "store return address into offset");
		emitComment("<- IndexVar");
	}

	public void visit(IfExp exp, int offset, boolean isAddress) {
		emitComment("-> if");
		exp.test.accept(this, offset, false);
		emitRM(RM.LD, ac, offset, fp, "restore test condition into ac");
		int falseJumpLoc = emitSkip(1); // Reserve space for jump
		emitComment("-> then block");
		exp.thenpart.accept(this, offset - 1, false);
		emitComment("<- then block");

		if (!(exp.elsepart instanceof NilExp)) {
			int exitJumpLoc = emitSkip(1); // Reserve space for unconditional jump
			emitComment("if: jump to exit");

			// Backpatch the original conditional jump
			emitBackup(falseJumpLoc);
			emitRM_Abs(RM.JEQ, ac, getHighEmitLoc(), "if: jump to else");
			emitRestore();

			// Else part
			emitComment("-> else block");
			exp.elsepart.accept(this, offset - 1, false);
			emitComment("<- else block");

			// Backpatch the exit jump
			emitBackup(exitJumpLoc);
			emitRM_Abs(RM.LDA, pc, getHighEmitLoc(), "if: exit");
			emitRestore();
		} else {
			// No else part - just backpatch the conditional jump
			emitBackup(falseJumpLoc);
			emitRM_Abs(RM.JEQ, ac, getHighEmitLoc(), "if: jump to exit");
			emitRestore();
		}
		emitComment("<- if");
	}

	public void visit(IntExp exp, int offset, boolean isAddress) {
		emitComment("-> constant");
		emitRM(RM.LDC, ac, exp.value, 0, "load const");
		emitRM(RM.ST, ac, offset, fp, "store const into stack");
		emitComment("<- constant");
	}

	public void visit(NameTy exp, int offset, boolean isAddress) {

	}

	public void visit(NilExp exp, int offset, boolean isAddress) {

	}

	public void visit(OpExp exp, int offset, boolean isAddress) {
		emitComment("-> op");

		// Handle unary operators (UMINUS, NOT) specially
		if (exp.op == OpExp.UMINUS || exp.op == OpExp.NOT) {
			exp.right.accept(this, offset - 1, false);

			emitRM(RM.LDC, ac, 0, 0, "load zero");
			emitRM(RM.LD, ac1, offset - 1, fp, "op: load rhs");
			if (exp.op == OpExp.UMINUS)
				emitRO(RO.SUB, ac, ac, ac1, "unary -");
			else { // OpExp.NOT
				emitRM(RM.JEQ, ac1, 1, pc, "jump on false"); // 0 is already stored in AC, just need to store in stack, and good to go
				emitRM(RM.LDC, ac, 1, 0, "load const 'true'");
			}
			emitRM(RM.ST, ac, offset, fp, "store result in offset");
			emitComment("<- op");
			return;
		}

		// Evaluate left operand
		exp.left.accept(this, offset - 1, false); // will store result in offset-1; not just AC

		// Evaluate right operand
		exp.right.accept(this, offset - 2, false);

		// load operands into registers
		emitRM(RM.LD, ac, offset - 1, fp, "op: load left");
		emitRM(RM.LD, ac1, offset - 2, fp, "op: load right");

		// Arithmetic OpExp
		switch (exp.op) {
		case OpExp.PLUS:
			emitRO(RO.ADD, ac, ac, ac1, "op +");
			emitRM(RM.ST, ac, offset, fp, "store result in offset");
			emitComment("<- op");
			return;
		case OpExp.MINUS:
			emitRO(RO.SUB, ac, ac, ac1, "op -");
			emitRM(RM.ST, ac, offset, fp, "store result in offset");
			emitComment("<- op");
			return;
		case OpExp.TIMES:
			emitRO(RO.MUL, ac, ac, ac1, "op *");
			emitRM(RM.ST, ac, offset, fp, "store result in offset");
			emitComment("<- op");
			return;
		case OpExp.DIV:
			emitRO(RO.DIV, ac, ac, ac1, "op /");
			emitRM(RM.ST, ac, offset, fp, "store result in offset");
			emitComment("<- op");
			return;
		}

		// J__ OpExp
		switch (exp.op) {
		case OpExp.EQ:
			emitRO(RO.SUB, ac, ac, ac1, "sub for JEQ");
			emitRM(RM.JEQ, ac, 2, pc, "jump to true on EQ");
			break;
		case OpExp.NEQ:
			emitRO(RO.SUB, ac, ac, ac1, "sub for JNEQ");
			emitRM(RM.JNE, ac, 2, pc, "jump to true on NEQ");
			break;
		case OpExp.LT:
			emitRO(RO.SUB, ac, ac, ac1, "sub for JLT");
			emitRM(RM.JLT, ac, 2, pc, "jump to true on LT");
			break;
		case OpExp.LTE:
			emitRO(RO.SUB, ac, ac, ac1, "sub for JLE");
			emitRM(RM.JLE, ac, 2, pc, "jump to true on LTE");
			break;
		case OpExp.GT:
			emitRO(RO.SUB, ac, ac, ac1, "sub for JGT");
			emitRM(RM.JGT, ac, 2, pc, "jump to true on GT");
			break;
		case OpExp.GTE:
			emitRO(RO.SUB, ac, ac, ac1, "sub for JGE");
			emitRM(RM.JGE, ac, 2, pc, "jump to true on GTE");
			break;
		case OpExp.AND:
			emitRO(RO.MUL, ac, ac, ac1, "multiply ac and ac1; on false, ac will be 0");
			emitRM(RM.JNE, ac, 2, pc, "and: jump if true");
			break;
		case OpExp.OR:
			emitRO(RO.MUL, ac, ac, ac, "square ac to guarantee positive val");
			emitRO(RO.MUL, ac1, ac1, ac1, "square ac1 to guarantee positive val");
			emitRO(RO.ADD, ac, ac, ac1, "add the ACs, if >0 is true");
			emitRM(RM.JGT, ac, 2, pc, "or: jump if true");
			break;
		}

		emitRM(RM.LDC, ac, 0, 0, "false case");
		emitRM(RM.LDA, pc, 1, pc, "unconditional jump");
		emitRM(RM.LDC, ac, 1, 0, "true case");
		emitRM(RM.ST, ac, offset, fp, "store result in offset");
		emitComment("<- op");
	}

	public void visit(ReturnExp exp, int offset, boolean isAddress) {
		emitComment("-> ReturnExp");
		exp.exp.accept(this, offset, false);

		emitRM(RM.LD, ac, offset, fp, "load value to return"); // keeping retval in AC
		emitRM(RM.LD, pc, retFO, fp, "load old retval into pc");
		emitComment("<- ReturnExp");
	}

	public void visit(SimpleDec exp, int offset, boolean isAddress) {
		exp.nestLevel = inGlobalScope ? 0 : 1;
		exp.offset = offset;
	}

	public void visit(SimpleVar exp, int offset, boolean isAddress) {
		emitComment("-> id");
		emitComment("looking up id: " + exp.name);

		if ((exp.def instanceof ArrayDec && ((ArrayDec) exp.def).size != 0) || isAddress)
			emitRM(RM.LDA, ac, exp.def.offset, (exp.def.nestLevel == 0) ? gp : fp, "load id address");
		else
			emitRM(RM.LD, ac, exp.def.offset, (exp.def.nestLevel == 0) ? gp : fp,
					"load id " + ((exp.def instanceof ArrayDec) ? "address" : "value"));

		emitRM(RM.ST, ac, offset, fp, "store id into stack");
		emitComment("<- id");
	}

	public void visit(VarDecList exp, int offset, boolean isAddress) {
		if (exp.head == null)
			return;

		while (exp != null) {
			exp.head.accept(this, frameOffset, false);
			if (exp.head instanceof ArrayDec)
				frameOffset -= (((ArrayDec) exp.head).size + 1);
			else
				frameOffset -= 1;
			exp = exp.tail;
		}
	}

	public void visit(VarExp exp, int offset, boolean isAddress) {
		// brug
		exp.variable.accept(this, offset, isAddress);
	}

	public void visit(WhileExp exp, int offset, boolean isAddress) {
		emitComment("-> while");
		int loopStart = getHighEmitLoc();

		exp.test.accept(this, offset, false);
		emitRM(RM.LD, ac, offset, fp, "load test condition");

		int exitJumpLoc = emitSkip(1); // Reserve space for jump
		emitComment("while: jump to exit");

		exp.body.accept(this, offset - 1, false);

		// Unconditional jump back to test
		emitRM_Abs(RM.LDA, pc, loopStart, "while: jump back to test");

		// Backpatch the exit jump
		emitBackup(exitJumpLoc);
		emitRM_Abs(RM.JEQ, ac, getHighEmitLoc(), "while: exit");
		emitRestore();
		emitComment("<- while");
	}
}
