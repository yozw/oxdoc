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

package oxdoc;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.Hashtable;

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
	public static final String[] iconFiles = { "index", "project", "file",
			"class", "method", "function", "field", "enum", "uplevel",
			"hierarchy", "global", "files" };
	private static String _imageCache = "images.xml";
	private static String _tempTexFileBase = "__oxdoc";
	public OxDoc oxdoc = null;

	// for speed reasons, register which resource we've tried to write so far
	// key: String (filename + "||" + resourcename), value: integer (0 =
	// success)
	Hashtable resourceResults = new Hashtable();

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
		return nativePath(oxdoc.config.OutputDir)
				+ nativePath(oxdoc.config.ImagePath) + filename;
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

		return appDir.toString().replaceAll("%20", " ") + File.separator
				+ fileName;
	}

	public static String nativePath(String Path) {
		String out = Path.replace('/', File.separatorChar).replace('\\',
				File.separatorChar);
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
		String out = FileName.replace('/', File.separatorChar).replace('\\',
				File.separatorChar);

		return out;
	}

	public String largeIcon(int iconType) {
		if (!oxdoc.config.EnableIcons)
			return "";
		if (iconType < 0)
			return "";

		String fileName = "icons/" + iconFiles[iconType] + ".png";
		copyFromResourceIfNotExists(fileName);

		return "<img class=\"icon\" src=\"" + fileName + "\">&nbsp;";
	}

	public String smallIcon(int iconType) {
		if (!oxdoc.config.EnableIcons)
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

	// this is a cached version of _copyFromResourceIfNotExists
	public boolean copyFromResourceIfNotExists(String fileName,
			String resourceName) {
		// check if we've requested this resource before
		String key = fileName + "||" + resourceName;
		if (resourceResults.containsKey(key))
			return ((Boolean) resourceResults.get(key)).booleanValue();

		boolean result = _copyFromResourceIfNotExists(fileName, resourceName);
		resourceResults.put(key, new Boolean(result));
		return result;
	}

	private boolean _copyFromResourceIfNotExists(String fileName,
			String resourceName) {
		try {
			if (oxdoc.fileManager.outputFileExists(fileName))
				return true;

			InputStream resourceFile = OxDoc.class
					.getResourceAsStream(resourceName);
			if (resourceFile == null) {
				oxdoc.warning("Resource '" + resourceName + "' does not exist.");
				return false;
			}

			BinaryOutputFile output = new BinaryOutputFile(fileName, oxdoc);

			byte[] buffer = new byte[4096];

			while (true) {
				int bytesRead = resourceFile.read(buffer, 0, buffer.length);
				if (bytesRead < 0)
					break;
				output.write(buffer, bytesRead);
			}
			resourceFile.close();
			output.close();

			oxdoc.message("Succesfully wrote " + fileName);
		} catch (Exception E) {
			oxdoc.message(E.getMessage());
			return false;
		}
		return true;
	}

}
