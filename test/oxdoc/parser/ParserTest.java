package oxdoc.parser;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.nio.file.Files;
import java.nio.file.Paths;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static oxdoc.parser.ParserTestHelper.create;

@RunWith(JUnit4.class)
public class ParserTest {

  private String readTestFile(String fileName) throws Exception {
    return new String(Files.readAllBytes(Paths.get("test/oxdoc/parser/" + fileName)));
  }

  @Test
  public void testClassDeclaration() throws Exception {
    String input = "class X : Y { decl Z; X(); W(); virtual U(); }";
    ParserTestHelper helper = create(input);
    helper.test();
    assertNotNull(helper.getProject().getSymbol("X"));
    assertNotNull(helper.getProject().getSymbol("X::Z"));
    assertNull(helper.getProject().getSymbol("Y"));
  }

  @Test
  public void testFunction() throws Exception {
    String input = "f() { }";
    ParserTestHelper helper = create(input);
    helper.test();
    assertNotNull(helper.getProject().getSymbol("f"));
  }

  @Test
  public void testFunctionForward() throws Exception {
    String input = "f(a, b);\nf(a,b) { return a + b; }";
    ParserTestHelper helper = create(input);
    helper.test();
    assertNotNull(helper.getProject().getSymbol("f"));
  }

  @Test
  public void testFunctionForwardWithDefault() throws Exception {
    String input = "f(a, b=2);\nf(a,b) { return a + b; }";
    ParserTestHelper helper = create(input);
    helper.test();
    assertNotNull(helper.getProject().getSymbol("f"));
  }

  @Test
  public void testLambdaFunction() throws Exception {
    String input = "decl f = [=](arg) { println(a, arg); return b; };";
    ParserTestHelper helper = create(input);
    helper.test();
    assertNotNull(helper.getProject().getSymbol("f"));
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
  public void testAssignment_FromVariable() throws Exception {
    String input = "decl x = y;";
    ParserTestHelper helper = create(input);
    helper.test();
    assertNotNull(helper.getProject().getSymbol("x"));
  }

  @Test
  public void testMultipleAssignmentInDecl() throws Exception {
    String input = "decl [a, b, c] = {1, 2, 3};";
    ParserTestHelper helper = create(input);
    helper.test();
    assertNotNull(helper.getProject().getSymbol("a"));
    assertNotNull(helper.getProject().getSymbol("b"));
    assertNotNull(helper.getProject().getSymbol("c"));
  }

  @Test
  public void testMultipleAssignmentInDecl_Single() throws Exception {
    String input = "decl [a] = {1};";
    ParserTestHelper helper = create(input);
    helper.test();
    assertNotNull(helper.getProject().getSymbol("a"));
  }

  @Test
  public void testMultipleAssignment() throws Exception {
    String input = "[a, b, c] = {1, 2, 3};";
    ParserTestHelper helper = create(input);
    helper.test();
  }

  @Test
  public void testMultipleAssignment_skip() throws Exception {
    String input = "[a,,c] = {1, 3};";
    ParserTestHelper helper = create(input);
    helper.test();
  }

  @Test
  public void testMultipleAssignment_single() throws Exception {
    String input = "[a] = {1};";
    ParserTestHelper helper = create(input);
    helper.test();
  }

  @Test
  public void testRawString() throws Exception {
    String input = "static decl x = `line 1\nline 2`;";
    ParserTestHelper helper = create(input);
    helper.test();
  }

  @Test
  public void testEquality() throws Exception {
    String input = "f() { return x == y; }";
    ParserTestHelper helper = create(input);
    helper.test();
    assertNotNull(helper.getProject().getSymbol("f"));
  }

  @Test
  public void testDotEquality() throws Exception {
    String input = "f() { return x .== y; }";
    ParserTestHelper helper = create(input);
    helper.test();
    assertNotNull(helper.getProject().getSymbol("f"));
  }

  @Test
  public void testStrictlyLessThan() throws Exception {
    String input = "f() { return x < y; }";
    ParserTestHelper helper = create(input);
    helper.test();
    assertNotNull(helper.getProject().getSymbol("f"));
  }
  @Test
  public void testLessThan() throws Exception {
    String input = "f() { return x <= y; }";
    ParserTestHelper helper = create(input);
    helper.test();
    assertNotNull(helper.getProject().getSymbol("f"));
  }

  @Test
  public void testStrictlyGreaterThan() throws Exception {
    String input = "f() { return x > y; }";
    ParserTestHelper helper = create(input);
    helper.test();
    assertNotNull(helper.getProject().getSymbol("f"));
  }

  @Test
  public void testGreaterThan() throws Exception {
    String input = "f() { return x >= y; }";
    ParserTestHelper helper = create(input);
    helper.test();
    assertNotNull(helper.getProject().getSymbol("f"));
  }

  @Test
  public void testDotEquals() throws Exception {
    String input = "main() { println(x .== y); }";
    ParserTestHelper helper = create(input);
    helper.test();
    assertNotNull(helper.getProject().getSymbol("main"));
  }

  @Test
  public void testIf() throws Exception {
    String input = "main() { if (x == y) println(x); }";
    ParserTestHelper helper = create(input);
    helper.test();
    assertNotNull(helper.getProject().getSymbol("main"));
  }

  @Test
  public void testIfElse() throws Exception {
    String input = "main() { if (x == y) println(x); else println(y); }";
    ParserTestHelper helper = create(input);
    helper.test();
    assertNotNull(helper.getProject().getSymbol("main"));
  }

  @Test
  public void testIfElseWithBraces() throws Exception {
    String input = "main() { if (x == y) { println(x); } else { println(y); } }";
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
  public void testFor() throws Exception {
    String input = "main() { for (x = 0; x < 10; x++) println(x);}";
    ParserTestHelper helper = create(input);
    helper.test();
    assertNotNull(helper.getProject().getSymbol("main"));
  }

  @Test
  public void testForWithInnerDeclaration() throws Exception {
    String input = "main() { for (decl x = 0; x < 10; x++) println(x);}";
    ParserTestHelper helper = create(input);
    helper.test();
    assertNotNull(helper.getProject().getSymbol("main"));
  }

  @Test
  public void testForWithBraces() throws Exception {
    String input = "main() { for (x = 0; x < 10; x++) { println(x); } }";
    ParserTestHelper helper = create(input);
    helper.test();
    assertNotNull(helper.getProject().getSymbol("main"));
  }

  @Test
  public void testForEach() throws Exception {
    String input = "main() { foreach ( x in y[0]) println(x);}";
    ParserTestHelper helper = create(input);
    helper.test();
    assertNotNull(helper.getProject().getSymbol("main"));
  }

  @Test
  public void testForEachWithBraces() throws Exception {
    String input = "main() { foreach(x in y[0]) { println(x); } }";
    ParserTestHelper helper = create(input);
    helper.test();
  }

  @Test
  public void testLambda() throws Exception {
    String input = "decl f = [=] (x) { return x*x; };";
    ParserTestHelper helper = create(input);
    helper.test();
  }

  @Test
  public void testLambdaAsArgument() throws Exception {
    String input = "main() { max([=] (x) { return x*x; }); }";
    ParserTestHelper helper = create(input);
    helper.test();
  }

  @Test
  public void testForEachWithDeclare() throws Exception {
    String input = "main() { foreach(decl x in y[0]) { println(x); } }";
    ParserTestHelper helper = create(input);
    helper.test();
  }
 
  @Test
  public void testSerial() throws Exception {
    String input = "main() { serial decl y; }";
    ParserTestHelper helper = create(input);
    helper.test();
  }
 
  @Test
  public void testParallelFor() throws Exception {
    String input = "main() {parallel for(decl i=0; i<10; i++){ print(i);}}";
    ParserTestHelper helper = create(input);
    helper.test();
  }
 
  @Test
  public void testSwitchSingleOnCharacter() throws Exception {
    String input = " main() {decl character = 'a'; switch_single(character){  case 'a': print(character);}}";
    ParserTestHelper helper = create(input);
    helper.test();
  }

  @Test
  public void testSwitchOnCharacter() throws Exception {
    String input = " main() {decl character = 'a'; switch(character){  case 'a': print(character);}}";
    ParserTestHelper helper = create(input);
    helper.test();
  }

  @Test
  public void testDottedArgs() throws Exception {
    String input = "main(...args) {decl a = 1;}";
    ParserTestHelper helper = create(input);
    helper.test();
  }

  @Test
  public void testVectorConstant() throws Exception {
    String input = "static decl vector = <0, 1, 2>;";
    ParserTestHelper helper = create(input);
    helper.test();
  }

  @Test
  public void testVectorConstantWithoutCommas() throws Exception {
    String input = "static decl vector = <0 1 2>;";
    ParserTestHelper helper = create(input);
    helper.test();
  }

  @Test
  public void testVectorConstantWithRange() throws Exception {
    String input = "static decl vector = < 1:5 >;";
    ParserTestHelper helper = create(input);
    helper.test();
  }

  @Test
  public void testVectorConstantWithStepRange() throws Exception {
    String input = "static decl zerovector = < 1:[2]5 >;";
    ParserTestHelper helper = create(input);
    helper.test();
  }

  @Test
  public void testVectorConstantWithRepeat() throws Exception {
    String input = "static decl zerovector = < [3] *0>;";
    ParserTestHelper helper = create(input);
    helper.test();
  }

  @Test
  public void testVectorConstantWithVariableRepeat() throws Exception {
    String input = "enum{Vleng=3}; static decl zerovector = < [Vleng] *0>;";
    ParserTestHelper helper = create(input);
    helper.test();
  }

  @Test
  public void testVectorConstantWithSpecificElement() throws Exception {
    String input = "static decl vector = <1:3; 1:3; [0][0]=5>;";
    ParserTestHelper helper = create(input);
    helper.test();
  }

  @Test
  public void testVectorConstant_Complicated() throws Exception {
    String input = "static decl vector = < [4]*1,2; 10,11,14-2; 1:4; [3][4]=99,2; 8:[-3]2 >;";
    ParserTestHelper helper = create(input);
    helper.test();
  }

  @Test
  public void testLastIndex() throws Exception {
    String input = "static decl vector = x[.last][.last-1];";
    ParserTestHelper helper = create(input);
    helper.test();
  }

  @Test
  public void testMultiplicateConcatenation() throws Exception {
    String input = "static decl a = {\"%6.0f\"} ~ {\"%#10.5g\"} * 4  ~ {\"%#15.10g\"};";
    ParserTestHelper helper = create(input);
    helper.test();
  }

  @Test
  public void testIndexArrayByString() throws Exception {
    String input = "static decl aa = {\"one\", 10, \"two\", 0, \"four\", <1>, \"five\", {1,2}};";
    ParserTestHelper helper = create(input);
    helper.test();
  }

  @Test
  public void testFile_test1() throws Exception {
    String input = readTestFile("test1.ox");
    ParserTestHelper helper = create(input);
    helper.test();
  }

  @Test
  public void testVariableNamedIn() throws Exception {
    String input = "main() { decl in = 5; }";
    ParserTestHelper helper = create(input);
    helper.test();
  }
}
