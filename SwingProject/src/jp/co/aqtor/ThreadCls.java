package jp.co.aqtor;

// http://java2005.cis.k.hosei.ac.jp/materials/lecture26/multithreaded.html

// Thread インスタンスは一度しか start() メソッドを呼び出すことができない
// ことに注意する必要がある。例えば、上記の例で "ほげ" という文字列を 20 回表示したい場合に、
// 次のように書いてはならない。

public class ThreadCls extends Thread {
	public void run() {
		System.out.println("<<<run-start>>>");
		System.out.println("<<<run-end>>>");
	}
}
