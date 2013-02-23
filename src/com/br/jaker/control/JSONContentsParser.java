package com.br.jaker.control;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.br.jaker.exception.ContentsException;

import android.util.Log;

public class JSONContentsParser {
	
	public static List<String> parseContents(JSONObject jsonBook) throws ContentsException {
		if (jsonBook == null) throw new ContentsException("The object 'jsonBook' is null.");		
		
		List<String> contents = null;
		
		try {
			JSONArray jsonContents = jsonBook.getJSONArray("contents");
			contents = new ArrayList<String>(jsonContents.length());
			for (int i = 0; i < jsonContents.length(); i++) {
				if (!jsonContents.isNull(i)) contents.add(jsonContents.getString(i));
			}
		} catch (JSONException jsone) {
			Log.e("JSONContentsParser.parseContents", "Error on read JSON: " + jsone.getMessage(), jsone);
		} catch (Exception e) {
			Log.e("JSONContentsParser.parseContents", "Error on read JSON: " + e.getMessage(), e);
		}
		
		return contents;
	}	
}