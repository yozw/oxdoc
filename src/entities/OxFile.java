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
import oxdoc.comments.FileComment;

import java.util.ArrayList;

public class OxFile extends OxEntity {
  private final OxEntityList<OxMethod> functions = new OxEntityList<OxMethod>();
  private final OxEntityList<OxClass> classes = new OxEntityList<OxClass>();
  private final OxEntityList<OxField> variables = new OxEntityList<OxField>();
  private final OxEntityList<OxEnum> enums = new OxEntityList<OxEnum>();

  private int enumCounter = 0;

  public OxFile(String fileName, OxProject project) {
    super(fileName, null, new FileComment(project), project, Icon.FILE);
  }

  public OxMethod addFunction(String name) {
    return functions.add(new OxMethod(name, this));
  }

  public OxField addVariable(String name) {
    return variables.add(new OxField(name, this));
  }

  public OxEnum addEnum(String alternativeName, ArrayList<String> elements) {
    if ((alternativeName == null) || (alternativeName.length() == 0)) {
      enumCounter++;
      alternativeName = "Anonymous enum " + enumCounter;
    }
    return enums.add(new OxEnum(alternativeName, elements, this));
  }

  public OxClass addClass(String name) {
    return classes.add(new OxClass(name, this));
  }

  public OxClass addClass(String name, String parentClassName) {
    return classes.add(new OxClass(name, parentClassName, this));
  }

  public OxClass getClass(String name) {
    return classes.get(name);
  }

  public OxEntityList<OxMethod> getFunctions() {
    return functions;
  }

  public OxEntityList<OxEnum> getEnums() {
    return enums;
  }

  public OxEntityList<OxField> getVariables() {
    return variables;
  }

  public OxEntityList<OxEntity> getFunctionsAndVariables() {
    OxEntityList<OxEntity> newList = new OxEntityList<OxEntity>();
    newList.addAll(functions);
    newList.addAll(variables);
    return newList;
  }

  public OxEntityList<OxClass> getClasses() {
    return classes;
  }

  @Override
  public String getUrl() {
    return getName() + ".html";
  }

  @Override
  public String toString() {
    return "<OxFile " + getName() + ">";
  }
}
