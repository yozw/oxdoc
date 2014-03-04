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

import java.io.File;

import static oxdoc.util.FileUtils.changeFileExtension;

public class FileUtilsTest extends TestCase {

  public void testChangeFileExtension() {
    assertEquals(new File("file.h"), changeFileExtension(new File("file.ox"), ".h"));
    assertEquals(new File("file.h"), changeFileExtension(new File("file."), ".h"));
    assertEquals(new File("file.h"), changeFileExtension(new File("file"), ".h"));
    assertEquals(new File("file.ox.h"), changeFileExtension(new File("file.ox.ox"), ".h"));
    assertEquals(new File("file.ox.h"), changeFileExtension(new File("file.ox."), ".h"));
    assertEquals(new File(".h"), changeFileExtension(new File(".ox"), ".h"));

    assertEquals(new File("/tmp/file.h"), changeFileExtension(new File("/tmp/file.ox"), ".h"));
    assertEquals(new File("/tmp/file.h"), changeFileExtension(new File("/tmp/file"), ".h"));
  }
}
