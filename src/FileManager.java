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

import java.io.*;
import java.net.*;
import java.security.*;


public class FileManager {
   public static final int NONE = -1;
   public static final int INDEX = 0;
   public static final int PROJECT = 1;
   public static final int FILE = 2;
   public static final int CLASS = 3;
   public static final int METHOD = 4;
   public static final int FUNCTION = 5;
   public static final int FIELD = 6;
   public static final String[] iconFiles = {
                                               "index", "project", "file",
                                               "class", "method", "function", "field"
   };
   private static String _imageCache = "images.xml";
   private static String _tempTexFileBase = "__oxdoc";
   public OxDoc oxdoc = null;

   public FileManager(OxDoc oxdoc) {
      this.oxdoc = oxdoc;
   }

   public String imageCache() {
      return outputFile(_imageCache);
   }

   public String outputFile(String filename) {
      return nativePath(oxdoc.config.OutputDir) + filename;
   }

   public String imageFile(String filename) {
      return nativePath(oxdoc.config.OutputDir) + nativePath(oxdoc.config.ImagePath) + filename;
   }

   public String tempDir() {
      return nativePath(oxdoc.config.TempDir);
   }

   public String tempFile(String filename) {
      return nativePath(tempDir()) + filename;
   }

   public String imageUrl(String filename) {
      return unixPath(oxdoc.config.ImagePath) + filename;
   }

   public boolean fileExists(String fileName) {
      File aFile = new File(fileName);

      return aFile.exists();
   }

   public boolean outputFileExists(String fileName) {
      File aFile = new File(outputFile(fileName));

      return aFile.exists();
   }

   public boolean imageFileExists(String fileName) {
      File aFile = new File(imageFile(fileName));

      return aFile.exists();
   }

   public String tempTexFile() {
      return tempFile(_tempTexFileBase + ".tex");
   }

   public String tempDviFile() {
      return tempFile(_tempTexFileBase + ".dvi");
   }

   public static File getApplicationDirectory(Class clas) {
      ProtectionDomain pd = clas.getProtectionDomain();
      if (pd == null)
         return null;

      CodeSource cs = pd.getCodeSource();
      if (cs == null)
         return null;

      URL url = cs.getLocation();
      if (url == null)
         return null;

      return new File(url.getFile()).getParentFile();
   }

   public static String appDirFile(String fileName) {
      File appDir = getApplicationDirectory(OxDoc.class);

      return appDir.toString().replaceAll("%20", " ") + File.separator + fileName;
   }

   public static String nativePath(String Path) {
      String out = Path.replace('/', File.separatorChar).replace('\\', File.separatorChar);
      if (out.length() == 0)
         return out;
      if (!out.endsWith(File.separator))
         out += File.separator;

      return out;
   }

   public static String unixPath(String Path) {
      String out = Path.replace('\\', '/');
      if (out.length() == 0)
         return out;
      if (!out.endsWith("/"))
         out += "/";

      return out;
   }

   public static String nativeFileName(String FileName) {
      String out = FileName.replace('/', File.separatorChar).replace('\\', File.separatorChar);

      return out;
   }

   public String largeIcon(int iconType) {
      if (!oxdoc.config.EnableIcons)
         return "";
      if (iconType < 0)
         return "";

      return "<img class=\"icon\" src=\"icons/" + iconFiles[iconType] + ".png\">&nbsp;";
   }

   public String smallIcon(int iconType) {
      if (!oxdoc.config.EnableIcons)
         return "";
      if (iconType < 0)
         return "";

      return "<img class=\"icon\" src=\"icons/" + iconFiles[iconType] + "_s.png\">&nbsp;";
   }
}
