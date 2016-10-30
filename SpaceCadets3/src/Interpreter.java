import java.util.ArrayList;
import java.util.HashMap;

public class Interpreter {

	protected int pc; //Program Counter
	protected ArrayList<Integer> returnStack = new ArrayList<Integer>(); //The return stack for while loops
	protected HashMap<String, Variable> variableList = new HashMap<String, Variable>(); //Variable identifier mapped to their respective object
	protected String ci; //Current instruction
	protected boolean branch; //If a branch has occurred 
	protected boolean outputToIDE;
	protected IDE currentIDE;
	
	public Interpreter() {
		pc = 1; //Set program counter to start at 1
		outputToIDE = false;
	}
	public Interpreter(IDE ide) {
		pc = 1; //Set program counter to start at 1
		outputToIDE = true;
		currentIDE = ide;
	}
	
	public void interpret(Program p) {

		String cline;
		String[] cis;
		
		while (!(pc > p.getLineCount())) {
			cline = p.getInstruction(pc);
			if (outputToIDE) {
				currentIDE.addOutputLine(cline);
			} else {
				System.out.println(cline);
			}
			if (cline.endsWith(";")) {
				cline = cline.substring(0, cline.length() -1);
			} else {
				System.err.println("Fatal error occured executing an instruction");
				System.err.println("Line: " + pc);
				System.err.println("ERROR CODE 1: Undeliminated instruction");
				System.exit(0);
			}
			cis = cline.split(" ");
			
				switch (cis[0]) {
				case "clear":
					clear(cis);
					break;
				case "incr": 
					incr(cis);
					break;
				case "decr":
					decr(cis);
					break;
				case "while":
					whiledo(cis,p);
					break;
				case "end":
					end(cis);
					break;
				default:
					System.err.println("Fatal error occured during execution");
					System.err.println("Line: " + pc);
					System.err.println("ERROR CODE 5: Unrecognised command word");
					System.exit(0);
				}
			
			if (!branch) {
				pc++;
			} else {
				branch = false;
			}
			
			outputVars();
		}
		
		System.out.println("*** FINAL RESULT ***");
		
		for (Variable v : variableList.values()) {
			System.out.println(v.getIdentifier() + " = " + v.getValue());
		}
		
	}
	
	protected void clear(String[] args) {	
		
		if (args.length != 2) {
			System.err.println("Fatal error occured executing CLEAR command");
			System.err.println("Line: " + pc);
			System.err.println("ERROR CODE 2: Incorrect number of arguments [Expected 1]");
			System.exit(0);
		}
		
		Variable cvar;
		
		if (variableList.containsKey(args[1])) {
			cvar = variableList.get(args[1]);
		} else {
			variableList.put(args[1], new Variable(args[1]));
			cvar = variableList.get(args[1]);
		}
		cvar.setValue(0);
	}
	
	protected void incr(String[] args) {
		
		if (args.length != 2) {
			System.err.println("Fatal error occured executing INCR command");
			System.err.println("Line: " + pc);
			System.err.println("ERROR CODE 2: Incorrect number of arguments [Expected 1]");
			System.exit(0);
		}
		
		Variable cvar;
		
		if (variableList.containsKey(args[1])) {
			cvar = variableList.get(args[1]);
		} else {
			variableList.put(args[1], new Variable(args[1]));
			cvar = variableList.get(args[1]);
		}
		cvar.setValue(cvar.getValue() + 1);
		
	}
	
	protected void decr(String[] args) {
		
		if (args.length != 2) {
			System.err.println("Fatal error occured executing DECR command");
			System.err.println("Line: " + pc);
			System.err.println("ERROR CODE 2: Incorrect number of arguments [Expected 1]");
			System.exit(0);
		}
		
		Variable cvar;
		
		if (variableList.containsKey(args[1])) {
			cvar = variableList.get(args[1]);
		} else {
			variableList.put(args[1], new Variable(args[1]));
			cvar = variableList.get(args[1]);
		}
		try {
			if (cvar.getValue() - 1 < 0) throw new Exception();
			cvar.setValue(cvar.getValue() - 1);
		} catch (Exception e) {
			System.err.println("Fatal error occured executing DECR command");
			System.err.println("Line: " + pc);
			System.err.println("ERROR CODE 3: Cannot decrease variable (" + cvar.getIdentifier() + ") below 0");
			System.exit(0);
		}
	}
	
	protected void whiledo(String args[], Program p) {
		
		if (args.length != 5) {
			System.err.println("Fatal error occured executing WHILE NOT 0 DO command");
			System.err.println("Line: " + pc);
			System.err.println("ERROR CODE 2: Incorrect number of arguments [Expected 4]");
			System.exit(0);
		}
		
		if (!args[2].equals("not") || !args[3].equals("0") || !args[4].equals("do")) {
			System.err.println("Fatal error occured executing WHILE NOT 0 DO command");
			System.err.println("Line: " + pc);
			System.err.println("ERROR CODE 4: Invalid argument values, Must follow format 'while <variable> not 0 do'");
			System.exit(0);
		}
		
		Variable cvar;
		
		if (variableList.containsKey(args[1])) {
			cvar = variableList.get(args[1]);
		} else {
			variableList.put(args[1], new Variable(args[1]));
			cvar = variableList.get(args[1]);
		}
		if (cvar.getValue() == 0) {
			String sline = "";
			int whilecount = 0;
			do {
				pc++;
				sline = p.getInstruction(pc);
				if (sline.startsWith("while")) {
					whilecount++;
				}
				if (sline.equals("end;")) {
					whilecount--;
				}
			} while (pc <= p.getLineCount() && (!sline.equals("end;") || whilecount > -1));
			branch = true;
			pc++;
		} else {
			returnStack.add(pc);
		}
	}
	
	protected void end(String args[]) {
		
		if (args.length != 1) {
			System.err.println("Fatal error occured executing END");
			System.err.println("Line: " + pc);
			System.err.println("ERROR CODE 2: Incorrect number of arguments [Expected 0]");
			System.exit(0);
		}
		
		if(returnStack.size() == 0) {
			System.err.println("Fatal error occured executing END");
			System.err.println("Line: " + pc);
			System.err.println("ERROR CODE 6: Disconnected END command, returnStack empty");
			System.exit(0);
		}
		
		pc = returnStack.get(returnStack.size() -1);
		returnStack.remove(returnStack.size() - 1);
		branch = true;
	}
	
	public HashMap<String, Variable> getVarList() {
		return variableList;
	}
	
	public void outputVars() {
		if (outputToIDE) {
			for (Variable v : variableList.values()) {
				currentIDE.addOutputLine(v.getIdentifier() + " = " + v.getValue());
			}
			currentIDE.addOutputLine("-----------");
		} else {
			for (Variable v : variableList.values()) {
				System.out.println(v.getIdentifier() + " = " + v.getValue());
			}
			System.out.println("-----------");
		}
	}
	
}
