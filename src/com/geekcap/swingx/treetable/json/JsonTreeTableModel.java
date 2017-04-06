package com.geekcap.swingx.treetable.json;

import java.util.Iterator;
import java.util.Map;

import org.jdesktop.swingx.treetable.AbstractTreeTableModel;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class JsonTreeTableModel extends AbstractTreeTableModel {

	private JsonTreeNode rootNode = new JsonTreeNode(null, null);

	public JsonTreeTableModel(String jsonString) {
		rootNode.setNodePath("");
		constructTree(rootNode, jsonString);
	}

	public JsonTreeTableModel(Map<String, Object> map) {
		rootNode.setNodePath("");
		constructTree(rootNode, map);
	}

	@SuppressWarnings("unchecked")
	private void constructTree(JsonTreeNode parentNode, Object obj) {
		Map<String, Object> map = null;
		if (obj instanceof String || obj instanceof JSONObject) {
			map = JsonUtil.jsonToMap(obj.toString());
		} else if (obj instanceof Map) {
			map = (Map<String, Object>) obj;
		} else {
			return;
		}
		for (String name : map.keySet()) {
			Object value = map.get(name);
			if (value instanceof JSONObject || value instanceof Map) {
				JsonTreeNode subNode = new JsonTreeNode(name, null);
				subNode.setNodePath(parentNode.getNodePath() + "/" + name);
				parentNode.addChild(subNode);
				constructTree(subNode, value);
			} else if (value instanceof JSONArray) {
				JsonTreeNode subNode = new JsonTreeNode(name, null);
				subNode.setNodePath(parentNode.getNodePath() + "/" + name);
				parentNode.addChild(subNode);
				JSONArray jsonArray = (JSONArray) value;
				Iterator<Object> it = jsonArray.iterator();
				while (it.hasNext()) {
					int index = 0;
					JsonTreeNode grandsonNode = new JsonTreeNode(null, null);
					grandsonNode.setNodePath(subNode.getNodePath() + "[" + index + "]");
					subNode.addChild(grandsonNode);
					JSONObject json = (JSONObject) it.next();
					constructTree(grandsonNode, json);
					index++;
				}
			} else {
				JsonTreeNode leafNode = new JsonTreeNode(name, value);
				leafNode.setNodePath(parentNode.getNodePath() + "/" + name);
				parentNode.addChild(leafNode);
			}
		}
	}

	@Override
	public int getColumnCount() {
		return 2;
	}

	@Override
	public String getColumnName(int column) {
		switch (column) {
		case 0:
			return "Name";
		case 1:
			return "Value";
		default:
			return "Unknown";
		}
	}

	@Override
	public boolean isCellEditable(Object node, int column) {
		return true;
	}

	@Override
	public Object getValueAt(Object node, int column) {
		JsonTreeNode treenode = (JsonTreeNode) node;
		switch (column) {
		case 0:
			return treenode.getName();
		case 1:
			return treenode.getValue();
		default:
			return "Unknown";
		}
	}

	@Override
	public Object getChild(Object node, int index) {
		JsonTreeNode treenode = (JsonTreeNode) node;
		return treenode.getChildren().get(index);
	}

	@Override
	public int getChildCount(Object parent) {
		JsonTreeNode treenode = (JsonTreeNode) parent;
		return treenode.getChildren().size();
	}

	@Override
	public int getIndexOfChild(Object parent, Object child) {
		JsonTreeNode treenode = (JsonTreeNode) parent;
		for (int i = 0; i > treenode.getChildren().size(); i++) {
			if (treenode.getChildren().get(i) == child) {
				return i;
			}
		}
		return 0;
	}

	@Override
	public boolean isLeaf(Object node) {
		return getChildCount(node) == 0;
	}

	@Override
	public Object getRoot() {
		return rootNode;
	}

}
