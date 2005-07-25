import java.io.*;
import java.util.*;
import java.util.regex.*;

	public class OxDocComment {

		private Hashtable _sections = new Hashtable();
		private ArrayList _description = new OxDocCommentTextBlock();

		private ArrayList _comments = new OxDocCommentTextBlock();
		private ArrayList _example = new OxDocCommentTextBlock();
		private ArrayList _params = new OxDocCommentParameterList();
		private ArrayList _returns = new OxDocCommentTextBlock();
		private ArrayList _see = new OxDocCommentReferenceList();
	
		public void SetText(String text) {
			if (!text.startsWith("/**") || !text.endsWith("**/"))
				return;
			text = text.substring(3, text.length() - 3).trim();

			String[] sections = text.split("@");

			_description.add(sections[0]);
			
			for (int i = 1; i < sections.length; i++) {
				String[] words = sections[i].split("[\t ]", 2);
				String sectionName = words[0];
				String sectionText = (words.length>1)?words[1]:"";

				if (sectionName.compareToIgnoreCase("comments") == 0) _comments.add(sectionText);
				else if (sectionName.compareToIgnoreCase("example") == 0) _example.add(sectionText);
				else if (sectionName.compareToIgnoreCase("param") == 0) _params.add(sectionText);
				else if (sectionName.compareToIgnoreCase("returns") == 0) _returns.add(sectionText);
				else if (sectionName.compareToIgnoreCase("see") == 0) _see.add(sectionText); 
				else
					oxdoc.warning("Comment section '@" + sectionName + "' unknown -- ignored");
			}
		}

		public ArrayList description() {
			return _description;
		}

		public ArrayList parameters() {
			return _params;
		}

		public ArrayList returns() {
			return _returns;
		}

		public ArrayList example() {
			return _example;
		}

		public ArrayList comments() {
			return _comments;
		}

		public ArrayList see() {
			return _see;
		}
}
