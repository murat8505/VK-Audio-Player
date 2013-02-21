package com.iradetskiy.vkapi;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import com.iradetskiy.utility.TimeUtility;


public class VKAudioGetResponse {
	public static final String AUDIO = "audio";
	
	private List<VKAudioItem> items;
	private VKAudioGetRequest mRequest;
	
	public VKAudioGetResponse(String responseXml) throws XmlPullParserException, IOException{
		parseResponseXml(responseXml);
	}

	private void parseResponseXml(String responseXml) throws XmlPullParserException, IOException {
		
		XmlPullParser xpp = XmlPullParserFactory.newInstance().newPullParser();
		xpp.setInput(new StringReader(responseXml));
		int eventType = xpp.getEventType();
		
		VKAudioItem item = null;
		items = new ArrayList<VKAudioItem>();
		
		while(eventType != XmlPullParser.END_DOCUMENT){
			
			if(eventType == XmlPullParser.START_TAG) {
	            
	            if (xpp.getName().equals(AUDIO)) {
	            	item = new VKAudioItem();
	            }
	            
	            if (xpp.getName().equals(VKAudioItem.AID)) {
	            	item.aid = xpp.nextText();
	            }
	            
	            if (xpp.getName().equals(VKAudioItem.OID)) {
	            	item.owner_id = xpp.nextText();
	            }
	            
	            if (xpp.getName().equals(VKAudioItem.ARTIST)) {
	            	item.artist = xpp.nextText();
	            }
	            
	            if (xpp.getName().equals(VKAudioItem.TITLE)) {
	            	item.title = xpp.nextText();
	            }
	            
	            if (xpp.getName().equals(VKAudioItem.DURATION)) {
	            	item.duration = TimeUtility.formatSeconds(xpp.nextText());
	            }
	            
	            if (xpp.getName().equals(VKAudioItem.URL)) {
	            	item.url = xpp.nextText();
	            }
	            
	            if (xpp.getName().equals(VKAudioItem.LYRICS_ID)) {
	            	item.lyrics_id = xpp.nextText();
	            }
	        	
	        } else if(eventType == XmlPullParser.END_TAG) {
	            
	        	if (xpp.getName().equals(AUDIO)) {
	            	items.add(item);
	            }
	        	
	        }
			
			eventType = xpp.next();
		}		
	}
	
	public List<VKAudioItem> getItems() {
		return items;
	}
	
	void setRequest(VKAudioGetRequest request) {
		mRequest = request;
	}
	
	public VKAudioGetRequest getRequest() {
		return mRequest;
	}
}
