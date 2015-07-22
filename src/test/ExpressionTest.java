package test;

import static org.junit.Assert.*;

import java.util.HashMap;

import org.junit.Test;

import SpreadSheet.Functions;
import SpreadSheet.Expression;
import SpreadSheet.ParseException;
import SpreadSheet.Tokenizer;

public class ExpressionTest {

	@Test
	public void test() {
		String exp = "1";
		Tokenizer tok =new Tokenizer(exp);
		Double expected = new Double(1);
		HashMap<String, Double> vars = new HashMap<String, Double>();
		Functions functions = new Functions("");
		try {
			assertEquals(Expression.parse(tok).evaluate(vars, functions),expected);
		} catch (ParseException e) {
			fail();
		}
	}

}
