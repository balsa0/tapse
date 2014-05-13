package hu.ypg.tapse;

import java.awt.EventQueue;

import javax.security.auth.x500.X500Principal;
import javax.swing.JFrame;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.Window.Type;

import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField.AbstractFormatter;
import javax.swing.JFormattedTextField.AbstractFormatterFactory;
import javax.swing.JLabel;
import javax.swing.JCheckBox;
import javax.swing.JSpinner;
import javax.swing.Timer;

import net.miginfocom.swing.MigLayout;

import java.awt.Image;
import java.awt.SystemTray;
import java.awt.TrayIcon;

import javax.swing.ImageIcon;
import javax.swing.JButton;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.SystemColor;
import java.io.File;
import java.text.Format;
import java.util.logging.Logger;

import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import javax.swing.text.MaskFormatter;
import javax.swing.JSeparator;
import javax.swing.JToggleButton;

import java.awt.Font;

import javax.swing.UIManager;
import javax.swing.SwingConstants;
import javax.swing.JFormattedTextField;
import javax.swing.JTextField;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class TapseSettings {

	//logger
	private static Logger LOGGER = Logger.getLogger("InfoLogging");
	
	//class
	private JFrame frmTapseSettings;
	
	//xml settings handler 
	private static TapseXMLHandler settingsHandler = new TapseXMLHandler();
	
	//camera module
	private static TapseCam camModule = new TapseCam();
	
	//the main window
	private static final TapseSettings window = new TapseSettings();
	
	//tray icon
	private static final TrayIcon trayIcon = new TrayIcon(getImage("res/tapse.png"),"Tapse");
	
	//components
	private static JLabel l_enableTapse;
	private static JCheckBox w_enableTapse;
	private static JSpinner w_freqSpin;
	private static JLabel w_saveFolderDisp;
	private static JLabel w_numberOfShotsDisp;
	private static JCheckBox w_enableSchedule;
	private static JToggleButton w_days_0;
	private static JToggleButton w_days_1;
	private static JToggleButton w_days_2;
	private static JToggleButton w_days_3;
	private static JToggleButton w_days_4;
	private static JToggleButton w_days_5;
	private static JToggleButton w_days_6;
	private static JFormattedTextField w_fromTime;
	private static JFormattedTextField w_toTime;
	private static JCheckBox w_enableAppWatch;
	private static JTextField w_appWatch;
	private static JLabel l_appWatch;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					//create widow
					//final TapseSettings window = new TapseSettings();
					window.frmTapseSettings.setVisible(true);
				
					//traybar
					initializeTray();

					//timer displays remaining time
					Timer timer = new Timer(1000, new ActionListener() {
					    public void actionPerformed(ActionEvent evt) {
					    	//if label exists
					    	if(l_enableTapse != null){
					    		
					    		//if shooting is enabled
					    		if(camModule.canIShot(false)){
					    			//enabled
					    			l_enableTapse.setText("Enable Tapse ("+
											settingsHandler.getTimeRemaining().toString()+"s until next shot)");
					    			l_enableTapse.setForeground(Color.black);
					    		}else{	
					    			//disabled
					    			l_enableTapse.setText("Enable Tapse (Idle mode)");
					    			l_enableTapse.setForeground(Color.gray);
					    		}
					    		
					    		//is watched app running
								if(settingsHandler.isProgEnabled()){
									if(camModule.checkWindowResult){
										l_appWatch.setText("Watch for application (Found!)");
										l_appWatch.setForeground(Color.blue);
									}else{
										l_appWatch.setText("Watch for application (Not found!)");
										l_appWatch.setForeground(Color.red);
									}
								}else{
									l_appWatch.setText("Watch for application");
									l_appWatch.setForeground(Color.black);
								}
					    		
					    		//if one seconds left before shooting
					    		if(settingsHandler.getTimeRemaining()== 1){
					    			//update form
					    			updateForm();
					    		}
					    	}	
					    }    
					});
					
					timer.start();
					
				} catch (Exception e) {
					e.printStackTrace();
				}				
			}

		});
	}
	
	/**
	 * Create the application.
	 */
	public TapseSettings() {
		//initialize window
		initialize();
		
		//update form
		updateForm();
		
		//camera module set settings handler
		camModule.settingsHandler = settingsHandler;
		
		//start camera module
		camModule.execute();
	}
	
	/**
	 * Initializes tray and tray menu.
	 */
	private static void initializeTray(){
		//system tray
		if (SystemTray.isSupported()) {
			 //new menu
			 final PopupMenu popMenu= new PopupMenu();
			 
			 //adding actions
			 //settings
			 MenuItem item1 = new MenuItem("Settings");
			 item1.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					showHide();
				}
			 });
			 popMenu.add(item1);
			 
			 //exit
			 MenuItem item2 = new MenuItem("Exit");
			 item2.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					exitApp();
				}
			 });
			 popMenu.add(item2);
			 
			 //traybar icon
			 SystemTray systemTray = SystemTray.getSystemTray();
	         trayIcon.setImageAutoSize(true);
	         trayIcon.setPopupMenu(popMenu);
	         
	         MouseAdapter mouseAdapter = new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                	if(e.getClickCount() == 2)
                		showHide();
                	else
                		super.mouseClicked(e);
                }
            };
	        
            trayIcon.addMouseListener(mouseAdapter);
 
            try {
                systemTray.add(trayIcon);
                trayIcon.displayMessage("Tapse",
                "Tapse has been started!",
                TrayIcon.MessageType.INFO);
            } catch (Exception e) {
                e.printStackTrace();
            }
		}
	}
	
	/**
	 * Reads image given in path.
	 * @param Path Path to image
	 * @return Returns image.
	 */
	public static Image getImage(String path) {
        ImageIcon icon = new ImageIcon(path, "omt");
        return icon.getImage();
    }

	/**
	 * Initialize the contents of the frame.
	 * @wbp.parser.entryPoint
	 */
	private void initialize() {
		frmTapseSettings = new JFrame();
		frmTapseSettings.setResizable(false);
		frmTapseSettings.addWindowListener(new WindowAdapter() {
			@Override
			//x gomb megnyomva
			public void windowClosing(WindowEvent e) {
				showHide();
				trayIcon.displayMessage("Tapse",
		                "Tapse has benn minimized to tray...",
		                TrayIcon.MessageType.INFO);
			}
		});
		frmTapseSettings.setType(Type.UTILITY);
		frmTapseSettings.setIconImage(getImage("res/tapse.png"));
		frmTapseSettings.getContentPane().setBackground(UIManager.getColor("Button.background"));
		frmTapseSettings.getContentPane().setLayout(new MigLayout("", "[542px,grow]", "[][][][][][][][][][][][][][]"));
		
		JLabel l_tapseLogo = new JLabel("Tapse Settings");
		l_tapseLogo.setIcon(new ImageIcon("res/tapse_sm.png"));
		frmTapseSettings.getContentPane().add(l_tapseLogo, "flowx,cell 0 0,growx");
		
		l_enableTapse = new JLabel("Enable Tapse");
		frmTapseSettings.getContentPane().add(l_enableTapse, "flowx,cell 0 1,growx");
		
		w_enableTapse = new JCheckBox("Enabled");
		w_enableTapse.setFont(new Font("Tahoma", Font.PLAIN, 11));
		w_enableTapse.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				settingsHandler.setEnabled(w_enableTapse.isSelected());
			}
		});
		w_enableTapse.setBackground(SystemColor.control);
		frmTapseSettings.getContentPane().add(w_enableTapse, "cell 0 1");
		
		JLabel l_shotFrequency = new JLabel("Shot frequency");
		frmTapseSettings.getContentPane().add(l_shotFrequency, "flowx,cell 0 2,growx");
		
		w_freqSpin = new JSpinner();
		w_freqSpin.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				//frequency changed
				try{
					settingsHandler.setFrequency((Integer) w_freqSpin.getValue());
					//restart cam module
					/*camModule.cancel(true);
					camModule.execute();*/
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		});
		
		w_freqSpin.setBackground(new Color(255, 255, 255));
		frmTapseSettings.getContentPane().add(w_freqSpin, "cell 0 2,growx");
		
		JLabel l_seconds = new JLabel("seconds");
		frmTapseSettings.getContentPane().add(l_seconds, "cell 0 2,growx");
		
		JSeparator separator = new JSeparator();
		separator.setForeground(Color.LIGHT_GRAY);
		frmTapseSettings.getContentPane().add(separator, "cell 0 4,growx");
		
		JLabel l_saveFolder = new JLabel("Save folder:");
		frmTapseSettings.getContentPane().add(l_saveFolder, "flowx,cell 0 5");
		
		w_numberOfShotsDisp = new JLabel("Number of Shots:");
		frmTapseSettings.getContentPane().add(w_numberOfShotsDisp, "flowx,cell 0 6,growx");
		
		JButton w_openFolder = new JButton("Open Folder");
		w_openFolder.setBackground(SystemColor.controlHighlight);
		w_openFolder.addActionListener(new ActionListener() {
			//open shots save folder in explorer
			public void actionPerformed(ActionEvent arg0) {
				Desktop desktop = Desktop.getDesktop();
		        File dirToOpen = null;
		        try {
		            dirToOpen = new File(settingsHandler.getSaveFolder());
		            desktop.open(dirToOpen);
		        }catch (Exception e) {
		            e.printStackTrace();
		        }
			}
		});
		
		JButton w_resetCounter = new JButton("Reset");
		w_resetCounter.setBackground(SystemColor.controlHighlight);
		w_resetCounter.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				//reset shots counter
				LOGGER.info("Counter has been reset!");
				settingsHandler.setLastShotID(0);
				updateForm();
			}
		});
		frmTapseSettings.getContentPane().add(w_resetCounter, "cell 0 6");
		frmTapseSettings.getContentPane().add(w_openFolder, "cell 0 6");
		
		w_saveFolderDisp = new JLabel("Loading...");
		w_saveFolderDisp.setHorizontalAlignment(SwingConstants.RIGHT);
		w_saveFolderDisp.setBackground(new Color(0, 128, 128));
		frmTapseSettings.getContentPane().add(w_saveFolderDisp, "cell 0 5,growx");
		
		JButton w_changeSaveFolder = new JButton("Change");
		w_changeSaveFolder.setBackground(SystemColor.controlHighlight);
		w_changeSaveFolder.addActionListener(new ActionListener() {
			//clicked on save folder chooser
			public void actionPerformed(ActionEvent e) {
				//opening folder browse dialog
			  	JFileChooser chooser = new JFileChooser();
			  	chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			  	//setting default directory			  	
			    int returnVal = chooser.showOpenDialog(frmTapseSettings);
			    if(returnVal == JFileChooser.APPROVE_OPTION) {
			    	String s = chooser.getSelectedFile().getAbsolutePath();
			    	if(!settingsHandler.setSaveFolder(s))
			    		LOGGER.info("Failed to change save folder!");
			    	updateForm();
			    }
			}
		});
		frmTapseSettings.getContentPane().add(w_changeSaveFolder, "cell 0 5");
		
		JSeparator separator_1 = new JSeparator();
		separator_1.setForeground(Color.LIGHT_GRAY);
		frmTapseSettings.getContentPane().add(separator_1, "cell 0 7,growx");
		
		JLabel l_schedule = new JLabel("Schedule");
		frmTapseSettings.getContentPane().add(l_schedule, "flowx,cell 0 8,growx");
		
		w_days_0 = new JToggleButton("Sun");
		w_days_0.setBackground(SystemColor.scrollbar);
		w_days_0.setSelected(true);
		w_days_0.setFont(new Font("Tahoma", Font.PLAIN, 9));
		frmTapseSettings.getContentPane().add(w_days_0, "flowx,cell 0 9,growx");
		
		w_days_1 = new JToggleButton("Mon");
		w_days_1.setBackground(SystemColor.scrollbar);
		w_days_1.setSelected(true);
		w_days_1.setFont(new Font("Tahoma", Font.PLAIN, 9));
		frmTapseSettings.getContentPane().add(w_days_1, "cell 0 9,growx");
		
		w_days_2 = new JToggleButton("Tue");
		w_days_2.setBackground(SystemColor.scrollbar);
		w_days_2.setSelected(true);
		w_days_2.setFont(new Font("Tahoma", Font.PLAIN, 9));
		frmTapseSettings.getContentPane().add(w_days_2, "cell 0 9,growx");
		
		w_days_3 = new JToggleButton("Wed");
		w_days_3.setBackground(SystemColor.scrollbar);
		w_days_3.setSelected(true);
		w_days_3.setFont(new Font("Tahoma", Font.PLAIN, 9));
		frmTapseSettings.getContentPane().add(w_days_3, "cell 0 9,growx");
		
		w_days_4 = new JToggleButton("Thu");
		w_days_4.setBackground(SystemColor.scrollbar);
		w_days_4.setSelected(true);
		w_days_4.setFont(new Font("Tahoma", Font.PLAIN, 9));
		frmTapseSettings.getContentPane().add(w_days_4, "cell 0 9,growx");
		
		w_days_5 = new JToggleButton("Fri");
		w_days_5.setBackground(SystemColor.scrollbar);
		w_days_5.setSelected(true);
		w_days_5.setFont(new Font("Tahoma", Font.PLAIN, 9));
		frmTapseSettings.getContentPane().add(w_days_5, "cell 0 9,growx");
		
		w_days_6 = new JToggleButton("Sat");
		w_days_6.setBackground(SystemColor.scrollbar);
		w_days_6.setSelected(true);
		w_days_6.setFont(new Font("Tahoma", Font.PLAIN, 9));
		frmTapseSettings.getContentPane().add(w_days_6, "cell 0 9,growx");
		
		//action listeners
		w_days_0.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				settingsHandler.setDayOfWeekEnabled(0, w_days_0.isSelected());
				updateForm();
			}
		});
		w_days_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				settingsHandler.setDayOfWeekEnabled(1, w_days_1.isSelected());
				updateForm();
			}
		});
		w_days_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				settingsHandler.setDayOfWeekEnabled(2, w_days_2.isSelected());
				updateForm();
			}
		});
		w_days_3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				settingsHandler.setDayOfWeekEnabled(3, w_days_3.isSelected());
				updateForm();
			}
		});
		w_days_4.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				settingsHandler.setDayOfWeekEnabled(4, w_days_4.isSelected());
				updateForm();
			}
		});
		w_days_5.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				settingsHandler.setDayOfWeekEnabled(5, w_days_5.isSelected());
				updateForm();
			}
		});
		w_days_6.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				settingsHandler.setDayOfWeekEnabled(6, w_days_6.isSelected());
				updateForm();
			}
		});
		
		
		w_enableSchedule = new JCheckBox("Enabled");
		w_enableSchedule.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				settingsHandler.setDaysEnabled(w_enableSchedule.isSelected());
				updateForm();
			}
		});
		frmTapseSettings.getContentPane().add(w_enableSchedule, "cell 0 8,alignx right");
		
		JLabel l_fromTime = new JLabel("From:");
		frmTapseSettings.getContentPane().add(l_fromTime, "flowx,cell 0 10");
		
		w_fromTime = new JFormattedTextField(createFormatter("##:##"));
		w_fromTime.setText("00:00");
		frmTapseSettings.getContentPane().add(w_fromTime, "cell 0 10,growx");
		
		JLabel l_toTime = new JLabel("To:");
		frmTapseSettings.getContentPane().add(l_toTime, "cell 0 10");
		
		w_toTime = new JFormattedTextField(createFormatter("##:##"));
		w_toTime.setText("00:00");
		frmTapseSettings.getContentPane().add(w_toTime, "cell 0 10,growx");
		
		//time input parsers
		w_fromTime.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent arg0) {
				//parse
				String s = w_fromTime.getText();
				String[] x = s.trim().split(":");
				boolean y = true;
				try{
					y = settingsHandler.setTimeFrom(Integer.parseInt(x[0]),
															Integer.parseInt(x[1]));
				}catch(Exception e){
					e.printStackTrace();
				}
				if(y){
					w_fromTime.setBackground(Color.white);
				}else{
					w_fromTime.setBackground(Color.red);
				}
			}
		});
		
		w_toTime.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent arg0) {
				//parse
				String s = w_toTime.getText();
				String[] x = s.trim().split(":");
				boolean y = true;
				try{
					y = settingsHandler.setTimeTo(Integer.parseInt(x[0]),
															Integer.parseInt(x[1]));
				}catch(Exception e){
					e.printStackTrace();
				}
				if(y){
					w_toTime.setBackground(Color.white);
				}else{
					w_toTime.setBackground(Color.red);
				}
			}
		});
		
		JSeparator separator_3 = new JSeparator();
		separator_3.setForeground(Color.LIGHT_GRAY);
		frmTapseSettings.getContentPane().add(separator_3, "cell 0 11,growx");
		
		l_appWatch = new JLabel("Watch for application");
		frmTapseSettings.getContentPane().add(l_appWatch, "flowx,cell 0 12,growx");
		
		w_appWatch = new JTextField();
		//on typing
		w_appWatch.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent arg0) {
				settingsHandler.setProgName(w_appWatch.getText());
				updateForm();
			}
		});
		w_appWatch.setText("Loading");
		frmTapseSettings.getContentPane().add(w_appWatch, "cell 0 13,growx");
		w_appWatch.setColumns(10);
		
		JButton w_exit = new JButton("Exit");
		w_exit.setBackground(SystemColor.controlHighlight);
		w_exit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				exitApp();
			}
		});
		frmTapseSettings.getContentPane().add(w_exit, "cell 0 0");
		
		w_enableAppWatch = new JCheckBox("Enabled");
		w_enableAppWatch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				settingsHandler.setProgEnabled(w_enableAppWatch.isSelected());
				updateForm();
				
				//appwatch text
				if(settingsHandler.isProgEnabled()){
					if(camModule.checkWindowResult){
						l_appWatch.setText("Watch for application (Found!)");
						l_appWatch.setForeground(Color.blue);
					}else{
						l_appWatch.setText("Watch for application (Not found!)");
						l_appWatch.setForeground(Color.red);
					}
				}else{
					l_appWatch.setText("Watch for application");
					l_appWatch.setForeground(Color.black);
				}
			}
		});
		frmTapseSettings.getContentPane().add(w_enableAppWatch, "cell 0 12");
		frmTapseSettings.setBackground(new Color(60, 179, 113));
		frmTapseSettings.setTitle("Tapse Settings");
		frmTapseSettings.setBounds(100, 100, 395, 333);
	}

	/**
	 * Closes application.
	 */
	private static void exitApp(){
		settingsHandler.writeSettings();
		System.out.println("Exiting...");
		System.exit(0);
	}
	
	/**
	 * Shows or hides settings window.
	 */
	private static void showHide(){
		if(window.frmTapseSettings.isVisible()){
			window.frmTapseSettings.setVisible(false);
			System.out.println("Hiding settings...");
		}else{
			window.frmTapseSettings.setVisible(true);
			System.out.println("Showing settings...");
		}
	}
	
	/**
	 * Updates settings form.
	 */
	private static void updateForm(){
		try{
			w_enableTapse.setSelected(settingsHandler.isEnabled());
			w_freqSpin.setValue(settingsHandler.getFrequency());
			w_saveFolderDisp.setText(settingsHandler.getSaveFolder());
			w_numberOfShotsDisp.setText("Number of Shots: " + new Integer(settingsHandler.getLastShotID()).toString());
			w_enableSchedule.setSelected(settingsHandler.isDaysEnabled());
			w_days_0.setSelected(settingsHandler.isDayOfWeekEnabed(0));
			w_days_1.setSelected(settingsHandler.isDayOfWeekEnabed(1));
			w_days_2.setSelected(settingsHandler.isDayOfWeekEnabed(2));
			w_days_3.setSelected(settingsHandler.isDayOfWeekEnabed(3));
			w_days_4.setSelected(settingsHandler.isDayOfWeekEnabed(4));
			w_days_5.setSelected(settingsHandler.isDayOfWeekEnabed(5));
			w_days_6.setSelected(settingsHandler.isDayOfWeekEnabed(6));
			w_fromTime.setText(settingsHandler.getTimeFrom().trim());
			w_toTime.setText(settingsHandler.getTimeTo().trim());
			w_enableAppWatch.setSelected(settingsHandler.isProgEnabled());
			w_appWatch.setEnabled(w_enableAppWatch.isSelected());
			w_appWatch.setText(settingsHandler.getProgName());
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * Creates formatter for formattedTextField.
	 * @param s Format string.
	 * @return Returns MaskFormatter.
	 */
	protected MaskFormatter createFormatter(String s) {
	    MaskFormatter formatter = null;
	    try {
	        formatter = new MaskFormatter(s);
	    } catch (java.text.ParseException exc) {
	        System.err.println("formatter is bad: " + exc.getMessage());
	    }
	    return formatter;
	}
	
}
