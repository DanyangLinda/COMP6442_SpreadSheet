package SpreadSheet;

import java.util.HashMap;


public class Number extends Expression {
	
	public Number(Double d) {
		num = d;
	}
	
	Double num = null;

	@Override
	public Double evaluate(HashMap<String, Double> vars, Functions functions) {
		return num;
	}	
	
}