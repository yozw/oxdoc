/**

oxdoc (c) Copyright 2005-2009 by Y. Zwols

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

import java.util.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import java.io.File;


public class Config {
   private OxDoc oxdoc = null;
   public String Latex = "latex.exe";
   public String LatexArg = "";
   public String Dvipng = "dvipng.exe";
   public String DvipngArg = "-Q 10 -D 110";
   public String OutputDir = ".";
   public String TempDir = ".";
   public String[] IncludePaths = new String[] {};
   public String ImagePath = "images/";
   public String WindowTitle = "";
   public String ProjectName = "";
   public MathProcessor MathProcessor = null;

   //    public static boolean EnableLatex = true;
   public boolean EnableIcons = false;

   public boolean ShowInternals = false;

   // color in the form "rgb <r> <g> <b>", or null for transparent
   public String ImageBgColor = "rgb 1.0 1.0 1.0";
   public String ConfigFile = "oxdoc.xml";
   public ArrayList LatexPackages = new ArrayList();

   public Config(OxDoc oxdoc) {
      this.oxdoc = oxdoc;
   }

   public boolean setSimpleOption(String name) {
      if (name.equals("icons"))
         setOption("enableicons", "1");
      else if (name.equals("showinternals"))
         setOption("showinternals", "1");
      else
         return false;

      return true;
   }

   public String htmlColorToLatex(String color) {
      try {
         String value = color.trim();
         if (value.startsWith("#"))
            value = value.substring(1);

         String out = "rgb ";
         for (int i = 0; i < 3; i++) {
            String H = value.substring(2 * i, 2 * i + 2);
            double val = Integer.parseInt(H, 16);
            out += (new Double(val / 256.0)).toString() + " ";
         }

         return out.trim();
      } catch (Exception e) {
         if (oxdoc != null)
            oxdoc.warning("Color specification " + color + " invalid. Ignored. (" + e.toString() + ")");

         return null;
      }
   }

   public MathProcessor toMathProcessor(String value) throws Exception {
      if (value.equals("latex"))
         return new MathProcessorLatex(oxdoc);
      if (value.equals("mathml"))
         return new MathProcessorMathML(oxdoc);
      if (value.equals("plain"))
         return new MathProcessor(oxdoc);
      throw new Exception("Formula specification " + value + " invalid. Ignored.");
   }

   public boolean setOption(String name, String value) {
      try {
         if (name.equals("latex"))
            Latex = FileManager.nativeFileName(value);
         else if (name.equals("dvipng"))
            Dvipng = FileManager.nativeFileName(value);
         else if (name.equals("tempdir"))
            TempDir = FileManager.nativePath(value);
         else if (name.equals("outputdir"))
            OutputDir = FileManager.nativePath(value);
         else if (name.equals("include"))
            IncludePaths = value.split(":");
         else if (name.equals("imagebgcolor")) {
            if (value.trim().equalsIgnoreCase("transparent"))
               ImageBgColor = null;
            else {
               String latexColor = htmlColorToLatex(value);
               if (latexColor != null)
                  ImageBgColor = latexColor;
            }
         } else if (name.equals("imagepath"))
            ImagePath = value;
         else if (name.equals("latexpackages")) {
            String[] packages = value.split("[,;]");
            for (int i = 0; i < packages.length; i++)
               LatexPackages.add(packages[i]);
         } else if (name.equals("formulas"))
            MathProcessor = toMathProcessor(value);
         else if (name.equals("enableicons"))
            EnableIcons = toBoolean(value);
         else if (name.equals("showinternals"))
            ShowInternals = toBoolean(value);
         else if (name.equals("projectname"))
            ProjectName = value;
         else if (name.equals("windowtitle"))
            WindowTitle = value;
         else

            return false;
      } catch (Exception E) {
         if (oxdoc != null)
            oxdoc.warning(E.getMessage());
      }

      return true;
   }

   public static void listOptions() {
      System.out.println("    -dvipng <executable>   Provides the path to the dvipng executable");
      System.out.println("    -imagebgcolor <color>  Sets background color for LaTeX images");
      System.out.println("    -latex <executable>    Provides the path to the LaTeX executable");
      System.out.println("    -include <paths>       Provides include search path");
      System.out.println("    -latexpackages <...>   Provides a list of packages to load in LaTeX files");
      System.out.println("    -icons                 Enables icons");
      System.out.println("    -showinternals         Enables documentation of internal methods/fields");
      System.out.println("    -nolatex               Disables LaTeX support. Formulas will be");
      System.out.println("                           inserted literally");
      System.out.println("    -outputdir <dir>       Specifies the output directory");
      System.out.println("    -projectname \"name\"    Specifies the name of the project");
      System.out.println("    -tempdir <dir>         Provides the directory in which temporary files");
      System.out.println("                           will be written");
      System.out.println("    -windowtitle \"title\"   Specifies a browser title");
   }

   public void validate() {
      if (MathProcessor == null) {
         // auto select Latex if possible, or else plain
         if (MathProcessorLatex.Supported(oxdoc))
            MathProcessor = new MathProcessorLatex(oxdoc);
         else
            MathProcessor = new MathProcessor(oxdoc);
      }

      // if the selected math processor is not supported, choose plain
      if (!MathProcessor.Supported(oxdoc))
         MathProcessor = new MathProcessor(oxdoc);

      MathProcessor.Start();
   }

   private boolean toBoolean(String value) {
      return (value.equals("1") || value.equals("yes"));
   }

   public void load() {
      load(FileManager.appDirFile(ConfigFile));
      load(ConfigFile);
   }

   public void load(String Filename) {
      File file = new File(Filename);
      if (!file.exists())
         return;
      if (oxdoc != null)
         oxdoc.message("Loading configuration file " + Filename);

      try {
         // Parse the file
         DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
         Document doc = builder.parse(file);

         // Find the tags of interest
         NodeList nodes = doc.getElementsByTagName("option");
         for (int i = 0; i < nodes.getLength(); i++) {
            Element element = (Element) nodes.item(i);

            String name = element.getAttribute("name");
            String value = element.getAttribute("value");
            setOption(name, value);
         }
      } catch (Exception E) {
      }
   }
}
