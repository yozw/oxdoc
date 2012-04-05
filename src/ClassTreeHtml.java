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

import java.text.MessageFormat;
import java.util.ArrayList;

import oxdoc.entities.OxClass;

class ClassTreeHtml {

	private OxProject project = null;
	private ClassTree classTree = null;

	private String css = "table.tree { border-collapse: collapse;  }\n"
			+ "table.tree tr { height: 1px; padding: 0px; margin: 0px; }\n"
			+ "table.tree td { vertical-align: top; padding: 0px; }\n"
			+ "table.tree td.line { width: 16px; font-size: 5pt; }\n"
			+ "table.tree td.line div { display:inline-block; width: 12px; padding: 0px; overflow: hidden; height: 100%; margin: 0px; }\n"
			+ "table.tree td.line div.vline { margin-left: 16px; border-left: 1px solid silver; }\n"
			+ "table.tree td.line div.vline.last { height: 10px; }\n"
			+ "table.tree td.line div.hline { height: 9px; border-bottom: 1px solid silver;  }\n"
			+ "table.labelwrapper { height: 100%; width: 100%; border-collapse: collapse; margin: 0px;  }\n"
			+ "table.labelwrapper tr.bottom { height: 100%; }\n"
			+ "table.labelwrapper tr.bottom td.line { height: 100%; }\n"
			+ "table.tree td.text { padding-bottom: 8px; }\n"
			+ "table.tree td.label { padding-left: 8px; padding-right: 32px; height: 1px; }";

	private String script = "<![if !IE]>\n" + "<script>"
			+ "if (navigator.userAgent.indexOf('Firefox') != -1) { // firefox\n"
			+ "   var elements = document.getElementsByClassName('fffix');\n"
			+ "   for (var i = 0; i < elements.length; i++) elements[i].style.height = 'auto';\n" + "}\n"
			+ "</script>\n" + "<![endif]>";

	public ClassTreeHtml(OxDoc oxdoc, ClassTree classTree) {
		project = oxdoc.project;
		this.classTree = classTree;
	}

	public void writeHtml(OutputFile output) {
		int column_count = classTree.maxDepth() + 1;
		output.append_css(css);

		output.writeln("<table class=\"tree\">");
		output.writeln("<tbody><tr>");
		for (int i = 0; i < column_count; i++)
			output.write("<td style=\"width: 32px;\"></td>");
		output.writeln("<td style=\"width: 100%;\"></td>");
		output.writeln("</tr>");

		writeTree(output, classTree.getTopClasses(), "", column_count);

		output.writeln("</tbody></table>");

		output.writeln(script);
	}

	private void writeTree(OutputFile output, ArrayList classes, String prependText, int remainingColumns) {
		String labelWrapper = "<td style=\"height:1px; width:auto;\" colspan=\"{1}\" class=\"fffix\">\n"
				+ "<table class=\"labelwrapper\">\n" + "   <tbody><tr><td class=\"label\">{0}</td></tr>\n"
				+ "   <tr class=\"bottom\"><td class=\"line\">{2}</td></tr>\n" + "</tbody></table>\n" + "</td>";

		String textWrapper = "<td class=\"text\">{0}</td>";

		for (int i = 0; i < classes.size(); i++) {
			OxClass oxClass = (OxClass) classes.get(i);
			boolean isLast = (i == classes.size() - 1);
			ArrayList childClasses = classTree.getChildren(oxClass);

			String lineText = "";
			if (childClasses.size() > 0)
				lineText = "<div class=\"vline\">&nbsp;</div>";
			else
				lineText = "&nbsp;";

			output.writeln("<tr>");
			output.write(prependText);
			if (!isLast)
				output.writeln("<td class=\"line\"><div class=\"vline\"><div class=\"hline\">&nbsp;</div></div></td>");
			else
				output.writeln("<td class=\"line\"><div class=\"vline last\"><div class=\"hline\">&nbsp;</div></div></td>");

			Object[] labelArgs = { project.linkToEntity(oxClass), remainingColumns - 1, lineText };
			output.writeln(MessageFormat.format(labelWrapper, labelArgs));

			Object[] textArgs = { oxClass.description() };
			output.writeln(MessageFormat.format(textWrapper, textArgs));
			output.writeln("</tr>");

			String addPrependText;
			if (!isLast)
				addPrependText = "<td class=\"line\"><div class=\"vline\">&nbsp;</div></td>";
			else
				addPrependText = "<td class=\"line\">&nbsp;</td>";

			writeTree(output, childClasses, prependText + addPrependText + "\n", remainingColumns - 1);
		}
	}

}
