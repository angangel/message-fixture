package com.googlecode.messagefixture.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class IOUtil {

	public static String readFull(File file) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(file));
		
		char[] buffer = new char[(int)file.length()];
		
		reader.read(buffer);
		
		String fileContent = new String(buffer); 
		return fileContent;
	}
}
