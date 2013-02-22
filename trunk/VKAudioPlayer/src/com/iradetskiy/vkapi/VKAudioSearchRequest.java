package com.iradetskiy.vkapi;

public class VKAudioSearchRequest {
	public final static int DEFAULT_COUNT = 20;
	
	public final static String Q = "q";
	public final static String AUTO_COMPLETE = "auto_complete";
	public final static String SORT = "sort";
	public final static String LYRICS = "lyrics";
	public final static String COUNT = "count";
	public final static String OFFSET = "offset";
	
	public VKAudioSearchRequest(String q) {
		this.q = q;
	}
	
	public String q;
	public int auto_complete = 1;
	public int sort = 2;
	public int lyrics = 0;
	public int count = DEFAULT_COUNT;
	public int offset = 0;	
}
