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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStreamWriter;

public class OxDoc {
  public static final String PRODUCT_NAME = "oxdoc";
  public static final String COPYRIGHT_NOTICE = "(c) Copyright 2005-2014 by Y. Zwols [yorizwols@users.sourceforge.net]";
  public static final String VERSION = Constants.VERSION;
  public static final String URL = "http://oxdoc.sourceforge.net";
  public static final String LICENSE_NOTICE = "oxdoc is free software and comes with ABSOLUTELY NO WARRANTY.\n"
      + "You are welcome to redistribute it under certain conditions.\n"
      + "See the LICENSE file for distribution details.\n";

  private final OxProject project;
  private final Config config;
  private final FileManager fileManager;
  private final LatexImageManager latexImageManager;
  private final TextProcessor textProcessor;
  private final Logger logger;

  public OxDoc(Logger logger) {
    this.logger = logger;
    config = new Config(logger);
    fileManager = new FileManager(logger, config);
    textProcessor = new TextProcessor(logger, config);
    project = new OxProject(logger, fileManager, textProcessor);
    latexImageManager = new LatexImageManager(logger, fileManager, config);

    config.addMathProcessor("latex", new MathProcessorLatex(logger, config, latexImageManager, fileManager));
    config.addMathProcessor("mathjax", new MathProcessorMathjax());
    config.addMathProcessor("plain", new MathProcessorPlain());

    logger.info(getAboutText());
  }

  public static String getAboutText() {
    return PRODUCT_NAME + " " + VERSION + " [" + Constants.COMPILETIME + "]\n" + COPYRIGHT_NOTICE + "\n\n"
        + LICENSE_NOTICE;
  }

  public void addFiles(String filespec) throws Exception {
    Iterable<File> files = PathMatcher.scan(filespec);
    for (File file : files)
      addFile(file);
  }

  public void addFile(File file) throws Exception {
    logger.info("Reading file " + file);

    ByteArrayOutputStream bufferStream = new ByteArrayOutputStream();
    OutputStreamWriter bufferWriter = new OutputStreamWriter(bufferStream);

    Preprocessor p = new Preprocessor(logger, config, bufferWriter);
    p.processFile(file);

    ByteArrayInputStream bufferIn = new ByteArrayInputStream(bufferStream.toByteArray());

    Parser parser = new Parser(bufferIn, logger, project.addFile(file.getName()), project);
    parser.OxFileDefinition();
  }

  public void generateDocs() throws Exception {
    Documentor documentor = new Documentor(project, logger, config, latexImageManager, fileManager);
    documentor.generateDocs();
  }

  public Config getConfig() {
    return config;
  }
}
