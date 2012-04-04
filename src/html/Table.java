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

import java.util.*;
import java.io.*;
import java.text.*;
import oxdoc.*;

public class Table extends Element {

   private TableSpecs tableSpecs;

   ArrayList rows = new ArrayList();
  
   public Table (OxDoc oxdoc) 
   {
       super(oxdoc);
       this.tableSpecs = new TableSpecs();
   }

   public Table (OxDoc oxdoc, TableSpecs tableSpecs) 
   {
       super(oxdoc);
       this.tableSpecs = tableSpecs;
   }

   public TableSpecs specs()
   {
       return tableSpecs;
   }

   public void addRow(String[] row)
   {
       ArrayList newRow = new ArrayList();
       for (int i = 0; i < row.length; i++)
          newRow.add(row[i]);
       rows.add(newRow);
   }

   public int getRowCount()
   {
       return rows.size();
   }

   public int getColumnCount()
   {
       int colCount = 0;
       for (int r = 0; r < rows.size(); r++)
       {
           ArrayList row = (ArrayList) rows.get(r);
           if (row.size() > colCount)
              colCount = row.size();
       }
       return colCount;
   }

   private String classAttr(String className)
   {
       if ((className == null) || (className.trim().length() == 0))
          return "";
       else
          return String.format(" class=\"%s\"", className);
   }

   protected void render(StringBuffer buffer)
   {
       buffer.append(String.format("<table%s>\n", classAttr(tableSpecs.cssClass)));

       int columnCount = getColumnCount();
       
       for (int r = 0; r < rows.size(); r++)
       {
           ArrayList row = (ArrayList) rows.get(r);
           buffer.append(String.format("<tr%s>\n", classAttr((r % 2 == 1) ? "even" : "odd")));
           for (int c = 0; c < columnCount; c++)
           {
               String text = (c < row.size()) ? (String) row.get(c) : "&nbsp;";
               String cssClass = (c < tableSpecs.columnCssClasses.size()) ? (String) tableSpecs.columnCssClasses.get(c) : null;

               buffer.append(String.format("<td%s>%s</td>\n", classAttr(cssClass), text));
           }
           buffer.append("</tr>\n");
      }

      buffer.append("</table>\n");
   }
}


