package com.codemajorgeek.zephirmc;

import java.io.*;

import fr.litarvan.openauth.*;
import fr.litarvan.openauth.model.*;
import fr.litarvan.openauth.model.response.*;
import fr.theshark34.openlauncherlib.launcher.*;
import fr.theshark34.supdate.*;
import fr.theshark34.supdate.application.integrated.*;
import fr.theshark34.swinger.*;

public class Launcher {

	public static final GameVersion ZMC_VERSION = new GameVersion("1.7.10", GameType.V1_7_10);
	public static final GameInfos ZMC_INFOS = new GameInfos("ZephirMC", ZMC_VERSION, true,
			new GameTweak[] { GameTweak.FORGE });
	public static final File ZMC_DIR = ZMC_INFOS.getGameDir();
	//public static final GameFolder SC_FOLDER = new GameFolder("ressources/assets/", "ressources/libraries/", "./", "jar/zphirMC.jar");

	private static AuthInfos authInfos;
	
	private static Thread progressBarThread;
	
	public static void auth(String username, String passwd) throws AuthenticationException {

		Authenticator authenticator = new Authenticator(Authenticator.MOJANG_AUTH_URL, AuthPoints.NORMAL_AUTH_POINTS);
		AuthResponse response = authenticator.authenticate(AuthAgent.MINECRAFT, username, passwd, "");

		authInfos = new AuthInfos(response.getSelectedProfile().getName(), response.getAccessToken(),
				response.getSelectedProfile().getId());
	}

	public static void update() throws Exception {

		SUpdate su = new SUpdate("http://localhost/S-UpdateServer/", ZMC_DIR);
		su.addApplication(new FileDeleter());
		
		
		progressBarThread = new Thread("progressBarThread") {

			@Override
			public void run() {

				int val;
				int max;
				while (!this.isInterrupted()) {
					if(BarAPI.getNumberOfFileToDownload() == 0) {
						
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
	
	public static void launch() throws IOException{
		
		GameLauncher gameLauncher = new GameLauncher(ZMC_INFOS, GameFolder.BASIC, authInfos);
		Process p = gameLauncher.launch();
		
		try {
			
			Thread.sleep(5);
		} catch (InterruptedException e) {
			
			e.printStackTrace();
			System.exit(1);
		}
		
		LauncherFrame.getInstance().setVisible(false);
		
		try {
			
			p.waitFor();
		} catch (InterruptedException e) {
			
			e.printStackTrace();
			System.exit(1);
		}
		
		System.exit(0);
	}
}
