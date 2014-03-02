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
package oxdoc.comments;

import oxdoc.OxProject;

import java.text.MessageFormat;

import static oxdoc.util.Utils.allNullOrEmpty;

public class FunctionComment extends BaseComment {
  public static final int SECTION_PARAM = 200, SECTION_RETURNS = 201;
  public static final int MODIFIER_INTERNAL = 300;

  private BaseCommentBlock params;
  private BaseCommentBlock returns;
  private boolean hasInternalModifier = false;

  public FunctionComment(OxProject project) {
    super(project);
    params = new CommentParameterList(project);
    returns = new CommentTextBlock(project);

    registerSection("param", SECTION_PARAM);
    registerSection("params", SECTION_PARAM);
    registerSection("return", SECTION_RETURNS);
    registerSection("returns", SECTION_RETURNS);
    registerModifier("internal", MODIFIER_INTERNAL);
  }

  protected boolean addToSection(int sectionId, String text) {
    if (super.addToSection(sectionId, text)) {
      return true;
    }

    switch (sectionId) {
      case SECTION_PARAM:
        params.add(text);
        break;
      case SECTION_RETURNS:
        returns.add(text);
        break;
      default:
        return false;
    }

    return true;
  }

  private String generateSection(String name, String classname, Object o) {
    String text = o.toString();
    if (text.length() == 0) {
      return "";
    }

    Object[] args = {classname, name, text};

    return MessageFormat.format("<dt class=\"{0}\">{1}:</dt><dd class=\"{0}\">{2}</dd>\n", args);
  }

  protected boolean processModifier(int ModifierId) {
    if (super.processModifier(ModifierId)) {
      return true;
    }
    if (ModifierId == MODIFIER_INTERNAL) {
      hasInternalModifier = true;
      return true;
    }
    return false;
  }

  public String toString() {
    String parameterStr = generateSection("Parameters", "parameters", params());
    String returnStr = generateSection("Returns", "returns", returns());
    String exampleStr = generateSection("Example", "example", example());
    String commentStr = generateSection("Comments", "comments", comments());
    String seeAlsoStr = generateSection("See also", "seealso", see());

    StringBuilder result = new StringBuilder();
    result.append(longdescription());

    if (!allNullOrEmpty(parameterStr, returnStr, exampleStr, commentStr, seeAlsoStr)) {
      result.append("\n<dl>");
      result.append(parameterStr);
      result.append(returnStr);
      result.append(exampleStr);
      result.append(commentStr);
      result.append(seeAlsoStr);
      result.append("</dl>");
    }

    return result.toString();
  }

  public BaseCommentBlock params() {
    return params;
  }

  public BaseCommentBlock returns() {
    return returns;
  }

  public boolean hasInternalModifier() {
    return hasInternalModifier;
  }
}
