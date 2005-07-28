import java.util.*;
import java.io.*;

	public class OxDocumentor {
		static void generateDocs() throws IOException {
			ArrayList files = oxdoc.project().files();
			for (int i = 0; i < files.size(); i++) {
				OxFile file = (OxFile) files.get(i);
				generateDoc(file, file.url());
			}

			generateStartPage("default.html");
			generateIndex("index.html");

			writeCss();

			LatexImageManager.MakeLatexFiles();
		}

		static void generateStartPage(String fileName) throws IOException {
			OxDocOutputFile output = new OxDocOutputFile(fileName, "Project homepage");
			ArrayList files = oxdoc.project().files();
			output.writeln("<h2>Project files</h2>");
			output.writeln("<ul>");
			for (int i = 0; i < files.size(); i++) {
				OxFile file = (OxFile) files.get(i);
				output.writeln("<li>" + oxdoc.project().linkToEntity(file));
			}		
			output.writeln("</ul>");
			output.writeln("<h2>Other links</h2>");
			output.writeln("<ul><li>");
			output.writeln("<a href=\"index.html\">Symbol index</a>");
			output.writeln("<ul>");
			output.close();
		}

		static void generateIndex(String fileName) throws IOException {
			OxDocOutputFile output = new OxDocOutputFile(fileName, "Index");
			output.writeln("<ul>");
			ArrayList symbols = oxdoc.project().symbols();
			for (int i = 0; i < symbols.size(); i++) {
				OxEntity entity = (OxEntity) symbols.get(i);
				output.write("<li>" + oxdoc.project().linkToEntity(entity));
				if (entity instanceof OxClass)
					output.write(" class");
					
				output.writeln("");
			}		
			output.writeln("</ul>");
			output.close();
		}

		static void generateDoc(OxFile oxFile, String fileName) throws IOException {
			OxDocOutputFile output = new OxDocOutputFile(fileName, oxFile.name());
    		try {
				ArrayList classes = oxFile.classes();
				for (int i = 0; i < classes.size(); i++) {
					OxClass oxclass = (OxClass) classes.get(i);
					generateClassHeaderDocs(output, oxclass, oxclass.methods());
				}
				if (oxFile.functions().size() > 0)
					generateClassHeaderDocs(output, null, oxFile.functions());

				for (int i = 0; i < classes.size(); i++) {
					OxClass oxclass = (OxClass) classes.get(i);
					generateClassDetailDocs(output, oxclass, oxclass.methods());
				}
				if (oxFile.functions().size() > 0)
					generateClassDetailDocs(output, null, oxFile.functions());
			}
    		finally {
      			if (output != null)
					output.close();
    		}
		}
		
		static void generateClassHeaderDocs(OxDocOutputFile output, OxClass oxclass, ArrayList methodList) throws IOException {
			String className = (oxclass != null) ? "Class " + oxclass.name() : "Global functions";
			String classPrefix = (oxclass != null) ? oxclass.name() : "";
			output.write("<h2>" + className);
			if ((oxclass != null) && (oxclass.parentClassName() != null)) 
				output.write(" : " + oxdoc.project().linkToSymbol(oxclass.parentClassName()));
			output.writeln("</h2>");

			if (oxclass != null) {
				output.write(oxclass.Comment());
			}
			
			output.writeln("<table class=\"methods\">");

			for (int i = 0; i < methodList.size(); i++) {
				OxFunction method = (OxFunction) methodList.get(i);
			    output.writeln("<tr><td class=\"declaration\">");
         		output.writeln("<a href=\"" + method.url() + "\">" + method.displayName() + "</a>");
			    output.writeln("</td><td class=\"description\">");
				output.write  (method.Comment().description());
				output.writeln("</td>");
			}
			output.writeln("</table>");
		}

		static void generateClassDetailDocs(OxDocOutputFile output, OxClass oxclass, ArrayList methodList) throws IOException {
			String className = (oxclass != null) ? "Class " + oxclass.name() : "Global functions";
			String classPrefix = (oxclass != null) ? oxclass.name() : "";
			output.writeln("<h2>" + className + " details</h2>");
			for (int i = 0; i < methodList.size(); i++) {
				OxFunction method = (OxFunction) methodList.get(i);

				if (method instanceof OxMethod)
			    	output.writeln("<a name=\"" + classPrefix + "___" + method.displayName() + "\">");
				else
			    	output.writeln("<a name=\"" + method.displayName() + "\">");
				output.writeln("<h3>" + method.displayName() + "</h3></a>");
				output.writeln("<pre>" + method.declaration() + "</pre>");

				output.write(method.Comment());
				output.writeln("<hr>");
			}
		}

		static void writeCss() throws IOException {
			if (OxDocOutputFile.fileExists("oxdoc.css"))
				return;

			InputStream resourceFile = oxdoc.class.getResourceAsStream("oxdoc.css");
			if (resourceFile == null) {
				oxdoc.warning("oxdoc.css resource was not found. Cannot write a style sheet.");
				return;
			}

     		OxDocOutputFile output = new OxDocOutputFile("oxdoc.css");
			BufferedReader cssReader = new BufferedReader( new InputStreamReader(resourceFile) );

			while (true) {
            	int data = cssReader.read();
            	if (data < 0)
					break;
            	output.writeChar(data);
         	}
			cssReader.close();
			output.close();

			oxdoc.message("oxdoc.css not found. A new one has been created.");
		}

	}		

	