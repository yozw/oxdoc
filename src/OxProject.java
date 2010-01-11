/**

oxdoc (c) Copyright 2005-2009 by Y. Zwols

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

import java.util.*;


public class OxProject {
   private OxEntityList _files = new OxEntityList();
   private OxEntityList _symbols = new OxEntityList();
   public OxDoc oxdoc = null;
   public String name = "Untitled project";

   public OxProject(OxDoc oxdoc) {
      this.oxdoc = oxdoc;
   }

   public OxFile addFile(String name) {
      return (OxFile) _files.add(new OxFile(name, this));
   }

   public ArrayList files() {
      return _files.sortedList();
   }

   public OxEntity addSymbol(OxEntity entity) {
      return (OxEntity) _symbols.add(entity.referenceName(), entity);
   }

   public ArrayList symbols() {
      return _symbols.sortedList();
   }

   public ArrayList symbolsByDisplayName() {
      return _symbols.sortedListByDisplayName();
   }

   public OxEntity getSymbol(String name) {
      return (OxEntity) _symbols.get(name);
   }

   public String linkToSymbol(String name) {
      OxEntity entity = getSymbol(name);
      if (entity == null) {
         oxdoc.warning("Symbol '" + name + "' referenced to, but was not found");

         return name;
      } else

         return linkToEntity(entity);
   }

   public String linkToEntity(OxEntity entity) {
      return linkToEntity(entity, false);
   }

   public String linkToEntity(OxEntity entity, boolean useDisplayName) {
      if (useDisplayName)
         return "<a href=\"" + entity.url() + "\">" + entity.displayName() + "</a>";
      else

         return "<a href=\"" + entity.url() + "\">" + entity.name() + "</a>";
   }
}
