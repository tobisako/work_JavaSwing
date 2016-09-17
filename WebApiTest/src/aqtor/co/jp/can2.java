package aqtor.co.jp;

import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;

public class can2 extends Canvas {

	public int h;
	public can2() {
		// TODO 自動生成されたコンストラクター・スタブ
		h = 0;
	}

	public can2(GraphicsConfiguration paramGraphicsConfiguration) {
		super(paramGraphicsConfiguration);
		// TODO 自動生成されたコンストラクター・スタブ
	}

	public void paint(Graphics g) {
		h++;
	}

	public int getH() {
		return h;
	}
}
