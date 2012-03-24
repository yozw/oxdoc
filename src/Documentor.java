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
import java.io.*;
import java.text.*;


public class Documentor {
   private OxProject project = null;
   private OxDoc oxdoc = null;
   private ClassTree classTree = null;

   public Documentor(OxDoc oxdoc) {
      this.oxdoc = oxdoc;
      this.project = oxdoc.project;
   }

   public void generateDocs() throws Exception {
      project.name = oxdoc.config.ProjectName;

      ArrayList files = project.files();
      for (int i = 0; i < files.size(); i++) {
         OxFile file = (OxFile) files.get(i);
         generateDoc(file, file.url());
      }
      classTree = new ClassTree(oxdoc, project.classes());

      generateStartPage("default.html");
      generateIndex("index.html");
      generateHierarchy("hierarchy.html");

      writeCss();

      oxdoc.latexImageManager.makeLatexFiles();
   }

   private void generateStartPage(String fileName) throws Exception {
      String title = (project.name.length() == 0) ? "Project home" : (project.name + " project home");
      String sectionTitle = (project.name.length() == 0) ? "Files" : (project.name + " files");

      OutputFile output = new OutputFile(fileName, title, FileManager.PROJECT, oxdoc);
      int iconType = FileManager.FILES; 
      ArrayList files = project.files();
      output.writeln("<h2><span class=\"icon\">" + oxdoc.fileManager.largeIcon(iconType) + "</span><span class=\"text\">" + sectionTitle + "</span></h2>");
      output.writeln("<table class=\"table_of_contents\">");
      for (int i = 0; i < files.size(); i++) {
         OxFile file = (OxFile) files.get(i);
         output.writeln("<tr><td class=\"file\">" + file.smallIcon() + project.linkToEntity(file) + "</td>");
         output.writeln("<td class=\"description\">" + file.description() + "</td></tr>");
      }
      output.writeln("</table>");
      output.close();
   }

   private void generateIndex(String fileName) throws Exception {
      OutputFile output = new OutputFile(fileName, "Index", FileManager.INDEX, oxdoc);

      SymbolIndex index = new SymbolIndex(oxdoc, classTree);

      index.write(output);
      output.close();
   }

   private void generateHierarchy(String fileName) throws Exception {
      oxdoc.fileManager.copyFromResourceIfNotExists("icons/tree_v.png");
      oxdoc.fileManager.copyFromResourceIfNotExists("icons/tree_n.png");
      oxdoc.fileManager.copyFromResourceIfNotExists("icons/tree_l.png");
      OutputFile output = new OutputFile(fileName, "Class hierarchy", FileManager.HIERARCHY, oxdoc);
      output.writeln("<div class=\"tree\">");
      output.writeln(classTree.toHtmlList());
      output.writeln("</div>");
      output.close();
   }

   private void generateDoc(OxFile oxFile, String fileName) throws Exception {
      OutputFile output = new OutputFile(fileName, oxFile.name(), oxFile.iconType(), oxdoc);
      try {
         output.writeln(oxFile.comment());

         ArrayList classes = oxFile.classes();
         for (int i = 0; i < classes.size(); i++) {
            OxClass oxclass = (OxClass) classes.get(i);
            generateClassHeaderDocs(output, oxclass);
         }

         if (oxFile.functions().size() + oxFile.enums().size() > 0)
            generateClassHeaderDocs(output, oxFile);


         for (int i = 0; i < classes.size(); i++) {
            OxClass oxclass = (OxClass) classes.get(i);
            generateClassDetailDocs(output, oxclass, oxclass.members());
         }
         if (oxFile.functions().size() > 0)
            generateClassDetailDocs(output, null, oxFile.functions());
         if (oxFile.enums().size() > 0)
            generateEnumDocs(output, oxFile.enums());
            
            
      } finally {
         if (output != null)
            output.close();
      }
   }

   // generate header docs. Entity should be either OxClass or OxFile type
   private void generateClassHeaderDocs(OutputFile output, OxEntity containerEntity) throws Exception {
      OxClass oxclass = (containerEntity instanceof OxClass) ? (OxClass) containerEntity : null;
      OxFile  oxfile = (containerEntity instanceof OxFile) ? (OxFile) containerEntity : null;

      String sectionName = (oxclass != null) ? oxclass.name() : "Global";
      String classPrefix = (oxclass != null) ? oxclass.name() : "";
      int iconType = (containerEntity instanceof OxClass) ? FileManager.CLASS : FileManager.GLOBAL;

      String inheritedMethods = "";
      String inheritedFields = "";
      String inheritanceText = "";
      String Enums = "";


      if ((oxclass != null) && (oxclass.superClassName() != null)) {
         OxClass sclass = oxclass;

         while (true) {
            String superClassName = sclass.superClassName();
            if (superClassName == null)
               break;

            String link = project.linkToSymbol(superClassName);

            inheritanceText += " : " + link;

            OxEntity entity = project.getSymbol(superClassName);
            if ((entity == null) || !(entity instanceof OxClass))
               break;
            sclass = (OxClass) entity;

            ArrayList members = sclass.members();
			int inheritedMethodCount = 0, inheritedFieldCount = 0, EnumsCount  = 0;

            inheritedMethods += "<dt>Inherited methods from " + link + ":</dt><dd>\n";
            inheritedFields  += "<dt>Inherited fields from " + link + ":</dt><dd>\n";
            Enums += "<dt>Enumerations :</dt><dd>\n";
            for (int i = 0; i < members.size(); i++) {
			    OxEntity member = (OxEntity) members.get(i);
                if ((!oxdoc.config.ShowInternals) && member.isInternal())
                    continue;
				if (member instanceof OxMethod)
				{
                    OxMethod method = (OxMethod) member;

					if (inheritedMethodCount > 0)
                   		inheritedMethods += ", ";
					inheritedMethods += method.link();
					inheritedMethodCount++;
				}
				else
				if (member instanceof OxField)
				{
                    OxField field = (OxField) member;

					if (field.visibility() == OxClass.Visibility.Private)
						continue;

					if (inheritedFieldCount > 0)
                   		inheritedFields += ", ";
					inheritedFields += field.link();
					inheritedFieldCount++;
				}
                else
                if (member instanceof OxEnum) 
                    { 
                    Enums += member.declaration();
					EnumsCount++;
                    } 
				else throw new Exception("Class member has unexpected class: " + member);
             }
             inheritedMethods += "</dd>\n";
             inheritedFields += "</dd>\n";
             Enums += "</dd>\n";

			 if (inheritedFieldCount == 0) inheritedFields = "";
			 if (inheritedMethodCount == 0) inheritedMethods = "";
			 if (EnumsCount == 0) Enums = "";
         }
      }

      output.writeln("\n<!-- " + sectionName + " --><a name=\"" + sectionName+"\"> </a>"); /** Modified by CF to include anchor **/
      output.write("<h2><span class=\"icon\">" + oxdoc.fileManager.largeIcon(iconType) + "</span><span class=\"text\">" + sectionName + " " + inheritanceText + "</span>");
      output.writeln("</h2>");

      if (oxclass != null)
         output.writeln(oxclass.comment());

		 String[] visLabels;
         ArrayList[] visMembers;
         if (oxclass != null) 
         {
			visLabels = new String[] {"Private fields", "Protected fields", "Public fields", "Public methods", "Enumerations"};
			visMembers = new ArrayList[] {oxclass.getPrivateFields(), oxclass.getProtectedFields(), oxclass.getPublicFields(), 
	                         oxclass.getMethods(),oxclass.getEnums()};
         }
         else
         {
			visLabels = new String[] {"Functions", "Enumerations"};
            visMembers = new ArrayList[] {oxfile.functions(), oxfile.enums() };
		 }

         if (!oxdoc.config.ShowInternals)
		 {
             // filter methods and fields marked as internal
             for (int i = 0; i < visLabels.length; i++) 
             {
                 int k = 0;
                 while (k < visMembers[i].size())
                 {
                     if ( ((OxEntity) visMembers[i].get(k)).isInternal() )
                        visMembers[i].remove(k);
                     else
                        k++;
                } 
             }
		 }

   int totalMembers = 0;
   for (int i = 0; i < visLabels.length; i++) 
      totalMembers += visMembers[i].size();

   if (totalMembers > 0) {
     
         output.writeln("\n<!-- Members of " + sectionName + " -->");
         output.writeln("<table class=\"method_table\">");

         for (int k = 0; k < visLabels.length; k++)		 
         {
             if (visMembers[k].size() == 0) continue;
		     output.writeln("<tr><td colspan=\"2\" class=\"header\" valign=\"top\">" + visLabels[k] + "</td></tr>");
		     for (int i = 0; i < visMembers[k].size(); i++) {
		        OxEntity entity = (OxEntity) visMembers[k].get(i);

		        output.writeln("<tr><td class=\"declaration\" valign=\"top\">");
		        output.writeln(entity.smallIcon() + entity.link());
		        output.writeln("</td><td class=\"description\" valign=\"top\">");
		        output.write(entity.description());
		        output.writeln("</td></tr>");
		     }
         }
	     output.writeln("</table>");
      }

      if (inheritedMethods.length() > 0)
         output.writeln("<dl class=\"inherited_methods\">" + inheritedMethods + "</dl>\n");
      if (inheritedFields.length() > 0)
         output.writeln("<dl class=\"inherited_fields\">" + inheritedFields + "</dl>\n");
      if (Enums.length() > 0)
         output.writeln("<dl class=\"inherited_fields\">" + Enums + "</dl>\n");
   }

   private void generateClassDetailDocs(OutputFile output, OxClass oxclass, ArrayList memberList) throws Exception {
      String sectionName = (oxclass != null) ? oxclass.name() : "Global functions";
      String classPrefix = (oxclass != null) ? oxclass.name() + "___" : "";
      int iconType = (oxclass != null) ? FileManager.CLASS : FileManager.GLOBAL;

      output.writeln("\n<!-- Details for " + sectionName + " -->");
      output.writeln("<h2><span class=\"icon\">" + oxdoc.fileManager.largeIcon(iconType) + "</span><span class=\"text\">" + sectionName + " details</span></h2>");
      int count = 0;
      for (int i = 0; i < memberList.size(); i++) {
         OxEntity entity = (OxEntity) memberList.get(i);
         String anchorName = classPrefix + entity.displayName();

	     if ((!oxdoc.config.ShowInternals) && (entity.isInternal())) 
            continue;

         if (count != 0)
            output.writeln("\n<hr>");
         count++;

         output.writeln("\n<!-- Entity " + entity.displayName() + " -->");

         Object[] args = { anchorName, entity.displayName(), entity.largeIcon() };
         output.writeln(MessageFormat.format("<a name=\"{0}\"></a><h3><span class=\"icon\">{2}</span><span class=\"text\">{1}</span></h3>", args));

		 if (entity.declaration() != null)
             output.writeln("<span class=\"declaration\">" + entity.declaration() + "</span>");

         output.writeln(entity.comment());
      }
   }

   private void generateEnumDocs(OutputFile output, ArrayList memberList) throws Exception {
      String sectionName = "Enumerations";
      String classPrefix = "";
      int iconType = FileManager.ENUM;

      output.writeln("\n<!-- " + sectionName + " -->");
      output.writeln("<h2><span class=\"icon\">" + oxdoc.fileManager.largeIcon(iconType) + "</span><span class=\"text\">" + sectionName + " details</span></h2>");
      int count = 0;
      for (int i = 0; i < memberList.size(); i++) {
         OxEntity entity = (OxEntity) memberList.get(i);
         String anchorName = classPrefix + entity.displayName();

	     if ((!oxdoc.config.ShowInternals) && (entity.isInternal())) 
            continue;

         if (count != 0)
            output.writeln("\n<hr>");
         count++;

         output.writeln("\n<!-- Enum " + entity.displayName() + " -->");

         Object[] args = { anchorName, entity.displayName(), entity.largeIcon() };
         output.writeln(MessageFormat.format("<a name=\"{0}\"></a><h3><span class=\"icon\">{2}</span><span class=\"text\">{1}</span></h3>", args));

		 if (entity.declaration() != null)
             output.writeln("<span class=\"declaration\">" + entity.declaration() + "</span>");

         output.writeln(entity.comment());
      }
   }



   private void writeCss() throws IOException {
      oxdoc.fileManager.copyFromResourceIfNotExists("oxdoc.css");
   }
}
