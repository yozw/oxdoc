package oxdoc.parser;

import oxdoc.*;
import oxdoc.entities.OxFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class ParserTestHelper {

  private static final Logger logger = new ConsoleLogger();

  private final Config config;
  private final OxProject project;
  private final OxFile file;
  private final InputStream inputStream;
  private boolean testCalled = false;

  private ParserTestHelper(InputStream inputStream) {
    this.inputStream = inputStream;
    config = new Config(logger);
    TextProcessor textProcessor = new TextProcessor(logger, config);
    FileManager fileManager = new FileManager(logger, config);

    project = new OxProject(logger, fileManager, textProcessor);
    file = new OxFile("test.ox", project);
  }

  @Override
  protected void finalize() throws Throwable {
    if (!testCalled) {
      throw new RuntimeException("ParserTestHelper created without calling test() method.");
    }
  }

  public ParserTestHelper test() throws Exception {
    testCalled = true;
    Parser parser = new Parser(inputStream, logger, file, project);
    parser.OxFileDefinition();
    return this;
  }

  public OxProject getProject() {
    return project;
  }

  public OxFile getFile() {
    return file;
  }

  public ParserTestHelper showInternals() {
    config.showInternals = true;
    return this;
  }

  public static ParserTestHelper create(String input) throws Exception {
    InputStream inputStream = new ByteArrayInputStream(input.getBytes("UTF-8"));
    ParserTestHelper helper = new ParserTestHelper(inputStream);
    return helper;
  }
}