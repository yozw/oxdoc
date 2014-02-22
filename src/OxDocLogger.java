package oxdoc;

public class OxDocLogger {
  private Logger logger = null;

  public OxDocLogger(Logger logger) {
    this.logger = logger;
  }

  public OxDocLogger() {
    this(new Logger() {
      public void writeMessage(String message, int Code) {
        System.out.println(message);
      }
    });
  }

  public void message(String message) {
    if (logger != null)
      logger.writeMessage(message, 0);
  }

  public void warning(String message) {
    if (logger != null)
      logger.writeMessage("Warning: " + message, 1);
  }
}
