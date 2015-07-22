package SpreadSheet;

public class ParseException extends Exception {
	private static final long serialVersionUID = 8930730209321088339L;
	private static String err = null;
	
	public ParseException() {
		err = new String("Parse Error.");
	}

	public ParseException(String serr) {
		err = serr;
	}

	public String toString() {
		return err;
	}
}

