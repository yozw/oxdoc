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

public class OxFile extends OxEntity {
    private OxEntityList _functions = new OxEntityList();
    private OxEntityList _classes = new OxEntityList();
    private OxProject  _project;
		
    public OxFile(String fileName, OxProject project) {
			super(fileName, new FileComment());
	_project = project;
			setIconType(FileManager.FILE);
    }

    public OxProject project() {
	return _project;
    }
		
    public OxFunction addFunction(String name) {
	return (OxFunction) _functions.add(new OxFunction(name, this));
    }

    public OxClass addClass(String name) {
	return (OxClass) _classes.add(new OxClass(name, this));
    }

    public OxClass addClass(String name, String parentclassname) {
	return (OxClass) _classes.add(new OxClass(name, parentclassname, this));
    }

    public OxClass getClass(String name) {
	return (OxClass) _classes.get(name);
    }

    public ArrayList functions() {
	return _functions.sortedList();
    }

    public ArrayList classes() {
	return _classes.sortedList();
    }

    public String url() {
	return name() + ".html";
    }

    public String toString() {
	return "<OxFile " + name() + ">";
    }
}
