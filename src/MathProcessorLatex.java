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

import java.io.File;

public class MathProcessorLatex extends MathProcessor {
	public MathProcessorLatex(OxDoc oxdoc) {
		super(oxdoc);
	}

	public String ProcessFormula(String formula, boolean isInline) {
		String extFormula = (isInline ? "\\textstyle{}" : "\\displaystyle{}")
				+ formula;
		String filename = oxdoc.latexImageManager
				.getFormulaFilename(extFormula);

		return "<img class=\"latex\" src=\""
				+ oxdoc.fileManager.imageUrl(filename) + "\" alt=\"" + formula
				+ "\">";
	}

	public boolean Supported(OxDoc oxdoc) {
		if (!(new File(oxdoc.config.Latex)).exists()) {
			oxdoc.warning("LaTeX executable not found. LaTeX support disabled (looking for "
					+ oxdoc.config.Latex + ")");

			return false;
		}
		if (!(new File(oxdoc.config.Dvipng)).exists()) {
			oxdoc.warning("Dvipng executable not found. LaTeX support disabled (looking for "
					+ oxdoc.config.Dvipng + ")");

			return false;
		}

		return true;
	}

	public String ExtraHeader() {
		return "<style>.expression { font-style: italic; font-family: times; font-size: 12px; }</style>";
	}
}
