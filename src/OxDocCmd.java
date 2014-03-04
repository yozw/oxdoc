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

package oxdoc;

import oxdoc.gui.OxDocGui;
import oxdoc.parser.ParseException;
import oxdoc.util.LogFile;
import oxdoc.util.LogFileLogger;
import oxdoc.util.Logger;
import oxdoc.util.Logging;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class OxDocCmd {
  public final LogFile logFile = new LogFile();
  public final Logger logger = Logging.getLogger();

  public int parseFiles(Iterable<String> files, OxDoc oxdoc) throws Exception {
    int totalFiles = 0;
    for (String filename : files) {
      filename = filename.trim();
      if (filename.length() == 0) {
        continue;
      }

      try {
        oxdoc.addFiles(filename);
        totalFiles++;
      } catch (ParseException e) {
        logger.info("Parsing of file " + filename + " failed");
        logger.info(e.toString());
      } catch (FileNotFoundException e) {
        logger.info("File not found: " + filename);
      }
    }

    return totalFiles;
  }

  public void examineCommandLine(String[] args, List<String> files, Config config)
      throws IllegalArgumentException {
    // examine command line
    for (int i = 0; i < args.length; i++) {
      if (!args[i].startsWith("-")) {
        files.add(args[i]);

        continue;
      }

      String option = args[i].substring(1);
      if (config.setSimpleOption(option)) {
        continue;
      }

      i++;
      if (i == args.length) {
        throw new IllegalArgumentException("Value expected after option -" + option);
      }

      if (!config.setOption(option, args[i])) {
        throw new IllegalArgumentException("Invalid option -" + option);
      }
    }
  }

  // We need this function in order to circumvent a bug in gcj:
  // even an empty commandline will pass non-empty args to main
  public static boolean emptyArray(String[] x) {
    for (String entry : x) {
      if (entry.trim().length() > 0) {
        return false;
      }
    }

    return true;
  }

  public void run(String[] args) {
    Logging.setLogger(new LogFileLogger(logFile));
    OxDoc oxdoc = new OxDoc();
    List<String> files = new ArrayList<String>();

    if (emptyArray(args)) {
      System.out.println("\nUsage is:");
      System.out.println("    java oxdoc [options] inputfile [inputfile ...]");
      System.out.println("");
      Config.listOptions();
      return;
    }

    try {
      // do configuration
      oxdoc.getConfig().load();
      try {
        examineCommandLine(args, files, oxdoc.getConfig());
      } catch (IllegalArgumentException E) {
        System.err.println("Error parsing command line. " + E.getMessage());
        return;
      }
      oxdoc.getConfig().validate();

      // execute parsing and document generation
      if (parseFiles(files, oxdoc) > 0) {
        oxdoc.generateDocs();
        logFile.close();
      }
    } catch (Exception e) {
      System.err.println("----------------------------------------------");
      System.err.println("An error has occurred");
      System.err.println(e.getMessage());
      e.printStackTrace();
    }
  }

  // oxdoc entry point
  public static void main(String[] args) {
    boolean runGui = false;

    if ((args.length == 1) && (args[0].compareTo("--gui") == 0)) {
      runGui = true;
    }

    if (runGui) {
      OxDocGui gui = new OxDocGui();
      gui.run(args);
    } else {
      OxDocCmd cmd = new OxDocCmd();
      cmd.run(args);
    }
  }
}
