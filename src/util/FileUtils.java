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

import java.io.File;

public class FileUtils {

  public static final String separator = System.getProperty("file.separator");

  public static File changeFileExtension(File file, String newExtension) {
    String filename = file.getPath();

    // Remove the path upto the filename.
    int lastSeparatorIndex = filename.lastIndexOf(separator);

    // Remove the extension.
    int extensionIndex = filename.lastIndexOf(".");
    if (extensionIndex == -1 || extensionIndex < lastSeparatorIndex) {
      return new File(filename + newExtension);
    }

    return new File(filename.substring(0, extensionIndex) + newExtension);
  }
}
