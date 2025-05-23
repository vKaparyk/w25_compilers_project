import java.util.*;
import absyn.*;

class Sym {
	String name;
	Dec def;
	int scope;

	public Sym(String name, Dec def, int scope) {
		this.name = name;
		this.def = def;
		this.scope = scope;
	}

	public String toString() {
		StringBuilder s = new StringBuilder();

		if (def instanceof FunctionDec) {
			FunctionDec funcDef = (FunctionDec) def;
			s.append("Function");
			if (funcDef.body instanceof NilExp) {
				s.append(" Prototype");
			}
			s.append(": " + name + "(");
			VarDecList params = funcDef.params;
			while (params != null) {
				if (params.head instanceof SimpleDec) {
					SimpleDec simpleDec = (SimpleDec) params.head;
					s.append(simpleDec.typ.toString());
				} else if (params.head instanceof ArrayDec) {
					ArrayDec arrayDec = (ArrayDec) params.head;
					s.append(arrayDec.typ.toString() + "[" + arrayDec.size + "]");
				}
				if (params.tail != null)
					s.append(", ");
				params = params.tail;
			}
			s.append(") -> " + funcDef.typ.toString());
		} else if (def instanceof SimpleDec) {
			SimpleDec simpleDef = (SimpleDec) def;
			s.append(name + ": " + simpleDef.typ.toString());
		} else if (def instanceof ArrayDec) {
			ArrayDec simpleDef = (ArrayDec) def;
			s.append(name + ": " + simpleDef.typ.toString() + "[]");
		} else {
			s.append("Error");
		}

		return s.toString();
	}

	public Dec getDef() {
		return def;
	}
}

class SymbolTable {
	final static int SPACES = 4;

	// Each scope has two separate maps: one for variables and one for functions
	Stack<HashMap<String, Sym>> variableScopes = new Stack<>();
	HashMap<String, Sym> functionScopes = new HashMap<>();

	void enterScope() {
		variableScopes.push(new HashMap<>());
	}

	void exitScope() {
		variableScopes.pop();
	}

	boolean addVariable(Sym symbol) {
		if (variableScopes.peek().containsKey(symbol.name)) {
			return false;
		}
		variableScopes.peek().put(symbol.name, symbol);
		return true;
	}

	boolean addFunction(Sym functionSymbol) {
		// @formatter:off
		if (functionScopes.containsKey(functionSymbol.name) && (
					((FunctionDec) functionSymbol.def).body instanceof NilExp							// passed in function prototype cant overwrite existing prototype
				|| !(((FunctionDec) lookupFunction(functionSymbol.name).def).body instanceof NilExp) 	// making sure the found definition is a prototype
				|| !(((FunctionDec) lookupFunction(functionSymbol.name).def).params
						.equals(((FunctionDec) functionSymbol.def).params)) 							// making sure the parameters are the same as the prototype
			)) {
			return false;
		}
		// @formatter:on

		Sym fuc = lookupFunction(functionSymbol.name);
		if (fuc != null) {
			((FunctionDec) fuc.def).body = ((FunctionDec) functionSymbol.def).body;
			((FunctionDec) fuc.def).params = ((FunctionDec) functionSymbol.def).params;
			((FunctionDec) fuc.def).funaddr = ((FunctionDec) functionSymbol.def).funaddr;
		} else
			functionScopes.put(functionSymbol.name, functionSymbol);

		return true;
	}

	public Sym lookupVariable(String name) {
		for (int i = variableScopes.size() - 1; i >= 0; i--) {
			if (variableScopes.get(i).containsKey(name)) {
				return variableScopes.get(i).get(name);
			}
		}
		return null; // Not found
	}

	public Sym lookupFunction(String name) {
		return functionScopes.get(name);
	}

	void printTopScope(int level) {
		String space = "";
		for (int i = 0; i < level * SPACES; i++)
			space += " ";

		for (Sym symbol : variableScopes.peek().values()) {
			System.out.println(space + symbol.toString());
		}
		if (variableScopes.size() == 1) {
			for (Sym symbol : functionScopes.values()) {
				System.out.println(space + symbol.toString());
			}
		}
	}
}