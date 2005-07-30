import java.io.*;
import java.util.*;
import java.util.regex.*;
import java.text.*;

	public class OxDocClassComment extends OxDocComment {
	
		String _author = "";
		String _version = "";

		protected boolean AddToSection(String name, String text) {
			if (!super.AddToSection(name, text)) {
				if (name.compareToIgnoreCase("author") == 0)      _author = text;
				else if (name.compareToIgnoreCase("version") == 0) _version = text;
				else return false;
			}
			return true;
		}

		private String generateSection(String name, String classname, Object o) {
			String text = o.toString();
			if (text.length() == 0) return "";
			
			Object[] args = {classname, name, text};
			return MessageFormat.format("<dt class=\"{0}\">{1}:</dt><dd class=\"{0}\">{2}</dd>\n", args);
		}

		public String toString() {
			String out = description() + "\n<dl>";

			out += generateSection("Author", "author", _author);
			out += generateSection("Version", "version", _version);

			out += generateSection("Example", "example", _example);
			out += generateSection("Comments", "comments", _comments);
			out += generateSection("See also", "seealso", _see);

			out += "</dl>";
			return out;
		}
	}
	