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
import java.awt.FlowLayout;
import javax.swing.*;

public class JFontChooser extends JComponent{
    private String fontName;
    private String styleName;
    private int style;
    private int size;
    private Color color;
    //used for cancel button
    private String oldFontName;
    private String oldStyleName;
    private int oldStyle;
    private int oldSize;
    private Color oldColor;
    
    public JFontChooser() {
        // only matters if setDefault() not called
        fontName = "Arial";
        styleName = "Bold";
        style = Font.BOLD;
        size = 18;
        color = Color.BLACK;
    }

    public boolean showDialog(JFrame parent){
        /* create dialog */
        // dialog will be modal since we don't want to go to parent frame while
        // changing font and color
        JDialog jdlg = new JDialog(parent,"Font",true);
        jdlg.setLayout(new FlowLayout());
        jdlg.setSize(500,450);
        jdlg.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        // don't let dialog to resize
        jdlg.setResizable(false);
        
        //save font for cancel button
        oldFontName = fontName;
        oldStyleName = styleName;
        oldStyle = style;
        oldSize = size;
        oldColor = color;
        
        /* create fontSP components */
        // get all fonts and put it in an array for fontList
        String[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        JList fontList = new JList(fonts);
        fontList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        fontList.setCellRenderer(new DefaultListCellRenderer() {
            JLabel label;
            public Component getListCellRendererComponent(JList list, Object value,
                int index, boolean isSelected, boolean cellHasFocus) {
            Component component = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            label = (JLabel)component;
            label.setFont(new Font(label.getText(),Font.BOLD,14));
            return component;
            }
        });
        /* put the font styles in an array and their 
        // names in another array to be shown on the list */    
        Integer[] styles = {Font.PLAIN,Font.BOLD,Font.ITALIC,
                                                 Font.BOLD + Font.ITALIC};
        String[] styleNames = {"Plain","Bold","Italic","Bold Italic"};
        JList styleList = new JList(styleNames);
        styleList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        styleList.setCellRenderer(new DefaultListCellRenderer() {
            JLabel label;
            public Component getListCellRendererComponent(JList list, Object value,
                int index, boolean isSelected, boolean cellHasFocus) {
            Component component = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            label = (JLabel)component;
            if (label.getText().equals("Plain"))
                label.setFont(new Font("Arial",Font.PLAIN,14));
            else if (label.getText().equals("Bold"))
                label.setFont(new Font("Arial",Font.BOLD,14));
            else if (label.getText().equals("Italic"))
                label.setFont(new Font("Italic",Font.ITALIC,14));
            else
                label.setFont(new Font("Arial",Font.BOLD + Font.ITALIC,14));
            return component;
            }
        });
        // put all possible font sizes in he list
        Integer[] sizes = {8,9,10,11,12,14,16,18,20,22,24,28,36,48,72};
        JList sizeList = new JList(sizes);
        sizeList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        /* create dialog components */ 
        // creates label,textfield,and sample text
        JLabel label = new JLabel("Sample text:");
        JTextField textField = new JTextField("Sample Text",20);
        label.setLabelFor(textField);
        label.setDisplayedMnemonic('S');
        JPanel textPanel = new JPanel();
        
        // adds all components into a panel
        textPanel.add(label);
        textPanel.add(textField);

        // create sample text to test fonts
        JLabel sampleText = new JLabel(textField.getText());
        
        sampleText.setFont(new Font(fontName,style,size));
        sampleText.setForeground(color);
        /* add key listener for text field */        
        textField.addKeyListener(new KeyAdapter(){
            public void keyPressed(KeyEvent ke) {
                sampleText.setText(textField.getText());
            }
            public void keyReleased(KeyEvent ke) {
                sampleText.setText(textField.getText());
            }
        });
        // add sample text to sample panel
        JPanel samplePanel = new JPanel();
        samplePanel.add(sampleText);
        samplePanel.setBackground(Color.WHITE);
        samplePanel.setPreferredSize(new Dimension(300,75));
        samplePanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        
        // add a ColorChooser to a button to change foreground color of text 
        // and add it to textPanel
        JButton button = new JButton("Color");
        button.setMnemonic('C');
        button.addActionListener(e ->{
            color = JColorChooser.showDialog(jdlg, 
                    "Change sample text color", Color.black);
            sampleText.setForeground(color);
        });
        textPanel.add(button);
        
        // scrollpane for fonts added into a fontPanel
        JLabel fontLabel = new JLabel("Font:");
        JTextField fontTF = new JTextField(fontName,20);
        fontTF.addKeyListener(new KeyAdapter(){
            public void keyPressed(KeyEvent ke) {
                fontLoop(fonts,fontList,fontTF);
            }
            public void keyReleased(KeyEvent ke) {
                fontLoop(fonts,fontList,fontTF);
            }
        });
        JScrollPane fontSP = new JScrollPane(fontList);
        fontSP.setPreferredSize(new Dimension(200,150));
        fontList.setSelectedValue(fontName, true);
        fontLabel.setLabelFor(fontTF);
        fontLabel.setDisplayedMnemonic('F');
        /* add selection listener for fontList */
        fontList.addListSelectionListener(e -> {
            // set text to new fontName 
            fontName = fonts[fontList.getSelectedIndex()];
            fontTF.setText(fontName);
            sampleText.setFont(new Font(fontName,style,size));
        });
        JPanel fontPanel = new JPanel();
        fontPanel.add(fontLabel);
        fontPanel.add(fontTF);
        fontPanel.add(fontSP);        
        // scrollpane for styleNames added into a stylePanel
        JLabel styleLabel = new JLabel("Font Style:");
        JTextField styleTF = new JTextField(styleName,10);
        styleTF.addKeyListener(new KeyAdapter(){
            public void keyPressed(KeyEvent ke) {
                styleLoop(styleNames,styleList,styleTF);
            }
            public void keyReleased(KeyEvent ke) {
                styleLoop(styleNames,styleList,styleTF);
            }
        });
        JScrollPane styleSP = new JScrollPane(styleList);
        styleSP.setPreferredSize(new Dimension(130,100));
        styleList.setSelectedValue(styleName, true);
        styleLabel.setLabelFor(styleTF);
        styleLabel.setDisplayedMnemonic('y');
        /* add selection listener for stylelist */
        styleList.addListSelectionListener(e -> {
            // set text to new style
            style = styles[styleList.getSelectedIndex()];
            styleName = styleNames[styleList.getSelectedIndex()];
            styleTF.setText(styleName);
            sampleText.setFont(new Font(fontName,style,size));
        });
        JPanel stylePanel = new JPanel();
        stylePanel.add(styleLabel);
        stylePanel.add(styleTF);
        stylePanel.add(styleSP);
        
        // scrollpane for sizes added into a sizePanel
        JLabel sizeLabel = new JLabel("Size:");
        JTextField sizeTF = new JTextField(Integer.toString(size),3);
        sizeTF.addKeyListener(new KeyAdapter(){
            public void keyPressed(KeyEvent ke) {
                sizeLoop(sizes,sizeList,sizeTF);
            }
            public void keyReleased(KeyEvent ke) {
                sizeLoop(sizes,sizeList,sizeTF);
            }
        });
        JScrollPane sizeSP = new JScrollPane(sizeList);
        sizeSP.setPreferredSize(new Dimension(50,100));
        sizeList.setSelectedValue(size, true);
        sizeLabel.setLabelFor(sizeTF);
        sizeLabel.setDisplayedMnemonic('z');
        /* add selection listener for stylelist */
        sizeList.addListSelectionListener(e -> {
            // set text to new size
            size = sizes[sizeList.getSelectedIndex()];
            sizeTF.setText(Integer.toString(size));
            sampleText.setFont(new Font(fontName,style,size));
        });
        JPanel sizePanel = new JPanel();
        sizePanel.add(sizeLabel);
        sizePanel.add(sizeTF);
        sizePanel.add(sizeSP);
        
        // add ok and cancel buttons onto a button panel
        // ok saves font, cancel stops any changes
        JButton ok = new JButton("OK");
        ok.setPreferredSize(new Dimension(70,25));
        ok.setMnemonic('O');
        jdlg.getRootPane().setDefaultButton(ok);
        ok.addActionListener(e -> jdlg.setVisible(false));
        JButton cancel = new JButton("Cancel");
        cancel.setMnemonic('a');
        cancel.addActionListener(e -> {
            fontName = oldFontName;
            styleName = oldStyleName;
            style = oldStyle;
            size = oldSize;
            color = oldColor;
            jdlg.setVisible(false);
        });
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(ok);
        buttonPanel.add(cancel);
        
        /* add dialog components */
        jdlg.add(textPanel);
        jdlg.add(fontPanel);
        jdlg.add(stylePanel);
        jdlg.add(sizePanel);
        jdlg.add(samplePanel);
        jdlg.add(buttonPanel);
        jdlg.setLocationRelativeTo(parent);
        jdlg.setVisible(true);
        return true;
    } // end showDialog    
    
    public Font getFont(){
        return new Font(this.fontName,this.style,this.size);
    } // end getFont
    
    public Color getColor() {
        return this.color;
    } // end getColor
    
    public void setDefault(Font font){
        this.fontName = font.getName();
        this.style = font.getStyle();
        switch (style) {
            case 0:
                this.styleName = "Plain";
                break;
            case 1:
                this.styleName = "Bold";
                break;
            case 2:
                this.styleName = "Italic";
                break;
            default:
                this.styleName = "Bold Italic";
                break;
        }
        this.size = font.getSize();
    } // end setDefault(font)
    
    public void setDefault(Color color) {
        this.color = color;
    } // end setDefault(color)
    
    private void fontLoop(String[] fonts, JList fontList, JTextField fontTF) {
        for (int i = 0; i < fonts.length; i++) {
            if (fonts[i].toLowerCase().equals(fontTF.getText().toLowerCase())) {
                fontList.setSelectedValue(fonts[i], true);
                break;
            } else if (fonts[i].toLowerCase().startsWith(fontTF.getText().toLowerCase())) {
                // offset list by 6 cells so cell starts at top instead of bottom
                fontList.ensureIndexIsVisible(i + 6);
                break;
            }
        }
    } // end fontLoop
    
    private void styleLoop(String[] styleNames, JList styleList, JTextField styleTF) {
        for (int i = 0; i < styleNames.length; i++) {
            if (styleNames[i].toLowerCase().equals(styleTF.getText().toLowerCase())) {
                // only 4 styles, so no need to scroll list for starts with
                styleList.setSelectedValue(styleNames[i], false);
                break;
            }
        }
    } // end styleLoop
        
    private void sizeLoop(Integer[] sizes, JList sizeList, JTextField sizeTF) {
        for (int i = 0; i < sizes.length; i++) {
            if (sizes[i].toString().equals(sizeTF.getText())) {
                sizeList.setSelectedValue(sizes[i], true);
                sizeTF.selectAll();
                break;
            } else if (sizes[i].toString().startsWith(sizeTF.getText())) {
                sizeList.ensureIndexIsVisible(i);
                break;
            }
        }
    } // end sizeLoop
} // end JFontChooser
