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

import java.util.ArrayList;

import oxdoc.OxDoc;

public class DefinitionList extends Element {

	String cssClass;
	ArrayList labels = new ArrayList();
	ArrayList definitions = new ArrayList();

	public DefinitionList(OxDoc oxdoc, String cssClass) {
		super(oxdoc);
		this.cssClass = cssClass;
	}

	public void addItem(String label, String definition) {
		labels.add(label);
		definitions.add(definition);
	}

	protected void render(StringBuffer buffer) {
		buffer.append(String.format("<dl%s>\n", classAttr(cssClass)));
		for (int i = 0; i < labels.size(); i++) {
			String label = (String) labels.get(i);
			String definition = (String) definitions.get(i);

			buffer.append(String.format("<dt>%s</dt><dd>%s</dd>\n", label, definition));
		}
		buffer.append("</dl>\n");
	}

	public String toString() {
		StringBuffer bf = new StringBuffer();
		render(bf);
		return bf.toString();
	}

}
