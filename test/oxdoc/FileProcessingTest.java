package oxdoc;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import oxdoc.parser.Parser;

@RunWith(JUnit4.class)
public class FileProcessingTest {

  private final static OxDocLogger logger = new OxDocLogger();
  private final static Config config = new Config(logger);
  private final static FileManager fileManager = new FileManager(logger, config);
  private final static TextProcessor textProcessor = new TextProcessor(logger, config);

  @Test
  public void thisAlwaysPasses() throws Exception {
    OxProject project = new OxProject(logger, fileManager, textProcessor);
    File file = new File("example/dist_degen.ox");
    
		ByteArrayOutputStream bufferStream = new ByteArrayOutputStream();
		OutputStreamWriter bufferWriter = new OutputStreamWriter(bufferStream);

		Preprocessor p = new Preprocessor(logger, config, bufferWriter);
		p.ProcessFile(file);

		ByteArrayInputStream bufferIn = new ByteArrayInputStream(bufferStream.toByteArray());

		Parser parser = new Parser(bufferIn, logger, project.addFile(file.getName()), project);
		parser.OxFileDefinition();

  }

  @Test
  @Ignore
  public void thisIsIgnored() {
  }
}

