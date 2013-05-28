package br.com.jaker.control;

import java.io.IOException;
import java.io.InputStream;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import android.util.Log;
import android.util.Xml;
import android.util.Xml.Encoding;
import br.com.jaker.exception.EnterpriseException;
import br.com.jaker.model.Address;
import br.com.jaker.model.Enterprise;
import br.com.jaker.model.Manager;


/**
 * @author guilherme
 * @email catanduva.gvg@gmail.com
 * */
public class SAXEnterpriseParser extends DefaultHandler {

	private Enterprise enterprise;
	
	public Enterprise getEnterprise() {
		return enterprise;
	}
	
	private StringBuilder sb;	

	/**
	 * <code>
	 * <pre>
	 * <?xml version="1.0" encoding="utf-8"?>
	 * <enterprise>
	 *		<name>Laker Framework</name>
	 *		<site>http://www.lakercompendium.com/</site>
	 *		<urlJsonEdtions>${site}/${magazine}/${edtions.do}</urlJsonEdtions>
	 *		<address country="" state="" city="" neighborhood="" street="" code="" others=""/>
	 *		<manager name="" email=""/>
	 * </enterprise> 
	 * </pre>
	 * </code>
	 * @param InputStream with the enterprise xml values.
	 * @return A object Enterprise with the values read in 'xml' object.
	 * @exception If the 'xml' is null or not in the standards, the exception will occur.
	 * */
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
			enterprise.setSite(sb.toString().trim());
		} else if ("urlEdtions".equals(localName)) {
			enterprise.setUrlJsonEdtions(sb.toString().trim());
		}
		sb.setLength(0);
	}	
}