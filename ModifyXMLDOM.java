package com.schwab;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class ModifyXMLDOM {

	public static void main(String[] args) throws XPathExpressionException {
		// String filePath = "D:\\CS\\Forms\\APP\\employee.xdp";
		String filePath = "D:\\CS\\Forms\\APP\\APP.xdp";
		File xmlFile = new File(filePath);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		try {
			dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(xmlFile);
			doc.getDocumentElement().normalize();
			addElement(doc);

			doc.getDocumentElement().normalize();
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File("D:\\CS\\Forms\\APP\\APP.xdp"));
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.transform(source, result);
			System.out.println("XML file updated successfully");

		} catch (SAXException | ParserConfigurationException | IOException | TransformerException e1) {
			e1.printStackTrace();
		}
	}

	@SuppressWarnings("null")
	private static void addElement(Document doc)
			throws XPathExpressionException, ParserConfigurationException, SAXException, IOException {

		String colorXML = "<?xml version=\"1.0\" encoding=\"utf-8\"?><border><fill presence=\"visible\"><color value=\"0,255,255\"/><pattern type=\"crossDiagonal\"><color value=\"153,204,255\"/></pattern></fill></border> ";

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document document = builder.parse(new InputSource(new StringReader(colorXML)));

		NodeList employees = doc.getElementsByTagName("subform");

		XPathFactory xpathFactory = XPathFactory.newInstance();

		XPath xpath = xpathFactory.newXPath();
		XPathExpression expr = xpath.compile("//subform/@usehref");
		NodeList nodes = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
		Element emp;
		System.out.println("count " + employees.getLength());
		for (int i = 0; i < employees.getLength(); i++) {
			if (employees.item(i).getAttributes().item(0).getNodeName() == "usehref") {
				emp = (Element) employees.item(i);
				emp.appendChild(doc.importNode(document.getDocumentElement(), true));
			}
		}
	}

	private static void deleteElement(Document doc) {
		NodeList employees = doc.getElementsByTagName("Employee");
		Element emp = null;
		// loop for each employee
		for (int i = 0; i < employees.getLength(); i++) {
			emp = (Element) employees.item(i);
			Node genderNode = emp.getElementsByTagName("gender").item(0);
			emp.removeChild(genderNode);
		}

	}
}
