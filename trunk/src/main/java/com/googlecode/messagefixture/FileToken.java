package com.googlecode.messagefixture;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class FileToken {

	public byte[] binary(String filePath) throws IOException {
		File filesDir = MessageConfiguration.getInstance().getFilesDirectory();
		File file = new File(filesDir, filePath);
		byte[] b = new byte[(int) file.length()];
		
		FileInputStream fis = new FileInputStream(file);
		fis.read(b);

		return b;
	}
	
}
