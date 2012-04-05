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

package oxdoc.entities;

import java.util.ArrayList;

import oxdoc.FileManager;
import oxdoc.comments.EnumComment;

public class OxEnum extends OxEntity {
	public String Declaration;
	public OxClass.Visibility _visibility;
	private ArrayList _elements = new ArrayList();

	OxEnum(String name, String[] elements, OxClass oxclass,
			OxClass.Visibility visibility) {
		super(name, oxclass, new EnumComment(oxclass.parentFile().project()),
				oxclass.parentFile());
		setIconType(FileManager.ENUM);
		
		for (int i = 0; i < elements.length; i++)
			_elements.add(new OxEnumElement(elements[i], this));
		_visibility = visibility;
	}

	OxEnum(String name, String[] elements, OxFile oxfile) {
		super(name, null, new EnumComment(oxfile.project()), oxfile);

		setIconType(FileManager.ENUM);
		for (int i = 0; i < elements.length; i++)
			_elements.add(new OxEnumElement(elements[i], this));
		_visibility = OxClass.Visibility.Public;
	}

	public String url() {
		if (parentClass() != null)
			return parentFileUrl() + "#" + parentClass().name() + "___"
					+ displayName();
		else
			return parentFileUrl() + "#" + displayName();
	}

	public String declaration() {
		String decl = "";
		decl += " enum { " + elementString() + " }";
		if (parentClass() != null)
			decl += " [" + visibility() + "]";
		return decl;
	}

	public String elementString() {
		String decl = "";
		for (int i = 0; i < _elements.size(); i++) {
			if (i != 0)
				decl += ", ";
			decl += ((OxEnumElement) _elements.get(i)).name();
		}
		return decl;
	}

	public OxClass.Visibility visibility() {
		return _visibility;
	}

	public String toString() {
		return "<OxEnum " + referenceName() + ">";
	}

	public boolean isInternal() {
		return ((EnumComment) comment()).hasInternalModifier()
				|| (visibility() != OxClass.Visibility.Public);
	}

	public ArrayList elements()
	{
		return _elements;
	}
}
