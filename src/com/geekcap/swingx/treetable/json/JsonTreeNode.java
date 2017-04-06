package com.geekcap.swingx.treetable.json;

import java.util.ArrayList;
import java.util.List;

public class JsonTreeNode {

	private String name;
	private Object value;
	private String nodePath;
	private List<JsonTreeNode> children = new ArrayList<JsonTreeNode>();
	
	public JsonTreeNode(String name, Object value){
		this.setName(name);
		this.setValue(value);
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public List<JsonTreeNode> getChildren() {
		return children;
	}

	public void setChildren(List<JsonTreeNode> children) {
		this.children = children;
	}
	
	public void addChild(String name, Object value) {
		this.children.add(new JsonTreeNode(name, value));
	}
	
	public void addChild(JsonTreeNode child) {
		this.children.add(child);
	}

	public String getNodePath() {
		return nodePath;
	}

	public void setNodePath(String nodePath) {
		this.nodePath = nodePath;
	}
}
