/**

oxdoc (c) Copyright 2005-2009 by Y. Zwols

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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import oxdoc.gui.OxDocGui;
import oxdoc.parser.ParseException;

public class OxDocCmd implements Logger {
	private OxDoc oxdoc = null;
	private ArrayList files = new ArrayList();
	public LogFile Logfile = null;

	public void writeMessage(String message, int Code) {
		try {
			System.out.println(message);
			if (Logfile != null)
				Logfile.writeln(message);
		} catch (IOException E) {
		}
	}

	public int parseFiles() throws Exception {
		int totalFiles = 0;
		for (int i = 0; i < files.size(); i++) {
			String filename = ((String) files.get(i)).trim();
			if (filename.length() == 0)
				continue;

			try {
				oxdoc.addFiles(filename);
				totalFiles++;
			} catch (ParseException e) {
				oxdoc.message("Parsing of file " + filename + " failed");
				oxdoc.message(e.toString());
			} catch (FileNotFoundException e) {
				oxdoc.message("File not found: " + filename);
			}
		}

		return totalFiles;
	}

	public void examineCommandLine(String[] args) throws Exception {
		// examine command line
		for (int i = 0; i < args.length; i++) {
			if (!args[i].startsWith("-")) {
				files.add(args[i]);

				continue;
			}

			String option = args[i].substring(1);
			if (oxdoc.config.setSimpleOption(option))
				continue;

			i++;
			if (i == args.length)
				throw new Exception("Value expected after option -" + option);

			if (!oxdoc.config.setOption(option, args[i]))
				throw new Exception("Invalid option -" + option);
		}
	}

	// We need this function in order to circumvent a bug in gcj:
	// even an empty commandline will pass non-empty args to main
	public static boolean emptyArray(String[] x) {
		for (int i = 0; i < x.length; i++)
			if (x[i].trim().length() > 0)
				return false;

		return true;
	}

	public void run(String[] args, Logger logger) {
		oxdoc = new OxDoc(logger);

		if (emptyArray(args)) {
			System.out.println("\nUsage is:");
			System.out
					.println("    java oxdoc [options] inputfile [inputfile ...]");
			Config.listOptions();

			return;
		}

		try {
			// do configuration
			oxdoc.config.load();
			examineCommandLine(args);
			oxdoc.config.validate();

			// execute parsing and document generation
			Logfile = new LogFile();
			if (parseFiles() > 0) {
				oxdoc.generateDocs();
				Logfile.close();
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	} 

	// oxdoc entry point
	public static void main(String[] args) {

		boolean runGui = false;

		if ((args.length == 1) && (args[0].compareTo("--gui") == 0))
			runGui = true;

		if (runGui) {
			OxDocGui gui = new OxDocGui();
			gui.run(args);
		} else {
			OxDocCmd cmd = new OxDocCmd();
			cmd.run(args, cmd);
		}
	}
}
