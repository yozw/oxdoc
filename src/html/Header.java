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

package oxdoc.html;

import java.text.MessageFormat;

import oxdoc.OxDoc;

public class Header extends Element {

	String title;
	int level;
	int iconType;

	public Header(OxDoc oxdoc, int level, int iconType, String title) {
		super(oxdoc);

		this.oxdoc = oxdoc;
		this.level = level;
		this.iconType = iconType;
		this.title = title;
	}

	protected void render(StringBuffer buffer) {
		Object args[] = { "" + level, oxdoc.fileManager.largeIcon(iconType), title };

		if (oxdoc.config.EnableIcons)
			buffer.append(MessageFormat.format(
					"<h{0}><span class=\"icon\">{1}</span><span class=\"text\">{2}</span></h{0}>\n", args));
		else
			buffer.append(MessageFormat.format("<h{0}><span class=\"text\">{2}</span></h{0}>\n", args));
	}

}
