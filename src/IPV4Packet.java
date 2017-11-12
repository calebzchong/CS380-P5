import java.util.Arrays;

public class IPV4Packet {
	public static final int IP_VERSION = 4;
	public static final int IHL = 5;
	public static final int TTL = 50;
	public static final int PROTOCOL_NUM = 17; // UDP
	public static final byte[] SRC_IP = {(byte)127,(byte)0,(byte)0,(byte)1};
	public static final byte[] DEST_IP = {(byte)18,(byte)221,(byte)102,(byte)182};
	private byte[] bytes;
	
	public IPV4Packet( byte[] payload ){
		int dataSize = payload.length;
		bytes = new byte[5 * 4 + dataSize];
		//Version, IHL
		bytes[0] = (IP_VERSION << 4) | IHL;
		//Total Length
		bytes[2] = (byte) (bytes.length >>> 8);
		bytes[3] = (byte) (bytes.length & 0xFF);
		//Flags - Do not fragment
		bytes[6] = 0b01000000;
		//TTL
		bytes[8] = TTL;
		//Protocol
		bytes[9] = PROTOCOL_NUM;
		//Src IP
		bytes[12] = SRC_IP[0];
		bytes[13] = SRC_IP[1];
		bytes[14] = SRC_IP[2];
		bytes[15] = SRC_IP[3];
		//Dest IP
		bytes[16] = DEST_IP[0];
		bytes[17] = DEST_IP[1];
		bytes[18] = DEST_IP[2];
		bytes[19] = DEST_IP[3];
		//Header checksum
		byte[] checksumBytes = checksumBytes(Arrays.copyOf(bytes, 20));
		bytes[10] = checksumBytes[0];
		bytes[11] = checksumBytes[1];
		
		for ( int i=0,j=20; i < payload.length; i++,j++){
			bytes[j] = payload[i];
		}
}
	
	public byte[] getBytes(){
		return bytes;
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
	
	
	
	
}
