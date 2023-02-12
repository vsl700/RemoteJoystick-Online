package com.vsl700.joyremote.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.math.Vector2;
import com.vsl700.joyremote.MyGdxGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		new LwjglApplication(new MyGdxGame(new MyGdxGame.JoystickValuesListener() {
			@Override
			public void onJoystickDirChanged(Vector2 joystickDir) {

			}

			@Override
			public void onShiftStateChanged(boolean touched) {

			}

			@Override
			public void onBtnAStateChanged(boolean touched) {

			}

			@Override
			public void onBtnBStateChanged(boolean touched) {

			}

			@Override
			public void onBtnCStateChanged(boolean touched) {

			}

			@Override
			public void onBtnPStateChanged(boolean touched) {

			}

			@Override
			public void onBtnNStateChanged(boolean touched) {

			}
		}) {
			@Override
			protected void onPortEntered(String port) {
				System.out.println(port);
			}
		}, config);
	}
}
