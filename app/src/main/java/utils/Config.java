package utils;

/**
 * Created by ludovicofabbri on 11/10/16.
 */

public final class Config {

    public static final int TOAST_DURATION = 4000;

    public static final String YOUTUBE_ANDROID_KEY = "AIzaSyB5YJjwIE9BaoWkoYyk401X2umRS_kTexM";
    public static final String YOUTUBE_API_KEY = "AIzaSyBer1B3B2wqGvg1MZGTVxAcE81Tz7LlF44";
    public static final String YOUTUBE_QUERY_URL = "https://www.googleapis.com/youtube/v3/search";



    public static final int NAV_MAIN_STATE = 0;
    public static final int NAV_LOGIN_STATE = 1;
    public static final int NAV_REGISTER_STATE = 2;
    public static final int NAV_YOUTUBE_STATE = 3;
    public static final int NAV_TAGS_STATE = 4;
    public static final int NAV_MAPS_STATE = 5;
    public static final int NAV_SETTINGS_STATE = 6;
    public static final int NAV_INFO_STATE = 7;
    public static final int NAV_DEFAULT_STATE = NAV_LOGIN_STATE;




//    public static final String PY_SERVER_BASE_URL = "http://192.168.1.72:4500";
    public static final String PY_SERVER_BASE_URL = "http://192.168.1.75:4500";
//    public static final String PY_SERVER_BASE_URL = "http://2.236.20.78:4500";
//    public static final String PY_SERVER_BASE_URL = "http://172.20.10.3:4500";

    public static final String PY_SERVER_REGISTER_URL = PY_SERVER_BASE_URL + "/auth/register";
    public static final String PY_SERVER_LOGIN_URL = PY_SERVER_BASE_URL + "/auth/login";
    public static final String PY_SERVER_AUTHORIZED_URL = PY_SERVER_BASE_URL + "/auth/isauthorized";
    public static final String PY_SERVER_TAGS_URL = PY_SERVER_BASE_URL + "/api/tags";
    public static final String PY_SERVER_USER_TAGS_URL = PY_SERVER_BASE_URL + "/api/user_tags";
    public static final String PY_SERVER_UPDATE_TAGS_URL = PY_SERVER_BASE_URL + "/api/update_tags";
    public static final String PY_SERVER_UPDATE_DISLIKES_URL = PY_SERVER_BASE_URL + "/api/update_dislikes";
    public static final String PY_SERVER_UPDATE_LISTENED_URL = PY_SERVER_BASE_URL + "/api/update_listened";
    public static final String PY_SERVER_SET_TELEPORT_URL = PY_SERVER_BASE_URL + "/api/set_teleport";
    public static final String PY_SERVER_SET_SHARE_POSITION_URL = PY_SERVER_BASE_URL + "/api/set_share_position";
    public static final String PY_SERVER_SET_ACTIVE_URL = PY_SERVER_BASE_URL + "/api/set_active";
    public static final String PY_SERVER_NEXT_TRACKS_URL = PY_SERVER_BASE_URL + "/api/next_tracks";


    public static final int HTTP_STATUS_CODE_OK = 200;
    public static final int HTTP_STATUS_CODE_CREATED = 201;
    public static final int HTTP_STATUS_CODE_NO_CONTENT = 204;
    public static final int HTTP_STATUS_CODE_ERROR = 400;
    public static final int HTTP_STATUS_CODE_UNAUTHORIZED = 401;
    public static final int HTTP_STATUS_CODE_SERVER_ERROR = 500;


    public static final String SHARED_PREF_LAST_RECOMMENDATIONS = "Last_Recommendations";
    public static final String SHARED_PREF_LAST_RECOMMENDATIONS_INDEX = "Last_Recommendations_index";
    public static final String ARTIST_NAME = "artist_name";
    public static final String SONG_TITLE = "song_title";

}
