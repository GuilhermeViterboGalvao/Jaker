package br.com.jaker.control;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.util.Log;
import br.com.jaker.exception.AuthorsException;
import br.com.jaker.model.Author;


/**
 * @author guilherme
 * @email catanduva.gvg@gmail.com
 * */
public class JSONAuthorsParse {

	/**
	 * <pre>
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
	 *		]
	 * </pre>
	 * @param InputStream 'jsonBook'.
	 * @return A list of objects Authors with the values read in 'jsonBook'.
	 * @exception If the 'jsonBook' is null or not in the standards, the exception will occur.
	 * */
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