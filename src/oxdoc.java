import java.util.*;
import java.io.*;

  public class oxdoc {

    private static OxProject _project = new OxProject();
	private static ArrayList files = new ArrayList();

	public static String ProductName = "oxdoc";
	public static String Version = Constants.VERSION;
	public static String Url = "http://oxdoc.sourceforge.net";
	public static String CopyrightNotice = "(c) Copyright 2005 by Y. Zwols";

	public static OxProject project() {
		return _project;
	}
	
	public static void warning(String msg) {
		System.out.println("Warning: " + msg);
	}

	public static void message(String msg) {
		System.out.println(msg);
	}

	public static void parseFiles() throws Exception {
	  	for (int i = 0; i < files.size(); i++) {
			String filename = (String) files.get(i);
   	       	System.out.println("Processing file " + filename + "...");
			try {
           		Parser parser = new Parser(new java.io.FileInputStream(filename), project().addFile(filename), project());
           		parser.OxFileDefinition();
			}
        	catch(ParseException e) {
           		System.out.println("Parsing of file " + filename + " failed");
 	       		System.out.println(e.toString());
	    	}
		}
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
	
    // oxdoc entry point
    public static void main ( String args [ ] ) {
      System.out.println(ProductName + " " + Version + " [" + Constants.COMPILETIME + "] " + CopyrightNotice);

      if (args.length == 0) {
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
		parseFiles();
	    Documentor.generateDocs();
	  }
      catch(Exception e){
   	    System.out.println(e.getMessage());
		e.printStackTrace();
      }
    }
  }
