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
import oxdoc.OxProject;
import oxdoc.comments.BaseComment;

import static oxdoc.Utils.checkNotNull;

public class OxEntity {
  private final String name;
  private final OxFile parentFile;
  private final BaseComment comment;
  private final OxProject project;
  private final OxClass parentClass;
  private String declaration;
  private int iconType = FileManager.NONE;

  public OxEntity(String name, OxClass parentClass, BaseComment comment, OxProject project) {
    this.name = checkNotNull(name);
    this.project = checkNotNull(project);
    this.comment = checkNotNull(comment);
    this.parentClass = parentClass;
    parentFile = null;
  }

  public OxEntity(String name, OxClass parentClass, BaseComment comment, OxFile parentFile) {
    this.name = checkNotNull(name);
    this.project = parentFile.getProject();
    this.comment = checkNotNull(comment);
    this.parentFile = parentFile;
    this.parentClass = parentClass;
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
    if (parentClass == null)
      return getName();
    return parentClass.getName() + "::" + getName();
  }

  public int getIconType() {
    return iconType;
  }

  public String getSmallIcon() {
    return getProject().fileManager.getSmallIconHtml(iconType);
  }

  public String getLargeIcon() {
    return getProject().fileManager.getLargeIconHtml(iconType);
  }

  protected void setIconType(int iconType) {
    this.iconType = iconType;
  }

  public String getDescription() {
    return getProject().textProcessor.process(comment().description(), getProject());
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

  public BaseComment comment() {
    return comment;
  }

  public String getUrl() {
    return "";
  }

  public String getDisplayName() {
    return name;
  }

  public String getLink() {
    if (getUrl().length() == 0)
      return getDisplayName();
    else

      return "<a href=\"" + getUrl() + "\">" + getDisplayName() + "</a>";
  }

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
    if ((comment() == null) || (comment().sortKey() == null))
      return getName();
    return comment().sortKey();
  }

  public OxFile getParentFile() {
    return parentFile;
  }
}
