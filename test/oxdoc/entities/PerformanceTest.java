package oxdoc.entities;

import junit.framework.TestCase;
import oxdoc.Config;
import oxdoc.OxProject;
import oxdoc.TextProcessor;
import oxdoc.util.Stopwatch;

import java.util.ArrayList;
import java.util.List;

public class PerformanceTest extends TestCase {

  private final Config config = new Config();
  private final TextProcessor textProcessor = new TextProcessor(config);

  public void testCreateProject() {
    int count = 1000;

    Stopwatch stopwatch = new Stopwatch();

    for (int i = 0; i < count; i++) {
      new OxProject(textProcessor, config);
    }
    System.out.println(String.format("Created %d projects in %d msec", count, stopwatch.elapsedMsec()));
  }

  public void testCreateClass() {
    OxProject project = new OxProject(textProcessor, config);
    OxFile file = new OxFile("a", project);
    int count = 1000;

    Stopwatch stopwatch = new Stopwatch();

    for (int i = 0; i < count; i++) {
      new OxClass("A", file);
    }
    System.out.println(String.format("Created %d classes in %d msec", count, stopwatch.elapsedMsec()));
  }

  public void testCreateFunction() {
    OxProject project = new OxProject(textProcessor, config);
    OxFile file = new OxFile("a", project);
    int count = 1000;

    Stopwatch stopwatch = new Stopwatch();

    for (int i = 0; i < count; i++) {
      new OxMethod("A", file);
    }
    System.out.println(String.format("Created %d functions in %d msec", count, stopwatch.elapsedMsec()));
  }

  public void testCreateMethod() {
    OxProject project = new OxProject(textProcessor, config);
    OxFile file = new OxFile("a", project);
    OxClass oxClass = new OxClass("A", file);
    int count = 1000;

    Stopwatch stopwatch = new Stopwatch();

    for (int i = 0; i < count; i++) {
      new OxMethod("A", oxClass);
    }
    System.out.println(String.format("Created %d methods in %d msec", count, stopwatch.elapsedMsec()));
  }

  public void testCreateField() {
    OxProject project = new OxProject(textProcessor, config);
    OxFile file = new OxFile("a", project);
    OxClass oxClass = new OxClass("A", file);
    int count = 1000;

    Stopwatch stopwatch = new Stopwatch();

    for (int i = 0; i < count; i++) {
      new OxField("A", oxClass, OxClass.Visibility.Public);
    }
    System.out.println(String.format("Created %d fields in %d msec", count, stopwatch.elapsedMsec()));
  }

  public void testCreateEnum() {
    OxProject project = new OxProject(textProcessor, config);
    OxFile file = new OxFile("a", project);
    OxClass oxClass = new OxClass("A", file);
    List<String> elements = new ArrayList<String>();

    for (int i = 0; i < 10; i++) {
      elements.add("el" + i);
    }

    int count = 1000;
    Stopwatch stopwatch = new Stopwatch();

    for (int i = 0; i < count; i++) {
      new OxEnum("A", elements, oxClass, OxClass.Visibility.Public);
    }
    System.out.println(String.format("Created %d enums in %d msec", count, stopwatch.elapsedMsec()));
  }

  public void testCreateEntityList() {
    OxProject project = new OxProject(textProcessor, config);
    OxFile file = new OxFile("a", project);
    List<String> elements = new ArrayList<String>();

    for (int i = 0; i < 10; i++) {
      elements.add("el" + i);
    }

    int count = 50;
    int element_count = 500;
    Stopwatch stopwatch = new Stopwatch();

    for (int i = 0; i < count; i++) {
      OxEntityList<OxEntity>  entities = new OxEntityList<OxEntity>();
      for (int j = 0; j < element_count; j++) {
        int index = (293 * j) % 983;
        entities.add(new OxMethod("A" + index, file));
      }
    }
    System.out.println(String.format("Created %d entity lists with %d random elements in %d msec", count, element_count, stopwatch.elapsedMsec()));
  }
}
