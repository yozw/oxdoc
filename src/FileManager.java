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

package oxdoc;

import oxdoc.util.Logger;
import oxdoc.util.Logging;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.Hashtable;

import static oxdoc.util.Utils.checkNotNull;

public class FileManager {
  public static final int NONE = -1;
  public static final int INDEX = 0;
  public static final int PROJECT = 1;
  public static final int FILE = 2;
  public static final int CLASS = 3;
  public static final int METHOD = 4;
  public static final int FUNCTION = 5;
  public static final int FIELD = 6;
  public static final int ENUM = 7;
  public static final int UPLEVEL = 8;
  public static final int HIERARCHY = 9;
  public static final int GLOBAL = 10;
  public static final int FILES = 11;
  public static final String[] iconFiles = {"index", "project", "file", "class", "method", "function", "field",
      "enum", "uplevel", "hierarchy", "global", "files"};
  private static final Logger logger = Logging.getLogger();
  private static final String imageCache = "images.xml";
  private static final String tempTexFileBase = "__oxdoc";
  private final Config config;

  // for speed reasons, register which resource we've tried to write so far
  // key: String (filename + "||" + resourcename), value: integer (0 =
  // success)
  private final Hashtable<String, Boolean> resourceResults = new Hashtable<String, Boolean>();

  public FileManager(Config config) {
    this.config = checkNotNull(config);
  }

  public String getImageCache() {
    return getOutputFilename(imageCache);
  }

  public String getOutputFilename(String filename) {
    return toNativePath(config.getOutputDir()) + filename;
  }

  public String getImageFilename(String filename) {
    return toNativePath(config.getOutputDir()) + toNativePath(config.getImagePath()) + filename;
  }

  public String getTempDir() {
    return toNativePath(config.getTempDir());
  }

  public String getTempFilename(String filename) {
    return toNativePath(getTempDir()) + filename;
  }

  public String getImageUrl(String filename) {
    return toUnixPath(config.getImagePath()) + filename;
  }

  public boolean outputFileExists(String fileName) {
    File aFile = new File(getOutputFilename(fileName));
    return aFile.exists();
  }

  public boolean imageFileExists(String fileName) {
    File aFile = new File(getImageFilename(fileName));
    return aFile.exists();
  }

  public String getTempTexFile() {
    return getTempFilename(tempTexFileBase + ".tex");
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

  public static String getAppDirFilename(String fileName) {
    File appDir = getApplicationDirectory(OxDoc.class);

    return appDir.toString().replaceAll("%20", " ") + File.separator + fileName;
  }

  public static String toNativePath(String path) {
    String out = path.replace('/', File.separatorChar).replace('\\', File.separatorChar);
    if (out.length() == 0)
      return out;
    if (!out.endsWith(File.separator))
      out += File.separator;

    return out;
  }

  public static String toUnixPath(String path) {
    String out = path.replace('\\', '/');
    if (out.length() == 0)
      return out;
    if (!out.endsWith("/"))
      out += "/";

    return out;
  }

  public static String toNativeFileName(String fileName) {
    return fileName.replace('/', File.separatorChar).replace('\\', File.separatorChar);
  }

  public String getLargeIconHtml(int iconType) {
    if (!config.isEnableIcons())
      return "";
    if (iconType < 0)
      return "";

    String fileName = "icons/" + iconFiles[iconType] + ".png";
    copyFromResourceIfNotExists(fileName);

    return "<img class=\"icon\" src=\"" + fileName + "\">&nbsp;";
  }

  public String getSmallIconHtml(int iconType) {
    if (!config.isEnableIcons())
      return "";
    if (iconType < 0)
      return "";

    String fileName = "icons/" + iconFiles[iconType] + "_s.png";
    copyFromResourceIfNotExists(fileName);

    return "<img class=\"icon\" src=\"" + fileName + "\">&nbsp;";
  }

  public boolean copyFromResourceIfNotExists(String fileName) {
    return copyFromResourceIfNotExists(fileName, fileName);
  }

  // this is a cached version of doCopyFromResourceIfNotExists
  public boolean copyFromResourceIfNotExists(String fileName, String resourceName) {
    // check if we've requested this resource before
    String key = fileName + "||" + resourceName;
    if (resourceResults.containsKey(key))
      return resourceResults.get(key);

    boolean result = doCopyFromResourceIfNotExists(fileName, resourceName);
    resourceResults.put(key, result);
    return result;
  }

  private boolean doCopyFromResourceIfNotExists(String fileName, String resourceName) {
    try {
      if (outputFileExists(fileName))
        return true;

      InputStream resourceFile = OxDoc.class.getResourceAsStream(resourceName);
      if (resourceFile == null) {
        logger.warning("Resource '" + resourceName + "' does not exist.");
        return false;
      }

      BinaryOutputFile output = new BinaryOutputFile(fileName, this);

      byte[] buffer = new byte[4096];

      while (true) {
        int bytesRead = resourceFile.read(buffer, 0, buffer.length);
        if (bytesRead < 0)
          break;
        output.write(buffer, bytesRead);
      }
      resourceFile.close();
      output.close();

      logger.info("Successfully wrote " + fileName);
    } catch (Exception E) {
      logger.info(E.getMessage());
      return false;
    }
    return true;
  }

}
