package absyn;

abstract public class VarDec extends Dec {
	public int nestLevel;
	public int offset;

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;

		VarDec other = (VarDec) obj;

		// Compare the type (assuming typ is a field in VarDec)
		if (this instanceof ArrayDec && other instanceof ArrayDec) {
			return ((ArrayDec) this).typ.typ == (((ArrayDec) other).typ.typ);
		} else if (this instanceof SimpleDec && other instanceof SimpleDec) {
			return ((SimpleDec) this).typ.typ == (((SimpleDec) other).typ.typ);
		}

		return false;
	}
}