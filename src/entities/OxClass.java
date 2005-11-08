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
