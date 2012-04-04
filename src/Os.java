/**

oxdoc (c) Copyright 2005-2009 by Y. Zwols

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

public class Os {

  public enum OperatingSystem { Win32, Linux, Solaris, Mac, Unknown };

  private Os() {
  }

  public static String getOsName() {
    return System.getProperty("os.name", "unknown");
  }
  
  public static OperatingSystem getOperatingSystem() {
    String osname = System.getProperty("os.name", "generic").toLowerCase();
    if (osname.startsWith("windows"))
      return OperatingSystem.Win32;
    else if (osname.startsWith("linux"))
      return OperatingSystem.Linux;
    else if (osname.startsWith("mac") || osname.startsWith("darwin"))
      return OperatingSystem.Mac;
    else if (osname.startsWith("sunos"))
      return OperatingSystem.Solaris;
    else 
      return OperatingSystem.Unknown;
  }

}


