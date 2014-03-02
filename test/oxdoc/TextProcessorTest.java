package oxdoc;

import junit.framework.TestCase;

public class TextProcessorTest extends TestCase {

  public void testFormatParagraphs() {
    assertEquals("", TextProcessor.formatParagraphs(""));
    assertEquals("", TextProcessor.formatParagraphs("\n"));
    assertEquals("", TextProcessor.formatParagraphs("\r\n"));
    assertEquals("", TextProcessor.formatParagraphs("\n\n"));
    assertEquals("", TextProcessor.formatParagraphs("\r\n\r\n"));
    assertEquals("hello world", TextProcessor.formatParagraphs("hello world"));
    assertEquals("hello\nworld", TextProcessor.formatParagraphs("hello\nworld"));
    assertEquals("hello\n<P/>\nworld", TextProcessor.formatParagraphs("hello\n\nworld"));
    assertEquals("hello\n<P/>\nworld", TextProcessor.formatParagraphs("hello\n\n\n\nworld"));
    assertEquals("hello\nworld", TextProcessor.formatParagraphs("hello\r\nworld"));
    assertEquals("hello\n<P/>\nworld", TextProcessor.formatParagraphs("hello\r\n\r\nworld"));
    assertEquals("hello\n<P/>\nworld\n", TextProcessor.formatParagraphs("hello\r\n\r\nworld\n"));
    assertEquals("hello\n<P/>\nworld\n", TextProcessor.formatParagraphs("hello\r\n\r\nworld\n\n\n"));
  }
}
