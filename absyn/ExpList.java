package absyn;

public class ExpList extends Absyn {
	public Exp head;
	public ExpList tail;

	public ExpList(Exp head, ExpList tail) {
		this.head = head;
		this.tail = tail;
	}

	public void accept(AbsynVisitor visitor, int level) { visitor.visit(this, level); }

	public String toString(String delim) {
		ExpList temp = this;
		StringBuilder s = new StringBuilder();
		while (temp != null) {
			s.append(temp.head.toString());
			if (temp.tail != null)
				s.append(delim);
			temp = temp.tail;
		}
		if (s.isEmpty())
			return "";
		return s.toString();
	}
	// TODO: equal and valid param_list
}
