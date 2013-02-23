package com.br.jaker.control;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Locale;
import org.json.JSONException;
import org.json.JSONObject;
import android.util.Log;
import com.br.jaker.exception.BookExpection;
import com.br.jaker.model.Book;

public class JSONBookParser {

	/**
	 * <pre>
	 * <h1>Exemple of jsonBook</h1>
	 * {
	 * 		"title": "Jaker Example", //Required
	 *	    "authors": [
	 *	    	{
	 *	    		"name" : "Guilherme Viterbo Galvao",
	 *	    		"link" : "http://facebook.com/GuilhermeViterboGalvao",
	 *	    		"email": "catanduva.gvg@gmail.com"
	 *			},
	 *	    	{
	 *	    		"name" : "Rafael Caetano Sobral da Silva",
	 *	    		"link" : "http://facebook.com/RafaelCaetanoSobralDaSilva",
 	 *	    		"email": "rafael@waves.com"
 	 *			}
	 *		], //Required
	 *	    "date": "2011-12-27", //Required
	 *	    "jakerOptions": {
	 *			"background": "#fff",
	 *			"vertical-bounce": true,
	 *			"indexHeight": 200,
	 *			"backgroundImagePortrait": "images/background-portrait.png",
	 *			"backgroundImageLandscape": "images/background-landscape.png",
	 *			"pageNumbersColor": "#333"
	 *		},
	 *	    "contents": [
	 *	        "Cover.html",
	 *	        "Introduction.html",
	 *	        "Interactivity.html",
	 *	        "Structure_and_Columns.html",
	 *	        "Maps.html"
	 *     ] //Required
	 * }
	 * </pre>
	 * @param InputStream 'in'.
	 * @return A object Book with the values read in 'in'.
	 * @exception If the 'jsonBook' is null or not in the standards, the exception will occur.
	 * */
	public static Book parseBook(InputStream in) throws BookExpection {		
		if (in == null) throw new BookExpection("The object 'in' is null."); 
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		StringBuilder sb = new StringBuilder();
		String line = "";
		
		try {			
			while (line != null) {
				sb.append(line);
				line = reader.readLine();
			}
		} catch (IOException ioe) {
			Log.e("JSONBookParser.parseBook", "Error on read JSON: " + ioe.getMessage(), ioe);
		} catch (Exception e) {
			Log.e("JSONBookParser.parseBook", "Error on read JSON: " + e.getMessage(), e);
		} finally {
			try {
				reader.close();
			} catch (IOException ioe) {
				Log.e("JSONBookParser.parseBook", "Error on close BufferedReader: " + ioe.getMessage(), ioe);
			} catch (Exception e) {
				Log.e("JSONBookParser.parseBook", "Erro on close BufferedReader: " + e.getMessage(), e);
			}
		}
		
		Book book = null;
		
		try {
			JSONObject jsonBook = new JSONObject(sb.toString());			
			if (validateJSONBook(jsonBook)) {
				book = new Book();				
				book.setTitle(jsonBook.getString("title"));				
				book.setAuthors(JSONAuthorsParse.parseAuthors(jsonBook));				
				book.setJakerOptions(JSONJakerOptionsParser.parseJakerOptions(jsonBook));				
				book.setDate(new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(jsonBook.getString("date")));				
				book.setContents(JSONContentsParser.parseContents(jsonBook));	
			}
		} catch (JSONException jsone) {
			Log.e("JSONBookParser.parseBook", "Error on read JSON: " + jsone.getMessage(), jsone);
		} catch (Exception e) {
			Log.e("JSONBookParser.parseBook", "Error on read JSON: " + e.getMessage(), e);
		} finally {
			try {
				if (in != null) {
					in.close();
					in = null;
				}
			} catch (IOException ioe) {
				Log.e("JSONBookParser.parseBook", "Error on close InputStream: " + ioe.getMessage(), ioe);
			} catch (Exception e) {
				Log.e("JSONBookParser.parseBook", "Error on close InputStream: " + e.getMessage(), e);
			}
		}		
		
		return book;
	}
	
	private static boolean validateJSONBook(JSONObject jsonBook) throws BookExpection {		
		if (jsonBook.isNull("title"))    throw new BookExpection("Json formatted bad. Your json not have the required property 'title'.");		
		if (jsonBook.isNull("authors"))  throw new BookExpection("Json formatted bad. Your json not have the required property 'authors'.");		
		if (jsonBook.isNull("date"))     throw new BookExpection("Json formatted bad. Your json not have the required property 'date'.");		
		if (jsonBook.isNull("contents")) throw new BookExpection("Json formatted bad. Your json not have the required property 'contents'.");		
		return true;
	}
}