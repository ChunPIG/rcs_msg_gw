package com.suntek.rcs.msg.gw.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

public abstract class DOM4jXMLHelper {

	/**
	 * 获取节点文本内容
	 * 
	 * @param node
	 * @return
	 */
	public static String getNodeText(Node node) {
		return ((Element) node).getTextTrim();
	}

	/**
	 * 获取节点的属性值
	 * 
	 * @param node
	 *            节点
	 * @param attributeName
	 *            属性名
	 * @return
	 */
	public static String getNodeAttribute(Node node, String attributeName) {
		Element elem = (Element) node;
		return elem.attributeValue(attributeName);
	}

	/**
	 * 获取节点的属性值
	 * 
	 * @param node
	 *            节点
	 * @param attributeName
	 *            属性名
	 * @return
	 */
	public static int getNodeIntAttribute(Node node, String attributeName) {
		String value = getNodeAttribute(node, attributeName);
		return Integer.parseInt(value);
	}

	/**
	 * 获取节点的属性值
	 * 
	 * @param node
	 *            节点
	 * @param attributeName
	 *            属性名
	 * @return
	 */
	public static boolean getNodeBooleanAttribute(Node node, String attributeName) {
		String value = getNodeAttribute(node, attributeName);
		return Boolean.valueOf(value).booleanValue();
	}

	/**
	 * 创建一个新的xml文档
	 * 
	 * @return
	 */
	public static Document createDocument() {
		return DocumentHelper.createDocument();
	}

	/**
	 * 从xml内容中创建文档
	 * 
	 * @param xmlcontent
	 * @return
	 */
	public static Document getDocument(String xmlcontent) {
		try {
			return DocumentHelper.parseText(xmlcontent);
		} catch (DocumentException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 从xml流中获取xml文档,读取xml流时不验证xml文档是否格式良好的
	 * 
	 * @param xmlstream
	 *            xml源
	 * @return
	 */
	public static Document getDocument(InputStream xmlstream) {
		return getDocument(xmlstream, false);
	}

	/**
	 * 从xml流中获取xml文档
	 * 
	 * @param xmlstream
	 *            xml源
	 * @param validating
	 *            读取xml流时是否需要验证xml文档是否格式良好的
	 * @return
	 */
	public static Document getDocument(InputStream xmlstream, boolean validating) {
		try {
			SAXReader reader = new SAXReader();
			reader.setValidation(validating);
			return reader.read(xmlstream);
		} catch (DocumentException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 从文件中获取xml文档,读取文件时不验证xml文档是否格式良好的
	 * 
	 * @param file
	 *            xml源
	 * @return
	 * @throws Exception
	 */
	public static Document getDocument(File file) throws Exception {
		return getDocument(file, false);
	}

	/**
	 * 从文件中获取xml文档
	 * 
	 * @param file
	 *            xml源
	 * @param validating
	 *            读取文件时是否需要验证xml文档是否格式良好的
	 * @return
	 * @throws Exception
	 */
	public static Document getDocument(File file, boolean validating) throws Exception {
		SAXReader reader = new SAXReader();
		reader.setValidation(validating);
		return reader.read(file);
	}

	/**
	 * 写xml到文件
	 * 
	 * @param doc
	 *            xml文档
	 * @param file
	 *            目标文件
	 * @param encoding
	 *            字符集
	 * @throws Exception
	 */
	public static void toXMLFile(Document doc, File file, String encoding) throws Exception {
		OutputFormat format = OutputFormat.createPrettyPrint();
		format.setEncoding(encoding);
		format.setLineSeparator(getSystemLineSeparator());
		XMLWriter out = new XMLWriter(new BufferedOutputStream(new FileOutputStream(file)), format);
		out.setOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
		out.write(doc);
		out.close();
	}

	/**
	 * 写xml到文件,默认字符集是GBK
	 * 
	 * @param doc
	 *            xml文档
	 * @param file
	 *            目标文件
	 * @throws Exception
	 */
	public static void toXMLFile(Document doc, File file) throws Exception {
		toXMLFile(doc, file, "GBK");
	}

	/**
	 * 利用xpath表达式查找单个节点
	 * 
	 * @param source
	 *            节点源,可以是Document,Node,Element之一
	 * @param expression
	 *            xpath表达式
	 * @return
	 */
	public static Node selectSingleNode(Object source, String expression) {
		if (source instanceof Document) {
			return ((Document) source).selectSingleNode(expression);
		} else if (source instanceof Node) {
			return ((Node) source).selectSingleNode(expression);
		} else if (source instanceof Element) {
			return ((Element) source).selectSingleNode(expression);
		}
		return null;
	}

	/**
	 * 利用xpath表达式查找多个节点
	 * 
	 * @param source
	 *            节点源,可以是Document,Node,Element之一
	 * @param expression
	 *            xpath表达式
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static List selectNodes(Object source, String expression) {
		if (source instanceof Document) {
			return ((Document) source).selectNodes(expression);
		} else if (source instanceof Node) {
			return ((Node) source).selectNodes(expression);
		} else if (source instanceof Element) {
			return ((Element) source).selectNodes(expression);
		}
		return new ArrayList();
	}

	private static String getSystemLineSeparator() {
		String lineSeparator = System.getProperty("line.separator");
		return lineSeparator;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static String convertMapToXml(Map<String, Object> params, int iMode) {
		StringBuffer sb = new StringBuffer();
		if (params == null || params.size() == 0) {
			return "";
		}

		if (0 == iMode) {
			sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
			sb.append("<eRequestService>\n");
		}

		Iterator ite = params.keySet().iterator();

		while (ite.hasNext()) {
			String key = (String) ite.next();
			if (!(params.get(key) instanceof Map)) {
				sb.append("<" + key + ">");
				sb.append(params.get(key));
				sb.append("</" + key + ">\n");
			}
		}

		ite = params.keySet().iterator();
		while (ite.hasNext()) {
			String key = (String) ite.next();
			if (params.get(key) instanceof Map) {
				if (key.indexOf("|!|") > 0) {
					sb.append("<" + key.substring(0, key.indexOf("|!|")) + ">\n");
					sb.append(convertMapToXml((Map) params.get(key), iMode + 1));
					sb.append("</" + key.substring(0, key.indexOf("|!|")) + ">\n");
				} else {
					sb.append("<" + key + ">\n");
					sb.append(convertMapToXml((Map) params.get(key), iMode + 1));
					sb.append("</" + key + ">\n");
				}
			}
		}

		if (0 == iMode)
			sb.append("</eRequestService>\n");
		
		return sb.toString();
	}
}
