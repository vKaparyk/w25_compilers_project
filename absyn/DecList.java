package absyn;

public class DecList extends Absyn {
	public Dec head;
	public DecList tail;

	public DecList(Dec head, DecList tail) {
		this.head = head;
		this.tail = tail;
	}

	public void accept(AbsynVisitor visitor, int level, boolean flag) {
		visitor.visit(this, level, flag);
	}

	@Override
	public String toString() {
		return this.toString("\n");
	}

	public String toString(String delim) {
		if (this.head == null)
			return "";
		DecList temp = this;
		StringBuilder s = new StringBuilder();
		while (temp != null) {
			s.append(temp.head.toString());
			if (temp.tail != null)
				s.append(delim);
			temp = temp.tail;
		}
		return s.toString();
	}
}
