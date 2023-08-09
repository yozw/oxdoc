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

package oxdoc.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static oxdoc.util.Utils.checkNotNull;

public class StreamGobbler extends Thread {
  private final Logger logger = Logging.getLogger();
  private final InputStream is;
  private final boolean echo;
  private String text = "";

  public StreamGobbler(InputStream is, boolean echo) {
    this.is = checkNotNull(is);
    this.echo = echo;
  }

  public void run() {
    try {
      InputStreamReader isr = new InputStreamReader(is);
      BufferedReader br = new BufferedReader(isr);
      String line;
      while ((line = br.readLine()) != null) {
        text += "> " + line + "\n";
        if (echo) {
          logger.info("> " + line);
        }
      }
    } catch (IOException ioe) {
      ioe.printStackTrace();
    }
  }

  public String getText() {
    return text;
  }

  public int length() {
    return text.length();
  }
}
