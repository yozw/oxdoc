import java.io.*;

	public class FileManager {

		private static String _imageCache = "images.xml";

		public static String imageCache() {
			return outputFile(_imageCache);
		}

		public static String outputFile(String filename) {
			return OxDocConfig.OutputDir + filename;
		}

		public static String tempDir() {
			return OxDocConfig.TempDir;
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

	}
	
