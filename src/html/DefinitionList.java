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

package oxdoc.html;

import java.util.ArrayList;

import static oxdoc.util.Utils.checkNotNull;

public class DefinitionList extends Element {

  private final String cssClass;
  private final ArrayList<String> labels = new ArrayList<String>();
  private final ArrayList<String> definitions = new ArrayList<String>();

  public DefinitionList(String cssClass) {
    this.cssClass = checkNotNull(cssClass);
  }

  public void addItem(String label, String definition) {
    labels.add(label);
    definitions.add(definition);
  }

  @Override
  protected void render(StringBuilder buffer) {
    buffer.append("<dl");
    buffer.append(classAttr(cssClass));
    buffer.append(">\n");
    for (int i = 0; i < labels.size(); i++) {
      String label = labels.get(i);
      String definition = definitions.get(i);

      buffer.append("<dt>");
      buffer.append(label);
      buffer.append("</dt><dd>");
      buffer.append(definition);
      buffer.append("</dd>\n");
    }
    buffer.append("</dl>\n");
  }

  @Override
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder();
    render(stringBuilder);
    return stringBuilder.toString();
  }
}
