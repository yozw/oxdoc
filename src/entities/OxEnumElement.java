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

public class OxEnumElement extends OxEntity {
  private final OxEnum oxEnum;

  public OxEnumElement(String name, OxEnum oxEnum) {
    super(name, null, null, oxEnum.getProject(), Icon.ENUM);
    this.oxEnum = oxEnum;
  }

  @Override
  public String getUrl() {
    return oxEnum.getUrl();
  }

  @Override
  public String getReferenceName() {
    return oxEnum.getReferenceName() + "$$" + getName();
  }

  @Override
  public String toString() {
    return "<OxEnumElement " + getReferenceName() + ">";
  }

  @Override
  public boolean isInternal() {
    return oxEnum.isInternal();
  }

  public OxEnum getParentEnum() {
    return oxEnum;
  }
}
