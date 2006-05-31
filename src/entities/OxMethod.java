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
public class OxMethod extends OxFunction {
   public String Declaration;
   private OxClass _class = null;

   OxMethod(String name, OxClass oxclass) {
      super(name, oxclass.parentFile());
      setIconType(FileManager.METHOD);
      _class = oxclass;
   }

   public OxClass parentClass() {
      return _class;
   }

   public String url() {
      return parentFileUrl() + "#" + _class.name() + "___" + displayName();
   }

   public String displayName() {
      String[] pts = name().split("::");

      return pts[1];
   }

   public BaseComment comment() {
      BaseComment _comment = super.comment();

      if (_comment.isEmpty() && superMethod() != null)
         return superMethod().comment();

      return _comment;
   }

   public OxMethod superMethod() {
      if (parentClass() == null || parentClass().superClass() == null)
         return null;

      return (OxMethod) parentClass().superClass().methodByName(this.displayName());
   }

   public String toString() {
      return "<OxMethod " + name() + ">";
   }
}