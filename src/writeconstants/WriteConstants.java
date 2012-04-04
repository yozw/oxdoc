import java.io.*;
import java.text.*;
import java.util.*;


public class WriteConstants {
   public static void main(String[] args) {
      if (args.length != 1) {
         System.out.println("Usage: WriteConstants <version>");

         return;
      }

      String date = GetDate();

      System.out.println("/* Generated code -- do not edit */");
      System.out.println("package oxdoc;");
      System.out.println("public class Constants {");
      System.out.println("    public static final String COMPILETIME = \"" + date + "\";");
      System.out.println("    public static final String VERSION = \"" + args[0] + "\";");
      System.out.println("}");
   }

   private static String GetDate() {
      Date date = new Date();
      SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy");

      return sdf.format(date);
   }
}
