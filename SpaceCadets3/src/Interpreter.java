import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;

public class Interpreter {

	protected int pc; //Program Counter
	protected ArrayList<Integer> returnStack = new ArrayList<Integer>(); //The return stack for while loops
	protected HashMap<String, Variable> variableList = new HashMap<String, Variable>(); //Variable identifier mapped to their respective object
	protected String ci; //Current instruction
	protected boolean branch; //If a branch has occurred 
	protected IDE currentIDE;
	
	public Interpreter() {
		pc = 1; //Set program counter to start at 1
		currentIDE = null;
	}
	public Interpreter(IDE ide) {
		pc = 1; //Set program counter to start at 1
		currentIDE = ide;
		System.setOut(new PrintStream(currentIDE.getOutputStream()));
		System.setErr(new PrintStream(currentIDE.getErrorStream()));
	}
	
	public void interpret(Program p) {

		try {
		
		//If linked to an IDE then clear the output box
		if (currentIDE != null) {
			currentIDE.clearOutput();
		}
		
		String cline; //Current program line data
		String[] cis; //Current instruction split into components
		
		//While end of the program has not yet been reached
		while (!(pc > p.getLineCount())) {
			cline = p.getInstruction(pc); //Get the program line corresponding to the program counter
			System.out.println(cline); //Output this line
			
			//If correctly deliminated then remove deliminator for processing
			if (cline.endsWith(";")) {
				cline = cline.substring(0, cline.length() -1);
			} else {
				//Otherwise throw error 1
				throw new InterpreterException(1,pc);
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
					throw new InterpreterException(5,pc);
				}
			
			if (!branch) {
				pc++;
			} else {
				branch = false;
			}
			
			outputVars();
		}
		
		System.out.println("*** FINAL RESULT ***");
		
		outputVars();
		
		if (returnStack.size() != 0) {
			throw new InterpreterException(8,pc);
		}
		
		} catch (InterpreterException ie) {
			ie.displayDetails();
		}
	}
	
	protected void clear(String[] args) throws InterpreterException {	
		
		if (args.length != 2) {
			throw new InterpreterException(2,pc,"clear");
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
	protected void incr(String[] args) throws InterpreterException {
		
		if (args.length != 2) {
			throw new InterpreterException(2,pc, "incr");
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
	protected void decr(String[] args) throws InterpreterException {
		
		if (args.length != 2) {
			throw new InterpreterException(2,pc,"decr");
		}
		
		Variable cvar;
		
		if (variableList.containsKey(args[1])) {
			cvar = variableList.get(args[1]);
		} else {
			variableList.put(args[1], new Variable(args[1]));
			cvar = variableList.get(args[1]);
		}
		if (cvar.getValue() - 1 < 0) throw new InterpreterException(3,pc); else cvar.setValue(cvar.getValue() - 1);
	}
	protected void whiledo(String args[], Program p) throws InterpreterException {
		
		if (args.length != 5) {
			throw new InterpreterException(2,pc,"while not 0 do");
		}
		
		if (!args[2].equals("not") || !args[3].equals("0") || !args[4].equals("do")) {
			throw new InterpreterException(4,pc);
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
			} while (pc < p.getLineCount() && (!sline.equals("end;") || whilecount > -1));
			if (pc == p.getLineCount()) {
				throw new InterpreterException(7,pc);
			}
			branch = true;
			pc++;
		} else {
			returnStack.add(pc);
		}
	}
	protected void end(String args[]) throws InterpreterException {
		
		if (args.length != 1) {
			throw new InterpreterException(2,pc,"end");
		}
		
		if(returnStack.size() == 0) {
			throw new InterpreterException(6,pc);
		}
		
		pc = returnStack.get(returnStack.size() -1);
		returnStack.remove(returnStack.size() - 1);
		branch = true;
	}
	
	public void outputVars() {
			for (Variable v : variableList.values()) {
				System.out.println(v.getIdentifier() + " = " + v.getValue());
			}
			System.out.println("-----------");
	}

}
