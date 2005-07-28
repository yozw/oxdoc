import java.util.*;

	public class OxEntity {
		private String name;
		protected OxFile _parentFile = null;
		private OxDocComment _comment = null;
		private String comments = "";

		public OxEntity(String name, OxDocComment comment) {
			this.name = name;
			_comment = comment;
		}

		public OxEntity(String name, OxDocComment comment, OxFile parentFile) {
			this.name = name;
			_comment = comment;
			_parentFile = parentFile;
		}

		public String name() {
			return this.name;
		}

		protected String parentFileUrl() {
			return (_parentFile == null)?"":_parentFile.url();
		}

		public OxDocComment SetComment(String comment) throws ParseException {
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
