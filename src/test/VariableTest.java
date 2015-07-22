package test;

import static org.junit.Assert.*;
import java.util.HashMap;
import org.junit.Test;
import SpreadSheet.Functions;
import SpreadSheet.Variable;

public class VariableTest {
	Variable var = new Variable("A1");
	
	@Test
	public void test() {
		Double expected = new Double(6);
		HashMap<String, Double> vars = new HashMap<String, Double>();
		vars.put("A1", new Double(6));
		Functions functions = new Functions("");
		assertEquals(var.evaluate(vars, functions),expected);
	}

}
