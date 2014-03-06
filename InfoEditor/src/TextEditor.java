//Changes phuong
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

import java.util.regex.*;
import java.io.*;
import java.util.ArrayList;

/**
   The TextEditor class is a simple text editor.
*/

public class TextEditor extends JFrame
{
   // The following are fields for the menu system.
   // First, the menu bar
   private JMenuBar menuBar;
   JPanel contents;
   // The menus
   private JMenu fileMenu;
   private JMenu viewMenu;
   private JMenu fontMenu;
  
   
   private boolean wrapped=false;
   // The menu items
   private JMenuItem newItem;
   private JMenuItem openItem;
   private JMenuItem openList;
   private JMenuItem saveItem;
   private JMenuItem saveAsItem;
   private JMenuItem exitItem;
   
   // The radio button menu items
   private JRadioButtonMenuItem monoItem;
   private JRadioButtonMenuItem serifItem;
   private JRadioButtonMenuItem sansSerifItem;
   
   // The checkbox menu items
   private JCheckBoxMenuItem italicItem;
   private JCheckBoxMenuItem boldItem;
   private JCheckBoxMenuItem showLeftList;
   private JCheckBoxMenuItem showAnnotations;
   private JCheckBoxMenuItem wordWrap;
   
   private String filename;     // To hold the file name
   private JTextPane editorText;// To display the text
   private JTree leftList;
  
   JScrollPane scrollPane ;
   JPanel noWrapPanel;
   
   
   private boolean hasFileChangedSinceSave; //tracks if any changes have been made to the file since the last save

   
   private InputMap inMap;
   private ActionMap actMap;
   /**
      Constructor
   */

   
   
   public TextEditor()
   {
      // Set the title.
      setTitle("Text Editor");
this.setPreferredSize(new Dimension(800,800));
      // Specify what happens when the close
      // button is clicked.
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

      // Create the text area.
      editorText = new JTextPane();
      editorText.getDocument().addDocumentListener(new textChangeListener());// adds listener for seeing if any changes have been made to the text
      
      
      hasFileChangedSinceSave=false; //initializes variable to false since the program has just started and nothing has changed yet
  
     
      
      

      // Create a scroll pane and add the text area to it.
      noWrapPanel = new JPanel( new BorderLayout() );
      noWrapPanel.add( editorText );
      
      inMap=noWrapPanel.getInputMap(JPanel.WHEN_IN_FOCUSED_WINDOW);
      actMap=noWrapPanel.getActionMap();
    
      KeyStroke controlS= KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK);  //creates a keystroke for control-s
      inMap.put(controlS, "control-s");//maps the keystroke to an Input Map
      actMap.put("control-s",  new ctrlS() );  //run ctrlS when control-s is pressed
      
      KeyStroke controlB= KeyStroke.getKeyStroke(KeyEvent.VK_B, KeyEvent.CTRL_DOWN_MASK);  //creates a keystroke for control-b
      inMap.put(controlB, "control-b");//maps the keystroke to an Input Map
      actMap.put("control-b",  new ctrlB() );  //run ctrlB when control-b is pressed
      
      KeyStroke controlI= KeyStroke.getKeyStroke(KeyEvent.VK_I, KeyEvent.CTRL_DOWN_MASK);  //creates a keystroke for control-i
      inMap.put(controlI, "control-i");//maps the keystroke to an Input Map
      actMap.put("control-i",  new ctrlI() );  //run ctrlI when control-i is pressed

      scrollPane = new JScrollPane( noWrapPanel );
      
      // scrollPane = new JScrollPane(editorText);
   
       contents= new JPanel();
       contents.setLayout(new BorderLayout());
       contents.add(scrollPane, BorderLayout.CENTER);

      // Add the scroll pane to the content pane.
      add(contents);

      // Build the menu bar.
      buildMenuBar();

      // Pack and display the window.
      pack();
      setVisible(true);
   }
public void wordWrap(){
	if (wrapped == true){        
        scrollPane.setViewportView(editorText);
       
    }else {
    	scrollPane.setViewportView(noWrapPanel);
        noWrapPanel.add(editorText); 
   
    }
}
   /**
    * Apply an annotation to the selected text
    */
   
   public void applyAnnotation(String value,Color c){

	   StyledDocument doc = editorText.getStyledDocument();
	    int start = editorText.getSelectionStart();
	    int end = editorText.getSelectionEnd();

	    if (start == end) { // No selection, cursor position.
	        return;
	    }
	    if (start > end) { // Backwards selection?
	        int life = start;
	        start = end;
	        end = life;
	    }

	    Style style = editorText.addStyle("annotated", null);
	    StyleConstants.setForeground(style, c);
	    doc.setCharacterAttributes(start, end - start, style, false);
	    try {
	    	doc.insertString(end, "</"+value+">",null);
			doc.insertString(start, "<"+value+">",null);

		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    StyleConstants.setForeground(style, Color.black);
   }
   public void hideAnnotations(boolean hide){
	   String text =editorText.getText();
	   Pattern pattern = Pattern.compile("<.*?>");


	// search for a match within a string
	   Matcher matcher = pattern.matcher(text);
	    // check all occurance
	    while (matcher.find()) {

	      setInvisible(matcher.start(),matcher.end(),hide);

	    }
   }
   /*
    * Set selected text invisible
    */
   public void setInvisible(int start,int end,boolean hide){


	    if(hide){
	    	StyledDocument doc = editorText.getStyledDocument();
	 	   Style regular = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);
	    	 Style invisible = editorText.getStyledDocument().addStyle("invisible", regular);
	 	    StyleConstants.setFontSize(invisible, 0);
	 	    StyleConstants.setForeground(invisible, editorText.getBackground());
	    doc.setCharacterAttributes(start, end - start, invisible, false);
	    }
	    else{	  
	    	System.out.println("show it");
	    	StyledDocument doc = editorText.getStyledDocument();
		 	   Style plain = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);
		 	  StyleConstants.setFontSize(plain, 12);
		 	    StyleConstants.setForeground(plain, editorText.getForeground());
	    	doc.setCharacterAttributes(start, end - start, plain, false);	
	    }
   }
   /**
      The buildMenuBar method creates a menu bar and
      calls the createFileMenu method to create the
      file menu.
   */

   private void buildMenuBar()
   {
      // Build the file and font menus.
      buildFileMenu();
      buildFontMenu();
      buildViewMenu();

      // Create the menu bar.
      menuBar = new JMenuBar();

      // Add the file and font menus to the menu bar.
      menuBar.add(fileMenu);
      menuBar.add(fontMenu);
      menuBar.add(viewMenu);

      // Set the menu bar for this frame.
      setJMenuBar(menuBar);
   }

   private void buildViewMenu(){
	      // Create the Italic menu item.
	      showLeftList = new JCheckBoxMenuItem("Show Ontology");
	      showLeftList.setSelected(true);
	      showLeftList.addActionListener(new ListListener());

	      wordWrap = new JCheckBoxMenuItem("Word Wrap");
	      wordWrap.setSelected(false);
	      wordWrap.addActionListener(new TextFormatListener());

	      showAnnotations = new JCheckBoxMenuItem("Show Annotations");
	      showAnnotations.setSelected(true);
	      showAnnotations.addActionListener(new TextFormatListener());

	      // Create a menu for the items we just created.
	      viewMenu = new JMenu("View");
	      viewMenu.setMnemonic(KeyEvent.VK_V);
	      viewMenu.add(showLeftList);
	      viewMenu.add(wordWrap);
	      viewMenu.add(showAnnotations);
   }
   /**
      The buildFileMenu method creates the file menu
      and populates it with its menu items.
   */

   private void buildFileMenu()
   {
      // Create the New menu item.
      newItem = new JMenuItem("New");
      newItem.setMnemonic(KeyEvent.VK_N);
      newItem.addActionListener(new NewListener());

      // Create the Open menu item.
      openItem = new JMenuItem("Open File");
      openItem.setMnemonic(KeyEvent.VK_O);
      openItem.addActionListener(new OpenListener());

      // Create the Open menu item.
      openList = new JMenuItem("Open Ontology");
      openList.setMnemonic(KeyEvent.VK_L);
      openList.addActionListener(new OpenListListener());
      
      // Create the Save menu item.
      saveItem = new JMenuItem("Save");
      saveItem.setMnemonic(KeyEvent.VK_S);
      saveItem.addActionListener(new SaveListener());

      // Create the Save As menu item.
      saveAsItem = new JMenuItem("Save As");
      saveAsItem.setMnemonic(KeyEvent.VK_A);
      saveAsItem.addActionListener(new SaveListener());

      // Create the Exit menu item.
      exitItem = new JMenuItem("Exit");
      exitItem.setMnemonic(KeyEvent.VK_X);
      exitItem.addActionListener(new ExitListener());

      // Create a menu for the items we just created.
      fileMenu = new JMenu("File");
      fileMenu.setMnemonic(KeyEvent.VK_F);

      // Add the items and some separator bars to the menu.
      fileMenu.add(newItem);
      fileMenu.add(openItem);
      fileMenu.add(openList);
      fileMenu.addSeparator();// Separator bar
      fileMenu.add(saveItem);
      fileMenu.add(saveAsItem);
      fileMenu.addSeparator();// Separator bar
      fileMenu.add(exitItem);
   }

   /**
      The buildFontMenu method creates the font menu
         and populates it with its menu items.
   */

   private void buildFontMenu()
   {
      // Create the Monospaced menu item.
      monoItem = new JRadioButtonMenuItem("Monospaced");
      monoItem.addActionListener(new FontListener());

      // Create the Serif menu item.
      serifItem = new JRadioButtonMenuItem("Serif");
      serifItem.addActionListener(new FontListener());

      // Create the SansSerif menu item.
      sansSerifItem =
              new JRadioButtonMenuItem("SansSerif", true);
      sansSerifItem.addActionListener(new FontListener());

      // Group the radio button menu items.
      ButtonGroup group = new ButtonGroup();
      group.add(monoItem);
      group.add(serifItem);
      group.add(sansSerifItem);

      // Create the Italic menu item.
      italicItem = new JCheckBoxMenuItem("Italic");
      italicItem.setActionCommand("italic");
      italicItem.addActionListener(new FontListener());

      // Create the Bold menu item.
      boldItem = new JCheckBoxMenuItem("Bold");
      boldItem.setActionCommand("bold");
      boldItem.addActionListener(new FontListener());

      // Create a menu for the items we just created.
      fontMenu = new JMenu("Font");
      fontMenu.setMnemonic(KeyEvent.VK_T);

      // Add the items and some separator bars to the menu.
      fontMenu.add(monoItem);
      fontMenu.add(serifItem);
      fontMenu.add(sansSerifItem);
      fontMenu.addSeparator();// Separator bar
      fontMenu.add(italicItem);
      fontMenu.add(boldItem);
      
    
   }

   /**
      Private inner class that handles the event that
      is generated when the user selects New from 
      the file menu.
   */

   private class NewListener implements ActionListener
   {
      public void actionPerformed(ActionEvent e)
      {
    	  int optionSelect=2; //the button you pick will change this number, "save"=0, "discard"=1, "cancel"=2
    	  if(hasFileChangedSinceSave)
    		  {
    		  Object[] buttons ={"save","discard","cancel"}; //these are the buttons for when it asks you if you want to save changes before starting new file
    		  optionSelect=JOptionPane.showOptionDialog(null,"Do you want to save changes before starting new file?", "warning", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE,null, buttons, buttons[2]);
    		  System.out.println(optionSelect);
    		  
    		  if(optionSelect==0) //goes through different options depending on which button was selected  
    	     	 {
    	    		 new SaveListener().actionPerformed(new ActionEvent("saving", 0, null));
    	    		 if(filename!=null)
    	    		 {
    	    				editorText.setText("");
    	    	    		filename = null;
    	    	    		setTitle("Text Editor-"+"untitled");
    	    	    		hasFileChangedSinceSave=false;
    	    		 }
    	     	 }
    	    	  
    	    	  if(optionSelect==1)  
    	    	 {
    	    			editorText.setText("");
    		    		filename = null;
    		    		setTitle("Text Editor-"+"untitled");
    		    		hasFileChangedSinceSave=false;
    	    	 }
    		  
    		  }
    	  else
    	  {
    		  	editorText.setText("");
	    		filename = null;
	    		setTitle("Text Editor-"+"untitled");
	    		hasFileChangedSinceSave=false;
    	  }
    	  
    
      }
   }
  
   /**
   Private inner class that handles the event that
   is generated when the user selects Open Ontology from
   the file menu.
*/
   private class OpenListListener implements ActionListener
   {
      public void actionPerformed(ActionEvent e)
      {
         int chooserStatus;

         JFileChooser chooser = new JFileChooser();
         chooserStatus = chooser.showOpenDialog(null);
         if (chooserStatus == JFileChooser.APPROVE_OPTION)
         {
            // Get a reference to the selected file.
            File selectedFile = chooser.getSelectedFile();

            // Get the path of the selected file.
            filename = selectedFile.getPath();

            // Open the file.
            if (!openList(filename))
            {
               JOptionPane.showMessageDialog(null, 
                                "Error reading " +
                                filename, "Error",
                                JOptionPane.ERROR_MESSAGE);
            }
         }
      }

      
      /**
         The openList method opens the file specified 
         after clicking "open ontology" . 
         The method returns true if the file was
         opened and read successfully, or false if an
         error occurred.
         @param filename The name of the file to open.
      */

      private boolean openList(String filename)
      {
      
  
         JTreeBuilder tree= new JTreeBuilder();
         try {
			leftList = tree.build(filename);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
         leftList.setBackground(Color.LIGHT_GRAY);
         leftList.addMouseListener(new OntologyTreeMouseListener(leftList,TextEditor.this));
         contents.add(leftList, BorderLayout.WEST);
      
       contents.updateUI();
       return true;
      }
   }
   /**
      Private inner class that handles the event that
      is generated when the user selects Open from
      the file menu.
   */

   private class OpenListener implements ActionListener
   {
      public void actionPerformed(ActionEvent e)
      {
         int chooserStatus;

         JFileChooser chooser = new JFileChooser();
         chooserStatus = chooser.showOpenDialog(null);
         if (chooserStatus == JFileChooser.APPROVE_OPTION)
         {
            // Get a reference to the selected file.
            File selectedFile = chooser.getSelectedFile();

            // Get the path of the selected file.
            filename = selectedFile.getPath();

            // Open the file.
            if (!openFile(filename))
            {
               JOptionPane.showMessageDialog(null, 
                                "Error reading " +
                                filename, "Error",
                                JOptionPane.ERROR_MESSAGE);
            }
         }
      }

      /**
         The openFile method opens the file specified by
         filename and reads its contents into the text
         area. The method returns true if the file was
         opened and read successfully, or false if an
         error occurred.
         @param filename The name of the file to open.
      */

      private boolean openFile(String filename)
      {
         boolean success;
         String inputLine, editorString = "";
         FileReader freader;
         BufferedReader inputFile;

         try
         {
            // Open the file.
            freader = new FileReader(filename);
            inputFile = new BufferedReader(freader);

            // Read the file contents into the editor.
            inputLine = inputFile.readLine();
            
            if (inputLine != null)
            {
               editorString = editorString +
                              inputLine ;
               inputLine = inputFile.readLine();
            }
            
            while (inputLine != null)
            {
               editorString =  editorString + "\n" +
                              inputLine  ;
               inputLine = inputFile.readLine();
            }
            editorText.setText(editorString);
            

            // Close the file.
            inputFile.close();  

            // Indicate that everything went OK.
            success = true;
            
            //set title when open a file
            setTitle("Text Editor-"+filename);
         }
         catch (IOException e)
         {
            // Something went wrong.
            success = false;
         }

         if(success==true)//if the file has been successfully opened, resets the hasFileChangedSinceSave boolean
         { hasFileChangedSinceSave=false;}          
         
         // Return our status.
         return success;
      }
   }

   /**
      Private inner class that handles the event that
      is generated when the user selects Save or Save
      As from the file menu.
   */

   private class SaveListener implements ActionListener
   {
      public void actionPerformed(ActionEvent e)
      {
         int chooserStatus;

         // If the user selected Save As, or the contents 
         // of the editor have not been saved, use a file
         // chooser to get the file name. Otherwise, save
         // the file under the current file name.

         if (e.getActionCommand() == "Save As" || 
             filename == null)
         {
            JFileChooser chooser = new JFileChooser();
            FileNameExtensionFilter text= new FileNameExtensionFilter("Text File (*.txt)", "txt" );
            chooser.addChoosableFileFilter(text);
            chooser.setFileFilter(text);
            chooserStatus = chooser.showSaveDialog(null);
            if (chooserStatus == JFileChooser.APPROVE_OPTION)
            {
               // Get a reference to the selected file.
               File selectedFile =
                             chooser.getSelectedFile();
               String extension="";
               if(chooser.getFileFilter().getDescription().equals("Text File (*.txt)"))
            	   extension=".txt";
               // Get the path of the selected file.
               filename = selectedFile.getPath()+extension; // Save file error only focuses when the user chooses save.               
            }
         }
         // If the user chooses Cancel, no message is appear.
         // The user saves the contents for the current opening file
         // In the condition, the first one is for save a current file, 
         // and the second is for save as or save a new file
         if((e.getActionCommand() == "Save" && filename != null)|| filename != null){
        	// Save the file. 
        	 if (!saveFile(filename))       	
             {
                JOptionPane.showMessageDialog(null,
                                   "Error saving " +
                                   filename, 
                                   "Error",
                                   JOptionPane.ERROR_MESSAGE);
             }
         }
      }

      /**
         The saveFile method saves the contents of the
         text area to a file. The method returns true if
         the file was saved successfully, or false if an
         error occurred.
         @param filename The name of the file.
         @return true if successful, false otherwise.
      */

      private boolean saveFile(String filename)
      {
               
    	  boolean success;
         String editorString;
         FileWriter fwriter;
         PrintWriter outputFile;
         
         

         try
         {
            // Open the file.
            fwriter = new FileWriter(filename);
            outputFile = new PrintWriter(fwriter);

            // Write the contents of the text area
            // to the file.
            editorString = editorText.getText();
            outputFile.print(editorString);

            // Close the file.
            outputFile.close();

            // Indicate that everything went OK.
            success = true;
            
            // Set a title when saving a file.
            setTitle("Text Editor-"+filename+"-saved");
         }
         catch (IOException e)
         {
            // Something went wrong.
             success = false;
         }

         
         
         if(success==true)//if the file has been successfully saved, resets the hasFileChangedSinceSave boolean
         { hasFileChangedSinceSave=false;} 
         
         return success; // Return our status.
      }
   }

   /**
      Private inner class that handles the event that
      is generated when the user selects Exit from
      the file menu.
   */

   private class ExitListener implements ActionListener
   {
      public void actionPerformed(ActionEvent e)
      {
    	  
         System.exit(0);
      }
   }

   /**
   Private inner class that handles the event that
   is generated when the user selects an item from
   the font menu.
*/

private class TextFormatListener implements ActionListener
{
   public void actionPerformed(ActionEvent e)
   {
      if(e.getActionCommand().equals("Word Wrap")){
      if (wordWrap.isSelected())
        wrapped=true;
      else {
    	  wrapped=false;
      }
      wordWrap();
      }
      else if(e.getActionCommand().equals("Show Annotations")){
    	 
    	  if (showAnnotations.isSelected()){
    		  System.out.println("Show Annotations");    		 
    	       hideAnnotations(false);
    	  }
    	  else{
    		 
    		  hideAnnotations(true); 
    		  System.out.println("Hide Annotations");
    	  }
    	     
      }
   }
}

   /**
      Private inner class that handles the event that
      is generated when the user selects an item from
      the font menu.
   */

   private class FontListener implements ActionListener
   {
      public void actionPerformed(ActionEvent e)
      {
    	 // Get the selected text
    	    int start = editorText.getSelectionStart();
    	    int end = editorText.getSelectionEnd();
    	    
    	    StyledDocument doc=editorText.getStyledDocument();
    	

    	    if (start == end) { // No selection, cursor position.
    	        return;
    	    }
    	    if (start > end) { // Backwards selection?
    	        int life = start;
    	        start = end;
    	        end = life;
    	    } 
    	    Style style = editorText.addStyle("annotated", null);
    	    
    	    
    	    
    	    
         // Get the current font.
         Font textFont = editorText.getFont();

         // Retrieve the font name and size.
         String fontName = textFont.getName();
         int fontSize = textFont.getSize();

         // Start with plain style.
         int fontStyle = Font.PLAIN;

         // Determine which font is selected.
         if (monoItem.isSelected())
            fontName = "Monospaced";
         else if (serifItem.isSelected())
            fontName = "Serif";
         else if (sansSerifItem.isSelected())
            fontName = "SansSerif";

         // Determine whether italic is selected.
         if(e.getActionCommand().equals("italic"))
        	 StyleConstants.setItalic(style, italicItem.isSelected());
         
         // Determine whether bold is selected.
       
         
         if(e.getActionCommand().equals("bold"))
        	 StyleConstants.setBold(style, boldItem.isSelected());

         // Set the font as selected.
         
         doc.setCharacterAttributes(start, end - start, style, false);
         
         editorText.setFont(new Font(fontName, 
                                fontStyle, fontSize));
      }
   }
   
   /**
   Private inner class that handles the event that
   the text is changed in any way. 
*/
   private class textChangeListener implements DocumentListener{ //used to see if any changes have been made to the text

	@Override
	public void changedUpdate(DocumentEvent arg0) {//activates if the format of the text has changed

		hasFileChangedSinceSave=true;
		// Add * end of the title to note that the contents changed.
		if(filename!=null)
			setTitle("Text Editor-"+filename+"*");		
			else
				setTitle("Text Editor-"+"untitled"+"*");	
	}

	@Override
	public void insertUpdate(DocumentEvent arg0) {//activates if anything has been added to the text
		hasFileChangedSinceSave=true;
		// Add * end of the title to note that the contents changed.
		if(filename!=null)
			setTitle("Text Editor-"+filename+"*");		
			else
				setTitle("Text Editor-"+"untitled"+"*");	
	}

	@Override
	public void removeUpdate(DocumentEvent arg0) {//activates if anything has been removed from the text
		hasFileChangedSinceSave=true;
		// Add * end of the title to note that the contents changed.
		if(filename!=null)
		setTitle("Text Editor-"+filename+"*");		
		else
			setTitle("Text Editor-"+"untitled"+"*");	
	}

   }
   
   /**
   Private inner class that handles the event that
   is generated when the user presses control-s on the keyboard
*/
   private class ctrlS extends AbstractAction{

	@Override
	public void actionPerformed(ActionEvent arg0) {


		  new SaveListener().actionPerformed(new ActionEvent("saving", 0, null));

	}

   }
   /**
   Private inner class that handles the event that
   is generated when the user presses control-b on the keyboard
*/
   private class ctrlB extends AbstractAction{

		@Override
		public void actionPerformed(ActionEvent arg0) {


			
			boldItem.doClick();; //calls fontListener so that the font is updated

		}

	   }
   
   private class ctrlI extends AbstractAction{

		@Override
		public void actionPerformed(ActionEvent arg0) {

			italicItem.doClick();; //calls fontListener so that the font is updated
		}
	   }
   
   /**
   Private inner class that handles the event that
   is generated when the user clicks on the "show ontology"
   menu button
*/
   private class ListListener implements ActionListener
   {
      public void actionPerformed(ActionEvent e)
      {
         
    	  if(leftList!=null){
    		    
    		  if (showLeftList.isSelected()){
    			  leftList.setVisible(true);       
    		  }
    		  
    		  else{
    			  leftList.setVisible(false);    
    		  }
    	  }
   }
   }
   
   /**
      main method
   */
   
   public static void main(String[] args)
   {
      TextEditor te = new TextEditor();
   }
}