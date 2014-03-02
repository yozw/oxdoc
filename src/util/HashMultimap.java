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

package oxdoc.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static oxdoc.util.Utils.checkNotNull;

public class HashMultimap<K, V> {

  private final Map<K, Set<V>> map = new HashMap<K, Set<V>>();

  public HashMultimap() {
  }

  public void put(K key, V value) {
    checkNotNull(key);
    checkNotNull(value);

    Set<V> values = map.get(key);
    if (values == null) {
      values = new HashSet<V>();
      map.put(key, values);
    }
    values.add(value);
  }

  public Set<K> keySet() {
    return map.keySet();
  }

  public Set<V> get(K key) {
    return map.get(key);
  }
}
