package com.iradetskiy.vkapi;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;


public class VKUsersGetResponse {
	
	public final static String FIRST_NAME = "first_name";
	public final static String LAST_NAME = "last_name";
	public final static String UID = "uid";
	
	private List<VKUserItem> items;
	
	public VKUsersGetResponse(String response) throws XmlPullParserException, IOException {
		parseResponse(response);
	}
	
	public void parseResponse(String response) throws XmlPullParserException, IOException {
		XmlPullParser xpp = XmlPullParserFactory.newInstance().newPullParser();
		xpp.setInput(new StringReader(response));
		int eventType = xpp.getEventType();
		
		VKUserItem item = null;
		items = new ArrayList<VKUserItem>();
		
		while(eventType != XmlPullParser.END_DOCUMENT){
			
			if(eventType == XmlPullParser.START_TAG) {
	            
				if (xpp.getName().equals("user")) {
	            	item = new VKUserItem();
	            }
				
	            if (xpp.getName().equals(FIRST_NAME)) {
	            	item.first_name = xpp.nextText();
	            }
	            
	            if (xpp.getName().equals(LAST_NAME)) {
	            	item.last_name = xpp.nextText();
	            }
	            
	            if (xpp.getName().equals(UID)) {
	            	item.uid = xpp.nextText();
	            }
	        	
	        } else if(eventType == XmlPullParser.END_TAG) {
	            
	        	if (xpp.getName().equals("user")) {
	            	items.add(item);
	            }
	        	
	        }
			
			eventType = xpp.next();
		}
	}
	
	public List<VKUserItem> getResults() {
		return items;
	}
}
