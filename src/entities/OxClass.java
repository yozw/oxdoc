/**

oxdoc (c) Copyright 2005-2010 by Y. Zwols

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

package oxdoc.entities;

import java.util.ArrayList;
import java.util.HashSet;

import oxdoc.FileManager;
import oxdoc.comments.ClassComment;

public class OxClass extends OxEntity {

	private int enumCounter = 0;

	public enum Visibility {
		Private {
			public String toString() {
				return "private";
			}
		},
		Protected {
			public String toString() {
				return "protected";
			}
		},
		Public {
			public String toString() {
				return "public";
			}
		}
	};

	private interface MemberFilter {
		boolean keepItem(OxEntity entity);
	};

	public String Declaration;
	private OxEntityList _members = new OxEntityList();
	private OxFile _parentFile = null;
	private String _superClassName = null;

	OxClass(String name, OxFile parentFile) {
		super(name, null, new ClassComment(parentFile.project()), parentFile);
		_parentFile = parentFile;
		setIconType(FileManager.CLASS);
	}

	OxClass(String name, String superClassName, OxFile parentFile) {
		super(name, null, new ClassComment(parentFile.project()), parentFile);
		_parentFile = parentFile;
		setIconType(FileManager.CLASS);
		_superClassName = superClassName;
	}

	public OxMethod addMethod(String name) {
		return (OxMethod) _members.add(new OxMethod(name, this));
	}

	public OxField addField(String name, Visibility vis) {
		return (OxField) _members.add(new OxField(name, this, vis));
	}

	public OxEnum addEnum(String alternativeName, ArrayList elements,
			Visibility vis) {
		if ((alternativeName == null) || (alternativeName.length() == 0)) {
			enumCounter++;
			alternativeName = "Anonymous enum " + enumCounter;
		}
		String[] _elements = new String[elements.size()];
		for (int i = 0; i < elements.size(); i++)
			_elements[i] = elements.get(i).toString();
		return (OxEnum) _members.add(new OxEnum(alternativeName, _elements,
				this, vis));
	}

	public ArrayList members() {
		return _members.sortedList();
	}

	public ArrayList filterMembers(MemberFilter filter) {
		ArrayList members = members();
		ArrayList list = new ArrayList();

		for (int i = 0; i < members.size(); i++) {
			OxEntity entity = (OxEntity) members.get(i);
			if (filter.keepItem(entity))
				list.add(entity);
		}
		return list;
	}

	public ArrayList filterInheritedMembers(MemberFilter filter) {
		ArrayList members = inheritedMembers();
		ArrayList list = new ArrayList();

		for (int i = 0; i < members.size(); i++) {
			OxEntity entity = (OxEntity) members.get(i);
			if (filter.keepItem(entity))
				list.add(entity);
		}
		return list;
	}

	public ArrayList getPrivateFields() {

		return filterMembers(new MemberFilter() {
			public boolean keepItem(OxEntity entity) {
				return ((entity instanceof OxField) && (((OxField) entity)
						.visibility() == Visibility.Private));
			}
		});
	}

	public ArrayList getProtectedFields() {

		return filterMembers(new MemberFilter() {
			public boolean keepItem(OxEntity entity) {
				return ((entity instanceof OxField) && (((OxField) entity)
						.visibility() == Visibility.Protected));
			}
		});
	}

	public ArrayList getPublicFields() {

		return filterMembers(new MemberFilter() {
			public boolean keepItem(OxEntity entity) {
				return ((entity instanceof OxField) && (((OxField) entity)
						.visibility() == Visibility.Public));
			}
		});
	}

	public ArrayList getMethods() {

		return filterMembers(new MemberFilter() {
			public boolean keepItem(OxEntity entity) {
				return (entity instanceof OxMethod);
			}
		});
	}

	public ArrayList getMethodsAndFields() {

		return filterMembers(new MemberFilter() {
			public boolean keepItem(OxEntity entity) {
				return (entity instanceof OxMethod)
						|| (entity instanceof OxField);
			}
		});
	}

	public ArrayList getEnums() {

		return filterMembers(new MemberFilter() {
			public boolean keepItem(OxEntity entity) {
				return (entity instanceof OxEnum);
			}
		});
	}

	public ArrayList getInheritedFields() throws Exception {
		
		return filterInheritedMembers(new MemberFilter() {
			public boolean keepItem(OxEntity entity) {
				return (entity instanceof OxField);
			}
		});
	}

	public ArrayList getInheritedMethods() throws Exception {
		
		return filterInheritedMembers(new MemberFilter() {
			public boolean keepItem(OxEntity entity) {
				return (entity instanceof OxMethod);
			}
		});
	}

	public ArrayList getInheritedEnums() throws Exception {
		
		return filterInheritedMembers(new MemberFilter() {
			public boolean keepItem(OxEntity entity) {
				return (entity instanceof OxEnum);
			}
		});
	}

	public ArrayList getSuperClasses() {
		ArrayList list = new ArrayList();
		OxClass currentClass = this;

		while (true) {
			String superClassName = currentClass.superClassName();
			if (superClassName == null)
				break;

			OxEntity entity = project().getSymbol(superClassName);
			if ((entity == null) || !(entity instanceof OxClass))
				break;
			
			currentClass = (OxClass) entity;
			list.add(currentClass);
		}
		return list;
	}
	

	public ArrayList inheritedMembers() {

		ArrayList list = new ArrayList();
		OxClass currentClass = this;
		HashSet seenMemberNames = new HashSet();
		ArrayList members = members();
		for (int i = 0; i < members.size(); i++)
		{
			OxEntity member = (OxEntity) members.get(i);
			seenMemberNames.add(member.name());
		}

		while (true) {
			String superClassName = currentClass.superClassName();
			if (superClassName == null)
				break;

			OxEntity entity = project().getSymbol(superClassName);
			if ((entity == null) || !(entity instanceof OxClass))
				break;
			currentClass = (OxClass) entity;
		
			members = currentClass.members();
			
			for (int i = 0; i < members.size(); i++) {
				OxEntity member = (OxEntity) members.get(i);
				if (seenMemberNames.contains(member.name()))
					continue;
				seenMemberNames.add(member.name());
				
				if (member instanceof OxMethod) {
					OxMethod oxMethod = (OxMethod) member;
					if (oxMethod.visibility() != OxClass.Visibility.Private)
						list.add(oxMethod);
				} else if (member instanceof OxField) {
					OxField oxField = (OxField) member;
					if (oxField.visibility() != OxClass.Visibility.Private)
						list.add(oxField);
				} else if (member instanceof OxEnum) {
					OxEnum oxEnum = (OxEnum) member;
					if (oxEnum.visibility() != OxClass.Visibility.Private)
						list.add(oxEnum);
				} else
					throw new java.lang.Error("Class member has unexpected class: "
							+ member);
			}
		}
		return list;
	}

	public OxMethod methodByName(String s) {
		return (OxMethod) _members.get(s);
	}

	public OxField fieldByName(String s) {
		return (OxField) _members.get(s);
	}

	public OxFile parentFile() {
		return _parentFile;
	}

	public String superClassName() {
		return _superClassName;
	}

	public OxClass superClass() {
		if (_superClassName == null)
			return null;

		return (OxClass) parentFile().project().getSymbol(_superClassName);
	}

	public String url() {
		return parentFile().url() + "#" + name();
		/** Modfied by CF **/
	}

	public String toString() {
		return "<OxClass " + name() + ">";
	}
}
