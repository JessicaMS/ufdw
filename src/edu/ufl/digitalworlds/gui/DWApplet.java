package edu.ufl.digitalworlds.gui;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.*;

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
public abstract class DWApplet extends JApplet {

    public static JApplet applet=null;
    public static JPanel applet_panel;
    public static int frame_width;
    public static int frame_height;

    public void createMainFrame(String title)
    {
        applet = this;
        DWApp.frame_title=title;
        
        
        applet_panel = new JPanel();
        JPanel panel=applet_panel;
        getContentPane().add(panel,BorderLayout.CENTER);
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        
        JPanel progressPanel = new JPanel() {
            public Insets getInsets() {
                return new Insets(40,30,20,30);
            }
        };
        progressPanel.setLayout(new BoxLayout(progressPanel, BoxLayout.Y_AXIS));

        panel.add(Box.createGlue());
        panel.add(progressPanel);
        panel.add(Box.createGlue());

        progressPanel.add(Box.createGlue());

        Dimension d = new Dimension(400, 20);
        DWApp.progressLabel = new JLabel("Loading, please wait...");
        DWApp.progressLabel.setMaximumSize(d);
        progressPanel.add(DWApp.progressLabel);
        progressPanel.add(Box.createRigidArea(new Dimension(1,20)));

        DWApp.progressBar = new JProgressBar();
        DWApp.progressBar.setStringPainted(true);
        DWApp.progressLabel.setLabelFor(DWApp.progressBar);
        DWApp.progressBar.setAlignmentX(CENTER_ALIGNMENT);
        DWApp.progressBar.setMaximumSize(d);
        DWApp.progressBar.setMinimum(0);
        DWApp.progressBar.setValue(0);
        progressPanel.add(DWApp.progressBar);
        progressPanel.add(Box.createGlue());
        progressPanel.add(Box.createGlue());

        Rectangle ab = getContentPane().getBounds();
        panel.setPreferredSize(new Dimension(ab.width,ab.height));
        getContentPane().add(panel,BorderLayout.CENTER);
        validate();
        setVisible(true);
    }
    
    public void setFrameSize(int WIDTH,int HEIGHT, InputStream iconfile) {


    	JPanel panel=applet_panel;
    	frame_width=WIDTH;
    	frame_height=HEIGHT;
        getContentPane().remove(panel);
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(DWApp.app, BorderLayout.CENTER);

        if(iconfile!=null)
        {
        	try{DWApp.frame_icon=ImageIO.read(iconfile);}catch(IOException e){}
    	}
        else
        {
        	DWApp.frame_icon=(BufferedImage) DWApp.DWIcon();
        }
    	
        validate();
        repaint();
    }
    
    public void start() {
        DWApp.app.start();
    }

    public void stop() {
        DWApp.app.stop();
    }
}
