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
import java.util.Collections;

import static oxdoc.Utils.checkNotNull;

public class Table extends Element {

  private static enum RowType {
    HEADER, REGULAR
  }

  private static class TableRow {
    final RowType type;
    final ArrayList<String> cells = new ArrayList<String>();
    String title = "";

    TableRow(RowType type) {
      this.type = checkNotNull(type);
    }
  }

  private final TableSpecs tableSpecs;
  private final ArrayList<TableRow> rows = new ArrayList<TableRow>();

  public Table() {
    this(new TableSpecs());
  }

  public Table(TableSpecs tableSpecs) {
    this.tableSpecs = checkNotNull(tableSpecs);
  }

  public TableSpecs specs() {
    return tableSpecs;
  }

  public void addHeaderRow(String title) {
    TableRow newRow = new TableRow(RowType.HEADER);
    newRow.title = title;
    rows.add(newRow);
  }

  public void addRow(String... row) {
    TableRow newRow = new TableRow(RowType.REGULAR);
    Collections.addAll(newRow.cells, row);
    rows.add(newRow);
  }

  public int getRowCount() {
    return rows.size();
  }

  public int getColumnCount() {
    int colCount = 0;
    for (TableRow row : rows) {
      if (row.cells.size() > colCount)
        colCount = row.cells.size();
    }
    return colCount;
  }

  @Override
  protected void render(StringBuffer buffer) {
    buffer.append(String.format("<table%s>\n", classAttr(tableSpecs.cssClass)));

    int columnCount = getColumnCount();

    int rowIndex = 0;
    for (TableRow row : rows) {
      switch (row.type) {
        case HEADER:
          buffer.append(String.format("<tr><td colspan=\"%d\" class=\"header\" valign=\"top\">%s</td></tr>",
              columnCount, row.title));
          break;

        case REGULAR:
          buffer.append(String.format("<tr%s>\n", classAttr((rowIndex % 2 == 1) ? "even" : "odd")));
          for (int c = 0; c < columnCount; c++) {
            String text = (c < row.cells.size()) ? row.cells.get(c) : "&nbsp;";
            String cssClass = (c < tableSpecs.columnCssClasses.size()) ? tableSpecs.columnCssClasses
                .get(c) : null;

            buffer.append(String.format("<td%s>%s</td>\n", classAttr(cssClass), text));
          }
          buffer.append("</tr>\n");
      }
      rowIndex++;
    }

    buffer.append("</table>\n");
  }
}
