import java.util.*;

	public class OxEntity {
		private String name;
		protected OxFile _parentFile = null;
		private BaseComment _comment = null;
		private String comments = "";
		private int _iconType = FileManager.NONE;

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

		public int iconType() {
			return _iconType;
		}

		public String smallIcon() {
			return FileManager.smallIcon(_iconType);
		}

		public String largeIcon() {
			return FileManager.largeIcon(_iconType);
		}

		protected void setIconType(int iconType) {
			_iconType = iconType;
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
