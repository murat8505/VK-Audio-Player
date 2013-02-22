package com.iradetskiy.vkapi;

public class VKAudioGetRequest {
	public final static int DEFAULT_COUNT = 20;
	
	public final static String UID = "uid";
	public final static String GID = "gid";
	public final static String ALBUM_ID = "album_id";
	public final static String AIDS = "aids";
	public final static String NEED_USER = "need_user";
	public final static String COUNT = "count";
	public final static String OFFSET = "offset";
	
	public VKAudioGetRequest(String uid) {
		this.uid = uid;
	}
	
	public String uid;
	public String gid = "";
	public String album_id = "";
	public String aids = "";
	public int need_user = 0;
	public int count = DEFAULT_COUNT;
	public int offset = 0;
}
