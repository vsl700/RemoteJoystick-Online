package com.vsl700.joyremote;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.websocket.CloseReason;
import jakarta.websocket.CloseReason.CloseCode;
import jakarta.websocket.EndpointConfig;
import jakarta.websocket.HandshakeResponse;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.HandshakeRequest;
import jakarta.websocket.server.ServerEndpoint;
import jakarta.websocket.server.ServerEndpointConfig;

@ServerEndpoint(value = "/websocket/rj", configurator=JoystickWSClient.class)
public class JoystickWSClient extends ServerEndpointConfig.Configurator {
	
	private class ConnectionCouple{
		private JoystickWSClient desktopClient, mobileClient;
		
		public ConnectionCouple(JoystickWSClient desktopClient) {
			this.desktopClient = desktopClient;
		}

		public JoystickWSClient getDesktopClient() {
			return desktopClient;
		}

		public JoystickWSClient getMobileClient() {
			return mobileClient;
		}

		public void setDesktopClient(JoystickWSClient desktopClient) {
			this.desktopClient = desktopClient;
		}

		public void setMobileClient(JoystickWSClient mobileClient) {
			this.mobileClient = mobileClient;
		}
		
		public boolean isEmpty() {
			return desktopClient == null && mobileClient == null;
		}
	}
	
	static final HashMap<Integer, ConnectionCouple> couples = new HashMap<>();
	private Session session;
	private int port;
	private boolean mobile;
	
	
	@OnOpen
	public void onOpen(Session session) throws IOException {
		this.session = session;
		port = getPort();
		mobile = isMobile();
		
		if(mobile) { // MOBILE
			if(!couples.containsKey(port)) { // 'NO SUCH PORT' RESTRICTION
				session.close(new CloseReason(new CloseCode() {
	
					@Override
					public int getCode() {
						return 400;
					}}, "No such port exists"));
				
				return;
			}
			
			couples.get(port).setMobileClient(this);
		}else if(!couples.containsKey(port)){ // DESKTOP
			var couple = new ConnectionCouple(this);
			couples.put(port, couple);
		}else { // THIS PORT IS ALREADY OCCUPIED
			session.close(new CloseReason(new CloseCode() {
				
				@Override
				public int getCode() {
					return 400;
				}}, "This port is already occupied"));
		}
	}
	
	private int getPort() {
		return Integer.parseInt(session.getRequestParameterMap().get("port").get(0));
	}
	
	private boolean isMobile() {
		return Boolean.parseBoolean(session.getUserProperties().get("mobile").toString());
	}
	
	@OnMessage
	public void onMessage(String message) {
		if(mobile) {
			couples.get(port).getDesktopClient().sendMessage(message);
		}
	}
	
	public void sendMessage(String message) {
		try {
			session.getBasicRemote().sendText(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@OnError
	public void onError(Throwable e) {
		e.printStackTrace();
	}
	
	@OnClose
	public void onClose(Session session) {
		var couple = couples.get(port);
		if(couple == null)
			return;
		
		if(mobile) {
			couple.setMobileClient(null);
			
			if(couple.getDesktopClient() != null) {
				couple.getDesktopClient().sendMessage("Mobile Disconnected");
			}
		}else {
			couple.setDesktopClient(null);
			
			if(couple.getMobileClient() != null) {
				couple.getMobileClient().closeConnection();
				couple.setMobileClient(null);
			}
			
			couples.remove(port);
		}
	}
	
	public void closeConnection() {
		try {
			session.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
    public void modifyHandshake(ServerEndpointConfig config, 
                                HandshakeRequest request, 
                                HandshakeResponse response)
    {
		//super.modifyHandshake(config, request, response);
		
        Map<String,List<String>> headers = request.getHeaders();
        var header = headers.get("Mobile"); 
        boolean mobile;
        if(header == null)
        	mobile = false;
        else mobile = Boolean.parseBoolean(header.get(0));
        
        // The system creates one instance of this class when working with this method, and another when working
        // with the 'onOpen' method! I had to pass the value through the UserParameters map!
        config.getUserProperties().put("mobile", mobile);
    }
}
