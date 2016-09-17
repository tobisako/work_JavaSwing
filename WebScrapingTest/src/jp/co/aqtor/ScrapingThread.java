package jp.co.aqtor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import javax.swing.JTextArea;
import javax.swing.text.html.parser.ParserDelegator;

enum ScrapingMode {
	ARTIST, GAKKYOKU
}

//�X���b�h����
public class ScrapingThread extends Thread {
	public ThreadInterface inter;
//	private ScrapingParserCallbackArtist pcArtist;
	private ScrapingParserCallback pcCallback;

	ArrayList<Artist> artistArray;
	ArrayList<Gakkyoku> gakkyokuArray;
	ArrayList<String> artistIdArray;
	private ScrapingMode mode;
	private boolean bProcessing;	// �������s���t���O
	private boolean bSuspend;		// �������ꎞ��~���Ă�����t���O
	private JTextArea tArea;
	private String artistID;		// �A�[�e�B�X�g�h�c
	private int sIndex;
	private String sNameIndex;
	private boolean bDBWriteMode = false;	// �f�[�^�x�[�X�֏������ނ��ǂ����t���O

	// �X���b�h���s
	public void run() {
		bProcessing = true;
		System.out.println("<<<RUN-START>>>");

		if(mode  == ScrapingMode.ARTIST) {
			// �X�N���C�s���O�����i�A�[�e�B�X�g�j
			StartScraping();
			// �c�a�փA�[�e�B�X�g���������o������
			if(bDBWriteMode) {
				StartDBWriteArtist();
			}
		} else {
			// �X�N���C�s���O�����i�y�ȁj
			StartScraping();
			// �c�a�փA�[�e�B�X�g���������o������
			if(bDBWriteMode) {
				StartDBWriteGakkyoku();
			}

/*			// �܂��A�A�[�e�B�X�g�h�c���������ꗗ�����B
			artistIdArray = ReadDBArtistIdListFromIndex();
			// �A�[�e�B�X�g�h�c���ɃX�N���C�s���O�����{���y�Ȉꗗ���擾����B
			for(int i = 0; i < artistIdArray.size(); i++) {
				tArea.append( "artistId=" + artistIdArray.get(i) + "\n");
				artistID = artistIdArray.get(i);
				StartScraping();		// �X�N���C�s���O�����i�y�ȁj
				// �c�a�փA�[�e�B�X�g���������o������
				if(bDBWriteMode) {
					StartDBWriteGakkyoku(artistID);
				}
			}
*/
		}

		System.out.println("<<<RUN-END>>>");
		doCallback();
		bProcessing = false;
	}

	///////////////////////////////////////////////////////////////
	// �f�[�^�x�[�X�������݊J�n�E�A�[�e�B�X�g
	private void StartDBWriteArtist() {
		boolean bFlg = false;
		String sql = "";
		int cnt = 0;
		Connection con = DBManager.getConnection();

		// �r�p�k�쐬�E�c�a�������ݏ���
		for(int i = 0; i < artistArray.size(); i++, cnt++) {
			if(cnt >= 20 || i+1 == artistArray.size()) {
				bFlg = true;	// �Q�O�����܂���or�Ō�̃f�[�^�ł��t���O�n�m
				cnt = 0;
			} else {
				bFlg = false;
			}

			// SQL�쐬�i�A�[�e�B�X�g�p�j
			sql = MakeArtistSqlString(bFlg, sql, Integer.valueOf(artistArray.get(i).GetArtistId()),
												sNameIndex, artistArray.get(i).GetArtistName());

			// �Q�O���̂r�p�k���o�������Ō�̃f�[�^�Ȃ�c�a�֏�������
			if( bFlg == true ) {
				System.out.println("sql=[" + sql + "]");
				WriteDB(con, sql);

				try {
					this.sleep(200);
				} catch (InterruptedException e) {
					// TODO �����������ꂽ catch �u���b�N
					e.printStackTrace();
				}
				sql = "";
			}
		}

		try {
			con.close();
		} catch (SQLException e) {
			// TODO �����������ꂽ catch �u���b�N
			e.printStackTrace();
		}
	}

	// �r�p�k�����i�P�A�N�Z�X�łT�O���R�[�h���j�A�[�e�B�X�g�p
	private String MakeArtistSqlString(boolean bEndFlg, String sql, int id, String nameIdx, String name) {
		if(sql.equals("")) {
			sql = "INSERT INTO kara.artist (artistid, nameindex, name) VALUES";
		}
		name = name.replaceAll("'", "''");
		sql += String.format(" (%d, '%s', '%s')", id, nameIdx, name);
		if(!bEndFlg) {
			sql += ",";
		}
		return sql;
	}

	// �c�a�������ݏ���
	private void WriteDB(Connection con, String sql) {
		// �c�a�ڑ�
		try {
			Statement stmt = con.createStatement();
			boolean bResult = stmt.execute( sql );

			tArea.append("DB-WRITE!(" + bResult + ")\n");
			tArea.setCaretPosition( tArea.getText().length() );
			stmt.close();
		} catch (SQLException e) {
			// TODO �����������ꂽ catch �u���b�N
			e.printStackTrace();
		}
	}

	///////////////////////////////////////////////////////////////
	// �f�[�^�x�[�X�������݊J�n�E�y��
	private void StartDBWriteGakkyoku() {
		boolean bFlg = false;
		String sql = "";
		int cnt = 0;
		Connection con = DBManager.getConnection();

		// �r�p�k�쐬�E�c�a�������ݏ���
		for(int i = 0; i < gakkyokuArray.size(); i++, cnt++) {
			if(cnt >= 20 || i+1 == gakkyokuArray.size()) {
				bFlg = true;	// �Q�O�����܂���or�Ō�̃f�[�^�ł��t���O�n�m
				cnt = 0;
			} else {
				bFlg = false;
			}

			// SQL�쐬�i�y�ȗp�j
			sql = MakeGakkyokuSqlString(bFlg, sql,
										Integer.valueOf(gakkyokuArray.get(i).getGakkyokuId()),
										Integer.valueOf(gakkyokuArray.get(i).getArtistId()),
										sNameIndex, gakkyokuArray.get(i).getGakkyokuTitle());

			// �Q�O���̂r�p�k���o�������Ō�̃f�[�^�Ȃ�c�a�֏�������
			if( bFlg == true ) {
				System.out.println("sql=[" + sql + "]");
				WriteDB(con, sql);

				try {
					this.sleep(200);
				} catch (InterruptedException e) {
					// TODO �����������ꂽ catch �u���b�N
					e.printStackTrace();
				}
				sql = "";
			}
		}

		try {
			con.close();
		} catch (SQLException e) {
			// TODO �����������ꂽ catch �u���b�N
			e.printStackTrace();
		}
	}

	// �r�p�k�����i�P�A�N�Z�X�łT�O���R�[�h���j�y�ȗp
	private String MakeGakkyokuSqlString(boolean bEndFlg, String sql, int gakkyokuid,
											int artistid, String titleIdx, String title) {
		if(sql.equals("")) {
			sql = "INSERT INTO kara.gakkyoku (gakkyokuid, artistid, titleindex, title) VALUES";
		}
		title = title.replaceAll("'", "''");
		sql += String.format(" (%d, %d, '%s', '%s')", gakkyokuid, artistid, titleIdx, title);
		if(!bEndFlg) {
			sql += ",";
		}
		return sql;
	}

	// �K�͂����������B�B�B�A�[�e�B�X�g�P�l�̊y�Ȃ͂���������Ă���B
	// �Ȃ�΁A�P�X���b�h�Łu���s����ԁv�Ƃ��̃f�U�C���ōs�����B

	///////////////////////////////////////////////////////////////////
	// �A�[�e�B�X�g���������i�L�[���[�h�����Ɍ������ăA�[�e�B�X�g�h�c��񋓂���B
	private ArrayList<String> ReadDBArtistIdListFromIndex()
	{
		ArrayList<String> artistIdArray = new ArrayList<String>();
		Connection con = DBManager.getConnection();
		String sql = "select artistid from `artist` where nameindex='��'";

		try {
			Statement stmt = con.createStatement();
			ResultSet result = stmt.executeQuery( sql );

			while (result.next()) {
				tArea.append("DB-READ! " + result.getString(1) + "\n");
				artistIdArray.add(result.getString(1));
			}
			tArea.setCaretPosition( tArea.getText().length() );

			stmt.close();

		} catch (SQLException e) {
			// TODO �����������ꂽ catch �u���b�N
			e.printStackTrace();
		}
		return artistIdArray;
	}

	// �X�N���C�s���O����������
	private ScrapingParserCallback initScraping(ScrapingMode mode) {
		if(mode  == ScrapingMode.ARTIST) {
			// �A�[�e�B�X�g���o�̏�����
			artistArray = new ArrayList<Artist>();
			return new ScrapingParserCallbackArtist();
		} else {
			// �y�Ȓ��o�̏�����
			gakkyokuArray = new ArrayList<Gakkyoku>();
			return new ScrapingParserCallbackGakkyoku();
		}
	}

	///////////////////////////////////////////////////////////////////
	// �X�N���C�s���O�J�n�E�A�[�e�B�X�g�{�y��
	private void StartScraping() {
		int currentNum = 0;
		int planNum = 0;
		int planVnum = 20;

		tArea.append("\nStartScraping()! - mode=" + mode + "\n");
		tArea.setCaretPosition( tArea.getText().length() );
		System.out.println("StartScraping()");

		// �X�N���C�s���O������
		//		pcArtist = new ScrapingParserCallbackArtist();
//		pcCallback = new ScrapingParserCallbackArtist();
		pcCallback = initScraping(mode);

		// URL����
//		String url = "http://joysound.com/ex/search/artistsearchindex.htm?searchType=02&searchWordType=2";
//		//st += "&charIndexKbn=01&charIndex1=101";
//		url += String.format("&charIndexKbn=01&charIndex1=%d", sIndex);
		String url = MakeBaseURL(mode);

		do {
			// �w��URL�̃A�[�e�B�X�g����y�Ȗ������ׂĒ��o����B
			currentNum += ScrapingBody(pcCallback, url, currentNum);
			planNum += planVnum;		// �\��ł͂Q�O�������Ă����n�Y�ł���B
			tArea.append("total cnt = " + currentNum + ", max cnt = " + pcCallback.GetMaxHit() + ".\n");
			tArea.setCaretPosition( tArea.getText().length() );

			ThreadSleep(100);

			// �擾���Ɨ\�萔�`�F�b�N
			if(currentNum != planNum && pcCallback.GetMaxHit() > currentNum) {
				// �擾���ʂ����������i�Q�O������������j
				planNum -= planVnum;
				currentNum = planNum;	// ��蒼��
				System.out.println("read fail. reaccess.");
				tArea.append("FAIL!!! retry now.\n");
				tArea.setCaretPosition( tArea.getText().length() );
			} else {
				// ����Ȏ擾�E�X�N���C�s���O���ʔz����}�[�W����
				if(mode  == ScrapingMode.ARTIST) {
					// �A�[�e�B�X�g
					ArrayList<Artist> KekkaartistArray = ((ScrapingParserCallbackArtist)pcCallback).GetArtistArray();
					artistArray.addAll(KekkaartistArray);
				} else {
					// �y��
					ArrayList<Gakkyoku> KekkagakkyokuArray = ((ScrapingParserCallbackGakkyoku)pcCallback).GetGakkyokuArray();
					gakkyokuArray.addAll(KekkagakkyokuArray);
				}
			}

			// �X�N���C�s���O�N���X�̏�����
			pcCallback.ResetScraping();

			// �ꎞ��~�`�F�b�N
			while(bSuspend) {
				ThreadSleep(500);
			}

			// �\�葝���̎Z�o
			planVnum = (pcCallback.GetMaxHit() > currentNum) ? 20 : pcCallback.GetMaxHit() - currentNum;
			System.out.println("planVnum=" + planVnum);

		} while(pcCallback.GetMaxHit() > currentNum && bProcessing);

		// �����E�o��
		tArea.append("------------------------------\n");
		tArea.append("Scraping Count = " + currentNum + ", Max Hit = " + pcCallback.GetMaxHit() + "\n");
		tArea.append("--- Scraping Done. ---\n");
		tArea.setCaretPosition( tArea.getText().length() );
	}

	// �t�q�k�����i�x�[�X�j
	public String MakeBaseURL(ScrapingMode mode) {
		String url;

		if(mode == ScrapingMode.ARTIST) {
			// URL�����E�A�[�e�B�X�g
			url = "http://joysound.com/ex/search/artistsearchindex.htm?searchType=02&searchWordType=2";
			//st += "&charIndexKbn=01&charIndex1=101";
			url += String.format("&charIndexKbn=01&charIndex1=%d", sIndex);
		} else {
			// URL�����E�y��
			//url = "http://joysound.com/ex/search/artist.htm";
			//url += "?artistId=" + artistID;		// 200193
			url = "http://joysound.com/ex/search/songsearchindex.htm?searchType=02&searchWordType=2";
			url += String.format("&charIndexKbn=01&charIndex1=%d", sIndex);
		}
		return url;
	}

	// �X�N���C�s���O�E�w��I�t�Z�b�g����擾����B
	public int ScrapingBody(ScrapingParserCallback pcCallback, String urlstr, int offset) {
		URL url;
		HttpURLConnection httpoc;
		BufferedReader bstr;
		int cnt;

		urlstr += String.format( "&offset=%d", offset );

		do {
			try {
				// 1.URL�I�u�W�F�N�g�̐����@�䂸�iJoysound�j
				url = new URL(urlstr);

				// 2.�ڑ��I�u�W�F�N�g�̎擾�E�ڑ�
				httpoc = (HttpURLConnection)url.openConnection();
				httpoc.setReadTimeout(15000);
				httpoc.setConnectTimeout(15000);
				httpoc.connect();
				bstr = new BufferedReader(new InputStreamReader(httpoc.getInputStream(), "UTF-8"));

				// ��͏���
				ParserDelegator pd = new ParserDelegator();
				pd.parse(bstr, pcCallback, true);

				// �N���[�Y
				bstr.close();
				httpoc.disconnect();
				break;	// �����I�������while�𔲂���B

			} catch (SocketTimeoutException se) {
				System.out.println("Exception! HTTP : Read timed out");
				tArea.append( "<<< Exception! HTTP : Read timed out >>>\n");
				tArea.setCaretPosition( tArea.getText().length() );
				ThreadSleep(2000);	// �Q�b�܂�
			} catch (IOException e) {
				// TODO �����������ꂽ catch �u���b�N
				e.printStackTrace();
				System.out.println("Exception! ScrapingArtistBody");
			}
			ThreadSleep(100);
		} while(true);

		// �o��
		tArea.append("------------------------------\n");
		tArea.append("url=[" + urlstr + "]\n");

		// �f�[�^���
		if(mode == ScrapingMode.ARTIST) {
			// �A�[�e�B�X�g
			ArrayList<Artist> artistArray = ((ScrapingParserCallbackArtist)pcCallback).GetArtistArray();
			tArea.append( "Get data size = " + artistArray.size() + "\n");
			// ���j�^�o�́��f�o�b�O�o��
			for(int i = 0; i < artistArray.size(); i++) {
				tArea.append( "artistId=" + artistArray.get(i).GetArtistId() + "," +
								"artistName=�u" + artistArray.get(i).GetArtistName() + "�v\n" );
				// System.out.println(artistArray.get(i).GetArtistId() + artistArray.get(i).GetArtistName());
			}
		} else {
			// �y��
			ArrayList<Gakkyoku> gakkyokuArray = ((ScrapingParserCallbackGakkyoku)pcCallback).GetGakkyokuArray();
			//tArea.append( "Check Artist ID = [" + artistID + "]\n");
			//tArea.append( "Get Artist name = [" + ((ScrapingParserCallbackGakkyoku)pcCallback).GetArtistName() + "]\n");
			tArea.append( "Get data size = " + gakkyokuArray.size() + "\n");
			// ���j�^�o�́��f�o�b�O�o��
			for(int i = 0; i < gakkyokuArray.size(); i++) {
				tArea.append( "��(" + gakkyokuArray.get(i).getGakkyokuId() + ")" +
								gakkyokuArray.get(i).getGakkyokuTitle() + "(" +
								gakkyokuArray.get(i).getArtistId() + ":" +
								gakkyokuArray.get(i).getArtistName() + ")\n");
				// System.out.println(artistArray.get(i).GetArtistId() + artistArray.get(i).GetArtistName());
			}
		}
		cnt = pcCallback.GetCount();

		// �o��
		//tArea.append( pcArtist.getLogStr() + "\n");
		tArea.append( "get cnt = " + cnt + "\n");
		tArea.setCaretPosition( tArea.getText().length() );

		// �f�o�b�O�o��
		System.out.println( pcCallback.getLogStr() );
		System.out.println( "get cnt = " + cnt );
		pcCallback.resetLogStr();

		return cnt;
	}



	// �ȉ��͔p�~�������B�㕔�œ�������ׂ��B
	///////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////
	// �X�N���C�s���O�J�n�E�y��

/*
	private void StartScrapingGakkyoku() {
		int currentNum = 0;
		ScrapingParserCallbackGakkyoku pcGakkyoku = new ScrapingParserCallbackGakkyoku();

		tArea.append("\nStartScrapingGakkyoku()!\n");
		tArea.setCaretPosition( tArea.getText().length() );
		System.out.println("StartScrapingGakkyoku()");

		// �e�X�g�t�q�k
		// http://joysound.com/ex/search/artist.htm?artistId=200193

		// URL����
		String url = "http://joysound.com/ex/search/artist.htm";
		url += "?artistId=" + artistID;		// 200193

		do {
			// �w��URL�̊y�Ȗ������ׂĒ��o����B
			currentNum = ScrapingGakkyokuBody(pcGakkyoku, url, currentNum);
		} while( pcGakkyoku.GetMaxHit() > currentNum  && bProcessing );

		// �����E�o��
		tArea.append("------------------------------\n");
		tArea.append("Scraping Count = " + currentNum + ", Max Hit = " + pcGakkyoku.GetMaxHit() + "\n");
		tArea.append("--- Scraping Done. ---\n");
		tArea.setCaretPosition( tArea.getText().length() );
	}
*/


	// �X�N���C�s���O�E�w��I�t�Z�b�g����擾����B
/*
	public int ScrapingGakkyokuBody(ScrapingParserCallbackGakkyoku pcGakkyoku, String urlstr, int offset) {
		URL url;
		HttpURLConnection httpoc;
		BufferedReader bstr;
		int cnt = offset;

		try {
			// 1.URL�I�u�W�F�N�g�̐����@�䂸�iJoysound�j
			urlstr += String.format( "&offset=%d", offset );
			url = new URL(urlstr);

			// 2.�ڑ��I�u�W�F�N�g�̎擾�E�ڑ�
			httpoc = (HttpURLConnection)url.openConnection();
			httpoc.connect();
			bstr = new BufferedReader(new InputStreamReader(httpoc.getInputStream(), "UTF-8"));

			// ��͏���
			ParserDelegator pd = new ParserDelegator();
			pd.parse(bstr, pcGakkyoku, true);

			// �N���[�Y
			bstr.close();
			httpoc.disconnect();

			// �o��
			tArea.append("------------------------------\n");
			tArea.append("url=[" + urlstr + "]\n");
			tArea.append("�A�[�e�B�X�g���u" + pcGakkyoku.GetArtistName() + "�v\n");

			// �f�[�^���
			ArrayList<Gakkyoku> gakkyokuArray = pcGakkyoku.GetGakkyokuArray();
			tArea.append( "Get data size = " + gakkyokuArray.size() + "\n");

			for(int i = offset; i < gakkyokuArray.size(); i++) {
				tArea.append( "gakkyokuId=" + gakkyokuArray.get(i).GetGakkyokuId() + "," +
						"TITLE=�u" + gakkyokuArray.get(i).GetGakkyokuTitle() + "�v\n" );
				//System.out.println(gakkyokuArray.get(i).GetGakkyokuId() + gakkyokuArray.get(i).GetGakkyokuName());
			}
			cnt = pcGakkyoku.GetCount();

			//tArea.append( pcGakkyoku.getLogStr() + "\n");
			tArea.append( "cnt = " + cnt + " / " + pcGakkyoku.GetMaxHit() + "\n");
			tArea.setCaretPosition( tArea.getText().length() );

			// �f�o�b�O�o��
			System.out.println( pcGakkyoku.getLogStr() );
			System.out.println( "cnt = " + cnt );

		} catch (Exception e) {
			// TODO �����������ꂽ catch �u���b�N
			e.printStackTrace();
		}

		return cnt;
	}
*/

	// �X���b�h�E�X���[�v
	public void ThreadSleep( int t ) {
		try {
			Thread.sleep( t );
		} catch (InterruptedException e) {
			// TODO �����������ꂽ catch �u���b�N
			e.printStackTrace();
			System.out.println("Exception! ThreadSleep");
		}
	}

	// �I���ʒm�R�[���o�b�N�o�^
	public void registerCallback(ThreadInterface call) {
		this.inter = call;
	}

	// �I���ʒm�R�[���o�b�N���s
	public void doCallback() {
		if(inter != null) {
			inter.Callback();
		}
	}

	// �X���b�h��~��v������`�o�h
	public void StopThread() {
		bSuspend = false;
		bProcessing = false;
	}

	// �X���b�h�������̈ꎞ��~��v������`�o�h
	public void SuspendProcess() {
		bSuspend = bSuspend ? false : true;
	}

	// �X���b�h���������ꎞ��~��Ԃ��ǂ����𒲂ׂ�`�o�h
	public boolean isSuspendProcess() {
		return bSuspend;
	}

	// �X�N���C�s���O���[�h�w��i�A�[�e�B�X�g�����^�y�Ȍ����j
	public void SetScrapingMode(ScrapingMode m) {
		mode = m;
	}

	// �X���b�h���Łu�e�L�X�g�G���A�v�ɏ������ވׂɖႤ�B
	public void SetTextArea(JTextArea ta) {
		tArea = ta;
	}

	// �c�a�������݃��[�h�̐ݒ�
	public void SetDBWriteMode(boolean b) {
		bDBWriteMode = b;
	}

	// �����C���f�b�N�X�ԍ���Ⴄ�i�����������E�E�E�݂����Ȃ��̂𐔒l���B�j
	public void SetScrapingIndex(int idx) {
		sIndex = idx;
	}
	public void SetScrapingIndex(String str, int idx) {
		sNameIndex = str;
		sIndex = idx;
	}

	// �A�[�e�B�X�g�h�c���Z�b�g����B
	public void SetArtistID(String id) {
		artistID = id;
	}
}
