import java.util.ArrayList;
import java.util.HashMap;

public class Interpreter {

	protected int pc;
	protected ArrayList<Integer> returnStack = new ArrayList<Integer>();
	protected HashMap<String, Variable> variableList = new HashMap<String, Variable>();
	protected String ci;
	protected boolean branch;
	
	public Interpreter() {
		pc = 1;
	}
	
	public static void main(String args[]) {
		
		Program bb = new Program();
		bb.addInstruction("clear x;");
		bb.addInstruction("incr z;");
		bb.addInstruction("incr z;");
		bb.addInstruction("incr z;");
		bb.addInstruction("incr z;");
		bb.addInstruction("while z not 0 do;");
		bb.addInstruction("incr x;");
		bb.addInstruction("decr z;");
		bb.addInstruction("end;");
		Interpreter i = new Interpreter();
		i.interpret(bb);
		
	}
	
	public void interpret(Program p) {

		String cline;
		String[] cis;
		
		while (!(pc > p.getLineCount())) {
			cline = p.getInstruction(pc);
			
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
			
		}
		
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
			do {
				pc++;
				sline = p.getInstruction(pc);
			} while (pc <= p.getLineCount() && !sline.equals("end;"));
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
		
		pc = returnStack.get(returnStack.size() -1);
		returnStack.remove(returnStack.size() - 1);
		branch = true;
	}
	
}
