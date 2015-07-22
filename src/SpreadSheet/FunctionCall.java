package SpreadSheet;

import java.util.ArrayList;
import java.util.HashMap;


public class FunctionCall extends Expression {
	
	public FunctionCall () {
		
	}
	
	Double d = null;
	public String name = null;
	public ArrayList<Expression> expressions = null;

	@Override
	public Double evaluate(HashMap<String, Double> vars, Functions functions) {
		if (expressions.size() == 2 && expressions.get(0) instanceof Variable && expressions.get(1) instanceof Variable){
			String exp1 = ((Variable) expressions.get(0)).var;
			String exp2 = ((Variable) expressions.get(1)).var;
			Character sc1 = exp1.replaceAll("[0-9]+", "").charAt(0);
			Character sc2 = exp2.replaceAll("[0-9]+", "").charAt(0);
			int i1 = Integer.parseInt(exp1.replaceAll("[A-Z]+", ""));
			int i2 = Integer.parseInt(exp2.replaceAll("[A-Z]+", ""));
			//i2 stores the larger value
			if (i1>i2){
				int temp = i2;
				i2=i1;
				i1=temp;
			}
			//sc2 stores the larger value
			if(sc1>sc2){
				Character temp = sc2;
				sc2 = sc1;
				sc1 = temp;						
			}
			//implement the SUM operation
			if(name.equals("SUM")){
				d = new Double(0);
				for(int i=i1;i<=i2;i++){
					for (char j=sc1;j<=sc2;j++){
						d += vars.get(j+""+Integer.toString(i));
					}
				}
			}
			//implement the COUNT operation
			else if(name.equals("COUNT")){
				return new Double((i2-i1+1)*(sc2-sc1+1));		
			}
			//implement the MAX and Min operation
			else if(name.equals("MAX")||name.equals("MIN")){
				d = new Double(0);
				Double maxTemp = Double.MIN_VALUE;
				Double minTemp = Double.MAX_VALUE;
				for(int i=i1;i<=i2;i++)
					for (char j=sc1;j<=sc2;j++){
						d = vars.get(j+""+Integer.toString(i));
						if (d > maxTemp){
							maxTemp = d;
						}
						if (d < minTemp){
							minTemp = d;
						}			
					}
				if (name.equals("MAX"))
					return maxTemp;
				else
					return minTemp;
				
			}
			//deal with the expression defined by users
			else if ((d = functions.evaluate(name, expressions, vars)) != null) {
				return d;
			}
		} 
		return d;
	}
	
}