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
		private static Hashtable entries = new Hashtable();
		private static int ImageCounter = 0;

		private static class ImageEntry {
			public String filename = "img" + (new Integer(++ImageCounter)).toString() + ".png";
			public String formula = "";
		}

		public static String RegisterFormula(String Formula) {
			if (entries.get(Formula) == null) {
				ImageEntry entry = new ImageEntry();
				entry.formula = Formula;
				entries.put(Formula, entry);
				return entry.filename;
			}
			else
				return ((ImageEntry) entries.get(Formula)).filename;			
		}

		public static void MakeLatexFiles() throws IOException {

			for (Enumeration enum = entries.elements(); enum.hasMoreElements() ;) {
				ImageEntry e = (ImageEntry) enum.nextElement();
				MakeLatexFile(e);
			}
			SaveCache();
		}

		public static void SaveCache() {
			try {
				DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
				Document doc = builder.newDocument();

				Element root = doc.createElement("cache");
				doc.appendChild(root);
				
				for (Enumeration enum = entries.elements(); enum.hasMoreElements() ;) {
					ImageEntry e = (ImageEntry) enum.nextElement();
					Element newElement = doc.createElement("image");
					newElement.setAttribute("formula", e.formula);
					newElement.setAttribute("filename", e.filename);
					root.appendChild(newElement);
				}

            	// Prepare the DOM document for writing
            	Source source = new DOMSource(doc);
    
            	// Prepare the output file
            	File file = new File("images.xml");
            	Result result = new StreamResult(file);
    
            	// Write the DOM document to the file
            	Transformer xformer = TransformerFactory.newInstance().newTransformer();
            	xformer.transform(source, result);
        	}
			catch (Exception e) {
				oxdoc.warning("Error writing image cache. Don't worry.");
			}
		}

		public static String FilterLatex(String text) {
			String pattern = "\\$[^\\$]+\\$";
			Pattern p = Pattern.compile(pattern);
			Matcher m = p.matcher(text);
			StringBuffer myStringBuffer = new StringBuffer();

			int formulaNumber = 1;
			while (m.find()) {
				String formula = text.substring(m.start()+1, m.end()-1);
				if (OxDocConfig.EnableLatex)  {
					String fileName = RegisterFormula(formula);
    				m.appendReplacement(myStringBuffer, "<img align=\"center\" src=\"" + fileName + "\" alt=\"" + formula + "\">");
				}
				else
    				m.appendReplacement(myStringBuffer, "<i>" + formula + "</i>");
			}
			return m.appendTail(myStringBuffer).toString();
		}	

		public static void MakeLatexFile(ImageEntry e) throws IOException {
			oxdoc.message("Generating image for formula \"" + e.formula + "\"...");

			File aFile = new File(OxDocConfig.TempDir + "__oxdoc.tex");
     		Writer output = new BufferedWriter( new FileWriter(aFile) );
			output.write("\\documentclass{article}\n");

			for (int i = 0; i < OxDocConfig.LatexPackages.size(); i++)
				output.write("\\usepackage{"  + (String) OxDocConfig.LatexPackages.get(i) + "}\n");
			output.write("\\begin{document}\n");
			output.write("\\pagestyle{empty}\n");
			output.write("\\begin{align*}\n");
			output.write(e.formula + "\n");
			output.write("\\end{align*}\n");
			output.write("\\end{document}\n");
			output.close();

			Run(OxDocConfig.Latex, OxDocConfig.LatexArg +
					" -aux-directory=" + OxDocConfig.TempDir +
					" -output-directory=" + OxDocConfig.TempDir +
					" -interaction=batchmode " + OxDocConfig.TempDir + "__oxdoc.tex");
			for (int i = 0; i < 2; i++)
				Run(OxDocConfig.Dvipng, OxDocConfig.DvipngArg +
						" -T tight -bg Transparent -o " +
						OxDocConfig.OutputDir + e.filename + " " + OxDocConfig.TempDir + "__oxdoc.dvi");
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
