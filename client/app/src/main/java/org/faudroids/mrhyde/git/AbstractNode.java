package org.faudroids.mrhyde.git;


import org.eclipse.egit.github.core.TreeEntry;

import java.io.Serializable;

public abstract class AbstractNode implements Comparable<AbstractNode>, Serializable {

	private final AbstractNode parent;
	private final String path;
	private final TreeEntry treeEntry;

	AbstractNode(AbstractNode parent, String path, TreeEntry treeEntry) {
		this.parent = parent;
		this.path = path;
		this.treeEntry = treeEntry;
	}


	public AbstractNode getParent() {
		return parent;
	}


	public String getPath() {
		return path;
	}


	public String getFullPath() {
		return treeEntry.getPath();
	}


	TreeEntry getTreeEntry() {
		return treeEntry;
	}


	@Override
	public int compareTo(AbstractNode node) {
		return path.compareTo(node.getPath());
	}

}