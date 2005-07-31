import java.io.*;
import java.util.*;
import java.util.regex.*;

	public class BaseComment {

		private CommentTextBlock   _description = new CommentTextBlock();
		private CommentTextBlock   _comments = new CommentTextBlock();
		private CommentTextBlock   _example = new CommentTextBlock();
		private CommentTextBlock   _longdescription = new CommentTextBlock();
		private CommentSeeAlsoList _see = new CommentSeeAlsoList();
		private CommentList        _ref = new CommentList();
		private CommentList        _todo = new CommentList();

		/** Add a piece of text to one of the sections. Do not access this
		method directly. It is used by SetText. Override this method to add more sections. 
		**/
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

		/** Feeds an input comment block as a string and parses it. It interprets @<section name> blocks and
		passes the contents to AddToSection. **/
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

		/** Description of the entity **/
		public CommentTextBlock description() { return _description; }

		/** Comments of the entity **/
		public CommentTextBlock comments() { return _comments; }

		/** Example block of the entity **/
		public CommentTextBlock example() { return _example; }

		/** The long description of the entity **/
		public CommentTextBlock longdescription() { return _longdescription; }

		/** The see also... list of the entity **/
		public CommentSeeAlsoList see() { return _see; }

		/** The reference list of the entity **/
		public CommentList ref() { return _ref; }

		/** The to do list of the entity **/
		public CommentList todo() { return _todo; }
}
