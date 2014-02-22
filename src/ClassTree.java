/**

 oxdoc (c) Copyright 2005-2012 by Y. Zwols

 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation; either
 version 2.1 of the License, or (at your option) any later version.

 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA

 **/

package oxdoc;

import oxdoc.entities.OxClass;
import oxdoc.entities.OxEntityList;

import java.util.ArrayList;
import java.util.Hashtable;

class ClassTree {

  private class Node {
    OxClass oxClass;
    ArrayList children = new ArrayList();
    int depth = 0;
  }

  private final OxProject project;
  private final Node rootNode = new Node();
  private final Hashtable nodes = new Hashtable(); // keys: OxClass, values: Node
  private int maxDepth = 0;

  public ClassTree(OxProject project, OxEntityList classes) {
    this.project = project;

    ArrayList classList = classes.sortedList();

    // first, construct all nodes
    for (int i = 0; i < classList.size(); i++) {
      OxClass oxClass = (OxClass) classList.get(i);
      Node node = new Node();
      node.oxClass = oxClass;
      nodes.put(oxClass, node);
    }

    // next, construct parent-child relationships
    for (int i = 0; i < classList.size(); i++) {
      OxClass oxClass = (OxClass) classList.get(i);
      OxClass parentClass = oxClass.superClass();

      Node classNode = (Node) nodes.get(oxClass);
      Node parentNode = (parentClass != null) ? (Node) nodes.get(parentClass) : rootNode;

      parentNode.children.add(classNode);
    }

    updateDepths();
  }

  private void addChildrenToHtmlList(StringBuffer text, Node node) {
    if (node.children.size() == 0)
      return;
    text.append("<ul>\n");
    for (int i = 0; i < node.children.size(); i++) {
      Node child = (Node) node.children.get(i);
      if (i < node.children.size() - 1)
        text.append("<li>");
      else
        text.append("<li class=\"last\">");
      text.append("<span class=\"label\">" + project.linkToEntity(child.oxClass) + "</span><span class=\"text\">"
          + child.oxClass.description() + "</span>\n");
      addChildrenToHtmlList(text, child);
    }
    text.append("</ul>\n");
  }

  public ArrayList getTopClasses() {
    return getChildClasses(rootNode);
  }

  private ArrayList getChildClasses(Node node) {
    ArrayList children = new ArrayList();
    for (int i = 0; i < node.children.size(); i++)
      children.add(((Node) node.children.get(i)).oxClass);
    return children;
  }

  public ArrayList getChildren(OxClass oxClass) {
    Node node = (Node) nodes.get(oxClass);
    return getChildClasses(node);
  }

  public int maxDepth() {
    return maxDepth;
  }

  private void updateDepths() {
    maxDepth = 0;
    updateDepths(rootNode, 0);
  }

  private void updateDepths(Node node, int depth) {
    node.depth = depth;
    if (depth > maxDepth)
      maxDepth = depth;
    for (int i = 0; i < node.children.size(); i++) {
      Node child = (Node) node.children.get(i);
      updateDepths(child, depth + 1);
    }
  }

  public int getClassDepth(OxClass oxClass) {
    Node node = (Node) nodes.get(oxClass);
    return node.depth;
  }

  public String toHtmlList() {
    StringBuffer buf = new StringBuffer();
    addChildrenToHtmlList(buf, rootNode);
    return buf.toString();
  }
}
