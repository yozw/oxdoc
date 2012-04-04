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

public class FunctionComment extends BaseComment {
	private BaseCommentBlock _params;
	private BaseCommentBlock _returns;
	private boolean _hasInternalModifier = false;

	final int SECTION_PARAM = 200, SECTION_RETURNS = 201;
	final int MODIFIER_INTERNAL = 300;

	public FunctionComment(OxProject project) {
		super(project);
		_params = new CommentParameterList(project);
		_returns = new CommentTextBlock(project);

		registerSection("param", SECTION_PARAM);
		registerSection("params", SECTION_PARAM);
		registerSection("return", SECTION_RETURNS);
		registerSection("returns", SECTION_RETURNS);
		registerModifier("internal", MODIFIER_INTERNAL);
	}

	protected boolean addToSection(int SectionId, String text) {
		if (super.addToSection(SectionId, text))
			return true;

		switch (SectionId) {
		case SECTION_PARAM:
			_params.add(text);
			break;
		case SECTION_RETURNS:
			_returns.add(text);
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

	protected boolean processModifier(int ModifierId) {
		if (super.processModifier(ModifierId))
			return true;
		if (ModifierId == MODIFIER_INTERNAL) {
			_hasInternalModifier = true;
			return true;
		}
		return false;
	}

	public String toString() {
		String out = "<dl>\n<dd>" + longdescription() + "<dl>\n";

		out += generateSection("Parameters", "parameters", params());
		out += generateSection("Returns", "returns", returns());
		out += generateSection("Example", "example", example());
		out += generateSection("Comments", "comments", comments());
		out += generateSection("See also", "seealso", see());

		out += "</dl></dd>\n</dl>";

		return out;
	}

	public BaseCommentBlock params() {
		return _params;
	}

	public BaseCommentBlock returns() {
		return _returns;
	}

	public boolean hasInternalModifier() {
		return _hasInternalModifier;
	}
}
