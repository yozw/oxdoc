import java.io.*;
import java.util.*;

	public class OxDocCommentList extends ArrayList {
		
		public String toString() {
			String out = "<ul>";

			for (int i = 0; i < size(); i++)
				out += "<li>" + ((String)get(i)).trim() + "\n";
			out += "</li>";
				
			return LatexImageManager.FilterLatex(out);
		}

	}
