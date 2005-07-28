import java.util.*;

	public class OxEntityList {
		Hashtable entities = new Hashtable();

		public OxEntity add(OxEntity entity) {
			return add(entity.name(), entity);
		}

		public OxEntity add(String name, OxEntity entity) {
			entities.put(name, entity);
			return entity;
		}

		public OxEntity get(String name) {
			return (OxEntity) entities.get(name);
		}

		public ArrayList sortedList() {
			ArrayList list = new ArrayList();
			for (Enumeration e = entities.elements(); e.hasMoreElements() ;)
				list.add(e.nextElement());

			Collections.sort(list, new Comparator() {
				public int compare(Object o1, Object o2) {
					OxEntity e1 = (OxEntity) o1;
					OxEntity e2 = (OxEntity) o2;
					return e1.name().toUpperCase().compareTo(e2.name().toUpperCase());
				}});
			return list;
		}
	}
	