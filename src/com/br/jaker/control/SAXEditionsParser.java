package com.br.jaker.control;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import android.util.Log;
import android.util.Xml;
import android.util.Xml.Encoding;
import com.br.jaker.exception.EditionException;
import com.br.jaker.model.Edition;

public class SAXEditionsParser extends DefaultHandler {

	private List<Edition> editions;	
	
	public List<Edition> getEditions() {
		return editions;
	}
	
	private Edition edition;
	
	private StringBuilder sb;
	
	/**
	 * <pre>
	 *	<?xml version="1.0" encoding="utf-8"?>
	 *	<editions>
	 *		<edition>
	 *			<new>false</new>
	 *			<number>1</number>
	 *			<title>Laker - 1</title>
	 * 			<description>Laker - 1</description>
	 *			<coverImage>http://www.lakercompendium.com/magazines/laker1.jpg</coverImage>
	 *			<downloadUrl>www.lakercompendium.com/downloads/laker-starter-pack-1.4.2.zip</downloadUrl>
	 *		</edition>
	 * 		<edition>
	 *			<new>false</new>
	 *			<number>2</number>
	 *			<title>Laker - 2</title>
	 *			<description>Laker - 2</description>
	 *			<coverImage>http://www.lakercompendium.com/magazines/laker2.jpg</coverImage>
	 *			<downloadUrl>www.lakercompendium.com/downloads/laker-starter-pack-1.4.2.zip</downloadUrl>
	 *		</edition>
	 *		<edition>
	 *			<new>true</new>
	 *			<number>3</number>
	 * 			<title>Laker - 3</title>
	 *			<description>Laker - 3</description>
	 *			<coverImage>http://www.lakercompendium.com/magazines/laker3.jpg</coverImage>
	 *			<downloadUrl>www.lakercompendium.com/downloads/laker-starter-pack-1.4.2.zip</downloadUrl>
	 *		</edition>
	 * </editions>
	 * </pre>
	 * @param InputStream with the editions xml values.
	 * @return A list of objects Edition with the values read in 'xml' object.
	 * @exception If the 'xml' is null or not in the standards, the exception will occur.
	 * */
	public static List<Edition> parseEdition(InputStream xml) throws EditionException {
		if (xml == null) throw new EditionException("The object 'xml' is null.");
		
		List<Edition> editions = null;		
		SAXEditionsParser parser = new SAXEditionsParser();
		
		try {
			Xml.parse(xml, Encoding.UTF_8, parser);
			editions = parser.getEditions();
		} catch (IOException e) {
			editions = null;
		} catch (SAXException e) {
			editions = null;			
		} catch (Exception e) {
			editions = null;
		} finally {
			try { 
				if (xml != null) {
					xml.close();
				}				
			} catch (Exception e) {
				Log.e("SAXEditionsParser.parseEdition", "Error on close InputStream: " + e.getMessage(), e);
			}
		}
		
		return editions;
	}
	
	@Override
	public void startDocument() throws SAXException {
		sb = new StringBuilder();
	}
	
	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		sb.append(ch,start,length);
	}
	
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if ("editions".equals(localName)) {
			editions = new ArrayList<Edition>();
		} else if ("edition".equals(localName)) {
			edition = new Edition();
		}
	}
	
	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {		
		if ("new".equals(localName)) {
			try {
				edition.setNewEdition(Boolean.parseBoolean(sb.toString().trim()));	
			} catch (Exception e) {
				Log.e("SAXEditionsParser.endElement", "Error parsing boolean property 'new'.", e);
			}			
		} else if ("number".equals(localName)) {
			try {
				edition.setNumber(Integer.parseInt(sb.toString().trim()));
			} catch (NumberFormatException nfe) {
				Log.e("SAXEditionsParser.endElement", "Error parsing int property 'number'.", nfe);
			} catch (Exception e) {
				Log.e("SAXEditionsParser.endElement", "Error parsing int property 'number'.", e);
			}
		} else if ("title".equals(localName)) {
			edition.setTitle(sb.toString().trim());
		} else if ("description".equals(localName)) {
			edition.setDescription(sb.toString().trim());
		} else if ("coverImage".equals(localName)) {
			edition.setCoverImage(sb.toString().trim());
		} else if ("downloadUrl".equals(localName)) {
			edition.setDownloadUrl(sb.toString().trim());
		} else if ("edition".equals(localName)) {
			editions.add(edition);
		}
	}	
}