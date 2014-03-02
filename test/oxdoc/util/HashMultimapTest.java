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

import junit.framework.TestCase;

import java.util.HashSet;

public class HashMultimapTest extends TestCase {

  private HashMultimap<String, String> map;

  @Override
  protected void setUp() throws Exception {
    map = new HashMultimap<String, String>();
    map.put("abc", "a");
    map.put("abc", "b");
    map.put("abc", "c");
    map.put("axy", "a");
    map.put("axy", "x");
    map.put("axy", "y");
  }

  public void testGet() throws Exception {
    HashSet<String> set1 = new HashSet<String>();
    set1.add("a");
    set1.add("b");
    set1.add("c");
    HashSet<String> set2 = new HashSet<String>();
    set2.add("a");
    set2.add("x");
    set2.add("y");

    assertEquals(set1, map.get("abc"));
    assertEquals(set2, map.get("axy"));
    assertEquals(null, map.get("bad"));
  }

  public void testKeySet() throws Exception {
    HashSet<String> expected = new HashSet<String>();
    expected.add("abc");
    expected.add("axy");

    assertEquals(expected, map.keySet());
  }

}
