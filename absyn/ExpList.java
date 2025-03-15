package absyn;

public class ExpList extends Absyn {
	public Exp head;
	public ExpList tail;

	public ExpList(Exp head, ExpList tail) {
		this.head = head;
		this.tail = tail;
	}

	public void accept(AbsynVisitor visitor, int level) { visitor.visit(this, level); }

	@Override
	public String toString() { return this.toString(","); }

	public String toString(String delim) {
		if (this.head == null)
			return "";
		ExpList temp = this;
		StringBuilder s = new StringBuilder();
		while (temp != null) {
			s.append(temp.head.toString());
			if (temp.tail != null)
				s.append(delim);
			temp = temp.tail;
		}
		return s.toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;

		ExpList other = (ExpList) obj;

		ExpList tmp = this;

		while (tmp != null || other != null) {
			if (tmp == null || other == null)
				return false;
			if (tmp.head == null && other.head == null)
				return true;
			if (tmp.head == null && other.head != null)
				return false;
			if (tmp.head != null && other.head == null)
				return false;
			if (!tmp.head.equals(other.head))
				return false;

			tmp = tmp.tail;
			other = other.tail;
		}

		return true;
	}
}
