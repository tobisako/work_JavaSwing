package jp.co.aqtor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;

public class CsvMapping {
	// �b�r�u���o�i�������A�P�s�Q�v�f�̂݁j
	public Map<String, String> getCsvMap(String filename) {
		Map<String, String> map = new LinkedHashMap<String, String>();

		// �t�@�C���I�[�v��
		try {
			File csv = new File(filename);	// CSV�f�[�^�t�@�C��
			BufferedReader br = new BufferedReader(new FileReader(csv));

			// �ŏI�s�܂œǂݍ���
			String line = "";

			while ((line = br.readLine()) != null) {
				// 1�s���f�[�^�̗v�f�ɕ���
				StringTokenizer st = new StringTokenizer(line, ",");
				String cel0 = st.nextToken();
				String cel1 = st.nextToken();
				map.put(cel0, cel1);
				System.out.println("cel0=[" + cel0 + "], cel1=[" + cel1 + "].");
			}
			br.close();
		} catch ( IOException e) {
			// TODO �����������ꂽ catch �u���b�N
			e.printStackTrace();
		}

		return map;
	}
}
