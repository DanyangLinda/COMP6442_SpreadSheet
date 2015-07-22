package test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;

import SpreadSheet.Expression;
import SpreadSheet.FunctionCall;
import SpreadSheet.Functions;
import SpreadSheet.Variable;

public class FunctionCallTest {
	FunctionCall fc = new FunctionCall();
	
	@Before
	public void setup() {
		Variable exp1 = new Variable("A1");
		Variable exp2 = new Variable("B1");
		fc.expressions = new ArrayList<Expression>();
		fc.expressions.add(exp1);
		fc.expressions.add(exp2);
		
	}
	
	@Test
	public void testSUM() {
		Double expected = new Double(3);		
		HashMap<String, Double> vars = new HashMap<String, Double>();
		vars.put("A1", new Double(1));
		vars.put("B1", new Double(2));
		Functions functions = new Functions("");
		fc.name = "SUM";
		assertEquals(fc.evaluate(vars, functions),expected, 0.0001);
	}
	
	@Test
	public void testCOUNT() {
		Double expected = new Double(2);		
		HashMap<String, Double> vars = new HashMap<String, Double>();
		vars.put("A1", new Double(1));
		vars.put("B1", new Double(2));
		Functions functions = new Functions("");
		fc.name = "COUNT";
		assertEquals(fc.evaluate(vars, functions),expected, 0.0001);
	}
	
	@Test
	public void testMAX() {
		Double expected = new Double(2);		
		HashMap<String, Double> vars = new HashMap<String, Double>();
		vars.put("A1", new Double(1));
		vars.put("B1", new Double(2));
		Functions functions = new Functions("");
		fc.name = "MAX";
		assertEquals(fc.evaluate(vars, functions),expected, 0.0001);
	}
	
	@Test
	public void testMIN() {
		Double expected = new Double(1);		
		HashMap<String, Double> vars = new HashMap<String, Double>();
		vars.put("A1", new Double(1));
		vars.put("B1", new Double(2));
		Functions functions = new Functions("");
		fc.name = "MIN";
		assertEquals(fc.evaluate(vars, functions),expected, 0.0001);
	}

}
