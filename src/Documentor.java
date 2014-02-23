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

import oxdoc.comments.BaseComment;
import oxdoc.entities.OxClass;
import oxdoc.entities.OxEntity;
import oxdoc.entities.OxEnum;
import oxdoc.entities.OxFile;
import oxdoc.html.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import static oxdoc.Utils.checkNotNull;

public class Documentor {
  private final OxProject project;
  private final LatexImageManager latexImageManager;
  private final FileManager fileManager;
  private final Config config;
  private final RenderContext renderContext;

  private ClassTree classTree;

  public Documentor(OxProject project, Config config, LatexImageManager latexImageManager, FileManager fileManager) {
    this.config = checkNotNull(config);
    this.project = checkNotNull(project);
    this.latexImageManager = checkNotNull(latexImageManager);
    this.fileManager = checkNotNull(fileManager);
    this.renderContext = new RenderContext(fileManager);
    constructTableSpecs();
  }

  private void constructTableSpecs() {
  }

  public void generateDocs() throws Exception {
    project.name = config.getProjectName();

    for (OxEntity entity : project.getFiles()) {
      OxFile file = (OxFile) entity;
      generateDoc(file, file.getUrl());
    }
    classTree = new ClassTree(project.getClasses());

    generateStartPage("default.html");
    generateIndex("index.html");
    generateHierarchy("hierarchy.html");

    writeCss();

    latexImageManager.makeLatexFiles();
  }

  private void generateStartPage(String fileName) throws Exception {
    String title = (project.name.length() == 0) ? "Project home" : (project.name + " project home");
    String sectionTitle = (project.name.length() == 0) ? "Files" : (project.name + " files");

    OutputFile output = new OutputFile(fileName, title, FileManager.PROJECT, config, fileManager);
    int iconType = FileManager.FILES;

    Header header = new Header(2, iconType, sectionTitle, renderContext);
    output.writeln(header);

    Table fileTable = new Table();
    fileTable.specs().cssClass = "table_of_contents";
    fileTable.specs().columnCssClasses.add("file");
    fileTable.specs().columnCssClasses.add("description");

    for (OxEntity entity : project.getFiles()) {
      OxFile file = (OxFile) entity;
      String[] row = {file.getSmallIcon() + project.getLinkToEntity(file), file.getDescription()};
      fileTable.addRow(row);
    }
    output.writeln(fileTable);

    output.close();
  }

  private void generateIndex(String fileName) throws Exception {
    OutputFile output = new OutputFile(fileName, "Index", FileManager.INDEX, config, fileManager);

    SymbolIndex index = new SymbolIndex(project, classTree, config);

    index.write(output);
    output.close();
  }

  private void generateHierarchy(String fileName) throws Exception {
    OutputFile output = new OutputFile(fileName, "Class hierarchy", FileManager.HIERARCHY, config, fileManager);

    ClassTreeHtml classTreeHtml = new ClassTreeHtml(project, classTree);
    classTreeHtml.writeHtml(output);

    output.close();
  }

  private void generateDoc(OxFile oxFile, String fileName) throws Exception {
    OutputFile output = new OutputFile(fileName, oxFile.getName(), oxFile.getIconType(), config, fileManager);
    try {
      output.writeln(oxFile.getComment());

      generateGlobalHeaderDocs(output, oxFile);

      for (OxEntity entity : oxFile.getClasses()) {
        generateClassHeaderDocs(output, (OxClass) entity);
      }

      generateClassDetailDocs(output, null, oxFile.getFunctionsAndVariables(), oxFile.getEnums());

      for (OxEntity entity : oxFile.getClasses()) {
        OxClass oxClass = (OxClass) entity;
        generateClassDetailDocs(output, oxClass, oxClass.getMethodsAndFields(), oxClass.getEnums());
      }

    } finally {
      output.close();
    }
  }

  private void removeInternals(List<OxEntity> entityList) {
    int k = 0;
    while (k < entityList.size())
      if (entityList.get(k).isInternal())
        entityList.remove(k);
      else
        k++;
  }

  private void formatInheritedMembers(DefinitionList list, ArrayList<OxEntity> inheritedMembers, String label) {
    String curLabel = "";
    String curItems = "";
    OxClass lastClass = null;
    for (OxEntity member : inheritedMembers) {
      if (member.getParentClass() != lastClass) {
        if (lastClass != null)
          list.addItem(curLabel, curItems);
        lastClass = member.getParentClass();
        curLabel = String.format("%s from %s:", label, project.getLinkToEntity(member.getParentClass()));
        curItems = "";
      }
      if (curItems.length() > 0)
        curItems += ", ";
      curItems += project.getLinkToEntity(member);
    }
    if (curLabel.length() > 0)
      list.addItem(curLabel, curItems);
  }

  // generate header docs. Entity should be either OxClass or OxFile type
  private void generateClassHeaderDocs(OutputFile output, OxClass oxclass) throws Exception {
    String sectionName = oxclass.getName();
    int iconType = FileManager.CLASS;

		/* Write anchor */
    output.writeln(new Anchor(sectionName));

		/* Add superclasses to section name */
    for (OxClass superClass : oxclass.getSuperClasses()) {
      sectionName += " : " + project.getLinkToEntity(superClass);
    }

		/* Write header */
    Header header = new Header(2, iconType, sectionName, renderContext);
    output.writeln(header);

		/* Print comment */
    if (oxclass.getComment() != null)
      output.writeln(oxclass.getComment());

		/* Construct a new table */
    Table table = new Table();
    table.specs().cssClass = "method_table";
    table.specs().columnCssClasses.add("declaration");
    table.specs().columnCssClasses.add("modifiers");
    table.specs().columnCssClasses.add("description");

		/* Determine which class members should be printed in the table */
    LinkedHashMap<String, ArrayList<OxEntity>> visMembers = new LinkedHashMap<String, ArrayList<OxEntity>>();
    visMembers.put("Private fields", oxclass.getPrivateFields());
    visMembers.put("Protected fields", oxclass.getProtectedFields());
    visMembers.put("Public fields", oxclass.getPublicFields());
    visMembers.put("Public methods", oxclass.getMethods());
    visMembers.put("Enumerations", oxclass.getEnums());

		/* Filter any members that are marked as internal */
    if (!config.isShowInternals())
      for (List<OxEntity> visMember : visMembers.values())
        removeInternals(visMember);

		/* Add sections to table */
    for (String caption : visMembers.keySet()) {
      ArrayList<OxEntity> entities = visMembers.get(caption);
      if (!entities.isEmpty()) {
        table.addHeaderRow(caption);
        for (OxEntity entity : entities) {
          table.addRow(entity.getSmallIcon() + entity.getLink(), entity.getModifiers(), entity.getDescription());
        }
      }
    }

    if (table.getRowCount() > 0)
      output.writeln(table);

		/* Write inherited members */
    LinkedHashMap<String, ArrayList<OxEntity>> inhMembers = new LinkedHashMap<String, ArrayList<OxEntity>>();
    inhMembers.put("Inherited methods", oxclass.getInheritedMethods());
    inhMembers.put("Inherited fields", oxclass.getInheritedFields());
    inhMembers.put("Inherited enumerations", oxclass.getInheritedEnums());

    if (!config.isShowInternals())
      for (List<OxEntity> inhMember : inhMembers.values())
        removeInternals(inhMember);

    for (String caption : inhMembers.keySet()) {
      if (inhMembers.get(caption).size() > 0) {
        DefinitionList dl = new DefinitionList("inherited");
        formatInheritedMembers(dl, inhMembers.get(caption), caption);
        output.writeln(dl);
      }
    }
  }

  private void generateGlobalHeaderDocs(OutputFile output, OxFile oxfile) throws Exception {
    String sectionName = "";
    int iconType = FileManager.GLOBAL;

    LinkedHashMap<String, ArrayList<OxEntity>> visMembers = new LinkedHashMap<String, ArrayList<OxEntity>>();
    visMembers.put("Variables", oxfile.getVariables());
    visMembers.put("Functions", oxfile.getFunctions());
    visMembers.put("Enumerations", oxfile.getEnums());

		/* Filter any members that are marked as internal */
    if (!config.isShowInternals())
      for (ArrayList<OxEntity> visMember : visMembers.values())
        removeInternals(visMember);

		/* Construct a new table */
    Table table = new Table();
    table.specs().cssClass = "method_table";
    table.specs().columnCssClasses.add("declaration");
    table.specs().columnCssClasses.add("modifiers");
    table.specs().columnCssClasses.add("description");

		/* Add sections to table */
    for (String caption : visMembers.keySet()) {
      List<OxEntity> entities = visMembers.get(caption);
      if (!entities.isEmpty()) {
        if (sectionName.length() > 0)
          sectionName += ", ";
        sectionName += caption.toLowerCase();

        table.addHeaderRow(caption);
        for (OxEntity entity : entities) {
          table.addRow(entity.getSmallIcon() + entity.getLink(), entity.getModifiers(), entity.getDescription());
        }
      }
    }

		/* Write if there is anything to write */
    if (table.getRowCount() > 0) {
      Header header = new Header(2, iconType, "Global " + sectionName, renderContext);
      output.writeln(new Anchor("global"));
      output.writeln(header);
      output.writeln(table);
    }
  }

  private void generateClassDetailDocs(OutputFile output, OxClass oxclass, ArrayList<OxEntity> memberList, ArrayList<OxEntity> enumList)
      throws Exception {
    String sectionName = (oxclass != null) ? oxclass.getName() : "Global ";
    String classPrefix = (oxclass != null) ? oxclass.getName() + "___" : "";
    int iconType = (oxclass != null) ? FileManager.CLASS : FileManager.GLOBAL;

    if (!config.isShowInternals())
      removeInternals(memberList);

    if (enumList.size() + memberList.size() == 0)
      return;

    Header header = new Header(2, iconType, sectionName, renderContext);
    output.writeln(header);

    generateEnumDocs(output, oxclass, enumList);

    int rowIndex = 0;
    for (OxEntity entity : memberList) {
      String anchorName = classPrefix + entity.getDisplayName();

      if (rowIndex != 0)
        output.writeln("\n<hr>");

      Header h3 = new Header(3, entity.getIconType(), entity.getName(), renderContext);
      output.writeln(new Anchor(anchorName));
      output.writeln(h3);

      if (entity.getDeclaration() != null)
        output.writeln("<span class=\"declaration\">" + entity.getDeclaration() + "</span>");

      output.writeln("<dl><dd>");
      output.writeln(entity.getComment());
      output.writeln("</dd></dl>");
      rowIndex++;
    }
  }

  private void generateEnumDocs(OutputFile output, OxClass oxclass, ArrayList<OxEntity> memberList) throws Exception {
    int count = 0;
    for (OxEntity entity : memberList) {
      OxEnum oxEnum = (OxEnum) entity;
      if ((!config.isShowInternals()) && (oxEnum.isInternal()))
        continue;
      count++;
    }
    if (count == 0)
      return;

    String sectionName = "Enumerations";
    String classPrefix = (oxclass != null) ? oxclass.getName() + "___" : "";

    Table table = new Table();
    table.specs().cssClass = "enum_table";
    table.specs().columnCssClasses.add("declaration");
    table.specs().columnCssClasses.add("description");
    table.specs().columnCssClasses.add("elements");

    table.addHeaderRow(sectionName);
    for (OxEntity oxEntity : memberList) {
      OxEnum oxEnum = (OxEnum) oxEntity;
      String anchorName = classPrefix + oxEnum.getName();

      if ((!config.isShowInternals()) && (oxEnum.isInternal()))
        continue;

      Anchor anchor = new Anchor(anchorName);
      BaseComment comment = oxEnum.getComment();
      String[] row = {
          anchor.toString() + oxEnum.getDisplayName(),
          comment.toString(),
          oxEnum.getElementString()};

      table.addRow(row);
    }

    if (table.getRowCount() > 1)
      output.writeln(table);
  }

  private void writeCss() throws IOException {
    if (config.isEnableIcons())
      fileManager.copyFromResourceIfNotExists("oxdoc.css");
    else
      fileManager.copyFromResourceIfNotExists("oxdoc-noicons.css");
    fileManager.copyFromResourceIfNotExists("print.css");
  }
}
