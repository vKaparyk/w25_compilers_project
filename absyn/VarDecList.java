package absyn;

public class VarDecList extends Absyn {
	public VarDec head;
	public VarDecList tail;

	public VarDecList(VarDec head, VarDecList tail) {
		this.head = head;
		this.tail = tail;
	}

	public void accept(AbsynVisitor visitor, int level) { visitor.visit(this, level); }

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;

		VarDecList other = (VarDecList) obj;

		VarDecList tmp = this;

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

	@Override
	public String toString() { return this.toString(",", false); }

	public String toString(boolean isHeader) { return this.toString(",", isHeader); }

	public String toString(String delim) { return this.toString(delim, false); }

	public String toString(String delim, boolean isHeader) {
		if (this.head == null) {
			if (isHeader)
				return "void";
			return "";
		}
		VarDecList temp = this;
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
