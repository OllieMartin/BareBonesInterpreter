import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.text.Highlighter.HighlightPainter;

@SuppressWarnings("serial")
public class IDE extends JFrame {

	public IDE thisIDE = this;
	private JTextArea editor = new JTextArea(20,80);
	private JTextArea output = new JTextArea(20,70);
	private JFileChooser fChooser = new JFileChooser(System.getProperty("user.dir"));
	private String currentFile = "Untitled";
	private boolean newChanges = false;
	private TextAreaOutputStream taOutputStream = new TextAreaOutputStream(
	         output, "Interpreter Output");
	private TextAreaOutputStream errorOutputStream = new TextAreaOutputStream(
	         output, "ERROR");
	
	Highlighter h = editor.getHighlighter();
    HighlightPainter painter = 
           new DefaultHighlighter.DefaultHighlightPainter(Color.cyan);
    
    HighlightPainter painter2 = 
            new DefaultHighlighter.DefaultHighlightPainter(Color.orange);
	
	public TextAreaOutputStream getOutputStream() {
		return taOutputStream;
	}
	
	public TextAreaOutputStream getErrorStream() {
		return errorOutputStream;
	}
	
	private KeyListener k1 = new KeyAdapter() {
		public void keyPressed(KeyEvent e) {
			newChanges = true;
			Save.setEnabled(true);
			SaveAs.setEnabled(true);
			
			/*if (editor.getText().matches("([^\"[^\".*\"]]*clear(.|\n)*)+")) {
			for (int i = -1; (i = editor.getText().indexOf("clear", i + 1)) != -1; ) {
				try {
					h.addHighlight(i, i + "clear".length(), painter );
				} catch (BadLocationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			} // prints "4", "13", "22"*/

		}
		public void keyTyped(KeyEvent e) {
			Timer time = new Timer();
			time.schedule(new TimerTask() {
				@Override
				public void run() {
					highlightSyntax();
				}
			}, 5);
		}
	};
	
	public void highlightSyntax() {
		h.removeAllHighlights();
		highlightErrors();
		Pattern pattern = Pattern.compile("(?m)(?=[^\"]*(?:\"[^\"]*\"[^\"]*)*$)(clear|incr|decr|end|not|while|do)(" + whitespace_chars + "|$| |;)");
	    Matcher matcher = pattern.matcher(editor.getText());
	    // Check all occurrences
	    while (matcher.find()) {
	        try {
				h.addHighlight(matcher.start(1), matcher.end(1), painter );
			} catch (BadLocationException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	    }
	}
	
	public void highlightErrors() {
		for (int i = 0; i <= editor.getLineCount() -1 ; i++) {
			try {
				if (!(editor.getText(editor.getLineStartOffset(i), editor.getLineEndOffset(i) - editor.getLineStartOffset(i)).endsWith(";")) && !(editor.getText(editor.getLineStartOffset(i), editor.getLineEndOffset(i) - editor.getLineStartOffset(i)).endsWith(";\n"))) {
					try {
						h.addHighlight(editor.getLineStartOffset(i), editor.getLineEndOffset(i), painter2);
					} catch (BadLocationException e) {
						// TODO Auto-generated catch block
						//e.printStackTrace();
					}	
				}
			} catch (BadLocationException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}
		}
	}
	
	public void highlightLine(int line) {
		h.removeAllHighlights();
		try {
			h.addHighlight(editor.getLineStartOffset(line), editor.getLineEndOffset(line), painter2);
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	Action Open = new AbstractAction("Open") {
		public void actionPerformed(ActionEvent e) {
			saveOld();
			if(fChooser.showOpenDialog(null)==JFileChooser.APPROVE_OPTION) {
				readInFile(fChooser.getSelectedFile().getAbsolutePath());
			}
			SaveAs.setEnabled(true);
			highlightSyntax();
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
			setTitle(currentFile);
			Save.setEnabled(false);
			SaveAs.setEnabled(false);
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
	
	public void clearOutput() {
		output.setText("");
	}
	
	String whitespace_chars =  ""       /* dummy empty string for homogeneity */
            + "\\u0009" // CHARACTER TABULATION
            + "\\u000A" // LINE FEED (LF)
            + "\\u000B" // LINE TABULATION
            + "\\u000C" // FORM FEED (FF)
            + "\\u000D" // CARRIAGE RETURN (CR)
            + "\\u0020" // SPACE
            + "\\u0085" // NEXT LINE (NEL) 
            + "\\u00A0" // NO-BREAK SPACE
            + "\\u1680" // OGHAM SPACE MARK
            + "\\u180E" // MONGOLIAN VOWEL SEPARATOR
            + "\\u2000" // EN QUAD 
            + "\\u2001" // EM QUAD 
            + "\\u2002" // EN SPACE
            + "\\u2003" // EM SPACE
            + "\\u2004" // THREE-PER-EM SPACE
            + "\\u2005" // FOUR-PER-EM SPACE
            + "\\u2006" // SIX-PER-EM SPACE
            + "\\u2007" // FIGURE SPACE
            + "\\u2008" // PUNCTUATION SPACE
            + "\\u2009" // THIN SPACE
            + "\\u200A" // HAIR SPACE
            + "\\u2028" // LINE SEPARATOR
            + "\\u2029" // PARAGRAPH SEPARATOR
            + "\\u202F" // NARROW NO-BREAK SPACE
            + "\\u205F" // MEDIUM MATHEMATICAL SPACE
            + "\\u3000" // IDEOGRAPHIC SPACE
            ;      
	
}

/*
 *  9
down vote
	

Use the DefaultHighlighter that comes with your JTextArea. For e.g.,

import java.awt.Color;
import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import javax.swing.text.Highlighter.HighlightPainter;

public class Foo001 {
   public static void main(String[] args) throws BadLocationException {

      JTextArea textArea = new JTextArea(10, 30);

      String text = "hello world. How are you?";

      textArea.setText(text);

      Highlighter highlighter = textArea.getHighlighter();
      HighlightPainter painter = 
             new DefaultHighlighter.DefaultHighlightPainter(Color.pink);
      int p0 = text.indexOf("world");
      int p1 = p0 + "world".length();
      highlighter.addHighlight(p0, p1, painter );

      JOptionPane.showMessageDialog(null, new JScrollPane(textArea));          
   }
}


 */
