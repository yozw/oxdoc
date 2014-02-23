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

import oxdoc.entities.OxEntity;
import oxdoc.entities.OxEntityList;
import oxdoc.entities.OxEnum;
import oxdoc.entities.OxFile;

import java.util.ArrayList;

public class OxProject {
  public final FileManager fileManager;
  public final TextProcessor textProcessor;
  public final Logger logger = Logging.getLogger();
  public String name = "Untitled project";
  private final OxEntityList list = new OxEntityList();
  private final OxEntityList symbols = new OxEntityList();

  public OxProject(FileManager fileManager, TextProcessor textProcessor) {
    this.fileManager = fileManager;
    this.textProcessor = textProcessor;
  }

  public OxFile addFile(String name) {
    return (OxFile) list.add(new OxFile(name, this));
  }

  public ArrayList getFiles() {
    return list.sortedList();
  }

  public OxEntity addSymbol(OxEntity entity) {
    if (getSymbol(entity.getReferenceName()) != null)
      logger.warning("Multiple declarations of symbol '" + entity.getReferenceName() + "'");
    return symbols.add(entity.getReferenceName(), entity);
  }

  public void addSymbolEnumElements(OxEnum oxenum) {
    ArrayList elements = oxenum.getElements();
    for (int i = 0; i < elements.size(); i++)
      addSymbol((OxEntity) elements.get(i));
  }

  public ArrayList getSymbols() {
    return symbols.sortedList();
  }

  public OxEntityList getClasses() {
    return symbols.getClasses();
  }

  public ArrayList getSymbolsByDisplayName() {
    return symbols.getSortedListByDisplayName();
  }

  public OxEntity getSymbol(String name) {
    return symbols.get(name);
  }

  public String getLinkToSymbol(String name) {
    OxEntity entity = getSymbol(name);
    if (entity == null) {
      logger.warning("Symbol '" + name + "' referenced to, but was not found");
      return name;
    } else

      return getLinkToEntity(entity);
  }

  public void printSymbols() {
    ArrayList list = getSymbolsByDisplayName();
    for (int i = 0; i < list.size(); i++)
      System.out.println(list.get(i));
  }

  public String getLinkToEntity(OxEntity entity) {
    return getLinkToEntity(entity, false);
  }

  public String getLinkToEntity(OxEntity entity, String displayText) {
    return "<a href=\"" + entity.getUrl() + "\">" + displayText + "</a>";
  }

  public String getLinkToEntity(OxEntity entity, boolean useDisplayName) {
    if (useDisplayName)
      return "<a href=\"" + entity.getUrl() + "\">" + entity.getDisplayName() + "</a>";
    else
      return "<a href=\"" + entity.getUrl() + "\">" + entity.getName() + "</a>";
  }
}
