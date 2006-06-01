import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;


public class MainWindow implements ActionListener {
   private final String actionRun = "run", actionExit = "exit", actionSaveBatch = "batch";
   private JFrame frame;
   private JTextField editDirectory, editFilenames, editOutputDir, editProjectName, editWindowTitle;
   private JCheckBox chkEnableLatex, chkEnableIcons;

   public MainWindow() {
      //Create and set up the window.
      frame = new JFrame(OxDoc.ProductName + " " + Constants.VERSION);
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

      JPanel mainPanel = new JPanel();  // new GridLayout(0, 1));
      mainPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
      mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
      frame.getContentPane().add(mainPanel);

      mainPanel.add(setupOptionPanel());
      mainPanel.add(setupButtonPanel());

      //Display the window.
      frame.pack();

      Dimension dim = frame.getToolkit().getScreenSize();
      Rectangle abounds = frame.getBounds();
      frame.setLocation((dim.width - abounds.width) / 2, (dim.height - abounds.height) / 2);
   }

   private JComponent setupOptionPanel() {
      JPanel optionPanel = new JPanel(new GridLayout(0, 2));
      optionPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

      JLabel labelDirectory   = new JLabel("Directory:");
      JLabel labelFilenames   = new JLabel("File specification:");
      JLabel labelOutputdir   = new JLabel("Output directory:");
      JLabel labelProjectName = new JLabel("Project name:");
      JLabel labelWindowTitle = new JLabel("Window title:");

      editDirectory   = new JTextField(30);
      editFilenames   = new JTextField(30);
      editOutputDir   = new JTextField(30);
      editProjectName = new JTextField(30);
      editWindowTitle = new JTextField(30);

      chkEnableLatex  = new JCheckBox("Enable LaTeX", true);
      chkEnableIcons  = new JCheckBox("Enable icons", true);

      editOutputDir.setText("doc/");
      editFilenames.setText("*.ox");

      optionPanel.add(labelDirectory);
      optionPanel.add(editDirectory);

      optionPanel.add(labelFilenames);
      optionPanel.add(editFilenames);

      optionPanel.add(labelOutputdir);
      optionPanel.add(editOutputDir);

      optionPanel.add(labelProjectName);
      optionPanel.add(editProjectName);

      optionPanel.add(labelWindowTitle);
      optionPanel.add(editWindowTitle);

      optionPanel.add(chkEnableLatex);
      optionPanel.add(chkEnableIcons);

      return optionPanel;
   }

   private JComponent setupButtonPanel() {
      JPanel buttonPanel = new JPanel(new GridLayout(0, 2));
      buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));

      JButton exitButton = new JButton("Exit");
      exitButton.setActionCommand(actionExit);
      exitButton.addActionListener(this);

      JButton runButton = new JButton("Run");
      runButton.setActionCommand(actionRun);
      runButton.addActionListener(this);

	   JButton batchButton = new JButton("Save batch file");
	   batchButton.setActionCommand(actionSaveBatch);
      batchButton.addActionListener(this);

      buttonPanel.add(Box.createHorizontalGlue());
      buttonPanel.add(batchButton);
      buttonPanel.add(Box.createRigidArea(new Dimension(8, 0)));
      buttonPanel.add(exitButton);
      buttonPanel.add(Box.createRigidArea(new Dimension(8, 0)));
      buttonPanel.add(runButton);

      frame.getRootPane().setDefaultButton(runButton);

      return buttonPanel;
   }

   public void actionPerformed(ActionEvent e) {
      if (e.getActionCommand().equals(actionExit))
         System.exit(0);
      if (e.getActionCommand().equals(actionRun))
         runOxdoc();
      if (e.getActionCommand().equals(actionSaveBatch))
         runSaveBatch();
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

   public void showException(Exception E) {
      JOptionPane.showMessageDialog(frame, E.getMessage());
   }

   public void runSaveBatch() {
      JFileChooser fc = new JFileChooser(new File("makedoc.bat"));    
      fc.showSaveDialog(frame);
      File selFile = fc.getSelectedFile();

      if (selFile == null)
      {
         return;
      }

      try
      {
         Writer output = new BufferedWriter(new FileWriter(selFile));
         output.write("@echo off\n");
         output.write("cd \"" + outputDir() + "\"\n");
         output.write("call oxdoc\n");
         output.close();
      }
      catch (Exception E)
      {
         showException(E);
      }
   }

   private File outputDir() {
      File f = new File(editOutputDir.getText());
      return f;
   }

   public void runOxdoc() {
      try {
         final CmdWindow cmd = new CmdWindow();
         cmd.show();

         Thread oxdocThread = new Thread() {
            public void run() {
               try {
                  OxDoc oxdoc = new OxDoc(cmd);

                  oxdoc.config.load();
                  oxdoc.config.setOption("outputdir", outputDir().getName());
                  oxdoc.config.validate();

                  oxdoc.addFiles(editFilenames.getText());
                  oxdoc.generateDocs();

                  cmd.writeMessage("\nExecution succesful", 0);
               } catch (Exception E) {
                  cmd.writeMessage("\nExecution failed: \n" + E.getMessage(), 0);
               }
            }
         };

         oxdocThread.start();
      } catch (Exception e) {
      }
   }

   public void show() {
      frame.setVisible(true);
   }
}