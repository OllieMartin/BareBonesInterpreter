import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import javax.swing.text.*;

@SuppressWarnings("serial")
public class IDE extends JFrame {

	public IDE thisIDE = this;
	private JTextArea editor = new JTextArea(20,60);
	private JTextArea output = new JTextArea(20,60);
	private JFileChooser fChooser = new JFileChooser(System.getProperty("user.dir"));
	private String currentFile = "Untitled";
	private boolean newChanges = false;
	
	private KeyListener k1 = new KeyAdapter() {
		public void keyPressed(KeyEvent e) {
			newChanges = true;
			Save.setEnabled(true);
			SaveAs.setEnabled(true);
		}
	};
	
	Action Open = new AbstractAction("Open") {
		public void actionPerformed(ActionEvent e) {
			saveOld();
			if(fChooser.showOpenDialog(null)==JFileChooser.APPROVE_OPTION) {
				readInFile(fChooser.getSelectedFile().getAbsolutePath());
			}
			SaveAs.setEnabled(true);
		}
	};
	
	Action Save = new AbstractAction("Save") {
		public void actionPerformed(ActionEvent e) {
			if(!currentFile.equals("Untitled"))
				saveFile(currentFile);
			else
				saveFileAs();
		}
	};
	
	Action SaveAs = new AbstractAction("Save as...") {
		public void actionPerformed(ActionEvent e) {
			saveFileAs();
		}
	};
	
	Action Quit = new AbstractAction("Quit") {
		public void actionPerformed(ActionEvent e) {
			saveOld();
			System.exit(0);
		}
	};
	
	Action New = new AbstractAction("New File") {
		public void actionPerformed(ActionEvent e) {
			saveOld();
			editor.setText("");
			currentFile = "Untitled";
			newChanges = false;
		}
	};
	
	Action Interpret = new AbstractAction("Interpret") {
		public void actionPerformed(ActionEvent e) {
			
			String fileline;
			Interpreter i = new Interpreter(thisIDE);
			
			saveOld();
			newChanges = false;
			Program currentProgram = new Program();
			
			try {
	            FileReader fileReader = 
	                new FileReader(currentFile);
	            BufferedReader bufferedReader = 
	                new BufferedReader(fileReader);

	            while((fileline = bufferedReader.readLine()) != null) {
	                currentProgram.addInstruction(fileline);
	            }   
	            bufferedReader.close();         
	        }
	        catch(FileNotFoundException ex) {
	            System.out.println(
	                "File doesnt exist");           
	        }
	        catch(IOException ex) {
	            System.out.println(
	                "Error reading file");               
	        }
			
			i.interpret(currentProgram);
			output.append("*** FINAL OUTPUT *** \n");
			i.outputVars();
			
		}
	};
	
	ActionMap m = editor.getActionMap();
	Action Cut = m.get(DefaultEditorKit.cutAction);
	Action Copy = m.get(DefaultEditorKit.copyAction);
	Action Paste = m.get(DefaultEditorKit.pasteAction);
	
	public void addOutputLine(String text) {
		output.append(text + "\n");
	}
	
	public static void main(String args[]) {

		@SuppressWarnings("unused")
		IDE ide = new IDE();
		
	}
	
	public IDE() {
		editor.setFont(new Font("Monospaced",Font.PLAIN,12));
		output.setFont(new Font("Monospaced",Font.PLAIN,12));
		output.setEditable(false);
		JScrollPane scroll = new JScrollPane(editor,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		JScrollPane scroll2 = new JScrollPane(output,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		add(scroll,BorderLayout.WEST);
		add(scroll2,BorderLayout.EAST);
		JMenuBar JMB = new JMenuBar();
		setJMenuBar(JMB);
		JMenu file = new JMenu("File");
		JMenu edit = new JMenu("Edit");
		JMB.add(file); JMB.add(edit);
		
		file.add(New);
		file.add(Open);
		file.add(Save);
		file.add(Quit);
		file.add(SaveAs);
		file.addSeparator();
		
		for(int i=0; i<4; i++)
			file.getItem(i).setIcon(null);
		
		edit.add(Cut);edit.add(Copy);edit.add(Paste);

		edit.getItem(0).setText("Cut out");
		edit.getItem(1).setText("Copy");
		edit.getItem(2).setText("Paste");
		
		JToolBar tool = new JToolBar();
		add(tool,BorderLayout.NORTH);
		tool.add(New);tool.add(Open);tool.add(Save);
		tool.addSeparator();
		
		JButton cut = tool.add(Cut), cop = tool.add(Copy),pas = tool.add(Paste);
		tool.addSeparator();
		tool.add(Interpret);
		
		cut.setText("Cut");
		cop.setText("Copy");
		pas.setText("Paste");
		
		Save.setEnabled(false);
		SaveAs.setEnabled(false);
		
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		pack();
		editor.addKeyListener(k1);
		setTitle(currentFile);
		setVisible(true);
	}
	
	private void saveFileAs() {
		if(fChooser.showSaveDialog(null)==JFileChooser.APPROVE_OPTION)
			saveFile(fChooser.getSelectedFile().getAbsolutePath());
	}
	
	private void saveOld() {
		if(newChanges) {
			if(JOptionPane.showConfirmDialog(this, "Would you like to save "+ currentFile +" ?","Save",JOptionPane.YES_NO_OPTION)== JOptionPane.YES_OPTION)
				saveFile(currentFile);
		}
	}
	
	private void readInFile(String fileName) {
		try {
			FileReader r = new FileReader(fileName);
			editor.read(r,null);
			r.close();
			currentFile = fileName;
			setTitle(currentFile);
			newChanges = false;
		}
		catch(IOException e) {
			Toolkit.getDefaultToolkit().beep();
			JOptionPane.showMessageDialog(this,"Editor can't find the file called "+fileName);
		}
	}
	
	private void saveFile(String fileName) {
		try {
			FileWriter w = new FileWriter(fileName);
			editor.write(w);
			w.close();
			currentFile = fileName;
			setTitle(currentFile);
			newChanges = false;
			Save.setEnabled(false);
		}
		catch(IOException e) {
		}
	}
	
}
