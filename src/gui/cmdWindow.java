import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;


public class cmdWindow implements Logger {
   private JFrame frame;
   private java.awt.TextArea memo;

   public cmdWindow() {
      //Create and set up the window.
      frame = new JFrame("oxdoc Output");

      JPanel mainPanel = new JPanel(new GridLayout(0, 1));
      mainPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
      frame.getContentPane().add(mainPanel);

      memo = new java.awt.TextArea();

      mainPanel.add(memo);

      Dimension dim = frame.getToolkit().getScreenSize();
      Rectangle abounds = frame.getBounds();
      int H = 400;
      int W = 600;
      frame.setBounds((dim.width - W) / 2, (dim.height - H) / 2, W, H);
   }

   public void writeMessage(String message, int Code) {
      memo.append(message + "\n");
   }

   public void show() {
      frame.setVisible(true);
   }
}