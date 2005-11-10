/**

oxdoc (c) Copyright 2005 by Y. Zwols [yori@brown.edu]

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

public class OxEntity {
    private String name;
    protected OxFile _parentFile = null;
    private BaseComment _comment = null;

    public OxEntity(String name, BaseComment comment) {
	this.name = name;
	_comment = comment;
    }

    public OxEntity(String name, BaseComment comment, OxFile parentFile) {
	this.name = name;
	_comment = comment;
	_parentFile = parentFile;
    }

    public String name() {
	return this.name;
    }

    public String description() {
	return TextProcessor.process(_comment.description());
    }

    protected String parentFileUrl() {
	return (_parentFile == null)?"":_parentFile.url();
    }

    public BaseComment setComment(String comment) throws ParseException {
	_comment.setText(comment);
	return _comment;
    }

    public BaseComment comment() {
	return _comment;
    }
		
    public String url() {
	return ""; 
    }

    public String displayName() {
	return name;
    }

    public String link() {
	if (url().length() == 0)
	    return displayName();
	else
	    return "<a href=\"" + url() + "\">" + displayName() + "</a>";
    }
}
