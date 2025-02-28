package absyn;

abstract public class Absyn {
	public int row;
	public int column;

	abstract public void accept(AbsynVisitor visitor, int level);
}
