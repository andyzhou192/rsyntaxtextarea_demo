package xml;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

import org.w3c.dom.*;
import org.xml.sax.SAXException;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.*;
import javax.xml.xpath.*;

public class DomXMLHelper {
	
	private DocumentBuilderFactory factory;
	private Element root;
	private Document xmldoc;
	private String sourceFile;

	public DomXMLHelper(String sourceFile){
		this.sourceFile = sourceFile;
		this.factory = DocumentBuilderFactory.newInstance();
		factory.setIgnoringElementContentWhitespace(true);
		try {
			DocumentBuilder db = factory.newDocumentBuilder();
			Document xmldoc = db.parse(new File(sourceFile));
			root = xmldoc.getDocumentElement();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @param tagName
	 * @param attributes
	 * @param textContent
	 * @return
	 */
	public Element createElement(String tagName, Map<String, String> attributes, String textContent){
		Element ele = xmldoc.createElement(tagName);
		if(null != attributes){
			for(String name : attributes.keySet()){
				ele.setAttribute(name, attributes.get(name));
			}
		}
		if(null != textContent){
			ele.setTextContent(textContent);
		}
		return ele;
	}
	
	/**
	 * 
	 * @param parent
	 * @param childern
	 * @return
	 */
	public Element appendChildern(Element parent, Element...childern){
		if(null != childern && null != parent){
			for(Element child : childern)
				parent.appendChild(child);
		}
		return parent;
	}
	
	/**
	 * 
	 * @param childern
	 * @return
	 */
	public Element appendChildernToRoot(Element...childern){
		return appendChildern(root, childern);
	}
	
	/**
	 * 
	 * @param parent
	 * @param child
	 * @return
	 */
	public Node appendChild(Node parent, Node child){
		return parent.appendChild(child);
	}
	
	/**
	 * 
	 * @param parent
	 * @param childern
	 * @return
	 */
	public Element removeChildern(Element parent, Element...childern){
		if(null != childern && null != parent){
			for(Element child : childern)
				parent.removeChild(child);
		}
		return parent;
	}
	
	/**
	 * 
	 * @param parentElement
	 * @param names
	 * @return
	 */
	public Element removeAttributes(Element parentElement, String...names){
		if(null != names && null != parentElement){
			for(String name : names)
				parentElement.removeAttribute(name);
		}
		return parentElement;
	}
	
	/**
	 * 
	 * @param parentNode
	 * @param childNodes
	 * @return
	 */
	public Node removeNodes(Node parentNode, Node...childNodes){
		if(null != childNodes && null != parentNode){
			for(Node childNode : childNodes)
				parentNode.removeChild(childNode);
		}
		return parentNode;
	}
	
	/**
	 * 
	 * @param express The XPath expression(eg. "/books/book[price<10]", "/books/book[@id='B02']")
	 * @param content
	 */
	public void updateTextContent(String express, String content){
		findSingleNode(express, root).setTextContent(content);
	}
	
	/**
	 * 将node的XML字符串输出到控制台
	 * @param node
	 */
	public void output(Node node) {
		TransformerFactory transFactory = TransformerFactory.newInstance();
		try {
			Transformer transformer = transFactory.newTransformer();
			transformer.setOutputProperty("encoding", "utf-8");
			transformer.setOutputProperty("indent", "yes");
			DOMSource source = new DOMSource();
			source.setNode(node);
			StreamResult result = new StreamResult();
			result.setOutputStream(System.out);

			transformer.transform(source, result);
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 查找节点，并返回第一个符合条件节点
	 * @param express The XPath expression(eg. "/books/book[price<10]", "/books/book[@id='B02']")
	 * @param source The starting context (a node, for example).
	 * @return
	 */
	public Node findSingleNode(String express, Object source) {
		Node result = null;
		XPathFactory xpathFactory = XPathFactory.newInstance();
		XPath xpath = xpathFactory.newXPath();
		try {
			result = (Node) xpath.evaluate(express, source, XPathConstants.NODE);
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * 查找节点，返回符合条件的节点集。
	 * @param express The XPath expression(eg. "/books/book[price<10]")
	 * @param source The starting context (a node, for example).
	 * @return
	 */
	public NodeList findNodes(String express, Object source) {
		NodeList result = null;
		XPathFactory xpathFactory = XPathFactory.newInstance();
		XPath xpath = xpathFactory.newXPath();
		try {
			result = (NodeList) xpath.evaluate(express, source, XPathConstants.NODESET);
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * 将Document输出到文件
	 * @param fileName
	 * @param doc
	 */
	public void saveAsXml(String fileName) {
		TransformerFactory transFactory = TransformerFactory.newInstance();
		try {
			Transformer transformer = transFactory.newTransformer();
			transformer.setOutputProperty("indent", "yes");
			DOMSource source = new DOMSource();
			source.setNode(xmldoc);
			StreamResult result = new StreamResult();
			result.setOutputStream(new FileOutputStream(fileName));

			transformer.transform(source, result);
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 将Document输出到文件
	 * @param fileName
	 * @param doc
	 */
	public void saveXml() {
		saveAsXml(this.sourceFile);
	}
	

	public Element getRoot() {
		return root;
	}

	public void setRoot(Element root) {
		this.root = root;
	}

	public Document getXmldoc() {
		return xmldoc;
	}

	public void setXmldoc(Document xmldoc) {
		this.xmldoc = xmldoc;
	}
	
	/**
	 * 
	 */
	public static void mergeXML(String mainxlm, String subxml){
		DomXMLHelper mainHelper = new DomXMLHelper(mainxlm);
		DomXMLHelper subHelper = new DomXMLHelper(subxml);
		
		NodeList subDeps = subHelper.findNodes("/project/dependencies/dependency", subHelper.getXmldoc());
		Node mainDep = mainHelper.findSingleNode("/project/dependencies", mainHelper.getXmldoc());
		for(int i = 0; i < subDeps.getLength(); i++){
			String temp_groupId = subHelper.findSingleNode("groupId", subDeps.item(i)).getTextContent();
			String temp_artifactId = subHelper.findSingleNode("artifactId", subDeps.item(i)).getTextContent();
			for(int j = 0; j < mainDep.getChildNodes().getLength(); j++){
				Node depNode = mainDep.getChildNodes().item(j);
				mainHelper.output(depNode);
				String groupId = mainHelper.findSingleNode("groupId", depNode).getTextContent().trim();
				String artifactId = mainHelper.findSingleNode("artifactId", depNode).getTextContent().trim();
				if(!groupId.equals(temp_groupId) && !artifactId.equals(temp_artifactId)){
					System.out.println(groupId + "-------------------------" + artifactId);
					mainHelper.appendChild(mainDep, mainDep.getOwnerDocument().adoptNode(subDeps.item(i)));
				} else {
					break;
				}
			}
			mainHelper.saveXml();
		}
	}
	
	public static void main(String[] args) {
		String mainxlm = "src/xml/pom.xml";
		String subxml = "src/xml/template_pom.xml";
		DomXMLHelper.mergeXML(mainxlm, subxml);
		
	}
	
//	public static void main(String[] args) {
//		XMLWriter xmlWriter = new XMLWriter("");
//		// --- 新建一本书开始 ----
//		Element bookElem = xmlWriter.createElement("book", null, null);
//		Element nameElem = xmlWriter.createElement("name", null, "新书");
//		Element priceElem = xmlWriter.createElement("price", null, "20");
//		Element memoElem = xmlWriter.createElement("memo", null, "新书的更好看。");
//		xmlWriter.appendChildern(bookElem, nameElem, priceElem, memoElem);
//		xmlWriter.appendChildernToRoot(bookElem);
//		xmlWriter.output(xmlWriter.getXmldoc());
//		// --- 新建一本书完成 ----
//		
//		// --- 下面对《哈里波特》做一些修改。 ----
//		// --- 查询找《哈里波特》----
//		Element theBook = (Element) xmlWriter.findSingleNode("/books/book[name='哈里波特']", xmlWriter.getRoot());
//		System.out.println("--- 查询找《哈里波特》 ----");
//		xmlWriter.output(theBook);
//		// --- 此时修改这本书的价格 -----
//		theBook.getElementsByTagName("price").item(0).setTextContent("15");// getElementsByTagName
//																			// 返回的是NodeList，所以要跟上item(0)。另外，getElementsByTagName("price")相当于xpath
//																			// 的".//price"。
//		System.out.println("--- 此时修改这本书的价格 ----");
//		xmlWriter.output(theBook);
//		// --- 另外还想加一个属性id，值为B01 ----
//		theBook.setAttribute("id", "B01");
//		System.out.println("--- 另外还想加一个属性id，值为B01 ----");
//		xmlWriter.output(theBook);
//		// --- 对《哈里波特》修改完成。 ----
//		// --- 要用id属性删除《三国演义》这本书 ----
//		theBook = (Element) xmlWriter.findSingleNode("/books/book[@id='B02']", xmlWriter.getRoot());
//		System.out.println("--- 要用id属性删除《三国演义》这本书 ----");
//		xmlWriter.output(theBook);
//		theBook.getParentNode().removeChild(theBook);
//		System.out.println("--- 删除后的ＸＭＬ ----");
//		xmlWriter.output(xmlWriter.getXmldoc());
//		// --- 再将所有价格低于10的书删除 ----
//		NodeList someBooks = xmlWriter.findNodes("/books/book[price<10]", xmlWriter.getRoot());
//		System.out.println("--- 再将所有价格低于10的书删除 ---");
//		System.out.println("--- 符合条件的书有　" + someBooks.getLength() + "本。 ---");
//		for (int i = 0; i < someBooks.getLength(); i++) {
//			someBooks.item(i).getParentNode().removeChild(someBooks.item(i));
//		}
//		xmlWriter.output(xmlWriter.getXmldoc());
//		xmlWriter.saveXml("Test1_Edited.xml");
//	}
}
