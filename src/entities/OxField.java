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

package oxdoc.entities;

import oxdoc.FileManager;
import oxdoc.comments.FieldComment;

public class OxField extends OxEntity {
	public String Declaration;
	public OxClass.Visibility _visibility;
	public boolean Static = false;
	public boolean Constant = false;

	OxField(String name, OxFile parentFile) {
		super(name, null, new FieldComment(parentFile.project()), parentFile);

		setIconType(FileManager.FIELD);
		_visibility = OxClass.Visibility.Public;
	}

	OxField(String name, OxClass oxclass, OxClass.Visibility visibility) {
		super(name, oxclass, new FieldComment(oxclass.parentFile().project()),
				oxclass.parentFile());
		setIconType(FileManager.FIELD);
		_visibility = visibility;
	}

	public String url() {
		if (parentClass() != null)
			return parentFileUrl() + "#" + parentClass().name() + "___"
					+ displayName();
		else
			return parentFileUrl() + "#" + displayName();

	}

	public String declaration() {
		String decl = modifiers() + " ";
		decl += " decl " + name();
		if (parentClass() != null)
			decl += " [" + visibility() + "]";
		return decl.trim();
	}

	public String modifiers() {
		String mod = "";
		if (Static)
			mod += "static ";
		if (Constant)
			mod += "const ";
		return mod.trim();
	}

	public OxClass.Visibility visibility() {
		return _visibility;
	}

	public String toString() {
		return "<OxField " + referenceName() + ">";
	}

	public boolean isInternal() {
		return ((FieldComment) comment()).hasInternalModifier()
				|| (visibility() != OxClass.Visibility.Public);
	}

}
