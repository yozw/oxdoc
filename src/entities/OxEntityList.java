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

import oxdoc.util.Predicate;

import java.util.*;

public class OxEntityList<T extends OxEntity> implements Iterable<T> {
  private final Map<String, T> nameMap = new HashMap<String, T>();
  private final TreeSet<T> entitySet = new TreeSet<T>(new OxEntityComparator<T>());

  public int size() {
    return entitySet.size();
  }

  public boolean isEmpty() {
    return entitySet.isEmpty();
  }

  public <S extends T> S add(S entity) {
    return add(entity.getName(), entity);
  }

  public <S extends T> S add(String name, S entity) {
    nameMap.put(name, entity);
    entitySet.add(entity);
    return entity;
  }

  public <S extends T> void addAll(OxEntityList<S> list) {
    for (S entity : list.nameMap.values()) {
      add(entity);
    }
  }

  public T get(String name) {
    return nameMap.get(name);
  }

  @Override
  public Iterator<T> iterator() {
    return entitySet.iterator();
  }

  public OxEntityList<T> filter(Predicate<T> filter) {
    OxEntityList<T> result = new OxEntityList<T>();
    for (T member : this) {
      if (filter.apply(member))
        result.add(member);
    }
    return result;
  }

  public <S extends T> OxEntityList<S> filterByClass(Class<? extends S> clazz) {
    Set<Class<? extends S>> classSet = new HashSet<Class<? extends S>>();
    classSet.add(clazz);
    return filterByClass(classSet);
  }

  public <S extends T> OxEntityList<S> filterByClass(Class<? extends S> clazz1, Class<? extends S> clazz2) {
    Set<Class<? extends S>> classSet = new HashSet<Class<? extends S>>();
    classSet.add(clazz1);
    classSet.add(clazz2);
    return filterByClass(classSet);
  }

  public <S extends T> OxEntityList<S> filterByClass(Iterable<Class<? extends S>> classes) {
    OxEntityList<S> result = new OxEntityList<S>();
    for (T entity : this) {
      boolean applies = false;
      for (Class<? extends S> clazz : classes) {
        if (clazz.isInstance(entity)) {
          applies = true;
          break;
        }
      }
      if (applies) {
        @SuppressWarnings("unchecked")
        S newEntity = (S) entity;
        result.add(newEntity);
      }
    }
    return result;
  }

  public OxEntityList<T> getNonInternal() {
    return filter(new Predicate<T>() {
      @Override
      public boolean apply(T entity) {
        return !entity.isInternal();
      }
    });
  }
}
