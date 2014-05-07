package hu.ypg.tapse;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class TapseXMLHandler {
	private static boolean set_enabled;
	private static Integer set_freq;
	private static Integer id_lastShot;
	
	private static Document doc;
	private static Element rootElement;
	
	//const settings params
	private static final String filePath = "res/settings.xml";
	private static final String tr_root = "settings";
	private static final String tr_enabled = "enabled";
	private static final String tr_freq = "freq";
	private static final String tr_lastID = "lastshotid";
	
	public TapseXMLHandler(){
		init();
	}
	
	public void init(){
		try{
			
			File fXmlFile = new File(filePath);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			doc = dBuilder.parse(fXmlFile);
		 
			doc.getDocumentElement().normalize();
			rootElement = doc.getDocumentElement();
			String rootE = rootElement.getNodeName();
			System.out.println("Root element :" + rootE);
		
			//check root element
			assert rootE == tr_root;
			//read settings
			readSettings();
			
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	
	private void readSettings(){
		
		//getting enabled
		String e = rootElement.getElementsByTagName(tr_enabled).item(0).getTextContent().toLowerCase().trim();
		
		if( e.equals("") || e.equals("true") ){
			set_enabled(true);
		}else{
			set_enabled(false);
		}
		
		//getting frequency
		set_frequency(Integer.parseInt(rootElement.getElementsByTagName(tr_freq).item(0).getTextContent()));
		
		//getting last shot id
		set_lastShotID(Integer.parseInt(rootElement.getElementsByTagName(tr_lastID).item(0).getTextContent()));
	}

	
	//setters and getters
	public Integer get_frequency() {
		return set_freq;
	}

	public static void set_frequency(Integer set_freq) {
		//freqency can't be smaller than '1'
		if(set_freq < 1)
			set_freq = 1;
		TapseXMLHandler.set_freq = set_freq;
		
		//write xml
		rootElement.getElementsByTagName(tr_freq).item(0).setTextContent(set_freq.toString());
	}

	public static boolean is_enabled() {
		return set_enabled;
	}

	public static void set_enabled(boolean set_enabled) {
		//set variable
		TapseXMLHandler.set_enabled = set_enabled;
		
		//write xml
		String r;
		if(set_enabled)
			r = "true";
		else
			r = "false";
		
		rootElement.getElementsByTagName(tr_enabled).item(0).setTextContent(r);
	}
	
	public static void settingsWriter(){
		try{
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(filePath));
			transformer.transform(source, result);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public static Integer get_lastShotID() {
		return id_lastShot;
	}

	public static void set_lastShotID(Integer id_lastShot) {		
		TapseXMLHandler.id_lastShot = id_lastShot;
		
		//write xml
		rootElement.getElementsByTagName(tr_lastID).item(0).setTextContent(id_lastShot.toString());
	}
}
