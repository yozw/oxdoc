import java.io.*;
import java.util.*;

	public class CommentList extends BaseCommentBlock {
		
		protected String renderHTML() {
			String out = "<ul>";

			for (int i = 0; i < size(); i++)
				out += "<li>" + ((String)get(i)).trim() + "\n";
			out += "</li>";
				
			return out;
		}

	}
