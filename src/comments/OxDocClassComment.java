import java.io.*;
import java.util.*;
import java.util.regex.*;

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

		private String generateSection(String name, Object o) {
			String text = o.toString();
			if (text.length() == 0) return "";
			return "<dt><b>" + name + ":</b><dd>" + text + "</dd>\n";
		}

		public String toString() {
			String out = description() + "\n<dl><dd>";

			out += generateSection("Author", _author);
			out += generateSection("Version", _version);

			out += generateSection("Example", _example);
			out += generateSection("Comments", _comments);
			out += generateSection("See also", _see);

			out += "</dl></dd></dl>";
			return out;
		}
	}
	