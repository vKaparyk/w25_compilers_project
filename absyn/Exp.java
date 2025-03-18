package absyn;

abstract public class Exp extends Absyn {
    public Dec dtype = null;

    public String decString() { return dtype.typ.toString() + ((dtype instanceof ArrayDec) ? "[]" : ""); }
}
