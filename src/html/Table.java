/**

 oxdoc (c) Copyright 2005-2023 by Y. Zwols

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

import static oxdoc.util.Utils.checkNotNull;

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
      if (row.cells.size() > colCount) {
        colCount = row.cells.size();
      }
    }
    return colCount;
  }

  @Override
  protected void render(StringBuilder buffer) {
    String[] evenOddClassAttr = {classAttr("odd"), classAttr("even")};

    buffer.append("<table");
    buffer.append(classAttr(tableSpecs.cssClass));
    buffer.append(">\n");

    int columnCount = getColumnCount();
    int rowIndex = 0;
    for (TableRow row : rows) {
      switch (row.type) {
        case HEADER:
          buffer.append("<tr><td colspan=\"");
          buffer.append(Integer.toString(columnCount));
          buffer.append("\" class=\"header\" valign=\"top\">");
          buffer.append(row.title);
          buffer.append("</td></tr>");
          break;

        case REGULAR:
          buffer.append("<tr");
          buffer.append(evenOddClassAttr[rowIndex % 2]);
          buffer.append(">\n");
          for (int c = 0; c < columnCount; c++) {
            String text = (c < row.cells.size()) ? row.cells.get(c) : "&nbsp;";
            String cssClass = (c < tableSpecs.columnCssClasses.size()) ? tableSpecs.columnCssClasses
                .get(c) : null;

            buffer.append("<td");
            buffer.append(classAttr(cssClass));
            buffer.append(">");
            buffer.append(text);
            buffer.append("</td>\n");
          }
          buffer.append("</tr>\n");
      }
      rowIndex++;
    }

    buffer.append("</table>\n");
  }
}
