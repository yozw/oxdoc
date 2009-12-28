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

import java.io.*;
import java.util.*;
import java.io.*;

public class Preprocessor {

	private ArrayList defines = new ArrayList();
	private OxDoc oxdoc = null;
	private Writer outputStream;
	private static Collection ignoredFiles = new ArrayList();
	private static Collection ignoredFileNames = new ArrayList();
	private static final int PLAINLINE = 0;
	private static final int ENDIF = 1;
	private static final int ELSE = 2;
	private static final int IFDEF = 4;
	private static final int IFNDEF = 8;
	private static final int DEFINE = 16;
	private static final int INCLUDE = 32;
	private static final int IMPORT = 64;

	public Preprocessor(OxDoc oxdoc, Writer outputStream) {
		this.oxdoc = oxdoc;
		this.outputStream = outputStream;
	}

	public void ProcessFile(File file) throws Exception {
       ProcessFile(file, file);
    }

	private void ProcessFile(File file, File mainFile) throws Exception {
		BufferedReader reader = null;
		try { 
			reader = new BufferedReader(new FileReader(file));
		} catch (IOException E) {
            throw new Exception("Could not open file " + file);
		}
		ProcessBlock(reader, 0, true, file, mainFile);
		outputStream.flush();
	}

	private void ignoreFile(File file) {
		File absFile = file.getAbsoluteFile();
		if (!ignoredFiles.contains(absFile)) {
			ignoredFiles.add(absFile);
	 	    if (!ignoredFileNames.contains(file.getName())) 
            {
			   ignoredFileNames.add(file.getName() );
			   oxdoc.warning("Included file " + file.getName() + " could not be opened. File will be ignored.");
            }
		}
	}

	private int getCommand(String line, ArrayList params) throws Exception {
		params.clear();

		String cmd = line.trim();
		if (!cmd.startsWith("#")) return PLAINLINE;

		int pos = cmd.indexOf(' ');
		if (pos == -1) 
			cmd = cmd.substring(1);
		else {
			params.add(cmd.substring(pos+1));
			cmd = cmd.substring(1, pos);
		}

		if (cmd.equals("endif")) return ENDIF;
		if (cmd.equals("else")) return ELSE;
		if (cmd.equals("ifdef")) return IFDEF;
		if (cmd.equals("ifndef")) return IFNDEF;
		if (cmd.equals("define")) return DEFINE;
		if (cmd.equals("include")) return INCLUDE;
		if (cmd.equals("import")) return IMPORT;
		throw new Exception("Unknown preprocessor directive: " + cmd);
	}

	private boolean listContainsString(ArrayList list, String str) {
		if (list == null) 
			return false;
		for (int i = 0; i < list.size(); i++)
			if ( ((String) list.get(i)).equals(str) ) 
				return true;
		return false;
	}

	private boolean isDefined(String define) {
		return listContainsString(defines, define);
	}

	private int ProcessBlock(BufferedReader is, int endMarkers, boolean active, File file, File mainFile) throws Exception {

		String line;
		String output = "";
		ArrayList params = new ArrayList();
		while ((line = is.readLine()) != null) {

			int cmd = getCommand(line, params);
			switch (cmd) {
				case IFDEF:
				case IFNDEF:
					boolean write = isDefined( ((String) params.get(0)).trim());
					if (cmd == IFNDEF)
						write = !write;

					int lastCmd = ProcessBlock(is, ELSE | ENDIF, write && active, file, mainFile);
					if (lastCmd == ELSE) 
						ProcessBlock(is, ENDIF, (!write) && active, file, mainFile);
					continue;
				case ELSE:
					if ((endMarkers & ELSE) == 0)
						throw new IOException("Unexpected else");
					return ELSE;
				case ENDIF:
					if ((endMarkers & ENDIF) == 0)
						throw new IOException("Unexpected endif");
					return ENDIF;
				case PLAINLINE:
					if (active)
						outputStream.write(line + "\n");
					break;
				case DEFINE:
					if (params.size() != 1)
						throw new IOException("#define requires 1 argument");
					if (active) 
						defines.add( ((String) params.get(0)).trim() );
					break;
				case INCLUDE:
				case IMPORT:
					if (params.size() != 1)
						throw new IOException( ((cmd == IMPORT)?"#import":"#include") + " requires 1 argument");
					if (active) {
                        boolean lookInSearchPath;
						String fileSpec = (String) params.get(0);
                        char firstChar = fileSpec.charAt(0);
                        char lastChar = fileSpec.charAt(fileSpec.length()-1);
                        if ((firstChar == '"') && (lastChar == '"'))
                            lookInSearchPath = false;
                        else if ((firstChar == '<') && (lastChar == '>'))
                            lookInSearchPath = true;
                        else {
                            oxdoc.warning("Invalid preprocessor clause: " + line);
                            break;
                        }
						String fileName = fileSpec.substring(1, fileSpec.length() - 1);
						if (cmd == IMPORT) 
							fileName += ".h";

                        ArrayList tryFiles = new ArrayList();
                        if (lookInSearchPath) 
                        {
						   for (int i = 0; i < oxdoc.config.IncludePaths.length; i++) 
                               tryFiles.add(new File(oxdoc.config.IncludePaths[i] + File.separatorChar + fileName));
                        }
						
                        String basePath = mainFile.getAbsoluteFile().getParent();
                        tryFiles.add(new File(basePath + File.separatorChar + fileName)); 

                        boolean done = false;
						for (int i = 0; i < tryFiles.size(); i++) 
                        {
                           File tryFile = (File) tryFiles.get(i);
						   if (tryFile.exists()) 
                           {
                               ProcessFile(tryFile, mainFile);
                               done = true;
                           }
                        }

                        if (!done) 
                        {
//                           String warning = "Could not find included file " + fileName + ". The file will be ignored.";
//						   for (int i = 0; i < tryFiles.size(); i++) 
//                              warning += "\nTried " + ((File) tryFiles.get(i)).toString();
//                           oxdoc.warning(warning);
						   for (int i = 0; i < tryFiles.size(); i++) 
                               ignoreFile((File) tryFiles.get(i));
                        }
					}
					break;
			}
		}
		return 0;
	}
}
