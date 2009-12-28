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
public class OxField extends OxEntity {
   public String Declaration;
   public OxClass.Visibility _visibility;
   public boolean Static = false;
   public boolean Constant = false;
   private OxClass _class = null;

   OxField(String name, OxClass oxclass, OxClass.Visibility visibility) {
      super(name, new FieldComment(oxclass.parentFile().project()), oxclass.parentFile());
//      setIconType(FileManager.FIELD);
      _visibility = visibility;
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

   public String declaration() {
      String decl = "";
      if (Static) decl += "static ";
      if (Constant) decl += "const ";
      decl += " decl " + name();
      decl += " [" + visibility() + "]";
      return decl;
   }

   public OxClass.Visibility visibility() 
   {
      return _visibility;
   }

   public String toString() {
      return "<OxField " + name() + ">";
   }

   public boolean isInternal()
   {
      return ((FieldComment) comment()).hasInternalModifier();
   }

}
