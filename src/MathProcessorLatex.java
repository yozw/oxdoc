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

import oxdoc.util.Logger;
import oxdoc.util.Logging;

import java.io.File;

import static oxdoc.util.Utils.checkNotNull;

public class MathProcessorLatex extends MathProcessor {
  private final Logger logger = Logging.getLogger();
  private final Config config;
  private final LatexImageManager latexImageManager;
  private final FileManager fileManager;

  public MathProcessorLatex(Config config, LatexImageManager latexImageManager, FileManager fileManager) {
    this.config = checkNotNull(config);
    this.latexImageManager = checkNotNull(latexImageManager);
    this.fileManager = checkNotNull(fileManager);
  }

  public String processFormula(String formula, boolean isInline) {
    String extFormula = (isInline ? "\\textstyle{}" : "\\displaystyle{}") + formula;
    String filename = latexImageManager.getFormulaFilename(extFormula);

    return "<img class=\"latex\" src=\"" + fileManager.getImageUrl(filename) + "\" alt=\"" + formula + "\">";
  }

  public boolean isSupported() {
    if (!(new File(config.getLatex())).exists()) {
      logger.warning("LaTeX executable not found. LaTeX support disabled (looking for " + config.getLatex() + ")");
      return false;
    }
    if (!(new File(config.getDvipng())).exists()) {
      logger.warning("Dvipng executable not found. LaTeX support disabled (looking for " + config.getDvipng() + ")");
      return false;
    }

    return true;
  }

  public String getExtraHeader() {
    return "<style>.expression { font-style: italic; font-family: times; font-size: 12px; }</style>";
  }
}
