package oxdoc.parser;

import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static oxdoc.parser.ParserTestHelper.create;

@RunWith(JUnit4.class)
public class ParserTest extends TestCase {

  @Test
  public void testClassDeclaration() throws Exception {
    String input = "class X : Y { decl Z; X(); W(); virtual U(); }";
    ParserTestHelper helper = create(input).showInternals().test();
    assertNotNull(helper.getProject().getSymbol("X"));
    assertNotNull(helper.getProject().getSymbol("X::Z"));
    assertNull(helper.getProject().getSymbol("Y"));
  }

  @Test
  public void testFunctionDeclaration() throws Exception {
    String input = "F() { }";
    ParserTestHelper helper = create(input);
    helper.test();
    assertNotNull(helper.getProject().getSymbol("F"));
  }

  @Test
  public void testMainFunction() throws Exception {
    String input = "main() {}";
    ParserTestHelper helper = create(input);
    helper.test();
    assertNotNull(helper.getProject().getSymbol("main"));
  }

  @Test
  public void testMainFunctionWithDoc() throws Exception {
    String input = "/** doc **/ main() {}";
    ParserTestHelper helper = create(input);
    helper.test();
    assertNotNull(helper.getProject().getSymbol("main"));
  }

  @Test
  public void testDisplayMathDoc() throws Exception {
    String input = "/** $$x^2+y^2=z^2$$ **/ main() {}";
    ParserTestHelper helper = create(input);
    helper.test();
    assertNotNull(helper.getProject().getSymbol("main"));
  }

  @Test
  public void testInlineMathDoc() throws Exception {
    String input = "/** $x^2+y^2=z^2$$ **/ main() {}";
    ParserTestHelper helper = create(input);
    helper.test();
    assertNotNull(helper.getProject().getSymbol("main"));
  }

  @Test
  public void testInlineConditional() throws Exception {
    String input = "main() { decl x = u == 0 ? 0 : 1; }";
    ParserTestHelper helper = create(input);
    helper.test();
    assertNotNull(helper.getProject().getSymbol("main"));
  }

  @Test
  public void testFunctionWithDefaultValues() throws Exception {
    String input = "main(a=4) {}";
    ParserTestHelper helper = create(input);
    helper.test();
    assertNotNull(helper.getProject().getSymbol("main"));
  }

  @Test
  public void testForEach() throws Exception {
    String input = "main() {foreach(x in y[0]) println(x);}";
    ParserTestHelper helper = create(input);
    helper.test();
  }

  @Test
  public void testForEachWithBraces() throws Exception {
    String input = "main() {foreach(x in y[0]) { println(x); } }";
    ParserTestHelper helper = create(input);
    helper.test();
  }
}
