import java.util.*;

	public class OxProject {
    	private OxEntityList _files   = new OxEntityList();
		private OxEntityList _symbols = new OxEntityList();

		public OxFile addFile(String name) {
			return (OxFile) _files.add(new OxFile(name, this));
 		}

		public ArrayList files() {
			return _files.sortedList();
		}

		public OxEntity addSymbol(OxEntity entity) {
			return (OxEntity) _symbols.add(entity.name(), entity);
		}

		public ArrayList symbols() {
			return _symbols.sortedList();
		}

		public OxEntity getSymbol(String name) {
			return (OxEntity) _symbols.get(name);
		}

		public String linkToSymbol(String name) {
			OxEntity entity = getSymbol(name);
			if (entity == null) {
				oxdoc.warning("Symbol '" + name + "' referenced to, but was not found");
				return name;
			}
			else
				return linkToEntity(entity);
		}

		public String linkToEntity(OxEntity entity) {
			return "<a href=\"" + entity.url() + "\">" + entity.name() + "</a>";
		}
		

	}
