package jp.co.aqtor;

import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

/**
 *	ベースクラス：１枚の巨大な画像からキャラクターを切り出して使えますクラス
 */
public class JCharUnitLabel extends JLabel {
	private final int	DIRECTION_DOWN	= 0;	// 下向き
	private final int	DIRECTION_LEFT	= 1;	// 左向き
	private final int	DIRECTION_RIGHT	= 2;	// 右向き
	private final int	DIRECTION_UP	= 3;	// 上向き
	private BufferedImage	bSource;
	private ImageIcon[][]	bImgIcons;
	private String			filename;
	private int				chara_org_width;		// １キャラのオリジナル横幅
	private int				chara_org_height;		// １キャラのオリジナル縦幅
	private int				chara_org_x_num;		// キャラのヨコ数（モーションを除く）
	private int				chara_org_y_num;		// キャラのタテ数（モーションを除く）
	private float			chara_zoom;				// 表示倍率
	private int				chara_disp_width;		// キャラの表示横幅
	private int				chara_disp_height;		// キャラの表示横幅
	private int				chara_motion_max_num;	// キャラが持つモーション数
	private int				chara_motion;			// 現在のモーション
	private int				chara_motion_v;			// モーション移動方向
	private int				chara_direction;		// キャラクタの「向き」
	private int				chara_choice;			// どのキャラを選んだのか？

	/**
	 * コンストラクタ
	 * @param file_str
	 * @param org_width
	 * @param org_height
	 * @param motion_num
	 * @param zoom
	 */
	public JCharUnitLabel( String file_str, int org_width, int org_height,
			int org_x_num, int org_y_num, int motion_max_num, int choice, float zoom ) {
		// 基本情報
		filename = file_str;
		chara_org_width = org_width;
		chara_org_height = org_height;
		chara_motion_max_num = motion_max_num;
		chara_choice = choice;
		chara_zoom = zoom;
		chara_org_x_num = org_x_num;
		chara_org_y_num = org_y_num;
		chara_disp_width = (int)(chara_org_width * chara_zoom);
		chara_disp_height = (int)(chara_org_height * chara_zoom);

		// 初期化情報
		chara_motion = 1;
		chara_motion_v = 1;
		chara_direction = 0;

		// イメージ初期化
		LoadImageData();

		// キャラクターのロード
		SelectChara( chara_choice );
	}

	/**
	 * キャラを選択する
	 * @param num
	 */
	public void SelectChara( int num ) {
		// キャライメージグループのロード
		LoadCharaImageGroup( num );
		// キャラクターのロード
		this.setIcon( bImgIcons[chara_direction][chara_motion] );
	}

	/**
	 * １キャラ分の上下左右アニメーションイメージを全てロードする
	 */
	private void LoadCharaImageGroup( int num ) {
		bImgIcons = new ImageIcon[4][chara_motion_max_num];
		for( int i = 0; i < 4; i++ ) {
			for( int j = 0; j < 3; j++ ) {
				bImgIcons[i][j] = GetImageParts( ChangeNoToPos( num, i, j ) );
			}
		}
	}

	/**
	 * 番号から座標を返す
	 * @param input_no
	 * @return p
	 */
	private Point ChangeNoToPos( int input_no, int direction, int motion ) {
		Point p = new Point();
		p.y = input_no / chara_org_x_num;
		p.x = input_no % chara_org_x_num;
		// モーション考慮
		p.x *= chara_motion_max_num;
		p.x += motion;
		// 「向き」考慮
		p.y *= 4;			// 上下左右の４つ。
		p.y += direction;
		return p;
	}

	/**
	 * 巨大イメージをロードする処理
	 **/
	public void LoadImageData() {
		File ff = new File( filename );
		try {
			bSource =ImageIO.read( ff );
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}

	/**
	 *	巨大イメージから「１つのパーツ」を切り出す。
	 * @param pos_x
	 * @param pos_y
	 * @param direction
	 * @param motion
	 * @return
	 */
	public ImageIcon GetImageParts( Point p ) {
		if( bSource == null ) {
			throw new IllegalStateException("bSource=" + bSource);
		}
		BufferedImage bTmp;
		bTmp = bSource.getSubimage( chara_org_width * p.x,  chara_org_height * p.y, chara_org_width, chara_org_height );
		Image im = bTmp.getScaledInstance( chara_disp_width, chara_disp_height, Image.SCALE_DEFAULT );
		return new ImageIcon( im );
	}

	// GetImageIcon
//	public ImageIcon GetImageIcon( int num ) {
//	return bImg[num];
//	}

	public void setPosition( int x, int y ) {
		super.setBounds(x, y, chara_disp_width, chara_disp_height);
	}

	/**
	 * 	キャラの方向をチェンジ
	 * @param dir	方向
	 */
	public void ChangeDirection( int dir ) {
		chara_direction = dir;
		// キャラクターのロード
		this.setIcon( bImgIcons[chara_direction][chara_motion] );
	}

	/**
	 * キャラのモーションを変更（動かす）0→1→2→1→0→1→2→1・・・。
	 */
	public void ChangeMotion() {
		chara_motion += chara_motion_v;
		if( chara_motion >= chara_motion_max_num - 1 ) chara_motion_v = -1;
		if( chara_motion == 0 ) chara_motion_v = 1;
	}
}

class JPersonUnitLabel extends JCharUnitLabel {
	public JPersonUnitLabel( int choice ) {
		super( "res/vx_chara02_a_transparent.png", 32, 48, 4, 2, 3, choice, 2 );
	}
}

class JMapUnitLabel extends JCharUnitLabel {

	public JMapUnitLabel(int choice) {
		super( "res/TileA5.png", 32, 32, 8, 16, 1, choice, 2 );
	}
}


