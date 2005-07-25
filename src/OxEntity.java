import java.util.*;

	public class OxEntity {
		private String name;
		protected OxFile _parentFile = null;
		private OxDocComment _comment = new OxDocComment();
		private String comments = "";

		public OxEntity(String name) {
			this.name = name;
		}

		public OxEntity(String name, OxFile parentFile) {
			this.name = name;
			_parentFile = parentFile;
		}

		public String name() {
			return this.name;
		}

		protected String parentFileUrl() {
			return (_parentFile == null)?"":_parentFile.url();
		}

		public OxDocComment SetComment(String comment) {
			_comment.SetText(comment);
			return _comment;
		}

		public OxDocComment Comment() {
			return _comment;
		}
		
		public String url() {
			return ""; 
		}
	}
