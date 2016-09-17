package jp.co.aqtor;

import java.util.ArrayList;
import java.util.Map;

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTML.Tag;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.HTMLEditorKit.ParserCallback;

/*

�H���P�F�A�[�e�B�X�g�̊y�ȏ��B
<a href="/ex/search/song.htm?gakkyokuId=279091">�j�āA�t�̓��X</a>
	���_�F�̂̂����������C���f�b�N�X���擾�ł��Ȃ��B

�H���Q�F�Ȃ̂������������́u���v���珇�ԁB
http://joysound.com/ex/search/songsearchindex.htm?searchType=02&searchWordType=1&charIndexKbn=01&charIndex1=101&offset=20
�c�傾���A���ꂪ�m���ȋC�����Ă����B
	�܂��A�y��ID������B���ɃA�[�e�B�X�gID������B
<a href="/ex/search/song.htm?gakkyokuId=693288">���� ���E�I a la mode�]�A�����[�h�]</a>
<a href="/ex/search/artist.htm?artistId=200193">�t�����X(CV:����⏹��)</a>

<span class="hit">29744</span>

*/

// �A�[�e�B�X�g�\����
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

// �y�ȍ\����
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

// �p�[�T�[�^�OENUM
enum ParserTagEnum {
	HIT,
	ARTIST_ARTISTNAME,
	GAKKYOKU_ARTISTNAME,
	GAKKYOKU_TITLE
}

////////////////////////////////////////////////////////////////
//���o�p�R�[���o�b�N�i�x�[�X�j
public abstract class ScrapingParserCallback extends HTMLEditorKit.ParserCallback {
	protected ParserTagEnum kind;
	protected boolean tagon = false;
	protected int max_hit;
	protected int cnt;
	protected String logstr;
//	private ScrapingMode mode;

	// �R���X�g���N�^
	public ScrapingParserCallback(ScrapingMode m) {
//		mode = m;
		cnt = 0;
		max_hit = 0;
	}

	public String getLogStr() {
		return logstr;
	}

	public void resetLogStr() {
		logstr = "";	// �����������������B
	}

	public int GetCount() {
		return cnt;
	}

	public int GetMaxHit() {
		return max_hit;
	}

	// �n���h���E�X�^�[�g�^�O
	public void handleStartTag(HTML.Tag tag,MutableAttributeSet attr, int pos) {
		// �q�b�g���̎擾
		if(tag.equals(HTML.Tag.SPAN)) {
			String src = "" + (String)attr.getAttribute(HTML.Attribute.CLASS);
			if( max_hit == 0 && src.indexOf( "hit" ) != -1 ) {
				tagon = true;
				kind = ParserTagEnum.HIT;
			}
		}
	}

	// �n���h���E�^�O���e�L�X�g�̒��o����
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

	// �n���h���E�^�O�I���`�F�b�N
	public void handleEndTag(HTML.Tag tag,int pos) {
		tagon = false;
	}

	///////////////////////////////////////////////////
	// �������z�֐�

	// �A�[�e�B�X�g�E�A���C���X�g�̎擾
//	public abstract ArrayList<Artist> GetArtistArray();

	// �X�N���C�s���O���ʂ̃��Z�b�g
	public abstract void ResetScraping();

}


////////////////////////////////////////////////////////////////
//�A�[�e�B�X�g���̒��o�p�R�[���o�b�N
class ScrapingParserCallbackArtist extends ScrapingParserCallback {
	ArrayList<Artist> artistArray;
	private String m_artistId;		// �O��̃A�[�e�B�X�gID

	// �R���X�g���N�^
	public ScrapingParserCallbackArtist() {
		super(ScrapingMode.ARTIST);
		artistArray = new ArrayList<Artist>();
		m_artistId = "";
	}

	// �X�N���C�s���O���ʂ̃��Z�b�g
	public void ResetScraping() {
		cnt = 0;				// �J�E���^������
		artistArray.clear();	// �S�v�f�̍폜
	}

	// �A�[�e�B�X�g�E�A���C���X�g�̎擾
	public ArrayList<Artist> GetArtistArray() {
		return artistArray;
	}

	// �n���h���E�X�^�[�g�^�O
	public void handleStartTag(HTML.Tag tag,MutableAttributeSet attr, int pos) {
		// �e�N���X�̃��\�b�h���Ăяo��
		super.handleStartTag(tag, attr, pos);

		// �A�[�e�B�X�gID
		if(tag.equals(HTML.Tag.A)) {
			String src = "" + (String)attr.getAttribute(HTML.Attribute.HREF);
			if( src.indexOf("artistId") != -1 ) {
				int p = src.indexOf("?");
				String chk = src.substring(p+1);
				Map<String,String> map = StringUtils.getQueryMap( chk );
				String artistId = map.get("artistId");

				// �V���ȃA�[�e�B�X�gID���o�Ă�����A�ŏ��̂P�̂݃s�b�N�A�b�v����B
				if( !artistId.equals(m_artistId) ) {
					tagon = true;
					kind = ParserTagEnum.ARTIST_ARTISTNAME;
					m_artistId = artistId;
					//str = str + "tagon!(" + chk + ")" + map.get("gakkyokuId") + src + "\n";
				}
			}
		}
	}

	// �n���h���E�^�O���e�L�X�g�̒��o����
	public void handleText(char[] data,int pos) {
		// �e�N���X�̃��\�b�h���Ăяo��
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
//�y�Ȓ��o�p�R�[���o�b�N
class ScrapingParserCallbackGakkyoku extends ScrapingParserCallback {
	ArrayList<Gakkyoku> gakkyokuArray;
	private String m_gakkyokuId;	// �y�Ȃh�c
	private String m_gakkyokuTitle;	// �y�ȃ^�C�g��
	private String m_artistId;		// �A�[�e�B�X�g�h�c
	private String m_artistName;	// �A�[�e�B�X�g��
	private boolean tmpFlg;			// �ꎞ�I�ȃt���O
	private boolean bTitleTagOn;

	// �R���X�g���N�^
	public ScrapingParserCallbackGakkyoku() {
		super(ScrapingMode.GAKKYOKU);
		gakkyokuArray = new ArrayList<Gakkyoku>();
		m_gakkyokuId = "";
		tmpFlg = false;
		bTitleTagOn = false;
	}

	// �X�N���C�s���O���ʂ̃��Z�b�g
	public void ResetScraping() {
		cnt = 0;				// �J�E���^������
		gakkyokuArray.clear();	// �S�v�f�̍폜
	}

	// �A�[�e�B�X�g�����擾����
	public String GetArtistName() {
		if(m_artistName == null) {
			return "��������܂���ł����I";
		}
		return m_artistName;
	}
	// �y�Ȕz��𒊏o
	public ArrayList<Gakkyoku> GetGakkyokuArray() {
		return gakkyokuArray;
	}

	// �n���h���E�X�^�[�g�^�O
	public void handleStartTag(HTML.Tag tag,MutableAttributeSet attr, int pos) {
		// �e�N���X�̃��\�b�h���Ăяo��
		super.handleStartTag(tag, attr, pos);

		// �A�[�e�B�X�g���i�y�Ȍ������Ɉ�ԏ�ɕ\������Ă���A�[�e�B�X�g���j
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

		// �y�Ȃh�c
		if(tag.equals(HTML.Tag.A)) {
			String src = "" + (String)attr.getAttribute(HTML.Attribute.HREF);
			if( src.indexOf("gakkyokuId") != -1 ) {
				int p = src.indexOf("?");
				String chk = src.substring(p+1);
				Map<String,String> map = StringUtils.getQueryMap( chk );
				String gakkyokuId = map.get("gakkyokuId");
				//logstr += "GAKKYOKU_TITLE...\n";

				// �V���Ȋy�Ȃh�c���o�Ă�����A�ŏ��̂P�̂݃s�b�N�A�b�v����B
				if( !gakkyokuId.equals(m_gakkyokuId) ) {
					tagon = true;
					bTitleTagOn = true;
					kind = ParserTagEnum.GAKKYOKU_TITLE;
					m_gakkyokuId = gakkyokuId;
					//logstr += "GAKKYOKU_TITLE tagon!(" + m_gakkyokuId + ")\n";
				}
			}
		}

		// �y�Ȃ̃A�[�e�B�X�g�h�c
		if(tag.equals(HTML.Tag.A)) {
			String src = "" + (String)attr.getAttribute(HTML.Attribute.HREF);
			if( src.indexOf("artistId") != -1 ) {
				int p = src.indexOf("?");
				String chk = src.substring(p+1);
				Map<String,String> map = StringUtils.getQueryMap( chk );
				String artistId = map.get("artistId");
				//logstr += "GAKKYOKU_ARTISTNAME...\n";

				// �y�ȂɑΉ������A�[�e�B�X�g�h�c���K������B
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

	// �n���h���E�^�O���e�L�X�g�̒��o����
	public void handleText(char[] data,int pos) {
		// �e�N���X�̃��\�b�h���Ăяo��
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
