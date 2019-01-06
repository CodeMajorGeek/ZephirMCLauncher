package com.codemajorgeek.zephyrmc;

import static fr.theshark34.swinger.Swinger.*;

import java.awt.*;
import java.io.*;

import javax.swing.*;

import fr.litarvan.openauth.*;
import fr.theshark34.openlauncherlib.*;
import fr.theshark34.openlauncherlib.util.*;
import fr.theshark34.swinger.animation.*;
import fr.theshark34.swinger.colored.*;
import fr.theshark34.swinger.event.*;
import fr.theshark34.swinger.textured.*;

public class LauncherPanel extends JPanel implements SwingerEventListener {

	private static final long serialVersionUID = 4407172249765338308L;

	private Image background = getResource("bg.png");
	private Saver saver = new Saver(new File(Launcher.ZMC_DIR, "launcher.properties"));

	private JTextField usernameField = new JTextField(saver.get("username"));
	private JPasswordField passwdField = new JPasswordField();

	private STexturedButton playButton = new STexturedButton(getResource("play.png"));
	private STexturedButton hideButton = new STexturedButton(getResource("hide.png"));
	private STexturedButton quitButton = new STexturedButton(getResource("quit.png"));
	
	private SColoredBar progressBar = new SColoredBar(getTransparentWhite(100), getTransparentWhite(161));
	private JLabel infoLabel = new JLabel("Cliquez sur jouer !", SwingConstants.CENTER);
	
	public LauncherPanel() {
		setLayout(null);

		usernameField.setBounds(595, 191, 332, 55);
		usernameField.setOpaque(false);
		usernameField.setBorder(null);
		usernameField.setForeground(Color.BLACK);
		usernameField.setFont(usernameField.getFont().deriveFont(25F));
		usernameField.setCaretColor(Color.BLACK);

		passwdField.setBounds(595, 320, 332, 55);
		passwdField.setOpaque(false);
		passwdField.setBorder(null);
		passwdField.setForeground(Color.BLACK);
		passwdField.setFont(passwdField.getFont().deriveFont(25F));
		passwdField.setCaretColor(Color.BLACK);

		playButton.setBounds(568, 467, 347, 77);
		playButton.addEventListener(this);

		hideButton.setBounds(875, 0, 50, 50);
		hideButton.addEventListener(this);

		quitButton.setBounds(925, 0, 50, 50);
		quitButton.addEventListener(this);
		
		infoLabel.setHorizontalAlignment(SwingConstants.CENTER);
		infoLabel.setBounds(12, 560, 951, 30);
		infoLabel.setFont(usernameField.getFont());
		
		progressBar.setBounds(12, 593, 951, 20);
		
		add(usernameField);
		add(passwdField);

		add(playButton);
		add(hideButton);
		add(quitButton);
		
		add(infoLabel);
		add(progressBar);
	}

	private void setFieldEnabled(boolean enabled) {

		usernameField.setEnabled(enabled);
		passwdField.setEnabled(enabled);
		playButton.setEnabled(enabled);
	}
	
	public SColoredBar getProgressBar() {
		
		return progressBar;
	}
	
	public void setInfoText(String text) {
		
		infoLabel.setText(text);
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		drawFullsizedImage(g, this, background);
	}

	@Override
	public void onEvent(SwingerEvent e) {

		if (e.getSource().equals(playButton)) {

			setFieldEnabled(false);
			if (usernameField.getText().replaceAll(" ", "").length() == 0 || passwdField.getText().length() == 0) {

				JOptionPane.showMessageDialog(this, "Merci de rentrer un user/mdp valide !", "Erreur",
						JOptionPane.ERROR_MESSAGE);
				usernameField.setText("");
				passwdField.setText("");
				setFieldEnabled(true);
				return;
			}

			Thread authThread = new Thread("authThread") {

				@Override
				public void run() {

					try {
						
						Launcher.auth(usernameField.getText(), passwdField.getText());
					} catch (AuthenticationException e) {

						JOptionPane.showMessageDialog(LauncherPanel.this,
								"Impossible de ce connecter: " + e.getErrorModel().getErrorMessage(), "Erreur",
								JOptionPane.ERROR_MESSAGE);
						passwdField.setText("");
						setFieldEnabled(true);
						return;
					}
					
					saver.set("username", usernameField.getText());
					try {
						
						Launcher.update();
					} catch (Exception e) {
						
						Launcher.interruptThread();
						Launcher.getCrashReporter().catchError(e, "Imposible de mettre à jour ZephyrMC !");
						return;
					}
					
					try {
						
						Launcher.launch();
					} catch (LaunchException e) {

						Launcher.getCrashReporter().catchError(e, "Imposible de lancer ZephyrMC !");
						setFieldEnabled(true);
					}
				}
			};
			authThread.start();
		} else if (e.getSource().equals(hideButton)) {

			LauncherFrame.getInstance().setState(JFrame.ICONIFIED);
		} else if (e.getSource().equals(quitButton)) {
			
			Animator.fadeOutFrame(LauncherFrame.getInstance(), Animator.FAST, new Runnable() {
				
				@Override
				public void run() {
					
					System.exit(0);
				}
			});
		}
	}
}
