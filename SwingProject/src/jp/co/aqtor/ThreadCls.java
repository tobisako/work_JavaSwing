package jp.co.aqtor;

// http://java2005.cis.k.hosei.ac.jp/materials/lecture26/multithreaded.html

// Thread �C���X�^���X�͈�x���� start() ���\�b�h���Ăяo�����Ƃ��ł��Ȃ�
// ���Ƃɒ��ӂ���K�v������B�Ⴆ�΁A��L�̗�� "�ق�" �Ƃ���������� 20 ��\���������ꍇ�ɁA
// ���̂悤�ɏ����Ă͂Ȃ�Ȃ��B

public class ThreadCls extends Thread {
	public void run() {
		System.out.println("<<<run-start>>>");
		System.out.println("<<<run-end>>>");
	}
}
