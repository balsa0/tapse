package hu.ypg.tapse;

import java.awt.EventQueue;

import javax.swing.JFrame;

import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.Window.Type;

import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JCheckBox;
import javax.swing.JSpinner;

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
import java.util.HashMap;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.SystemColor;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.io.File;

import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.JSeparator;
import javax.swing.JToggleButton;
import java.awt.Font;
import javax.swing.UIManager;
import javax.swing.SwingConstants;
import javax.swing.JFormattedTextField;
import javax.swing.JTextField;
import javax.swing.Box;

public class TapseSettings {

	//class
	private JFrame frmTapseSettings;
	
	//xml settings handler 
	private static TapseXMLHandler settingsHandler = new TapseXMLHandler();;
	
	//the main window
	private static final TapseSettings window = new TapseSettings();
	
	//tray icon
	private static final TrayIcon trayIcon = new TrayIcon(getImage("res/tapse.png"),"Tapse");
	private JTextField w_appWatch;
	
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
					
					
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				
			}

		});
	}
	
	//exit from application
	private static void exitApp(){
		settingsHandler.settingsWriter();
		System.out.println("Exiting...");
		System.exit(0);
	}
	
	//show or hide settings window
	private static void showHide(){
		if(window.frmTapseSettings.isVisible()){
			window.frmTapseSettings.setVisible(false);
			System.out.println("Hiding settings...");
		}else{
			window.frmTapseSettings.setVisible(true);
			System.out.println("Showing settings...");
		}
	}
	
	//initialize tray icon and menu
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
	
	public static Image getImage(String path) {
        ImageIcon icon = new ImageIcon(path, "omt");
        return icon.getImage();
    }
	
	/**
	 * Create the application.
	 */
	public TapseSettings() {
		initialize();
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
		l_tapseLogo.setIcon(new ImageIcon("D:\\Qt\\workspace\\Tapse\\res\\tapse_sm.png"));
		frmTapseSettings.getContentPane().add(l_tapseLogo, "flowx,cell 0 0,growx");
		
		JLabel l_enableTapse = new JLabel("Enable Tapse");
		frmTapseSettings.getContentPane().add(l_enableTapse, "flowx,cell 0 1,growx");
		
		final JCheckBox w_enableTapse = new JCheckBox("Enabled");
		w_enableTapse.setFont(new Font("Tahoma", Font.PLAIN, 11));
		w_enableTapse.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				settingsHandler.set_enabled(w_enableTapse.isSelected());
			}
		});
		w_enableTapse.setSelected(settingsHandler.is_enabled());
		w_enableTapse.setBackground(SystemColor.control);
		frmTapseSettings.getContentPane().add(w_enableTapse, "cell 0 1");
		
		JLabel l_shotFrequency = new JLabel("Shot frequency");
		frmTapseSettings.getContentPane().add(l_shotFrequency, "flowx,cell 0 2,growx");
		
		final JSpinner w_freqSpin = new JSpinner();
		w_freqSpin.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				try{
					settingsHandler.set_frequency((Integer) w_freqSpin.getValue());
				}catch(Exception e){
					e.printStackTrace();
				}
				
			}
		});
		
		w_freqSpin.setValue(settingsHandler.get_frequency());
		
		w_freqSpin.setBackground(new Color(255, 255, 255));
		frmTapseSettings.getContentPane().add(w_freqSpin, "cell 0 2,growx");

		
		JLabel l_seconds = new JLabel("seconds");
		frmTapseSettings.getContentPane().add(l_seconds, "cell 0 2,growx");
		
		JSeparator separator = new JSeparator();
		separator.setForeground(Color.LIGHT_GRAY);
		frmTapseSettings.getContentPane().add(separator, "cell 0 4,growx");
		
		JLabel l_saveFolder = new JLabel("Save folder:");
		frmTapseSettings.getContentPane().add(l_saveFolder, "flowx,cell 0 5");
		
		JLabel w_numberOfShotsDisp = new JLabel("Number of Shots:");
		frmTapseSettings.getContentPane().add(w_numberOfShotsDisp, "flowx,cell 0 6,growx");
		
		JButton w_openFolder = new JButton("Open Folder");
		w_openFolder.setBackground(SystemColor.controlHighlight);
		w_openFolder.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Desktop desktop = Desktop.getDesktop();
		        File dirToOpen = null;
		        try {
		            dirToOpen = new File("c:\\");
		            desktop.open(dirToOpen);
		        }catch (Exception e) {
		            e.printStackTrace();
		        }
			}
		});
		frmTapseSettings.getContentPane().add(w_openFolder, "cell 0 6");
		
		JLabel w_saveFolderDisp = new JLabel("Unknown");
		w_saveFolderDisp.setFont(new Font("Tahoma", Font.BOLD, 11));
		w_saveFolderDisp.setHorizontalAlignment(SwingConstants.RIGHT);
		w_saveFolderDisp.setBackground(new Color(0, 128, 128));
		frmTapseSettings.getContentPane().add(w_saveFolderDisp, "cell 0 5,growx");
		
		JButton w_changeSaveFolder = new JButton("Change");
		w_changeSaveFolder.setBackground(SystemColor.controlHighlight);
		w_changeSaveFolder.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				  	JFileChooser chooser = new JFileChooser();
				  	chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				    int returnVal = chooser.showOpenDialog(frmTapseSettings);
				    if(returnVal == JFileChooser.APPROVE_OPTION) {
				       System.out.println("You chose to open this file: " +
				            chooser.getSelectedFile().getName());
				    }
			}
		});
		frmTapseSettings.getContentPane().add(w_changeSaveFolder, "cell 0 5");
		
		JSeparator separator_1 = new JSeparator();
		separator_1.setForeground(Color.LIGHT_GRAY);
		frmTapseSettings.getContentPane().add(separator_1, "cell 0 7,growx");
		
		JLabel l_schedule = new JLabel("Schedule");
		frmTapseSettings.getContentPane().add(l_schedule, "flowx,cell 0 8,growx");
		
		JToggleButton w_days_0 = new JToggleButton("Sun");
		w_days_0.setBackground(SystemColor.scrollbar);
		w_days_0.setSelected(true);
		w_days_0.setFont(new Font("Tahoma", Font.PLAIN, 9));
		frmTapseSettings.getContentPane().add(w_days_0, "flowx,cell 0 9,growx");
		
		JToggleButton w_days_1 = new JToggleButton("Mon");
		w_days_1.setBackground(SystemColor.scrollbar);
		w_days_1.setSelected(true);
		w_days_1.setFont(new Font("Tahoma", Font.PLAIN, 9));
		frmTapseSettings.getContentPane().add(w_days_1, "cell 0 9,growx");
		
		JToggleButton w_days_2 = new JToggleButton("Tue");
		w_days_2.setBackground(SystemColor.scrollbar);
		w_days_2.setSelected(true);
		w_days_2.setFont(new Font("Tahoma", Font.PLAIN, 9));
		frmTapseSettings.getContentPane().add(w_days_2, "cell 0 9,growx");
		
		JToggleButton w_days_3 = new JToggleButton("Wed");
		w_days_3.setBackground(SystemColor.scrollbar);
		w_days_3.setSelected(true);
		w_days_3.setFont(new Font("Tahoma", Font.PLAIN, 9));
		frmTapseSettings.getContentPane().add(w_days_3, "cell 0 9,growx");
		
		JToggleButton w_days_4 = new JToggleButton("Thu");
		w_days_4.setBackground(SystemColor.scrollbar);
		w_days_4.setSelected(true);
		w_days_4.setFont(new Font("Tahoma", Font.PLAIN, 9));
		frmTapseSettings.getContentPane().add(w_days_4, "cell 0 9,growx");
		
		JToggleButton w_days_5 = new JToggleButton("Fri");
		w_days_5.setBackground(SystemColor.scrollbar);
		w_days_5.setSelected(true);
		w_days_5.setFont(new Font("Tahoma", Font.PLAIN, 9));
		frmTapseSettings.getContentPane().add(w_days_5, "cell 0 9,growx");
		
		JToggleButton w_days_6 = new JToggleButton("Sat");
		w_days_6.setBackground(SystemColor.scrollbar);
		w_days_6.setSelected(true);
		w_days_6.setFont(new Font("Tahoma", Font.PLAIN, 9));
		frmTapseSettings.getContentPane().add(w_days_6, "cell 0 9,growx");
		
		JCheckBox w_enableSchedule = new JCheckBox("Enabled");
		frmTapseSettings.getContentPane().add(w_enableSchedule, "cell 0 8,alignx right");
		
		JLabel l_fromTime = new JLabel("From:");
		frmTapseSettings.getContentPane().add(l_fromTime, "flowx,cell 0 10");
		
		JFormattedTextField w_fromTime = new JFormattedTextField();
		w_fromTime.setText("00:00");
		frmTapseSettings.getContentPane().add(w_fromTime, "cell 0 10,growx");
		
		JLabel l_toTime = new JLabel("To:");
		frmTapseSettings.getContentPane().add(l_toTime, "cell 0 10");
		
		JFormattedTextField w_toTime = new JFormattedTextField();
		w_toTime.setText("00:00");
		frmTapseSettings.getContentPane().add(w_toTime, "cell 0 10,growx");
		
		JSeparator separator_3 = new JSeparator();
		separator_3.setForeground(Color.LIGHT_GRAY);
		frmTapseSettings.getContentPane().add(separator_3, "cell 0 11,growx");
		
		JLabel l_appWatch = new JLabel("Watch for application");
		frmTapseSettings.getContentPane().add(l_appWatch, "flowx,cell 0 12,growx");
		
		w_appWatch = new JTextField();
		w_appWatch.setText("Google Chrome");
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
		
		final JCheckBox w_enableAppWatch = new JCheckBox("Enabled");
		w_enableAppWatch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				w_appWatch.setEnabled(w_enableAppWatch.isSelected());
			}
		});
		frmTapseSettings.getContentPane().add(w_enableAppWatch, "cell 0 12");
		frmTapseSettings.setBackground(new Color(60, 179, 113));
		frmTapseSettings.setTitle("Tapse Settings");
		frmTapseSettings.setBounds(100, 100, 395, 333);
	}


}
