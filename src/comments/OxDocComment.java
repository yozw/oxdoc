import java.io.*;
import java.util.*;
import java.util.regex.*;

	public class OxDocComment {

		protected ArrayList _description = new OxDocCommentTextBlock();
		protected ArrayList _comments = new OxDocCommentTextBlock();
		protected ArrayList _example = new OxDocCommentTextBlock();
		protected ArrayList _longdescription = new OxDocCommentTextBlock();
		protected ArrayList _see = new OxDocCommentReferenceList();
		protected ArrayList _ref = new OxDocCommentList();
		protected ArrayList _todo = new OxDocCommentList();

		protected boolean AddToSection(String name, String text) {
				if (name.compareToIgnoreCase("comments") == 0) _comments.add(text);
				else if (name.compareToIgnoreCase("desc") == 0) _longdescription.add(text);
				else if (name.compareToIgnoreCase("todo") == 0) _longdescription.add(text);
				else if (name.compareToIgnoreCase("ref") == 0) _ref.add(text);
				else if (name.compareToIgnoreCase("example") == 0) _example.add(text);
				else if (name.compareToIgnoreCase("see") == 0) _see.add(text);
				else
					return false;
				return true;
		}
	
		public void SetText(String text) throws ParseException {
			if (!text.startsWith("/**") || !text.endsWith("**/"))
				return;
			text = text.substring(3, text.length() - 3).trim();

			String[] sections = text.split("@");

			_description.add(sections[0]);
			
			for (int i = 1; i < sections.length; i++) {
				String[] words = sections[i].split("[\t ]", 2);
				String sectionName = words[0];
				String sectionText = (words.length>1)?words[1]:"";

				if (!AddToSection(sectionName, sectionText))
					throw new ParseException("Comment section '@" + sectionName + "' unknown -- ignored");
			}
		}

		public String description() {
			return _description.toString();
		}
		
		public String toString() {
			return "";
		}
}
