/**

oxdoc (c) Copyright 2005 by Y. Zwols

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

import java.util.*;
import java.io.*;
import java.text.*;

public class Documentor {
    public static void generateDocs() throws IOException {
	ArrayList files = oxdoc.project().files();
	for (int i = 0; i < files.size(); i++) {
	    OxFile file = (OxFile) files.get(i);
	    generateDoc(file, file.url());
	}

	generateStartPage("default.html");
	generateIndex("index.html");

	writeCss();

	LatexImageManager.makeLatexFiles();
    }

    private static void generateStartPage(String fileName) throws IOException {
			OutputFile output = new OutputFile(fileName, Config.ProjectName + " project home", FileManager.PROJECT);
			ArrayList files = oxdoc.project().files();
			output.writeln("<h2>" + Config.ProjectName + " files</h2>");
			output.writeln("<table class=\"table_of_contents\">");
			for (int i = 0; i < files.size(); i++) {
				OxFile file = (OxFile) files.get(i);
				output.writeln("<tr><td class=\"file\">" + file.smallIcon() + oxdoc.project().linkToEntity(file) + "</td>");
				output.writeln("<td class=\"description\">" + file.description() + "</td></tr>");
			}		
			output.writeln("</table>");
	output.close();
    }

    private static void generateIndex(String fileName) throws IOException {
			OutputFile output = new OutputFile(fileName, "Index", FileManager.INDEX);
	output.writeln("<table class=\"index\">");
			
	ArrayList symbols = oxdoc.project().symbolsByDisplayName();
	for (int i = 0; i < symbols.size(); i++) {
				String description = "";
				int iconType = -1;
				OxEntity entity = (OxEntity) symbols.get(i);
				if (entity instanceof OxClass) 
					description = "Class";
				else if (entity instanceof OxMethod) 
					description = "Method of " + oxdoc.project().linkToEntity(( (OxMethod) entity).parentClass());
				else if (entity instanceof OxFunction)
					description = "Global function";

				output.write( (i % 2 == 0) ? "<tr class=\"even_row\">" : "<tr class=\"odd_row\">");
				output.write("<td class=\"declaration\" valign=\"top\">" +
					entity.smallIcon() + oxdoc.project().linkToEntity(entity, true) + "</td>");
				output.write("<td class=\"description\" valign=\"top\">" + description + "</td>");
	    output.writeln("</tr>");
	}		
	output.writeln("</table>");
	output.close();
    }

    private static void generateDoc(OxFile oxFile, String fileName) throws IOException {
			OutputFile output = new OutputFile(fileName, oxFile.name(), oxFile.iconType());
    		try {
				output.writeln(oxFile.comment());
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
		
    private static void generateClassHeaderDocs(OutputFile output, OxClass oxclass, ArrayList methodList) throws IOException {
			String sectionName = (oxclass != null) ? oxclass.name() : "Global functions";
			String classPrefix = (oxclass != null) ? oxclass.name() : "";
			int iconType = (oxclass != null) ? FileManager.CLASS : FileManager.NONE;

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
			output.write("<h2>" + FileManager.largeIcon(iconType) + sectionName + " " + inheritanceText);
			output.writeln("</h2>");

			if (oxclass != null) {
				output.writeln(oxclass.comment());
			}
			
			if (methodList.size() > 0) {
				output.writeln("\n<!-- Methods of " + sectionName + " --!>");
				output.writeln("<table class=\"method_table\">");
				output.writeln("<tr><td colspan=\"2\" class=\"header\" valign=\"top\">Methods</td></tr>");	
				for (int i = 0; i < methodList.size(); i++) {
					OxFunction method = (OxFunction) methodList.get(i);
				    output.writeln("<tr><td class=\"declaration\" valign=\"top\">");
	         		output.writeln(method.smallIcon() + method.link());
				    output.writeln("</td><td class=\"description\" valign=\"top\">");
					output.write  (method.description());
					output.writeln("</td></tr>");
				}
				output.writeln("</table>");
			}
				
			if (inheritedMethods.length() > 0)
				output.writeln("<dl class=\"inherited_methods\">" + inheritedMethods + "</dl>\n");
		}

		private static void generateClassDetailDocs(OutputFile output, OxClass oxclass, ArrayList methodList) throws IOException {
			String sectionName = (oxclass != null) ? oxclass.name() : "Global functions";
			String classPrefix = (oxclass != null) ? oxclass.name() : "";
			int iconType = (oxclass != null) ? FileManager.CLASS : FileManager.NONE;
			
			output.writeln("\n<!-- Details for " + sectionName + " --!>");
			output.writeln("<h2>" + FileManager.largeIcon(iconType) + sectionName + " details</h2>");
			for (int i = 0; i < methodList.size(); i++) {
				OxFunction method = (OxFunction) methodList.get(i);
				String anchorName = ((method instanceof OxMethod)?classPrefix+"___":"")
									+ method.displayName();

				if (i != 0)
					output.writeln("\n<hr>");
					
				output.writeln("\n<!-- Method " + method.displayName() + " --!>");

				Object[] args = {anchorName, method.displayName(), method.largeIcon()};
				output.writeln(MessageFormat.format("<a name=\"{0}\"><h3>{2}{1}</h3></a>", args));

	    output.writeln("<span class=\"declaration\">" + method.declaration() + "</span>");

	    output.writeln(method.comment());
	}
    }

    private static void writeCss() throws IOException {
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

	
