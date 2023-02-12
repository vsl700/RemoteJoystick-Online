package com.vsl700.joyremote.tests;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.net.URI;
import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;

import com.vsl700.joyremote.JoystickWSClient;

import jakarta.websocket.CloseReason;
import jakarta.websocket.Extension;
import jakarta.websocket.MessageHandler;
import jakarta.websocket.Session;
import jakarta.websocket.WebSocketContainer;
import jakarta.websocket.MessageHandler.Partial;
import jakarta.websocket.MessageHandler.Whole;
import jakarta.websocket.RemoteEndpoint.Async;
import jakarta.websocket.RemoteEndpoint.Basic;

class ClientHandleTests {

	private static final ArrayList<JoystickWSClient> clients = new ArrayList<>();
	
	@Test
	void test1() {
		var session1 = new TestSession(3999, false);
		var session2 = new TestSession(3999, true);
		var session3 = new TestSession(3999, false);
		
		
		addClient(session1);
		addClient(session2);
		addClient(session3);
		
		assertTrue(session1.isOpen());
		assertTrue(session2.isOpen());
		assertFalse(session3.isOpen());
		
		clearClients();
	}
	
	@Test
	void test2() {
		var session1 = new TestSession(3999, false);
		var session2 = new TestSession(3999, true);
		var session3 = new TestSession(3998, true);
		
		
		addClient(session1);
		addClient(session2);
		addClient(session3);
		
		assertTrue(session1.isOpen());
		assertTrue(session2.isOpen());
		assertFalse(session3.isOpen());
		
		clearClients();
	}
	
	@Test
	void test3() {
		var session1 = new TestSession(3999, false);
		var session2 = new TestSession(3999, true);
		
		
		var client = addClient(session1);
		addClient(session2);
		
		removeClient(client);
		
		assertFalse(session2.isOpen());
		
		clearClients();
	}
	
	@Test
	void test4() {
		var session1 = new TestSession(3999, false);
		var session2 = new TestSession(3999, true);
		var session3 = new TestSession(3999, true);
		
		
		addClient(session1);
		
		var client = addClient(session2);
		removeClient(client);
		
		addClient(session3);
		
		assertTrue(session3.isOpen());
		
		clearClients();
	}
	
	private JoystickWSClient addClient(TestSession session) {
		var client = new JoystickWSClient();
		try {
			client.onOpen(session);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		clients.add(client);
		
		return client;
	}
	
	private void removeClient(JoystickWSClient client) {
		client.onClose(null);
		clients.remove(client);
	}
	
	private void clearClients() {
		for(var client : clients) {
			client.onClose(null);
		}
		
		clients.clear();
	}
	
	private class TestSession implements Session{
		
		private static final AtomicInteger ids = new AtomicInteger();
		private int port;
		private boolean mobile;
		private boolean open;
		
		public TestSession(int port, boolean mobile) {
			this.port = port;
			this.mobile = mobile;
			open = true;
		}

		@Override
		public void addMessageHandler(MessageHandler arg0) throws IllegalStateException {
			// TODO Auto-generated method stub
			
		}

		@Override
		public <T> void addMessageHandler(Class<T> arg0, Partial<T> arg1) throws IllegalStateException {
			// TODO Auto-generated method stub
			
		}

		@Override
		public <T> void addMessageHandler(Class<T> arg0, Whole<T> arg1) throws IllegalStateException {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void close() throws IOException {
			System.out.print(this.toString() + ": ");
			System.out.println("Closed...");
			open = false;
		}

		@Override
		public void close(CloseReason arg0) throws IOException {
			System.out.print(this.toString() + ": ");
			System.out.println(arg0.getReasonPhrase());
			open = false;
		}

		@Override
		public Async getAsyncRemote() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Basic getBasicRemote() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public WebSocketContainer getContainer() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getId() {
			return ids.getAndIncrement() + "";
		}

		@Override
		public int getMaxBinaryMessageBufferSize() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public long getMaxIdleTimeout() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public int getMaxTextMessageBufferSize() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public Set<MessageHandler> getMessageHandlers() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public List<Extension> getNegotiatedExtensions() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getNegotiatedSubprotocol() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Set<Session> getOpenSessions() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Map<String, String> getPathParameters() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getProtocolVersion() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getQueryString() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Map<String, List<String>> getRequestParameterMap() {
			HashMap<String, List<String>> tempMap = new HashMap<>();
			var tempList = new ArrayList<String>(1);
			tempList.add(port + "");
			
			tempMap.put("port", tempList);
			return tempMap;
		}

		@Override
		public URI getRequestURI() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Principal getUserPrincipal() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Map<String, Object> getUserProperties() {
			HashMap<String, Object> tempMap = new HashMap<>();
			tempMap.put("mobile", mobile);
			
			return tempMap;
		}

		@Override
		public boolean isOpen() {
			return open;
		}

		@Override
		public boolean isSecure() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void removeMessageHandler(MessageHandler arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void setMaxBinaryMessageBufferSize(int arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void setMaxIdleTimeout(long arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void setMaxTextMessageBufferSize(int arg0) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public String toString() {
			return port + "" + mobile;
		}
		
	}


}
