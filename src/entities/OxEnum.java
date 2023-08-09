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

package oxdoc.entities;

import oxdoc.Icon;
import oxdoc.comments.EnumComment;

import java.util.ArrayList;
import java.util.List;

import static oxdoc.util.Utils.checkNotNull;

public class OxEnum extends OxEntity {
  private final ArrayList<OxEnumElement> elements = new ArrayList<OxEnumElement>();
  private OxClass.Visibility visibility;

  public OxEnum(String name, List<String> elements, OxClass oxClass, OxClass.Visibility visibility) {
    super(name, oxClass, new EnumComment(oxClass.getParentFile().getProject()), oxClass.getParentFile(), Icon.ENUM);
    for (String element : elements) {
      this.elements.add(new OxEnumElement(element, this));
    }
    this.visibility = checkNotNull(visibility);
  }

  public OxEnum(String name, List<String> elements, OxFile file) {
    super(name, null, new EnumComment(file.getProject()), file, Icon.ENUM);
    for (String element : elements) {
      this.elements.add(new OxEnumElement(element, this));
    }
    visibility = OxClass.Visibility.Public;
  }

  @Override
  public String getUrl() {
    if (getParentClass() != null) {
      return getParentFileUrl() + "#" + getParentClass().getName() + "___" + getDisplayName();
    } else {
      return getParentFileUrl() + "#" + getDisplayName();
    }
  }

  @Override
  public String getDeclaration() {
    StringBuilder declaration = new StringBuilder();
    declaration.append(" enum { ");
    declaration.append(getElementString());
    declaration.append(" }");

    if (getParentClass() != null) {
      declaration.append(" [");
      declaration.append(getVisibility());
      declaration.append("]");
    }
    return declaration.toString();
  }

  public String getElementString() {
    StringBuilder elementStr = new StringBuilder();
    for (OxEnumElement element : elements) {
      if (elementStr.length() > 0) {
        elementStr.append(", ");
      }
      elementStr.append(element.getName());
    }
    return elementStr.toString();
  }

  public OxClass.Visibility getVisibility() {
    return visibility;
  }

  @Override
  public String toString() {
    return "<OxEnum " + getReferenceName() + ">";
  }

  @Override
  public boolean isInternal() {
    return ((EnumComment) getComment()).hasInternalModifier() || (getVisibility() != OxClass.Visibility.Public);
  }

  public ArrayList<OxEnumElement> getElements() {
    return elements;
  }
}
