package tsp;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Vector;

public class Tree<T> implements Iterable<Tree<T>> {

    public T data;
    public Tree<T> parent;
    public List<Tree<T>> children;
    public int id;
    public Vector<Integer> nai;
    public Vector<Integer> naj;
    public boolean mati;

    public boolean isRoot() {
        return parent == null;
    }

    public boolean isLeaf() {
        return children.size() == 0;
    }

    private List<Tree<T>> elementsIndex;

    public Tree(T data,int id) {
        this.data = data;
        this.id = id;
        this.children = new LinkedList<Tree<T>>();
        this.elementsIndex = new LinkedList<Tree<T>>();
        this.elementsIndex.add(this);
        this.nai = new Vector<>();
        this.naj = new Vector<>();
        this.mati = false;
    }

    public Tree<T> addChild(T child, int id) {
        Tree<T> childNode = new Tree<T>(child,id);
        childNode.parent = this;
        this.children.add(childNode);
        this.registerChildForSearch(childNode);
        return childNode;
    }

    public int getLevel() {
        if (this.isRoot())
            return 0;
        else
            return parent.getLevel() + 1;
    }

    private void registerChildForSearch(Tree<T> node) {
        elementsIndex.add(node);
        if (parent != null)
            parent.registerChildForSearch(node);
    }

    public Tree<T> findTree(Comparable<T> cmp) {
        for (Tree<T> element : this.elementsIndex) {
            T elData = element.data;
            if (cmp.compareTo(elData) == 0)
                return element;
        }
        return null;
    }

    @Override
    public String toString() {
        return data != null ? data.toString() : "[data null]";
    }

    @Override
    public Iterator<Tree<T>> iterator() {
        TreeIter<T> iter = new TreeIter<T>(this);
        return iter;
    }

    public Vector<Tree<T>> getAllLeafNodes() {
        Vector<Tree<T>> leafNodes = new Vector<>();
        if (this.children.isEmpty()&&!this.mati) {
            leafNodes.add(this);
        } else {
            for (Tree<T> child : this.children) {
                leafNodes.addAll(child.getAllLeafNodes());
            }
        }
        return leafNodes;
    }
    
}
