import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;


public class oxdocGui {
   public static void main(String[] args) {
      javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
               try {
                  UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
               } catch (Exception e) {
               }

               mainWindow main = new mainWindow();
               main.show();
            }
         });
   }
}