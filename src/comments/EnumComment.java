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

package oxdoc.comments;

import java.text.*;
import oxdoc.*;


public class EnumComment extends FieldComment {
   private String _alternativeName = "";

   final int SECTION_NAME = 500;

   public EnumComment(OxProject project) {
      super(project);

      registerSection("name", SECTION_NAME);
   }

   protected boolean addToSection(int SectionId, String text) {
      if (super.addToSection(SectionId, text)) 
         return true;

      switch (SectionId)
      {
         case SECTION_NAME:  _alternativeName += text; break;
         default:
            return false;
      }
      return true;
   }

   public String alternativeName()
   {
      return _alternativeName;
   }

}
