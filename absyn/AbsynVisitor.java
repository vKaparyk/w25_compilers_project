package absyn;

public interface AbsynVisitor {

	public void visit(ArrayDec exp, int level);

	public void visit(AssignExp exp, int level);

	public void visit(BoolExp exp, int level);

	public void visit(OpExp exp, int level);

	public void visit(CallExp exp, int level);

	public void visit(CompoundExp exp, int level);

	public void visit(DecList exp, int level);

	public void visit(ExpList exp, int level);

	public void visit(FunctionDec exp, int level);

	public void visit(IfExp exp, int level);

	public void visit(IndexVar exp, int level);

	public void visit(IntExp exp, int level);

	public void visit(NameTy exp, int level);

	public void visit(NilExp exp, int level);

	public void visit(ReturnExp exp, int level);

	public void visit(SimpleDec exp, int level);

	public void visit(SimpleVar exp, int level);

	public void visit(VarDecList exp, int level);

	public void visit(VarExp exp, int level);

	public void visit(WhileExp exp, int level);

}
