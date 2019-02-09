package com.codemajorgeek.zephyrmc;

import javax.swing.*;

import com.sun.awt.*;

import fr.theshark34.swinger.*;
import fr.theshark34.swinger.animation.*;
import fr.theshark34.swinger.util.*;

public class LauncherFrame extends JFrame {

	private static final long serialVersionUID = 7252277129218697224L;
	private static LauncherFrame instance;
	private LauncherPanel panel;

	public LauncherFrame() {

		setTitle("ZephyrMC Launcher");
		setSize(704, 493);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setUndecorated(true);

		setIconImage(Swinger.getResource("icon.png"));

		panel = new LauncherPanel();
		setContentPane(panel);
		AWTUtilities.setWindowOpaque(this, false);
		AWTUtilities.setWindowOpacity(this, 0.f);

		WindowMover mover = new WindowMover(this);
		addMouseListener(mover);
		addMouseMotionListener(mover);

		setVisible(true);

		Animator.fadeInFrame(this, Animator.FAST);
	}

	public static void main(String[] args) {

		Swinger.setSystemLookNFeel();
		Swinger.setResourcePath("/res/");

		Launcher.ZMC_DIR.mkdirs();

		instance = new LauncherFrame();
	}

	public static LauncherFrame getInstance() {

		return instance;
	}

	public LauncherPanel getLauncherPanel() {

		return panel;
	}
}
