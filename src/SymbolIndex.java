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

package oxdoc;

import oxdoc.entities.*;
import oxdoc.html.Table;

import java.util.*;

import static oxdoc.util.Utils.checkNotNull;

public class SymbolIndex {

  public static final Comparator<IndexEntry> COMPARATOR_BY_NAME = new Comparator<IndexEntry>() {
    public int compare(IndexEntry e1, IndexEntry e2) {
      return e1.text.compareToIgnoreCase(e2.text);
    }
  };
  private final OxProject project;
  private final ClassTree classTree;
  private final Map<OxEntity, IndexEntry> entries;
  private final Comparator<OxEntity> compareByDepthAndName;

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
    final String text;
    final String type;
    /**
     * the entity associated with the index entry *
     */
    final OxEntity entity;
    /**
     * the entities as members of classes (in case of inheritance) *
     */
    private final ArrayList<OxEntity> owningClassMembers = new ArrayList<OxEntity>();

    IndexEntry(String text, String type, OxEntity entity) {
      this.text = checkNotNull(text);
      this.type = checkNotNull(type);
      this.entity = checkNotNull(entity);
    }
  }

  public SymbolIndex(OxProject project, ClassTree classTree, Config config) {
    this.project = checkNotNull(project);
    this.classTree = checkNotNull(classTree);
    this.entries = constructIndex(project, config);
    this.compareByDepthAndName = createCompareByDepthAndName();
  }

  private  Comparator<OxEntity> createCompareByDepthAndName() {
    return new Comparator<OxEntity>() {
      public int compare(OxEntity o1, OxEntity o2) {
        OxClass e1 = o1.getParentClass();
        OxClass e2 = o2.getParentClass();

        int depth1 = classTree.getClassDepth(e1);
        int depth2 = classTree.getClassDepth(e2);

        if (depth1 != depth2) {
          return depth2 - depth1;
        }

        return e1.getName().compareToIgnoreCase(e2.getName());
      }
    };
  }

  private static Map<OxEntity, IndexEntry> constructIndex(OxProject project, Config config) {
    Map<OxEntity, IndexEntry> entries = new HashMap<OxEntity, IndexEntry>();

    for (OxEntity entity : project.getSymbols()) {
      if ((!config.isShowInternals()) && entity.isInternal()) {
        continue;
      }

      if (entity instanceof OxClass) {
        addSingletonEntry(entries, entity, "Class");
      } else if ((entity instanceof OxField) && (entity.getParentClass() == null)) {
        addSingletonEntry(entries, entity, "Global variable");
      } else if (entity instanceof OxField) {
        addGroupedEntry(entries, entity, entity, "Field");
      } else if (entity instanceof OxEnumElement) {
        OxEnumElement element = (OxEnumElement) entity;
        addSingletonEntry(entries, entity, "Element of enumeration " + project.getLinkToEntity(element.getParentEnum()));
      } else if ((entity instanceof OxMethod) && (entity.getParentClass() == null)) {
        addSingletonEntry(entries, entity, "Global function");
      } else if (entity instanceof OxMethod) {
        OxMethod method = (OxMethod) entity;
        OxClass parentClass = method.getParentClass();
        String type = "Method";
        if (method.getName().compareTo(parentClass.getName()) == 0) {
          type = "Constructor";
        } else if (method.getName().compareTo("~" + parentClass.getName()) == 0) {
          type = "Destructor";
        }

        // now, find furthest ancestor class that has the same method
        OxClass ancestorClass = parentClass;
        while ((ancestorClass.getSuperClass() != null)
            && (ancestorClass.getSuperClass().getMethodByName(method.getName()) != null)) {
          ancestorClass = ancestorClass.getSuperClass();
        }

        addGroupedEntry(entries, ancestorClass.getMethodByName(method.getName()), entity, type);
      }
    }
    return entries;
  }

  private static IndexEntry addSingletonEntry(Map<OxEntity, IndexEntry> entries, OxEntity entity, String type) {
    IndexEntry entry = new IndexEntry(entity.getName(), type, entity);
    entries.put(entity, entry);
    return entry;
  }

  private static IndexEntry addGroupedEntry(Map<OxEntity, IndexEntry> entries, OxEntity ancestorEntity, OxEntity entity, String type) {
    IndexEntry entry = entries.get(ancestorEntity);
    if (entry == null) {
      entry = new IndexEntry(ancestorEntity.getName(), type, entity);
    }
    entry.owningClassMembers.add(entity);
    entries.put(ancestorEntity, entry);
    return entry;
  }

  private void sortEntriesByName(ArrayList<IndexEntry> indexEntries) {
    Collections.sort(indexEntries, COMPARATOR_BY_NAME);
  }

  // this method sorts the owning class members first in decreasing order of
  // depth of the class, and subject to
  // that, by name
  private void sortOwningClassMembers(ArrayList<OxEntity> members) {
    Collections.sort(members, compareByDepthAndName);
  }

  public void write(OutputFile output, FileManager fileManager) throws Exception {
    // store entries in an array list
    ArrayList<IndexEntry> indexEntries = new ArrayList<IndexEntry>();
    indexEntries.addAll(entries.values());

    // sort entries by name
    sortEntriesByName(indexEntries);

    // write table
    Table table = new Table();
    table.specs().cssClass = "index";
    table.specs().columnCssClasses.add("declaration");
    table.specs().columnCssClasses.add("description");

    for (IndexEntry entry : indexEntries) {
      OxEntity entity = entry.entity;
      StringBuilder description = new StringBuilder();
      description.append(entry.type);
      if (!entry.owningClassMembers.isEmpty()) {
        sortOwningClassMembers(entry.owningClassMembers);
        description.append(" of ");
        int index = 0;
        for (OxEntity memberEntity : entry.owningClassMembers) {
          if (index++ > 0) {
            description.append(", ");
          }
          description.append(project.getLinkToEntity(memberEntity, memberEntity.getParentClass().getName()));
        }
      }

      String smallIconHtml = fileManager.getSmallIconHtml(entity.getIcon());
      table.addRow(smallIconHtml + project.getLinkToEntity(entity), description.toString());
    }
    output.writeln(table);
  }
}
