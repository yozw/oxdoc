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
  public String declaration;

  protected final OxFile parentFile;

  private final String name;
  private final BaseComment comment;
  private final OxProject project;
  private final OxClass parentClass;
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
    this.project = parentFile.project();
    this.comment = checkNotNull(comment);
    this.parentFile = parentFile;
    this.parentClass = parentClass;
  }

  public OxProject project() {
    return project;
  }

  public OxClass parentClass() {
    return parentClass;
  }

  public String name() {
    return name;
  }

  public String referenceName() {
    if (parentClass == null)
      return name();
    return parentClass.name() + "::" + name();
  }

  public int iconType() {
    return iconType;
  }

  public String smallIcon() {
    return project().fileManager.smallIcon(iconType);
  }

  public String largeIcon() {
    return project().fileManager.largeIcon(iconType);
  }

  protected void setIconType(int iconType) {
    this.iconType = iconType;
  }

  public String description() {
    return project().textProcessor.process(comment().description(), project());
  }

  protected String parentFileUrl() {
    return (parentFile == null) ? "" : parentFile.url();
  }

  public BaseComment setComment(String comment) throws Exception {
    this.comment.setText(comment);

    return this.comment;
  }

  public String modifiers() {
    return "";
  }

  public BaseComment comment() {
    return comment;
  }

  public String url() {
    return "";
  }

  public String displayName() {
    return name;
  }

  public String link() {
    if (url().length() == 0)
      return displayName();
    else

      return "<a href=\"" + url() + "\">" + displayName() + "</a>";
  }

  public String toString() {
    return "<OxEntity " + name() + ">";
  }

  public String declaration() {
    return declaration;
  }

  public boolean isInternal() {
    return false;
  }

  public String sortKey() {
    if ((comment() == null) || (comment().sortKey() == null))
      return name();
    return comment().sortKey();
  }

  public OxFile parentFile() {
    return parentFile;
  }
}
