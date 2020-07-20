package com.schwab;

import static javax.xml.xpath.XPathConstants.NODE;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
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
import org.w3c.dom.Text;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class HighlightFragment {

	public static void main(String[] args) throws TransformerConfigurationException, TransformerException,
			TransformerFactoryConfigurationError, XPathExpressionException, SAXException, IOException, ParserConfigurationException {
		// TODO Auto-generated method stub
		String colorXML = "<?xml version=\"1.0\" encoding=\"utf-8\"?><border><fill presence=\"visible\"><color value=\"0,255,255\"/><pattern type=\"crossDiagonal\"><color value=\"153,204,255\"/></pattern></fill></border> ";

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();  
		DocumentBuilder builder = factory.newDocumentBuilder();  
		Document document, doc;
		doc = builder.parse(new InputSource(new StringReader(colorXML)));
	    Element root = doc.getDocumentElement();

System.out.println(root.getNodeName());
		
		
		File location = new File("D:\\CS\\Forms\\APP");
		factory = DocumentBuilderFactory.newInstance();
		builder = factory.newDocumentBuilder();

		for (File f : location.listFiles()) {
			if (f.isFile() && f.getName().endsWith(".xdp")) {
				document = builder.parse(new File(f.getAbsolutePath()));
				XPathFactory xpathFactory = XPathFactory.newInstance();

				XPath xpath = xpathFactory.newXPath();
				XPathExpression expr = xpath.compile("//subform/@usehref");
				NodeList nodes = (NodeList) expr.evaluate(document, XPathConstants.NODESET);


				System.out.println(f.getName() + " and Count is " + nodes.item(0));

				for(int i=0;i<nodes.getLength();i++) {
					
					
					//Node newNode =  doc.importNode(document.getDocumentElement() , true); //Need to import prior to appending it
					Element e = (Element)nodes.item(i);
					//doc.getDocumentElement().appendChild(nodes.item(i));
					e.appendChild(document.importNode(root, true));
					//nodes.item(i).getParentNode().insertBefore(doc, nodes.item(i));

				}
				Transformer transformer = TransformerFactory.newInstance().newTransformer();
				transformer.setOutputProperty(OutputKeys.INDENT, "yes");

				StreamResult result = new StreamResult(new StringWriter());
				DOMSource source = new DOMSource(document);
				transformer.transform(source, result);

				String xmlOutput = result.getWriter().toString();
				System.out.println(xmlOutput);
		        

			}

		}

	}

	private static List<String> evaluateXPath(Document document, String xpathExpression) throws Exception {
		XPathFactory xpathFactory = XPathFactory.newInstance();

		XPath xpath = xpathFactory.newXPath();

		List<String> values = new ArrayList<>();
		try {
			// Create XPathExpression object
			XPathExpression expr = xpath.compile(xpathExpression);

			// Evaluate expression result on XML document
			NodeList nodes = (NodeList) expr.evaluate(document, XPathConstants.NODESET);

			for (int i = 0; i < nodes.getLength(); i++) {
				values.add(nodes.item(i).getNodeValue());
			}

		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}
		return values;
	}

}
