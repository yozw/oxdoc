import java.io.*;
import java.util.*;

	public class BaseCommentBlock extends ArrayList {

		public String toString() {
			return LatexImageManager.filterLatex(renderHTML());
		}

		protected String renderHTML() {
			return "";
		}
	}