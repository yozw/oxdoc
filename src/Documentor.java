import java.util.*;
import java.io.*;
import java.text.*;

	public class Documentor {
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
			OutputFile output = new OutputFile(fileName, "Project homepage");
			ArrayList files = oxdoc.project().files();
			output.writeln("<h2>Project files</h2>");
			output.writeln("<ul class=\"table_of_contents\">");
			for (int i = 0; i < files.size(); i++) {
				OxFile file = (OxFile) files.get(i);
				output.writeln("<li>" + oxdoc.project().linkToEntity(file));
			}		
			output.writeln("</ul>");
			output.writeln("<h2>Other links</h2>");
			output.writeln("<ul class=\"table_of_contents\"><li>");
			output.writeln("<a href=\"index.html\">Symbol index</a>");
			output.writeln("<ul>");
			output.close();
		}

		static void generateIndex(String fileName) throws IOException {
			OutputFile output = new OutputFile(fileName, "Index");
			output.writeln("<ul class=\"index\">");
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
			OutputFile output = new OutputFile(fileName, oxFile.name());
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
		
		static void generateClassHeaderDocs(OutputFile output, OxClass oxclass, ArrayList methodList) throws IOException {
			String sectionName = (oxclass != null) ? "Class " + oxclass.name() : "Global functions";
			String classPrefix = (oxclass != null) ? oxclass.name() : "";

			String inheritedMethods = "";
			String inheritanceText = "";

			if ((oxclass != null) && (oxclass.parentClassName() != null)) {
				OxClass pclass = oxclass;

				while (true) {
					String parentClassName = pclass.parentClassName();
					if (parentClassName == null)
						break;

					String link = oxdoc.project().linkToSymbol(parentClassName);

					inheritanceText += " : " + link;
					
					OxEntity entity = oxdoc.project().getSymbol(parentClassName);
					if ((entity == null) || !(entity instanceof OxClass))
						break;
					pclass = (OxClass) entity;

					ArrayList methods = pclass.methods();
					if (methods.size() > 0) {
						inheritedMethods += "<dt>Inherited methods from " + link + ":</dt><dd>\n";
						for (int i = 0; i < methods.size(); i++) {
							OxFunction method = (OxFunction) methods.get(i);
							if (i > 0) inheritedMethods += ", ";
							inheritedMethods += method.link();
						}
						inheritedMethods += "</dd>\n";
					}
				}
			}
			
			output.writeln("\n<!-- " + sectionName + " --!>");
			output.write("<h2>" + sectionName + " " + inheritanceText);
			output.writeln("</h2>");

			if (oxclass != null) {
				output.writeln(oxclass.Comment());
			}
			
			output.writeln("\n<!-- Methods of " + sectionName + " --!>");
			output.writeln("<table class=\"method_table\">");

			for (int i = 0; i < methodList.size(); i++) {
				OxFunction method = (OxFunction) methodList.get(i);
			    output.writeln("<tr><td class=\"declaration\" valign=\"top\">");
         		output.writeln(method.link());
			    output.writeln("</td><td class=\"description\" valign=\"top\">");
				output.write  (method.Comment().description());
				output.writeln("</td></tr>");
			}
			output.writeln("</table>");

			if (inheritedMethods.length() > 0)
				output.writeln("<dl class=\"inherited_methods\">" + inheritedMethods + "</dl>\n");
		}

		static void generateClassDetailDocs(OutputFile output, OxClass oxclass, ArrayList methodList) throws IOException {
			String sectionName = (oxclass != null) ? "Class " + oxclass.name() : "Global functions";
			String classPrefix = (oxclass != null) ? oxclass.name() : "";

			output.writeln("\n<!-- Details for " + sectionName + " --!>");
			output.writeln("<h2>" + sectionName + " details</h2>");
			for (int i = 0; i < methodList.size(); i++) {
				OxFunction method = (OxFunction) methodList.get(i);
				String anchorName = ((method instanceof OxMethod)?classPrefix+"___":"")
									+ method.displayName();

				if (i != 0)
					output.writeln("\n<hr>");
					
				output.writeln("\n<!-- Method " + method.displayName() + " --!>");

				Object[] args = {anchorName, method.displayName()};
				output.writeln(MessageFormat.format("<a name=\"{0}\"><h3>{1}</h3></a>", args));

				output.writeln("<span class=\"declaration\">" + method.declaration() + "</span>");

				output.writeln(method.Comment());
			}
		}

		static void writeCss() throws IOException {
			if (OutputFile.exists("oxdoc.css"))
				return;

			InputStream resourceFile = oxdoc.class.getResourceAsStream("oxdoc.css");
			if (resourceFile == null) {
				oxdoc.warning("oxdoc.css resource was not found. Cannot write a style sheet.");
				return;
			}

     		OutputFile output = new OutputFile("oxdoc.css");
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

	