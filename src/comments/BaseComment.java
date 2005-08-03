import java.io.*;
import java.util.*;
import java.util.regex.*;

	public class BaseComment {

		private String 			   _description = "";
		private CommentTextBlock   _longdescription = new CommentTextBlock();
		private CommentTextBlock   _comments = new CommentTextBlock();
		private CommentTextBlock   _example = new CommentTextBlock();
		private CommentSeeAlsoList _see = new CommentSeeAlsoList();
		private CommentList        _ref = new CommentList();
		private CommentList        _todo = new CommentList();

		/** Add a piece of text to one of the sections. Do not access this
		method directly. It is used by SetText. Override this method to add more sections. 
		**/
		protected boolean addToSection(String name, String text) {
				if (name.compareToIgnoreCase("comments") == 0) _comments.add(text);
				else if (name.compareToIgnoreCase("ref") == 0) _ref.add(text);
				else if (name.compareToIgnoreCase("example") == 0) _example.add(text);
				else if (name.compareToIgnoreCase("see") == 0) _see.add(text);
				else
					return false;
				return true;
		}

		private static String extractShortDescription(String text) {
			text = text + " ";
			int i = text.indexOf(". ");
			if (i == -1)
				return text;
			else
				return text.substring(0, i+1);
		}

		/** Feeds an input comment block as a string and parses it. It interprets @<section name> blocks and
		passes the contents to AddToSection. **/
		public void setText(String text) throws ParseException {
			if (!text.startsWith("/**") || !text.endsWith("**/"))
				return;
			text = text.substring(3, text.length() - 3).trim();

			String[] sections = text.split("@");

			_description = extractShortDescription(sections[0]);
			_longdescription.add(sections[0]);
			
			for (int i = 1; i < sections.length; i++) {
				String[] words = sections[i].split("[\t ]", 2);
				String sectionName = words[0];
				String sectionText = (words.length>1)?words[1]:"";

				if (!addToSection(sectionName, sectionText))
					throw new ParseException("Comment section '@" + sectionName + "' unknown -- ignored");
			}
		}


		/** Short description of the entity, i.e. the part before the first . **/
		public String description() { return TextProcessor.process(_description); }
		
		/** Long description of the entity **/
		public CommentTextBlock longdescription() { return _longdescription; }

		/** Comments of the entity **/
		public CommentTextBlock comments() { return _comments; }

		/** Example block of the entity **/
		public CommentTextBlock example() { return _example; }

		/** The see also list of the entity **/
		public CommentSeeAlsoList see() { return _see; }

		/** The reference list of the entity **/
		public CommentList ref() { return _ref; }

		/** The to do list of the entity **/
		public CommentList todo() { return _todo; }
}
