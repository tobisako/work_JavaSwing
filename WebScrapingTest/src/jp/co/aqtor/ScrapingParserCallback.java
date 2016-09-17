package jp.co.aqtor;

import java.util.ArrayList;
import java.util.Map;

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTML.Tag;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.HTMLEditorKit.ParserCallback;

/*

路線１：アーティストの楽曲順。
<a href="/ex/search/song.htm?gakkyokuId=279091">嗚呼、青春の日々</a>
	問題点：歌のあいうえおインデックスが取得できない。

路線２：曲のあいうえお順の「あ」から順番。
http://joysound.com/ex/search/songsearchindex.htm?searchType=02&searchWordType=1&charIndexKbn=01&charIndex1=101&offset=20
膨大だが、これが確実な気がしてきた。
	まず、楽曲IDが来る。次にアーティストIDが来る。
<a href="/ex/search/song.htm?gakkyokuId=693288">あぁ 世界的 a la mode‐アラモード‐</a>
<a href="/ex/search/artist.htm?artistId=200193">フランス(CV:小野坂昌也)</a>

<span class="hit">29744</span>

*/

// アーティスト構造体
class Artist {
	private String artistId;
	private String artistName;
	private String artistNameIndex;

	public Artist(String id, String name) {
		artistId = id;
		artistName = name;
	}

	public String GetArtistId() {
		return artistId;
	}

	public String GetArtistName() {
		return artistName;
	}
}

// 楽曲構造体
class Gakkyoku {
	//private String artistId;
	private String GakkyokuId;
	private String GakkyokuTitle;
	private String ArtistId;
	private String ArtistName;

	public Gakkyoku(String gakkyoku_id, String title, String artist_id, String artist_name) {
		GakkyokuId = gakkyoku_id;
		GakkyokuTitle = title;
		ArtistId = artist_id;
		ArtistName = artist_name;
	}

	public String getGakkyokuId() {
		return GakkyokuId;
	}

	public String getGakkyokuTitle() {
		return GakkyokuTitle;
	}

	public String getArtistId() {
		return ArtistId;
	}

	public String getArtistName() {
		return ArtistName;
	}
}

// パーサータグENUM
enum ParserTagEnum {
	HIT,
	ARTIST_ARTISTNAME,
	GAKKYOKU_ARTISTNAME,
	GAKKYOKU_TITLE
}

////////////////////////////////////////////////////////////////
//抽出用コールバック（ベース）
public abstract class ScrapingParserCallback extends HTMLEditorKit.ParserCallback {
	protected ParserTagEnum kind;
	protected boolean tagon = false;
	protected int max_hit;
	protected int cnt;
	protected String logstr;
//	private ScrapingMode mode;

	// コンストラクタ
	public ScrapingParserCallback(ScrapingMode m) {
//		mode = m;
		cnt = 0;
		max_hit = 0;
	}

	public String getLogStr() {
		return logstr;
	}

	public void resetLogStr() {
		logstr = "";	// 文字列を初期化する。
	}

	public int GetCount() {
		return cnt;
	}

	public int GetMaxHit() {
		return max_hit;
	}

	// ハンドル・スタートタグ
	public void handleStartTag(HTML.Tag tag,MutableAttributeSet attr, int pos) {
		// ヒット数の取得
		if(tag.equals(HTML.Tag.SPAN)) {
			String src = "" + (String)attr.getAttribute(HTML.Attribute.CLASS);
			if( max_hit == 0 && src.indexOf( "hit" ) != -1 ) {
				tagon = true;
				kind = ParserTagEnum.HIT;
			}
		}
	}

	// ハンドル・タグ内テキストの抽出処理
	public void handleText(char[] data,int pos) {
		String datastr = new String(data);
		if(tagon) {
			switch(kind) {
			case HIT:
				max_hit = Integer.parseInt(datastr);
				break;
			default:
				break;
			}
		}
	}

	// ハンドル・タグ終了チェック
	public void handleEndTag(HTML.Tag tag,int pos) {
		tagon = false;
	}

	///////////////////////////////////////////////////
	// 純粋仮想関数

	// アーティスト・アレイリストの取得
//	public abstract ArrayList<Artist> GetArtistArray();

	// スクレイピング結果のリセット
	public abstract void ResetScraping();

}


////////////////////////////////////////////////////////////////
//アーティスト名の抽出用コールバック
class ScrapingParserCallbackArtist extends ScrapingParserCallback {
	ArrayList<Artist> artistArray;
	private String m_artistId;		// 前回のアーティストID

	// コンストラクタ
	public ScrapingParserCallbackArtist() {
		super(ScrapingMode.ARTIST);
		artistArray = new ArrayList<Artist>();
		m_artistId = "";
	}

	// スクレイピング結果のリセット
	public void ResetScraping() {
		cnt = 0;				// カウンタ初期化
		artistArray.clear();	// 全要素の削除
	}

	// アーティスト・アレイリストの取得
	public ArrayList<Artist> GetArtistArray() {
		return artistArray;
	}

	// ハンドル・スタートタグ
	public void handleStartTag(HTML.Tag tag,MutableAttributeSet attr, int pos) {
		// 親クラスのメソッドを呼び出す
		super.handleStartTag(tag, attr, pos);

		// アーティストID
		if(tag.equals(HTML.Tag.A)) {
			String src = "" + (String)attr.getAttribute(HTML.Attribute.HREF);
			if( src.indexOf("artistId") != -1 ) {
				int p = src.indexOf("?");
				String chk = src.substring(p+1);
				Map<String,String> map = StringUtils.getQueryMap( chk );
				String artistId = map.get("artistId");

				// 新たなアーティストIDが出てきたら、最初の１つのみピックアップする。
				if( !artistId.equals(m_artistId) ) {
					tagon = true;
					kind = ParserTagEnum.ARTIST_ARTISTNAME;
					m_artistId = artistId;
					//str = str + "tagon!(" + chk + ")" + map.get("gakkyokuId") + src + "\n";
				}
			}
		}
	}

	// ハンドル・タグ内テキストの抽出処理
	public void handleText(char[] data,int pos) {
		// 親クラスのメソッドを呼び出す
		super.handleText(data, pos);

		String datastr = new String(data);
		if(tagon) {
			switch(kind) {
			case ARTIST_ARTISTNAME:
				Artist a = new Artist(m_artistId, datastr);
				artistArray.add(a);
				cnt++;
				// debug
				logstr += "<" + cnt + "> " + datastr + "\n";
				break;
			default:
				break;
			}
		}
	}
}


////////////////////////////////////////////////////////////////
//楽曲抽出用コールバック
class ScrapingParserCallbackGakkyoku extends ScrapingParserCallback {
	ArrayList<Gakkyoku> gakkyokuArray;
	private String m_gakkyokuId;	// 楽曲ＩＤ
	private String m_gakkyokuTitle;	// 楽曲タイトル
	private String m_artistId;		// アーティストＩＤ
	private String m_artistName;	// アーティスト名
	private boolean tmpFlg;			// 一時的なフラグ
	private boolean bTitleTagOn;

	// コンストラクタ
	public ScrapingParserCallbackGakkyoku() {
		super(ScrapingMode.GAKKYOKU);
		gakkyokuArray = new ArrayList<Gakkyoku>();
		m_gakkyokuId = "";
		tmpFlg = false;
		bTitleTagOn = false;
	}

	// スクレイピング結果のリセット
	public void ResetScraping() {
		cnt = 0;				// カウンタ初期化
		gakkyokuArray.clear();	// 全要素の削除
	}

	// アーティスト名を取得する
	public String GetArtistName() {
		if(m_artistName == null) {
			return "※見つかりませんですた！";
		}
		return m_artistName;
	}
	// 楽曲配列を抽出
	public ArrayList<Gakkyoku> GetGakkyokuArray() {
		return gakkyokuArray;
	}

	// ハンドル・スタートタグ
	public void handleStartTag(HTML.Tag tag,MutableAttributeSet attr, int pos) {
		// 親クラスのメソッドを呼び出す
		super.handleStartTag(tag, attr, pos);

		// アーティスト名（楽曲検索時に一番上に表示されているアーティスト名）
		if(tag.equals(HTML.Tag.DIV)) {
			String src = "" + (String)attr.getAttribute(HTML.Attribute.CLASS);
			if( src.indexOf("artistNameBlock clearfix") != -1 ) {
				tmpFlg = true;
			}
		}
		if(tag.equals(HTML.Tag.H3)) {
			if(tmpFlg) {
				tagon = true;
				kind = ParserTagEnum.GAKKYOKU_ARTISTNAME;
			}
			tmpFlg = false;
		}

		// 楽曲ＩＤ
		if(tag.equals(HTML.Tag.A)) {
			String src = "" + (String)attr.getAttribute(HTML.Attribute.HREF);
			if( src.indexOf("gakkyokuId") != -1 ) {
				int p = src.indexOf("?");
				String chk = src.substring(p+1);
				Map<String,String> map = StringUtils.getQueryMap( chk );
				String gakkyokuId = map.get("gakkyokuId");
				//logstr += "GAKKYOKU_TITLE...\n";

				// 新たな楽曲ＩＤが出てきたら、最初の１つのみピックアップする。
				if( !gakkyokuId.equals(m_gakkyokuId) ) {
					tagon = true;
					bTitleTagOn = true;
					kind = ParserTagEnum.GAKKYOKU_TITLE;
					m_gakkyokuId = gakkyokuId;
					//logstr += "GAKKYOKU_TITLE tagon!(" + m_gakkyokuId + ")\n";
				}
			}
		}

		// 楽曲のアーティストＩＤ
		if(tag.equals(HTML.Tag.A)) {
			String src = "" + (String)attr.getAttribute(HTML.Attribute.HREF);
			if( src.indexOf("artistId") != -1 ) {
				int p = src.indexOf("?");
				String chk = src.substring(p+1);
				Map<String,String> map = StringUtils.getQueryMap( chk );
				String artistId = map.get("artistId");
				//logstr += "GAKKYOKU_ARTISTNAME...\n";

				// 楽曲に対応したアーティストＩＤが必ず居る。
				if( bTitleTagOn ) {
					tagon = true;
					bTitleTagOn = false;
					kind = ParserTagEnum.GAKKYOKU_ARTISTNAME;
					m_artistId = artistId;
					//logstr += "GAKKYOKU_ARTISTNAME tagon!(" + m_artistId + ")\n";
				}
			}
		}
	}

	// ハンドル・タグ内テキストの抽出処理
	public void handleText(char[] data,int pos) {
		// 親クラスのメソッドを呼び出す
		super.handleText(data, pos);

		String datastr = new String(data);
		if(tagon) {
			switch(kind) {
			case GAKKYOKU_TITLE:
				m_gakkyokuTitle = datastr;
				//bTitleTagOn = true;
				// debug
				//logstr += "GAKKYOKU_TITLE<title> " + datastr + "\n";
				break;
			case GAKKYOKU_ARTISTNAME:
				m_artistName = datastr;
				Gakkyoku a = new Gakkyoku(m_gakkyokuId, m_gakkyokuTitle, m_artistId, m_artistName);
				gakkyokuArray.add(a);
				//bTitleTagOn = false;
				cnt++;
				// debug
				logstr += "GAKKYOKU_ARTISTNAME<" + cnt + "> " + datastr + "\n";
				break;
			default:
				break;
			}
		}
	}
}
