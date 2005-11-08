import java.io.*;
import java.util.*;

	public class CommentTextBlock extends BaseCommentBlock {
		
		protected String renderHTML() {
			String out = "";

			for (int i = 0; i < size(); i++)
				out += ((String)get(i)).trim() + "\n";
				
			return out;
		}

	}
