package com.br.jaker.control;

import org.json.JSONException;
import org.json.JSONObject;
import android.util.Log;
import com.br.jaker.exception.JakerOptionsException;
import com.br.jaker.model.JakerOptions;

public class JSONJakerOptionsParser {

	/**
	 * <pre>
	 *	    "jakerOptions": {
	 *			"background": "#fff",
	 *			"vertical-bounce": true,
	 *			"indexHeight": 200,
	 *			"backgroundImagePortrait": "images/background-portrait.png",
	 *			"backgroundImageLandscape": "images/background-landscape.png",
	 *			"pageNumbersColor": "#333"
	 *		}
	 * </pre>
	 * @param InputStream 'jsonBook'.
	 * @return A object JakerOptions with the values read in 'jsonBook'.
	 * @exception If the 'jsonBook' is null or not in the standards, the exception will occur.
	 * */
	public static JakerOptions parseJakerOptions(JSONObject jsonBook) throws JakerOptionsException {
		if (jsonBook == null) throw new JakerOptionsException("The object 'jsonBook' is null.");
		
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
				Log.e("JSONJakerOptionsParser.parseJakerOptions", "Error on read JSON: " + jsone.getMessage(), jsone);
			} catch (Exception e) {
				Log.e("JSONJakerOptionsParser.parseJakerOptions", "Error on read JSON: " + e.getMessage(), e);
			}
		}
		
		return jakerOptions;
	}	
}