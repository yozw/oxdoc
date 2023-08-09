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
import oxdoc.comments.BaseComment;
import oxdoc.comments.FunctionComment;

public class OxMethod extends OxEntity {
  private boolean isVirtual = false;
  private boolean isStatic = false;

  public OxMethod(String name, OxFile parentFile) {
    super(name, null, new FunctionComment(parentFile.getProject()), parentFile, Icon.FUNCTION);
  }

  public OxMethod(String name, OxClass oxClass) {
    super(name, oxClass, new FunctionComment(oxClass.getParentFile().getProject()), oxClass.getParentFile(), Icon.METHOD);
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
    if (super.getDeclaration() == null) {
      return null;
    }
    return (getModifiers() + " " + super.getDeclaration()).trim();
  }

  public OxClass.Visibility getVisibility() {
    return OxClass.Visibility.Public;
    // every method is public in the current version of Ox
  }

  @Override
  public String getModifiers() {
    StringBuilder modifiers = new StringBuilder();
    if (isVirtual) {
      modifiers.append("virtual ");
    }
    if (isStatic) {
      modifiers.append("static ");
    }
    return modifiers.toString().trim();
  }

  @Override
  public BaseComment getComment() {
    BaseComment superComment = super.getComment();

    if (superComment.isEmpty() && superMethod() != null) {
      return superMethod().getComment();
    }

    return superComment;
  }

  @Override
  public boolean isInternal() {
    return ((FunctionComment) getComment()).hasInternalModifier();
  }

  public OxMethod superMethod() {
    if (getParentClass() == null || getParentClass().getSuperClass() == null) {
      return null;
    }

    return getParentClass().getSuperClass().getMethodByName(getDisplayName());
  }

  @Override
  public String toString() {
    return "<OxMethod " + getReferenceName() + ">";
  }

  public void setVirtual(boolean virtual) {
    isVirtual = virtual;
  }

  public void setStatic(boolean aStatic) {
    isStatic = aStatic;
  }
}
