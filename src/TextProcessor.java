/**

oxdoc (c) Copyright 2005 by Y. Zwols

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA

**/

import java.text.*;
import java.util.regex.*;

public class TextProcessor {
    public static String process(String text) {
	return filterReferences(filterLatexExpressions(text));
    }

    private static String filterReferences(String text) {
	String pattern = "\\`([^\\`]+)\\`";
	Pattern p = Pattern.compile(pattern);
	Matcher m = p.matcher(text);
	StringBuffer myStringBuffer = new StringBuffer();

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
		replacement = "<img align=\"center\" src=\"" + FileManager.imageUrl(filename) + "\" alt=\"" + formula + "\">";
	    }

	    Object[] args = { isEquation?"equation":"expression", replacement };
	    replacement = MessageFormat.format("<span class=\"{0}\">{1}</span>", args);
	    m.appendReplacement(myStringBuffer, replacement); 
	}
	return m.appendTail(myStringBuffer).toString();
    }	
}
