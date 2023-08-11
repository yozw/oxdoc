package oxdoc;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.xml.sax.InputSource;
import org.xml.sax.SAXParseException;

import oxdoc.Config;

import java.io.StringReader;

@RunWith(JUnit4.class)
public class ConfigTest {

  @Test
  public void testConfig() throws Exception {
    String xml = "<oxdoc><option outputdir='test-docs'/></oxdoc>";
    Config config = new Config();
    InputSource inputSource = new InputSource(new StringReader(xml));
    config.parse(inputSource);
  }

  @Test
  public void testConfig_NameValue() throws Exception {
    String xml = "<oxdoc><option name='outputdir' value='test-docs'/></oxdoc>";
    Config config = new Config();
    InputSource inputSource = new InputSource(new StringReader(xml));
    config.parse(inputSource);
  }

  @Test(expected=IllegalArgumentException.class)
  public void testConfig_illegalOption() throws Exception {
    String xml = "<oxdoc><option illegal='true'/></oxdoc>";
    Config config = new Config();
    InputSource inputSource = new InputSource(new StringReader(xml));
    config.parse(inputSource);
  }

  @Test(expected=IllegalArgumentException.class)
  public void testConfig_illegalSyntax() throws Exception {
    String xml = "<oxdoc><option name='outputdir' value='test-docs' another='true'/></oxdoc>";
    Config config = new Config();
    InputSource inputSource = new InputSource(new StringReader(xml));
    config.parse(inputSource);
  }

  @Test(expected=IllegalArgumentException.class)
  public void testConfig_illegalSyntax_nameValue() throws Exception {
    String xml = "<oxdoc><option flag='outputdir' value='test-docs'/></oxdoc>";
    Config config = new Config();
    InputSource inputSource = new InputSource(new StringReader(xml));
    config.parse(inputSource);
  }

  @Test(expected=SAXParseException.class)
  public void testConfig_illegalSyntax_xmlError() throws Exception {
    String xml = "<oxdoc><option outputdir='test-docs'></oxdoc>";
    Config config = new Config();
    InputSource inputSource = new InputSource(new StringReader(xml));
    config.parse(inputSource);
  }
}

