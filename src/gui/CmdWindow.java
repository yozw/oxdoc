import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;


public class CmdWindow implements ActionListener, Logger {
	private final String actionClose = "close", actionSave = "save";
   private JFrame frame;
   private java.awt.TextArea memo;
   private JButton closeButton, saveButton;

   public CmdWindow() {
      //Create and set up the window.
      frame = new JFrame("oxdoc Output");

      JPanel mainPanel = new JPanel();
      mainPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
      mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

		frame.getContentPane().add(mainPanel);

      memo = new java.awt.TextArea();

      mainPanel.add(memo);
      mainPanel.add(setupButtonPanel());

      Dimension dim = frame.getToolkit().getScreenSize();
      Rectangle abounds = frame.getBounds();
      int H = 400;
      int W = 600;
      frame.setBounds((dim.width - W) / 2, (dim.height - H) / 2, W, H);

      enableButtons(false);
   }

   private JComponent setupButtonPanel() {
      JPanel buttonPanel = new JPanel(new GridLayout(0, 2));
      buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));
      buttonPanel.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));

      closeButton = new JButton("Close");
      closeButton.setActionCommand(actionClose);
      closeButton.addActionListener(this);

      saveButton = new JButton("Save log...");
      saveButton.setActionCommand(actionSave);
      saveButton.addActionListener(this);

      buttonPanel.add(Box.createHorizontalGlue());
      buttonPanel.add(saveButton);
      buttonPanel.add(Box.createRigidArea(new Dimension(8, 0)));
      buttonPanel.add(closeButton);

      frame.getRootPane().setDefaultButton(closeButton);

      return buttonPanel;
   }

   public void actionPerformed(ActionEvent e) {
      if (e.getActionCommand().equals(actionClose))
         hide();
      if (e.getActionCommand().equals(actionSave))
         runSaveLog();
   }

   public void enableButtons(boolean enabled) {
      saveButton.setEnabled(enabled);
      closeButton.setEnabled(enabled);
   }

   public void runSaveLog() {
      JFileChooser fc = new JFileChooser(new File("."));
      fc.setSelectedFile(new File("oxdoc.log"));
      if (fc.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION)
         try {
            Writer output = new BufferedWriter(new FileWriter(fc.getSelectedFile()));
			output.write(memo.getText());
            output.close();
         } catch (Exception E) {
            MainWindow.showException(E);
         }
   }

   
   public void writeMessage(String message, int Code) {
      memo.append(message + "\n");
   }

   public void show() {
      frame.setVisible(true);
   }

	public void hide() {
      frame.setVisible(false);
   }
}