import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;


public class mainWindow implements ActionListener {
   private final String actionRun = "run";
   private final String actionExit = "exit";
   private JFrame frame;
   private JTextField editFilenames;
   private JTextField editOutputDir;

   public mainWindow() {
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

      JLabel labelFilenames = new JLabel("Filenames:");
      optionPanel.add(labelFilenames);

      editFilenames = new JTextField(15);
      editFilenames.setText("*.ox");
      optionPanel.add(editFilenames);

      JLabel labelOutputdir = new JLabel("Output directory:");
      optionPanel.add(labelOutputdir);

      editOutputDir = new JTextField(15);
      editOutputDir.setText("doc/");
      optionPanel.add(editOutputDir);

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

      buttonPanel.add(Box.createHorizontalGlue());
      buttonPanel.add(exitButton);
      buttonPanel.add(Box.createRigidArea(new Dimension(8, 0)));
      buttonPanel.add(runButton);

      return buttonPanel;
   }

   public void actionPerformed(ActionEvent e) {
      if (e.getActionCommand().equals(actionExit))
         System.exit(0);
      if (e.getActionCommand().equals(actionRun))
         runOxdoc();
   }

   private String extractPathName(String spec) {
      int idx = spec.lastIndexOf(File.separatorChar);
      if (idx == -1)
         return "";
      else

         return spec.substring(0, idx);
   }

   private String extractFileName(String spec) {
      int idx = spec.lastIndexOf(File.separatorChar);
      if (idx == -1)
         return spec;
      else

         return spec.substring(idx + 1, spec.length());
   }

   public void runOxdoc() {
      try {
         final cmdWindow cmd = new cmdWindow();
         cmd.show();

         Thread oxdocThread = new Thread() {
            public void run() {
               try {
                  OxDoc oxdoc = new OxDoc(cmd);

                  oxdoc.config.load();
                  oxdoc.config.setOption("outputdir", editOutputDir.getText());
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