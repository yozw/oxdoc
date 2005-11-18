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

import java.util.*;

public class OxClass extends OxEntity {
    public String Declaration;
    private OxEntityList _methods = new OxEntityList();
    private OxFile _parentFile = null;
    private String _parentClassName = null;
		
    OxClass(String name, OxFile parentFile) {
	super(name, new ClassComment());
	_parentFile = parentFile;
    }

    OxClass(String name, String parentClassName, OxFile parentFile) {
	super(name, new ClassComment());
	_parentFile = parentFile;
			setIconType(FileManager.CLASS);
	_parentClassName = parentClassName;
    }

    public OxMethod addMethod(String name) {
	return (OxMethod) _methods.add(new OxMethod(name, this));
    }

    public ArrayList methods() {
	return _methods.sortedList();
    }

    public OxFile parentFile() {
	return _parentFile;
    }

    public String parentClassName() {
	return _parentClassName;
    }

    public OxClass parentClass() {
	if (_parentClassName == null)
	    return null;
	return (OxClass) parentFile().project().getSymbol(_parentClassName);
    }

    public String url() {
	return parentFile().url();
    }
}
