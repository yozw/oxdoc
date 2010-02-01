import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;


public class OxDocSetup {

   private static final boolean fancyLook = false;

   public void run(String[] args) {
      javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
               try {
                  if (fancyLook)
				         JFrame.setDefaultLookAndFeelDecorated(true);
                  else
                     UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
               } catch (Exception e) {
               }

               SetupWindow setup = new SetupWindow();
               setup.show();
            }
         });
   }
}
