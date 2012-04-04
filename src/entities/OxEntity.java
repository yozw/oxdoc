/**

oxdoc (c) Copyright 2005 by Y. Zwols

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

package oxdoc.entities;

import oxdoc.*;
import oxdoc.comments.*;

public class OxEntity {
   private String name;
   protected OxFile _parentFile = null;
   private BaseComment _comment = null;
   private int _iconType = FileManager.NONE;
   private OxProject _project;
   public String _declaration;
   private OxClass _parentClass;

   public OxEntity(String name, OxClass parentClass, BaseComment comment, OxProject project) {
      this.name = name;
      _project = project;
      _comment = comment;
      _parentClass = parentClass;
   }

   public OxEntity(String name, OxClass parentClass, BaseComment comment, OxFile parentFile) {
      this.name = name;
      _project = parentFile.project();
      _comment = comment;
      _parentFile = parentFile;
      _parentClass = parentClass;
   }

   public OxProject project() {
      return _project;
   }

   public OxClass parentClass() {
      return _parentClass;
   }

   public String name() {
      return this.name;
   }

   public String referenceName() 
   {
      if (_parentClass == null) return name();
      return _parentClass.name() + "::" + name();
   }

   public int iconType() {
      return _iconType;
   }

   public String smallIcon() {
      return project().oxdoc.fileManager.smallIcon(_iconType);
   }

   public String largeIcon() {
      return project().oxdoc.fileManager.largeIcon(_iconType);
   }

   protected void setIconType(int iconType) {
      _iconType = iconType;
   }

   public String description() {
      return project().oxdoc.textProcessor.process(comment().description());
   }

   protected String parentFileUrl() {
      return (_parentFile == null) ? "" : _parentFile.url();
   }

   public BaseComment setComment(String comment) throws Exception {
      _comment.setText(comment);

      return _comment;
   }

   public String modifiers()
   {
      return "";
   }

   public BaseComment comment() {
      return _comment;
   }

   public String url() {
      return "";
   }

   public String displayName() {
      return name;
   }

   public String link() {
      if (url().length() == 0)
         return displayName();
      else

         return "<a href=\"" + url() + "\">" + displayName() + "</a>";
   }

   public String toString() {
      return "<OxEntity " + name() + ">";
   }

   public String declaration() {
      return _declaration;
   }

   public boolean isInternal()
   {
      return false;
   }

   public String sortKey() 
   {
      String sortKey = comment().sortKey();
      return (sortKey != null) ? sortKey : name();
   } 

}
