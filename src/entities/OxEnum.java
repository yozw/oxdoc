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

package oxdoc.entities;

import oxdoc.FileManager;
import oxdoc.comments.EnumComment;

import java.util.ArrayList;
import java.util.List;

import static oxdoc.Utils.checkNotNull;

public class OxEnum extends OxEntity {
  private final ArrayList<OxEnumElement> elements = new ArrayList<OxEnumElement>();
  private OxClass.Visibility visibility;

  public OxEnum(String name, List<String> elements, OxClass oxclass, OxClass.Visibility visibility) {
    super(name, oxclass, new EnumComment(oxclass.getParentFile().getProject()), oxclass.getParentFile());
    setIconType(FileManager.ENUM);

    for (String element : elements) {
      this.elements.add(new OxEnumElement(element, this));
    }
    this.visibility = checkNotNull(visibility);
  }

  public OxEnum(String name, List<String> elements, OxFile oxfile) {
    super(name, null, new EnumComment(oxfile.getProject()), oxfile);

    setIconType(FileManager.ENUM);
    for (String element : elements) {
      this.elements.add(new OxEnumElement(element, this));
    }
    visibility = OxClass.Visibility.Public;
  }

  public String getUrl() {
    if (getParentClass() != null)
      return getParentFileUrl() + "#" + getParentClass().getName() + "___" + getDisplayName();
    else
      return getParentFileUrl() + "#" + getDisplayName();
  }

  public String getDeclaration() {
    String decl = "";
    decl += " enum { " + getElementString() + " }";
    if (getParentClass() != null)
      decl += " [" + getVisibility() + "]";
    return decl;
  }

  public String getElementString() {
    String decl = "";
    int index = 0;
    for (OxEnumElement element : elements) {
      if (index != 0)
        decl += ", ";
      decl += element.getName();
      index++;
    }
    return decl;
  }

  public OxClass.Visibility getVisibility() {
    return visibility;
  }

  public String toString() {
    return "<OxEnum " + getReferenceName() + ">";
  }

  public boolean isInternal() {
    return ((EnumComment) getComment()).hasInternalModifier() || (getVisibility() != OxClass.Visibility.Public);
  }

  public ArrayList<OxEnumElement> getElements() {
    return elements;
  }
}
