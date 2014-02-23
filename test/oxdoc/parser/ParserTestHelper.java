package oxdoc.parser;

import oxdoc.Config;
import oxdoc.FileManager;
import oxdoc.OxProject;
import oxdoc.TextProcessor;
import oxdoc.entities.OxFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class ParserTestHelper {

  private final Config config;
  private final OxProject project;
  private final OxFile file;
  private final InputStream inputStream;

  private ParserTestHelper(InputStream inputStream) {
    this.inputStream = inputStream;
    config = new Config();
    TextProcessor textProcessor = new TextProcessor(config);
    FileManager fileManager = new FileManager(config);

    project = new OxProject(fileManager, textProcessor, config);
    file = new OxFile("test.ox", project);
  }

  public ParserTestHelper test() throws Exception {
    Parser parser = new Parser(inputStream, file, project);
    parser.OxFileDefinition();
    return this;
  }

  public OxProject getProject() {
    return project;
  }

  public static ParserTestHelper create(String input) throws Exception {
    InputStream inputStream = new ByteArrayInputStream(input.getBytes("UTF-8"));
    return new ParserTestHelper(inputStream);
  }
}
