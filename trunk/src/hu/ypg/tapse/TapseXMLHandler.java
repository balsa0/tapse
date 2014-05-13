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
	private static String set_saveFolder;
	private static Integer id_lastShot;
	private static boolean[] set_days;
	private static boolean set_daysEnabled;
	private static Integer fromH, fromM;
	private static Integer toH, toM;
	private static String set_prog;
	private static boolean set_progEnabled;
	
	private static Integer val_NextShotRemaining;
	
	
	private static Document doc;
	private static Element rootElement;
	
	//const settings tag name in xml
	private static final String filePath = "res/settings.xml";
	private static final String tr_root = "settings";
	private static final String tr_enabled = "enabled";
	private static final String tr_freq = "freq";
	private static final String tr_saveFolder = "savefolder";
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
		
		//remaining until next shot
		val_NextShotRemaining = 0;
		
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
		
		//log
		LOGGER.info("Reading Settings...");
		
		//getting enabled
		setEnabled(stringToBool(rootElement.getElementsByTagName(tr_enabled).item(0).getTextContent()));
		
		//getting frequency
		setFrequency(Integer.parseInt(rootElement.getElementsByTagName(tr_freq).item(0).getTextContent()));
		
		//getting save folder
		setSaveFolder(rootElement.getElementsByTagName(tr_saveFolder).item(0).getTextContent());
		
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
		setTimeTo(Integer.parseInt(to.getNamedItem("h").getTextContent()),
				  Integer.parseInt(to.getNamedItem("m").getTextContent()));
		
		//get program watching enabled
		setProgEnabled(stringToBool(rootElement.getElementsByTagName(tr_progEnabled).item(0).getTextContent()));
	
		//get name of program to watch
		setProgName(rootElement.getElementsByTagName(tr_prog).item(0).getTextContent());
	}
	
	/**
	 * This method writes changes to the settings XML.
	 */
	public void writeSettings(){
		//log
		LOGGER.info("Writing XML...");
		
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
	public void setFrequency(Integer freq) {
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
	public boolean isEnabled() {
		return set_enabled;
	}

	/**
	 * Sets program to active or passive state
	 * @param enabled Enables or Disables taking shots.
	 */
	public void setEnabled(boolean enabled) {
		//set variable
		set_enabled = enabled;
		
		//write xml
		String r = boolToString(enabled);
		rootElement.getElementsByTagName(tr_enabled).item(0).setTextContent(r);
	}
	
	/**
	 * Returns the shot save folder path.
	 * @return Returns the shot save folder path.
	 */
	public String getSaveFolder(){
		return set_saveFolder;
	}
	
	/**
	 * Sets shot save folder.
	 * @param folder The folder path.
	 * @return returns false if folder not exists.
	 */
	public boolean setSaveFolder(String folder){
		//translate folder path
		folder = translatePath(folder);
		
		//check if folder exists
		if(!checkSaveFolder(folder))
			return false;
		
		//set variable
		set_saveFolder = folder;
		
		//write xml
		rootElement.getElementsByTagName(tr_saveFolder).item(0).setTextContent(folder);
		
		//log
		LOGGER.info("Save folder changed to: "+folder);
		
		//success
		return true;
	}

	/**
	 * Private method to check a specific folder.
	 * @param folder The folder to check.
	 * @return Returns true if folder is OK.
	 */
	private boolean checkSaveFolder(String folder){
		//check if folder exists
		File f = new File(folder);
		if(!f.exists())
			return false;
		else
			return true;
		//return true;
	}
	
	/**
	 * Checks if save folder is ready.
	 * @return Returns true if folder is OK.
	 */
	public boolean checkSaveFolder(){
		return checkSaveFolder(getSaveFolder());
	}

	/**
	 * Returns the ID of the last shot.
	 * @return Returns the ID of the last shot. (ID goes from 0 - )
	 */
	public Integer getLastShotID() {
		return id_lastShot;
	}

	/**
	 * Sets the actual shot id minus one (act - 1)
	 * @param num ID of the last shot.
	 */
	public void setLastShotID(Integer num) {		
		id_lastShot = num;
		
		//write xml
		rootElement.getElementsByTagName(tr_lastID).item(0).setTextContent(id_lastShot.toString());
	}
	
	/**
	 * Returns true if scheduling shots is enabled.
	 * @return Returns true if scheduling shots is enabled.
	 */
	public boolean isDaysEnabled(){
		return set_daysEnabled;
	}
	
	/**
	 * Enables/Disables scheduling shots.
	 * @param enabled True turns scheduling on.
	 */
	public void setDaysEnabled(boolean enabled){
		set_daysEnabled = enabled;
		
		//write xml
		String r = boolToString(enabled);
		rootElement.getElementsByTagName(tr_daysEnabled).item(0).setTextContent(r);
	}
	
	/**
	 * Gets day of week and returns if shotting is enabled that day.
	 * @param dow Day of week (0-6) Begins with Sonday.
	 * @return Retrurns true if taking shots is enabled that day.
	 */
	public boolean isDayOfWeekEnabed(int dow){
		
		//must be between 0 and 6
		if(dow >= set_days.length || dow < 0)
			return false;
		return set_days[dow];
	}
	
	/**
	 * Enables/Disables taking shots on specific day.
	 * @param dow Day of week (0-6) Begins with Sonday.
	 * @param enabled True enables taking shots on the specific day.
	 * @return Returns true on success.
	 */
	public boolean setDayOfWeekEnabled(Integer dow, boolean enabled){
		if(dow >= set_days.length || dow < 0)
			return false;
		set_days[dow] = enabled;
		
		//write xml
		String r = boolToString(enabled);
		NamedNodeMap n = rootElement.getElementsByTagName(tr_days).item(0).getAttributes();
		n.getNamedItem("d"+ dow.toString()).setTextContent(r); ///tesztelni
	
		//success
		return true;
	}
	
	/**
	 * Returns schedule start hour.
	 * @return Returns schedule start hour.
	 */
	public Integer getTimeFromH(){
		return fromH;
	}
	
	/**
	 * Returns schedule start minute.
	 * @return Returns schedule start minute.
	 */
	public Integer getTimeFromM(){
		return fromM;
	}
	
	/**
	 * Returns timefrom as string.
	 * @return Returns timefrom as string
	 */
	public String getTimeFrom(){
		return String.format("%02d", fromH)+":"+String.format("%02d", fromM);
	}
	
	/**
	 * Returns schedule stop hour.
	 * @return Returns schedule stop hour.
	 */
	public Integer getTimeToH(){
		return toH;
	}
	
	/**
	 * Returns schedule stop minute.
	 * @return Returns schedule stop minute.
	 */
	public Integer getTimeToM(){
		return toM;
	}
	
	/**
	 * Returns timeto as string.
	 * @return Returns timeto as string
	 */
	public String getTimeTo(){
		return String.format("%02d", toH)+":"+String.format("%02d", toM);
	}
	
	/**
	 * Sets schedule start time.
	 * @param h Start hour.
	 * @param m Start minute of hour.
	 * @return Returns true on success.
	 */
	public boolean setTimeFrom(Integer h, Integer m){
		if(h < 0 || h > 23 || m < 0 || m > 59)
			return false;
		fromH = h; fromM = m;
		
		//write xml
		NamedNodeMap n = rootElement.getElementsByTagName(tr_from).item(0).getAttributes();
		n.getNamedItem("h").setTextContent(h.toString()); ///tesztelni
		n.getNamedItem("m").setTextContent(m.toString()); ///tesztelni
		
		//success
		return true;
	}
	
	/**
	 * Sets schedule stop time.
	 * @param h Stop hour.
	 * @param m Stop minute of hour.
	 * @return Returns true on success.
	 */
	public boolean setTimeTo(Integer h, Integer m){
		//checking for correct time intervals
		if(h < 0 || h > 23 || m < 0 || m > 59)
			return false;
		
		toH = h; toM = m;
		
		//write xml
		NamedNodeMap n = rootElement.getElementsByTagName(tr_to).item(0).getAttributes();
		n.getNamedItem("h").setTextContent(h.toString()); ///tesztelni
		n.getNamedItem("m").setTextContent(m.toString()); ///tesztelni
		
		//success
		return true;
	}
	
	/**
	 * Returns true if program watching is active.
	 * @return Returns true if program watching is active.
	 */
	public boolean isProgEnabled(){
		return set_progEnabled;
	}
	
	/**
	 * Enables/Disables program watching.
	 * @param enabled True enables program watching.
	 */
	public void setProgEnabled(boolean enabled){
		set_progEnabled = enabled;
		
		//write xml
		String r = boolToString(enabled);
		rootElement.getElementsByTagName(tr_progEnabled).item(0).setTextContent(r);
	}
	
	/**
	 * Returns name of the watched program.
	 * @return Returns name of the watched program.
	 */
	public String getProgName(){
		return set_prog;
	}
	
	/**
	 * Sets name of the watched program.
	 * @param name Name of the watched program.
	 */
	public void setProgName(String name){
		set_prog = name;
		
		//write xml
		rootElement.getElementsByTagName(tr_prog).item(0).setTextContent(name);
	}
	
	/**
	 * Returns time until next shot.
	 * @return Returns time until next shot.
	 */
	public Integer getTimeRemaining(){
		return val_NextShotRemaining;
	}
	
	/**
	 * Sets time until next shot.
	 * @param i Time until next shot.
	 */
	public void setTimeRemaining(Integer i){
		val_NextShotRemaining = i;
	}
	
	/**
	 * Makes a string from a bool.
	 * @param b The bool to convert.
	 * @return "true" or "false" string.
	 */
	public static final String boolToString(boolean b){
		if(b)
			return "true";
		else
			return "false";
	}
	
	/**
	 * Parses a string, converts it to bool.
	 * @param s The string to parse.
	 * @return Returns the bool parsed from string.
	 */
	public static final boolean stringToBool(String s){
		s = s.toLowerCase().trim();
		if( s.equals("") || s.equals("true") ){
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * Translates a path to unix format.
	 * @param s The path to translate
	 * @return The translated path.
	 */
	public static final String translatePath(String s){
		return s.replace("\\\\", "/").replace("\\", "/");
	}
	
}
