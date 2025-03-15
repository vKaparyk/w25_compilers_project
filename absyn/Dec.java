package absyn;

abstract public class Dec extends Absyn {
    public NameTy typ;
    public String name;

    public boolean isInt() { return typ.typ == NameTy.INT; }

    public boolean isBool() { return typ.typ == NameTy.BOOL; }

    public boolean isVoid() { return typ.typ == NameTy.VOID; }

    public boolean isArray() { return false; };

    abstract public String toString();
}