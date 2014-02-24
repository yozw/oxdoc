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

import oxdoc.Icon;

import java.text.MessageFormat;

import static oxdoc.util.Utils.checkNotNull;

public class Header extends Element {

  private final String title;
  private final RenderContext context;
  private final int level;
  private final Icon icon;

  public Header(int level, Icon icon, String title, RenderContext context) {
    this.level = level;
    this.icon = checkNotNull(icon);
    this.title = checkNotNull(title);
    this.context = checkNotNull(context);
  }

  @Override
  protected void render(StringBuilder buffer) {
    String iconHtml = context.getFileManager().getLargeIconHtml(icon);
    Object args[] = {Integer.toString(level), iconHtml, title};

    if (iconHtml.length() > 0)
      buffer.append(MessageFormat.format(
          "<h{0}><span class=\"icon\">{1}</span><span class=\"text\">{2}</span></h{0}>\n", args));
    else
      buffer.append(MessageFormat.format("<h{0}><span class=\"text\">{2}</span></h{0}>\n", args));
  }

}
