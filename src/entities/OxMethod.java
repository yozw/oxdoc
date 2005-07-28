import java.util.*;

	public class OxMethod extends OxFunction {
		public String Declaration;
		private OxClass _class = null;

		OxMethod(String name, OxClass oxclass) {
			super(name, oxclass.parentFile());
			_class = oxclass;
		}

		public OxClass parentClass() {
			return _class;
		}

		public String url() {
			return parentFileUrl() + "#" + _class.name() + "___" + displayName();
		}

		public String displayName() {
			String[] pts = name().split("::");
			return pts[1];
		}
}
