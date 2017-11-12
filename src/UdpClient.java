import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class UdpClient {
	public static void main(String[] args ){
		try (Socket socket = new Socket("18.221.102.182", 38005)) {
			System.out.println("Connected to server.");
			InputStream is = socket.getInputStream();
			OutputStream os = socket.getOutputStream();
			
			//Handshake
			byte[] handshakeCode = {(byte) 0xDE, (byte) 0xAD, (byte) 0xBE, (byte) 0xEF};
			IPV4Packet handshake = new IPV4Packet(handshakeCode);
			os.write(handshake.getBytes());
			System.out.print("Handshake response: ");
			for ( int j = 0; j < 4; j++){
				System.out.printf("%02X", is.read());
			}
			System.out.println();
			int udpDestPort = is.read() << 8;
			udpDestPort = udpDestPort + is.read();
			System.out.println("Port number received: " + udpDestPort + "\n");
			
			int numPackets = 12;
			int rttSum = 0;
			for ( int i=0, dataLength=2; i < numPackets; i++,dataLength*=2){
				System.out.printf("Sending packet with %d bytes of data%n", dataLength);
				UDPPacket udpPacket = new UDPPacket(udpDestPort, dataLength);
				IPV4Packet packet = new IPV4Packet(udpPacket.getBytes());
				os.write(packet.getBytes());
				long RTT = System.currentTimeMillis();
				boolean rttSet = false;
				System.out.print("Reponse: ");
				for ( int j = 0; j < 4; j++){
					System.out.printf("%02X", is.read());
					if ( !rttSet ){
						rttSet = true;
						RTT = System.currentTimeMillis()-RTT;
					}
				}
				rttSum += RTT;
				System.out.printf("%nRTT: %dms%n%n",RTT);
			}
			System.out.printf("Average RTT: %.2fms%n%n", (float)rttSum/numPackets);

		} catch ( Exception e ){
			e.printStackTrace();
		} finally {
			System.out.println("Disconnected from server.");
		}

	}

}
