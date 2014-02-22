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

import oxdoc.entities.OxClass;
import oxdoc.entities.OxEntity;
import oxdoc.entities.OxEnum;
import oxdoc.entities.OxFile;
import oxdoc.html.Anchor;
import oxdoc.html.DefinitionList;
import oxdoc.html.Header;
import oxdoc.html.Table;

import java.io.IOException;
import java.util.ArrayList;

public class Documentor {
	private OxProject project = null;
	private OxDoc oxdoc = null;
	private ClassTree classTree = null;

	public Documentor(OxDoc oxdoc) {
		this.oxdoc = oxdoc;
		project = oxdoc.project;
		constructTableSpecs();
	}

	private void constructTableSpecs() {
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

		Header header = new Header(oxdoc, 2, iconType, sectionTitle);
		output.writeln(header);

		Table fileTable = new Table(oxdoc);
		fileTable.specs().cssClass = "table_of_contents";
		fileTable.specs().columnCssClasses.add("file");
		fileTable.specs().columnCssClasses.add("description");

		for (int i = 0; i < files.size(); i++) {
			OxFile file = (OxFile) files.get(i);
			String[] row = { file.smallIcon() + project.linkToEntity(file), file.description() };
			fileTable.addRow(row);
		}
		output.writeln(fileTable);

		output.close();
	}

	private void generateIndex(String fileName) throws Exception {
		OutputFile output = new OutputFile(fileName, "Index", FileManager.INDEX, oxdoc);

		SymbolIndex index = new SymbolIndex(oxdoc, classTree);

		index.write(output);
		output.close();
	}

	private void generateHierarchy(String fileName) throws Exception {
		OutputFile output = new OutputFile(fileName, "Class hierarchy", FileManager.HIERARCHY, oxdoc);

		ClassTreeHtml classTreeHtml = new ClassTreeHtml(oxdoc, classTree);
		classTreeHtml.writeHtml(output);

		output.close();
	}

	private void generateDoc(OxFile oxFile, String fileName) throws Exception {
		OutputFile output = new OutputFile(fileName, oxFile.name(), oxFile.iconType(), oxdoc);
		try {
			output.writeln(oxFile.comment());

			generateGlobalHeaderDocs(output, oxFile);

			ArrayList classes = oxFile.classes();
			for (int i = 0; i < classes.size(); i++) {
				OxClass oxClass = (OxClass) classes.get(i);
				generateClassHeaderDocs(output, oxClass);
			}

			generateClassDetailDocs(output, null, oxFile.functionsAndVariables(), oxFile.enums());

			for (int i = 0; i < classes.size(); i++) {
				OxClass oxclass = (OxClass) classes.get(i);
				generateClassDetailDocs(output, oxclass, oxclass.getMethodsAndFields(), oxclass.getEnums());
			}

		} finally {
			if (output != null)
				output.close();
		}
	}

	private void removeInternals(ArrayList entityList) {
		int k = 0;
		while (k < entityList.size())
			if (((OxEntity) entityList.get(k)).isInternal())
				entityList.remove(k);
			else
				k++;
	}

	private void formatInheritedMembers(DefinitionList list, ArrayList inheritedMembers, String label) {
		String curLabel = "";
		String curItems = "";
		OxClass lastClass = null;
		for (int i = 0; i < inheritedMembers.size(); i++) {
			OxEntity member = (OxEntity) inheritedMembers.get(i);
			if (member.parentClass() != lastClass) {
				if (lastClass != null)
					list.addItem(curLabel, curItems);
				lastClass = member.parentClass();
				curLabel = String.format("%s from %s:", label, project.linkToEntity(member.parentClass()));
				curItems = "";
			}
			if (curItems.length() > 0)
				curItems += ", ";
			curItems += project.linkToEntity(member);
		}
		if (curLabel.length() > 0)
			list.addItem(curLabel, curItems);
	}

	// generate header docs. Entity should be either OxClass or OxFile type
	private void generateClassHeaderDocs(OutputFile output, OxClass oxclass) throws Exception {
		String sectionName = oxclass.name();
		int iconType = FileManager.CLASS;

		/* Write anchor */
		output.writeln(new Anchor(oxdoc, sectionName));

		/* Add superclasses to section name */
		ArrayList superClasses = oxclass.getSuperClasses();
		for (int i = 0; i < superClasses.size(); i++) {
			OxClass superClass = (OxClass) superClasses.get(i);
			sectionName += " : " + project.linkToEntity(superClass);
		}

		/* Write header */
		Header header = new Header(oxdoc, 2, iconType, sectionName);
		output.writeln(header);

		/* Print comment */
		if (oxclass != null)
			output.writeln(oxclass.comment());

		/* Construct a new table */
		Table table = new Table(oxdoc);
		table.specs().cssClass = "method_table";
		table.specs().columnCssClasses.add("declaration");
		table.specs().columnCssClasses.add("modifiers");
		table.specs().columnCssClasses.add("description");

		/* Determine which class members should be printed in the table */
		String[] visLabels = { "Private fields", "Protected fields", "Public fields", "Public methods", "Enumerations" };
		ArrayList[] visMembers = { oxclass.getPrivateFields(), oxclass.getProtectedFields(), oxclass.getPublicFields(),
				oxclass.getMethods(), oxclass.getEnums() };

		/* Filter any members that are marked as internal */
		if (!oxdoc.config.ShowInternals)
			for (int i = 0; i < visMembers.length; i++)
				removeInternals(visMembers[i]);

		/* Add sections to table */
		for (int k = 0; k < visLabels.length; k++) {
			if (visMembers[k].size() == 0)
				continue;

			table.addHeaderRow(visLabels[k]);
			for (int i = 0; i < visMembers[k].size(); i++) {
				OxEntity entity = (OxEntity) visMembers[k].get(i);

				String[] row = { entity.smallIcon() + entity.link(), entity.modifiers(), entity.description() };

				table.addRow(row);
			}
		}

		if (table.getRowCount() > 0)
			output.writeln(table);

		/* Write inherited members */
		String[] inhLabels = { "Inherited methods", "Inherited fields", "Inherited enumerations" };
		ArrayList[] inhMembers = { oxclass.getInheritedMethods(), oxclass.getInheritedFields(),
				oxclass.getInheritedEnums() };

		if (!oxdoc.config.ShowInternals)
			for (int i = 0; i < inhMembers.length; i++)
				removeInternals(inhMembers[i]);

		for (int i = 0; i < inhMembers.length; i++) {
			if (inhMembers[i].size() == 0)
				continue;
			DefinitionList dl = new DefinitionList(oxdoc, "inherited");
			formatInheritedMembers(dl, inhMembers[i], inhLabels[i]);
			output.writeln(dl);
		}
	}

	// generate header docs. Entity should be either OxClass or OxFile type
	private void generateGlobalHeaderDocs(OutputFile output, OxFile containerEntity) throws Exception {
		OxFile oxfile = (containerEntity instanceof OxFile) ? (OxFile) containerEntity : null;

		String sectionName = "";
		int iconType = FileManager.GLOBAL;

		String[] visLabels = { "Variables", "Functions", "Enumerations" };
		ArrayList[] visMembers = { oxfile.variables(), oxfile.functions(), oxfile.enums() };

		/* Filter any members that are marked as internal */
		if (!oxdoc.config.ShowInternals)
			for (int i = 0; i < visMembers.length; i++)
				removeInternals(visMembers[i]);

		/* Construct a new table */
		Table table = new Table(oxdoc);
		table.specs().cssClass = "method_table";
		table.specs().columnCssClasses.add("declaration");
		table.specs().columnCssClasses.add("modifiers");
		table.specs().columnCssClasses.add("description");

		/* Add sections to table */
		for (int k = 0; k < visLabels.length; k++) {

			if (visMembers[k].size() == 0)
				continue;

			if (sectionName.length() > 0)
				sectionName += ", ";
			sectionName += visLabels[k].toLowerCase();

			table.addHeaderRow(visLabels[k]);
			for (int i = 0; i < visMembers[k].size(); i++) {
				OxEntity entity = (OxEntity) visMembers[k].get(i);

				String[] row = { entity.smallIcon() + entity.link(), entity.modifiers(), entity.description() };

				table.addRow(row);
			}
		}

		/* Write if there is anything to write */
		if (table.getRowCount() > 0) {
			Header header = new Header(oxdoc, 2, iconType, "Global " + sectionName);
			output.writeln(new Anchor(oxdoc, "global"));
			output.writeln(header);
			output.writeln(table);
		}
	}

	private void generateClassDetailDocs(OutputFile output, OxClass oxclass, ArrayList memberList, ArrayList enumList)
			throws Exception {
		String sectionName = (oxclass != null) ? oxclass.name() : "Global ";
		String classPrefix = (oxclass != null) ? oxclass.name() + "___" : "";
		int iconType = (oxclass != null) ? FileManager.CLASS : FileManager.GLOBAL;

		if (!oxdoc.config.ShowInternals)
			removeInternals(memberList);

		if (enumList.size() + memberList.size() == 0)
			return;

		Header header = new Header(oxdoc, 2, iconType, sectionName);
		output.writeln(header);

		generateEnumDocs(output, oxclass, enumList);

		for (int i = 0; i < memberList.size(); i++) {
			OxEntity entity = (OxEntity) memberList.get(i);
			String anchorName = classPrefix + entity.displayName();

			if (i != 0)
				output.writeln("\n<hr>");

			Header h3 = new Header(oxdoc, 3, entity.iconType(), entity.name());
			output.writeln(new Anchor(oxdoc, anchorName));
			output.writeln(h3);

			if (entity.declaration() != null)
				output.writeln("<span class=\"declaration\">" + entity.declaration() + "</span>");

			output.writeln("<dl><dd>");
			output.writeln(entity.comment());
			output.writeln("</dd></dl>");
		}
	}

	private void generateEnumDocs(OutputFile output, OxClass oxclass, ArrayList memberList) throws Exception {
		int count = 0;
		for (int i = 0; i < memberList.size(); i++) {
			OxEnum entity = (OxEnum) memberList.get(i);
			if ((!oxdoc.config.ShowInternals) && (entity.isInternal()))
				continue;
			count++;
		}
		if (count == 0)
			return;

		String sectionName = "Enumerations";
		String classPrefix = (oxclass != null) ? oxclass.name() + "___" : "";

		Table table = new Table(oxdoc);
		table.specs().cssClass = "enum_table";
		table.specs().columnCssClasses.add("declaration");
		table.specs().columnCssClasses.add("description");
		table.specs().columnCssClasses.add("elements");

		table.addHeaderRow(sectionName);
		for (int i = 0; i < memberList.size(); i++) {
			OxEnum entity = (OxEnum) memberList.get(i);
			String anchorName = classPrefix + entity.name();

			if ((!oxdoc.config.ShowInternals) && (entity.isInternal()))
				continue;

			String[] row = { new Anchor(oxdoc, anchorName).toString() + entity.displayName(),
					entity.comment().toString(), entity.elementString() };

			table.addRow(row);
		}

		if (table.getRowCount() > 1)
			output.writeln(table);
	}

	private void writeCss() throws IOException {
		if (oxdoc.config.EnableIcons)
			oxdoc.fileManager.copyFromResourceIfNotExists("oxdoc.css");
		else
			oxdoc.fileManager.copyFromResourceIfNotExists("oxdoc-noicons.css");
		oxdoc.fileManager.copyFromResourceIfNotExists("print.css");
	}
}
