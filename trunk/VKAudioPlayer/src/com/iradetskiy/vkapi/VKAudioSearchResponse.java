package com.iradetskiy.vkapi;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import com.iradetskiy.utility.TimeUtility;


public class VKAudioSearchResponse {
	private int count;
	private List<VKAudioItem> items;
	
	public VKAudioSearchResponse(String responseXml) throws XmlPullParserException, IOException{
		parseResponseXml(responseXml);
	}
	
	public void parseResponseXml(String responseXml) throws XmlPullParserException, IOException{
				
		XmlPullParser xpp = XmlPullParserFactory.newInstance().newPullParser();
		xpp.setInput(new StringReader(responseXml));
		int eventType = xpp.getEventType();
		
		VKAudioItem item = null;
		items = new ArrayList<VKAudioItem>();
		
		while(eventType != XmlPullParser.END_DOCUMENT){
			
			if(eventType == XmlPullParser.START_TAG) {
	        	
	            if (xpp.getName().equals("count")) {
	            	count = Integer.parseInt(xpp.nextText());
	            	if (count == 0) {
	            		return;
	            	}
	            }
	            
	            if (xpp.getName().equals("audio")) {
	            	item = new VKAudioItem();
	            }
	            
	            if (xpp.getName().equals("aid")) {
	            	item.aid = xpp.nextText();
	            }
	            
	            if (xpp.getName().equals("owner_id")) {
	            	item.owner_id = xpp.nextText();
	            }
	            
	            if (xpp.getName().equals("artist")) {
	            	item.artist = xpp.nextText();
	            }
	            
	            if (xpp.getName().equals("title")) {
	            	item.title = xpp.nextText();
	            }
	            
	            if (xpp.getName().equals("duration")) {
	            	item.duration = TimeUtility.formatSeconds(xpp.nextText());
	            }
	            
	            if (xpp.getName().equals("url")) {
	            	item.url = xpp.nextText();
	            }
	            
	            if (xpp.getName().equals("lyrics_id")) {
	            	item.lyrics_id = xpp.nextText();
	            }
	        	
	        } else if(eventType == XmlPullParser.END_TAG) {
	            
	        	if (xpp.getName().equals("audio")) {
	            	items.add(item);
	            }
	        	
	        }
			
			eventType = xpp.next();
		}
	}
	
	public List<VKAudioItem> getItems() {
		return items;
	}
	
	public int getCount() {
		return count;
	}
}
