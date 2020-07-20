package com.schwab;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class ExtractNodes {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		System.out.println("start Program execution." + System.getProperty("user.dir"));

		/*
		 * THis was before JDK7
		 * 
		 * InputStream is = new
		 * FileInputStream("D:\\CS\\Forms\\APP\\LD12 - APP10149 - Beneficiary Design Form for IRA and 403b.xdp"
		 * ); BufferedReader buf = new BufferedReader(new InputStreamReader(is)); String
		 * line = buf.readLine(); StringBuilder sb = new StringBuilder(); while(line !=
		 * null) { sb.append(line).append("\n"); line = buf.readLine(); } String
		 * fileAsString = sb.toString(); System.out.println("Contents :"+fileAsString);
		 */

		String contents = null;

		/*
		 * using REGEX File location = new File("D:\\CS\\Forms\\APP"); if
		 * (location.isDirectory() && location != null) { for (File f :
		 * location.listFiles()) { if (f.isFile() && f.getName().endsWith(".xdp")) {
		 * System.out.println(f.getAbsolutePath()); contents = new
		 * String(Files.readAllBytes(Paths.get(f.getAbsolutePath())),
		 * StandardCharsets.UTF_8); System.out.println("For File " + f.getName());
		 * List<String> allMatches = new ArrayList<String>(); Matcher m =
		 * Pattern.compile("[^\\\\]*\\.xdp").matcher(contents); while (m.find()) {
		 * allMatches.add(m.group()); System.out.println(m.group()); } } } }
		 */

		// Using XML DOM parsers

		HashMap<String, Object> formInfo;
		List<HashMap<String, Object>> listFormInfo = new ArrayList<HashMap<String, Object>>();
		List<HashMap<String, Object>> fragmentListInfo = new ArrayList<HashMap<String, Object>>();

		//List<String> fragmentList = new ArrayList<String>();
		HashSet<String>  fragmentList = new HashSet<String>();

		File location = new File("D:\\CS\\Forms\\APP");
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();

		File folder = new File("D:\\CS\\PROD Forms\\APP");
		List<String> prodForms = new ArrayList<String>();
		Collections.addAll(prodForms, folder.list());
		System.out.println(prodForms);
		if (location.isDirectory() && location != null) {
			for (File f : location.listFiles()) {
				if (f.isFile() && f.getName().endsWith(".xdp")) {
					System.out.println(f.getAbsolutePath());
					formInfo = new HashMap<String, Object>();
					Document document = builder.parse(new File(f.getAbsolutePath()));
					formInfo.put("formTitle",
							evaluateXPath(document, "//desc/text[contains(@name,'title')]/text()").get(0));
					System.out.println("\nForm Number : "
							+ evaluateXPath(document, "//desc/text[contains(@name,'description')]/text()").get(0));

					formInfo.put("formNumber",
							evaluateXPath(document, "//desc/text[contains(@name,'description')]/text()").get(0));

					System.out.println("\nFragments :");
					formInfo.put("fragments", getfragmentNames(evaluateXPath(document, "//subform/@usehref"), true));
					fragmentList.addAll(getfragmentNames(evaluateXPath(document, "//subform/@usehref"), false));

					System.out.println("\nTotal Count : " + evaluateXPath(document, "//subform/@usehref").size());
					formInfo.put("fragmentCount", evaluateXPath(document, "//subform/@usehref").size());

					System.out.println("\ntitle : "
							+ evaluateXPath(document, "//desc/text[contains(@name,'title')]/text()").get(0));
					if (prodForms.contains(f.getName()))
						formInfo.put("status", "Deployed");
					else
						formInfo.put("status", "Not Deployed");

					listFormInfo.add(formInfo);

				}
				System.out.println("\n");

			}

			// System.out.println();
			/*
			 * contents = new String( Files.readAllBytes(Paths.
			 * get("D:\\CS\\Forms\\APP\\LD12 - APP13051 - Attorney_in_Fact.xdp")),
			 * StandardCharsets.UTF_8); System.out.println(contents);
			 * System.out.println("matcher 2"); List<String> allMatches = new
			 * ArrayList<String>(); Matcher m =
			 * Pattern.compile("[^\\\\]*\\.xdp").matcher(contents); //
			 * 
			 * while (m.find()) { allMatches.add(m.group()); System.out.println(m.group());
			 * }
			 */
			// System.out.println("matcher 2"+allMatches.);
		}
		//fragmentList = fragmentList.stream().distinct().collect(Collectors.toList());
		System.out.println("fragment list " + fragmentList);

		
		Set<String> prodFragmentList = new HashSet<String>();
		
		for (String form : prodForms) {
			File f = new File(folder.getAbsoluteFile()+"\\"+form);
			if (f.isFile()) {
				System.out.println(f.getAbsolutePath());
				formInfo = new HashMap<String, Object>();
				Document document = builder.parse(f);
				prodFragmentList.addAll(getfragmentNames(evaluateXPath(document, "//subform/@usehref"), false));
			}
			System.out.println("\n");
		}
		System.out.println("prodFragList "+prodFragmentList);
		
		
		
		System.out.println("fragment list " + fragmentList);

		List<HashMap<String, Object>> formList;

		for (String frag : fragmentList) {
			formList = new ArrayList<HashMap<String, Object>>();
			formInfo = new HashMap<String, Object>();
			for (HashMap<String, Object> form : listFormInfo) {
				formInfo = new HashMap<String, Object>();
				if (form.get("fragments").toString().contains(frag)) {
					formInfo.put("id", form.get("formNumber"));
					formInfo.put("formTitle", form.get("formTitle"));
					formList.add(formInfo);
				}

			}
			formInfo.clear();

			formInfo.put("fragmentName", frag);
			formInfo.put("formInfo", formList);

			fragmentListInfo.add(formInfo);
		}
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		// String json = new Gson().toJson(listFormInfo);
		// System.out.println("Forms \n" + json);
		// HasMap<Object> finalList = new ArrayList<>();
		formInfo = new HashMap<String, Object>();
		formInfo.put("Forms", listFormInfo);
		formInfo.put("Fragments", fragmentListInfo);
		System.out.println("Fragments \n" + gson.toJson(formInfo));
		// String jsonStr = JSONArray.toJSONString(list);
		try (PrintWriter out = new PrintWriter("output.json")) {
		    out.println(gson.toJson(formInfo));
		}

	}

	private static HashSet<String> getFragmentsInfo() {
		HashSet<String> fragmentList = new HashSet<>();

		return fragmentList;
	}

	private static List<String> getfragmentNames(List<String> nodes, boolean getCount) {
		HashSet<String> hSetNumbers = new HashSet();
		ArrayList<String> fragments = new ArrayList<String>();
		for (String country : nodes)
			if (getCount) {
				hSetNumbers.add(country.split("#")[0].replace("..\\lib\\fragments\\", "") + " : "
						+ Collections.frequency(nodes, country));
			} else {
				hSetNumbers.add(country.split("#")[0].replace("..\\lib\\fragments\\", ""));
			}
		for (String strNumber : hSetNumbers) {
			System.out.println("\t" + strNumber);
			fragments.add(strNumber);
		}
		return fragments;
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
