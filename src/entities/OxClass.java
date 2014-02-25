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
import oxdoc.comments.ClassComment;
import oxdoc.util.Predicate;

import java.util.ArrayList;
import java.util.HashSet;

import static oxdoc.util.Utils.checkNotNull;

public class OxClass extends OxEntity {

  private int enumCounter = 0;

  public enum Visibility {
    Private("private"),
    Protected("protected)"),
    Public("public");

    private final String name;

    Visibility(String name) {
      this.name = checkNotNull(name);
    }

    @Override
    public String toString() {
      return name;
    }
  }

  private final OxEntityList<OxEntity> members = new OxEntityList<OxEntity>();
  private final String superClassName;

  public OxClass(String name, OxFile parentFile) {
    super(name, null, new ClassComment(parentFile.getProject()), parentFile, Icon.CLASS);
    this.superClassName = null;
  }

  public OxClass(String name, String superClassName, OxFile parentFile) {
    super(name, null, new ClassComment(parentFile.getProject()), parentFile, Icon.CLASS);
    this.superClassName = superClassName;
  }

  public OxMethod addMethod(String name) {
    return members.add(new OxMethod(name, this));
  }

  public OxField addField(String name, Visibility vis) {
    return members.add(new OxField(name, this, vis));
  }

  public OxEnum addEnum(String alternativeName, ArrayList<String> elements, Visibility vis) {
    if ((alternativeName == null) || (alternativeName.length() == 0)) {
      enumCounter++;
      alternativeName = "Anonymous enum " + enumCounter;
    }
    return members.add(new OxEnum(alternativeName, elements, this, vis));
  }

  public OxEntityList<OxEntity> getMembers() {
    return members;
  }

  public OxEntityList<OxField> getFields(final Visibility visibility) {
    return members.filterByClass(OxField.class).filter(new Predicate<OxField>() {
      public boolean apply(OxField entity) {
        return entity.getVisibility().equals(visibility);
      }
    });
  }

  public OxEntityList<OxMethod> getMethods() {
    return members.filterByClass(OxMethod.class);
  }

  public OxEntityList<OxEntity> getMethodsAndFields() {
    return members.filterByClass(OxMethod.class, OxField.class);
  }

  public OxEntityList<OxEnum> getEnums() {
    return members.filterByClass(OxEnum.class);
  }

  public OxEntityList<OxField> getInheritedFields() throws Exception {
    return getInheritedMembers().filterByClass(OxField.class);
  }

  public OxEntityList<OxMethod> getInheritedMethods() throws Exception {
    return getInheritedMembers().filterByClass(OxMethod.class);
  }

  public OxEntityList<OxEnum> getInheritedEnums() throws Exception {
    return getInheritedMembers().filterByClass(OxEnum.class);
  }

  public OxEntityList<OxClass> getSuperClasses() {
    OxEntityList<OxClass> list = new OxEntityList<OxClass>();
    OxClass currentClass = this;

    while (true) {
      String superClassName = currentClass.getSuperClassName();
      if (superClassName == null)
        break;

      OxEntity entity = getProject().getSymbol(superClassName);
      if ((entity == null) || !(entity instanceof OxClass))
        break;

      currentClass = (OxClass) entity;
      list.add(currentClass);
    }
    return list;
  }

  public OxEntityList<OxEntity> getInheritedMembers() {
    OxEntityList<OxEntity> result = new OxEntityList<OxEntity>();
    OxClass currentClass = this;
    HashSet<String> seenMemberNames = new HashSet<String>();
    OxEntityList<OxEntity> members = getMembers();

    for (OxEntity member : members) {
      seenMemberNames.add(member.getName());
    }

    while (true) {
      String superClassName = currentClass.getSuperClassName();
      if (superClassName == null)
        break;

      OxEntity entity = getProject().getSymbol(superClassName);
      if ((entity == null) || !(entity instanceof OxClass))
        break;
      currentClass = (OxClass) entity;

      members = currentClass.getMembers();

      for (OxEntity member : members) {
        if (seenMemberNames.contains(member.getName()))
          continue;
        seenMemberNames.add(member.getName());

        if (member instanceof OxMethod) {
          OxMethod oxMethod = (OxMethod) member;
          if (oxMethod.getVisibility() != Visibility.Private)
            result.add(oxMethod);
        } else if (member instanceof OxField) {
          OxField oxField = (OxField) member;
          if (oxField.getVisibility() != Visibility.Private)
            result.add(oxField);
        } else if (member instanceof OxEnum) {
          OxEnum oxEnum = (OxEnum) member;
          if (oxEnum.getVisibility() != Visibility.Private)
            result.add(oxEnum);
        } else
          throw new Error("Class member has unexpected class: " + member);
      }
    }
    return result;
  }

  public OxMethod getMethodByName(String s) {
    return (OxMethod) members.get(s);
  }

  public String getSuperClassName() {
    return superClassName;
  }

  public OxClass getSuperClass() {
    if (superClassName == null)
      return null;

    return (OxClass) getParentFile().getProject().getSymbol(superClassName);
  }

  public String getUrl() {
    return getParentFile().getUrl() + "#" + getName();
  }

  public String toString() {
    return "<OxClass " + getName() + ">";
  }
}
