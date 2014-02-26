/**

 oxdoc (c) Copyright 2005-2014 by Y. Zwols

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

public class MathProcessorMathjax extends MathProcessor {
  public String processFormula(String formula, boolean isInline) {
    if (isInline) {
      return "\\(" + formula + "\\)";
    } else {
      return "$$" + formula + "$$";
    }
  }

  public String getExtraHeader() {
    return "<script type=\"text/javascript\" src=\"http://cdn.mathjax.org/mathjax/latest/MathJax.js?config=TeX-AMS-MML_HTMLorMML\"></script>";
  }

  public String getExtraFooter() {
    return "Math typesetting by <a href=\"http://www.mathjax.org/\">Mathjax</a>";
  }
}
