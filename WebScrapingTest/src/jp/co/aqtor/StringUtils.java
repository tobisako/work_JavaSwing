package jp.co.aqtor;

// http://d.hatena.ne.jp/wistery_k/20110824/1314145373

import java.util.HashMap;
import java.util.Map;

public class StringUtils {
	public static Map<String, String> getQueryMap(String query) {
		String[] params = query.split("&");
		Map<String, String> map = new HashMap<String, String>();

		for(String param : params) {
			String[] splitted = param.split("=");
			if( splitted.length == 2 ) {
				map.put(splitted[0], splitted[1]);
			} else {
				map.put(splitted[0], "");
			}
		}

		return map;
	}
}
