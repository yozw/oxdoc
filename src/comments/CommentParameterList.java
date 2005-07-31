import java.io.*;
import java.util.*;
import java.util.regex.*;

	public class CommentParameterList extends BaseCommentBlock {

		protected String renderHTML() {
			if (size() == 0) return "";

			String out = "<!-- parameter table --!>\n";
			out += "<table class=\"parameter_table\">\n";

			for (int i = 0; i < size(); i++) {
				String[] params = ((String) get(i)).split("[\t ]", 2);

				out += "<tr>\n";
				out += "<td class=\"declaration\" valign=\"top\">" + params[0] + "</td>\n";
				if (params.length > 1)
					out += "<td class=\"description\" valign=\"top\">" + params[1] + "</td>\n";
				out += "</tr>\n";
			}
			out += "</table>\n";
			return out;
		}
		
	}
