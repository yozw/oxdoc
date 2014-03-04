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

import oxdoc.util.FileUtils;
import oxdoc.util.Logger;
import oxdoc.util.Logging;

import java.io.*;
import java.util.*;

import static oxdoc.util.Utils.checkNotNull;

public class Preprocessor {

  private final Set<String> ignoredFiles = new HashSet<String>();

  private static enum LineType {
    PLAINLINE, EOF, ENDIF, ELSE, IFDEF, IFNDEF, DEFINE, INCLUDE, IMPORT
  }

  private final Logger logger = Logging.getLogger();
  private final Set<String> defines = new HashSet<String>();
  private static final Map<String, LineType> keywordMap = buildKeywordMap();
  private final Writer outputStream;
  private final Config config;

  public Preprocessor(Config config, Writer outputStream) {
    this.outputStream = checkNotNull(outputStream);
    this.config = checkNotNull(config);
  }

  private static Map<String, LineType> buildKeywordMap() {
    Map<String, LineType> map = new HashMap<String, LineType>();
    for (LineType type : LineType.values()) {
      if (type != LineType.PLAINLINE && type != LineType.EOF) {
        map.put(type.toString().toLowerCase(), type);
      }
    }
    return map;
  }

  public void processFile(File file) throws Exception {
    // See if there is a <filename>.oxdoc file; if so, parse it
    File oxdocAuxFile = FileUtils.changeFileExtension(file, ".oxdoc");
    if (oxdocAuxFile.exists()) {
      processFile(oxdocAuxFile, file);
    }

    // Continue processing the main file
    processFile(file, file);
  }

  private void processFile(File file, File mainFile) throws Exception {
    BufferedReader reader;

    try {
      reader = new BufferedReader(new FileReader(file));
    } catch (IOException E) {
      throw new Exception("Could not open file " + file);
    }

    processBlock(reader, true, mainFile);
    outputStream.flush();
  }

  private void ignoreIncludeFiles(String fileName, ArrayList<File> tryFiles) {
    if (!ignoredFiles.contains(fileName)) {
      ignoredFiles.add(fileName);
      String warning = "Included file " + fileName + " could not be opened. File will be ignored.";

      if (config.isVerbose()) {
        warning += "\n-- Looked for the following files:";

        for (File file : tryFiles) {
          warning += "\n   " + file.getAbsoluteFile();
        }
      }
      logger.warning(warning);
    }
  }

  private LineType getCommand(String line, ArrayList<String> params) throws Exception {
    params.clear();

    String cmd = line.trim();
    if (!cmd.startsWith("#")) {
      return LineType.PLAINLINE;
    }

    int pos = cmd.indexOf(' ');
    if (pos == -1) {
      cmd = cmd.substring(1);
    } else {
      params.add(cmd.substring(pos + 1));
      cmd = cmd.substring(1, pos);
    }

    LineType lineType = keywordMap.get(cmd.toLowerCase());
    if (lineType == null) {
      throw new Exception("Unknown preprocessor directive: " + cmd);
    }
    return lineType;
  }

  private boolean isDefined(String define) {
    return defines.contains(define);
  }

  private LineType processBlock(BufferedReader is, boolean active, File mainFile, LineType... endMarkers)
      throws Exception {

    String line;
    ArrayList<String> params = new ArrayList<String>();
    while ((line = is.readLine()) != null) {

      LineType lineType = getCommand(line, params);
      switch (lineType) {
        case IFDEF:
        case IFNDEF:
          boolean write = isDefined(params.get(0).trim());
          if (lineType == LineType.IFNDEF) {
            write = !write;
          }

          LineType lastCmd = processBlock(is, write && active, mainFile, LineType.ELSE, LineType.ENDIF);
          if (lastCmd == LineType.ELSE) {
            processBlock(is, (!write) && active, mainFile, LineType.ENDIF);
          }
          continue;
        case ELSE:
          if (!contains(endMarkers, LineType.ELSE)) {
            throw new IOException("Unexpected else");
          }
          return LineType.ELSE;
        case ENDIF:
          if (!contains(endMarkers, LineType.ENDIF)) {
            throw new IOException("Unexpected endif");
          }
          return LineType.ENDIF;
        case PLAINLINE:
          if (active) {
            outputStream.write(line + "\n");
          }
          break;
        case DEFINE:
          if (params.size() != 1) {
            throw new IOException("#define requires 1 argument");
          }
          if (active) {
            defines.add(params.get(0).trim());
          }
          break;
        case IMPORT:
          break;
        case INCLUDE:
          if (params.size() != 1) {
            throw new IOException("#include requires 1 argument");
          }
          if (active) {
            boolean lookLocally;
            String fileSpec = params.get(0);
            if (fileSpec.startsWith("\"") && fileSpec.endsWith("\"")) {
              lookLocally = true;
            } else if (fileSpec.startsWith("<") && fileSpec.endsWith(">")) {
              lookLocally = false;
            } else {
              logger.warning("Invalid preprocessor clause: " + line);
              break;
            }
            String fileName = fileSpec.substring(1, fileSpec.length() - 1);

            // specify search paths as described in Ox Syntax guide
            ArrayList<File> tryFiles = new ArrayList<File>();
            if (lookLocally) {
              /* in the directory containing the source file (if just a filename, or a filename with a relative path is
                specified), or in the specified directory (if the filename has an absolute path); */
              String basePath = mainFile.getAbsoluteFile().getParent();
              tryFiles.add(new File(basePath + File.separatorChar + fileName));
            }

            // the directories specified on the compiler command line (if any);
            for (String path : config.getIncludePaths()) {
              tryFiles.add(new File(path + File.separatorChar + fileName));
            }
            // * in the current directory.
            tryFiles.add(new File(fileName));

            boolean success = false;
            for (File file : tryFiles) {
              if (file.exists()) {
                processFile(file, mainFile);
                success = true;
                break;
              }
            }

            if (!success) {
              ignoreIncludeFiles(fileName, tryFiles);
            }
          }
          break;
      }
    }
    return LineType.EOF;
  }

  private boolean contains(LineType[] endMarkers, LineType marker) {
    for (LineType type : endMarkers) {
      if (type.equals(marker)) {
        return true;
      }
    }
    return false;
  }
}
