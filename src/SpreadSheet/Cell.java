package SpreadSheet;
import java.awt.Color;
import java.util.HashMap;

/**
 * Cell - an object of this class holds the data of a single cell. 
 * 
 * @author Eric McCreath
 * @author Danyang Li
 */

public class Cell {

	private String text = ""; // this is the text the person typed into the cell
	private Double calculatedValue = null; // this is the current calculated value for the cell
	private Color background = null; //this is the background color of the cell
	public Cell(String text) {
		this.text = text;
		calculatedValue = null;
		background = null;
	}
	
	public Cell(String text,String color){
		this.text = text;
		this.background = new Color(Integer.parseInt(color));
	}

	public Cell() {
		text = "";
		calculatedValue = null;
	}
	
	public void setBackground(Color backgroundColor){
		this.background=backgroundColor;		
	}
	
	public Color getBackground(){
		return background;
	}

	public Double value() {
		return calculatedValue;
	}
	
	public void setValue(Double value){
		this.calculatedValue=value;
	}

	//calculate the result for each cell
	public void calcuate(WorkSheet worksheet) {
		String expString = this.getText();
		int count1 = 0;
		int count2 = 0;
		int index = 0;
		while(index < expString.length()) {
			char ch = expString.charAt(index);
			if(ch == '(')
				count1++;
			if(ch == ')'){
				count2++;
				if(count1 < count2) {
					worksheet.ERROR =  true;
					return;
				}
			}
			index++;
		}

		if (expString.startsWith("=")){
			try {
				Functions functions = new Functions(worksheet.getFuctions());
				HashMap<String, Double> vars = new HashMap<String, Double>();
				for (CellIndex key: worksheet.tabledata.keySet()){
					String var = key.show();
					String sval = worksheet.tabledata.get(key).show();
					Double val = new Double(0);
					try {
						val = Double.parseDouble(sval);
					} catch (NumberFormatException nfe) {
						calculatedValue = null;
					}
					vars.put(var, val);
				}
				Tokenizer tok = new Tokenizer(expString);
				Expression exp = Expression.parse(tok);
				this.setValue(exp.evaluate(vars,functions));
			} catch (Exception e) {
				worksheet.ERROR = true;
				e.printStackTrace();
			}
		}
	}
	
	public String show() {
		if(calculatedValue == null || "null".equals(calculatedValue)){
			if("null".equals(text)) return "";
			else return  text;
		}
		else return calculatedValue.toString();
	}

	@Override
	public String toString() {
		if(calculatedValue == null || "null".equals(calculatedValue)){
			if("null".equals(text)) return "";
			else return  text;
		}
		else return calculatedValue.toString();
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getText() {
		return text;
	}

	public boolean isEmpty() {
		if (text.equals("") && background==null)
			return true;
		else
			return false;
	}
}
