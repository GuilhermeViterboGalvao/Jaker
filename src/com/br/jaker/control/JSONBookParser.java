package com.br.jaker.control;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.util.Log;

import com.br.jaker.exception.BookExpection;
import com.br.jaker.model.Author;
import com.br.jaker.model.Book;
import com.br.jaker.model.JakerOptions;

public class JSONBookParser {

	public static Book parseBook(InputStream in) throws BookExpection {		
		if (in == null) throw new BookExpection("InputStream is null."); 
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		StringBuilder sb = new StringBuilder();
		String line = "";
		
		try {			
			while (line != null) {
				sb.append(line);
				line = reader.readLine();
			}
		} catch (IOException ioe) {
			Log.e("JSONBookParser.parseBook", "Erro ao ler JSON: " + ioe.getMessage(), ioe);
		} catch (Exception e) {
			Log.e("JSONBookParser.parseBook", "Erro ao ler JSON: " + e.getMessage(), e);
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
				book.setAuthors(getAuthors(jsonBook));				
				book.setJakerOptions(getJakerOptions(jsonBook));				
				book.setDate(new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(jsonBook.getString("date")));				
				book.setContents(getContents(jsonBook));	
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
	
	private static JakerOptions getJakerOptions(JSONObject jsonBook) {
		JakerOptions jakerOptions = null;
		if (!jsonBook.isNull("jakerOptions")) {
			jakerOptions = new JakerOptions();			
			try {
				JSONObject jsonJakerOptions = jsonBook.getJSONObject("jakerOptions");	 
				if (!jsonJakerOptions.isNull("background")) jakerOptions.setBackground(jsonJakerOptions.getString("background"));
				if (!jsonJakerOptions.isNull("backgroundImageLandscape")) jakerOptions.setBackgroundImageLandscape(jsonJakerOptions.getString("backgroundImageLandscape"));
				if (!jsonJakerOptions.isNull("backgroundImagePortrait")) jakerOptions.setBackgroundImagePortrait(jsonJakerOptions.getString("backgroundImagePortrait"));
				if (!jsonJakerOptions.isNull("indexHeight")) jakerOptions.setIndexHeight(jsonJakerOptions.getInt("indexHeight"));
				if (!jsonJakerOptions.isNull("pageNumbersColor")) jakerOptions.setPageNumbersColor(jsonJakerOptions.getString("pageNumbersColor"));
				if (!jsonJakerOptions.isNull("verticalBounce")) jakerOptions.setVerticalBounce(jsonJakerOptions.getBoolean("verticalBounce"));				
			} catch (JSONException jsone) {
				Log.e("JSONBookParser.getJakerOptions", "Error on read JSON: " + jsone.getMessage(), jsone);
			} catch (Exception e) {
				Log.e("JSONBookParser.getJakerOptions", "Error on read JSON: " + e.getMessage(), e);
			}
		}
		return jakerOptions;
	}
	
	private static List<Author> getAuthors(JSONObject jsonBook) {
		List<Author> authors = null;	
		try {
			JSONArray jsonAuthors = jsonBook.getJSONArray("authors");
			authors = new ArrayList<Author>(jsonAuthors.length());
			JSONObject jsonAuthor = null;
			Author author = null;			
			for (int i = 0; i < jsonAuthors.length(); i++) {				
				if (!jsonAuthors.isNull(i)) {
					jsonAuthor = jsonAuthors.getJSONObject(i);
					author = new Author();
					if (!jsonAuthor.isNull("name")) author.setName(jsonAuthor.getString("name"));
					if (!jsonAuthor.isNull("link")) author.setLink(jsonAuthor.getString("link"));
					if (!jsonAuthor.isNull("email")) author.setEmail(jsonAuthor.getString("email"));
					authors.add(author);					
				}
			}	
		} catch (JSONException jsone) {
			Log.e("JSONBookParser.getAuthors", "Error on read JSON: " + jsone.getMessage(), jsone);
		} catch (Exception e) {
			Log.e("JSONBookParser.getAuthors", "Error on read JSON: " + e.getMessage(), e);
		}	
		return authors;
	}
	
	private static List<String> getContents(JSONObject jsonBook) {
		List<String> contents = null;		
		try {
			JSONArray jsonContents = jsonBook.getJSONArray("contents");
			contents = new ArrayList<String>(jsonContents.length());
			for (int i = 0; i < jsonContents.length(); i++) {
				if (!jsonContents.isNull(i)) contents.add(jsonContents.getString(i));
			}
		} catch (JSONException jsone) {
			Log.e("JSONBookParser.getContents", "Error on read JSON: " + jsone.getMessage(), jsone);
		} catch (Exception e) {
			Log.e("JSONBookParser.getContents", "Error on read JSON: " + e.getMessage(), e);
		}		
		return contents;
	}
}