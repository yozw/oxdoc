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
import oxdoc.comments.FieldComment;

public class OxField extends OxEntity {
  private OxClass.Visibility visibility;
  private boolean isStatic = false;
  private boolean isConstant = false;

  public OxField(String name, OxFile parentFile) {
    super(name, null, new FieldComment(parentFile.getProject()), parentFile);

    setIconType(FileManager.FIELD);
    visibility = OxClass.Visibility.Public;
  }

  public OxField(String name, OxClass oxclass, OxClass.Visibility visibility) {
    super(name, oxclass, new FieldComment(oxclass.getParentFile().getProject()), oxclass.getParentFile());
    setIconType(FileManager.FIELD);
    this.visibility = visibility;
  }

  public String getUrl() {
    if (getParentClass() != null)
      return getParentFileUrl() + "#" + getParentClass().getName() + "___" + getDisplayName();
    else
      return getParentFileUrl() + "#" + getDisplayName();

  }

  public String getDeclaration() {
    String decl = getModifiers() + " ";
    decl += " decl " + getName();
    if (getParentClass() != null)
      decl += " [" + getVisibility() + "]";
    return decl.trim();
  }

  public String getModifiers() {
    String mod = "";
    if (isStatic)
      mod += "static ";
    if (isConstant)
      mod += "const ";
    return mod.trim();
  }

  public OxClass.Visibility getVisibility() {
    return visibility;
  }

  public String toString() {
    return "<OxField " + getReferenceName() + ">";
  }

  public boolean isInternal() {
    return ((FieldComment) getComment()).hasInternalModifier() || (getVisibility() != OxClass.Visibility.Public);
  }

  public void setStatic(boolean aStatic) {
    isStatic = aStatic;
  }

  public void setConstant(boolean constant) {
    isConstant = constant;
  }
}
