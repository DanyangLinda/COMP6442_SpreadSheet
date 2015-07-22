package SpreadSheet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Functions {

	HashMap<String, String> funs = new HashMap<String, String>();

	public Functions(String functions) {
		String[] kv = functions.split("\n");
		for (int i = 0; i < kv.length; i++) {
			String s = kv[i];
			String[] fa = s.split("=+");
			if (fa.length == 2) {
				funs.put(fa[0], fa[1]);
			}
		}
	}
	
	public Double evaluate(String name, ArrayList<Expression> expressions, HashMap<String, Double> vars) {
		String k = null;
		String v = null;
		Iterator iter = funs.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry<String, String> entry = (Map.Entry<String, String>) iter.next();
			if (entry.getKey().startsWith(name)) {
				k = entry.getKey();
				v = entry.getValue();
				break;
			}				
		}
		
		if (k != null) {
			Tokenizer tok = new Tokenizer(v);
			
			String sk = k.substring(k.indexOf('(')+1, k.lastIndexOf(')'));
			String[] s = sk.split(":");
			
			if (s.length == expressions.size()) {
				for (int i = 0; i < s.length; i++) {		
					tok.replace(s[i], ((Variable) expressions.get(i)).var);
				}
			}
			
			try {
				return Expression.parse(tok).evaluate(vars, this);
			} catch (ParseException pe) {
			}
		}
				
		return null;
	}

}