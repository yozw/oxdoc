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

package oxdoc;

import oxdoc.entities.*;
import oxdoc.html.Table;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;

class SymbolIndex {

  private OxProject project = null;
  private OxDoc oxdoc = null;
  private ClassTree classTree = null;

  /*
   * Entry in the index. Every entry is associated with an entity. For
   * singleton entries in the index, it is associated with the entity it
   * refers to. However, methods are grouped according to class inheritance.
   * That is, if X() is a method of A, B is a subclass of A, and B
   * reimplements X(), then they show up together in one index entry. The
   * associated entity will be A::X(). The array owningClassMembers (for lack
   * of a better name) will contain A::X() and B::X().
   */
  private class IndexEntry {
    String text = "";
    String type = "";
    OxEntity entity = null; // the entity associated with the index entry
    ArrayList owningClassMembers = new ArrayList(); // the entities as
    // members of classes
    // (in case of
    // inheritance)

    IndexEntry(String text, String type, OxEntity entity) {
      this.text = text;
      this.type = type;
      this.entity = entity;
    }
  }

  Hashtable entries = null; // entries, key: OxEntity, value: IndexEntry

  public SymbolIndex(OxDoc oxdoc, ClassTree classTree) {
    this.oxdoc = oxdoc;
    project = oxdoc.project;
    this.classTree = classTree;

    constructIndex();
  }

  private void constructIndex() {
    entries = new Hashtable();

    ArrayList symbols = project.symbolsByDisplayName();

    for (int i = 0; i < symbols.size(); i++) {
      OxEntity entity = (OxEntity) symbols.get(i);
      if ((!oxdoc.config.ShowInternals) && entity.isInternal())
        continue;

      if (entity instanceof OxClass)
        addSingletonEntry(entity, "Class");
      else if ((entity instanceof OxField) && (entity.parentClass() == null))
        addSingletonEntry(entity, "Global variable");
      else if (entity instanceof OxField)
        addGroupedEntry(entity, entity, "Field");
      else if (entity instanceof OxEnumElement) {
        OxEnumElement element = (OxEnumElement) entity;
        addSingletonEntry(entity, "Element of enumeration " + project.linkToEntity(element.parentEnum()));
      } else if ((entity instanceof OxMethod) && (entity.parentClass() == null))
        addSingletonEntry(entity, "Global function");
      else if (entity instanceof OxMethod) {
        OxMethod method = (OxMethod) entity;
        OxClass parentClass = method.parentClass();
        String type = "Method";
        if (method.name().compareTo(parentClass.name()) == 0)
          type = "Constructor";
        else if (method.name().compareTo("~" + parentClass.name()) == 0)
          type = "Destructor";

        // now, find furthest ancestor class that has the same method
        OxClass ancestorClass = parentClass;
        while ((ancestorClass.superClass() != null)
            && (ancestorClass.superClass().methodByName(method.name()) != null))
          ancestorClass = ancestorClass.superClass();

        addGroupedEntry(ancestorClass.methodByName(method.name()), entity, type);
      }
    }
  }

  private IndexEntry addSingletonEntry(OxEntity entity, String type) {
    IndexEntry entry = new IndexEntry(entity.name(), type, entity);
    entries.put(entity, entry);
    return entry;
  }

  private IndexEntry addGroupedEntry(OxEntity ancestorEntity, OxEntity entity, String type) {
    IndexEntry entry = (IndexEntry) entries.get(ancestorEntity);
    if (entry == null)
      entry = new IndexEntry(ancestorEntity.name(), type, entity);
    entry.owningClassMembers.add(entity);
    entries.put(ancestorEntity, entry);
    return entry;
  }

  private void sortEntriesByName(ArrayList indexEntries) {
    Collections.sort(indexEntries, new Comparator() {
      public int compare(Object o1, Object o2) {
        IndexEntry e1 = (IndexEntry) o1;
        IndexEntry e2 = (IndexEntry) o2;

        return e1.text.toUpperCase().compareTo(e2.text.toUpperCase());
      }
    });
  }

  // this method sorts the owning class members first in decreasing order of
  // depth of the class, and subject to
  // that, by name
  private void sortOwningClassMembers(ArrayList members) {
    Collections.sort(members, new Comparator() {
      public int compare(Object o1, Object o2) {
        OxClass e1 = ((OxEntity) o1).parentClass();
        OxClass e2 = ((OxEntity) o2).parentClass();

        int depth1 = classTree.getClassDepth(e1);
        int depth2 = classTree.getClassDepth(e2);

        if (depth1 != depth2)
          return depth2 - depth1;

        return e1.name().toUpperCase().compareTo(e2.name().toUpperCase());
      }
    });
  }

  public void write(OutputFile output) throws Exception {

    // store entries in an array list
    ArrayList indexEntries = new ArrayList();
    indexEntries.addAll(entries.values());

    // sort entries by name
    sortEntriesByName(indexEntries);

    // write table
    Table table = new Table(oxdoc);
    table.specs().cssClass = "index";
    table.specs().columnCssClasses.add("declaration");
    table.specs().columnCssClasses.add("description");

    for (int i = 0; i < indexEntries.size(); i++) {
      IndexEntry entry = (IndexEntry) indexEntries.get(i);
      OxEntity entity = entry.entity;
      String description = entry.type;
      if (entry.owningClassMembers.size() > 0) {
        sortOwningClassMembers(entry.owningClassMembers);
        description += " of ";
        for (int j = 0; j < entry.owningClassMembers.size(); j++) {
          OxEntity memberEntity = (OxEntity) entry.owningClassMembers.get(j);
          description += (j == 0 ? "" : ", ")
              + project.linkToEntity(memberEntity, memberEntity.parentClass().name());
        }
      }

      String[] row = {entity.smallIcon() + project.linkToEntity(entity), description};
      table.addRow(row);
    }
    output.writeln(table);
  }
}
