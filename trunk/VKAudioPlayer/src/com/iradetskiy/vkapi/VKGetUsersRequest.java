package com.iradetskiy.vkapi;

public class VKGetUsersRequest {
	public static final String UIDS = "uids";
	public static final String FIELDS = "fields";
	public static final String NAME_CASE = "name_case";
	
	public static final String FIELD_UID = "uid";
	public static final String FIELD_FIRST_NAME = "first_name";
	public static final String FIELD_LAST_NAME = "last_name";
	public static final String FIELD_NICKNAME = "nickname";
	public static final String FIELD_SCREEN_NAME = "screen_name";
	public static final String FIELD_SEX = "sex";
	public static final String FIELD_BDATE = "bdate";
	public static final String FIELD_CITY = "city";
	public static final String FIELD_COUNTRY = "country";
	public static final String FIELD_TIMEZONE = "timezone";
	public static final String FIELD_PHOTO = "photo";
	public static final String FIELD_PHOTO_MEDIUM = "photo_medium";
	public static final String FIELD_PHOTO_BIG = "photo_big";
	public static final String FIELD_HAS_MOBILE = "has_mobile";
	public static final String FIELD_RATE = "rate";
	public static final String FIELD_CONTACTS = "contacts";
	public static final String FIELD_EDUCATION = "education";
	public static final String FIELD_ONLINE = "online";
	public static final String FIELD_COUNTERS = "counters";
	
	public static final String NAME_CASE_NOM = "nom";
	public static final String NAME_CASE_GEN = "gen";
	public static final String NAME_CASE_DAT = "dat";
	public static final String NAME_CASE_ACC = "acc";
	public static final String NAME_CASE_INS = "ins";
	public static final String NAME_CASE_ABL = "abl";
	
	public VKGetUsersRequest(String uids) {
		this.uids = uids;
	}
	
	public String uids;
	public String fields = FIELD_FIRST_NAME + "," + FIELD_LAST_NAME;
	public String name_case = NAME_CASE_NOM;
}
