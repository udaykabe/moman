package net.deuce.moman.allocation.ui;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.eclipse.swt.dnd.ByteArrayTransfer;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.TransferData;

public class AllocationTransfer extends ByteArrayTransfer {

	private static final String MYTYPENAME = "allocation-ids";
	private static final int MYTYPEID = registerType(MYTYPENAME);
	private static AllocationTransfer _instance = new AllocationTransfer();

	private AllocationTransfer() {
	}

	public static AllocationTransfer getInstance() {
		return _instance;
	}

	public void javaToNative(Object object, TransferData transferData) {
		if (object == null || !(object instanceof int[]) || !isSupportedType(transferData)) {
			DND.error(DND.ERROR_INVALID_DATA);
			return;
		}
		
		int[] myTypes = (int[]) object;	
 		try {
 			// write data to a byte array and then ask super to convert to pMedium
 			ByteArrayOutputStream out = new ByteArrayOutputStream();
 			DataOutputStream writeOut = new DataOutputStream(out);
		    writeOut.writeInt(myTypes.length);
 			for (int i = 0, length = myTypes.length; i < length;  i++){
 				writeOut.writeInt(myTypes[i]);
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

			int[] myData = new int[0];
			try {
				ByteArrayInputStream in = new ByteArrayInputStream(buffer);
				DataInputStream readIn = new DataInputStream(in);
				int size = readIn.readInt();
				myData = new int[size];
				for (int i=0; i<size; i++) {
					myData[i] = readIn.readInt();
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
