import java.util.Arrays;
import java.util.Random;

public class UDPPacket {
	byte[] bytes;
	
	public UDPPacket(int destPort, int length){
		Random rand = new Random();
		bytes = new byte[8+length];
		rand.nextBytes(bytes);
		bytes[0] = 12;
		bytes[1] = 34;
		bytes[2] = (byte)(destPort >> 8);
		bytes[3] = (byte)destPort;
		bytes[4] = (byte)(bytes.length >> 8);
		bytes[5] = (byte)bytes.length;
		bytes[6] = 0;
		bytes[7] = 0;
		byte[] checksumBytes = checksumBytes(generatePseudoHeader());
		bytes[6] = checksumBytes[0];
		bytes[7] = checksumBytes[1];
	}
	
	private byte[] generatePseudoHeader(){
		int index = 0;
		byte[] checksumInfo = new byte[bytes.length+12];
		//SRC IP
		for ( int j = 0; j < IPV4Packet.SRC_IP.length; j++){
			checksumInfo[index++] = IPV4Packet.SRC_IP[j];
		}
		//Destination IP
		for ( int j = 0; j < IPV4Packet.DEST_IP.length; j++){
			checksumInfo[index++] = IPV4Packet.DEST_IP[j];
		}
		//Reserved Zeroes
		checksumInfo[index++] = 0;
		// Protocol Number
		checksumInfo[index++] = IPV4Packet.PROTOCOL_NUM;
		// UDP Length
		checksumInfo[index++] = bytes[4];
		checksumInfo[index++] = bytes[5];
		// UDP Datagram
		for ( int j=0; j < bytes.length; j++ ){
			checksumInfo[index++] = bytes[j];
		}
		//Calculate Checksum
		return checksumInfo;
	}

	public static byte[] checksumBytes(byte[] bytes){
		long sum = 0;
		for ( int i = 0; i < bytes.length; i=i+2){
			int a = byteToUnsignedInt(bytes[i]);
			int b = 0;
			if ( i+1 < bytes.length ){
				b = byteToUnsignedInt(bytes[i+1]);
			}
			sum += (a << 8) + b;
			if ( (sum & 0xFFFF0000) != 0 ){
				sum = sum & 0xFFFF;
				sum++;
			}
		}
		short checksum = (short) ~(sum & 0xFFFF);
		byte[] checksumBytes = new byte[2];
		checksumBytes[0] = (byte)(checksum >>> 8);
		checksumBytes[1] = (byte)(checksum & 0xFF);
		return checksumBytes;
	}
	
	private static int byteToUnsignedInt(byte b){
		return b & 0xFF;
	}
	
	byte[] getBytes(){
		return bytes;
	}
}
