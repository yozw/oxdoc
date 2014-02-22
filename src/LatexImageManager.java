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

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

public class LatexImageManager extends ArrayList {
  /**
   *
   */
  private static final long serialVersionUID = 1L;
  private ImageEntryList imageEntries;
  private boolean _cacheLoaded = false;
  private int _imageCounter = 0;
  public final OxDocLogger logger;
  private FileManager fileManager;
  private Config config;

  private class ImageEntry {
    public String _filename = "";
    public String _formula = "";
    public boolean needsGenerating = true;

    public ImageEntry(String formula, String filename) {
      _filename = filename;
      _formula = formula;
    }

    public String filename() {
      return _filename;
    }

    public String formula() {
      return _formula;
    }
  }

  private class ImageEntryList {
    private Hashtable _formulas = new Hashtable();
    private Hashtable _filenames = new Hashtable();

    public Hashtable formulas() {
      return _formulas;
    }

    public ImageEntry register(String formula, String filename) {
      if (!_cacheLoaded) {
        _cacheLoaded = true;
        loadCache();
      }

      formula = formula.trim().replace('\n', ' ').replace('\r', ' ');

      ImageEntry entry = ((ImageEntry) formulas().get(formula));
      if (entry != null)
        return entry;

      if (filename == null)
        do
          filename = "img" + (new Integer(++_imageCounter)).toString() + ".png";
        while (_filenames.get(filename) != null);

      entry = new ImageEntry(formula, filename);
      _formulas.put(formula, entry);
      _filenames.put(filename, entry);

      return entry;
    }
  }

  public LatexImageManager(OxDocLogger logger, FileManager fileManager, Config config) {
    this.logger = logger;
    this.fileManager = fileManager;
    this.config = config;
    imageEntries = new ImageEntryList();
  }

  public String getFormulaFilename(String Formula) {
    return imageEntries.register(Formula, null).filename();
  }

  private void saveCache() {
    try {
      DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
      Document doc = builder.newDocument();

      Element root = doc.createElement("cache");
      doc.appendChild(root);

      for (Enumeration elements = imageEntries.formulas().elements(); elements.hasMoreElements(); ) {
        ImageEntry e = (ImageEntry) elements.nextElement();
        Element newElement = doc.createElement("image");
        newElement.setAttribute("formula", e.formula());
        newElement.setAttribute("filename", e.filename());
        root.appendChild(newElement);
      }

      // Prepare the DOM document for writing
      Source source = new DOMSource(doc);

      // Prepare the output file
      File file = new File(fileManager.imageCache());
      Result result = new StreamResult(new FileWriter(file));

      // Write the DOM document to the file
      Transformer xformer = TransformerFactory.newInstance().newTransformer();
      xformer.transform(source, result);
    } catch (Exception e) {
      logger.warning("Error writing image cache. Don't worry.");
    }
  }

  private void loadCache() {
    try {
      File file = new File(fileManager.imageCache().trim());
      if (!file.exists())
        return;

      // Parse the file
      DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
      Document doc = builder.parse(fileManager.imageCache());

      // Find the tags of interest
      NodeList nodes = doc.getElementsByTagName("image");
      for (int i = 0; i < nodes.getLength(); i++) {
        Element element = (Element) nodes.item(i);
        String formula = element.getAttribute("formula");
        String filename = element.getAttribute("filename");
        if (fileManager.imageFileExists(filename)) {
          ImageEntry entry = imageEntries.register(formula, filename);
          entry.needsGenerating = false;
        }
      }
    } catch (Exception e) {
      logger.warning("Error reading image cache. Don't worry.");
    }
  }

  public void makeLatexFiles() throws IOException {
    for (Enumeration elements = imageEntries.formulas().elements(); elements.hasMoreElements(); ) {
      ImageEntry e = (ImageEntry) elements.nextElement();
      if (e.needsGenerating)
        makeLatexFile(e);
    }
    saveCache();
  }

  private void makeLatexFile(ImageEntry e) throws IOException {
    logger.message("Generating image for formula \"" + e.formula() + "\"...");

    File aFile = new File(fileManager.tempTexFile());
    Writer output = new BufferedWriter(new FileWriter(aFile));
    output.write("\\documentclass{article}\n");
    output.write("\\usepackage{amsmath}\n");

    for (int i = 0; i < config.latexPackages.size(); i++)
      output.write("\\usepackage{" + (String) config.latexPackages.get(i) + "}\n");
    output.write("\\begin{document}\n");
    output.write("\\pagestyle{empty}\n");
    if (config.imageBgColor != null)
      output.write("\\special{background " + config.imageBgColor + " }");
    output.write("\\begin{align*}\n");
    output.write(e.formula() + "\n");
    output.write("\\end{align*}\n");
    output.write("\\end{document}\n");
    output.close();

    String latexParams = config.latexArg + " -interaction=batchmode";

    File tempDir = new File(fileManager.tempDir());
    File curDir = new File(".");
    if (!tempDir.equals(curDir))
      latexParams += MessageFormat.format(" -aux-directory={1} -output-directory={1}",
          fileManager.tempDir());

    run(config.latex, latexParams + " " + fileManager.tempFile("__oxdoc.tex"));

    String dvipngParams = "{0} -T tight --gamma 1.5 -o {1} {2}";
    if (config.imageBgColor == null)
      dvipngParams += " -bg Transparent";

    // have to do the following twice, because it sometimes seems to miss
    // fonts at the first run
    for (int i = 0; i < 2; i++) {
      // make sure the directory exists. If not, create it
      (new File(fileManager.imageFile(e.filename()))).getParentFile().mkdirs();

      Object[] _args = {config.dvipngArg, fileManager.imageFile(e.filename()),
          fileManager.tempFile("__oxdoc.dvi")};
      run(config.dvipng, MessageFormat.format(dvipngParams, _args));
    }
    (new File(fileManager.tempFile("__oxdoc.tex"))).delete();
    (new File(fileManager.tempFile("__oxdoc.dvi"))).delete();
    (new File(fileManager.tempFile("__oxdoc.aux"))).delete();
    (new File(fileManager.tempFile("__oxdoc.log"))).delete();
  }

  private void run(String filename, String parameters) throws IOException {
    logger.message("   " + filename + " " + parameters);

    Runtime run = Runtime.getRuntime();
    try {
      Process pp = run.exec(filename + " " + parameters);
      logger.message("");

      StreamGobbler errorGobbler = new StreamGobbler(pp.getErrorStream(), logger, false);
      StreamGobbler outputGobbler = new StreamGobbler(pp.getInputStream(), logger, true);

      errorGobbler.start();
      outputGobbler.start();

      pp.waitFor();

      if (errorGobbler.length() > 0) {
        logger.message("");
        logger.message(errorGobbler.getText());
      }

    } catch (InterruptedException E) {
      System.out.println("Execution interrupted");
      System.exit(1);
    } catch (IOException E) {
      System.out.println("Input/output error");
      System.exit(1);
    }
  }
}
