package net.deuce.moman.envelope.ui;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.eclipse.swt.dnd.ByteArrayTransfer;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.TransferData;

public class EnvelopeTransfer extends ByteArrayTransfer {

	private static final String MYTYPENAME = "envelope-ids";
	private static final int MYTYPEID = registerType(MYTYPENAME);
	private static EnvelopeTransfer _instance = new EnvelopeTransfer();

	private EnvelopeTransfer() {
	}

	public static EnvelopeTransfer getInstance() {
		return _instance;
	}

	public void javaToNative(Object object, TransferData transferData) {
		if (object == null || !(object instanceof String[]) || !isSupportedType(transferData)) {
			DND.error(DND.ERROR_INVALID_DATA);
			return;
		}
		
		String[] myTypes = (String[]) object;	
 		try {
 			// write data to a byte array and then ask super to convert to pMedium
 			ByteArrayOutputStream out = new ByteArrayOutputStream();
 			DataOutputStream writeOut = new DataOutputStream(out);
 			for (int i = 0, length = myTypes.length; i < length;  i++){
 				byte[] buffer = myTypes[i].getBytes();
 				writeOut.writeInt(buffer.length);
 				writeOut.write(buffer);
 			}
 			byte[] buffer = out.toByteArray();
 			writeOut.close();
 
 			super.javaToNative(buffer, transferData);
 			
 		} catch (IOException e) {
 			e.printStackTrace();
 		}
	}

	public Object nativeToJava(TransferData transferData) {

		if (isSupportedType(transferData)) {

			byte[] buffer = (byte[]) super.nativeToJava(transferData);
			if (buffer == null)
				return null;

			String[] myData = new String[0];
			try {
				ByteArrayInputStream in = new ByteArrayInputStream(buffer);
				DataInputStream readIn = new DataInputStream(in);
				while (readIn.available() > 20) {
					int size = readIn.readInt();
					byte[] id = new byte[size];
					readIn.read(id);
					String datum = new String(id);
					String[] newMyData = new String[myData.length + 1];
					System.arraycopy(myData, 0, newMyData, 0, myData.length);
					newMyData[myData.length] = datum;
					myData = newMyData;
				}
				readIn.close();
			} catch (IOException ex) {
				ex.printStackTrace();
				return null;
			}
			return myData;
		}

		return null;
	}

	@Override
	protected String[] getTypeNames() {
		return new String[] { MYTYPENAME };
	}

	@Override
	protected int[] getTypeIds() {
		return new int[] { MYTYPEID };
	}

}
