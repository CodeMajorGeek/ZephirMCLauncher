package com.codemajorgeek.zephyrmc;

import java.io.*;

import fr.litarvan.openauth.*;
import fr.litarvan.openauth.model.*;
import fr.litarvan.openauth.model.response.*;
import fr.theshark34.openlauncherlib.*;
import fr.theshark34.openlauncherlib.external.*;
import fr.theshark34.openlauncherlib.minecraft.*;
import fr.theshark34.openlauncherlib.util.*;
import fr.theshark34.supdate.*;
import fr.theshark34.supdate.application.integrated.*;
import fr.theshark34.swinger.*;

public class Launcher {

	public static final GameVersion ZMC_VERSION = new GameVersion("1.7.10", GameType.V1_7_10);
	public static final GameInfos ZMC_INFOS = new GameInfos("ZephyrMC", ZMC_VERSION,
			new GameTweak[] { GameTweak.FORGE });
	public static final File ZMC_DIR = ZMC_INFOS.getGameDir();

	private static AuthInfos authInfos;
	private static Thread progressBarThread;
	
	private static CrashReporter crashReporter = new CrashReporter("crash", new File(ZMC_DIR, "crashes"));
	
	public static void auth(String username, String passwd) throws AuthenticationException {

		Authenticator authenticator = new Authenticator(Authenticator.MOJANG_AUTH_URL, AuthPoints.NORMAL_AUTH_POINTS);
		AuthResponse response = authenticator.authenticate(AuthAgent.MINECRAFT, username, passwd, "");

		authInfos = new AuthInfos(response.getSelectedProfile().getName(), response.getAccessToken(),
				response.getSelectedProfile().getId());
	}

	public static void update() throws Exception {
		
		//TODO: temp localhost
		SUpdate su = new SUpdate("http://127.0.0.1/", ZMC_DIR);
		
		//SUpdate su = new SUpdate("http://88.123.20.173/", ZMC_DIR);
		su.addApplication(new FileDeleter());

		progressBarThread = new Thread("progressBarThread") {

			@Override
			public void run() {

				int val;
				int max;
				while (!this.isInterrupted()) {
					if (BarAPI.getNumberOfFileToDownload() == 0) {

						LauncherFrame.getInstance().getLauncherPanel().setInfoText("Verification des fichiers");
						continue;
					}
					val = (int) (BarAPI.getNumberOfTotalDownloadedBytes() / 1000);
					max = (int) (BarAPI.getNumberOfTotalBytesToDownload() / 1000);

					LauncherFrame.getInstance().getLauncherPanel().getProgressBar().setMaximum(max);
					LauncherFrame.getInstance().getLauncherPanel().getProgressBar().setValue(val);

					LauncherFrame.getInstance().getLauncherPanel()
							.setInfoText("Telechargement des fichiers " + BarAPI.getNumberOfDownloadedFiles() + "/"
									+ BarAPI.getNumberOfTotalDownloadedBytes() + " " + Swinger.percentage(val, max)
									+ " %");
				}
			}
		};
		progressBarThread.start();
		su.start();
		progressBarThread.interrupt();
	}

	public static void interruptThread() {

		progressBarThread.interrupt();
	}
	
	public static CrashReporter getCrashReporter() {
		
		return crashReporter;
	}
	
	public static void launch() throws LaunchException {
		
		ExternalLaunchProfile profile = MinecraftLauncher.createExternalProfile(ZMC_INFOS, GameFolder.BASIC, authInfos);
		ExternalLauncher launcher = new ExternalLauncher(profile);
		
		LauncherFrame.getInstance().setVisible(false);
		
		launcher.launch();
		System.exit(0);
	}
}
