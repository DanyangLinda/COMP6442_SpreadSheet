package test;

import static org.junit.Assert.*;
import org.junit.Test;
import SpreadSheet.Tokenizer;

public class TokenizerTest {
	Tokenizer tok = new Tokenizer("A1+2");

	@Test
	public void test1() {
		String expected = "+";
		assertEquals((String)tok.current(),expected);
	}
	
	@Test
	public void test2() {
		tok.next();
		String expected = "A1";
		assertEquals((String)tok.current(),expected);
	}
	
	@Test
	public void test3() {
		tok.next();
		tok.next();
		Double expected = new Double(2);
		Double result = (Double)tok.current();
		assertEquals(result,expected);
	}
}
