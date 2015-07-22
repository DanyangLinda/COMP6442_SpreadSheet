package test;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;

import SpreadSheet.Functions;
import SpreadSheet.Number;

import org.junit.Test;

public class NumberTest {
	Number num1 = new Number(6.0);

	@Test
	public void test() {		
		Double expected = new Double(6);
		HashMap<String, Double> vars = new HashMap<String, Double>();
		Functions functions = new Functions("");
		assertEquals(num1.evaluate(vars, functions),expected);
	}

}
