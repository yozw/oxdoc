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

package oxdoc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class StreamGobbler extends Thread {
  private final InputStream is;
  private final Logger logger;
  private final boolean echo;
  private String text = "";

  StreamGobbler(InputStream is, Logger logger, boolean echo) {
    this.is = is;
    this.logger = logger;
    this.echo = echo;
  }

  public void run() {
    try {
      InputStreamReader isr = new InputStreamReader(is);
      BufferedReader br = new BufferedReader(isr);
      String line;
      while ((line = br.readLine()) != null) {
        text += "> " + line + "\n";
        if (echo)
          logger.info("> " + line);
      }
    } catch (IOException ioe) {
      ioe.printStackTrace();
    }
  }

  String getText() {
    return text;
  }

  int length() {
    return text.length();
  }
}
