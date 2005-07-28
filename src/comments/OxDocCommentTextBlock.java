import java.io.*;
import java.util.*;

	public class OxDocCommentTextBlock extends ArrayList {
		
		public String toString() {
			String out = "";

			for (int i = 0; i < size(); i++)
				out += ((String)get(i)).trim() + "\n";
				
			return LatexImageManager.FilterLatex(out);
		}

	}
