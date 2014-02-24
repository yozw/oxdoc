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
import oxdoc.util.Logger;
import oxdoc.util.Logging;
import oxdoc.util.StreamGobbler;

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
import java.util.Enumeration;
import java.util.Hashtable;

import static oxdoc.util.Utils.checkNotNull;

public class LatexImageManager {
  private final Logger logger = Logging.getLogger();
  private final Config config;
  private final FileManager fileManager;
  private final ImageEntryList imageEntries;

  private boolean cacheLoaded = false;
  private int imageCounter = 0;

  private static class ImageEntry {
    public final String filename;
    public final String formula;
    public boolean needsGenerating = true;

    public ImageEntry(String formula, String filename) {
      this.filename = checkNotNull(filename);
      this.formula = checkNotNull(formula);
    }

    public String getFilename() {
      return filename;
    }

    public String getFormula() {
      return formula;
    }
  }

  private class ImageEntryList {
    private final Hashtable<String, ImageEntry> formulas = new Hashtable<String, ImageEntry>();
    private final Hashtable<String, ImageEntry> filenames = new Hashtable<String, ImageEntry>();

    public Hashtable<String, ImageEntry> getFormulas() {
      return formulas;
    }

    public ImageEntry register(String formula, String filename) {
      if (!cacheLoaded) {
        cacheLoaded = true;
        loadCache();
      }

      formula = formula.trim().replace('\n', ' ').replace('\r', ' ');

      ImageEntry entry = getFormulas().get(formula);
      if (entry != null)
        return entry;

      if (filename == null)
        do
          filename = "img" + (Integer.toString(++imageCounter)) + ".png";
        while (filenames.get(filename) != null);

      entry = new ImageEntry(formula, filename);
      formulas.put(formula, entry);
      filenames.put(filename, entry);

      return entry;
    }
  }

  public LatexImageManager(FileManager fileManager, Config config) {
    this.fileManager = checkNotNull(fileManager);
    this.config = checkNotNull(config);
    imageEntries = new ImageEntryList();
  }

  public String getFormulaFilename(String Formula) {
    return imageEntries.register(Formula, null).getFilename();
  }

  private void saveCache() {
    try {
      DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
      Document doc = builder.newDocument();

      Element root = doc.createElement("cache");
      doc.appendChild(root);

      for (Enumeration elements = imageEntries.getFormulas().elements(); elements.hasMoreElements(); ) {
        ImageEntry e = (ImageEntry) elements.nextElement();
        Element newElement = doc.createElement("image");
        newElement.setAttribute("formula", e.getFormula());
        newElement.setAttribute("filename", e.getFilename());
        root.appendChild(newElement);
      }

      // Prepare the DOM document for writing
      Source source = new DOMSource(doc);

      // Prepare the output file
      File file = new File(fileManager.getImageCache());
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
      File file = new File(fileManager.getImageCache().trim());
      if (!file.exists())
        return;

      // Parse the file
      DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
      Document doc = builder.parse(fileManager.getImageCache());

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
    for (Enumeration elements = imageEntries.getFormulas().elements(); elements.hasMoreElements(); ) {
      ImageEntry e = (ImageEntry) elements.nextElement();
      if (e.needsGenerating)
        makeLatexFile(e);
    }
    saveCache();
  }

  private void makeLatexFile(ImageEntry e) throws IOException {
    logger.info("Generating image for formula \"" + e.getFormula() + "\"...");

    File aFile = new File(fileManager.getTempTexFile());
    Writer output = new BufferedWriter(new FileWriter(aFile));
    output.write("\\documentclass{article}\n");
    output.write("\\usepackage{amsmath}\n");

    for (String latexPackage : config.getLatexPackages()) {
      output.write("\\usepackage{" + latexPackage + "}\n");
    }
    output.write("\\begin{document}\n");
    output.write("\\pagestyle{empty}\n");
    if (config.getImageBgColor() != null) {
      output.write("\\special{background " + config.getImageBgColor() + " }");
    }
    output.write("\\begin{align*}\n");
    output.write(e.getFormula() + "\n");
    output.write("\\end{align*}\n");
    output.write("\\end{document}\n");
    output.close();

    String latexParams = config.getLatexArg() + " -interaction=batchmode";

    File tempDir = new File(fileManager.getTempDir());
    File curDir = new File(".");
    if (!tempDir.equals(curDir))
      latexParams += MessageFormat.format(" -aux-directory={1} -output-directory={1}",
          fileManager.getTempDir());

    run(config.getLatex(), latexParams + " " + fileManager.getTempFilename("__oxdoc.tex"));

    String dvipngParams = "{0} -T tight --gamma 1.5 -o {1} {2}";
    if (config.getImageBgColor() == null) {
      dvipngParams += " -bg Transparent";
    }

    // have to do the following twice, because it sometimes seems to miss
    // fonts at the first run
    for (int i = 0; i < 2; i++) {
      // make sure the directory exists. If not, create it
      (new File(fileManager.getImageFilename(e.getFilename()))).getParentFile().mkdirs();

      Object[] args = {config.getDvipngArg(), fileManager.getImageFilename(e.getFilename()),
          fileManager.getTempFilename("__oxdoc.dvi")};
      run(config.getDvipng(), MessageFormat.format(dvipngParams, args));
    }
    (new File(fileManager.getTempFilename("__oxdoc.tex"))).delete();
    (new File(fileManager.getTempFilename("__oxdoc.dvi"))).delete();
    (new File(fileManager.getTempFilename("__oxdoc.aux"))).delete();
    (new File(fileManager.getTempFilename("__oxdoc.log"))).delete();
  }

  private void run(String filename, String parameters) throws IOException {
    logger.info("   " + filename + " " + parameters);

    Runtime run = Runtime.getRuntime();
    try {
      Process pp = run.exec(filename + " " + parameters);
      logger.info("");

      StreamGobbler errorGobbler = new StreamGobbler(pp.getErrorStream(), false);
      StreamGobbler outputGobbler = new StreamGobbler(pp.getInputStream(), true);

      errorGobbler.start();
      outputGobbler.start();

      pp.waitFor();

      if (errorGobbler.length() > 0) {
        logger.info("");
        logger.info(errorGobbler.getText());
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
