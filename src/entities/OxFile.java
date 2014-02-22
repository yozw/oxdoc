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
  private OxEntityList _functions = new OxEntityList();
  private OxEntityList _classes = new OxEntityList();
  private OxEntityList _variables = new OxEntityList();
  private OxEntityList _enums = new OxEntityList();
  /**
   * added by CF *
   */
  private int enumCounter = 0;

  public OxFile(String fileName, OxProject project) {
    super(fileName, null, new FileComment(project), project);
    setIconType(FileManager.FILE);
  }

  public OxMethod addFunction(String name) {
    return (OxMethod) _functions.add(new OxMethod(name, this));
  }

  public OxField addVariable(String name) {
    return (OxField) _variables.add(new OxField(name, this));
  }

  public OxEnum addEnum(String alternativeName, ArrayList elements) {
    if ((alternativeName == null) || (alternativeName.length() == 0)) {
      enumCounter++;
      alternativeName = "Anonymous enum " + enumCounter;
    }
    String[] _elements = new String[elements.size()];
    for (int i = 0; i < elements.size(); i++)
      _elements[i] = elements.get(i).toString();
    return (OxEnum) _enums.add(new OxEnum(alternativeName, _elements, this));
  }

  public OxClass addClass(String name) {
    return (OxClass) _classes.add(new OxClass(name, this));
  }

  public OxClass addClass(String name, String parentclassname) {
    return (OxClass) _classes.add(new OxClass(name, parentclassname, this));
  }

  public OxClass getClass(String name) {
    return (OxClass) _classes.get(name);
  }

  public ArrayList functions() {
    return _functions.sortedList();
  }

  public ArrayList enums() {
    return _enums.sortedList();
  }

  public ArrayList variables() {
    return _variables.sortedList();
  }

  public ArrayList functionsAndVariables() {
    OxEntityList newList = new OxEntityList();
    newList.addAll(_functions);
    newList.addAll(_variables);
    return newList.sortedList();
  }

  public ArrayList classes() {
    return _classes.sortedList();
  }

  public String url() {
    return name() + ".html";
  }

  public String toString() {
    return "<OxFile " + name() + ">";
  }
}
