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
import oxdoc.comments.ClassComment;

import java.util.ArrayList;
import java.util.HashSet;

public class OxClass extends OxEntity {

  private int enumCounter = 0;

  public enum Visibility {
    Private {
      public String toString() {
        return "private";
      }
    },
    Protected {
      public String toString() {
        return "protected";
      }
    },
    Public {
      public String toString() {
        return "public";
      }
    }
  }

  ;

  private interface MemberFilter {
    boolean keepItem(OxEntity entity);
  };

  private final OxEntityList members = new OxEntityList();
  private String superClassName = null;

  public OxClass(String name, OxFile parentFile) {
    super(name, null, new ClassComment(parentFile.getProject()), parentFile);
    setIconType(FileManager.CLASS);
  }

  public OxClass(String name, String superClassName, OxFile parentFile) {
    super(name, null, new ClassComment(parentFile.getProject()), parentFile);
    setIconType(FileManager.CLASS);
    this.superClassName = superClassName;
  }

  public OxMethod addMethod(String name) {
    return (OxMethod) members.add(new OxMethod(name, this));
  }

  public OxField addField(String name, Visibility vis) {
    return (OxField) members.add(new OxField(name, this, vis));
  }

  public OxEnum addEnum(String alternativeName, ArrayList elements, Visibility vis) {
    if ((alternativeName == null) || (alternativeName.length() == 0)) {
      enumCounter++;
      alternativeName = "Anonymous enum " + enumCounter;
    }
    String[] _elements = new String[elements.size()];
    for (int i = 0; i < elements.size(); i++)
      _elements[i] = elements.get(i).toString();
    return (OxEnum) members.add(new OxEnum(alternativeName, _elements, this, vis));
  }

  public ArrayList getMembers() {
    return members.sortedList();
  }

  public ArrayList filterMembers(MemberFilter filter) {
    ArrayList members = getMembers();
    ArrayList list = new ArrayList();

    for (int i = 0; i < members.size(); i++) {
      OxEntity entity = (OxEntity) members.get(i);
      if (filter.keepItem(entity))
        list.add(entity);
    }
    return list;
  }

  public ArrayList filterInheritedMembers(MemberFilter filter) {
    ArrayList members = getInheritedMembers();
    ArrayList list = new ArrayList();

    for (int i = 0; i < members.size(); i++) {
      OxEntity entity = (OxEntity) members.get(i);
      if (filter.keepItem(entity))
        list.add(entity);
    }
    return list;
  }

  public ArrayList getPrivateFields() {
    return filterMembers(new MemberFilter() {
      public boolean keepItem(OxEntity entity) {
        return ((entity instanceof OxField) && (((OxField) entity).getVisibility() == Visibility.Private));
      }
    });
  }

  public ArrayList getProtectedFields() {
    return filterMembers(new MemberFilter() {
      public boolean keepItem(OxEntity entity) {
        return ((entity instanceof OxField) && (((OxField) entity).getVisibility() == Visibility.Protected));
      }
    });
  }

  public ArrayList getPublicFields() {
    return filterMembers(new MemberFilter() {
      public boolean keepItem(OxEntity entity) {
        return ((entity instanceof OxField) && (((OxField) entity).getVisibility() == Visibility.Public));
      }
    });
  }

  public ArrayList getMethods() {
    return filterMembers(new MemberFilter() {
      public boolean keepItem(OxEntity entity) {
        return (entity instanceof OxMethod);
      }
    });
  }

  public ArrayList getMethodsAndFields() {
    return filterMembers(new MemberFilter() {
      public boolean keepItem(OxEntity entity) {
        return (entity instanceof OxMethod) || (entity instanceof OxField);
      }
    });
  }

  public ArrayList getEnums() {
    return filterMembers(new MemberFilter() {
      public boolean keepItem(OxEntity entity) {
        return (entity instanceof OxEnum);
      }
    });
  }

  public ArrayList getInheritedFields() throws Exception {
    return filterInheritedMembers(new MemberFilter() {
      public boolean keepItem(OxEntity entity) {
        return (entity instanceof OxField);
      }
    });
  }

  public ArrayList getInheritedMethods() throws Exception {
    return filterInheritedMembers(new MemberFilter() {
      public boolean keepItem(OxEntity entity) {
        return (entity instanceof OxMethod);
      }
    });
  }

  public ArrayList getInheritedEnums() throws Exception {
    return filterInheritedMembers(new MemberFilter() {
      public boolean keepItem(OxEntity entity) {
        return (entity instanceof OxEnum);
      }
    });
  }

  public ArrayList getSuperClasses() {
    ArrayList list = new ArrayList();
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

  public ArrayList getInheritedMembers() {
    ArrayList list = new ArrayList();
    OxClass currentClass = this;
    HashSet seenMemberNames = new HashSet();
    ArrayList members = getMembers();
    for (int i = 0; i < members.size(); i++) {
      OxEntity member = (OxEntity) members.get(i);
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

      for (int i = 0; i < members.size(); i++) {
        OxEntity member = (OxEntity) members.get(i);
        if (seenMemberNames.contains(member.getName()))
          continue;
        seenMemberNames.add(member.getName());

        if (member instanceof OxMethod) {
          OxMethod oxMethod = (OxMethod) member;
          if (oxMethod.visibility() != OxClass.Visibility.Private)
            list.add(oxMethod);
        } else if (member instanceof OxField) {
          OxField oxField = (OxField) member;
          if (oxField.getVisibility() != OxClass.Visibility.Private)
            list.add(oxField);
        } else if (member instanceof OxEnum) {
          OxEnum oxEnum = (OxEnum) member;
          if (oxEnum.getVisibility() != OxClass.Visibility.Private)
            list.add(oxEnum);
        } else
          throw new java.lang.Error("Class member has unexpected class: " + member);
      }
    }
    return list;
  }

  public OxMethod getMethodByName(String s) {
    return (OxMethod) members.get(s);
  }

  public OxField getFieldByName(String s) {
    return (OxField) members.get(s);
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
    /** Modfied by CF **/
  }

  public String toString() {
    return "<OxClass " + getName() + ">";
  }
}
