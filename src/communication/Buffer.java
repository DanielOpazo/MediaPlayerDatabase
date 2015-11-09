package communication;

public class Buffer {
	private byte[] buffer;
	private int bufferSize;
	
	public Buffer(int bufferSize) {
		this.bufferSize = bufferSize;
		buffer = new byte[bufferSize];
	}
	
	public void clearBuffer() {
		buffer = new byte[bufferSize];
	}
	
	public void add(int index, byte item) {
		if (index < bufferSize) {
			buffer[index] = item;
		}
	}
	
	public byte[] getBuffer() {
		return buffer;
	}
	
	public int setBuffer(byte[] inBuf) {
		if (inBuf.length <= buffer.length) {
			for (int i = 0; i < inBuf.length; i++) {
				buffer[i] = inBuf[i];
			}
			return 1;
		}
		return -1;
	}
}
