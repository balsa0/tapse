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
	private static boolean[] set_days;
	private static boolean set_daysEnabled;
	private static Integer fromH, fromM;
	private static Integer toH, toM;
	private static String set_prog;
	private static boolean set_progEnabled;
	
	
	private static Document doc;
	private static Element rootElement;
	
	//const settings params
	private static final String filePath = "res/settings.xml";
	private static final String tr_root = "settings";
	private static final String tr_enabled = "enabled";
	private static final String tr_freq = "freq";
	private static final String tr_lastID = "lastshotid";
	
	public TapseXMLHandler(){
		//initialize bool array
		set_days = new boolean[7];
		
		//initialize variables
		set_enabled = true;
		set_freq = 5;
		id_lastShot = 0;
		set_daysEnabled = false;
		fromH = 0; fromM = 0;
		toH = 0; toM = 0;
		set_prog = "None";
		set_progEnabled = false;
		
		//days array fill
		for(int i = 0; i < 7; i++){
			set_days[i] = true;
		}
		
		//initialize xml and variables
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
	
	
	//settings reader
	private void readSettings(){
		
		//getting enabled
		String e = rootElement.getElementsByTagName(tr_enabled).item(0).getTextContent().toLowerCase().trim();
		
		if( e.equals("") || e.equals("true") ){
			setEnabled(true);
		}else{
			setEnabled(false);
		}
		
		//getting frequency
		setFrequency(Integer.parseInt(rootElement.getElementsByTagName(tr_freq).item(0).getTextContent()));
		
		//getting last shot id
		setLastShotID(Integer.parseInt(rootElement.getElementsByTagName(tr_lastID).item(0).getTextContent()));
	
		//get days enabled
		
	}
	
	//setting writer
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

	//setters and getters
	public Integer getFrequency() {
		return set_freq;
	}

	public static void setFrequency(Integer freq) {
		//freqency can't be smaller than '1'
		if(freq < 1)
			freq = 1;
		set_freq = freq;
		
		//write xml
		rootElement.getElementsByTagName(tr_freq).item(0).setTextContent(set_freq.toString());
	}

	public static boolean isEnabled() {
		return set_enabled;
	}

	public static void setEnabled(boolean enabled) {
		//set variable
		set_enabled = enabled;
		
		//write xml
		String r;
		if(enabled)
			r = "true";
		else
			r = "false";
		
		rootElement.getElementsByTagName(tr_enabled).item(0).setTextContent(r);
	}

	public static Integer getLastShotID() {
		return id_lastShot;
	}

	public static void setLastShotID(Integer num) {		
		id_lastShot = num;
		
		//write xml
		rootElement.getElementsByTagName(tr_lastID).item(0).setTextContent(id_lastShot.toString());
	}
	
	public static boolean isDaysEnabled(){
		return set_daysEnabled;
	}
	
	public static void setDaysEnabled(boolean enabled){
		set_daysEnabled = enabled;
	}
	
	public static boolean isDayOfWeekEnabed(int dow){
		//must be between 0 and 6
		if(dow >= set_days.length || dow < 0)
			return false;
		return set_days[dow];
	}
	
	public static void setDayOfWeekEnabled(int dow, boolean enabled){
		if(dow >= set_days.length || dow < 0)
			return;
		set_days[dow] = enabled;
	}
	
	public static Integer getTimeFromH(){
		return fromH;
	}
	
	public static Integer getTimeFromM(){
		return fromM;
	}
	
	public static Integer getTimeToH(){
		return toH;
	}
	
	public static Integer getTimeToM(){
		return toM;
	}
	
	public static void setTimeFrom(Integer h, Integer m){
		if(h < 0 || h > 23 || m < 0 || m > 59)
			return;
		toH = h; toM = m;
	}
	
	public static void setTimeTo(Integer h, Integer m){
		if(h < 0 || h > 23 || m < 0 || m > 59)
			return;
		fromH = h; fromM = m;
	}
	
	public static boolean isProgEnabled(){
		return set_progEnabled;
	}
	
	public static void setProgEnabled(boolean enabled){
		set_progEnabled = enabled;
	}
	
	public static String getProgName(){
		return set_prog;
	}
	
	public static void setProgName(String name){
		set_prog = name;
	}
	
}
