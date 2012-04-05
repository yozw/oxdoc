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

package oxdoc.comments;

import java.util.HashMap;

import oxdoc.OxProject;

public class BaseComment {
	final int SECTION_COMMENTS = 1, SECTION_REF = 2, SECTION_EXAMPLE = 3, SECTION_SEE = 4, SECTION_SORTKEY = 5;

	private String _text = "";
	private String _description = "";
	private String _sortKey = null;
	private CommentTextBlock _longdescription = null;
	private CommentTextBlock _comments = null;
	private CommentTextBlock _example = null;
	private CommentSeeAlsoList _see = null;
	private CommentList _ref = null;
	private CommentList _todo = null;
	private HashMap _sections = new HashMap();
	private HashMap _modifiers = new HashMap(); // modifiers have 0 parameters
	private HashMap _settings = new HashMap(); // settings have 1 parameter
	public OxProject project = null;

	public BaseComment(OxProject project) {
		this.project = project;
		_longdescription = new CommentTextBlock(project);
		_comments = new CommentTextBlock(project);
		_example = new CommentTextBlock(project);
		_see = new CommentSeeAlsoList(project);
		_ref = new CommentList(project);
		_todo = new CommentList(project);

		registerSection("comments", SECTION_COMMENTS);
		registerSection("comment", SECTION_COMMENTS);
		registerSection("ref", SECTION_REF);
		registerSection("example", SECTION_EXAMPLE);
		registerSection("examples", SECTION_EXAMPLE);
		registerSection("see", SECTION_SEE);
		registerSection("seealso", SECTION_SEE);
		registerSetting("sortkey", SECTION_SORTKEY);
	}

	protected void registerSection(String name, int SectionId) {
		_sections.put(name, new Integer(SectionId));
	}

	protected void registerSetting(String name, int SettingId) {
		_settings.put(name, new Integer(SettingId));
	}

	protected void registerModifier(String name, int ModifierId) {
		_modifiers.put(name, new Integer(ModifierId));
	}

	protected int getSectionId(String name) {
		return (Integer) _sections.get(name);
	}

	protected int getModifierId(String name) {
		return (Integer) _modifiers.get(name);
	}

	protected int getSettingId(String name) {
		return (Integer) _settings.get(name);
	}

	/**
	 * Add a piece of text to one of the sections. Do not access this method
	 * directly. It is used by SetText. Override this method to add more
	 * sections.
	 **/
	protected boolean addToSection(int SectionId, String text) {
		switch (SectionId) {
		case SECTION_COMMENTS:
			_comments.add(text);
			break;
		case SECTION_REF:
			_ref.add(text);
			break;
		case SECTION_EXAMPLE:
			_example.add(text);
			break;
		case SECTION_SEE:
			_see.add(text);
			break;
		default:
			return false;
		}
		return true;
	}

	protected boolean isSection(String name) {
		return _sections.containsKey(name);
	}

	protected boolean isModifier(String name) {
		return _modifiers.containsKey(name);
	}

	protected boolean isSetting(String name) {
		return _settings.containsKey(name);
	}

	protected boolean processModifier(int ModifierId) {
		return false;
	}

	protected boolean processSetting(int SettingId, String argument) {
		switch (SettingId) {
		case SECTION_SORTKEY:
			_sortKey = argument;
			return true;
		}

		return false;
	}

	private static String extractShortDescription(String text) {
		text = text + " ";

		int i = -1;
		while ((i = text.indexOf(".", i + 1)) != -1) {
			// short description ends with space followed by whitespace, cr, lf,
			// or tab
			if (i == text.length())
				return text;
			char nextChar = text.charAt(i + 1);
			if ((nextChar == ' ') || (nextChar == '\n') || (nextChar == '\r') || (nextChar == '\t'))
				return text.substring(0, i + 1);
		}

		return text;
	}

	/**
	 * Feeds an input comment block as a string and parses it. It interprets
	 * 
	 * @<section name> blocks and passes the contents to AddToSection.
	 **/
	public void setText(String text) throws Exception {
		if (!text.startsWith("/**") || !text.endsWith("**/"))
			return;
		text = text.substring(3, text.length() - 3).trim();
		_text = text;

		String[] sections = text.split("@");

		String description = "";
		int curSection = -1;

		for (int i = 0; i < sections.length; i++) {
			String textLine;

			if (i == 0)
				textLine = sections[0];
			else {
				String[] words = sections[i].split("[\t\n ]", 2);
				String commandName = words[0];
				textLine = (words.length > 1) ? words[1] : "";

				if (isModifier(commandName))
					processModifier(getModifierId(commandName));
				else if (isSetting(commandName)) {
					words = sections[i].split("[\t\n ]", 3);
					textLine = (words.length > 2) ? words[2] : "";
					processSetting(getSettingId(commandName), words[1]);
				} else if (isSection(commandName))
					curSection = getSectionId(commandName);
				else
					textLine = "@" + commandName + " " + textLine;
			}

			if (curSection == -1)
				description += textLine + " ";
			else
				addToSection(curSection, textLine);
		}

		_description = extractShortDescription(description);
		_longdescription.add(description);
	}

	/**
	 * Short description of the entity, i.e. the part before the first period
	 * (.)
	 **/
	public String description() {
		return project.oxdoc.textProcessor.process(_description);
	}

	/** Long description of the entity **/
	public CommentTextBlock longdescription() {
		return _longdescription;
	}

	/** Comments of the entity **/
	public CommentTextBlock comments() {
		return _comments;
	}

	/** Example block of the entity **/
	public CommentTextBlock example() {
		return _example;
	}

	/** The see also list of the entity **/
	public CommentSeeAlsoList see() {
		return _see;
	}

	/** The reference list of the entity **/
	public CommentList ref() {
		return _ref;
	}

	/** The to do list of the entity **/
	public CommentList todo() {
		return _todo;
	}

	/** The sort key of this entity; returns null if not set. **/
	public String sortKey() {
		return _sortKey;
	}

	/** Checks whether the comment is empty **/
	public boolean isEmpty() {
		return _text.trim().length() == 0;
	}
}
