package jp.co.aqtor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class CsvMapping_UTF8 {
	// ＣＳＶ抽出（ただし、１行２要素のみ）
	public Map<String, String> getCsvMap(String filename) {
		Map<String, String> map = new LinkedHashMap<String, String>();

		// ファイルオープン
		try {
			File csv = new File(filename);	// CSVデータファイル
			BufferedReader br = new BufferedReader(new FileReader(csv));

			// 最終行まで読み込む
			String line = "";

			while ((line = br.readLine()) != null) {
				// 1行をデータの要素に分割
				StringTokenizer st = new StringTokenizer(line, ",");
				String cel0 = st.nextToken();
				String cel1 = st.nextToken();
				map.put(cel0, cel1);
				System.out.println("cel0=[" + cel0 + "], cel1=[" + cel1 + "].");
			}
			br.close();
		} catch ( IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}

		return map;
	}
}
