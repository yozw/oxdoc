import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;


public class OxDocGui {

   private static final boolean fancyLook = false;

   public static void main(String[] args) {
      javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
               try {
                  if (fancyLook)
				         JFrame.setDefaultLookAndFeelDecorated(true);
                  else
                     UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
               } catch (Exception e) {
               }

               MainWindow main = new MainWindow();
               main.show();
            }
         });
   }
}