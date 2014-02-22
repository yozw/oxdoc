/**

 oxdoc (c) Copyright 2005-2012 by Y. Zwols

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

public class FieldComment extends BaseComment {
  private final static int MODIFIER_INTERNAL = 300;
  private boolean _hasInternalModifier = false;

  public FieldComment(OxProject project) {
    super(project);
    registerModifier("internal", MODIFIER_INTERNAL);
  }

  private String generateSection(String name, String classname, Object o) {
    String text = o.toString();
    if (text.length() == 0)
      return "";

    Object[] args = {classname, name, text};

    return MessageFormat.format("<dt class=\"{0}\">{1}:</dt><dd class=\"{0}\">{2}</dd>\n", args);
  }

  protected boolean processModifier(int ModifierId) {
    if (super.processModifier(ModifierId))
      return true;
    if (ModifierId == MODIFIER_INTERNAL) {
      _hasInternalModifier = true;
      return true;
    }
    return false;
  }

  public String toString() {
    String extraInfo = "";

    extraInfo += generateSection("Example", "example", example());
    extraInfo += generateSection("Comments", "comments", comments());
    extraInfo += generateSection("See also", "seealso", see());

    if (extraInfo.length() > 0)
      extraInfo = "\n<dl>" + extraInfo + "</dl>";

    return longdescription() + extraInfo;
  }

  public boolean hasInternalModifier() {
    return _hasInternalModifier;
  }
}
