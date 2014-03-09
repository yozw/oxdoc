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
import oxdoc.util.Logger;
import oxdoc.util.Logging;

import java.util.Comparator;

public class OxEntityComparator<T extends OxEntity> implements Comparator<T> {
  private static final AlphanumComparator ALPHANUM_COMPARATOR_NO_CASE = new AlphanumComparator(true);
  private static final AlphanumComparator ALPHANUM_COMPARATOR = new AlphanumComparator(false);

  public int compare(T e1, T e2) {
    if (e1.equals(e2)) {
      return 0;
    }

    String key1 = e1.getSortKey();
    String key2 = e2.getSortKey();
    int bySortKeyNoCase = ALPHANUM_COMPARATOR_NO_CASE.compare(key1, key2);
    if (bySortKeyNoCase != 0) {
      return bySortKeyNoCase;
    }

    int bySortKey = ALPHANUM_COMPARATOR.compare(key1, key2);
    if (bySortKey != 0) {
      return bySortKey;
    }

    int byReferenceName = e1.getReferenceName().compareTo(e2.getReferenceName());
    if (byReferenceName != 0) {
      return byReferenceName;
    }

    throw new IllegalStateException("Encountered two supposedly different entities with the same reference name: " +
        "something is wrong!");
  }
}
