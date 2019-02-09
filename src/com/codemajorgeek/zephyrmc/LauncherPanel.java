package com.codemajorgeek.zephyrmc;

import static fr.theshark34.swinger.Swinger.*;

import java.awt.*;
import java.io.*;
import java.net.*;

import javax.swing.*;

import org.json.*;

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
	private JPasswordField passwdField = new JPasswordField(saver.get("password"));

	private STexturedButton playButton = new STexturedButton(getResource("play.png"));
	private STexturedButton quitButton = new STexturedButton(getResource("quit.png"));
	private STexturedCheckbox saveBox = new STexturedCheckbox(getResource("save.png"), getResource("savechecked.png"));

	private SColoredBar progressBar = new SColoredBar(new Color(245, 0, 0, 100), new Color(255, 0, 0, 200));
	private JLabel infoLabel = new JLabel("Cliquez sur jouer !", SwingConstants.CENTER);

	public LauncherPanel() {
		setLayout(null);

		usernameField.setBounds(280, 267, 163, 39);
		usernameField.setOpaque(false);
		usernameField.setBorder(null);
		usernameField.setForeground(Color.BLACK);
		usernameField.setFont(this.usernameField.getFont().deriveFont(25.0F));
		usernameField.setCaretColor(Color.BLACK);
		
		passwdField.setBounds(282, 334, 163, 39);
		passwdField.setOpaque(false);
		passwdField.setBorder(null);
		passwdField.setForeground(Color.BLACK);
		passwdField.setFont(this.passwdField.getFont().deriveFont(25.0F));
		passwdField.setCaretColor(Color.BLACK);
		
		playButton.setBounds(278, 386, 147, 45);
		playButton.addEventListener(this);
		
		quitButton.setBounds(608, 117, 32, 32);
		quitButton.addEventListener(this);
		
		saveBox.setBounds(461, 312, 14, 14);
		saveBox.setEnabled(Boolean.parseBoolean(saver.get("checked")));
		
		infoLabel.setHorizontalAlignment(SwingConstants.CENTER);
		infoLabel.setBounds(12, 560, 951, 30);
		infoLabel.setFont(usernameField.getFont());
		
		progressBar.setBounds(160, 440, 407, 10);

		add(usernameField);
		add(passwdField);

		add(playButton);
		add(quitButton);

		add(saveBox);

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
			if (usernameField.getText().replaceAll(" ", "").length() == 0 || passwdField.getPassword().length == 0) {

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

						Launcher.auth(usernameField.getText(), passwdField.getPassword());
					} catch (MalformedURLException e) {

						JOptionPane.showMessageDialog(LauncherPanel.this,
								"Impossible de ce connecter: " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
						passwdField.setText("");
						setFieldEnabled(true);
						return;
					}
					
					if(saveBox.isChecked()) {
						
						saver.set("username", usernameField.getText());
						saver.set("password", new String(passwdField.getPassword()));
						saver.set("checked", "true");
					} else {
						
						saver.set("username", "");
						saver.set("password", "");
						saver.set("checked", "false");
					}
					try {

						Launcher.update();
					} catch (Exception e) {

						Launcher.interruptThread();
						Launcher.getCrashReporter().catchError(e, "Imposible de mettre à jour ZephyrMC !");
						return;
					}

					try {

						Launcher.launch();
					} catch (LaunchException | IOException | JSONException e) {

						Launcher.getCrashReporter().catchError(e, "Imposible de lancer ZephyrMC !");
						setFieldEnabled(true);
					}
				}
			};
			authThread.start();
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
