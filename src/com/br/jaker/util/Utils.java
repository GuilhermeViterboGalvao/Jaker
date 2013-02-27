package com.br.jaker.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import android.util.Log;

public class Utils {
	
	public static File writeZipInputStream(InputStream zip, File dir) {
		OutputStream out = null;
		try {
			out = new FileOutputStream(dir);
			int read = 0;
			byte[] bytes = new byte[1024];		 
			while ((read = zip.read(bytes)) != -1) out.write(bytes, 0, read);		
		} catch (IOException ioe) {
			Log.e("Utils.writeZipInputStream", "Problem o write zip file: " + ioe.getMessage(), ioe);
		} catch (Exception e) {
			Log.e("Utils.writeZipInputStream", "Problem o write zip file: " + e.getMessage(), e);
		} finally {
			if (out != null) {
				try {
					out.flush();
					out.close();	
				} catch (Exception e) {	}
			}
		}
		return dir;
	}
	
	//TODO Documentar método
	public static void unzip(File zipFile, File dir) throws IOException {
		
		ZipFile zip = null;
		File arquivo = null;
		InputStream is = null;
		OutputStream os = null;
		byte[] buffer = new byte[1024];
		
		try {
			
			// cria diretório informado, caso não exista
			if (!dir.exists()) dir.mkdirs();
			if (!dir.exists() || !dir.isDirectory()) throw new IOException("O diretório " + dir.getName() + " não é um diretório válido");
			
			zip = new ZipFile(zipFile);
			Enumeration<? extends ZipEntry> entries = zip.entries();
			
			while (entries.hasMoreElements()) {
				ZipEntry entrada = (ZipEntry)entries.nextElement();
				arquivo = new File(dir, entrada.getName());
				
				// se for diretório inexistente, cria a estrutura e pula 
				// pra próxima entrada
				if (entrada.isDirectory() && !arquivo.exists()) {
					arquivo.mkdirs();	
					continue;
				}
				
				// se a estrutura de diretórios não existe, cria
				if (!arquivo.getParentFile().exists()) arquivo.getParentFile().mkdirs();
				
				try {
					// lê o arquivo do zip e grava em disco
					is = zip.getInputStream(entrada);
					os = new FileOutputStream(arquivo);
					int bytesLidos = 0;
					
					if (is == null) throw new ZipException("Erro ao ler a entrada do zip: " + entrada.getName());
					
					while ((bytesLidos = is.read(buffer)) > 0) {
						os.write(buffer, 0, bytesLidos);
					}
				} finally {
					if (is != null) try { is.close(); } catch (Exception ex) { }
					if (os != null) try { os.close(); } catch (Exception ex) { }
				}
			}
		} finally {
			if (zip != null) try { zip.close(); } catch (Exception e) { }
		}
	}
}
