package SpreadSheet;

import java.util.ArrayList;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tokenizer {

	private ArrayList<String> token = null;
	private int index = 0;

	public Tokenizer(String exp) {
		token = new ArrayList<String>();
		index = 0;
		Pattern p0 = null;
		Matcher m0 = null;
		String v0 = null;
		String exp1 = exp.replaceAll(" ", "");
		//match variable, number, function and operator
		p0 = Pattern.compile("([A-Z]+\\([^\\)]*\\))|([A-Z][0-9]+)|(?<=[\\d\\)])\\-|(\\-?[0-9]+\\.?[0-9]*)|([+\\-*\\/%^\\(\\):])");
		m0 = p0.matcher(exp1);
		
		while (m0.find()) {
			v0 = m0.group(0);
			//System.out.println(v0);
			token.add(v0);                       //(1)
		}

		Stack<String> s1 = new Stack<String>(); // Operator Statck
		Stack<String> pe = new Stack<String>(); // Pre-Expression Stack		
		String s, t;
		
		for (int i = token.size()-1; i >= 0; --i) {
			s = token.get(i);
			
			if (s.matches("\\-?[0-9\\.]+") || s.matches("[A-Z]+[0-9]+")) { // Number || Variable
				pe.push(s);
			} else if (s.matches("[A-Z]+\\([^\\\\)]*\\)")) { // Function
				pe.push(s);
			} else if (s.matches("[+\\-*/%]")) { // Operator
				while (!s1.isEmpty() && !(s1.peek().equals(")")) && priority(s, s1.peek()) < 0) {
					pe.push(s1.peek());
					s1.pop();
				}
				s1.push(s);
			} else if (s.equals(")")) {
				s1.push(s);
			} else if (s.equals("(")) {
				while (!(t = s1.pop()).equals(")")) {
					pe.push(t);
					if (s1.isEmpty()) {
						throw new IllegalArgumentException("Bracket dosen't match, missing right bracket ')'.");
					}
				}
			} else {
				throw new IllegalArgumentException("Invalid character '" + s + "'.");
			}
		}
		while (!s1.isEmpty()) {
			t = s1.pop();
			pe.push(t);
		}
		
		token.clear();
		while (!pe.isEmpty()) { // Split Function
			s = pe.pop();
			p0 = Pattern.compile("([A-Z][0-9]+)|([A-Z]+)|(\\-?[0-9]+\\.?[0-9]*)|([+\\-*\\/%^\\(\\):])");
			m0 = p0.matcher(s);
			while (m0.find()) {
				v0 = m0.group(0);
				token.add(v0);
			}
		}
		
		//for (String o : token)
		//	System.out.println(o);
	}
	
	private static int priority(String op1, String op2) {
		if (op1.equals("+") || op1.equals("-")) {
			if (op2.equals("*") || op2.equals("/") || op2.equals("%"))
				return -1;
			else
				return 0;
		} else if (op1.equals("*") || op1.equals("/") || op2.equals("%")) {
			if (op2.equals("+") || op2.equals("-"))
				return 1;
			else
				return 0;
		}
	    return 1; 
	}

	public Object current() {
		String s = token.get(index);
		if (s.matches("\\-?[0-9]+\\.?[0-9]*")) { //number
			return new Double(Double.parseDouble(s));
		}
		return s;
	}

	public boolean next() {
		if (index == token.size())
			return false;
		index++;
		return true;
	}

	public boolean end() {
		if (index == token.size())
			return true;
		return false;
	}

	public void parse(String s) throws ParseException {
		if (!token.get(index).equalsIgnoreCase(s)) {
			String serr = "Parse Error: " + token.get(index) + " dosen't match " + s + ".";
			throw new ParseException(serr);
		} else {
			next();
		}
	}
	
	public void replace(String s1, String s2) {
		for (int i = 0; i < token.size(); i++) {
			if (token.get(i).equals(s1)) {
				token.set(i, s2);
			}
		}
	}
}

