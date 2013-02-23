package com.br.jaker.control;

import java.io.IOException;
import java.io.InputStream;
import android.content.res.AssetManager;
import com.br.jaker.exception.BookExpection;
import com.br.jaker.exception.EnterpriseException;
import com.br.jaker.model.Book;
import com.br.jaker.model.Enterprise;

public class AppManager {
	
	private Enterprise enterprise;
	
	private Book book;
	
	public AppManager(AssetManager assetManager) throws EnterpriseException, IOException {
		enterprise = SAXEnterpriseParser.parseEnterprise(assetManager.open("Enterprise.xml"));
	}
	
	public Book getBook(InputStream jsonBook) throws BookExpection {
		return JSONBookParser.parseBook(jsonBook);
	}
	
	//Criar o JSON parser de edtions.
}