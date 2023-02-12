package com.vsl700.joyremote;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.math.Vector2;
import com.vsl700.joyremote.MyGdxGame;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Timer;
import java.util.TimerTask;

import dev.gustavoavila.websocketclient.WebSocketClient;

public class AndroidLauncher extends AndroidApplication implements MyGdxGame.JoystickValuesListener {

	private MyGdxGame game;
	private WebSocketClient ws;

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		initialize(game = new MyGdxGame(this) {
			@Override
			protected void onPortEntered(String port) {
				createWebSocket(port);
			}
		}, config);
	}

	private void createWebSocket(String port){
		URI uri;
		try {
			uri = new URI("ws://192.168.0.107:8080/RemoteJoystickDynWebProject/websocket/rj?port=" + port);
		} catch (URISyntaxException e) {
			e.printStackTrace();
			return;
		}

		ws = new WebSocketClient(uri) {
			@Override
			public void onOpen() {
				System.out.println("Connection Opened!");
				game.onConnectionOpened();
			}

			@Override
			public void onTextReceived(String s) {
				System.out.println("Message: " + s);
			}

			@Override
			public void onBinaryReceived(byte[] bytes) {

			}

			@Override
			public void onPingReceived(byte[] bytes) {

			}

			@Override
			public void onPongReceived(byte[] bytes) {

			}

			@Override
			public void onException(Exception e) {
				e.printStackTrace();
				game.onConnectionClosed();

				ws = null;
			}

			@Override
			public void onCloseReceived() {
				System.out.println("Connection Closed!");
				game.onConnectionClosed();

				ws = null;
			}
		};
		ws.addHeader("Mobile", "true");
		ws.connect();
		System.out.println("Connecting...");
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();

		if(ws == null)
			return;

		ws.close();
		ws = null;
	}

	@Override
	public void onJoystickDirChanged(Vector2 joystickDir) {
		if(ws == null)
			return;

		JSONObject obj = new JSONObject();
		try {
			obj.put("joyX", joystickDir.x);
			obj.put("joyY", joystickDir.y);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		ws.send(obj.toString());
	}

	@Override
	public void onShiftStateChanged(boolean touched) {
		if(ws == null)
			return;

		JSONObject obj = new JSONObject();
		try {
			obj.put("shift", touched);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		ws.send(obj.toString());
	}

	@Override
	public void onBtnAStateChanged(boolean touched) {
		if(ws == null)
			return;

		JSONObject obj = new JSONObject();
		try {
			obj.put("btnA", touched);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		ws.send(obj.toString());
	}

	@Override
	public void onBtnBStateChanged(boolean touched) {
		if(ws == null)
			return;

		JSONObject obj = new JSONObject();
		try {
			obj.put("btnB", touched);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		ws.send(obj.toString());
	}

	@Override
	public void onBtnCStateChanged(boolean touched) {
		if(ws == null)
			return;

		JSONObject obj = new JSONObject();
		try {
			obj.put("btnC", touched);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		ws.send(obj.toString());
	}

	@Override
	public void onBtnPStateChanged(boolean touched) {
		if(ws == null)
			return;

		JSONObject obj = new JSONObject();
		try {
			obj.put("btnP", touched);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		ws.send(obj.toString());
	}

	@Override
	public void onBtnNStateChanged(boolean touched) {
		if(ws == null)
			return;

		JSONObject obj = new JSONObject();
		try {
			obj.put("btnN", touched);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		ws.send(obj.toString());
	}
}
