/**

 oxdoc (c) Copyright 2005-2023 by Y. Zwols

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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class BinaryOutputFile {
  private final FileOutputStream output;

  // create a blank file
  public BinaryOutputFile(String fileName, FileManager fileManager) throws IOException {
    File aFile = new File(fileManager.getOutputFilename(fileName).trim());
    aFile.getParentFile().mkdirs();
    output = new FileOutputStream(aFile);
  }

  public void close() throws IOException {
    output.close();
  }

  public void write(byte[] buf, int length) throws IOException {
    output.write(buf, 0, length);
  }

}
