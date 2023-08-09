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

import static oxdoc.util.Utils.checkNotNull;

public class Logging {

  private static Logger logger = new ConsoleLogger();

  private static final Logger FORWARDING_LOGGER = new Logger() {
    @Override
    public void info(String message) {
      logger.info(message);
    }

    @Override
    public void warning(String message) {
      logger.warning(message);
    }
  };

  public static Logger getLogger() {
    return FORWARDING_LOGGER;
  }

  public static void setLogger(Logger logger) {
    Logging.logger = checkNotNull(logger);
  }

}
