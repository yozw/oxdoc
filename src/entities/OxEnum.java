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
public class OxEnum extends OxEntity {
   public String Declaration;
   public OxClass.Visibility _visibility;
   private String[] _elements;

   OxEnum(String name, String[] elements, OxClass oxclass, OxClass.Visibility visibility) {
      super(name, oxclass, new FieldComment(oxclass.parentFile().project()), oxclass.parentFile());
      setIconType(FileManager.ENUM);
      _elements = elements;
      _visibility = visibility;
   }

   public String url() {
      return parentFileUrl() + "#" + parentClass().name() + "___" + displayName();
   }

   public String declaration() {
      String decl = "";
      decl += " enum { ";
      for (int i = 0; i < _elements.length; i++) 
      {
          if (i != 0) decl += ", ";
          decl += _elements[i];
      }
      decl += " } [" + visibility() + "]";
      return decl;
   }

   public OxClass.Visibility visibility() 
   {
      return _visibility;
   }

   public String toString() {
      return "<OxEnum " + referenceName() + ">";
   }

   public boolean isInternal()
   {
      return ((FieldComment) comment()).hasInternalModifier() || (visibility() != OxClass.Visibility.Public);
   }

}
