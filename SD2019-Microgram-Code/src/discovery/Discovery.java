package discovery;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Logger;

public class Discovery {

	private static Logger Log = Logger.getLogger(Discovery.class.getName());

	static {
		System.setProperty("java.net.preferIPv4Stack", "true");
		System.setProperty("java.util.logging.SimpleFormatter.format", "%4$s: %5$s\n");
	}
	
	
	static final InetSocketAddress DISCOVERY_ADDR = new InetSocketAddress("226.226.226.226", 2266);
	static final int DISCOVERY_PERIOD = 1000;
	static final int DISCOVERY_TIMEOUT = 30000;

	private static final String DELIMITER = "\t";

	/**
	 * 
	 * Announces periodically a service in a separate thread .
	 * 
	 * @param serviceName the name of the service being announced.
	 * @param serviceURI the location of the service
	 */
	public static void announce(String serviceName, String serviceURI) {
		Log.info(String.format("Starting Discovery announcements on: %s for: %s -> %s", DISCOVERY_ADDR, serviceName, serviceURI));
		
		byte[] pktBytes = String.format("%s%s%s", serviceName, DELIMITER, serviceURI).getBytes();

		DatagramPacket pkt = new DatagramPacket(pktBytes, pktBytes.length, DISCOVERY_ADDR);
		new Thread(() -> {
			try (DatagramSocket ms = new DatagramSocket()) {
				for (;;) {
					ms.send(pkt);
					Thread.sleep(DISCOVERY_PERIOD);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}).start();
	}


	/**
	 * Performs discovery of instances of the service with the given name.
	 * 
	 * @param  serviceName the name of the service being discovered
	 * @param  minRepliesNeeded the required number of service replicas to find. 
	 * @return an array of URI with the service instances discovered. Returns an empty, 0-length, array if the service is not found within the alloted time.
	 * @throws IOException 
	 * @throws URISyntaxException 
	 * 
	 */
	public static URI[] findUrisOf(String serviceName, int minRepliesNeeded) throws IOException, URISyntaxException {
		int counterUris = 0;
		final int MAX_DATAGRAM_SIZE = 65536;
		String[] uriArray = new String[100];
		boolean isDiferent = true;
		URI[] finalArray = new URI[minRepliesNeeded];

		final InetAddress group = InetAddress.getByName("226.226.226.226");
		try (MulticastSocket socket = new MulticastSocket(2266)) {
			socket.joinGroup(group);

			long startTime = System.currentTimeMillis();

			while (counterUris < minRepliesNeeded) {

				byte[] buffer = new byte[MAX_DATAGRAM_SIZE];
				DatagramPacket request = new DatagramPacket(buffer, buffer.length);

				socket.setSoTimeout(5000);
				socket.receive(request);

				String line = new String(request.getData(), 0, request.getLength());
				String[] newLine = line.split(DELIMITER);
				String name = newLine[0];
				String uri = newLine[1];

				if (counterUris > 0) {
					for (String currentUri : uriArray) {
						if (uri.equals(currentUri)) {
							isDiferent = false;
						}
					}
				}

				if (serviceName.equals(name) && isDiferent == true) {
					uriArray[counterUris] = uri;
					URI myUri = new URI(uri);
					finalArray[counterUris++] = myUri;
				}
			}

		}

		// set set so timeout antes do receive 5000 !!!!
		// guadar empo ao entrar na funcao
		// start time = time
		// ver quanto tempo ja pasoou , so quremos etsra 5 seundos. entro 3

		return finalArray;
	}	
}
