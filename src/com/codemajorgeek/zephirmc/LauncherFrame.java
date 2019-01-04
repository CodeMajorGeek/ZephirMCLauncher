package com.codemajorgeek.zephirmc;

import javax.swing.*;

import fr.theshark34.openlauncherlib.launcher.util.*;
import fr.theshark34.swinger.*;

public class LauncherFrame extends JFrame{
	
	private static final long serialVersionUID = 7252277129218697224L;
	private static LauncherFrame instance;
	private LauncherPanel panel;
	
	public LauncherFrame() {
		
		setTitle("ZephirMC Launcher");
		setSize(975, 625);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setUndecorated(true);
		setIconImage(Swinger.getResource("icon.png"));
		
		panel = new LauncherPanel();
		setContentPane(panel);
		
		WindowMover mover = new WindowMover(this);
		addMouseListener(mover);
		addMouseMotionListener(mover);
		
		setVisible(true);
	}
	
	public static void main(String[] args) {
		
		Swinger.setSystemLookNFeel();
		Swinger.setResourcePath("/res/");
		
		instance = new LauncherFrame();
	}
	
	public static LauncherFrame getInstance() {
		
		return instance;
	}
	
	public LauncherPanel getLauncherPanel() {
		
		return panel;
	}
}
