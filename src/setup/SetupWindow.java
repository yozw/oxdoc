import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;

public class SetupWindow implements ActionListener {
   private final String actionWrite = "write", actionExit = "exit";
   private static JFrame frame;
   private JTextField editLatex;
   private JTextField editDvipng;
   private JRadioButton radioLatex;
   private JRadioButton radioMathML;
   private JRadioButton radioPlainText;
   private JCheckBox chkShowInternals;
   private JCheckBox chkEnableIcons;
   private JButton writeButton;

   public SetupWindow() {
      //Create and set up the window.
      frame = new JFrame(OxDoc.ProductName + " " + Constants.VERSION + " setup");
      frame.setResizable(false);
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

      JPanel mainPanel = new JPanel();  
      mainPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
      mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
      frame.getContentPane().add(mainPanel);

      try {
         UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
      } catch (Exception E) { }

      mainPanel.add(setupOptionPanel());
      mainPanel.add(setupButtonPanel());
      mainPanel.add(setupCopyright());

      //Display the window.
      frame.pack();

      Dimension dim = frame.getToolkit().getScreenSize();
      Rectangle abounds = frame.getBounds();
      frame.setLocation((dim.width - abounds.width) / 2, (dim.height - abounds.height) / 2);
   }

   private JComponent setupCopyright() {
      JLabel copyrightLabel = new JLabel();
      copyrightLabel.setText(OxDoc.CopyrightNotice);
      return copyrightLabel;
   }

   private GridBagConstraints gridBagConstraints(int x, int y)
   {
      return gridBagConstraints(x, y, 1);
   }

   private GridBagConstraints gridBagConstraints(int x, int y, int w)
   {
      GridBagConstraints c = new GridBagConstraints();
      c.fill = GridBagConstraints.HORIZONTAL;
      c.weightx = 0.5;
      c.weighty = 0.5;
      c.gridx = x;
      c.gridy = y;
      c.gridwidth = w;
      return c;
   }

   private String latexExecutable()
   {
      switch (Os.getOperatingSystem()) {
         case Win32:
            return "latex.exe";
         case Linux:
            return "latex";
      }
      return "latex executable";
   }

   private String dvipngExecutable()
   {
      switch (Os.getOperatingSystem()) {
         case Win32:
            return "dvipng.exe";
         case Linux:
            return "dvipng";
      }
      return "dvipng executable";
   }

   private JComponent setupOptionPanel() {
      GridBagLayout layout = new GridBagLayout();
      JPanel optionPanel = new JPanel(layout);

      JLabel labelLatex = new JLabel("Location of " + latexExecutable() + ":");
      JLabel labelDvipng = new JLabel("Location of " + dvipngExecutable() + ":");
      JLabel labelFormulas = new JLabel("Default setting for formula generation:");

      editLatex = new JTextField(20);
      editDvipng = new JTextField(20);
      radioLatex = new JRadioButton("LaTeX", true);
      radioMathML = new JRadioButton("MathML", false);
      radioPlainText = new JRadioButton("Plain text", false);
      ButtonGroup group = new ButtonGroup();
      group.add(radioLatex);
      group.add(radioMathML);
      group.add(radioPlainText);

      switch (Os.getOperatingSystem()) {
         case Win32:
            editLatex.setText("c:\\texmf\\miktex\\bin\\latex.exe");
            editDvipng.setText("c:\\texmf\\miktex\\bin\\dvipng.exe");
            break;
         case Linux:
            editLatex.setText("/usr/bin/latex");
            editDvipng.setText("/usr/bin/dvipng");
            break;
      }

      optionPanel.add(labelLatex, gridBagConstraints(0, 0));
      optionPanel.add(editLatex, gridBagConstraints(1, 0));

      optionPanel.add(labelDvipng, gridBagConstraints(0, 1));
      optionPanel.add(editDvipng, gridBagConstraints(1, 1));

      optionPanel.add(labelFormulas, gridBagConstraints(0, 2));
      optionPanel.add(radioLatex, gridBagConstraints(1, 2));
      optionPanel.add(radioMathML, gridBagConstraints(1, 3));
      optionPanel.add(radioPlainText, gridBagConstraints(1, 4));

      writeButton = new JButton("Write configuration");
      writeButton.setActionCommand(actionWrite);
      writeButton.addActionListener(this);

      optionPanel.add(writeButton, gridBagConstraints(1, 12));

      return optionPanel;
   }

   private JComponent setupButtonPanel() {
      JPanel buttonPanel = new JPanel(new GridLayout(0, 2));
      buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));
      buttonPanel.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));

      frame.getRootPane().setDefaultButton(writeButton);

      return buttonPanel;
   }

   private void writeOxDocXml()
   {
      try {
         File latexFile = new File(getLatex());
         File dvipngFile = new File(getDvipng());
         if (!latexFile.exists()) 
         {
            Object[] options = { "Cancel", "Ignore" };
            int result = JOptionPane.showOptionDialog(frame, "Warning: the file " + latexFile + " does not exist.", 
                         "File does not exist",  
                         JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE,
                         null, options, options[0]);
            if (result != 1) return;
         }
         if (!dvipngFile.exists())
         { 
            Object[] options = { "Cancel", "Ignore" };
            int result = JOptionPane.showOptionDialog(frame, "Warning: the file " + dvipngFile + " does not exist.",  
                         "File does not exist",  
                         JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE,
                         null, options, options[0]);
            if (result != 1) return;
         }

         File oxdocXmlFile = new File(Config.userHomeConfigFile());
         File directory = oxdocXmlFile.getParentFile();

         if (oxdocXmlFile.exists()) {
            Object[] options = { "Ok", "Cancel" };
            int result = JOptionPane.showOptionDialog(frame, "The file " + oxdocXmlFile + " already exists.\n" + 
                         "Click OK to overwrite the file.", "File exists",
                         JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE,
                         null, options, options[1]);
            if (result != 0) 
                return;
         }

         directory.mkdirs();

         Writer output = new BufferedWriter(new FileWriter(oxdocXmlFile));
         output.write("<oxdoc>\n");
         output.write("   <option name=\"latex\" value=\"" + getLatex() + "\" />\n");
         output.write("   <option name=\"dvipng\" value=\"" + getDvipng() + "\" />\n");
         String formulas = null;
         if (radioLatex.isSelected())
            formulas = "latex";
         else if (radioMathML.isSelected())
            formulas = "mathml";
         else if (radioPlainText.isSelected())
            formulas = "plain";
         if (formulas != null);
            output.write("   <option name=\"formulas\" value=\"" + formulas + "\" />\n");

         output.write("</oxdoc>\n");
         output.close();

         Object[] options = { "No", "Yes" };
         int result = JOptionPane.showOptionDialog(frame, "Successfully wrote configuration into file " + oxdocXmlFile + 
               ".\nDo you want to exit this setup program?", "Success", 
               JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE,
               null, options, options[1]);
         if (result == 1) 
             System.exit(1);
      } catch (Exception E) {
        showException(E);
      }
   } 

   public void actionPerformed(ActionEvent e) {
      if (e.getActionCommand().equals(actionWrite))
         writeOxDocXml();
//         System.exit(0);
   }

   private String extractPathName(String spec) {
      int idx = spec.lastIndexOf(File.separatorChar);
      if (idx == -1)
         return "";

      return spec.substring(0, idx);
   }

   private String extractFileName(String spec) {
      int idx = spec.lastIndexOf(File.separatorChar);
      if (idx == -1)
         return spec;

      return spec.substring(idx + 1, spec.length());
   }

   private String getLatex() 
   {
      return new File(editLatex.getText()).getAbsolutePath().toString();
   }

   private String getDvipng() 
   {
      return new File(editDvipng.getText()).getAbsolutePath().toString();
   }

   public static void showException(Exception E) {
      JOptionPane.showMessageDialog(frame, E.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
   }

   public static void showAbout() {
      JOptionPane.showMessageDialog(frame, OxDoc.aboutText());
   }

   public void show() {
      frame.setVisible(true);
   }
}
