import java.io.*;

	public class FileManager {

		private static String _imageCache = "images.xml";
		private static String _tempTexFileBase = "__oxdoc";

		public static String imageCache() {
			return outputFile(_imageCache);
		}

		public static String outputFile(String filename) {
			return Config.OutputDir + filename;
		}

		public static String tempDir() {
			return Config.TempDir;
		}

		public static String tempFile(String filename) {
			return tempDir() + filename;
		}

		public static boolean fileExists(String fileName) {
			File aFile = new File(fileName);
			return aFile.exists();			
		}

		public static boolean outputFileExists(String fileName) {
			File aFile = new File(outputFile(fileName));
			return aFile.exists();			
		}

		public static String tempTexFile() {
			return tempFile(_tempTexFileBase + ".tex");
		}

		public static String tempDviFile() {
			return tempFile(_tempTexFileBase + ".dvi");
		}

	}
	
