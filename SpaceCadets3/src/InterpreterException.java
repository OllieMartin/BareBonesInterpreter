
@SuppressWarnings("serial")
public class InterpreterException extends Exception {
	
	protected int id;
	protected String instr;
	protected int line;
	
	public InterpreterException(int errorCode, int pc) {
		id = errorCode;
		instr = null;
		line = pc;
	}
	public InterpreterException(int errorCode, int pc, String instruction) {
		id = errorCode;
		instr = instruction;
		line = pc;
	}
	
	public void displayDetails() {
		handleError(id, instr);
	}
	
	protected void handleError(int errorCode, String commandWord) {
		switch (errorCode) {
		case 1:
			System.err.println("Fatal error occured executing an instruction");
			System.err.println("Line: " + line);
			System.err.println("ERROR CODE 1: Undeliminated instruction");
			break;
		case 2:
			System.err.println("Fatal error occured executing " + commandWord.toUpperCase());
			System.err.println("Line: " + line);
			if (commandWord.equals("end")) {
				System.err.println("ERROR CODE 2: Incorrect number of arguments [Expected 0]");
			}
			if (commandWord.equals("while not 0 do")) {
				System.err.println("ERROR CODE 2: Incorrect number of arguments [Expected 4]");
			}
			if (commandWord.equals("decr") || commandWord.equals("incr") || commandWord.equals("clear")) {
				System.err.println("ERROR CODE 2: Incorrect number of arguments [Expected 1]");
			}
			break;
		case 3:
			System.err.println("Fatal error occured executing DECR command");
			System.err.println("Line: " + line);
			System.err.println("ERROR CODE 3: Cannot decrease variable below 0");
			break;
		case 4:
			System.err.println("Fatal error occured executing WHILE NOT 0 DO command");
			System.err.println("Line: " + line);
			System.err.println("ERROR CODE 4: Invalid argument values, Must follow format 'while <variable> not 0 do'");
			break;
		case 5:
			System.err.println("Fatal error occured during execution");
			System.err.println("Line: " + line);
			System.err.println("ERROR CODE 5: Unrecognised command word");
			break;
		case 6:
			System.err.println("Fatal error occured executing END");
			System.err.println("Line: " + line);
			System.err.println("ERROR CODE 6: Disconnected END command, returnStack empty");
			break;
		case 7:
			System.err.println("Fatal error occured executing WHILE NOT 0 DO");
			System.err.println("Line: " + line);
			System.err.println("ERROR CODE 7: No END command found to terminate loop");
			break;
		case 8:
			System.err.println("Warning for at least one WHILE NOT 0 DO loop in the program...");
			System.err.println("ERROR CODE 8: END statement for this loop is missing");
			System.err.println("As the loop condition was true the code executed");
			System.err.println("This continued to the end of the program");
			System.err.println("However; if the condition were false...");
			System.err.println("...then no END statement would have been found to jump to");
			System.err.println("This would result in a fatal run time error");
			break;
		}
	}

}
