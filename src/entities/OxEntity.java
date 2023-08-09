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
import oxdoc.OxProject;
import oxdoc.comments.BaseComment;

import static oxdoc.util.Utils.checkNotNull;

public class OxEntity {
  private final String name;
  private final OxFile parentFile;
  private final BaseComment comment;
  private final OxProject project;
  private final OxClass parentClass;
  private final Icon icon;
  private String declaration;

  public OxEntity(String name, OxClass parentClass, BaseComment comment, OxProject project, Icon icon) {
    this.name = checkNotNull(name);
    this.project = checkNotNull(project);
    this.comment = comment;
    this.parentClass = parentClass;
    this.icon = icon;
    parentFile = null;
  }

  public OxEntity(String name, OxClass parentClass, BaseComment comment, OxFile parentFile, Icon icon) {
    this.name = checkNotNull(name);
    this.project = parentFile.getProject();
    this.comment = comment;
    this.parentFile = parentFile;
    this.parentClass = parentClass;
    this.icon = icon;
  }

  public OxProject getProject() {
    return project;
  }

  public void setDeclaration(String declaration) {
    this.declaration = checkNotNull(declaration);
  }

  public OxClass getParentClass() {
    return parentClass;
  }

  public String getName() {
    return name;
  }

  public String getReferenceName() {
    if (parentClass == null) {
      return getName();
    }
    return parentClass.getName() + "::" + getName();
  }

  public Icon getIcon() {
    return icon;
  }

  public String getDescription() {
    return getProject().getTextProcessor().process(getComment().description(), getProject());
  }

  protected String getParentFileUrl() {
    return (parentFile == null) ? "" : parentFile.getUrl();
  }

  public BaseComment setComment(String comment) throws Exception {
    this.comment.setText(comment);
    return this.comment;
  }

  public String getModifiers() {
    return "";
  }

  public BaseComment getComment() {
    return comment;
  }

  public String getUrl() {
    return "";
  }

  public String getDisplayName() {
    return name;
  }

  public String getLink() {
    if (getUrl().length() == 0) {
      return getDisplayName();
    } else

    {
      return "<a href=\"" + getUrl() + "\">" + getDisplayName() + "</a>";
    }
  }

  @Override
  public String toString() {
    return "<OxEntity " + getName() + ">";
  }

  public String getDeclaration() {
    return declaration;
  }

  public boolean isInternal() {
    return false;
  }

  public String getSortKey() {
    if ((getComment() == null) || (getComment().sortKey() == null)) {
      return getName();
    }
    return getComment().sortKey();
  }

  public OxFile getParentFile() {
    return parentFile;
  }

  @Override
  public int hashCode() {
    return getReferenceName().hashCode() + 2 * getClass().hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (!obj.getClass().equals(this.getClass())) {
      return false;
    }
    OxEntity other = (OxEntity) obj;
    return other.getReferenceName().equals(this.getReferenceName());
  }
}
