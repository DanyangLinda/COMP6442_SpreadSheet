package SpreadSheet;

import java.util.HashMap;


public class Variable extends Expression {
	String var = null;
	
	public Variable(String v) {
		var = v;
	}
	


	@Override
	public Double evaluate(HashMap<String, Double> vars, Functions functions) {
		if (var != null && vars != null && vars.containsKey(var)) {
			return vars.get(var);
		}
		return null;
	}	
	
}
