package hu.ypg.tapse;

import java.io.File;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

public class TapseXMLHandler {
	
	//logger
	private static Logger LOGGER = Logger.getLogger("InfoLogging");
	
	//settings
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
	
	//const settings tag name in xml
	private static final String filePath = "res/settings.xml";
	private static final String tr_root = "settings";
	private static final String tr_enabled = "enabled";
	private static final String tr_freq = "freq";
	private static final String tr_lastID = "lastshotid";
	private static final String tr_daysEnabled = "daysenabled";
	private static final String tr_days = "days";
	private static final String tr_from = "from";
	private static final String tr_to = "to";
	private static final String tr_progEnabled = "progenabled";
	private static final String tr_prog = "prog";
	
	/**
	 * Class constructor, initializes settings and starts xml parsing.
	 */
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
		initialize();
	}
	
	/**
	 * This method initializes XML parsing.
	 */
	public void initialize(){
		
		LOGGER.info("Reading XML...");
		
		try{
			
			//load xml file
			File fXmlFile = new File(filePath);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			doc = dBuilder.parse(fXmlFile);
			
			doc.getDocumentElement().normalize();
			rootElement = doc.getDocumentElement();
			String rootE = rootElement.getNodeName();
			
			//logging root element
			LOGGER.info("XML Root element found: "+rootE);
		
			//check root element
			assert rootE == tr_root;
			
			//read settings
			readSettings();
			
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	
	
	/**
	 * This method reads settings from 'settings.xml'.
	 */
	private void readSettings(){
		
		//getting enabled
		setEnabled(stringToBool(rootElement.getElementsByTagName(tr_enabled).item(0).getTextContent()));
		
		//getting frequency
		setFrequency(Integer.parseInt(rootElement.getElementsByTagName(tr_freq).item(0).getTextContent()));
		
		//getting last shot id
		setLastShotID(Integer.parseInt(rootElement.getElementsByTagName(tr_lastID).item(0).getTextContent()));
	
		//get days enabled
		setDaysEnabled(stringToBool(rootElement.getElementsByTagName(tr_daysEnabled).item(0).getTextContent()));
		
		//get days of week enabled
		NamedNodeMap dow = rootElement.getElementsByTagName(tr_days).item(0).getAttributes();
		for (Integer i = 0; i < 7; i++){
			String s = dow.getNamedItem("d"+i.toString()).getTextContent();
			setDayOfWeekEnabled(i, stringToBool(s));
		}
		
		//get time "from"
		NamedNodeMap from = rootElement.getElementsByTagName(tr_from).item(0).getAttributes();
		setTimeFrom(Integer.parseInt(from.getNamedItem("h").getTextContent()),
					Integer.parseInt(from.getNamedItem("m").getTextContent()));
		
		//get time "to"
		NamedNodeMap to = rootElement.getElementsByTagName(tr_to).item(0).getAttributes();
		setTimeFrom(Integer.parseInt(to.getNamedItem("h").getTextContent()),
					Integer.parseInt(to.getNamedItem("m").getTextContent()));
		
		//get program watching enabled
		setProgEnabled(stringToBool(rootElement.getElementsByTagName(tr_progEnabled).item(0).getTextContent()));
	
		//get name of program to watch
		setProgName(rootElement.getElementsByTagName(tr_prog).item(0).getTextContent());
	}
	
	/**
	 * This method writes changes to the settings XML.
	 */
	public static void settingsWriter(){
		
		//writing setting
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
	
	/**
	 * Returns shot frequency.
	 * @return Returns shot frequency (in seconds)
	 */
	public Integer getFrequency() {
		return set_freq;
	}

	/**
	 * Sets shot frequency.
	 * @param freq Shot frequency in seconds.
	 */
	public static void setFrequency(Integer freq) {
		//freqency can't be smaller than '1'
		if(freq < 1)
			freq = 1;
		set_freq = freq;
		
		//write xml
		rootElement.getElementsByTagName(tr_freq).item(0).setTextContent(set_freq.toString());
	}

	/**
	 * Returns true if taking shots is allowed.
	 * @return Returns true if taking shots is allowed.
	 */
	public static boolean isEnabled() {
		return set_enabled;
	}

	/**
	 * Sets program to active or passive state
	 * @param enabled Enables or Disables taking shots.
	 */
	public static void setEnabled(boolean enabled) {
		//set variable
		set_enabled = enabled;
		
		//write xml
		String r = boolToString(enabled);
		rootElement.getElementsByTagName(tr_enabled).item(0).setTextContent(r);
	}

	/**
	 * Returns the ID of the last shot.
	 * @return Returns the ID of the last shot. (ID goes from 0 - )
	 */
	public static Integer getLastShotID() {
		return id_lastShot;
	}

	/**
	 * Sets the actual shot id minus one (act - 1)
	 * @param num ID of the last shot.
	 */
	public static void setLastShotID(Integer num) {		
		id_lastShot = num;
		
		//write xml
		rootElement.getElementsByTagName(tr_lastID).item(0).setTextContent(id_lastShot.toString());
	}
	
	/**
	 * Returns true if scheduling shots is enabled.
	 * @return Returns true if scheduling shots is enabled.
	 */
	public static boolean isDaysEnabled(){
		return set_daysEnabled;
	}
	
	/**
	 * Enables/Disables scheduling shots.
	 * @param enabled True turns scheduling on.
	 */
	public static void setDaysEnabled(boolean enabled){
		set_daysEnabled = enabled;
	}
	
	/**
	 * Gets day of week and returns if shotting is enabled that day.
	 * @param dow Day of week (0-6) Begins with Sonday.
	 * @return Retrurns true if taking shots is enabled that day.
	 */
	public static boolean isDayOfWeekEnabed(int dow){
		
		//must be between 0 and 6
		if(dow >= set_days.length || dow < 0)
			return false;
		return set_days[dow];
	}
	
	/**
	 * Enables/Disables taking shots on specific day.
	 * @param dow Day of week (0-6) Begins with Sonday.
	 * @param enabled True enables taking shots on the specific day.
	 */
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
	
	//bool to string converter
	public static final String boolToString(boolean b){
		if(b)
			return "true";
		else
			return "false";
	}
	
	public static final boolean stringToBool(String s){
		s = s.toLowerCase().trim();
		if( s.equals("") || s.equals("true") ){
			return true;
		}else{
			return false;
		}
	}
	
}
