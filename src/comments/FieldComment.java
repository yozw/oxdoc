/**

 oxdoc (c) Copyright 2005-2023 by Y. Zwols

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

import static oxdoc.util.Utils.allNullOrEmpty;

public class FieldComment extends BaseComment {
  private final static int MODIFIER_INTERNAL = 300;

  private boolean hasInternalModifier = false;

  public FieldComment(OxProject project) {
    super(project);
    registerModifier("internal", MODIFIER_INTERNAL);
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

  @Override
  public String toString() {
    String exampleStr = generateSection("Example", "example", example());
    String commentStr = generateSection("Comments", "comments", comments());
    String seeAlsoStr = generateSection("See also", "seealso", see());

    StringBuilder result = new StringBuilder();
    result.append(longdescription());

    if (!allNullOrEmpty(exampleStr, commentStr, seeAlsoStr)) {
      result.append("\n<dl>");
      result.append(exampleStr);
      result.append(commentStr);
      result.append(seeAlsoStr);
      result.append("</dl>");
    }

    return result.toString();
  }

  public boolean hasInternalModifier() {
    return hasInternalModifier;
  }
}
