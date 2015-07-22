package SpreadSheet;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class Expression {
	
	public abstract Double evaluate(HashMap<String,Double> vars, Functions functions);
	
	public static Expression parse(Tokenizer tok) throws ParseException {
		Object t = tok.current();
		
		if (t instanceof Double) {
			Double v = (Double) t;
			tok.next();
			return new Number(v); //return number
		} 
		else if (t instanceof String && ((String) t).matches("[A-Z]+[0-9]+")) {
			tok.next();
			return new Variable((String) t); //return variable
		} 
		else if (t instanceof String && ((String) t).matches("[+\\-*/%]")) {
			tok.next();
			Expression exp1 = parse(tok);
			Expression exp2 = parse(tok);
			return new Arithmetic((String) t, exp1, exp2); //return arithmetic
		} 
		else if (t instanceof String) {
			FunctionCall res = new FunctionCall();
			res.expressions = new ArrayList<Expression>();
			res.name = (String) t;
			tok.next();
			tok.parse("(");
			while (!")".equals(tok.current())) {
				if (tok.current().equals(":")) tok.next();
				res.expressions.add(parse(tok));
			}
			tok.parse(")");
			return res;
		} else {
			throw new ParseException();
		}
	}
}