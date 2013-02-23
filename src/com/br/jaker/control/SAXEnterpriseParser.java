package com.br.jaker.control;

import java.io.IOException;
import java.io.InputStream;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import android.util.Log;
import android.util.Xml;
import android.util.Xml.Encoding;
import com.br.jaker.exception.EnterpriseException;
import com.br.jaker.model.Address;
import com.br.jaker.model.Enterprise;
import com.br.jaker.model.Manager;

public class SAXEnterpriseParser extends DefaultHandler {

	private Enterprise enterprise;
	
	public Enterprise getEnterprise() {
		return enterprise;
	}
	
	private StringBuilder sb;	

	public static Enterprise parseEnterprise(InputStream xml) throws EnterpriseException {		
		if (xml == null) throw new EnterpriseException("The object 'xml' is null.");
		
		Enterprise enterprise = null;
		SAXEnterpriseParser parser = new SAXEnterpriseParser();
		
		try {
			Xml.parse(xml, Encoding.UTF_8, parser);
			enterprise = parser.getEnterprise();
		} catch (IOException e) {
			enterprise = null;
		} catch (SAXException e) {
			enterprise = null;
		} catch (Exception e) {
			enterprise = null;
		} finally {
			try { 
				if (xml != null) {
					xml.close();
				}				
			} catch (Exception e) {
				Log.e("SAXEnterpriseParser.parseEnterprise", "Error on close InputStream: " + e.getMessage(), e);
			}
		}
		
		return enterprise;
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
		if ("address".equals(localName)) {
			Address address = new Address();
			
			for (int i = 0; i < attributes.getLength(); i++) {
				if ("country".equals(attributes.getLocalName(i))) {
					address.setCountry(attributes.getValue(i));
				} else if ("state".equals(attributes.getLocalName(i))) {
					address.setState(attributes.getValue(i));
				} else if ("city".equals(attributes.getLocalName(i))) {
					address.setCity(attributes.getValue(i));
				} else if ("neighborhood".equals(attributes.getLocalName(i))) {
					address.setNeighborhood(attributes.getValue(i));
				} else if ("street".equals(attributes.getLocalName(i))) {
					address.setStreet(attributes.getValue(i));
				} else if ("code".equals(attributes.getLocalName(i))) {
					address.setCode(attributes.getValue(i));
				} else if ("others".equals(attributes.getLocalName(i))) {
					address.setOthers(attributes.getValue(i));
				}					 
			}
			
			enterprise.setAddress(address);
		} else if ("manager".equals(localName)) {
			Manager manager = new Manager();
			
			for (int i = 0; i < attributes.getLength(); i++) {
				if ("name".equals(attributes.getLocalName(i))) {
					manager.setName(attributes.getValue(i));
				} else if ("email".equals(attributes.getLocalName(i))) {
					manager.setEmail(attributes.getValue(i));
				}
			}
			
			enterprise.setManager(manager);
		}
	}
	
	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if ("name".equals(localName)) {
			enterprise = new Enterprise();
			enterprise.setName(sb.toString().trim());
		} else if ("site".equals(localName)) {
			enterprise.setName(sb.toString().trim());
		} else if ("urlJsonEdtions".equals(localName)) {
			enterprise.setName(sb.toString().trim());
		}
		sb.setLength(0);
	}	
}