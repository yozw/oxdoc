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
}
