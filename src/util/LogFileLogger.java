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

import java.io.IOException;

import static oxdoc.util.Utils.checkNotNull;

public class LogFileLogger implements Logger {
  private LogFile logFile;

  public LogFileLogger(LogFile logFile) {
    this.logFile = checkNotNull(logFile);
  }

  @Override
  public void info(String message) {
    try {
      System.out.println(message);
      logFile.writeln(message);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void warning(String message) {
    try {
      System.out.println("Warning: " + message);
      logFile.writeln("Warning: " + message);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
