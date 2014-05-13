package hu.ypg.tapse;

import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.SwingWorker;

import org.joda.time.DateTime;
import org.junit.runners.ParentRunner;

public class TapseCam extends SwingWorker<Void, Void> {
	
	//logger
	private static Logger LOGGER = Logger.getLogger("InfoLogging");
	
	//Settings handler
	public TapseXMLHandler settingsHandler;
	
	public static boolean checkWindowResult;
	
	/**
	 * Threaded method.
	 */
	@Override
	protected Void doInBackground() throws Exception {
		LOGGER.info("Running background thread...");
		
		checkWindowResult = true;
		
		while(true){
			
			//check for shot
			if(canIShot(true)){
				//take a shot
				settingsHandler.setTimeRemaining(0);
				takeShot();
			}

			//sleep thread
			if(settingsHandler == null){
				LOGGER.info("Settings handler not found!");
				Thread.sleep(1000);
			}else{
				for (int i = 0; i < settingsHandler.getFrequency(); i++) {
					Thread.sleep(1000);
					settingsHandler.setTimeRemaining(settingsHandler.getFrequency() - i);
					//checking window
					checkForWindow(settingsHandler.getProgName());
				}
			}
				
		}
	}
	
	/**
	 * Checks if it is able to shot.
	 * @param checkProg Do check for program name?
	 * @return Returns true if shotting is enabled.
	 */
	public boolean canIShot(boolean checkProg){
		
		//if no settings handler
		if(settingsHandler == null){
			LOGGER.info("Settings handler not found!");
			return false;
		}
		
		//check enabled
		if(!settingsHandler.isEnabled())
			return false;
		
		//check date and time
		if(settingsHandler.isDaysEnabled()){
			DateTime dt = new DateTime();
			
			//check day of week
			int dow = dt.getDayOfWeek();
			if(!settingsHandler.isDayOfWeekEnabed(dow))
				return false;
			
			//check time
			int mid = dt.getMinuteOfDay();
			int from = settingsHandler.getTimeFromH()*60 + settingsHandler.getTimeFromM();
			int to = settingsHandler.getTimeToH()*60 + settingsHandler.getTimeToM();
			
			if(to > from){
				if(mid > to || mid < from)
					return false;
			}else{
				if(mid < from && mid > to)
					return false;
			}
				
		}
		
		//check program
		if(checkProg && settingsHandler.isProgEnabled()){
			String s = settingsHandler.getProgName();	
			if(!checkWindowResult && !s.trim().equals(""))
				return false;
		}
		
		return true;
	}
	
	/**
	 * Checks if window title given exists. Runs in thread.
	 * @param s Input window name.
	 */
	private static void checkForWindow(String s){
		try {
			//param must be final
			final String param = s;
			
			//callable and executor
			ExecutorService executor = Executors.newSingleThreadExecutor();
			Callable<Boolean> callable = new Callable<Boolean>() {
				@Override
			    public Boolean call() {
					try{
						//String line;
					    
						//start process
						Process p = Runtime.getRuntime().exec(System.getenv("windir")+"\\system32\\"+"tasklist.exe /fo csv /nh /v ");
					    
						//attach buffered reader to process
						BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
					    
					    //parse process list
					    /*while ((line = input.readLine()) != null) {
					    	if(line.contains(param)){
					    		return true;
					        }
					    }*/
						boolean x = parseProcessList(input, param);
						
					    //close reader, result false, nothing found
					    input.close();
					    return x;
					    
					}catch(Exception e){
						e.printStackTrace();
						//return true, to keep program multi-platform
						return true;
					}
			    }
			};
		    //execute
			Future<Boolean> future = executor.submit(callable);
		    
		    //get result and set parameter
		    checkWindowResult = future.get();
		    executor.shutdown();
		    
	    }catch(Exception e) {
	        e.printStackTrace();
	    }
	}
	
	/**
	 * Parses process list.
	 * @param input Input stream.
	 * @param param Window title look for.
	 * @return Returns true if title found
	 * @throws IOException If can't read buffer
	 */
	public static boolean parseProcessList(BufferedReader input, String param) throws IOException{
		String line;
		while ((line = input.readLine()) != null) {
	    	if(line.contains(param)){
	    		return true;
	        }
	    }
		return false;
	}
	
	/**
	 * Takes and saves screenshot.
	 */
	private void takeShot(){
		BufferedImage image;
		try {
			//get last shot id
			Integer id = settingsHandler.getLastShotID();
			
			//capture screen
			image = new Robot().createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
			
			//write image
			ImageIO.write(image, "png", new File(settingsHandler.getSaveFolder()+"/shot_"+id.toString()+".png"));
			
			//increase last shot id
			settingsHandler.setLastShotID(id+1);
			
			//play sound effect
			AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File("res/shot.wav").getAbsoluteFile());
	        Clip clip = AudioSystem.getClip();
	        clip.open(audioInputStream);
	        clip.start();

			
			//logging shot taken
			LOGGER.info("Shot id #"+id.toString()+" taken!");
		} catch (Exception e){
			e.printStackTrace();
			LOGGER.info("Unable to take screenshot");
		}
		
	}

}
