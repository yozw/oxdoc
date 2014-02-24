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

import oxdoc.util.AlphanumComparator;

import java.util.Comparator;

public class OxEntityComparator<T extends OxEntity> implements Comparator<T> {
  private static final AlphanumComparator ALPHANUM_COMPARATOR = new AlphanumComparator();

  public int compare(T e1, T e2) {
    String key1 = e1.getSortKey().toUpperCase();
    String key2 = e2.getSortKey().toUpperCase();
    return ALPHANUM_COMPARATOR.compare(key1, key2);
  }
}
