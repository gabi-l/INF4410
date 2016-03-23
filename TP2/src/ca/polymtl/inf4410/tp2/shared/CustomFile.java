package ca.polymtl.inf4410.tp2.shared;

import java.util.zip.CRC32;
import java.util.zip.Checksum;
import java.io.Serializable;
import java.nio.ByteBuffer;

public class CustomFile implements Serializable {
	private static final long serialVersionUID = -4888330855773754904L;
	public String fileName = null;
	public long fileOwner = -1;
	public byte[] data = null;
	
	public CustomFile() { 
		super();	
	}
	
	public CustomFile(String fileName) {
		super();
		this.fileName = fileName;
	}
	
	public long getChecksum() {
		if(data != null) {
			Checksum checksum = new CRC32();
	        checksum.update(data, 0, data.length);
	        return checksum.getValue();
		}
		return 0;
	}
	
	public long getDataInLong()
	{
		ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
		buffer.put(data);
		buffer.flip();
		return buffer.getLong();
	}
	
	public void setData(long dataToSet) {
		ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
		buffer.putLong(dataToSet);
		data = buffer.array();
	}
}