/**

 oxdoc (c) Copyright 2005-2023 by Y. Zwols

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

import oxdoc.OxProject;

import java.util.HashMap;

public class BaseComment {
  private final static int SECTION_COMMENTS = 1;
  private final static int SECTION_REF = 2;
  private final static int SECTION_EXAMPLE = 3;
  private final static int SECTION_SEE = 4;
  private final static int SECTION_SORTKEY = 5;

  private String text = "";
  private String description = "";
  private String sortKey = null;
  private final CommentTextBlock longDescription;
  private final CommentTextBlock comments;
  private final CommentTextBlock example;
  private final CommentSeeAlsoList see;
  private final CommentList ref;
  private final CommentList toDo;
  private final HashMap<String, Integer> sections = new HashMap<String, Integer>();
  private final HashMap<String, Integer> modifiers = new HashMap<String, Integer>(); // modifiers have 0 parameters
  private final HashMap<String, Integer> settings = new HashMap<String, Integer>(); // settings have 1 parameter
  private final OxProject project;

  public BaseComment(OxProject project) {
    this.project = project;
    longDescription = new CommentTextBlock(project);
    comments = new CommentTextBlock(project);
    example = new CommentTextBlock(project);
    see = new CommentSeeAlsoList(project);
    ref = new CommentList(project);
    toDo = new CommentList(project);

    registerSection("comments", SECTION_COMMENTS);
    registerSection("comment", SECTION_COMMENTS);
    registerSection("ref", SECTION_REF);
    registerSection("example", SECTION_EXAMPLE);
    registerSection("examples", SECTION_EXAMPLE);
    registerSection("see", SECTION_SEE);
    registerSection("seealso", SECTION_SEE);
    registerSetting("sortkey", SECTION_SORTKEY);
  }

  protected void registerSection(String name, int sectionId) {
    sections.put(name, sectionId);
  }

  protected void registerSetting(String name, int settingId) {
    settings.put(name, settingId);
  }

  protected void registerModifier(String name, int modifierId) {
    modifiers.put(name, modifierId);
  }

  protected int getSectionId(String name) {
    return sections.get(name);
  }

  protected int getModifierId(String name) {
    return modifiers.get(name);
  }

  protected int getSettingId(String name) {
    return settings.get(name);
  }

  /**
   * Add a piece of text to one of the sections. Do not access this method
   * directly. It is used by SetText. Override this method to add more
   * sections.
   */
  protected boolean addToSection(int sectionId, String text) {
    switch (sectionId) {
      case SECTION_COMMENTS:
        comments.add(text);
        break;
      case SECTION_REF:
        ref.add(text);
        break;
      case SECTION_EXAMPLE:
        example.add(text);
        break;
      case SECTION_SEE:
        see.add(text);
        break;
      default:
        return false;
    }
    return true;
  }

  protected boolean isSection(String name) {
    return sections.containsKey(name);
  }

  protected boolean isModifier(String name) {
    return modifiers.containsKey(name);
  }

  protected boolean isSetting(String name) {
    return settings.containsKey(name);
  }

  protected boolean processModifier(int ModifierId) {
    return false;
  }

  protected boolean processSetting(int SettingId, String argument) {
    switch (SettingId) {
      case SECTION_SORTKEY:
        sortKey = argument;
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
      if (i == text.length()) {
        return text;
      }
      char nextChar = text.charAt(i + 1);
      if ((nextChar == ' ') || (nextChar == '\n') || (nextChar == '\r') || (nextChar == '\t')) {
        return text.substring(0, i + 1);
      }
    }

    return text;
  }

  /**
   * Feeds an input comment block as a string and parses it. It interprets
   *
   * @<section name> blocks and passes the contents to AddToSection.
   */
  public void setText(String text) throws Exception {
    if (!text.startsWith("/**") || !text.endsWith("**/")) {
      return;
    }
    text = text.substring(3, text.length() - 3).trim();
    this.text = text;

    String[] sections = text.split("@");

    StringBuilder description = new StringBuilder();
    int curSection = -1;
    int index = 0;

    for (String section : sections) {
      String textLine;

      if (index == 0) {
        textLine = sections[0];
      } else {
        String[] words = section.split("[\t\n ]", 2);
        String commandName = words[0];
        textLine = (words.length > 1) ? words[1] : "";

        if (isModifier(commandName)) {
          processModifier(getModifierId(commandName));
        } else if (isSetting(commandName)) {
          words = section.split("[\t\n ]", 3);
          textLine = (words.length > 2) ? words[2] : "";
          processSetting(getSettingId(commandName), words[1]);
        } else if (isSection(commandName)) {
          curSection = getSectionId(commandName);
        } else {
          textLine = "@" + commandName + " " + textLine;
        }
      }

      if (curSection == -1) {
        description.append(textLine);
        description.append(" ");
      } else {
        addToSection(curSection, textLine);
      }
      index++;
    }

    String descriptionString = description.toString();
    this.description = extractShortDescription(descriptionString);
    longDescription.add(descriptionString);
  }

  /**
   * Short description of the entity, i.e. the part before the first period
   * (.)
   */
  public String description() {
    return project.getTextProcessor().process(description, project);
  }

  /**
   * Long description of the entity *
   */
  public CommentTextBlock longdescription() {
    return longDescription;
  }

  /**
   * Comments of the entity *
   */
  public CommentTextBlock comments() {
    return comments;
  }

  /**
   * Example block of the entity *
   */
  public CommentTextBlock example() {
    return example;
  }

  /**
   * The see also list of the entity *
   */
  public CommentSeeAlsoList see() {
    return see;
  }

  /**
   * The sort key of this entity; returns null if not set. *
   */
  public String sortKey() {
    return sortKey;
  }

  /**
   * Checks whether the comment is empty *
   */
  public boolean isEmpty() {
    return text.trim().length() == 0;
  }

  protected String generateSection(String name, String classname, Object o) {
    String text = o.toString();
    if (text.length() == 0) {
      return "";
    }

    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("<dt class=\"");
    stringBuilder.append(classname);
    stringBuilder.append("\">");
    stringBuilder.append(name);
    stringBuilder.append(":</dt><dd class=\"");
    stringBuilder.append(classname);
    stringBuilder.append("\">");
    stringBuilder.append(text);
    stringBuilder.append("</dd>\n");

    return stringBuilder.toString();
  }
}
