package com.schwab;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.Scanner;

public class PDFGenerator {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		String fId, fVersion, BCSVersion, fullFormID, payload;
		File location = new File("D:\\CS\\Forms\\APP");
		if (location.isDirectory() && location != null) {
			for (File f : location.listFiles()) {
				if (f.isFile() && f.getName().endsWith(".xdp")) {
					fullFormID = f.getName();
					fId = fullFormID.split("-")[0];
					fVersion = fullFormID.split("-")[1];
					BCSVersion = fullFormID.split("-")[2];
					payload = "{\"metadata\":{\"fillable\":true},\"envelope\":{\"actions\":{\"action\":{\"id\":\"1234\",\"formid\":\""
							+ fId + "\",\"bcsversion\":\"" + BCSVersion + "\",\"formversion\":\"" + fVersion
							+ "\",\"form_data\":{}}}}}";

					URL website = new URL("http://endpoint?json_input=" + payload);
					ReadableByteChannel rbc = Channels.newChannel(website.openStream());
					FileOutputStream fos = new FileOutputStream(fullFormID + ".pdf");
					fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
				}
			}
		}

	}

}
