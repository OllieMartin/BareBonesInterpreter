
public class Variable {

	protected int value;
	protected String id;
	
	public Variable(String identifier) {
		value = 0;
		id = identifier;
	}
	public Variable(String identifier, int newValue){
		value = newValue;
		id = identifier;
	}
	public int getValue() {
		return value;
	}
	public void setValue(int newValue) {
		value = newValue;
	}
	
	public String getIdentifier() {
		return id;
	}
	
}
