package test;

import static org.junit.Assert.*;

import java.util.HashMap;

import org.junit.Test;

import SpreadSheet.Arithmetic;
import SpreadSheet.Number;
import SpreadSheet.Functions;

public class ArithmeticTest {
	Number exp1 = new Number(new Double(1));
	Number exp2 = new Number(new Double(2));
	Arithmetic ar;
	@Test
	public void testAdd() {
		ar = new Arithmetic("+",exp1,exp2);
		Double expected = new Double(3);
		HashMap<String, Double> vars = new HashMap<String, Double>();
		Functions functions = new Functions("");
		assertEquals(ar.evaluate(vars, functions),expected);
	}
	
	@Test
	public void testMinus() {
		ar = new Arithmetic("-",exp1,exp2);
		Double expected = new Double(-1);
		HashMap<String, Double> vars = new HashMap<String, Double>();
		Functions functions = new Functions("");
		assertEquals(ar.evaluate(vars, functions),expected);
	}
	
	@Test
	public void testProduct() {
		ar = new Arithmetic("*",exp1,exp2);
		Double expected = new Double(2);
		HashMap<String, Double> vars = new HashMap<String, Double>();
		Functions functions = new Functions("");
		assertEquals(ar.evaluate(vars, functions),expected);
	}
	
	@Test
	public void testDivide() {
		ar = new Arithmetic("/",exp1,exp2);
		Double expected = new Double(0.5);
		HashMap<String, Double> vars = new HashMap<String, Double>();
		Functions functions = new Functions("");
		assertEquals(ar.evaluate(vars, functions),expected);
	}

}
