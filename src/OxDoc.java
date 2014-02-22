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

import oxdoc.parser.Parser;

import java.io.*;

public class OxDoc {
  public static final String ProductName = "oxdoc";
  public static final String Version = Constants.VERSION;
  public static final String Url = "http://oxdoc.sourceforge.net";
  public static final String CopyrightNotice = "(c) Copyright 2005-2012 by Y. Zwols [yorizwols@users.sourceforge.net]";
  public static final String LicenseNotice = "oxdoc is free software and comes with ABSOLUTELY NO WARRANTY.\n"
      + "You are welcome to redistribute it under certain conditions.\n"
      + "See the LICENSE file for distribution details.\n";
  public OxProject project = new OxProject(this);
  public Config config = new Config(this);
  public FileManager fileManager = new FileManager(this);
  public LatexImageManager latexImageManager = new LatexImageManager(this);
  public TextProcessor textProcessor = new TextProcessor(this);
  private Logger logger = null;

  public OxDoc(Logger logger) {
    this.logger = logger;
    message(aboutText());
  }

  public OxDoc() {
    this(new Logger() {
      public void writeMessage(String message, int Code) {
        System.out.println(message);
      }
    });
  }

  public static String aboutText() {
    return ProductName + " " + Version + " [" + Constants.COMPILETIME + "]\n" + CopyrightNotice + "\n\n"
        + LicenseNotice;
  }

  public void addFiles(String filespec) throws Exception {
    Iterable<File> files = PathMatcher.scan(filespec);
    for (File file : files)
      addFile(file);
  }

  public void addFile(File file) throws Exception {
    message("Reading file " + file);

    ByteArrayOutputStream bufferStream = new ByteArrayOutputStream();
    OutputStreamWriter bufferWriter = new OutputStreamWriter(bufferStream);

    Preprocessor p = new Preprocessor(this, bufferWriter);
    p.ProcessFile(file);

    ByteArrayInputStream bufferIn = new ByteArrayInputStream(bufferStream.toByteArray());

    Parser parser = new Parser(bufferIn, this, project.addFile(file.getName()), project);
    parser.OxFileDefinition();
  }

  public void generateDocs() throws Exception {
    Documentor documentor = new Documentor(this);
    documentor.generateDocs();
  }

  public void message(String message) {
    if (logger != null)
      logger.writeMessage(message, 0);
  }

  public void warning(String message) {
    if (logger != null)
      logger.writeMessage("Warning: " + message, 1);
  }

  public void messageStream(InputStream inputStream) throws IOException {
    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

    while (true) {
      String data = reader.readLine();
      if (data == null)
        break;
      message("> " + data);
    }
    reader.close();
  }
}
