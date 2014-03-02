package oxdoc.entities;

import junit.framework.TestCase;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import oxdoc.OxProject;

import static org.mockito.Mockito.when;

public class OxEntityListTest extends TestCase {

  @Mock private OxProject project;
  @Mock private OxFile file;

  private OxClass classA;
  private OxClass classB;
  private OxClass classC;

  @Override
  protected void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    when(file.getProject()).thenReturn(project);
    classA = new OxClass("A", file);
    classB = new OxClass("B", file);
    classC = new OxClass("C", file);
  }

  public void testSize() throws Exception {
    OxEntityList<OxEntity> list = new OxEntityList<OxEntity>();
    assertEquals(0, list.size());

    list.add(new OxMethod("f", file));
    assertEquals(1, list.size());

    list.add(new OxMethod("F", file));
    assertEquals(2, list.size());
  }

  public void testIsEmpty() throws Exception {
    OxEntityList<OxEntity> list = new OxEntityList<OxEntity>();
    assertTrue(list.isEmpty());

    list.add(new OxMethod("f", file));
    assertFalse(list.isEmpty());
  }

  public void testGet() throws Exception {
    OxEntityList<OxEntity> list = new OxEntityList<OxEntity>();
    OxMethod method1 = new OxMethod("f", file);
    OxMethod method2 = new OxMethod("F", file);
    list.add(method1);
    list.add(method2);

    assertEquals(method1, list.get("f"));
    assertEquals(method2, list.get("F"));
    assertNull(list.get("g"));
  }

  public void testIterator() throws Exception {
    OxEntityList<OxEntity> list = new OxEntityList<OxEntity>();
    list.add(new OxMethod("f", file));
    list.add(new OxMethod("F", file));

    StringBuilder names = new StringBuilder();
    for (OxEntity entity : list) {
      names.append(entity.getName());
    }
    assertEquals("Ff", names.toString());
  }

  public void testFilterByClass() throws Exception {
    OxEntityList<OxEntity> list = new OxEntityList<OxEntity>();
    list.add(new OxField("f", classA, OxClass.Visibility.Public));
    list.add(new OxField("f", classB, OxClass.Visibility.Public));
    list.add(new OxMethod("f", classC));

    OxEntityList<OxField> fields = list.filterByClass(OxField.class);
    OxEntityList<OxMethod> methods = list.filterByClass(OxMethod.class);

    assertEquals(2, fields.size());
    assertEquals(1, methods.size());
  }

  public void testGetNonInternal() throws Exception {

  }
}
