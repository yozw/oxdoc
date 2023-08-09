/**

 oxdoc (c) Copyright 2005-2023 by Y. Zwols

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
import java.util.HashMap;
import java.util.Map;

import static oxdoc.util.Utils.checkNotNull;

public class ClassTree {

  private class Node {
    private final OxClass oxClass;
    private final ArrayList<Node> children = new ArrayList<Node>();
    int depth = 0;

    private Node() {
      this.oxClass = null;
    }

    private Node(OxClass oxClass) {
      this.oxClass = checkNotNull(oxClass);
    }
  }

  private final Node rootNode = new Node();
  private final Map<OxClass, Node> nodes = new HashMap<OxClass, Node>();
  private int maxDepth = 0;

  public ClassTree(OxEntityList<OxClass> classes) {
    // first, construct all nodes
    for (OxClass oxClass : classes) {
      nodes.put(oxClass, new Node(oxClass));
    }

    // next, construct parent-child relationships
    for (OxClass oxClass : classes) {
      OxClass parentClass = oxClass.getSuperClass();
      Node classNode = nodes.get(oxClass);
      Node parentNode = (parentClass != null) ? nodes.get(parentClass) : rootNode;
      parentNode.children.add(classNode);
    }

    updateDepths();
  }

  public OxEntityList<OxClass> getTopClasses() {
    return getChildClasses(rootNode);
  }

  private OxEntityList<OxClass> getChildClasses(Node node) {
    OxEntityList<OxClass> children = new OxEntityList<OxClass>();
    for (Node child : node.children) {
      children.add(child.oxClass);
    }
    return children;
  }

  public OxEntityList<OxClass> getChildren(OxClass oxClass) {
    Node node = nodes.get(oxClass);
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
    maxDepth = Math.max(depth, maxDepth);
    for (Node child : node.children) {
      updateDepths(child, depth + 1);
    }
  }

  public int getClassDepth(OxClass oxClass) {
    return nodes.get(oxClass).depth;
  }
}
