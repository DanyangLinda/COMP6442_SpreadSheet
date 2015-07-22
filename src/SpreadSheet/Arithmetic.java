package SpreadSheet;

import java.util.HashMap;


public class Arithmetic extends Expression {
	
	public Arithmetic(String op, Expression e1, Expression e2) {
		oper = op;
		exp1 = e1;
		exp2 = e2;
	}
	
	String oper = null;
	Expression exp1 = null;
	Expression exp2 = null;

	@Override
	public Double evaluate(HashMap<String, Double> vars, Functions functions) {
		if (oper.equals("+")) {
			return exp1.evaluate(vars, functions) + exp2.evaluate(vars, functions);
		} 
		else if (oper.equals("-")) {
			return exp1.evaluate(vars, functions) - exp2.evaluate(vars, functions);
		} 
		else if (oper.equals("*")) {
			return exp1.evaluate(vars, functions) * exp2.evaluate(vars, functions);
		} 
		else if (oper.equals("/")) {
			return exp1.evaluate(vars, functions) / exp2.evaluate(vars, functions);
		} 
		else if (oper.equals("%")) {
			return new Double(exp1.evaluate(vars, functions).intValue() % exp2.evaluate(vars, functions).intValue());
		} 
		else {
			return null;
		}
	}	
	
}