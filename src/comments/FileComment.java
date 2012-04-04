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

package oxdoc.comments;

import java.text.MessageFormat;

import oxdoc.OxProject;

public class FileComment extends BaseComment {
	private String _author = "";
	private String _version = "";
	final int SECTION_AUTHOR = 100, SECTION_VERSION = 101;

	public FileComment(OxProject project) {
		super(project);

		registerSection("author", SECTION_AUTHOR);
		registerSection("authors", SECTION_AUTHOR);
		registerSection("version", SECTION_VERSION);
	}

	protected boolean addToSection(int SectionId, String text) {
		if (super.addToSection(SectionId, text))
			return true;

		switch (SectionId) {
		case SECTION_AUTHOR:
			_author += text;
			break;
		case SECTION_VERSION:
			_version += text;
			break;
		default:
			return false;
		}
		return true;
	}

	private String generateSection(String name, String classname, Object o) {
		String text = o.toString();
		if (text.length() == 0)
			return "";

		Object[] args = { classname, name, text };

		return MessageFormat
				.format("<dt class=\"{0}\">{1}:</dt><dd class=\"{0}\">{2}</dd>\n",
						args);
	}

	public String toString() {
		String out = longdescription() + "\n<dl>";

		out += generateSection("Author", "author", _author);
		out += generateSection("Version", "version", _version);

		out += generateSection("Example", "example", example());
		out += generateSection("Comments", "comments", comments());
		out += generateSection("See also", "seealso", see());

		out += "</dl>";

		return out;
	}
}
