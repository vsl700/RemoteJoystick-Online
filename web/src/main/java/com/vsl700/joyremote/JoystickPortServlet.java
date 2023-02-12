package com.vsl700.joyremote;

import java.io.IOException;
import java.util.Random;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Returns a port number that isn't already occupied, but doesn't reserve the port
 */
public class JoystickPortServlet extends HttpServlet {
	private static final long serialVersionUID = 45452434564564L;
	private static final Random random = new Random();
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public JoystickPortServlet() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected synchronized void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		int port;
		do {
			port = random.nextInt(1000, 10000);
		}while(JoystickWSClient.couples.containsKey(port));
		
		response.getWriter().append(port + "");
	}

}
