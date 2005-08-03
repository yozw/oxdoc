import java.io.*;
import java.util.*;
import java.text.*;
import java.util.regex.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import org.xml.sax.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;

	public class TextProcessor {
		public static String process(String text) {
			return filterReferences(filterLatexExpressions(text));
		}

		private static String filterReferences(String text) {
			String pattern = "\\`([^\\`]+)\\`";
			Pattern p = Pattern.compile(pattern);
			Matcher m = p.matcher(text);
			StringBuffer myStringBuffer = new StringBuffer();

			int formulaNumber = 1;
			while (m.find()) {
				String ref = text.substring(m.start()+1, m.end()-1);
				m.appendReplacement(myStringBuffer, oxdoc.project().linkToSymbol(ref)); 
			}
			return m.appendTail(myStringBuffer).toString();
		}
		
		private static String filterLatexExpressions(String text) {
			String pattern = "(\\$([^\\$]+)\\$)|(\\$\\$[^\\$]+\\$\\$)";
			Pattern p = Pattern.compile(pattern);
			Matcher m = p.matcher(text);
			StringBuffer myStringBuffer = new StringBuffer();

			int formulaNumber = 1;
			while (m.find()) {
				boolean isEquation = false;
				String formula = text.substring(m.start(), m.end());
				if (formula.startsWith("$$")) {
					formula = text.substring(m.start()+2, m.end()-2);
					isEquation = true;
				}
				else {
					formula = text.substring(m.start()+1, m.end()-1);
				}

				String replacement = formula;
				if (Config.EnableLatex)  {
					String filename = LatexImageManager.getFormulaFilename( (isEquation?"\\displaystyle{}":"\\textstyle{}") + formula);  
					replacement = "<img align=\"center\" src=\"" + filename + "\" alt=\"" + formula + "\">";
				}

				Object[] args = { isEquation?"equation":"expression", replacement };
				replacement = MessageFormat.format("<span class=\"{0}\">{1}</span>", args);
				m.appendReplacement(myStringBuffer, replacement); 
			}
			return m.appendTail(myStringBuffer).toString();
		}	
	}
