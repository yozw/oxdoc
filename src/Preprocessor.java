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

import oxdoc.util.Logger;
import oxdoc.util.Logging;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static oxdoc.util.Utils.checkNotNull;

public class Preprocessor {

  private static final Set<String> ignoredFiles = new HashSet<String>();
  private static final int PLAINLINE = 0;
  private static final int ENDIF = 1;
  private static final int ELSE = 2;
  private static final int IFDEF = 4;
  private static final int IFNDEF = 8;
  private static final int DEFINE = 16;
  private static final int INCLUDE = 32;
  private static final int IMPORT = 64;

  private final Logger logger = Logging.getLogger();
  private final Set<String> defines = new HashSet<String>();
  private final Writer outputStream;
  private final Config config;

  public Preprocessor(Config config, Writer outputStream) {
    this.outputStream = checkNotNull(outputStream);
    this.config = checkNotNull(config);
  }

  public void processFile(File file) throws Exception {
    processFile(file, file);
  }

  private void processFile(File file, File mainFile) throws Exception {
    BufferedReader reader;
    try {
      reader = new BufferedReader(new FileReader(file));
    } catch (IOException E) {
      throw new Exception("Could not open file " + file);
    }
    processBlock(reader, 0, true, mainFile);
    outputStream.flush();
  }

  private void ignoreIncludeFiles(String fileName, ArrayList<File> tryFiles) {
    if (!ignoredFiles.contains(fileName)) {
      ignoredFiles.add(fileName);
      String warning = "Included file " + fileName + " could not be opened. File will be ignored.";

      if (config.isVerbose()) {
        warning += "\n-- Looked for the following files:";

        for (File file : tryFiles)
          warning += "\n   " + file.getAbsoluteFile();
      }
      logger.warning(warning);
    }
  }

  private int getCommand(String line, ArrayList<String> params) throws Exception {
    params.clear();

    String cmd = line.trim();
    if (!cmd.startsWith("#"))
      return PLAINLINE;

    int pos = cmd.indexOf(' ');
    if (pos == -1)
      cmd = cmd.substring(1);
    else {
      params.add(cmd.substring(pos + 1));
      cmd = cmd.substring(1, pos);
    }

    if (cmd.equals("endif"))
      return ENDIF;
    if (cmd.equals("else"))
      return ELSE;
    if (cmd.equals("ifdef"))
      return IFDEF;
    if (cmd.equals("ifndef"))
      return IFNDEF;
    if (cmd.equals("define"))
      return DEFINE;
    if (cmd.equals("include"))
      return INCLUDE;
    if (cmd.equals("import"))
      return IMPORT;
    throw new Exception("Unknown preprocessor directive: " + cmd);
  }

  private boolean isDefined(String define) {
    return defines.contains(define);
  }

  private String preProcessLine(String line) {
    return line;
  }

  private int processBlock(BufferedReader is, int endMarkers, boolean active, File mainFile)
      throws Exception {

    String line;
    ArrayList<String> params = new ArrayList<String>();
    while ((line = is.readLine()) != null) {

      line = preProcessLine(line);
      int cmd = getCommand(line, params);
      switch (cmd) {
        case IFDEF:
        case IFNDEF:
          boolean write = isDefined(params.get(0).trim());
          if (cmd == IFNDEF)
            write = !write;

          int lastCmd = processBlock(is, ELSE | ENDIF, write && active, mainFile);
          if (lastCmd == ELSE)
            processBlock(is, ENDIF, (!write) && active, mainFile);
          continue;
        case ELSE:
          if ((endMarkers & ELSE) == 0)
            throw new IOException("Unexpected else");
          return ELSE;
        case ENDIF:
          if ((endMarkers & ENDIF) == 0)
            throw new IOException("Unexpected endif");
          return ENDIF;
        case PLAINLINE:
          if (active)
            outputStream.write(line + "\n");
          break;
        case DEFINE:
          if (params.size() != 1)
            throw new IOException("#define requires 1 argument");
          if (active)
            defines.add(params.get(0).trim());
          break;
        case IMPORT:
          break;
        case INCLUDE:
          if (params.size() != 1)
            throw new IOException(((cmd == IMPORT) ? "#import" : "#include") + " requires 1 argument");
          if (active) {
            boolean lookLocal;
            String fileSpec = params.get(0);
            char firstChar = fileSpec.charAt(0);
            char lastChar = fileSpec.charAt(fileSpec.length() - 1);
            if ((firstChar == '"') && (lastChar == '"'))
              lookLocal = true;
            else if ((firstChar == '<') && (lastChar == '>'))
              lookLocal = false;
            else {
              logger.warning("Invalid preprocessor clause: " + line);
              break;
            }
            String fileName = fileSpec.substring(1, fileSpec.length() - 1);

            // specify search paths as described in Ox Syntax guide
            ArrayList<File> tryFiles = new ArrayList<File>();
            if (lookLocal) {
              /* in the directory containing the source file (if just a filename, or a filename with a relative path is
                specified), or in the specified directory (if the filename has an absolute path); */
              String basePath = mainFile.getAbsoluteFile().getParent();
              tryFiles.add(new File(basePath + File.separatorChar + fileName));
            }

            // * the directories specified on the compiler command line
            // (if any);
            for (String path : config.getIncludePaths()) {
              tryFiles.add(new File(path + File.separatorChar + fileName));
            }
            // * in the current directory.
            tryFiles.add(new File(fileName));

            boolean done = false;
            for (File file : tryFiles) {
              if (file.exists()) {
                processFile(file, mainFile);
                done = true;
                break;
              }
            }

            if (!done)
              ignoreIncludeFiles(fileName, tryFiles);
          }
          break;
      }
    }
    return 0;
  }
}
