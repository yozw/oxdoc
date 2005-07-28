import java.io.*;
import java.util.*;
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

		private String generateSection(String name, Object o) {
			String text = o.toString();
			if (text.length() == 0) return "";
			return "<dt><b>" + name + ":</b><dd>" + text + "</dd>\n";
		}

		public String toString() {
			String out = "<dl><dd>" + description() + "<dl>";

			out += generateSection("Parameters", _params);
			out += generateSection("Returns", _returns);
			out += generateSection("Example", _example);
			out += generateSection("Comments", _comments);
			out += generateSection("See also", _see);

			out += "</dl></dd></dl>";
			return out;
		}
}
