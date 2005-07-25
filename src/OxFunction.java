import java.util.*;

	public class OxFunction extends OxEntity {
		public String _declaration;

		OxFunction(String name, OxFile parentFile) {
			super(name, parentFile);
		}

		public String url() {
			return parentFileUrl() + "#" + name();
		}

		public String declaration() {
			return _declaration;
		}

		public String displayName() {
			return name();
		}
}
