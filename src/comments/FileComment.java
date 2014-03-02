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

public class FileComment extends BaseComment {
  public static final int SECTION_AUTHOR = 100, SECTION_VERSION = 101;

  private StringBuilder author = new StringBuilder();
  private StringBuilder version = new StringBuilder();

  public FileComment(OxProject project) {
    super(project);

    registerSection("author", SECTION_AUTHOR);
    registerSection("authors", SECTION_AUTHOR);
    registerSection("version", SECTION_VERSION);
  }

  protected boolean addToSection(int sectionId, String text) {
    if (super.addToSection(sectionId, text)) {
      return true;
    }

    switch (sectionId) {
      case SECTION_AUTHOR:
        author.append(text);
        break;
      case SECTION_VERSION:
        version.append(text);
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

  public String toString() {
    String authorStr = generateSection("Author", "author", author.toString());
    String versionStr = generateSection("Version", "version", version.toString());
    String exampleStr = generateSection("Example", "example", example());
    String commentStr = generateSection("Comments", "comments", comments());
    String seeAlsoStr = generateSection("See also", "seealso", see());

    StringBuilder result = new StringBuilder();
    result.append(longdescription());
    if (!allNullOrEmpty(authorStr, versionStr, exampleStr, commentStr, seeAlsoStr)) {
      result.append("\n<dl>");
      result.append(authorStr);
      result.append(versionStr);
      result.append(exampleStr);
      result.append(commentStr);
      result.append(seeAlsoStr);
      result.append("</dl>");
    }

    return result.toString();
  }
}
