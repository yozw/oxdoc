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

  private final static OxDoc OXDOC = new OxDoc();

  @Test
  public void thisAlwaysPasses() throws Exception {
    OxProject project = new OxProject(OXDOC);
    File file = new File("example/dist_degen.ox");
    
		ByteArrayOutputStream bufferStream = new ByteArrayOutputStream();
		OutputStreamWriter bufferWriter = new OutputStreamWriter(bufferStream);

		Preprocessor p = new Preprocessor(OXDOC, bufferWriter);
		p.ProcessFile(file);

		ByteArrayInputStream bufferIn = new ByteArrayInputStream(bufferStream.toByteArray());

		Parser parser = new Parser(bufferIn, OXDOC, project.addFile(file.getName()), project);
		parser.OxFileDefinition();

  }

  @Test
  @Ignore
  public void thisIsIgnored() {
  }
}

