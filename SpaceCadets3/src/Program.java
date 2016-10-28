import java.util.ArrayList;

public class Program {

	protected int lineCount;
	protected ArrayList<String> instructions = new ArrayList<String>();
	
	public Program() {
		lineCount = 0;
	}
	
	public void addInstruction(String instruction) {
		instructions.add(instruction);
		lineCount++;
	}
	
	public String getInstruction(int line) {
		return instructions.get(line-1);
	}
	
	public int getLineCount() {
		return lineCount;
	}
	
}
