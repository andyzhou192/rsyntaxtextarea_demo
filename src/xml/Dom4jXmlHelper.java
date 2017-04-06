package xml;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.jaxen.JaxenException;

public class Dom4jXmlHelper {
	
	private Document xmlDoc;
	private Element rootElement;
	private File sourceFile;
	
	public Element getRootElement() {
		return rootElement;
	}
	
	public void setRootElement(Element rootElement) {
		this.rootElement = rootElement;
	}

	public Dom4jXmlHelper(String xmlFile) {
		this.sourceFile = new File(xmlFile);
		SAXReader reader = new SAXReader();   
		try {
			xmlDoc = reader.read(sourceFile);
			rootElement = xmlDoc.getRootElement();  
		} catch (DocumentException e) {
			e.printStackTrace();
		}   
	}
	
	/**
	 * 
	 * @return
	 */
	public Document createDocument(){
		return DocumentHelper.createDocument();
	}
	
	/**
	 * 
	 * @param xmlString
	 * @return
	 */
	public Document createDocument(String xmlString){
		try {
			return DocumentHelper.parseText(xmlString);
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 
	 * @param parent
	 * @param tagName
	 * @param content
	 * @return
	 */
	public Element addElement(Element parent, String tagName, String content){
		Element subEle = parent.addElement(tagName);
		subEle.setText(content);
		return subEle;
	}
	
	/**
	 * 
	 * @param parent
	 * @param child
	 */
	public void addElement(Element parent, Node child){
		parent.add(child);
	}
	
	/**
	 * 
	 * @param parent
	 * @param tagName
	 * @param cdataContent
	 * @return
	 */
	public Element addCDATAElement(Element parent, String tagName, String cdataContent){
		Element subEle = parent.addElement(tagName);
		return subEle.addCDATA(cdataContent);
	}
	
	/**
	 * 
	 * @param parent
	 * @param child
	 * @return
	 */
	public boolean delElement(Element parent, Element child){
		return parent.remove(child);
	}
	
	/**
	 * 
	 * @param ele
	 * @param newName
	 * @param newContent
	 */
	public void modifyElement(Element ele, String newTagName, String newContent){
		ele.setName(newTagName);
		ele.setText(newContent);
	}
	
	/**
	 * 
	 * @param parent
	 * @param xpathExpression 
	 * 	eg.  "/*[name()='project']/*[name()='groupId']"
	 * 	/*[name()='project']/*[name()='dependencies']/*[name()='dependency'][1]
	 * "/*[name()='project']/*[name()='dependencies']/*[name()='dependency'][@id=1]"
	 * @return
	 */
	public Element findSingleElement(Node parent, String xpathExpression){
		return (Element)parent.selectSingleNode(xpathExpression);
	}
	
	/**
	 * 
	 * @param parent
	 * @param child
	 * @return
	 */
	public Element findSingleElement(Element parent, Element child){
		return (Element)parent.selectSingleNode(child.getPath());
	}
	
	/**
	 * 
	 * @param child
	 * @return
	 */
	public Node findSingleElement(Element child){
		return xmlDoc.selectSingleNode(child.getPath());
	}
	
	/**
	 * 
	 * @param xpathExpression  
	 * 	eg.  "/*[name()='project']/*[name()='groupId']"
	 * 	/*[name()='project']/*[name()='dependencies']/*[name()='dependency'][1]
	 * "/*[name()='project']/*[name()='dependencies']/*[name()='dependency'][@id=1]"
	 * @return
	 */
	public Node findSingleElement(String xpathExpression){
		return rootElement.selectSingleNode(xpathExpression);
	}
	
	/**
	 * 
	 * @param parent
	 * @param xpathExpression 
	 * 	eg.  "/*[name()='project']/*[name()='groupId']"
	 * 	/*[name()='project']/*[name()='dependencies']/*[name()='dependency'][1]
	 * "/*[name()='project']/*[name()='dependencies']/*[name()='dependency'][@id=1]"
	 * @return
	 */
	public List<Node> findNodes(Element parent, String xpathExpression){
		return parent.selectNodes(xpathExpression);
	}
	
	/**
	 * 
	 * @param xpathExpression 
	 * 	eg.  "/*[name()='project']/*[name()='groupId']"
	 * 	/*[name()='project']/*[name()='dependencies']/*[name()='dependency'][1]
	 *  "/*[name()='project']/*[name()='dependencies']/*[name()='dependency'][@id=1]"
	 * @return
	 */
	public List<Node> findNodes(String xpathExpression){
		return xmlDoc.selectNodes(xpathExpression);
	}
	
	/**
	 * 
	 * @param parent
	 * @param attributeKey
	 * @param attributeValue
	 */
	public Element addAttribute(Element ele, String attributeKey, String attributeValue){
		return ele.addAttribute(attributeKey, attributeValue);
	}
	
	/**
	 * 
	 * @param parent
	 * @param attributeKey
	 * @return
	 */
	public boolean delAttribute(Element ele, String attributeKey){
		Attribute attribute = ele.attribute(attributeKey);
		return ele.remove(attribute);
	}
	
	/**
	 * 
	 * @param parent
	 * @param attributeKey
	 * @param attributeValue
	 */
	public void modifyAttribute(Element ele, String attributeKey, String attributeValue){
		Attribute attribute = ele.attribute(attributeKey);
		attribute.setText(attributeValue);
	}
	
	/**
	 * 
	 * @param ele
	 * @return
	 */
	public Map<String, Object> getAttributes(Element ele){
		Map<String, Object> attributes = new HashMap<String, Object>();
		for(Iterator<Attribute> it = ele.attributeIterator(); it.hasNext();){
			Attribute attribute = (Attribute) it.next();
			attributes.put(attribute.getName(), attribute.getData());
		}
		return attributes;
	}
	
	/**
	 * 
	 * @param ele
	 * @return
	 */
	public String getString(Node ele){
		return ele.asXML();
	}
	
	/**
	 * 
	 * @param eles
	 * @return
	 */
	public String getString(List<Element> eles){
		StringBuilder sb = new StringBuilder();
		for(Element ele : eles){
			sb.append(ele.asXML());
			sb.append("\r\n");
		}
		return sb.toString();
	}
	
	/**
	 * 
	 */
	public String toString(){
		return xmlDoc.asXML();
	}
	
	 /** 
     * 把document对象写入新的文件 
     *  
     */  
    public void save() {
    	saveAs(sourceFile);
    }  
    
    /** 
     * 把document对象写入新的文件 
     *  
     * @param targetFile 
     */  
    public void saveAs(File targetFile) {  
    	saveAs(xmlDoc, targetFile);
    }
    
    public void saveAs(Document doc, File targetFile) {  
        // 紧凑的格式  
        // OutputFormat format = OutputFormat.createCompactFormat();  
        // 排版缩进的格式  
        OutputFormat format = OutputFormat.createPrettyPrint();  
        // 设置编码  
        format.setEncoding("UTF-8");  
        // 创建XMLWriter对象,指定了写出文件及编码格式  
        // XMLWriter writer = new XMLWriter(new FileWriter(new File("src//a.xml")),format);  
        XMLWriter writer;
		try {
			writer = new XMLWriter(new OutputStreamWriter(new FileOutputStream(targetFile), "UTF-8"), format);
			// 写入  
			writer.write(doc);  
			// 立即写入  
			writer.flush();  
			// 关闭操作  
			writer.close();  
		} catch (IOException e) {
			e.printStackTrace();
		}  
    }
    
    /**
	 * 
	 */
	public static void mergeXML(String mainxlm, String subxml){
		Dom4jXmlHelper mainHelper = new Dom4jXmlHelper(mainxlm);
		Element mainDep = mainHelper.rootElement.element("dependencies");
		List<Element> mainDeps = mainHelper.rootElement.element("dependencies").elements();
		
		Dom4jXmlHelper subHelper = new Dom4jXmlHelper(subxml);
		List<Element> subDeps = subHelper.rootElement.element("dependencies").elements();
		for(int i = 0; i < subDeps.size(); i++){
			String temp_groupId = subDeps.get(i).element("groupId").getTextTrim();
			String temp_artifactId = subDeps.get(i).element("artifactId").getTextTrim();
			String tempKey = temp_groupId + "." + temp_artifactId;
			HashSet<String> set = new HashSet<String>();
			for(int j = 0; j < mainDeps.size(); j++){
				String groupId = mainDeps.get(j).element("groupId").getTextTrim();
				String artifactId = mainDeps.get(j).element("artifactId").getTextTrim();
				set.add(groupId + "." + artifactId);
			}
			if(set.contains(tempKey)){
				continue;
			} else {
				mainHelper.addElement(mainDep, (Node)(subDeps.get(i).clone()));
			}
			mainHelper.save();
		}
	}
	
	public static void main(String[] args) throws JaxenException {
		String mainxlm = "src/xml/pom.xml";
//		String subxml = "src/xml/template_pom.xml";
//		Dom4jXmlHelper.mergeXML(mainxlm, subxml);
		Dom4jXmlHelper help = new Dom4jXmlHelper(mainxlm);
		String path = "/*[name()='project']/*[name()='dependencies']/*[name()='dependency'][@id=1]";
		System.out.println(help.findSingleElement(path));
//		Dom4jXPath xpath = new Dom4jXPath(path);
//		System.out.println(xpath);
//		List<Element> eles = help.getRootElement().element("dependencies").elements();
//		for(Element ele : eles){
//			//System.out.println(ele.getUniquePath());
//			List<Attribute> attrs = ele.attributes();
//			for(Attribute attr : attrs){
//				if(null != attr){
//					String str = attr.getUniquePath();
//					System.out.println(str);
//					System.out.println(attr.getParent().getUniquePath());
//				}
//			}
//		}
//		String text = help.findSingleElement(path).getText();
//		System.out.println(text);
	}
	
}
