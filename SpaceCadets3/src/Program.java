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
		String instruction;
		instruction = instructions.get(line-1);
		while (instruction.startsWith("\t") || instruction.startsWith(" ")) {
			if (instruction.startsWith("\t")) {
				instruction = instruction.replaceFirst("\t", "");
			} else {
				instruction = instruction.replaceFirst(" ", "");
			}
		}
		return instruction;
	}
	
	public int getLineCount() {
		return lineCount;
	}
	
}
