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
import oxdoc.comments.FileComment;

import java.util.ArrayList;

public class OxFile extends OxEntity {
  private final OxEntityList functions = new OxEntityList();
  private final OxEntityList classes = new OxEntityList();
  private final OxEntityList variables = new OxEntityList();
  private final OxEntityList enums = new OxEntityList();

  private int enumCounter = 0;

  public OxFile(String fileName, OxProject project) {
    super(fileName, null, new FileComment(project), project);
    setIconType(FileManager.FILE);
  }

  public OxMethod addFunction(String name) {
    return (OxMethod) functions.add(new OxMethod(name, this));
  }

  public OxField addVariable(String name) {
    return (OxField) variables.add(new OxField(name, this));
  }

  public OxEnum addEnum(String alternativeName, ArrayList<OxEnumElement> elements) {
    if ((alternativeName == null) || (alternativeName.length() == 0)) {
      enumCounter++;
      alternativeName = "Anonymous enum " + enumCounter;
    }

    ArrayList<String> stringElements = new ArrayList<String>();
    for (Object element : elements) {
      stringElements.add(element.toString());
    }
    return (OxEnum) enums.add(new OxEnum(alternativeName, stringElements, this));
  }

  public OxClass addClass(String name) {
    return (OxClass) classes.add(new OxClass(name, this));
  }

  public OxClass addClass(String name, String parentClassName) {
    return (OxClass) classes.add(new OxClass(name, parentClassName, this));
  }

  public OxClass getClass(String name) {
    return (OxClass) classes.get(name);
  }

  public ArrayList<OxEntity> getFunctions() {
    return functions.sortedList();
  }

  public ArrayList<OxEntity> getEnums() {
    return enums.sortedList();
  }

  public ArrayList<OxEntity> getVariables() {
    return variables.sortedList();
  }

  public ArrayList<OxEntity> getFunctionsAndVariables() {
    OxEntityList newList = new OxEntityList();
    newList.addAll(functions);
    newList.addAll(variables);
    return newList.sortedList();
  }

  public ArrayList<OxEntity> getClasses() {
    return classes.sortedList();
  }

  public String getUrl() {
    return getName() + ".html";
  }

  public String toString() {
    return "<OxFile " + getName() + ">";
  }
}
