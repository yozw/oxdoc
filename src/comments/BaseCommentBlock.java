import java.io.*;
import java.util.*;

	public class BaseCommentBlock extends ArrayList {

		public String toString() {
			return TextProcessor.process(renderHTML());
		}

		protected String renderHTML() {
			return "";
		}
	}