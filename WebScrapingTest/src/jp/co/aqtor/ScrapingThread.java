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

//スレッド処理
public class ScrapingThread extends Thread {
	public ThreadInterface inter;
//	private ScrapingParserCallbackArtist pcArtist;
	private ScrapingParserCallback pcCallback;

	ArrayList<Artist> artistArray;
	ArrayList<Gakkyoku> gakkyokuArray;
	ArrayList<String> artistIdArray;
	private ScrapingMode mode;
	private boolean bProcessing;	// 処理実行中フラグ
	private boolean bSuspend;		// 処理を一時停止しておくれフラグ
	private JTextArea tArea;
	private String artistID;		// アーティストＩＤ
	private int sIndex;
	private String sNameIndex;
	private boolean bDBWriteMode = false;	// データベースへ書き込むかどうかフラグ

	// スレッド実行
	public void run() {
		bProcessing = true;
		System.out.println("<<<RUN-START>>>");

		if(mode  == ScrapingMode.ARTIST) {
			// スクレイピング処理（アーティスト）
			StartScraping();
			// ＤＢへアーティスト情報を書き出す処理
			if(bDBWriteMode) {
				StartDBWriteArtist();
			}
		} else {
			// スクレイピング処理（楽曲）
			StartScraping();
			// ＤＢへアーティスト情報を書き出す処理
			if(bDBWriteMode) {
				StartDBWriteGakkyoku();
			}

/*			// まず、アーティストＩＤを検索し一覧を作る。
			artistIdArray = ReadDBArtistIdListFromIndex();
			// アーティストＩＤ毎にスクレイピングを実施し楽曲一覧を取得する。
			for(int i = 0; i < artistIdArray.size(); i++) {
				tArea.append( "artistId=" + artistIdArray.get(i) + "\n");
				artistID = artistIdArray.get(i);
				StartScraping();		// スクレイピング処理（楽曲）
				// ＤＢへアーティスト情報を書き出す処理
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
	// データベース書き込み開始・アーティスト
	private void StartDBWriteArtist() {
		boolean bFlg = false;
		String sql = "";
		int cnt = 0;
		Connection con = DBManager.getConnection();

		// ＳＱＬ作成・ＤＢ書き込み処理
		for(int i = 0; i < artistArray.size(); i++, cnt++) {
			if(cnt >= 20 || i+1 == artistArray.size()) {
				bFlg = true;	// ２０個分たまったor最後のデータですフラグＯＮ
				cnt = 0;
			} else {
				bFlg = false;
			}

			// SQL作成（アーティスト用）
			sql = MakeArtistSqlString(bFlg, sql, Integer.valueOf(artistArray.get(i).GetArtistId()),
												sNameIndex, artistArray.get(i).GetArtistName());

			// ２０個分のＳＱＬが出来たか最後のデータならＤＢへ書き込む
			if( bFlg == true ) {
				System.out.println("sql=[" + sql + "]");
				WriteDB(con, sql);

				try {
					this.sleep(200);
				} catch (InterruptedException e) {
					// TODO 自動生成された catch ブロック
					e.printStackTrace();
				}
				sql = "";
			}
		}

		try {
			con.close();
		} catch (SQLException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}

	// ＳＱＬ生成（１アクセスで５０レコード分）アーティスト用
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

	// ＤＢ書き込み処理
	private void WriteDB(Connection con, String sql) {
		// ＤＢ接続
		try {
			Statement stmt = con.createStatement();
			boolean bResult = stmt.execute( sql );

			tArea.append("DB-WRITE!(" + bResult + ")\n");
			tArea.setCaretPosition( tArea.getText().length() );
			stmt.close();
		} catch (SQLException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}

	///////////////////////////////////////////////////////////////
	// データベース書き込み開始・楽曲
	private void StartDBWriteGakkyoku() {
		boolean bFlg = false;
		String sql = "";
		int cnt = 0;
		Connection con = DBManager.getConnection();

		// ＳＱＬ作成・ＤＢ書き込み処理
		for(int i = 0; i < gakkyokuArray.size(); i++, cnt++) {
			if(cnt >= 20 || i+1 == gakkyokuArray.size()) {
				bFlg = true;	// ２０個分たまったor最後のデータですフラグＯＮ
				cnt = 0;
			} else {
				bFlg = false;
			}

			// SQL作成（楽曲用）
			sql = MakeGakkyokuSqlString(bFlg, sql,
										Integer.valueOf(gakkyokuArray.get(i).getGakkyokuId()),
										Integer.valueOf(gakkyokuArray.get(i).getArtistId()),
										sNameIndex, gakkyokuArray.get(i).getGakkyokuTitle());

			// ２０個分のＳＱＬが出来たか最後のデータならＤＢへ書き込む
			if( bFlg == true ) {
				System.out.println("sql=[" + sql + "]");
				WriteDB(con, sql);

				try {
					this.sleep(200);
				} catch (InterruptedException e) {
					// TODO 自動生成された catch ブロック
					e.printStackTrace();
				}
				sql = "";
			}
		}

		try {
			con.close();
		} catch (SQLException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}

	// ＳＱＬ生成（１アクセスで５０レコード分）楽曲用
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

	// 規模がちがうか。。。アーティスト１人の楽曲はたかがしれている。
	// ならば、１スレッドで「あ行ぜんぶ」とかのデザインで行こう。

	///////////////////////////////////////////////////////////////////
	// アーティスト検索処理（キーワードを元に検索してアーティストＩＤを列挙する。
	private ArrayList<String> ReadDBArtistIdListFromIndex()
	{
		ArrayList<String> artistIdArray = new ArrayList<String>();
		Connection con = DBManager.getConnection();
		String sql = "select artistid from `artist` where nameindex='や'";

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
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		return artistIdArray;
	}

	// スクレイピング初期化処理
	private ScrapingParserCallback initScraping(ScrapingMode mode) {
		if(mode  == ScrapingMode.ARTIST) {
			// アーティスト抽出の初期化
			artistArray = new ArrayList<Artist>();
			return new ScrapingParserCallbackArtist();
		} else {
			// 楽曲抽出の初期化
			gakkyokuArray = new ArrayList<Gakkyoku>();
			return new ScrapingParserCallbackGakkyoku();
		}
	}

	///////////////////////////////////////////////////////////////////
	// スクレイピング開始・アーティスト＋楽曲
	private void StartScraping() {
		int currentNum = 0;
		int planNum = 0;
		int planVnum = 20;

		tArea.append("\nStartScraping()! - mode=" + mode + "\n");
		tArea.setCaretPosition( tArea.getText().length() );
		System.out.println("StartScraping()");

		// スクレイピング初期化
		//		pcArtist = new ScrapingParserCallbackArtist();
//		pcCallback = new ScrapingParserCallbackArtist();
		pcCallback = initScraping(mode);

		// URL生成
//		String url = "http://joysound.com/ex/search/artistsearchindex.htm?searchType=02&searchWordType=2";
//		//st += "&charIndexKbn=01&charIndex1=101";
//		url += String.format("&charIndexKbn=01&charIndex1=%d", sIndex);
		String url = MakeBaseURL(mode);

		do {
			// 指定URLのアーティスト名や楽曲名をすべて抽出する。
			currentNum += ScrapingBody(pcCallback, url, currentNum);
			planNum += planVnum;		// 予定では２０ずつ増えていくハズである。
			tArea.append("total cnt = " + currentNum + ", max cnt = " + pcCallback.GetMaxHit() + ".\n");
			tArea.setCaretPosition( tArea.getText().length() );

			ThreadSleep(100);

			// 取得数と予定数チェック
			if(currentNum != planNum && pcCallback.GetMaxHit() > currentNum) {
				// 取得結果がおかしい（２０未満だったり）
				planNum -= planVnum;
				currentNum = planNum;	// やり直し
				System.out.println("read fail. reaccess.");
				tArea.append("FAIL!!! retry now.\n");
				tArea.setCaretPosition( tArea.getText().length() );
			} else {
				// 正常な取得・スクレイピング結果配列をマージする
				if(mode  == ScrapingMode.ARTIST) {
					// アーティスト
					ArrayList<Artist> KekkaartistArray = ((ScrapingParserCallbackArtist)pcCallback).GetArtistArray();
					artistArray.addAll(KekkaartistArray);
				} else {
					// 楽曲
					ArrayList<Gakkyoku> KekkagakkyokuArray = ((ScrapingParserCallbackGakkyoku)pcCallback).GetGakkyokuArray();
					gakkyokuArray.addAll(KekkagakkyokuArray);
				}
			}

			// スクレイピングクラスの初期化
			pcCallback.ResetScraping();

			// 一時停止チェック
			while(bSuspend) {
				ThreadSleep(500);
			}

			// 予定増分の算出
			planVnum = (pcCallback.GetMaxHit() > currentNum) ? 20 : pcCallback.GetMaxHit() - currentNum;
			System.out.println("planVnum=" + planVnum);

		} while(pcCallback.GetMaxHit() > currentNum && bProcessing);

		// 完了・出力
		tArea.append("------------------------------\n");
		tArea.append("Scraping Count = " + currentNum + ", Max Hit = " + pcCallback.GetMaxHit() + "\n");
		tArea.append("--- Scraping Done. ---\n");
		tArea.setCaretPosition( tArea.getText().length() );
	}

	// ＵＲＬ生成（ベース）
	public String MakeBaseURL(ScrapingMode mode) {
		String url;

		if(mode == ScrapingMode.ARTIST) {
			// URL生成・アーティスト
			url = "http://joysound.com/ex/search/artistsearchindex.htm?searchType=02&searchWordType=2";
			//st += "&charIndexKbn=01&charIndex1=101";
			url += String.format("&charIndexKbn=01&charIndex1=%d", sIndex);
		} else {
			// URL生成・楽曲
			//url = "http://joysound.com/ex/search/artist.htm";
			//url += "?artistId=" + artistID;		// 200193
			url = "http://joysound.com/ex/search/songsearchindex.htm?searchType=02&searchWordType=2";
			url += String.format("&charIndexKbn=01&charIndex1=%d", sIndex);
		}
		return url;
	}

	// スクレイピング・指定オフセットから取得する。
	public int ScrapingBody(ScrapingParserCallback pcCallback, String urlstr, int offset) {
		URL url;
		HttpURLConnection httpoc;
		BufferedReader bstr;
		int cnt;

		urlstr += String.format( "&offset=%d", offset );

		do {
			try {
				// 1.URLオブジェクトの生成　ゆず（Joysound）
				url = new URL(urlstr);

				// 2.接続オブジェクトの取得・接続
				httpoc = (HttpURLConnection)url.openConnection();
				httpoc.setReadTimeout(15000);
				httpoc.setConnectTimeout(15000);
				httpoc.connect();
				bstr = new BufferedReader(new InputStreamReader(httpoc.getInputStream(), "UTF-8"));

				// 解析処理
				ParserDelegator pd = new ParserDelegator();
				pd.parse(bstr, pcCallback, true);

				// クローズ
				bstr.close();
				httpoc.disconnect();
				break;	// 無事終了すればwhileを抜ける。

			} catch (SocketTimeoutException se) {
				System.out.println("Exception! HTTP : Read timed out");
				tArea.append( "<<< Exception! HTTP : Read timed out >>>\n");
				tArea.setCaretPosition( tArea.getText().length() );
				ThreadSleep(2000);	// ２秒まつ
			} catch (IOException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
				System.out.println("Exception! ScrapingArtistBody");
			}
			ThreadSleep(100);
		} while(true);

		// 出力
		tArea.append("------------------------------\n");
		tArea.append("url=[" + urlstr + "]\n");

		// データ解析
		if(mode == ScrapingMode.ARTIST) {
			// アーティスト
			ArrayList<Artist> artistArray = ((ScrapingParserCallbackArtist)pcCallback).GetArtistArray();
			tArea.append( "Get data size = " + artistArray.size() + "\n");
			// モニタ出力＆デバッグ出力
			for(int i = 0; i < artistArray.size(); i++) {
				tArea.append( "artistId=" + artistArray.get(i).GetArtistId() + "," +
								"artistName=「" + artistArray.get(i).GetArtistName() + "」\n" );
				// System.out.println(artistArray.get(i).GetArtistId() + artistArray.get(i).GetArtistName());
			}
		} else {
			// 楽曲
			ArrayList<Gakkyoku> gakkyokuArray = ((ScrapingParserCallbackGakkyoku)pcCallback).GetGakkyokuArray();
			//tArea.append( "Check Artist ID = [" + artistID + "]\n");
			//tArea.append( "Get Artist name = [" + ((ScrapingParserCallbackGakkyoku)pcCallback).GetArtistName() + "]\n");
			tArea.append( "Get data size = " + gakkyokuArray.size() + "\n");
			// モニタ出力＆デバッグ出力
			for(int i = 0; i < gakkyokuArray.size(); i++) {
				tArea.append( "曲(" + gakkyokuArray.get(i).getGakkyokuId() + ")" +
								gakkyokuArray.get(i).getGakkyokuTitle() + "(" +
								gakkyokuArray.get(i).getArtistId() + ":" +
								gakkyokuArray.get(i).getArtistName() + ")\n");
				// System.out.println(artistArray.get(i).GetArtistId() + artistArray.get(i).GetArtistName());
			}
		}
		cnt = pcCallback.GetCount();

		// 出力
		//tArea.append( pcArtist.getLogStr() + "\n");
		tArea.append( "get cnt = " + cnt + "\n");
		tArea.setCaretPosition( tArea.getText().length() );

		// デバッグ出力
		System.out.println( pcCallback.getLogStr() );
		System.out.println( "get cnt = " + cnt );
		pcCallback.resetLogStr();

		return cnt;
	}



	// 以下は廃止を検討。上部で統合するべき。
	///////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////
	// スクレイピング開始・楽曲

/*
	private void StartScrapingGakkyoku() {
		int currentNum = 0;
		ScrapingParserCallbackGakkyoku pcGakkyoku = new ScrapingParserCallbackGakkyoku();

		tArea.append("\nStartScrapingGakkyoku()!\n");
		tArea.setCaretPosition( tArea.getText().length() );
		System.out.println("StartScrapingGakkyoku()");

		// テストＵＲＬ
		// http://joysound.com/ex/search/artist.htm?artistId=200193

		// URL生成
		String url = "http://joysound.com/ex/search/artist.htm";
		url += "?artistId=" + artistID;		// 200193

		do {
			// 指定URLの楽曲名をすべて抽出する。
			currentNum = ScrapingGakkyokuBody(pcGakkyoku, url, currentNum);
		} while( pcGakkyoku.GetMaxHit() > currentNum  && bProcessing );

		// 完了・出力
		tArea.append("------------------------------\n");
		tArea.append("Scraping Count = " + currentNum + ", Max Hit = " + pcGakkyoku.GetMaxHit() + "\n");
		tArea.append("--- Scraping Done. ---\n");
		tArea.setCaretPosition( tArea.getText().length() );
	}
*/


	// スクレイピング・指定オフセットから取得する。
/*
	public int ScrapingGakkyokuBody(ScrapingParserCallbackGakkyoku pcGakkyoku, String urlstr, int offset) {
		URL url;
		HttpURLConnection httpoc;
		BufferedReader bstr;
		int cnt = offset;

		try {
			// 1.URLオブジェクトの生成　ゆず（Joysound）
			urlstr += String.format( "&offset=%d", offset );
			url = new URL(urlstr);

			// 2.接続オブジェクトの取得・接続
			httpoc = (HttpURLConnection)url.openConnection();
			httpoc.connect();
			bstr = new BufferedReader(new InputStreamReader(httpoc.getInputStream(), "UTF-8"));

			// 解析処理
			ParserDelegator pd = new ParserDelegator();
			pd.parse(bstr, pcGakkyoku, true);

			// クローズ
			bstr.close();
			httpoc.disconnect();

			// 出力
			tArea.append("------------------------------\n");
			tArea.append("url=[" + urlstr + "]\n");
			tArea.append("アーティスト名「" + pcGakkyoku.GetArtistName() + "」\n");

			// データ解析
			ArrayList<Gakkyoku> gakkyokuArray = pcGakkyoku.GetGakkyokuArray();
			tArea.append( "Get data size = " + gakkyokuArray.size() + "\n");

			for(int i = offset; i < gakkyokuArray.size(); i++) {
				tArea.append( "gakkyokuId=" + gakkyokuArray.get(i).GetGakkyokuId() + "," +
						"TITLE=「" + gakkyokuArray.get(i).GetGakkyokuTitle() + "」\n" );
				//System.out.println(gakkyokuArray.get(i).GetGakkyokuId() + gakkyokuArray.get(i).GetGakkyokuName());
			}
			cnt = pcGakkyoku.GetCount();

			//tArea.append( pcGakkyoku.getLogStr() + "\n");
			tArea.append( "cnt = " + cnt + " / " + pcGakkyoku.GetMaxHit() + "\n");
			tArea.setCaretPosition( tArea.getText().length() );

			// デバッグ出力
			System.out.println( pcGakkyoku.getLogStr() );
			System.out.println( "cnt = " + cnt );

		} catch (Exception e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}

		return cnt;
	}
*/

	// スレッド・スリープ
	public void ThreadSleep( int t ) {
		try {
			Thread.sleep( t );
		} catch (InterruptedException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
			System.out.println("Exception! ThreadSleep");
		}
	}

	// 終了通知コールバック登録
	public void registerCallback(ThreadInterface call) {
		this.inter = call;
	}

	// 終了通知コールバック実行
	public void doCallback() {
		if(inter != null) {
			inter.Callback();
		}
	}

	// スレッド停止を要請するＡＰＩ
	public void StopThread() {
		bSuspend = false;
		bProcessing = false;
	}

	// スレッド内処理の一時停止を要請するＡＰＩ
	public void SuspendProcess() {
		bSuspend = bSuspend ? false : true;
	}

	// スレッド内処理が一時停止状態かどうかを調べるＡＰＩ
	public boolean isSuspendProcess() {
		return bSuspend;
	}

	// スクレイピングモード指定（アーティスト検索／楽曲検索）
	public void SetScrapingMode(ScrapingMode m) {
		mode = m;
	}

	// スレッド側で「テキストエリア」に書き込む為に貰う。
	public void SetTextArea(JTextArea ta) {
		tArea = ta;
	}

	// ＤＢ書き込みモードの設定
	public void SetDBWriteMode(boolean b) {
		bDBWriteMode = b;
	}

	// 検索インデックス番号を貰う（あいうえお・・・みたいないのを数値化。）
	public void SetScrapingIndex(int idx) {
		sIndex = idx;
	}
	public void SetScrapingIndex(String str, int idx) {
		sNameIndex = str;
		sIndex = idx;
	}

	// アーティストＩＤをセットする。
	public void SetArtistID(String id) {
		artistID = id;
	}
}
