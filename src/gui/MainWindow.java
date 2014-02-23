/**

 oxdoc (c) Copyright 2005-2012 by Y. Zwols

 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation; either
 version 2.1 of the License, or (at your option) any later version.

 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA

 **/

package oxdoc.gui;

import oxdoc.Constants;
import oxdoc.Logging;
import oxdoc.Os;
import oxdoc.OxDoc;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.HashMap;

public class MainWindow implements ActionListener {
  private final String actionRun = "run", actionExit = "exit", actionSaveBatch = "batch", actionSaveBash = "bash",
      actionSaveXml = "xml", actionAbout = "about";
  private static JFrame frame;
  private JTextField editWorkDirectory;
  private JTextField editFilenames;
  private JTextField editOutputDir;
  private JTextField editProjectName;
  private JTextField editIncludes;
  private JTextField editWindowTitle;
  private JRadioButton radioLatex;
  private JRadioButton radioMathjax;
  private JRadioButton radioPlainText;
  private JCheckBox chkShowInternals;
  private JCheckBox chkEnableIcons;
  private StatusBar statusBar;
  private JButton runButton;

  public MainWindow() {
    // Create and set up the window.

    frame = new JFrame(OxDoc.PRODUCT_NAME + " " + Constants.VERSION);
    frame.setResizable(false);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    JPanel mainPanel = new JPanel();
    mainPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
    mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

    mainPanel.add(setupOptionPanel());
    mainPanel.add(setupButtonPanel());
    frame.setJMenuBar(createMenuBar());

    // Display the window.
    frame.getContentPane().add(mainPanel, BorderLayout.CENTER);
    statusBar = new StatusBar();
    setStatus(OxDoc.COPYRIGHT_NOTICE);
    frame.getContentPane().add(statusBar, BorderLayout.SOUTH);
    frame.pack();

    Dimension dim = frame.getToolkit().getScreenSize();
    Rectangle abounds = frame.getBounds();
    frame.setLocation((dim.width - abounds.width) / 2, (dim.height - abounds.height) / 2);
  }

  private void setStatus(String text) {
    statusBar.setText(text);
  }

  private GridBagConstraints gridBagConstraints(int x, int y) {
    return gridBagConstraints(x, y, 1);
  }

  private GridBagConstraints gridBagConstraints(int x, int y, int w) {
    GridBagConstraints c = new GridBagConstraints();
    c.fill = GridBagConstraints.HORIZONTAL;
    c.weightx = 0.5;
    c.weighty = 0.5;
    c.gridx = x;
    c.gridy = y;
    c.gridwidth = w;
    return c;
  }

  private String separatorDescription(char c) {
    switch (c) {
      case ':':
        return "colons (:)";
      case ';':
        return "semicolons (;)";
      case ',':
        return "commas (,)";
      case '.':
        return "periods (.)";
    }

    return "" + c;
  }

  private JComponent setupOptionPanel() {
    GridBagLayout layout = new GridBagLayout();
    JPanel optionPanel = new JPanel(layout);

    JLabel labelWorkDirectory = new JLabel("Directory containing input Ox files:");
    JLabel labelFilenames = new JLabel("File specification (e.g. *.ox):");
    JLabel labelOutputdir = new JLabel("Output directory:");
    JLabel labelProjectName = new JLabel("Project name:");
    JLabel labelWindowTitle = new JLabel("Window title:");
    JLabel labelIncludes = new JLabel("Include paths separated by " + separatorDescription(File.pathSeparatorChar)
        + ":");
    JLabel labelOtherOptions = new JLabel("Other options:");
    JLabel labelFormulas = new JLabel("Generate formulas by:");

    editWorkDirectory = new JTextField(30);
    editFilenames = new JTextField(30);
    editOutputDir = new JTextField(30);
    editIncludes = new JTextField(30);
    editProjectName = new JTextField(30);
    editWindowTitle = new JTextField(30);
    radioMathjax = new JRadioButton("Mathjax", true);
    radioLatex = new JRadioButton("LaTeX", false);
    radioPlainText = new JRadioButton("Plain text", false);
    ButtonGroup group = new ButtonGroup();
    group.add(radioMathjax);
    group.add(radioLatex);
    group.add(radioPlainText);

    chkShowInternals = new JCheckBox("Generate documentation for internal fields and methods", false);
    chkEnableIcons = new JCheckBox("Enable icons", true);

    editWorkDirectory.setText((new File("")).getAbsolutePath());
    editOutputDir.setText("doc" + File.separator);
    editFilenames.setText("*.ox");

    optionPanel.add(labelWorkDirectory, gridBagConstraints(0, 0));
    optionPanel.add(editWorkDirectory, gridBagConstraints(1, 0));

    optionPanel.add(labelFilenames, gridBagConstraints(0, 1));
    optionPanel.add(editFilenames, gridBagConstraints(1, 1));

    optionPanel.add(labelOutputdir, gridBagConstraints(0, 2));
    optionPanel.add(editOutputDir, gridBagConstraints(1, 2));

    optionPanel.add(labelProjectName, gridBagConstraints(0, 3));
    optionPanel.add(editProjectName, gridBagConstraints(1, 3));

    optionPanel.add(labelWindowTitle, gridBagConstraints(0, 4));
    optionPanel.add(editWindowTitle, gridBagConstraints(1, 4));

    optionPanel.add(labelIncludes, gridBagConstraints(0, 5));
    optionPanel.add(editIncludes, gridBagConstraints(1, 5));

    optionPanel.add(labelFormulas, gridBagConstraints(0, 6));
    optionPanel.add(radioMathjax, gridBagConstraints(1, 6));
    optionPanel.add(radioLatex, gridBagConstraints(1, 7));
    optionPanel.add(radioPlainText, gridBagConstraints(1, 8));

    optionPanel.add(labelOtherOptions, gridBagConstraints(0, 9));
    optionPanel.add(chkEnableIcons, gridBagConstraints(1, 9));
    optionPanel.add(chkShowInternals, gridBagConstraints(1, 10));

    runButton = new JButton("Run");
    runButton.setActionCommand(actionRun);
    runButton.addActionListener(this);

    optionPanel.add(runButton, gridBagConstraints(1, 12));

    return optionPanel;
  }

  private JMenuBar createMenuBar() {
    JMenuBar menuBar = new JMenuBar();
    JMenu fileMenu = new JMenu("File");
    menuBar.add(fileMenu);

    switch (Os.getOperatingSystem()) {
      case Win32:
        JMenuItem batchItem = new JMenuItem("Save options to batch file...");
        batchItem.setActionCommand(actionSaveBatch);
        batchItem.addActionListener(this);
        fileMenu.add(batchItem);
        break;
      case Linux:
        JMenuItem bashItem = new JMenuItem("Save options to bash script...");
        bashItem.setActionCommand(actionSaveBash);
        bashItem.addActionListener(this);
        fileMenu.add(bashItem);
        break;
    }

    JMenuItem oxdocXmlItem = new JMenuItem("Save options to oxdoc.xml file...");
    oxdocXmlItem.setActionCommand(actionSaveXml);
    oxdocXmlItem.addActionListener(this);
    fileMenu.add(oxdocXmlItem);

    JMenuItem aboutItem = new JMenuItem("About...");
    aboutItem.setActionCommand(actionAbout);
    aboutItem.addActionListener(this);
    fileMenu.add(aboutItem);

    JMenuItem exitItem = new JMenuItem("Exit");
    exitItem.setActionCommand(actionExit);
    exitItem.addActionListener(this);
    fileMenu.add(exitItem);

    return menuBar;
  }

  private JComponent setupButtonPanel() {
    JPanel buttonPanel = new JPanel(new GridLayout(0, 2));
    buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));
    buttonPanel.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));

    frame.getRootPane().setDefaultButton(runButton);

    return buttonPanel;
  }

  public void actionPerformed(ActionEvent e) {
    if (e.getActionCommand().equals(actionExit))
      System.exit(0);
    else if (e.getActionCommand().equals(actionRun))
      runOxdoc();
    else if (e.getActionCommand().equals(actionSaveBatch))
      runSaveBatch();
    else if (e.getActionCommand().equals(actionSaveBash))
      runSaveBash();
    else if (e.getActionCommand().equals(actionSaveXml))
      runSaveXml();
    else if (e.getActionCommand().equals(actionAbout))
      showAbout();
  }

  public static void showException(Exception E) {
    JOptionPane.showMessageDialog(frame, E.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
  }

  public static void showAbout() {
    JOptionPane.showMessageDialog(frame, OxDoc.getAboutText());
  }

  private HashMap getOptions() {
    HashMap map = new HashMap();

    if (radioLatex.isSelected())
      map.put("formulas", "latex");
    else if (radioMathjax.isSelected())
      map.put("formulas", "mathjax");
    else if (radioPlainText.isSelected())
      map.put("formulas", "plain");

    map.put("icons", chkEnableIcons.isSelected() ? "on" : "off");
    map.put("internals", chkShowInternals.isSelected() ? "on" : "off");

    if (outputDir().length() > 0)
      map.put("outputdir", outputDir());
    if (projectName().length() > 0)
      map.put("projectname", projectName());
    if (windowTitle().length() > 0)
      map.put("windowtitle", windowTitle());
    if (includePaths().length() > 0)
      map.put("include", includePaths());
    return map;
  }

  private String generateOxdocArguments() {
    HashMap options = getOptions();
    String args = inputFileSpec();

    for (Object key : options.keySet())
      args += String.format(" -%s \"%s\"", key, options.get(key));

    return args;
  }

  public void runSaveBash() {
    File file = runSaveAsDialog("makedoc");
    if (file == null)
      return;

    try {
      Writer output = new BufferedWriter(new FileWriter(file));
      String args = generateOxdocArguments();
      output.write("#!/bin/bash\n");
      output.write("cd " + workingDir() + "\n");
      output.write("oxdoc " + args + "\n");
      output.close();
      Runtime.getRuntime().exec("chmod +x " + file);
      setStatus("Succesfully wrote the file " + file);
    } catch (Exception E) {
      showException(E);
    }
  }

  public File runSaveAsDialog(String fileName) {
    JFileChooser fc = new JFileChooser(new File(workingDir()));
    fc.setSelectedFile(new File(fileName));

    while (true) {
      int result = fc.showSaveDialog(frame);
      if (result != JFileChooser.APPROVE_OPTION)
        return null;

      File file = fc.getSelectedFile();
      if (!file.exists())
        return file;

      int overwriteResult = JOptionPane.showConfirmDialog(frame, "The file " + file
          + " already exists. Do you want to overwrite the existing file?", "Save file",
          JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE);
      if (overwriteResult == JOptionPane.YES_OPTION)
        return file;
      if (overwriteResult == JOptionPane.CANCEL_OPTION)
        return null;
    }
  }

  public void runSaveXml() {
    File file = runSaveAsDialog("oxdoc.xml");
    if (file == null)
      return;

    try {
      Writer output = new BufferedWriter(new FileWriter(file));
      generateOxdocArguments();
      output.write("<oxdoc>\n");
      output.write("<!-- This file was generated by oxdocgui -->\n");
      HashMap options = getOptions();
      for (Object key : options.keySet())
        output.write(String.format("  <option %s=\"%s\"/>\n", key, options.get(key)));
      output.write("</oxdoc>\n");
      output.close();
      setStatus("Succesfully wrote the file " + file);
    } catch (Exception E) {
      showException(E);
    }
  }

  public void runSaveBatch() {
    File file = runSaveAsDialog("makedoc.bat");
    if (file == null)
      return;

    try {
      Writer output = new BufferedWriter(new FileWriter(file));
      String args = generateOxdocArguments();
      output.write("@echo off\n");
      output.write("cd \"" + workingDir() + "\"\n");
      output.write("call oxdoc " + args + "\n");
      output.close();
      setStatus("Succesfully wrote the file " + file);
    } catch (Exception E) {
      showException(E);
    }
  }

  private String inputFileSpec() {
    return editFilenames.getText();
  }

  private String includePaths() {
    return editIncludes.getText();
  }

  private String projectName() {
    return editProjectName.getText();
  }

  private String windowTitle() {
    return editWindowTitle.getText();
  }

  private String outputDir() {
    File f = new File(editOutputDir.getText());

    return f.getAbsolutePath();
  }

  private String workingDir() {
    File f = new File(editWorkDirectory.getText());

    return f.getAbsolutePath();
  }

  public void runOxdoc() {
    try {
      final CmdWindow cmd = new CmdWindow();
      Logging.setLogger(cmd);
      cmd.show();

      Thread oxdocThread = new Thread() {
        public void run() {
          try {
            System.setProperty("user.dir", workingDir());

            OxDoc oxdoc = new OxDoc();

            oxdoc.getConfig().load();
            HashMap options = getOptions();
            for (Object key : options.keySet())
              oxdoc.getConfig().setOption(key.toString(), options.get(key).toString());
            oxdoc.getConfig().setOption("outputdir", outputDir());
            oxdoc.getConfig().validate();

            oxdoc.addFiles(editFilenames.getText());
            oxdoc.generateDocs();

            cmd.info("Execution succesful");
          } catch (Exception E) {
            cmd.warning("Execution failed: \n" + E.getMessage());
          }
          cmd.enableButtons(true);
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
