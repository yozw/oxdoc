import java.io.*;
import java.util.*;
import java.text.*;
import java.util.regex.*;

	public class OxDocFunctionComment extends OxDocComment {

		protected ArrayList _params = new OxDocCommentParameterList();
		protected ArrayList _returns = new OxDocCommentTextBlock();

		protected boolean AddToSection(String name, String text) {
			if (!super.AddToSection(name, text)) {
				if (name.compareToIgnoreCase("param") == 0)        _params.add(text);
				else if (name.compareToIgnoreCase("returns") == 0) _returns.add(text);
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
			String out = "<dl>\n<dd>" + description() + "<dl>\n";

			out += generateSection("Parameters", "parameters", _params);
			out += generateSection("Returns",    "returns", _returns);
			out += generateSection("Example",    "example", _example);
			out += generateSection("Comments",   "comments", _comments);
			out += generateSection("See also",   "seealso", _see);

			out += "</dl></dd>\n</dl>";
			return out;
		}
}
