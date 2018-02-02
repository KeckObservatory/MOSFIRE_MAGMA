package edu.ucla.astro.irlab.util;

import edu.hawaii.keck.kjava.JniKJavaClient;
import edu.hawaii.keck.kjava.KJavaCShowListener;
import edu.hawaii.keck.kjava.KJavaException;
import edu.hawaii.keck.kjava.KJavaJniException;
import edu.hawaii.keck.kjava.KeywordInfo;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;

import org.apache.log4j.Logger;

public class KJavaPropertyManager implements KJavaCShowListener, ExternalPropertySetter {

	private static final Logger logger = Logger.getLogger(KJavaPropertyManager.class);
	
	String serverName;
	PropertyList propertyList;
	String[] cshowKeywords;
  private JniKJavaClient kjavaClient;
  boolean polling=false;
	DateFormat lastAliveFormat = new SimpleDateFormat("yy/MM/dd,HH:mm:ss");
	Calendar calendar;
	MonitorServerThread monitorServerThread;
	public KJavaPropertyManager(PropertyList list, String server) throws KJavaException {
  	propertyList = list;
  	serverName=server;
  	kjavaClient = new JniKJavaClient(server);
    kjavaClient.addKJavaCShowListener(this);
		calendar = Calendar.getInstance();
	}
  public void getCShowKeywordsFromServer() throws KJavaJniException {
  	boolean keywordUsed;
  	
  	//. get only keywords that are both available by the server and in the propertyList
  	ArrayList<String> keywords = new ArrayList<String>();
  	KeywordInfo[] serverKeywords = kjavaClient.getAllReadableKeywords(); 
  	Enumeration<String> e = propertyList.keys();
  	while (e.hasMoreElements()) {
  		Property prop = propertyList.get(e.nextElement());
  		keywordUsed = false;
  		for (KeywordInfo serverKey : serverKeywords) {
  			if  (serverKey.getKeywordName().compareToIgnoreCase(prop.getKeywordName()) == 0) {
  				logger.info("Adding keyword: " + prop.getKeywordName());
  				//. todo: should be serverKey.getKeywordName()?
  				keywords.add(prop.getKeywordName());
  				keywordUsed = true;
  				break;
  			}
  		} 
  		if (!keywordUsed) {
				logger.info("Ignoring keyword: " + prop.getKeywordName());
			}
  	}
  	cshowKeywords = keywords.toArray(new String[keywords.size()]);
  }
  public String[] getCShowKeywords() throws KJavaJniException {
  	if (cshowKeywords == null) {
  		getCShowKeywordsFromServer();
  	}
  	return cshowKeywords;
  }
  public void start() throws KJavaException {
    kjavaClient.setCShowKeywords(getCShowKeywords());
    kjavaClient.start();
  }
  public void stop() throws KJavaException {
    kjavaClient.stop();
  }

  //. implemented method from KJavaCShowListener interface
  public void kJavaCShowCallback(String serviceName, String keywordName, String value) {
    if (serviceName.equalsIgnoreCase(serverName)) {
    	logger.trace("kJavaCShowCallback: <"+keywordName+"> = <"+value+">");
    	
			try {
				propertyList.setPropertyValueFromKeywordName(keywordName, value);
			} catch (InvalidValueException ivEx) {
				ivEx.printStackTrace();
			}
    }
  }
	public void setExternalProperty(Property prop, String value) throws SetExternalPropertyException {
		try {
			//. for KTL keywords, convert boolean values to 1 for true and 0 for false
			if (prop.getDatatype().compareTo(Property.DATATYPE_BOOLEAN) == 0) {
				if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("t")) {
					value = "1";
				} else if (value.equalsIgnoreCase("false") || value.equalsIgnoreCase("f")) {				
					value = "0";
				}
			}
			logger.info("Doing a modify on "+serverName+"."+prop.getKeywordName()+"="+value);
			kjavaClient.modify(prop.getKeywordName(), value);
		} catch (KJavaJniException e) {
			e.printStackTrace();
			throw new SetExternalPropertyException(e.getMessage());
		}
	}	
	public void startServerMonitor(Property mainServerProp, ArrayList<Property> aliveProps, int pauseBetweenEachServerMs) {
		if (polling) {
			polling = false;
		}
		monitorServerThread = new MonitorServerThread(mainServerProp, aliveProps, pauseBetweenEachServerMs);
		monitorServerThread.start();
	}
	public void doServerMonitor(Property mainServerProp, ArrayList<Property> aliveProps, int pauseBetweenEachServerMs) throws InterruptedException {
		while (polling) {
			for (Property prop : aliveProps) {
				try {
					String lastalive = kjavaClient.show(mainServerProp.getKeywordName());
					//. lastalive should pretty much be the current time
					Date lastaliveDate = lastAliveFormat.parse(lastalive);
					//. if these succeed, the server is alive
					if (mainServerProp instanceof StringProperty) {
						//. should be
						((StringProperty)mainServerProp).setValue(lastalive);
					}
					Thread.currentThread().sleep(pauseBetweenEachServerMs);		
					
					lastalive = kjavaClient.show(prop.getKeywordName());
					//. lastalive should pretty much be the current time
					lastaliveDate = lastAliveFormat.parse(lastalive);
					//. if these succeed, the server is alive
					if (prop instanceof StringProperty) {
						//. should be
						((StringProperty)prop).setValue(lastalive);
					}
				} catch (KJavaJniException ex) {
					if (prop instanceof StringProperty) {
						//. should be
						((StringProperty)prop).setValue("SERVER ERROR");
					}
					ex.printStackTrace();
				} catch (ParseException ex) {
					if (prop instanceof StringProperty) {
						//. should be
						((StringProperty)prop).setValue("KEYWORD ERROR");
					}
				}
				Thread.currentThread().sleep(pauseBetweenEachServerMs);
			}
		}
	}
	public void stopServerMonitor() {
		polling=false;
	}
	private class MonitorServerThread extends Thread {
		ArrayList<Property> props;
		Property mainProp;
		int pause;
		public MonitorServerThread(Property mainServerProp, ArrayList<Property> aliveProps, int pauseBetweenEachServerMs) {
			mainProp = mainServerProp;
			props = aliveProps;
			pause = pauseBetweenEachServerMs;
		}
		public void run() {
			try {
				polling = true;
				doServerMonitor(mainProp, props, pause);
			} catch (InterruptedException ex) {
				polling=false;
				ex.printStackTrace();
			}
		}
		
	}
}
