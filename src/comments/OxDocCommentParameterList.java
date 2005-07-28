import java.io.*;
import java.util.*;
import java.util.regex.*;

	public class OxDocCommentParameterList extends ArrayList {

		public String toString() {
			if (size() == 0) return "";
			
			String out = "<table class=\"parameters\">";

			for (int i = 0; i < size(); i++) {
				String[] params = ((String) get(i)).split("[\t ]", 2);
				out += "<tr><td valign=\"top\"><tt>" + params[0] + "&nbsp;&nbsp;</tt></td>";
				if (params.length > 1)
					out += "<td valign=\"top\">" + LatexImageManager.FilterLatex(params[1]) + "</td>";
				out += "</tr>\n";
			}
			out += "</table>\n";
			return out;
		}
		
	}
