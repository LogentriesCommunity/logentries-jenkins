package com.logentries.jenkins;

import hudson.scm.SubversionSCM.DescriptorImpl.SslClientCertificateCredential;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLSocketFactory;

/**
 * Class to write logs to logentries asynchronously
 * 
 */
public class LogentriesWriter {

	/** Logentries API server address. */
	private static final String LE_API = "api.logentries.com";
	// FIXME Use ssl port
	/** Port number for Token logging on Logentries API server. */
	private static final int LE_PORT = 20000;
	/** UTF-8 output character set. */
	private static final Charset UTF8 = Charset.forName("UTF-8");
	private static final int SHUTDOWN_TIMEOUT_SECONDS = 10;

	private final ExecutorService executor;
	private final String token;
	private Socket socket;
	private OutputStream outputStream;

	/**
	 * Constructor.
	 *  // FIXME Nicer exceptions
	 * @param token Token for logentries log.
	 * @throws UnknownHostException If there was a problem connecting to LE_API
	 * @throws IOException If there was a problem getting the output stream.
	 */
	public LogentriesWriter(String token) throws UnknownHostException,
			IOException {
		this.executor = Executors.newSingleThreadExecutor();
		this.token = token;
		socket = SSLSocketFactory.getDefault().createSocket(LE_API, LE_PORT);
		outputStream = socket.getOutputStream();
	}

	/**
	 * Writes the given string to logentries.com asynchronously. It would be
	 * possible to take an array of bytes as a parameter but we want to make
	 * sure it is UTF8 encoded.
	 * 
	 * @param line The line to write.
	 */
	public void writeLogentry(final String line) {
		executor.execute(new Runnable() {
			@Override
			public void run() {
				try {
					outputStream.write((token + line + '\n').getBytes( UTF8));
					outputStream.flush();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	public void close() {
		try {
			if (!executor.awaitTermination(SHUTDOWN_TIMEOUT_SECONDS,
					TimeUnit.SECONDS)) {
				System.err
						.println("LogentriesWriter shutdown before finished execution");
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			closeStream();
			closeSocket();
		}
	}

	private void closeStream() {
		try {
			outputStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void closeSocket() {
		try {
			socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
