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

package oxdoc.entities;

import oxdoc.Icon;
import oxdoc.comments.FieldComment;

import static oxdoc.entities.OxClass.Visibility;
import static oxdoc.util.Utils.checkNotNull;

public class OxField extends OxEntity {
  private final Visibility visibility;
  private boolean isStatic = false;
  private boolean isConstant = false;

  public OxField(String name, OxFile parentFile) {
    super(name, null, new FieldComment(parentFile.getProject()), parentFile, Icon.FIELD);
    visibility = Visibility.Public;
  }

  public OxField(String name, OxClass oxClass, Visibility visibility) {
    super(name, oxClass, new FieldComment(oxClass.getParentFile().getProject()), oxClass.getParentFile(), Icon.FIELD);
    this.visibility = checkNotNull(visibility);
  }

  @Override
  public String getUrl() {
    if (getParentClass() != null) {
      return getParentFileUrl() + "#" + getParentClass().getName() + "___" + getDisplayName();
    } else {
      return getParentFileUrl() + "#" + getDisplayName();
    }

  }

  public String getDeclaration() {
    StringBuilder declaration = new StringBuilder();
    declaration.append(getModifiers());
    if (declaration.length() > 0) {
      declaration.append(" ");
    }

    declaration.append("decl ");
    declaration.append(getName());

    if (getParentClass() != null) {
      declaration.append(" [");
      declaration.append(getVisibility());
      declaration.append("]");
    }
    return declaration.toString();
  }

  @Override
  public String getModifiers() {
    StringBuilder modifiers = new StringBuilder();
    if (isStatic) {
      modifiers.append("static ");
    }
    if (isConstant) {
      modifiers.append("const ");
    }
    return modifiers.toString().trim();
  }

  public Visibility getVisibility() {
    return visibility;
  }

  @Override
  public String toString() {
    return "<OxField " + getReferenceName() + ">";
  }

  @Override
  public FieldComment getComment() {
    return (FieldComment) super.getComment();
  }

  @Override
  public boolean isInternal() {
    return getComment().hasInternalModifier() || !getVisibility().equals(Visibility.Public);
  }

  public void setStatic(boolean isStatic) {
    this.isStatic = isStatic;
  }

  public void setConstant(boolean isConstant) {
    this.isConstant = isConstant;
  }
}
