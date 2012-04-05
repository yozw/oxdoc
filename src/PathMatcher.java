import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.regex.Pattern;

public class PathMatcher {

	// Determines whether matching is supposed to be case-sensitive
	public static boolean caseSensitive = true;

	/** This class implements pattern-based file name filtering **/
	private class WildcardFilter implements FilenameFilter {

		private Pattern regexPattern;

		public WildcardFilter(String pattern) {
			// turn the pattern into a regular expression
			pattern = pattern.replace(".", "\\.").replace("?", ".?")
					.replace("*", ".*");
			if (caseSensitive)
				regexPattern = Pattern.compile(pattern);
			else
				regexPattern = Pattern.compile(pattern.toLowerCase());
		}

		@Override
		public boolean accept(File dir, String fileName) {
			// check whether the file name matches the regular expression
			if (caseSensitive)
				return this.regexPattern.matcher(fileName).matches();
			else
				return this.regexPattern.matcher(fileName.toLowerCase())
						.matches();
		}

	}

	/**
	 * This function scans files bases on pattern. For example:
	 * PathMatcher.scan("/home/user/*.java")
	 * 
	 * @return A list of matching files.
	 **/
	public static Iterable<File> scan(String pattern) {

		// check for Windows; if so, we want case-insensitive matching
		caseSensitive = System.getProperty("os.name", "generic").toLowerCase().indexOf("win") < 0;
		
		// construct the full path corresponding to pattern
		pattern = new File(pattern).getAbsolutePath();

		// separate full path into levels
		List<String> patterns = Arrays.asList(pattern.split(File.separator));

		// scan directories
		List<File> fileList = new ArrayList<File>();
		PathMatcher pm = new PathMatcher();
		pm.doScan(new File(File.separator), patterns.listIterator(), fileList);

		// return file list
		return fileList;
	}

	/** This function does the actual scanning. It looks in directory **/
	private void doScan(File dir, ListIterator<String> patternIterator,
			List<File> fileList) {
		// Find next (non-empty) pattern. If no such pattern exists, exit.
		String pattern = "";
		while (pattern.length() == 0) {
			if (!patternIterator.hasNext())
				return;
			pattern = patternIterator.next();
		}

		// List files in current directory that match the pattern
		String[] fileNames = dir.list(new WildcardFilter(pattern));
		for (String fileName : fileNames) {
			File file = new File(dir, fileName);

			// If there is no next pattern, then add the file to the list.
			// Otherwise, make a recursive call
			if (!patternIterator.hasNext())
				fileList.add(file);
			else if (file.isDirectory())
				doScan(file, patternIterator, fileList);
		}

		// Return the pattern iterator to its original position
		patternIterator.previous();
	}
}
