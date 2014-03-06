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

import oxdoc.comments.BaseComment;
import oxdoc.entities.*;
import oxdoc.html.*;
import oxdoc.util.FileUtils;
import oxdoc.util.Stopwatch;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static oxdoc.entities.OxClass.Visibility;
import static oxdoc.util.Utils.checkNotNull;

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
  }

  public void generateDocs() throws Exception {
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
    String title = (project.getName().length() == 0) ? "Project home" : (project.getName() + " project home");
    String sectionTitle = (project.getName().length() == 0) ? "Files" : (project.getName() + " files");

    OutputFile output = new OutputFile(fileName, title, Icon.PROJECT, config, fileManager);

    Header header = new Header(2, Icon.FILES, sectionTitle, renderContext);
    output.writeln(header);

    Table fileTable = new Table();
    fileTable.specs().cssClass = "table_of_contents";
    fileTable.specs().columnCssClasses.add("file");
    fileTable.specs().columnCssClasses.add("description");

    for (OxFile file : project.getFiles()) {
      String smallIconHtml = fileManager.getSmallIconHtml(file.getIcon());
      fileTable.addRow(smallIconHtml + project.getLinkToEntity(file), file.getDescription());
    }
    output.writeln(fileTable);

    output.close();
  }

  private void generateIndex(String fileName) throws Exception {
    OutputFile output = new OutputFile(fileName, "Index", Icon.INDEX, config, fileManager);
    SymbolIndex index = new SymbolIndex(project, classTree, config);
    index.write(output, fileManager);
    output.close();
  }

  private void generateHierarchy(String fileName) throws Exception {
    OutputFile output = new OutputFile(fileName, "Class hierarchy", Icon.HIERARCHY, config, fileManager);

    ClassTreeHtml classTreeHtml = new ClassTreeHtml(project, classTree);
    classTreeHtml.writeHtml(output);

    output.close();
  }

  private void generateDoc(OxFile oxFile, String fileName) throws Exception {
    OutputFile output = new OutputFile(fileName, oxFile.getName(), oxFile.getIcon(), config, fileManager);
    try {
      output.writeln(oxFile.getComment());

      generateGlobalHeaderDocs(output, oxFile);

      Stopwatch stopwatch = new Stopwatch();
      for (OxEntity entity : oxFile.getClasses()) {
        generateClassHeaderDocs(output, (OxClass) entity);
      }
      System.out.println("Generated header docs in  " + stopwatch.elapsedMsec());

      generateClassDetailDocs(output, null, oxFile.getFunctionsAndVariables(), oxFile.getEnums());

      for (OxEntity entity : oxFile.getClasses()) {
        OxClass oxClass = (OxClass) entity;
        generateClassDetailDocs(output, oxClass, oxClass.getMethodsAndFields(), oxClass.getEnums());
      }
    } finally {
      output.close();
    }
  }

  // generate header docs. Entity should be either OxClass or OxFile type
  private void generateClassHeaderDocs(OutputFile output, OxClass oxClass) throws Exception {

		/* Write anchor */
    output.writeln(new Anchor(oxClass.getName()));

    System.out.println("Writing class headers for " + oxClass);

		/* Add superclasses to section name */
    StringBuilder sectionName = new StringBuilder();
    sectionName.append(oxClass.getName());
    for (OxClass superClass : oxClass.getSuperClasses()) {
      sectionName.append(" : ");
      sectionName.append(project.getLinkToEntity(superClass));
    }

		/* Write header */
    Header header = new Header(2, Icon.CLASS, sectionName.toString(), renderContext);
    output.writeln(header);

		/* Print comment */
    if (oxClass.getComment() != null) {
      output.writeln(oxClass.getComment());
    }

		/* Construct a new table */
    Table table = new Table();
    table.specs().cssClass = "method_table";
    table.specs().columnCssClasses.add("declaration");
    table.specs().columnCssClasses.add("modifiers");
    table.specs().columnCssClasses.add("description");

		/* Determine which class members should be printed in the table */
    LinkedHashMap<String, OxEntityList<? extends OxEntity>> visMembers =
        new LinkedHashMap<String, OxEntityList<? extends OxEntity>>();
    visMembers.put("Private fields", oxClass.getFields(Visibility.Private));
    visMembers.put("Protected fields", oxClass.getFields(Visibility.Protected));
    visMembers.put("Public fields", oxClass.getFields(Visibility.Public));
    visMembers.put("Public methods", oxClass.getMethods());
    visMembers.put("Enumerations", oxClass.getEnums());

		/* Add sections to table */
    for (String caption : visMembers.keySet()) {
      OxEntityList<? extends OxEntity> entities = visMembers.get(caption);
      if (!config.isShowInternals()) {
        entities = entities.getNonInternal();
      }

      if (!entities.isEmpty()) {
        table.addHeaderRow(caption);
        for (OxEntity entity : entities) {
          String smallIconHtml = fileManager.getSmallIconHtml(entity.getIcon());
          table.addRow(smallIconHtml + entity.getLink(), entity.getModifiers(), entity.getDescription());
        }
      }
    }

    if (table.getRowCount() > 0) {
      output.writeln(table);
    }

		/* Write inherited members */
    LinkedHashMap<String, OxEntityList<? extends OxEntity>> inhMembers =
        new LinkedHashMap<String, OxEntityList<? extends OxEntity>>();
    inhMembers.put("Inherited methods", oxClass.getInheritedMethods());
    inhMembers.put("Inherited fields", oxClass.getInheritedFields());
    inhMembers.put("Inherited enumerations", oxClass.getInheritedEnums());

    for (String caption : inhMembers.keySet()) {
      OxEntityList<? extends OxEntity> members = inhMembers.get(caption);
      if (!config.isShowInternals()) {
        members = members.getNonInternal();
      }

      if (members.size() > 0) {
        DefinitionList dl = new DefinitionList("inherited");
        formatInheritedMembers(dl, oxClass, members, caption);
        output.writeln(dl);
      }
    }
  }

  private void formatInheritedMembers(DefinitionList list, OxClass oxClass, OxEntityList<? extends OxEntity> inheritedMembers, String label) {
    Map<OxClass, OxEntityList<OxEntity>> entityMap = new HashMap<OxClass, OxEntityList<OxEntity>>();
    for (OxEntity member : inheritedMembers) {
      OxClass parentClass = member.getParentClass();
      if (!entityMap.containsKey(parentClass)) {
        entityMap.put(parentClass, new OxEntityList<OxEntity>());
      }
      entityMap.get(parentClass).add(member);
    }

    for (OxClass parentClass : oxClass.getSuperClasses()) {
      OxEntityList<OxEntity> members = entityMap.get(parentClass);
      if (members == null) {
        continue;
      }
      String classLabel = String.format("%s from %s:", label, project.getLinkToEntity(parentClass));
      StringBuilder classItems = new StringBuilder();
      for (OxEntity member : members) {
        if (classItems.length() > 0) {
          classItems.append(", ");
        }
        classItems.append(project.getLinkToEntity(member));
      }
      list.addItem(classLabel, classItems.toString());
    }
  }

  private void generateGlobalHeaderDocs(OutputFile output, OxFile file) throws Exception {
    StringBuilder sectionName = new StringBuilder();

    LinkedHashMap<String, OxEntityList<? extends OxEntity>> visMembers =
        new LinkedHashMap<String, OxEntityList<? extends OxEntity>>();
    visMembers.put("Variables", file.getVariables());
    visMembers.put("Functions", file.getFunctions());
    visMembers.put("Enumerations", file.getEnums());

		/* Construct a new table */
    Table table = new Table();
    table.specs().cssClass = "method_table";
    table.specs().columnCssClasses.add("declaration");
    table.specs().columnCssClasses.add("modifiers");
    table.specs().columnCssClasses.add("description");

		/* Add sections to table */
    for (String caption : visMembers.keySet()) {
      OxEntityList<? extends OxEntity> entities = visMembers.get(caption);
      if (!config.isShowInternals()) {
        entities = entities.getNonInternal();
      }

      if (!entities.isEmpty()) {
        if (sectionName.length() > 0) {
          sectionName.append(", ");
        }
        sectionName.append(caption.toLowerCase());

        table.addHeaderRow(caption);
        for (OxEntity entity : entities) {
          String smallIconHtml = fileManager.getSmallIconHtml(entity.getIcon());
          table.addRow(smallIconHtml + entity.getLink(), entity.getModifiers(), entity.getDescription());
        }
      }
    }

		/* Write if there is anything to write */
    if (table.getRowCount() > 0) {
      Header header = new Header(2, Icon.GLOBAL, "Global " + sectionName.toString(), renderContext);
      output.writeln(new Anchor("global"));
      output.writeln(header);
      output.writeln(table);
    }
  }

  private void generateClassDetailDocs(OutputFile output, OxClass oxClass, OxEntityList<OxEntity> members, OxEntityList<OxEnum> enums)
      throws Exception {
    String sectionName = (oxClass != null) ? oxClass.getName() : "Global ";
    String classPrefix = (oxClass != null) ? oxClass.getName() + "___" : "";
    Icon icon = (oxClass != null) ? Icon.CLASS : Icon.GLOBAL;

    if (!config.isShowInternals()) {
      members = members.getNonInternal();
    }

    if (enums.size() + members.size() == 0) {
      return;
    }

    Header header = new Header(2, icon, sectionName, renderContext);
    output.writeln(header);

    generateEnumDocs(output, oxClass, enums);

    int rowIndex = 0;
    for (OxEntity entity : members) {
      String anchorName = classPrefix + entity.getDisplayName();

      if (rowIndex != 0) {
        output.writeln("\n<hr>");
      }

      Header h3 = new Header(3, entity.getIcon(), entity.getName(), renderContext);
      output.writeln(new Anchor(anchorName));
      output.writeln(h3);

      if (entity.getDeclaration() != null) {
        output.writeln("<span class=\"declaration\">" + entity.getDeclaration() + "</span>");
      }

      output.writeln("<dl><dd>");
      output.writeln(entity.getComment());
      output.writeln("</dd></dl>");
      rowIndex++;
    }
  }

  private void generateEnumDocs(OutputFile output, OxClass oxClass, OxEntityList<OxEnum> enums) throws Exception {
    if (!config.isShowInternals()) {
      enums = enums.getNonInternal();
    }

    if (enums.isEmpty()) {
      return;
    }

    String sectionName = "Enumerations";
    String classPrefix = (oxClass != null) ? oxClass.getName() + "___" : "";

    Table table = new Table();
    table.specs().cssClass = "enum_table";
    table.specs().columnCssClasses.add("declaration");
    table.specs().columnCssClasses.add("description");
    table.specs().columnCssClasses.add("elements");

    table.addHeaderRow(sectionName);
    for (OxEnum oxEnum : enums) {
      String anchorName = classPrefix + oxEnum.getName();
      Anchor anchor = new Anchor(anchorName);
      BaseComment comment = oxEnum.getComment();
      table.addRow(anchor.toString() + oxEnum.getDisplayName(),
          comment.toString(),
          oxEnum.getElementString());
    }

    if (table.getRowCount() > 1) {
      output.writeln(table);
    }
  }

  private void writeCss() throws IOException {
    String outputCssFile = FileUtils.joinPath(config.getCssPath(), config.getCssFilename());
    String templateCss = config.isEnableIcons() ? "oxdoc.css" : "oxdoc-noicons.css";
    fileManager.copyFromResourceIfNotExists(outputCssFile, templateCss);

    String outputPrintCssFile = FileUtils.joinPath(config.getCssPath(), "print.css");
    fileManager.copyFromResourceIfNotExists(outputPrintCssFile, "print.css");
  }
}
