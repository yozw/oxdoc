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

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogFile {
	Writer output = null;

	// create a blank file
	public LogFile() throws IOException {
		File aFile = new File("oxdoc.log");
		output = new BufferedWriter(new FileWriter(aFile));
		writeln("Log file created at " + GetDate());
	}

	private static String GetDate() {
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy HH:mm:ss");

		return sdf.format(date);
	}

	public void close() throws IOException {
		output.close();
	}

	public void write(Object s) throws IOException {
		output.write(s.toString());
	}

	public void writeln(Object s) throws IOException {
		output.write(s.toString() + "\n");
	}
}
