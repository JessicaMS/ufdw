package edu.ufl.digitalworlds.gui;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/*
 * Copyright 2011, Digital Worlds Institute, University of 
 * Florida, Angelos Barmpoutis.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *     * Redistributions of source code must retain this copyright
 * notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce this
 * copyright notice, this list of conditions and the following disclaimer
 * in the documentation and/or other materials provided with the
 * distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

@SuppressWarnings("serial")
public abstract class DWApp extends JPanel implements ItemListener, ActionListener
{
	static SupportedLaF list_of_lafs[];

    static class SupportedLaF {
	String name;
	LookAndFeel laf;
	JMenuItem menuitem; 

	SupportedLaF(String name, LookAndFeel laf) {
	    this.name = name;
	    this.laf = laf;
	    this.menuitem=null;
	}

	public String toString() {
	    return name;
	}
    }    
   
    private static String most_recent_path="";
    public static String getMostRecentPath(){return most_recent_path;}
    public static void setMostRecentPath(String path){most_recent_path=path;}
    
    public static JFrame app_frame;
    public static String frame_title;
    public static BufferedImage frame_icon;
    
    private JMenuItem aboutDW;
    private JMenuItem detachWindowMI;
    private JMenuItem hideBorderMI;
    private JMenuItem closeWindowMI;
    private JMenuItem setSizeMI;
    private JMenuItem setLocationMI;
    private JMenuItem toggleFullScreenMI;
    private boolean fullscreen=false;
    private Point fullscreen_mem_location;
    private Dimension fullscreen_mem_size;
    
    private JComboBox lafs_ComboBox;
    private JButton fullscreen_Button;
    private JButton setsize_Button;
    private JButton setlocation_Button;
    private JButton hideborder_Button;
    private JButton systeminfo_Button;
    private JButton detach_Button;
    
    private JMenu windowMenu;
    
    private JMenuItem system_properties;
    public static DWApp app;
    static JProgressBar progressBar;
    static JLabel progressLabel;

    public void setLoadingProgress(String msg,int value)
    {
    	progressLabel.setText(msg);
        progressBar.setValue(value);
    }
    
    public abstract void GUIsetup(JPanel p_root);

    public void GUIclosing(){};
    
    public static boolean showConfirmDialog(String title, String question)
    {
    	if(JOptionPane.showConfirmDialog(app_frame, question, title, JOptionPane.YES_NO_OPTION)==0) return true;
    	else return false;
    }
    
    public static void showErrorDialog(String title, String question)
    {
    	JOptionPane.showMessageDialog(app_frame, question, title, JOptionPane.ERROR_MESSAGE);
    }
    
    public static void showInformationDialog(String title, String question)
    {
    	JOptionPane.showMessageDialog(app_frame, question, title, JOptionPane.INFORMATION_MESSAGE);
    }
    
    public static boolean showConfirmDialog(Component parent, String title, String question)
    {
    	if(JOptionPane.showConfirmDialog(parent, question, title, JOptionPane.YES_NO_OPTION)==0) return true;
    	else return false;
    }
    
    public static void showErrorDialog(Component parent,String title, String question)
    {
    	JOptionPane.showMessageDialog(parent, question, title, JOptionPane.ERROR_MESSAGE);
    }
    
    public static void showInformationDialog(Component parent, String title, String question)
    {
    	JOptionPane.showMessageDialog(parent, question, title, JOptionPane.INFORMATION_MESSAGE);
    }
    
    public void destructor(){
        if ( showConfirmDialog("Closing application","Are you sure you want to exit?")) {
            app_frame.dispose();
            GUIclosing();
            System.exit(0);
        }
    }
 
    public DWApp() {
    	
    	UIManager.LookAndFeelInfo lafInfo;
    	UIManager.LookAndFeelInfo[] installedLafs = UIManager.getInstalledLookAndFeels();
    	list_of_lafs=new SupportedLaF[installedLafs.length];
    	for (int i=0;i<installedLafs.length;i++) {
    		lafInfo=installedLafs[i];
    	    try { 
    		Class lnfClass = Class.forName(lafInfo.getClassName());
    		LookAndFeel laf = (LookAndFeel)(lnfClass.newInstance());
    		if (laf.isSupportedLookAndFeel()) {
    		    String name = lafInfo.getName();
    		    list_of_lafs[i]=new SupportedLaF(name, laf);
    		}
    	    } catch (Exception e) { // If ANYTHING weird happens, don't add it
    		continue;
    	    }
    	}
  
    	for(int i=0;i<DWApp.list_of_lafs.length;i++)
        {
        	if(DWApp.list_of_lafs[i]!=null)
        	{
        		if(DWApp.list_of_lafs[i].name.compareTo("Nimbus")==0)
        		{
        		LookAndFeel laf = DWApp.list_of_lafs[i].laf;
        		try {
        		    UIManager.setLookAndFeel(laf);
        		} catch (UnsupportedLookAndFeelException exc) {}
        		}
        	}
        }
    	
    	
    	
        setLayout(new BorderLayout());
        setBorder(new EtchedBorder());

        

        // hard coding 14 = 11 demo dirs + images + fonts + Intro
        progressBar.setMaximum(100);   
        
        setLoadingProgress("Loading ...",0);
        
        UIManager.put("Button.margin", new Insets(0,0,0,0));
       

       
        JPanel p_root=new JPanel(new BorderLayout());
        
        GUIsetup(p_root);
        
  
        add(createMenuBar(), BorderLayout.NORTH);
        
        add(p_root,BorderLayout.CENTER);
        
    }

    public void MenuGUIsetup(JMenuBar menuBar){}
 
    public JPanel createWindowSettingsPanel()
    {
    	return createWindowSettingsPanel(Color.BLACK);
    }
    
    public JPanel createWindowSettingsPanel(Color clr)
    {
    	JPanel windowPanel=new JPanel(new GridBagLayout());
  		windowPanel.setOpaque(false);
  		TitledBorder tb=new TitledBorder(new EtchedBorder(), "Window");
		tb.setTitleColor(clr);
  		windowPanel.setBorder(tb);
  		
  		
  		
  		JPanel lookAndFeelPanel=new JPanel(new GridBagLayout());
  		lookAndFeelPanel.setOpaque(false);
  		lafs_ComboBox=new JComboBox();
  		
        for(int i=0;i<list_of_lafs.length;i++)
        {
        	if(list_of_lafs[i]!=null)
        	{
        		lafs_ComboBox.addItem(list_of_lafs[i]);
        	}
        }
        lafs_ComboBox.addActionListener(this);
  		
  		JButton detach_button2=new JButton("Detach window 2");
		detach_button2.addActionListener(this);
		systeminfo_Button=new JButton("System info");
		systeminfo_Button.addActionListener(this);
		
		int c=0;
  		if(DWApplet.applet!=null)
        {
  			detach_Button=new JButton("Detach window");
  			detach_Button.addActionListener(this);
  			DWApp.addToGridBag(lookAndFeelPanel,detach_Button, c, 0, 1, 1, 1.0, 1.0);
  			c+=1;
        }
		
		DWApp.addToGridBag(lookAndFeelPanel,systeminfo_Button, c, 0, 1, 1, 1.0, 1.0);c+=1;
		JLabel l=new JLabel("Look and Feel:");
		l.setForeground(clr);
		l.setHorizontalAlignment(JLabel.RIGHT);
		DWApp.addToGridBag(lookAndFeelPanel,l, c, 0, 1, 1, 1.0, 1.0);c+=1;
		DWApp.addToGridBag(lookAndFeelPanel,lafs_ComboBox, c, 0, 1, 1, 1.0, 1.0);c+=1;
		DWApp.addToGridBag(windowPanel,lookAndFeelPanel, 0, 0, 4, 1, 1.0, 1.0);
		
		fullscreen_Button=new JButton("Full Screen");
		if(DWApplet.applet!=null)fullscreen_Button.setEnabled(false);
		fullscreen_Button.addActionListener(this);
		DWApp.addToGridBag(windowPanel,fullscreen_Button, 0, 1, 1, 1, 1.0, 1.0);
		
		setsize_Button=new JButton("Set Size");
		if(DWApplet.applet!=null)setsize_Button.setEnabled(false);
		setsize_Button.addActionListener(this);
		DWApp.addToGridBag(windowPanel,setsize_Button, 1, 1, 1, 1, 1.0, 1.0);
		
		setlocation_Button=new JButton("Set Location");
		if(DWApplet.applet!=null)setlocation_Button.setEnabled(false);
		setlocation_Button.addActionListener(this);
		DWApp.addToGridBag(windowPanel,setlocation_Button, 2, 1, 1, 1, 1.0, 1.0);
		
		hideborder_Button=new JButton("Hide Border");
		if(DWApplet.applet!=null)hideborder_Button.setEnabled(false);
		hideborder_Button.addActionListener(this);
		DWApp.addToGridBag(windowPanel,hideborder_Button, 3, 1, 1, 1, 1.0, 1.0);
		
		
  		return windowPanel;
    }
    
    private JMenuBar createMenuBar() {

        JPopupMenu.setDefaultLightWeightPopupEnabled(false);
        JMenuBar menuBar = new JMenuBar();
  
        MenuGUIsetup(menuBar);


        JMenu system_options = (JMenu) menuBar.add(new JMenu("Window"));       
        
        
        if(DWApplet.applet!=null)
        {
        	detachWindowMI = (JMenuItem) system_options.add(new JMenuItem("Detach window"));
            detachWindowMI.addActionListener(this);
        }
        
        JMenu options= (JMenu) system_options.add(new JMenu("Look and Feel"));       

        for(int i=0;i<list_of_lafs.length;i++)
        {
        	if(list_of_lafs[i]!=null)
        	{
        		list_of_lafs[i].menuitem = (JMenuItem) options.add(new JMenuItem(list_of_lafs[i].name));
        		list_of_lafs[i].menuitem.addActionListener(this);
        	}
        }
        
        windowMenu= new JMenu("Properties");  
        
        hideBorderMI = (JMenuItem) windowMenu.add(new JMenuItem("Hide border"));
        hideBorderMI.addActionListener(this);
        
        toggleFullScreenMI = (JMenuItem) windowMenu.add(new JMenuItem("Toggle full screen"));
        toggleFullScreenMI.addActionListener(this);
        
        setSizeMI = (JMenuItem) windowMenu.add(new JMenuItem("Set window size"));
        setSizeMI.addActionListener(this);
        
        setLocationMI = (JMenuItem) windowMenu.add(new JMenuItem("Set window location"));
        setLocationMI.addActionListener(this);
        
        closeWindowMI = (JMenuItem) windowMenu.add(new JMenuItem("Close window"));
        closeWindowMI.addActionListener(this);
        
        if(DWApplet.applet!=null)
        {
        	windowMenu.setEnabled(false);
        }
        system_options.add(windowMenu);
        
        system_properties = (JMenuItem) system_options.add(new JMenuItem("System info"));
        system_properties.addActionListener(this);
    

        aboutDW = (JMenuItem) menuBar.add(new JMenuItem("About"));
        aboutDW.addActionListener(this);
        Dimension d=new Dimension();
        d.setSize(aboutDW.getPreferredSize().getWidth(),aboutDW.getMaximumSize().getHeight());
        aboutDW.setMaximumSize(d);
        
        return menuBar;
    }


    public void GUIactionPerformed(ActionEvent e){}
    public void updateLookAndFeel(){}
    
    public void onWindowStateChanged(){}
    
    public void actionPerformed(ActionEvent e) {
         
        
        if(e.getSource().equals(aboutDW))
        {
        	JOptionPane.showMessageDialog(app_frame,
        			"<html><center>Digital Worlds Institute<br>University of Florida<br><a href=\"http://www.digitalworlds.ufl.edu\">www.digitalworlds.ufl.edu</a><br><br>This application was made using the UFDW Java library <br>developed by Angelos Barmpoutis, and further extended<br>by the students and faculty who work in the SAGE <br>program (Serious and Applied Gaming Environments) at <br>the Digital Worlds institute. <br><br>For more details, please contact me: angelos@digitalworlds.ufl.edu </center>",
        		    "About", 
        		    JOptionPane.INFORMATION_MESSAGE,
        		    new ImageIcon(DWIcon().getScaledInstance(32, 32, Image.SCALE_SMOOTH)));
        	
        }
        else if(e.getSource().equals(system_properties) || e.getSource().equals(systeminfo_Button))
        {
        	showInformationDialog("System Properties", "<html><b>Operating System:</b> "+System.getProperty("os.name")+"<br><b>Version:</b> "+System.getProperty("os.version")+"<br><b>Architecture:</b> "+System.getProperty("os.arch")+"<br><b>Java Version:</b> "+System.getProperty("java.version")+"<br><b>Java Vendor:</b> "+System.getProperty("java.vendor"));
        }
        else if(e.getSource().equals(closeWindowMI))
        {
        	destructor();
        }
        else if(e.getSource().equals(setLocationMI) || e.getSource().equals(setlocation_Button))
        {
        	new SetLocationDialog();
        }
        else if(e.getSource().equals(setSizeMI) || e.getSource().equals(setsize_Button))
        {
        	new SetSizeDialog();
        }
        else if (e.getSource().equals(detachWindowMI) || e.getSource().equals(detach_Button)) {
        	
        	if(detachWindowMI.getText().compareTo("Detach window")==0)
        	{
        		if(detach_Button!=null)detach_Button.setText("Attach window");
        		if(fullscreen_Button!=null)fullscreen_Button.setEnabled(true);
        		if(setsize_Button!=null)setsize_Button.setEnabled(true);
        		if(setlocation_Button!=null)setlocation_Button.setEnabled(true);
        		if(hideborder_Button!=null)hideborder_Button.setEnabled(true);
        		
                windowMenu.setEnabled(true);
        		detachWindowMI.setText("Attach window");
        			
                DWApplet.applet.getContentPane().remove(this);
                DWApplet.applet.getContentPane().setLayout(new BorderLayout());
                DWApplet.applet.getContentPane().add(new JLabel("Notice: By closing this window, you terminate your Java application.",JLabel.CENTER), BorderLayout.CENTER);
                DWApplet.applet.validate();
                    
                
                app_frame=new JFrame(DWApp.frame_title);
            	JFrame frame = app_frame;
                frame.getAccessibleContext().setAccessibleDescription("DW Engine");
                frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
                WindowAdapter adapter=new WindowAdapter() {
                    public void windowClosing(WindowEvent e) { app.destructor();}
                    public void windowDeiconified(WindowEvent e) { 
                        if (app != null) { /*app.start();*/ }
                    }
                    public void windowIconified(WindowEvent e) { 
                        if (app != null) { /*app.stop();*/ }
                    }
                    public void windowStateChanged(WindowEvent e)
                    {
                    	if(app!=null)app.onWindowStateChanged();
                    }
                };
                frame.addWindowListener(adapter);
                frame.addWindowStateListener(adapter);
                frame.getContentPane().add("Center",DWApp.app);
                frame.pack();
                
                
                Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
                frame.setLocation(d.width/2 - DWApplet.frame_width/2, d.height/2 - DWApplet.frame_height/2);
                frame.setSize(DWApplet.frame_width, DWApplet.frame_height);
                frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                frame.setIconImage(DWApp.frame_icon);
                frame.setVisible(true);
                
                return;
        	}
        	else
        	{
        		if(detach_Button!=null)detach_Button.setText("Detach window");
        		if(fullscreen_Button!=null)fullscreen_Button.setEnabled(false);
        		if(setsize_Button!=null)setsize_Button.setEnabled(false);
        		if(setlocation_Button!=null)setlocation_Button.setEnabled(false);
        		if(hideborder_Button!=null)hideborder_Button.setEnabled(false);
        		
        		windowMenu.setEnabled(false);
        		detachWindowMI.setText("Detach window");
        		app_frame.dispose();
        		app_frame=null;
        		DWApplet.applet.getContentPane().removeAll();
                DWApplet.applet.getContentPane().setLayout(new BorderLayout());
                DWApplet.applet.getContentPane().add(DWApp.app, BorderLayout.CENTER);
                DWApplet.applet.validate();   
            }
        }
        else if (e.getSource().equals(hideBorderMI) || e.getSource().equals(hideborder_Button)) {
        	
        	app_frame.getContentPane().removeAll();
        	Point loc=app_frame.getLocation();
        	Dimension siz=app_frame.getSize();
        	app_frame.dispose();
        	app_frame=new JFrame(DWApp.frame_title);
        	
        	if(hideBorderMI.getText().compareTo("Hide border")==0)	
        	{
        		hideBorderMI.setText("Show border");
        		if(hideborder_Button!=null)hideborder_Button.setText("Show border");
        		app_frame.setUndecorated(true);
        	}
        	else
        	{
        		hideBorderMI.setText("Hide border");
        		if(hideborder_Button!=null)hideborder_Button.setText("Hide border");
        	}
        	
        	
        	JFrame frame = app_frame;
            frame.getAccessibleContext().setAccessibleDescription("DW Engine");
            frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            WindowAdapter adapter=new WindowAdapter() {
                public void windowClosing(WindowEvent e) { app.destructor();}
                public void windowDeiconified(WindowEvent e) { 
                    if (app != null) { /*app.start();*/ }
                }
                public void windowIconified(WindowEvent e) { 
                    if (app != null) { /*app.stop();*/ }
                }
                public void windowStateChanged(WindowEvent e)
                {
                	if(app!=null)app.onWindowStateChanged();
                }
            };
            frame.addWindowListener(adapter);
            frame.addWindowStateListener(adapter);
            frame.getContentPane().add("Center",DWApp.app);
            frame.pack();
            
            frame.setLocation(loc);
            frame.setSize(siz);
            frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            frame.setIconImage(DWApp.frame_icon);
            
            frame.setVisible(true);
            
            return;
        }
        else if (e.getSource().equals(toggleFullScreenMI) || e.getSource().equals(fullscreen_Button))
        {
        	app_frame.getContentPane().removeAll();
        	Point loc=app_frame.getLocation();
        	Dimension siz=app_frame.getSize();
        	app_frame.dispose();
        	app_frame=new JFrame(DWApp.frame_title);
        	
        	if(fullscreen==false)	
        	{
        		fullscreen=true;
        		fullscreen_mem_location=loc;
        		fullscreen_mem_size=siz;
        		hideBorderMI.setText("Show border");
        		if(hideborder_Button!=null)hideborder_Button.setText("Show border");
        		app_frame.setUndecorated(true);
        	}
        	else
        	{
        		fullscreen=false;
        		hideBorderMI.setText("Hide border");
        		if(hideborder_Button!=null)hideborder_Button.setText("Hide border");
        	}
        	
        	
        	JFrame frame = app_frame;
            frame.getAccessibleContext().setAccessibleDescription("DW Engine");
            frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            WindowAdapter adapter=new WindowAdapter() {
                public void windowClosing(WindowEvent e) { app.destructor();}
                public void windowDeiconified(WindowEvent e) { 
                    if (app != null) { /*app.start();*/ }
                }
                public void windowIconified(WindowEvent e) { 
                    if (app != null) { /*app.stop();*/ }
                }
                public void windowStateChanged(WindowEvent e)
                {
                	if(app!=null)app.onWindowStateChanged();
                }
            };
            frame.addWindowListener(adapter);
            frame.addWindowStateListener(adapter);
            frame.getContentPane().add("Center",DWApp.app);
            frame.pack();
            
            if(fullscreen==true)	
        	{
        		frame.setLocation(0,0);
                frame.setSize(Toolkit.getDefaultToolkit().getScreenSize());
        	}
        	else
        	{
        		frame.setLocation(fullscreen_mem_location);
        		frame.setSize(fullscreen_mem_size);
        	}
            frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            frame.setIconImage(DWApp.frame_icon);
            
            frame.setVisible(true);
            
            return;
        }
        else if(e.getSource().equals(lafs_ComboBox))
        {
        	LookAndFeel laf = ((SupportedLaF)lafs_ComboBox.getSelectedItem()).laf;
    		try {
    		    UIManager.setLookAndFeel(laf);
    		    
    		    SwingUtilities.updateComponentTreeUI(app);
    		    updateLookAndFeel();
    		} catch (UnsupportedLookAndFeelException exc) {}
        }
        
        for(int i=0;i<list_of_lafs.length;i++)
        {
        	if(e.getSource().equals(list_of_lafs[i].menuitem))
        	{
        		LookAndFeel laf = list_of_lafs[i].laf;
        		try {
        		    UIManager.setLookAndFeel(laf);
        		    
        		    SwingUtilities.updateComponentTreeUI(app);
        		    updateLookAndFeel();
        		} catch (UnsupportedLookAndFeelException exc) {}
        		return;
        	}
        }
        
        GUIactionPerformed(e);
    }


    public void GUIitemStateChanged(ItemEvent e){};
    public void itemStateChanged(ItemEvent e) {
        GUIitemStateChanged(e);
    	revalidate();
    }


    public void start() {}


    public void stop() {GUIclosing();System.exit(0);}

    
    public static String FormatDecimal(String in,int decimals)
    {
    	return in.substring(0, Math.min(in.indexOf(".")+decimals+1, in.length()));
    }

       
    public static void createMainFrame(String title)
    {
    	DWApp.frame_title=title;
    	app_frame=new JFrame(title);
    	JFrame frame = app_frame;
        frame.getAccessibleContext().setAccessibleDescription("DW Engine");
        int WIDTH = 400, HEIGHT = 200;
        frame.setSize(WIDTH, HEIGHT);
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setLocation(d.width/2 - WIDTH/2, d.height/2 - HEIGHT/2);
        frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        WindowAdapter adapter=new WindowAdapter() {
            public void windowClosing(WindowEvent e) { app.destructor();}
            public void windowDeiconified(WindowEvent e) { 
                if (app != null) { /*app.start();*/ }
            }
            public void windowIconified(WindowEvent e) { 
                if (app != null) { /*app.stop();*/ }
            }
            public void windowStateChanged(WindowEvent e)
            {
            	if(app!=null)app.onWindowStateChanged();
            }
        };
        frame.addWindowListener(adapter);
        frame.addWindowStateListener(adapter);
        JOptionPane.setRootFrame(frame);

        JPanel progressPanel = new JPanel() {
            public Insets getInsets() {
                return new Insets(40,30,20,30);
            }
        };
        
        progressPanel.setLayout(new BoxLayout(progressPanel, BoxLayout.Y_AXIS));
        frame.getContentPane().add(progressPanel, BorderLayout.CENTER);

        Dimension labelSize = new Dimension(400, 20);
        progressLabel = new JLabel("Loading, please wait...");
        progressLabel.setAlignmentX(CENTER_ALIGNMENT);
        progressLabel.setMaximumSize(labelSize);
        progressLabel.setPreferredSize(labelSize);
        
        progressPanel.add(progressLabel);
        progressPanel.add(Box.createRigidArea(new Dimension(1,20)));

        progressBar = new JProgressBar();
        progressBar.setStringPainted(true);
        progressLabel.setLabelFor(progressBar);
        progressBar.setAlignmentX(CENTER_ALIGNMENT);
        progressBar.setMinimum(0);
        progressBar.setValue(0);
        
        progressBar.getAccessibleContext().setAccessibleName("loading progress");
        progressPanel.add(progressBar);

        frame.setIconImage(DWIcon());
        frame.setVisible(true);

    }
    
    public static Image DWIcon()
    {
    	BufferedImage img=new BufferedImage(16, 16, 5);
    	img.setRGB(0,0,-1);
    	img.setRGB(0,1,-5982238);
    	img.setRGB(0,2,-9661979);
    	img.setRGB(0,3,-2630425);
    	img.setRGB(0,4,-1);
    	img.setRGB(0,5,-1);
    	img.setRGB(0,6,-1);
    	img.setRGB(0,7,-3419681);
    	img.setRGB(0,8,-8873758);
    	img.setRGB(0,9,-12027154);
    	img.setRGB(0,10,-14523400);
    	img.setRGB(0,11,-16034306);
    	img.setRGB(0,12,-16034306);
    	img.setRGB(0,13,-14523400);
    	img.setRGB(0,14,-9661979);
    	img.setRGB(0,15,-197637);
    	img.setRGB(1,0,-9661979);
    	img.setRGB(1,1,-16034306);
    	img.setRGB(1,2,-16034306);
    	img.setRGB(1,3,-15705602);
    	img.setRGB(1,4,-8873758);
    	img.setRGB(1,5,-8873758);
    	img.setRGB(1,6,-13669387);
    	img.setRGB(1,7,-16034306);
    	img.setRGB(1,8,-16034306);
    	img.setRGB(1,9,-16034306);
    	img.setRGB(1,10,-16034306);
    	img.setRGB(1,11,-16034306);
    	img.setRGB(1,12,-16034306);
    	img.setRGB(1,13,-16034306);
    	img.setRGB(1,14,-16034306);
    	img.setRGB(1,15,-10450456);
    	img.setRGB(2,0,-14523400);
    	img.setRGB(2,1,-16034306);
    	img.setRGB(2,2,-16034306);
    	img.setRGB(2,3,-16034306);
    	img.setRGB(2,4,-16034306);
    	img.setRGB(2,5,-15246085);
    	img.setRGB(2,6,-12027154);
    	img.setRGB(2,7,-9661979);
    	img.setRGB(2,8,-9661979);
    	img.setRGB(2,9,-9661979);
    	img.setRGB(2,10,-14523400);
    	img.setRGB(2,11,-16034306);
    	img.setRGB(2,12,-16034306);
    	img.setRGB(2,13,-16034306);
    	img.setRGB(2,14,-16034306);
    	img.setRGB(2,15,-15246085);
    	img.setRGB(3,0,-16034306);
    	img.setRGB(3,1,-16034306);
    	img.setRGB(3,2,-16034306);
    	img.setRGB(3,3,-16034306);
    	img.setRGB(3,4,-16034306);
    	img.setRGB(3,5,-15246085);
    	img.setRGB(3,6,-2696218);
    	img.setRGB(3,7,-1);
    	img.setRGB(3,8,-65794);
    	img.setRGB(3,9,-65794);
    	img.setRGB(3,10,-1907480);
    	img.setRGB(3,11,-15246085);
    	img.setRGB(3,12,-16034306);
    	img.setRGB(3,13,-16034306);
    	img.setRGB(3,14,-16034306);
    	img.setRGB(3,15,-13669387);
    	img.setRGB(4,0,-15246085);
    	img.setRGB(4,1,-16034306);
    	img.setRGB(4,2,-16034306);
    	img.setRGB(4,3,-16034306);
    	img.setRGB(4,4,-16034306);
    	img.setRGB(4,5,-16034306);
    	img.setRGB(4,6,-12880911);
    	img.setRGB(4,7,-65793);
    	img.setRGB(4,8,-65794);
    	img.setRGB(4,9,-65794);
    	img.setRGB(4,10,-1907479);
    	img.setRGB(4,11,-14523400);
    	img.setRGB(4,12,-16034306);
    	img.setRGB(4,13,-16034306);
    	img.setRGB(4,14,-15114757);
    	img.setRGB(4,15,-2432789);
    	img.setRGB(5,0,-12880911);
    	img.setRGB(5,1,-16034306);
    	img.setRGB(5,2,-16034306);
    	img.setRGB(5,3,-4601623);
    	img.setRGB(5,4,-4273435);
    	img.setRGB(5,5,-13669387);
    	img.setRGB(5,6,-16034306);
    	img.setRGB(5,7,-8873758);
    	img.setRGB(5,8,-65794);
    	img.setRGB(5,9,-2499358);
    	img.setRGB(5,10,-11304469);
    	img.setRGB(5,11,-16034306);
    	img.setRGB(5,12,-16034306);
    	img.setRGB(5,13,-16034306);
    	img.setRGB(5,14,-3747352);
    	img.setRGB(5,15,-263173);
    	img.setRGB(6,0,-9661979);
    	img.setRGB(6,1,-16034306);
    	img.setRGB(6,2,-12880911);
    	img.setRGB(6,3,-1);
    	img.setRGB(6,4,-1);
    	img.setRGB(6,5,-3353888);
    	img.setRGB(6,6,-15246085);
    	img.setRGB(6,7,-15246085);
    	img.setRGB(6,8,-7362596);
    	img.setRGB(6,9,-13669387);
    	img.setRGB(6,10,-16034306);
    	img.setRGB(6,11,-16034306);
    	img.setRGB(6,12,-10450456);
    	img.setRGB(6,13,-16034306);
    	img.setRGB(6,14,-7690779);
    	img.setRGB(6,15,-197637);
    	img.setRGB(7,0,-4536866);
    	img.setRGB(7,1,-16034306);
    	img.setRGB(7,2,-12880911);
    	img.setRGB(7,3,-1);
    	img.setRGB(7,4,-1);
    	img.setRGB(7,5,-1);
    	img.setRGB(7,6,-8873758);
    	img.setRGB(7,7,-16034306);
    	img.setRGB(7,8,-16034306);
    	img.setRGB(7,9,-16034306);
    	img.setRGB(7,10,-11304469);
    	img.setRGB(7,11,-2696218);
    	img.setRGB(7,12,-197123);
    	img.setRGB(7,13,-12880911);
    	img.setRGB(7,14,-14523400);
    	img.setRGB(7,15,-263173);
    	img.setRGB(8,0,-1);
    	img.setRGB(8,1,-14523400);
    	img.setRGB(8,2,-13669387);
    	img.setRGB(8,3,-1);
    	img.setRGB(8,4,-2301980);
    	img.setRGB(8,5,-11304469);
    	img.setRGB(8,6,-16034306);
    	img.setRGB(8,7,-16034306);
    	img.setRGB(8,8,-16034306);
    	img.setRGB(8,9,-9661979);
    	img.setRGB(8,10,-131587);
    	img.setRGB(8,11,-66051);
    	img.setRGB(8,12,-197380);
    	img.setRGB(8,13,-12880911);
    	img.setRGB(8,14,-16034306);
    	img.setRGB(8,15,-3090201);
    	img.setRGB(9,0,-1);
    	img.setRGB(9,1,-8347933);
    	img.setRGB(9,2,-16034306);
    	img.setRGB(9,3,-9661979);
    	img.setRGB(9,4,-15246085);
    	img.setRGB(9,5,-16034306);
    	img.setRGB(9,6,-14523400);
    	img.setRGB(9,7,-5982238);
    	img.setRGB(9,8,-15246085);
    	img.setRGB(9,9,-15246085);
    	img.setRGB(9,10,-3550492);
    	img.setRGB(9,11,-131587);
    	img.setRGB(9,12,-131844);
    	img.setRGB(9,13,-12880911);
    	img.setRGB(9,14,-16034306);
    	img.setRGB(9,15,-10450456);
    	img.setRGB(10,0,-1);
    	img.setRGB(10,1,-3682076);
    	img.setRGB(10,2,-16034306);
    	img.setRGB(10,3,-16034306);
    	img.setRGB(10,4,-16034306);
    	img.setRGB(10,5,-13669387);
    	img.setRGB(10,6,-1512979);
    	img.setRGB(10,7,-65794);
    	img.setRGB(10,8,-6310687);
    	img.setRGB(10,9,-16034306);
    	img.setRGB(10,10,-15246085);
    	img.setRGB(10,11,-5916445);
    	img.setRGB(10,12,-7099161);
    	img.setRGB(10,13,-16034306);
    	img.setRGB(10,14,-16034306);
    	img.setRGB(10,15,-12880911);
    	img.setRGB(11,0,-3617058);
    	img.setRGB(11,1,-15246085);
    	img.setRGB(11,2,-16034306);
    	img.setRGB(11,3,-16034306);
    	img.setRGB(11,4,-16034306);
    	img.setRGB(11,5,-2039065);
    	img.setRGB(11,6,-1);
    	img.setRGB(11,7,-1);
    	img.setRGB(11,8,-65794);
    	img.setRGB(11,9,-12027154);
    	img.setRGB(11,10,-16034306);
    	img.setRGB(11,11,-16034306);
    	img.setRGB(11,12,-16034306);
    	img.setRGB(11,13,-16034306);
    	img.setRGB(11,14,-16034306);
    	img.setRGB(11,15,-16034306);
    	img.setRGB(12,0,-11304469);
    	img.setRGB(12,1,-16034306);
    	img.setRGB(12,2,-16034306);
    	img.setRGB(12,3,-16034306);
    	img.setRGB(12,4,-16034306);
    	img.setRGB(12,5,-2696218);
    	img.setRGB(12,6,-1);
    	img.setRGB(12,7,-65538);
    	img.setRGB(12,8,-65538);
    	img.setRGB(12,9,-2302236);
    	img.setRGB(12,10,-15246085);
    	img.setRGB(12,11,-16034306);
    	img.setRGB(12,12,-16034306);
    	img.setRGB(12,13,-16034306);
    	img.setRGB(12,14,-16034306);
    	img.setRGB(12,15,-16034306);
    	img.setRGB(13,0,-12880911);
    	img.setRGB(13,1,-16034306);
    	img.setRGB(13,2,-16034306);
    	img.setRGB(13,3,-16034306);
    	img.setRGB(13,4,-16034306);
    	img.setRGB(13,5,-14523400);
    	img.setRGB(13,6,-9661979);
    	img.setRGB(13,7,-9661979);
    	img.setRGB(13,8,-9661979);
    	img.setRGB(13,9,-11304469);
    	img.setRGB(13,10,-15246085);
    	img.setRGB(13,11,-16034306);
    	img.setRGB(13,12,-16034306);
    	img.setRGB(13,13,-16034306);
    	img.setRGB(13,14,-16034306);
    	img.setRGB(13,15,-14523400);
    	img.setRGB(14,0,-9661979);
    	img.setRGB(14,1,-16034306);
    	img.setRGB(14,2,-16034306);
    	img.setRGB(14,3,-16034306);
    	img.setRGB(14,4,-16034306);
    	img.setRGB(14,5,-16034306);
    	img.setRGB(14,6,-16034306);
    	img.setRGB(14,7,-16034306);
    	img.setRGB(14,8,-16034306);
    	img.setRGB(14,9,-14523400);
    	img.setRGB(14,10,-9661979);
    	img.setRGB(14,11,-8873758);
    	img.setRGB(14,12,-15246085);
    	img.setRGB(14,13,-16034306);
    	img.setRGB(14,14,-16034306);
    	img.setRGB(14,15,-4733209);
    	img.setRGB(15,0,-1);
    	img.setRGB(15,1,-9661979);
    	img.setRGB(15,2,-13669387);
    	img.setRGB(15,3,-16034306);
    	img.setRGB(15,4,-16034306);
    	img.setRGB(15,5,-14523400);
    	img.setRGB(15,6,-12027154);
    	img.setRGB(15,7,-8873758);
    	img.setRGB(15,8,-3090456);
    	img.setRGB(15,9,-65794);
    	img.setRGB(15,10,-65794);
    	img.setRGB(15,11,-131587);
    	img.setRGB(15,12,-2893596);
    	img.setRGB(15,13,-8873758);
    	img.setRGB(15,14,-4667678);
    	img.setRGB(15,15,-197637);
    	return img;
    }

    public static void setFrameSize(int WIDTH,int HEIGHT, InputStream iconfile)
    {
    	JFrame frame=app_frame;
    	frame.getContentPane().removeAll();
        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(app, BorderLayout.CENTER);
        
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setLocation(d.width/2 - WIDTH/2, d.height/2 - HEIGHT/2);
        frame.setSize(WIDTH, HEIGHT);
        frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        if(iconfile!=null)
        {	
        	try{DWApp.frame_icon=ImageIO.read(iconfile);
        	    frame.setIconImage(DWApp.frame_icon);
        	}catch(IOException e){}
        }
        else
        {
        	DWApp.frame_icon=(BufferedImage) DWApp.DWIcon();
        	frame.setIconImage(DWApp.frame_icon);
        }
    	
        //frame.setIconImage(DWIcon());
          
        
        frame.validate();
        frame.repaint();
        
        try{
        frame.getFocusTraversalPolicy()
             .getDefaultComponent(frame)
             .requestFocus();
        }catch(NullPointerException e){}
        
        app.start();


    }
    
    public static void addToGridBag(JPanel panel, Component comp,
            int x, int y, int w, int h, double weightx, double weighty) {

        GridBagLayout gbl = (GridBagLayout) panel.getLayout();
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.gridx = x;
        c.gridy = y;
        c.gridwidth = w;
        c.gridheight = h;
        c.weightx = weightx;
        c.weighty = weighty;
        panel.add(comp);
        gbl.setConstraints(comp, c);
    }

    public static void openURL(String url) {
        String osName = System.getProperty("os.name");
        try {
                if (osName.startsWith("Windows"))
                        Runtime.getRuntime().exec(
                                        "rundll32 url.dll,FileProtocolHandler " + url);
                else {
                        String[] browsers = { "firefox", "opera", "konqueror",
                                        "epiphany", "mozilla", "netscape" };
                        String browser = null;
                        for (int count = 0; count < browsers.length && browser == null; count++)
                                if (Runtime.getRuntime().exec(
                                                new String[] { "which", browsers[count] })
                                                .waitFor() == 0)
                                        browser = browsers[count];
                        Runtime.getRuntime().exec(new String[] { browser, url });
                }
        } catch (Exception e) {
                }
}
    
    class SetLocationDialog extends JDialog
    {
   	 JTextField text_X;
   	 JTextField text_Y;
   	 JButton button_ok;
   	 
   	 SetLocationDialog()
   	 {
   		 setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
   	     setTitle("Set window location");
   	     setIconImage(DWApp.DWIcon());
   	     setModal(true);
   	     setSize(200,100);
   	     setLocationRelativeTo(null); 
   	     
   	     setLayout(new BorderLayout());
   	     JPanel p_root=new JPanel(new BorderLayout());
   	     
   	     
   	     JPanel p_=new JPanel(new GridBagLayout());
   	     
   	     Point p=app_frame.getLocation();
   	     
   	     JPanel tmp=new JPanel(new BorderLayout());
   	     tmp.add(new JLabel("X:"), BorderLayout.WEST);
   	     text_X=new JTextField(""+p.x);
   	     text_X.getDocument().addDocumentListener(new DocumentListener() {
   	    	  public void changedUpdate(DocumentEvent e) {warn();}
   	    	  public void removeUpdate(DocumentEvent e) {warn();}
   	    	  public void insertUpdate(DocumentEvent e) {warn();}
   	    	  public void warn() {
   	    		try
 	    		{app_frame.setLocation(Integer.parseInt(text_X.getText()), Integer.parseInt(text_Y.getText()));}
 	    		catch(NumberFormatException e){}
 	    	  }});
   	     tmp.add(text_X,BorderLayout.CENTER);
   	     DWApp.addToGridBag(p_, tmp, 0, 0, 1, 1, 1.0, 1.0);
   	     
   	     tmp=new JPanel(new BorderLayout());
   	  	 tmp.add(new JLabel("Y:"), BorderLayout.WEST);
   	  	 text_Y=new JTextField(""+p.y);
   	     text_Y.getDocument().addDocumentListener(new DocumentListener() {
   	    	  public void changedUpdate(DocumentEvent e) {warn();}
   	    	  public void removeUpdate(DocumentEvent e) {warn();}
   	    	  public void insertUpdate(DocumentEvent e) {warn();}
   	    	  public void warn() {
   	    		  try
   	    		  {app_frame.setLocation(Integer.parseInt(text_X.getText()), Integer.parseInt(text_Y.getText()));}
   	    		  catch(NumberFormatException e){}
   	    	  }});
   	     tmp.add(text_Y,BorderLayout.CENTER);
	     DWApp.addToGridBag(p_, tmp, 0, 1, 1, 1, 1.0, 1.0);
	     
   	     
    	 p_root.add(p_,BorderLayout.CENTER);
   	     
   	     add(p_root,BorderLayout.CENTER);
   	     setVisible(true);
   	     
   	 }
    }
    
    class SetSizeDialog extends JDialog
    {
   	 JTextField text_X;
   	 JTextField text_Y;
   	 JButton button_ok;
   	 
   	 SetSizeDialog()
   	 {
   		 setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
   	     setTitle("Set window size");
   	     setIconImage(DWApp.DWIcon());
   	     setModal(true);
   	     setSize(200,100);
   	     setLocationRelativeTo(null); 
   	     
   	     setLayout(new BorderLayout());
   	     JPanel p_root=new JPanel(new BorderLayout());
   	     
   	     
   	     JPanel p_=new JPanel(new GridBagLayout());
   	     
   	     Dimension p=app_frame.getSize();
   	     
   	     JPanel tmp=new JPanel(new BorderLayout());
   	     tmp.add(new JLabel("X:"), BorderLayout.WEST);
   	     text_X=new JTextField(""+p.width);
   	     text_X.getDocument().addDocumentListener(new DocumentListener() {
   	    	  public void changedUpdate(DocumentEvent e) {warn();}
   	    	  public void removeUpdate(DocumentEvent e) {warn();}
   	    	  public void insertUpdate(DocumentEvent e) {warn();}
   	    	  public void warn() {
   	    		try
 	    		{app_frame.setSize(Integer.parseInt(text_X.getText()), Integer.parseInt(text_Y.getText()));}
 	    		catch(NumberFormatException e){}
 	    	  }});
   	     tmp.add(text_X,BorderLayout.CENTER);
   	     DWApp.addToGridBag(p_, tmp, 0, 0, 1, 1, 1.0, 1.0);
   	     
   	     tmp=new JPanel(new BorderLayout());
   	  	 tmp.add(new JLabel("Y:"), BorderLayout.WEST);
   	  	 text_Y=new JTextField(""+p.height);
   	     text_Y.getDocument().addDocumentListener(new DocumentListener() {
   	    	  public void changedUpdate(DocumentEvent e) {warn();}
   	    	  public void removeUpdate(DocumentEvent e) {warn();}
   	    	  public void insertUpdate(DocumentEvent e) {warn();}
   	    	  public void warn() {
   	    		  try
   	    		  {app_frame.setSize(Integer.parseInt(text_X.getText()), Integer.parseInt(text_Y.getText()));}
   	    		  catch(NumberFormatException e){}
   	    	  }});
   	     tmp.add(text_Y,BorderLayout.CENTER);
	     DWApp.addToGridBag(p_, tmp, 0, 1, 1, 1, 1.0, 1.0);
	     
   	     
    	 p_root.add(p_,BorderLayout.CENTER);
   	     
   	     add(p_root,BorderLayout.CENTER);
   	     setVisible(true);
   	     
   	 }
    }
}
