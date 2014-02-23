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

import static oxdoc.Utils.checkNotNull;

public class SymbolIndex {

  private final OxProject project;
  private final ClassTree classTree;
  private final Hashtable entries; // entries, key: OxEntity, value: IndexEntry

  /*
   * Entry in the index. Every entry is associated with an entity. For
   * singleton entries in the index, it is associated with the entity it
   * refers to. However, methods are grouped according to class inheritance.
   * That is, if X() is a method of A, B is a subclass of A, and B
   * reimplements X(), then they show up together in one index entry. The
   * associated entity will be A::X(). The array owningClassMembers (for lack
   * of a better name) will contain A::X() and B::X().
   */
  private static class IndexEntry {
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

  public SymbolIndex(OxProject project, ClassTree classTree, Config config) {
    this.project = checkNotNull(project);
    this.classTree = checkNotNull(classTree);
    this.entries = constructIndex(project, config);
  }

  private static Hashtable constructIndex(OxProject project, Config config) {
    Hashtable entries = new Hashtable();

    ArrayList symbols = project.getSymbolsByDisplayName();

    for (int i = 0; i < symbols.size(); i++) {
      OxEntity entity = (OxEntity) symbols.get(i);
      if ((!config.showInternals) && entity.isInternal())
        continue;

      if (entity instanceof OxClass)
        addSingletonEntry(entries, entity, "Class");
      else if ((entity instanceof OxField) && (entity.getParentClass() == null))
        addSingletonEntry(entries, entity, "Global variable");
      else if (entity instanceof OxField)
        addGroupedEntry(entries, entity, entity, "Field");
      else if (entity instanceof OxEnumElement) {
        OxEnumElement element = (OxEnumElement) entity;
        addSingletonEntry(entries, entity, "Element of enumeration " + project.getLinkToEntity(element.getParentEnum()));
      } else if ((entity instanceof OxMethod) && (entity.getParentClass() == null))
        addSingletonEntry(entries, entity, "Global function");
      else if (entity instanceof OxMethod) {
        OxMethod method = (OxMethod) entity;
        OxClass parentClass = method.getParentClass();
        String type = "Method";
        if (method.getName().compareTo(parentClass.getName()) == 0)
          type = "Constructor";
        else if (method.getName().compareTo("~" + parentClass.getName()) == 0)
          type = "Destructor";

        // now, find furthest ancestor class that has the same method
        OxClass ancestorClass = parentClass;
        while ((ancestorClass.getSuperClass() != null)
            && (ancestorClass.getSuperClass().getMethodByName(method.getName()) != null))
          ancestorClass = ancestorClass.getSuperClass();

        addGroupedEntry(entries, ancestorClass.getMethodByName(method.getName()), entity, type);
      }
    }
    return entries;
  }

  private static IndexEntry addSingletonEntry(Hashtable entries, OxEntity entity, String type) {
    IndexEntry entry = new IndexEntry(entity.getName(), type, entity);
    entries.put(entity, entry);
    return entry;
  }

  private static IndexEntry addGroupedEntry(Hashtable entries, OxEntity ancestorEntity, OxEntity entity, String type) {
    IndexEntry entry = (IndexEntry) entries.get(ancestorEntity);
    if (entry == null)
      entry = new IndexEntry(ancestorEntity.getName(), type, entity);
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
        OxClass e1 = ((OxEntity) o1).getParentClass();
        OxClass e2 = ((OxEntity) o2).getParentClass();

        int depth1 = classTree.getClassDepth(e1);
        int depth2 = classTree.getClassDepth(e2);

        if (depth1 != depth2)
          return depth2 - depth1;

        return e1.getName().toUpperCase().compareTo(e2.getName().toUpperCase());
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
    Table table = new Table();
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
              + project.getLinkToEntity(memberEntity, memberEntity.getParentClass().getName());
        }
      }

      String[] row = {entity.getSmallIcon() + project.getLinkToEntity(entity), description};
      table.addRow(row);
    }
    output.writeln(table);
  }
}
