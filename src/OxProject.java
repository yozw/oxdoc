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

import oxdoc.entities.*;
import oxdoc.util.Logger;
import oxdoc.util.Logging;

import static oxdoc.util.Utils.checkNotNull;

public class OxProject {
  private final FileManager fileManager;
  private final TextProcessor textProcessor;
  private final Logger logger = Logging.getLogger();
  private final Config config;
  private final OxEntityList<OxFile> files = new OxEntityList<OxFile>();
  private final OxEntityList<OxEntity> symbols = new OxEntityList<OxEntity>();

  public OxProject(FileManager fileManager, TextProcessor textProcessor, Config config) {
    this.fileManager = checkNotNull(fileManager);
    this.textProcessor = checkNotNull(textProcessor);
    this.config = checkNotNull(config);
  }

  public OxFile addFile(String name) {
    OxFile oxFile = new OxFile(name, this);
    files.add(oxFile);
    return oxFile;
  }

  public OxEntityList<OxFile> getFiles() {
    return files;
  }

  public OxEntity addSymbol(OxEntity entity) {
    if (getSymbol(entity.getReferenceName()) != null) {
      logger.warning("Multiple declarations of symbol '" + entity.getReferenceName() + "'");
    }
    return symbols.add(entity.getReferenceName(), entity);
  }

  public void addSymbolEnumElements(OxEnum oxenum) {
    for (OxEnumElement element : oxenum.getElements()) {
      addSymbol(element);
    }
  }

  public OxEntityList<OxClass> getClasses() {
    return symbols.filterByClass(OxClass.class);
  }

  public OxEntityList<OxEntity> getSymbols() {
    return symbols;
  }

  public OxEntity getSymbol(String name) {
    return symbols.get(name);
  }

  public String getLinkToSymbol(String name) {
    OxEntity entity = getSymbol(name);
    if (entity == null) {
      logger.warning("Symbol '" + name + "' referenced, but not found");
      return name;
    } else {
      return getLinkToEntity(entity);
    }
  }

  public String getLinkToEntity(OxEntity entity) {
    return getLinkToEntity(entity, false);
  }

  public String getLinkToEntity(OxEntity entity, String displayText) {
    return "<a href=\"" + entity.getUrl() + "\">" + displayText + "</a>";
  }

  public String getLinkToEntity(OxEntity entity, boolean useDisplayName) {
    if (useDisplayName) {
      return "<a href=\"" + entity.getUrl() + "\">" + entity.getDisplayName() + "</a>";
    } else {
      return "<a href=\"" + entity.getUrl() + "\">" + entity.getName() + "</a>";
    }
  }

  public String getName() {
    return config.getProjectName();
  }

  public TextProcessor getTextProcessor() {
    return textProcessor;
  }

  public FileManager getFileManager() {
    return fileManager;
  }
}
