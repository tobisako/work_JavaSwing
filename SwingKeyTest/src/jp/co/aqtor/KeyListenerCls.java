package jp.co.aqtor;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyListenerCls implements KeyListener {
	private KeyTestForm oya;

	public KeyListenerCls(KeyTestForm ptr) {
		oya = ptr;
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO 自動生成されたメソッド・スタブ
		System.out.println("タイプ！" + e.getKeyCode());
	}

	@Override
	public void keyPressed(KeyEvent e) {
		int vx = 0, vy = 0;
		// TODO 自動生成されたメソッド・スタブ
		System.out.println("押した！" + e.getKeyCode());
		// キー入力判定
		switch( e.getKeyCode() ) {
		case 37:	// 左
			vx = -10;	vy = 0;
			break;
		case 38:	// 上
			vx = 0;	vy = -10;
			break;
		case 39:	// 右
			vx = 10;	vy = 0;
			break;
		case 40:	// 下
			vx = 0;	vy = 10;
			break;
		}
		oya.MoveLabel(vx, vy);
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO 自動生成されたメソッド・スタブ
		System.out.println("離した！" + e.getKeyCode());
	}
}

