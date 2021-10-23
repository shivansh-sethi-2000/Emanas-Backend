package com.ehrc.utility;

import java.io.File;
import java.nio.charset.StandardCharsets;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;

import org.apache.commons.io.FileUtils;


public class Encryptions {
	/**
	 * @param args the command line arguments
	 * @throws NoSuchPaddingException 
	 * @throws NoSuchAlgorithmException 
	 */
	static Cipher cipher;
	Encryptions() throws NoSuchAlgorithmException, NoSuchPaddingException{
		cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
	}
	public static void main(String[] args) throws Exception {
		
		String clearText = "Test@123";
		System.out.println("ClearText: " + clearText);
		System.out.println("ClearText length: " + clearText.getBytes(StandardCharsets.UTF_8).length);
				
		new Encryptions();
		byte[] encrypted = Encryptions.encrypt(clearText);
		System.out.println("Encrypted length: " + encrypted.length);
		System.out.println("Encrypted: " + Base64.getEncoder().encodeToString(encrypted));
		System.out.println("Encrypted string: " + new String(encrypted, StandardCharsets.UTF_8));

		new Encryptions();
		String decrypted = Encryptions.decrypt(new String(encrypted, StandardCharsets.UTF_8));
		System.out.println("Decrypted: " + decrypted);
		

	}

	@SuppressWarnings("deprecation")
	public static PrivateKey loadPrivateKey() throws Exception {
		String filename = LoadConfig.getConfigValue("PRIVATE_KEY");
		String privateKeyPEM = FileUtils.readFileToString(new File(filename));

		// strip of header, footer, newlines, whitespace
		privateKeyPEM = privateKeyPEM.replace("-----BEGIN PRIVATE KEY-----", "")
				.replace("-----END PRIVATE KEY-----", "").replaceAll("\\s", "");

		// decode to get the binary DER representation
		byte[] privateKeyDER = Base64.getDecoder().decode(privateKeyPEM);

		PKCS8EncodedKeySpec keySpecPKCS8 = new PKCS8EncodedKeySpec(privateKeyDER);
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		PrivateKey privKey = keyFactory.generatePrivate(keySpecPKCS8);

		System.out.println(privKey);
		return privKey;

	}

	@SuppressWarnings("deprecation")
	public static PublicKey loadPublicKey() throws Exception {
		String filename = LoadConfig.getConfigValue("PUBLIC_KEY");
		String publicKeyPEM = FileUtils.readFileToString(new File(filename));

		// strip of header, footer, newlines, whitespace
		publicKeyPEM = publicKeyPEM.replace("-----BEGIN PUBLIC KEY-----", "").replace("-----END PUBLIC KEY-----", "")
				.replaceAll("\\s", "");

		// decode to get the binary DER representation
		byte[] publicKeyDER = Base64.getDecoder().decode(publicKeyPEM);

		X509EncodedKeySpec publicSpec = new X509EncodedKeySpec(publicKeyDER);
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		PublicKey pubKey = keyFactory.generatePublic(publicSpec);

		System.out.println(pubKey);

		return pubKey;
	}
	
	@SuppressWarnings("static-access")
	public static byte[] encrypt(String text) throws Exception {
		System.out.println("Encrypting");
		PublicKey publicKey = new Encryptions().loadPublicKey();
		cipher.init(Cipher.ENCRYPT_MODE, publicKey);
		byte[] encrypted = cipher.doFinal(text.getBytes(StandardCharsets.UTF_8));
		System.out.println("Encrypted");
		return encrypted;
	}
	
	@SuppressWarnings("static-access")
	public static String decrypt(String encrypted) throws Exception {
		System.out.println("Decrypting");
		PrivateKey privateKey = new Encryptions().loadPrivateKey();
		cipher.init(Cipher.DECRYPT_MODE, privateKey);
		byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(encrypted));
		System.out.println("Decrypted");
		return new String(decrypted, StandardCharsets.UTF_8);
	}

}
