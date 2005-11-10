/**

oxdoc (c) Copyright 2005 by Y. Zwols [yori@brown.edu]

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

public class CommentParameterList extends BaseCommentBlock {

    protected String renderHTML() {
	if (size() == 0) return "";

	String out = "<!-- parameter table --!>\n";
	out += "<table class=\"parameter_table\">\n";

	for (int i = 0; i < size(); i++) {
	    String[] params = ((String) get(i)).split("[\t ]", 2);

	    out += "<tr>\n";
	    out += "<td class=\"declaration\" valign=\"baseline\">" + params[0] + "</td>\n";
	    if (params.length > 1)
		out += "<td class=\"description\" valign=\"baseline\">" + params[1] + "</td>\n";
	    out += "</tr>\n";
	}
	out += "</table>\n";
	return out;
    }
		
}
