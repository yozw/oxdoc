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

import java.util.HashMap;

public class BaseComment {
   final int SECTION_COMMENTS = 1, SECTION_REF = 2, SECTION_EXAMPLE = 3, SECTION_SEE = 4;
 
   private String _text = "";
   private String _description = "";
   private CommentTextBlock _longdescription = null;
   private CommentTextBlock _comments = null;
   private CommentTextBlock _example = null;
   private CommentSeeAlsoList _see = null;
   private CommentList _ref = null;
   private CommentList _todo = null;
   private HashMap _sections = new HashMap();
   private HashMap _modifiers = new HashMap();
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
      registerSection("ref", SECTION_REF);
      registerSection("example", SECTION_EXAMPLE);
      registerSection("see", SECTION_SEE);
   }

   protected void registerSection(String name, int SectionId)
   {
      _sections.put(name, new Integer(SectionId));
   }

   protected void registerModifier(String name, int ModifierId)
   {
      _modifiers.put(name, new Integer(ModifierId));
   }

   protected int getSectionId(String name) 
   {
      return (Integer) _sections.get(name);
   }

   protected int getModifierId(String name) 
   {
      return (Integer) _modifiers.get(name);
   }

   /** Add a piece of text to one of the sections. Do not access this
   method directly. It is used by SetText. Override this method to add more sections.
   **/
   protected boolean addToSection(int SectionId, String text) 
   {
      switch (SectionId) 
      {
          case SECTION_COMMENTS: _comments.add(text); break;
          case SECTION_REF:      _ref.add(text); break;
          case SECTION_EXAMPLE:  _example.add(text); break;
          case SECTION_SEE:      _see.add(text); break;
          default: return false;
      }
      return true;
   }

   protected boolean isSection(String name) 
   {
      return _sections.containsKey(name);
   }

   protected boolean isModifier(String name) 
   {
      return _modifiers.containsKey(name);
   }

   protected boolean processModifier(int ModifierId) 
   {
      return false;
   }

   private static String extractShortDescription(String text) {
      text = text + " ";

      int i = -1;
      while ((i = text.indexOf(".", i+1)) != -1)
      {
         // short description ends with space followed by whitespace, cr, lf, or tab
         if (i == text.length()) 
            return text;
         char nextChar = text.charAt(i+1);
         if ((nextChar == ' ') || (nextChar == '\n') || (nextChar == '\r') || (nextChar == '\t')) 
            return text.substring(0, i + 1);
      }

      return text;
   }

   /** Feeds an input comment block as a string and parses it. It interprets @<section name> blocks and
   passes the contents to AddToSection. **/
   public void setText(String text) throws ParseException {
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
         else
         {
            String[] words = sections[i].split("[\t\n ]", 2);
            String commandName = words[0];
            textLine = (words.length > 1) ? words[1] : "";

            if (isModifier(commandName))
               processModifier(getModifierId(commandName));
            else if (isSection(commandName))
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

   /** Short description of the entity, i.e. the part before the first . **/
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

   /** Checks whether the comment is empty **/
   public boolean isEmpty() {
      return _text.trim().length() == 0;
   }
}
