import java.io.*;
import java.util.*;
import java.text.*;
import java.util.regex.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import org.xml.sax.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;

	public class LatexImageManager extends ArrayList {
		private static Hashtable _formulas = new Hashtable();
		private static Hashtable _filenames = new Hashtable();
		private static boolean _cacheLoaded = false;
		private static int _imageCounter = 0;

		private static class ImageEntry {
			public String _filename = "";
			public String _formula = "";
			public boolean needsGenerating = true;

			public ImageEntry(String formula, String filename) {
				_filename = filename;
				_formula = formula;
				_formulas.put(formula, this);
				_filenames.put(filename, this);
			}

			public String filename() { return _filename; }
			public String formula()  { return _formula; }

			public static ImageEntry Register(String Formula) {
				return Register(Formula, null);
			}

			public static ImageEntry Register(String formula, String filename) {
				formula = formula.trim().replace('\n',' ');
				ImageEntry entry = ((ImageEntry) _formulas.get(formula));
				if (entry != null)
					return entry;

				if (filename == null) {
					do {
						filename = "img" + (new Integer(++_imageCounter)).toString() + ".png";
					} while (_filenames.get(filename) != null);
				}
			
				return new ImageEntry(formula, filename);
			}
		}


		public static void SaveCache() {
			try {
				DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
				Document doc = builder.newDocument();

				Element root = doc.createElement("cache");
				doc.appendChild(root);
				
				for (Enumeration enum = _formulas.elements(); enum.hasMoreElements() ;) {
					ImageEntry e = (ImageEntry) enum.nextElement();
					Element newElement = doc.createElement("image");
					newElement.setAttribute("formula", e.formula());
					newElement.setAttribute("filename", e.filename());
					root.appendChild(newElement);
				}

            	// Prepare the DOM document for writing
            	Source source = new DOMSource(doc);
    
            	// Prepare the output file
            	File file = new File(FileManager.imageCache());
            	Result result = new StreamResult(file);
    
            	// Write the DOM document to the file
            	Transformer xformer = TransformerFactory.newInstance().newTransformer();
            	xformer.transform(source, result);
        	}
			catch (Exception e) {
				oxdoc.warning("Error writing image cache. Don't worry.");
			}
		}

		public static void LoadCache() {
    		try {
            	File file = new File(FileManager.imageCache());
				if (!file.exists())
					return;

				// Parse the file
         		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
         		Document doc = builder.parse(FileManager.imageCache());

         		// Find the tags of interest
         		NodeList nodes = doc.getElementsByTagName("image");
				for (int i = 0; i < nodes.getLength(); i++) {
            		Element element = (Element) nodes.item(i);
					String formula  = element.getAttribute("formula");
					String filename = element.getAttribute("filename");
					if (FileManager.outputFileExists(filename)) {
						ImageEntry entry = ImageEntry.Register(formula, filename);
						entry.needsGenerating = false;
					}
				}
			}
			catch (Exception e) {
				oxdoc.warning("Error reading image cache. Don't worry.");
			}
		}

		public static String FilterLatex(String text) {
			if (!_cacheLoaded) {
				LoadCache();
				_cacheLoaded = true;
			}
			return FilterExpressions(text);
		}

		private static String FilterExpressions(String text) {
			String pattern = "(\\$([^\\$]+)\\$)|(\\$\\$[^\\$]+\\$\\$)";
			Pattern p = Pattern.compile(pattern);
			Matcher m = p.matcher(text);
			StringBuffer myStringBuffer = new StringBuffer();

			int formulaNumber = 1;
			while (m.find()) {
				boolean isEquation = false;
				String formula = text.substring(m.start(), m.end());
				if (formula.startsWith("$$")) {
					formula = text.substring(m.start()+2, m.end()-2);
					isEquation = true;
				}
				else {
					formula = text.substring(m.start()+1, m.end()-1);
				}

				String replacement = formula;
				if (Config.EnableLatex)  {
					ImageEntry entry = ImageEntry.Register( (isEquation?"\\displaystyle{}":"\\textstyle{}") + formula);  
					replacement = "<img align=\"center\" src=\"" + entry.filename() + "\" alt=\"" + formula + "\">";
				}

				Object[] args = { isEquation?"equation":"expression", replacement };
				replacement = MessageFormat.format("<span class=\"{0}\">{1}</span>", args);
				m.appendReplacement(myStringBuffer, replacement); 
			}
			return m.appendTail(myStringBuffer).toString();
		}	

		public static void MakeLatexFiles() throws IOException {

			for (Enumeration enum = _formulas.elements(); enum.hasMoreElements() ;) {
				ImageEntry e = (ImageEntry) enum.nextElement();
				if (e.needsGenerating) 
					MakeLatexFile(e);
			}
			SaveCache();
		}

		public static void MakeLatexFile(ImageEntry e) throws IOException {
			oxdoc.message("Generating image for formula \"" + e.formula() + "\"...");

			File aFile = new File(FileManager.tempTexFile());
     		Writer output = new BufferedWriter( new FileWriter(aFile) );
			output.write("\\documentclass{article}\n");
			output.write("\\usepackage{amsmath}\n");

			for (int i = 0; i < Config.LatexPackages.size(); i++)
				output.write("\\usepackage{"  + (String) Config.LatexPackages.get(i) + "}\n");
			output.write("\\begin{document}\n");
			output.write("\\pagestyle{empty}\n");
			output.write("\\begin{align*}\n");
			output.write(e.formula() + "\n");
			output.write("\\end{align*}\n");
			output.write("\\end{document}\n");
			output.close();


			String latexParams  = "{0} -aux-directory={1} -output-directory={1} -interaction=batchmode {2}";
			Object[] args = {Config.LatexArg, FileManager.tempDir(), FileManager.tempFile("__oxdoc.tex")};
			
			Run(Config.Latex, MessageFormat.format(latexParams, args));
			
			String dvipngParams = "{0} -T tight --gamma 1.5 -bg Transparent -o {1} {2}";
			for (int i = 0; i < 2; i++) {
				Object[] _args = {Config.DvipngArg, FileManager.outputFile(e.filename()), FileManager.tempFile("__oxdoc.dvi")};
				Run(Config.Dvipng, MessageFormat.format(dvipngParams, _args));
			}
		}

		public static void Run(String filename, String parameters) throws IOException {
			oxdoc.message("   " + filename + " "+ parameters);
			Runtime run = Runtime.getRuntime();
			try {
         		Process pp = run.exec(filename + " " + parameters);
         		pp.waitFor();
			} catch (InterruptedException E) {
				System.out.println("Execution interrupted");
				System.exit(1);
			}
		 }
	}
