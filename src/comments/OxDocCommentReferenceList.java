import java.io.*;
import java.util.*;
import java.util.regex.*;

	public class OxDocCommentReferenceList extends ArrayList {

		public boolean add(Object o) {
			String[] references = o.toString().split(",");
			for (int j = 0; j < references.length; j++)
				super.add(references[j].trim());
			return true;
		}

		public String toString() {
			String out = "";
			for (int j = 0; j < size(); j++) {
				if (j > 0)
					out += ", ";
				out += oxdoc.project().linkToSymbol(get(j).toString());
			}
			return out;
		}
		
	}
