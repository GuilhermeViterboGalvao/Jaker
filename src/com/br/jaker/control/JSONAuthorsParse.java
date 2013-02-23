package com.br.jaker.control;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.util.Log;
import com.br.jaker.exception.AuthorsException;
import com.br.jaker.model.Author;

public class JSONAuthorsParse {

	public static List<Author> parseAuthors(JSONObject jsonBook) throws AuthorsException {		
		if (jsonBook == null) throw new AuthorsException("The object 'jsonBook' is null.");
		
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
			Log.e("JSONAuthorsParse.parseAuthors", "Error on read JSON: " + jsone.getMessage(), jsone);
		} catch (Exception e) {
			Log.e("JSONAuthorsParse.parseAuthors", "Error on read JSON: " + e.getMessage(), e);
		} finally {
			if (authors != null && authors.size() == 0) authors = null;
		}
		
		return authors;
	}	
}