package absyn;

public interface AbsynVisitor {

	public void visit(Absyn exp, int level);

	public void visit(ArrayDec exp, int level);

	public void visit(AssignExp exp, int level);

	public void visit(Exp exp, int level);

	public void visit(ExpList exp, int level);

	public void visit(IfExp exp, int level);

	public void visit(IntExp exp, int level);

	public void visit(OpExp exp, int level);

	public void visit(SimpleDec exp, int level);

	public void visit(VarExp exp, int level);

}
