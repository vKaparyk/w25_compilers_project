package absyn;

public class ArrayDec {
	public NameTy typ;
	public String name;
	public int size;

	public ArrayDec(int pos, NameTy typ, String name, int size) {
		this.typ = typ;
		this.name = name;
		this.size = size;
	}

	public void accept( AbsynVisitor visitor, int level ) {
		visitor.visit( this, level );
	  }
}
