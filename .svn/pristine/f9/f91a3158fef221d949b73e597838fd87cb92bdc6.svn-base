package com.nike.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.cert.Certificate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLPeerUnverifiedException;

import com.nike.listeners.PushProfilesValidityCheckListener;

public class HttpsClient {

	public String getContent(String https_url_string) {

//		String https_url = "https://vsptest.nike.com/api/v1/apps/inventory/app?appname=PasswordReset&limit=50";
		https_url_string = https_url_string.replace(" ", "%20");
		URL url;
		try {

			url = new URL(https_url_string);
			HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
			String userPassword= PushProfilesValidityCheckListener.MOBILE_IRON_PWD;
		    String encoding = new String(org.apache.commons.codec.binary.Base64.encodeBase64(org.apache.commons.codec.binary.StringUtils.getBytesUtf8(userPassword)));
		    con.setRequestMethod("GET");
		    con.setRequestProperty("Content-Type", "application/json");
		    con.setRequestProperty("charset", "UTF-8");
		    con.setRequestProperty("Authorization","Basic "+encoding);
			// dumpl all cert info
//			print_https_cert(con);

			// dump all the content
			return getContentString(con);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private void print_https_cert(HttpsURLConnection con) {

		if (con != null) {

			try {

				System.out.println("Response Code : " + con.getResponseCode());
				System.out.println("Cipher Suite : " + con.getCipherSuite());
				System.out.println("\n");

				Certificate[] certs = con.getServerCertificates();
				for (Certificate cert : certs) {
					System.out.println("Cert Type : " + cert.getType());
					System.out.println("Cert Hash Code : " + cert.hashCode());
					System.out.println("Cert Public Key Algorithm : "
							+ cert.getPublicKey().getAlgorithm());
					System.out.println("Cert Public Key Format : "
							+ cert.getPublicKey().getFormat());
					System.out.println("\n");
				}

			} catch (SSLPeerUnverifiedException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}

	private String getContentString(HttpsURLConnection con) {
		String inputString = "", input;
		if (con != null) {
			try {
				boolean isError = con.getResponseCode() >= 400;
			      //The normal input stream doesn't work in error-cases.
			      InputStream is = isError ? con.getErrorStream() : con.getInputStream();
				BufferedReader br = new BufferedReader(new InputStreamReader(
						is));
				while ((input = br.readLine()) != null) {
//					System.out.println(input);
					inputString += input;
				}
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return inputString;
	}

}