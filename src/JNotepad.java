//
//    Name:         Trinh, Michael
//    Project:      3
//    Due:          March 12,2018
//    Course:       cs-245-01-w18
//
//    Description:
//                  Creates a working notepad similar to the one on Windows
import java.awt.*;
import java.awt.event.*;
import java.awt.GridLayout;
import java.io.*;
import java.text.*;
import java.util.Date;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.filechooser.FileFilter;

public class JNotepad {
    private boolean saved;
    private int result;
    private BufferedReader contentRead;
    private DateFormat df;
    private File fileSelected;
    private FileReader dataRead;
    private Font defaultFont;
    private JFileChooser jfc;
    private JFontChooser fontChooser;
    private JFrame frame;
    private JTextArea textArea;
    private String filePath;
    private String fileName;
    // dialog variables
    private JCheckBox matchCase;  
    private JDialog aboutDialog;
    private JDialog findDialog;
    private JButton findNext;
    private JRadioButton up;
    private JRadioButton down;
    private String text;
    
    JNotepad() {
        // initialize global variables
        filePath = "Untitled";
        saved = true; // initialize to saved
        fontChooser = new JFontChooser();
        defaultFont = new Font("Courier New",Font.PLAIN,12);
        fontChooser.setDefault(defaultFont);
        fontChooser.setDefault(Color.BLACK);
        df = new SimpleDateFormat("hh:mm a MM/dd/yyy");
        jfc = new JFileChooser();
        createAboutDialog();
        createFindDialog();
        // add file filters to file chooser
        jfc.addChoosableFileFilter(new fileFilter(".java","Java Files"));
        jfc.setFileFilter(new fileFilter(".txt","Text Documents"));
        
        // create frame
        frame = new JFrame("Untitled - Notepad");
        frame.setLayout(new GridLayout(1,1));
        frame.setSize(1000,1000);
        frame.setIconImage(new ImageIcon("JNotepad.png").getImage());
        // ask if you want to save changes before closing
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we){
                exit();
            }
        });
        // make text area of notepad onto scroll pane
        textArea = new JTextArea(20,75);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setFont(defaultFont);
        textArea.setForeground(Color.BLACK);
        // set save status to unsaved when changes are made to the notepad
        textArea.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                saved = false;
            }
            public void removeUpdate(DocumentEvent e) {
                saved = false;
            }
            public void changedUpdate(DocumentEvent e) {
                saved = false;
            }
        });
        
        // create Scroll Pane to hold text area
        JScrollPane scrollPane = new JScrollPane(textArea);
        frame.add(scrollPane);
        
        // make menubar and menus + mnemonics
        JMenuBar jmb = new JMenuBar();
        JMenu file = new JMenu("File");
        file.setMnemonic('F');
        jmb.add(file);
        JMenu edit = new JMenu("Edit");
        edit.setMnemonic('E');
        jmb.add(edit);
        JMenu format = new JMenu("Format");
        format.setMnemonic('o');
        jmb.add(format);
        JMenu view = new JMenu("View");
        view.setMnemonic('V');
        jmb.add(view);
        JMenu help = new JMenu("Help");
        help.setMnemonic('H');
        jmb.add(help);
        frame.setJMenuBar(jmb);
        
        /* make File menu items */
        
        // new: clears all text; if unsaved, open save dialog 
        JMenuItem newFile = new JMenuItem("New",'N');
        newFile.setAccelerator(KeyStroke.getKeyStroke("control N"));
        newFile.addActionListener(e-> {
            if (!saved) {
                if(closeSave()) {
                    textArea.setText("");
                    frame.setTitle("Untitled - Notepad");
                }
            } else {
                textArea.setText("");
                frame.setTitle("Untitled - Notepad");
            }
        });
        file.add(newFile);
        // open: opens a file and replace current contents with opened contents
        JMenuItem openFile = new JMenuItem("Open...");
        openFile.setAccelerator(KeyStroke.getKeyStroke("control O"));
        openFile.addActionListener(e -> {
            if (!saved) {
                if (closeSave()) open();                 
            }
            else open();
        });    
        file.add(openFile);
        // save
        JMenuItem saveFile = new JMenuItem("Save");
        saveFile.setAccelerator(KeyStroke.getKeyStroke("control S"));
        saveFile.addActionListener(e->{
            if (fileSelected == null || !fileSelected.exists()) saveAs();
            else save();
        });
        file.add(saveFile);
        // save as
        JMenuItem saveAsFile = new JMenuItem("Save as...");
        saveAsFile.addActionListener(e -> {
            saveAs();
        });
        file.add(saveAsFile);
        file.addSeparator();
        // page setup
        JMenuItem pageSetupFile = new JMenuItem("Page Setup...",'u');
        pageSetupFile.setEnabled(false);
        file.add(pageSetupFile);
        // print
        JMenuItem printFile = new JMenuItem("Print...");
        printFile.setAccelerator(KeyStroke.getKeyStroke("control P"));
        printFile.setEnabled(false);
        file.add(printFile);    
        file.addSeparator();
        // exit
        JMenuItem exitFile = new JMenuItem("Exit",'x');
        exitFile.addActionListener(e->exit());
        file.add(exitFile);
        
        /* make Edit menu items */
        
        // undo
        JMenuItem undoEdit = new JMenuItem("Undo");
        undoEdit.setEnabled(false);
        edit.add(undoEdit);
        edit.addSeparator();
        // cut
        JMenuItem cutEdit = new JMenuItem("Cut");
        cutEdit.setAccelerator(KeyStroke.getKeyStroke("control X"));
        cutEdit.addActionListener(e->textArea.cut());
        edit.add(cutEdit);
        // copy
        JMenuItem copyEdit = new JMenuItem("Copy");
        copyEdit.setAccelerator(KeyStroke.getKeyStroke("control C"));
        copyEdit.addActionListener(e->textArea.copy());
        edit.add(copyEdit);
        // paste
        JMenuItem pasteEdit = new JMenuItem("Paste");
        pasteEdit.setAccelerator(KeyStroke.getKeyStroke("control V"));
        copyEdit.addActionListener(e->textArea.paste());
        edit.add(pasteEdit);
        // delete
        JMenuItem deleteEdit = new JMenuItem("Delete");
        deleteEdit.setAccelerator(KeyStroke.getKeyStroke("DELETE"));
        copyEdit.addActionListener(e->textArea.replaceSelection(""));
        edit.add(deleteEdit);
        edit.addSeparator();
        // find
        JMenuItem findEdit = new JMenuItem("Find...");
        findEdit.setAccelerator(KeyStroke.getKeyStroke("control F"));
        findEdit.addActionListener(e->showFindDialog());
        edit.add(findEdit);
        // find next
        JMenuItem findNextEdit = new JMenuItem("Find Next");
        findNextEdit.addActionListener(e->findNext.doClick());
        edit.add(findNextEdit);
        // replace
        JMenuItem replaceEdit = new JMenuItem("Replace...");
        replaceEdit.setAccelerator(KeyStroke.getKeyStroke("control H"));
        replaceEdit.setEnabled(false);
        edit.add(replaceEdit);
        // go to
        JMenuItem goToEdit = new JMenuItem("Go To...");
        goToEdit.setAccelerator(KeyStroke.getKeyStroke("control G"));
        goToEdit.setEnabled(false);
        edit.add(goToEdit);
        edit.addSeparator();
        // select all
        JMenuItem selectAllEdit = new JMenuItem("Select All");
        selectAllEdit.setAccelerator(KeyStroke.getKeyStroke("control A"));
        selectAllEdit.addActionListener(e->textArea.selectAll());
        // time/date
        edit.add(selectAllEdit);
        JMenuItem timeEdit = new JMenuItem("Time/Date");
        timeEdit.setAccelerator(KeyStroke.getKeyStroke("F5"));
        timeEdit.addActionListener(e-> textArea.insert(df.format(new Date()),
                textArea.getSelectionStart()));
        edit.add(timeEdit);
     
        /* make Format menu items */        
        
        // word wrap
        JCheckBoxMenuItem wordWrapFormat = new JCheckBoxMenuItem("Word Wrap",true);
        wordWrapFormat.setMnemonic('W');
        wordWrapFormat.addItemListener(e -> {
            if (wordWrapFormat.isSelected()) {
                textArea.setLineWrap(true);
                textArea.setWrapStyleWord(true);
            }
            else {
                textArea.setLineWrap(false);
                textArea.setWrapStyleWord(false);
            }
        });
        format.add(wordWrapFormat);
        // font
        JMenuItem fontFormat = new JMenuItem("Font...",'F');
        fontFormat.addActionListener(e->{
            if (fontChooser.showDialog(frame)) {
                textArea.setFont(fontChooser.getFont());
                textArea.setForeground(fontChooser.getColor());
                textArea.setCaretColor(fontChooser.getColor());
            }
        });
        format.add(fontFormat);
        
        /* make View menu items */
        
        // status bar
        JMenuItem statusBarView = new JMenuItem("Status Bar",'S');
        statusBarView.setEnabled(false);
        view.add(statusBarView);
        
        /* make Help menu items */
        
        // view help
        JMenuItem viewHelpHelp = new JMenuItem("View Help",'H');
        viewHelpHelp.setEnabled(false);
        help.add(viewHelpHelp);
        help.addSeparator();
        // about JNotepad
        JMenuItem aboutJNotepadHelp = new JMenuItem("About JNotepad"); 
        aboutJNotepadHelp.addActionListener(e->showAboutDialog());
        help.add(aboutJNotepadHelp);
        
        // create popup menu
        JPopupMenu popUp = new JPopupMenu();
        JMenuItem cutPopUp = new JMenuItem("Cut");
        cutPopUp.addActionListener(e->textArea.cut());
        popUp.add(cutPopUp);
        JMenuItem copyPopUp = new JMenuItem("Copy");
        copyPopUp.addActionListener(e->textArea.copy());
        popUp.add(copyPopUp);
        JMenuItem pastePopUp = new JMenuItem("Paste");
        pastePopUp.addActionListener(e->textArea.paste());
        popUp.add(pastePopUp);
        textArea.addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent e){
                if (e.isPopupTrigger()) 
                    popUp.show(e.getComponent(), e.getX(), e.getY());
            }
        });
        // tidy,center, and show frame 
        frame.pack();
        frame.setLocationByPlatform(true);
        frame.setVisible(true);
    } // end constructor
 
    // create fileFilter that can contain any type of extension
    private class fileFilter extends FileFilter {
        private String extension;
        private String description;
        public fileFilter(String extension, String description) {
            this.extension = extension;
            this.description = description;
        }
        public boolean accept(File file){
            if(file.isDirectory())
                return true;
            return file.getName().endsWith(extension);
        }
        public String getDescription(){
            return description + " (*" + extension + ")";
        }
    } // end fileFilter 
    
    private void changeFile(){
        fileSelected = jfc.getSelectedFile();
        fileName = fileSelected.getName();
    } // end changeFile
    
    private void changeTitle() {
        fileName = fileName.substring(0,fileName.lastIndexOf('.'));
        frame.setTitle(fileName + " - Notepad");
    } // end changeTitle
    
    private boolean closeSave(){
        result = JOptionPane.showConfirmDialog(frame, "<html>Do you want to save changes to <br>"
                +filePath+"?", "Notepad",JOptionPane.YES_NO_CANCEL_OPTION);
        switch(result){
            case JOptionPane.YES_OPTION:
                if (fileSelected == null || !fileSelected.exists())
                    saveAs();
                else save();
                return saved;
            case JOptionPane.NO_OPTION:
                return true;
            case JOptionPane.CANCEL_OPTION:
                return false;
        }
        return false;
    } // end closeSave
    
    private void open() {
        jfc.setName("Open");
        result = jfc.showOpenDialog(frame);
        switch (result) {
            case JFileChooser.APPROVE_OPTION:
                while (true) {
                try {
                    changeFile();
                    dataRead = new FileReader(fileSelected);
                    contentRead = new BufferedReader(dataRead);
                    filePath = fileSelected.getAbsolutePath();
                    break;
                } catch (FileNotFoundException fnfe) { // exception if file not found 
                    JOptionPane.showMessageDialog(frame, "<html>" + fileSelected.getName()
                            + "<br>File not found.<br>Check the file name and "
                            + "try again.", "Open", JOptionPane.WARNING_MESSAGE);
                    result = jfc.showOpenDialog(frame);
                    if (result == JFileChooser.CANCEL_OPTION) break;
                } // end try-catch
                }
                try {
                    if (result == JFileChooser.CANCEL_OPTION) break;
                    textArea.read(contentRead, fileSelected);
                    dataRead.close();
                    contentRead.close();
                    changeTitle();
                    saved = true;
                    // add documentListener to new document
                    textArea.getDocument().addDocumentListener(new DocumentListener() {
                        public void insertUpdate(DocumentEvent e) {
                            saved = false;
                        }
                        public void removeUpdate(DocumentEvent e) {
                            saved = false;
                        }
                        public void changedUpdate(DocumentEvent e) {
                            saved = false;
                        }
                    });
                } catch (IOException ioe) {
                    JOptionPane.showMessageDialog(frame, "Error reading file",
                            "Open", JOptionPane.ERROR_MESSAGE);
                    break;
                }
        }
    } // end open
    
    private void save() {
        try {
            while (true){
                changeFile();
            // no autofill extension name
            // .txt default in all files, only .txt and .java filters
                if (!fileName.contains(".")) {
                    JOptionPane.showMessageDialog(frame,
                            "Add extension to file name: " + fileName,
                            "Save", JOptionPane.PLAIN_MESSAGE);
                    saved = false;
                    result = jfc.showOpenDialog(frame);
                    if (result == JFileChooser.CANCEL_OPTION)
                        break;
                } else {
                    try (FileWriter dataWritten = new FileWriter(fileSelected)) {
                        dataWritten.write(textArea.getText());
                        filePath = fileSelected.getAbsolutePath();
                    }
                    changeTitle();
                    saved = true;
                    break;
                }
            }
        } catch (IOException ioe) {
            JOptionPane.showMessageDialog(frame, "Error writing file",
                    "Open", JOptionPane.ERROR_MESSAGE);
        }
    } // end save
    
    private void saveAs(){
        while (true) {
            result = jfc.showSaveDialog(frame);
            if (result == JFileChooser.APPROVE_OPTION) {
                if (jfc.getSelectedFile().exists()) {
                    result = JOptionPane.showConfirmDialog(frame, "<html>" + jfc.getSelectedFile().getName()
                            + " already exists." + "<br>Do you want to replace it?",
                            "Confirm Save As", JOptionPane.YES_NO_OPTION);
                    if (result == JOptionPane.YES_OPTION) {
                        save();
                        break;
                    }
                } else {
                    save();
                    break;
                }
            }
            else break;
        }
    } // end saveAs
    
    private void createFindDialog() {
        // create dialog
        findDialog = new JDialog(frame,"Find",false);
        findDialog.setLayout(new FlowLayout());
        findDialog.setSize(new Dimension(350,130));
        findDialog.setResizable(false);
        findDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        // create components
        JLabel findWhat = new JLabel("Find what:");
        JTextField findText = new JTextField(10);
        findWhat.setLabelFor(findText);
        findWhat.setDisplayedMnemonic('n');
        JPanel findPanel = new JPanel();
        findPanel.add(findWhat);
        findPanel.add(findText);
        findNext = new JButton("Find Next");
        findDialog.getRootPane().setDefaultButton(findNext);
        matchCase = new JCheckBox("Match case",false);
        matchCase.setMnemonic('c');
        matchCase.setDisplayedMnemonicIndex(6);
        ButtonGroup bg = new ButtonGroup();
        up = new JRadioButton("Up");
        up.setMnemonic('U');
        down = new JRadioButton("Down",true);
        down.setMnemonic('D');
        bg.add(up);
        bg.add(down);
        JPanel dirPanel = new JPanel();
        dirPanel.add(up);
        dirPanel.add(down);
        dirPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(),"Direction"));
        JButton cancel = new JButton("Cancel");
        cancel.addActionListener(e->findDialog.dispose());
        findNext.addActionListener(e->{
            text = textArea.getText().substring(0, textArea.getText().length());
            if(!matchCase.isSelected())
                find(findText.getText().toLowerCase());
            else
                find(findText.getText());
        });
        // add components to dialog
        findDialog.add(findPanel);
        findDialog.add(findNext);
        findDialog.add(matchCase);
        findDialog.add(dirPanel);
        findDialog.add(cancel);
    } // end createFindDialog
    
    private void find(String key) {
        if (!matchCase.isSelected()) {
            String oldtext = text;
            text=text.toLowerCase();
            findBase(key);
            text = oldtext;
        }
        else findBase(key);
    } // end find
    
    private void findBase(String key) {
        // up
        if (up.isSelected()) {
            text = text.substring(0, textArea.getSelectionStart());
            if (text.contains(key))
                textArea.select(text.lastIndexOf(key),(text.lastIndexOf(key)+key.length()));
            else
                JOptionPane.showMessageDialog(findDialog, "Cannot find \"" + key
                        + "\"", "Notepad", JOptionPane.INFORMATION_MESSAGE);
        // down
        } else {
            text = text.substring(textArea.getSelectionEnd(),textArea.getText().length());
            if (text.contains(key))
                textArea.select(text.indexOf(key) + textArea.getSelectionEnd(),
                        text.indexOf(key)+key.length()+textArea.getSelectionEnd());
            else 
                JOptionPane.showMessageDialog(findDialog, "Cannot find \"" + key
                        + "\"", "Notepad", JOptionPane.INFORMATION_MESSAGE);
        }
    } //end findBase 
    
    private void showFindDialog() {
        findDialog.setLocationRelativeTo(frame);
        findDialog.setVisible(true);
    } // end showFindDialog
    
    private void createAboutDialog(){
                // create dialog
        aboutDialog = new JDialog(frame,"About Notepad",true);
        aboutDialog.setLayout(new FlowLayout());
        aboutDialog.setSize(new Dimension(150,100));
        aboutDialog.setResizable(false);
        aboutDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        JLabel copyright = new JLabel("(c) Michael Trinh");
        JButton ok = new JButton("OK");
        ok.addActionListener(e->aboutDialog.setVisible(false));
        aboutDialog.add(copyright);
        aboutDialog.add(ok);
    } // end createAboutDialog
    
    private void showAboutDialog(){
        aboutDialog.setLocationRelativeTo(frame);
        aboutDialog.setVisible(true);
    } // end showAboutDialog
    
    private void exit() {
        if (!saved) {
            if (closeSave()) {
                System.exit(0);
            }
        } else System.exit(0);
    } // end exit
    
    public static void main(String[] args) {
        System.out.println("M. Trinh's Notepad");
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new JNotepad();
            }
        });
    } // end main
} // end JNotepad
