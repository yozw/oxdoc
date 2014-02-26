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

public class EnumComment extends FieldComment {
  private final static int SECTION_NAME = 500, MODIFIER_INTERNAL = 300;

  private String alternativeName = "";
  private boolean hasInternalModifier = false;

  public EnumComment(OxProject project) {
    super(project);

    registerSection("name", SECTION_NAME);
    registerModifier("internal", MODIFIER_INTERNAL);
  }

  protected boolean addToSection(int sectionId, String text) {
    if (super.addToSection(sectionId, text)) {
      return true;
    }

    switch (sectionId) {
      case SECTION_NAME:
        alternativeName += text;
        break;
      default:
        return false;
    }
    return true;
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

  public String alternativeName() {
    return alternativeName;
  }

  public boolean hasInternalModifier() {
    return hasInternalModifier;
  }

}
