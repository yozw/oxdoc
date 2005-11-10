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

public class oxdoc {

    private static OxProject _project = new OxProject();
    private static ArrayList files = new ArrayList();

    public static String ProductName = "oxdoc";
    public static String Version = Constants.VERSION;
    public static String Url = "http://oxdoc.sourceforge.net";
    public static String CopyrightNotice = "(c) Copyright 2005 by Y. Zwols [yorizwols@users.sourceforge.net]\n\n" +
	"oxdoc is free software and comes with ABSOLUTELY NO WARRANTY.\n" +
	"You are welcome to redistribute it under certain conditions.\n" +
	"See the LICENSE file for distribution details.\n";

    public static OxProject project() {
	return _project;
    }
	
    public static void warning(String msg) {
	System.out.println("Warning: " + msg);
    }

    public static void message(String msg) {
	System.out.println(msg);
    }

    public static int parseFiles() throws Exception {
	int totalFiles = 0;
	for (int i = 0; i < files.size(); i++) {

	    String filename = ((String) files.get(i)).trim();
	    if (filename.length() == 0)
		continue;

	    System.out.println("Processing file " + filename + "...");
	    try {
		Parser parser = new Parser(new java.io.FileInputStream(filename), project().addFile(filename), project());
		parser.OxFileDefinition();
		totalFiles++;
	    }
	    catch(ParseException e) {
		System.out.println("Parsing of file " + filename + " failed");
		System.out.println(e.toString());
	    }
	    catch(FileNotFoundException e) {
		System.out.println("File not found: " + filename);
	    }
	}
	return totalFiles;
    }

    public static void examineCommandLine(String args[]) throws Exception {
	// examine command line
	for (int i = 0; i < args.length; i++) {
	    if (!args[i].startsWith("-")) {
		files.add(args[i]);
		continue;
	    }

	    String option = args[i].substring(1);
	    if (Config.setSimpleOption(option))
		continue;

	    i++;
	    if (i == args.length) 
		throw new Exception("Value expected after option -" + option);

	    if (!Config.setOption(option, args[i]))
		throw new Exception("Invalid option -" + option);
	}
    }

    // We need this function in order to circumvent a bug in gcj:
    // even an empty commandline will pass non-empty args to main
    public static boolean emptyArray(String x[]) {
	for (int i = 0; i < x.length; i++)
	    if (x[i].trim().length() > 0) 
		return false;
	return true;
    }
	
    // oxdoc entry point
    public static void main ( String args [ ] ) {
	System.out.println(ProductName + " " + Version + " [" + Constants.COMPILETIME + "]");
	System.out.println(CopyrightNotice);

	if (emptyArray(args)) {
	    System.out.println("\nUsage is:");
	    System.out.println("    java oxdoc [options] inputfile [inputfile ...]");
	    System.out.println("\nOptions:");
	    Config.listOptions();
	    return;
	}

	try {
	    // do configuration
	    Config.load();
	    examineCommandLine(args);
	    Config.validate();

	    // execute parsing and document generation
	    if (parseFiles() > 0)
		Documentor.generateDocs();
	}
	catch(Exception e){
   	    System.out.println(e.getMessage());
	    e.printStackTrace();
	}
    }
}
