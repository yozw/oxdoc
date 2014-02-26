/**

 oxdoc (c) Copyright 2005-2014 by Y. Zwols

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

package oxdoc;

import java.text.MessageFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static oxdoc.util.Utils.checkNotNull;

public class TextProcessor {
  private final Config config;

  public TextProcessor(Config config) {
    this.config = checkNotNull(config);
  }

  private static boolean isEmptyLine(String S) {
    return (S.length() == 0) || (S.trim().length() == 0);
  }

  public String process(String text, OxProject project) {
    String processed = filterReferences(filterLatexExpressions(text), project);
    return formatParagraphs(processed);
  }

  static String formatParagraphs(String input) {
    StringBuilder output = new StringBuilder();

    int index = 0;
    boolean newParRequired = false;
    while (index < input.length()) {
      String line;
      int newLineIndex = input.indexOf('\n', index);
      if (newLineIndex < 0) {
        line = input.substring(index);
        index = input.length();
      } else {
        if (newLineIndex > 0 && input.charAt(newLineIndex - 1) == '\r') {
          line = input.substring(index, newLineIndex - 1) + "\n";
        } else {
          line = input.substring(index, newLineIndex + 1);
        }
        index = newLineIndex + 1;
      }
      if (isEmptyLine(line)) {
        newParRequired = true;
        continue;
      }

      if (newParRequired) {
        output.append("<P/>");
        newParRequired = false;
      }
      output.append(line);
    }
    return output.toString();
  }

  private String filterReferences(String text, OxProject project) {
    String pattern = "`([^`]+)`";
    Pattern p = Pattern.compile(pattern);
    Matcher m = p.matcher(text);
    StringBuffer myStringBuffer = new StringBuffer();

    while (m.find()) {
      String ref = text.substring(m.start() + 1, m.end() - 1);
      m.appendReplacement(myStringBuffer, project.getLinkToSymbol(ref));
    }

    return m.appendTail(myStringBuffer).toString();
  }

  private String filterLatexExpressions(String text) {
    String pattern = "(\\$([^\\$]+)\\$)|(\\$\\$[^\\$]+\\$\\$)";
    Pattern p = Pattern.compile(pattern);
    Matcher m = p.matcher(text);
    StringBuffer stringBuffer = new StringBuffer();

    while (m.find()) {
      boolean isInline = true;
      String formula = text.substring(m.start(), m.end());
      if (formula.startsWith("$$")) {
        formula = text.substring(m.start() + 2, m.end() - 2);
        isInline = false;
      } else {
        formula = text.substring(m.start() + 1, m.end() - 1);
      }

      String replacement = config.getMathProcessor().processFormula(formula, isInline);

      Object[] args = {isInline ? "expression" : "equation", replacement};
      replacement = MessageFormat.format("<span class=\"{0}\">{1}</span>", args);
      m.appendReplacement(stringBuffer, Matcher.quoteReplacement(replacement));
    }

    return m.appendTail(stringBuffer).toString();
  }
}
