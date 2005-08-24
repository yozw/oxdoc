import java.io.*;
import java.security.*;
import java.net.*;

	public class FileManager {

		public static final int NONE = -1, INDEX = 0, PROJECT = 1, FILE = 2, CLASS = 3, METHOD = 4, FUNCTION = 5;
		public static final String[] iconFiles = {"index", "project", "file", "class", "method", "function"};
		private static String _imageCache = "images.xml";
		private static String _tempTexFileBase = "__oxdoc";

		public static String imageCache() {
			return outputFile(_imageCache);
		}

		public static String outputFile(String filename) {
			return nativePath(Config.OutputDir) + filename;
		}

		public static String imageFile(String filename) {
			return nativePath(Config.OutputDir + Config.ImagePath) + filename;
		}
		
		public static String tempDir() {
			return nativePath(Config.TempDir);
		}

		public static String tempFile(String filename) {
			return nativePath(tempDir()) + filename;
		}

		public static String imageUrl(String filename) {
			return unixPath(Config.ImagePath) + filename;
		}

		public static boolean fileExists(String fileName) {
			File aFile = new File(fileName);
			return aFile.exists();			
		}

		public static boolean outputFileExists(String fileName) {
			File aFile = new File(outputFile(fileName));
			return aFile.exists();			
		}

		public static boolean imageFileExists(String fileName) {
			File aFile = new File(imageFile(fileName));
			return aFile.exists();			
		}

		public static String tempTexFile() {
			return tempFile(_tempTexFileBase + ".tex");
		}

		public static String tempDviFile() {
			return tempFile(_tempTexFileBase + ".dvi");
		}

		public static File getApplicationDirectory( Class clas ) {
	      	ProtectionDomain pd = clas.getProtectionDomain();
	      	if ( pd == null ) return null;
	
	      	CodeSource cs = pd.getCodeSource();
	      	if ( cs == null ) return null;
	
	      	URL url = cs.getLocation();
	      	if ( url == null ) return null;
	
	      	return new File( url.getFile() ).getParentFile();
	   } 

		public static String appDirFile(String fileName) {
			File appDir = getApplicationDirectory(oxdoc.class);
			return appDir.toString() + File.separator + fileName;
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
		
		public static String largeIcon(int iconType) {
			if (!Config.EnableIcons) return "";
			if (iconType < 0) return "";
			return "<img align=\"center\" src=\"icons/" + iconFiles[iconType] + ".png\">&nbsp;";
		}

		public static String smallIcon(int iconType) {
			if (!Config.EnableIcons) return "";
			if (iconType < 0) return "";
			return "<img align=\"center\" src=\"icons/" + iconFiles[iconType] + "_s.png\">&nbsp;";
		}
	}
	
