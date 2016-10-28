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
		bb.addInstruction("decr z;");
		bb.addInstruction("incr x;");
		bb.addInstruction("incr z;");
		bb.addInstruction("incr z;");
		bb.addInstruction("incr z;");
		bb.addInstruction("while z not 0 do;");
		bb.addInstruction("decr z;");
		bb.addInstruction("end;");
		//bb.addInstruction("clear x;");
		Interpreter i = new Interpreter();
		i.interpret(bb);
		
	}
	
	public void interpret(Program p) {

		String cline;
		String[] cis;
		Variable cvar;
		
		while (!(pc > p.getLineCount())) {
			cline = p.getInstruction(pc);
			
			//for (String ci : cline.split(";")){
				//cis = ci.split(" ");
			if (cline.endsWith(";")) {
				cline = cline.substring(0, cline.length() -1);
			} else {
				System.err.println("Undeliminated instruction!");
				System.exit(0);
			}
			cis = cline.split(" ");
			
				switch (cis[0]) {
				case "clear":
					//if (variableList.containsKey(cis[1])) {
					//	cvar = variableList.get(cis[1]);
					//} else {
					//	variableList.put(cis[1], new Variable(cis[1]));
					//	cvar = variableList.get(cis[1]);
					//}
					//cvar.setValue(0);
					clear(cis);
					break;
				case "incr": 
					//if (variableList.containsKey(cis[1])) {
					//	cvar = variableList.get(cis[1]);
					//} else {
					//	variableList.put(cis[1], new Variable(cis[1]));
					//	cvar = variableList.get(cis[1]);
					//}
					incr(cis);
					//cvar.setValue(cvar.getValue() + 1);
					break;
				case "decr":
					if (variableList.containsKey(cis[1])) {
						cvar = variableList.get(cis[1]);
					} else {
						variableList.put(cis[1], new Variable(cis[1]));
						cvar = variableList.get(cis[1]);
					}
					try {
						if (cvar.getValue() - 1 < 0) throw new Exception();
						cvar.setValue(cvar.getValue() - 1);
					} catch (Exception e) {
						System.err.println("ERROR HANDLING VAR: " + cvar.getIdentifier());
						System.err.println("Could not decrease below 0!");
						System.exit(0);
					}
					break;
				case "while":
					if (variableList.containsKey(cis[1])) {
						cvar = variableList.get(cis[1]);
					} else {
						variableList.put(cis[1], new Variable(cis[1]));
						cvar = variableList.get(cis[1]);
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
					break;
				case "end":
					pc = returnStack.get(returnStack.size() -1);
					returnStack.remove(returnStack.size() - 1);
					branch = true;
					break;
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
		
		if (args.length > 2) {
			System.err.println("Fatal error occured executing CLEAR command");
			System.err.println("Line: " + pc);
			System.err.println("ERROR CODE 2: Incorrect arguments");
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
		
		if (args.length > 2) {
			System.err.println("Fatal error occured executing INCR command");
			System.err.println("Line: " + pc);
			System.err.println("ERROR CODE 2: Too many arguments");
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
		
		if (args.length > 2) {
			System.err.println("Fatal error occured executing DECR command");
			System.err.println("Line: " + pc);
			System.err.println("ERROR CODE 2: Too many arguments");
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
	
}
