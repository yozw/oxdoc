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

package oxdoc;

public enum Icon {
  NONE(null),
  INDEX("index"),
  PROJECT("project"),
  FILE("file"),
  CLASS("class"),
  METHOD("method"),
  FUNCTION("function"),
  FIELD("field"),
  ENUM("enum"),
  UPLEVEL("uplevel"),
  HIERARCHY("hierarchy"),
  GLOBAL("global"),
  FILES("files");

  private final String fileName;

  private Icon(String fileName) {
    this.fileName = fileName;
  }

  public String getFileName() {
    return fileName;
  }
}
