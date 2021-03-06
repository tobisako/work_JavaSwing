# work_JavaSwing
Ｊａｖａ Swingフレームワークを使った、Windowsアプリのサンプルです。Swingは、Androidと構造がとても良く似ているので、UI開発前に学習すると良いかもです。  

# 環境
・Eclipse 4.2 Juno  
・Swing Designer（Eclipseプラグイン）  
・Java

Swingを使う為に、Eclipseにソフトウェアを追加インストールして下さい。
＃Swing環境の作り方は、ドキュメントに纏め、リポジトリに入れてています。
＃（【飛峪】Javaウィンドウズアプリ(eclipse_Swing)_環境設定手順書.doc)

# サンプル紹介
Eclipseのプロジェクトを数本入れています。インポートしてお使い下さい。  

**WebApiTest**  
WebAPI(Twitter)を使用し、つぶやいたり、ツイート一覧を取得するクライアント・サンプルです。  
![ss](./_thumbnail/WebApiTest_ss_1.jpg)  
＃ライブラリtwitter4jを使用しています。  

**WebScrapingTest**  
カラオケサイト（JOYSOUND）にWebアクセスし、取得したデータをスクレイピング・抽出するサンプルです。  
このツールで、アーティスト名称データ（漢字・フリガナ）を５万件以上抽出しました。  
![ss](./_thumbnail/WebScrapingTest_ss_1.jpg)  
＃MySQLConnectorライブラリが必要です。  

**SwingProject**  
２Ｄキャラのデータは、だいたい以下の様な「１枚もの」の画像データに収められています。  
![s1](./_thumbnail/SwingProject_ss_4_small.jpg)  
このサンプルは、画像データから１人分を切り出し、キャラクターとして歩かせる（アニメーションさせる）プログラムです。  

キャラその１  
![s1](./_thumbnail/SwingProject_ss_1.jpg)  
キャラその２  
![s1](./_thumbnail/SwingProject_ss_2.jpg)  
キャラその３  
![s1](./_thumbnail/SwingProject_ss_3.jpg)  
こんな感じです。  
マウスをぐりぐり動かして、歩かせて楽しみましょう。（この画像は、無料素材を使用しています。）  

300msタイマーで歩かせるアニメーション処理を入れています。マウスのボタンで、キャラの向き変更、キャラの種類変更が行えます。本プログラム実行時は、「SwingTest01.java」を起動して下さい。
他にも、「ThreadTest.java」を起動すると、スレッド起動実験が出来たり、
「SwingTest02.java」を起動すると、タイマーテストが出来たりします。  

**SwingKeyTest**  
マウスでは無く、キーボードからのイベントを受け付けるテストです。  
カーソルキーを押すと、  
![ss1](./_thumbnail/SwingKeyTest_ss_1.jpg)  
　　↓  
![ss2](./_thumbnail/SwingKeyTest_ss_2.jpg)  
こんな風に、[LABEL]というラベルが移動します。（お好みで、好きな画像に差し替えて下さい。）  

また、ログ出力で、キーボードのキーコードを調べる事も出来ます。
![ss3](./_thumbnail/SwingKeyTest_ss_3.jpg)  
これで、好きなキー入力処理を記述できます。  
詳しくは、「KeyListenerCls.java」を確認して下さい。  

**SwingDesiignTestProject**  
今iが1つ多い事に気が付きました（恥）  
ArrayList（）の動作確認プログラムです。「PUT」ボタンを押すとラベルが増えて、「MOVE」ボタンを押すと、全てのラベルが一斉に右に移動します。  
![ss1](./_thumbnail/SwingDesignTest_ss_1.jpg)  
動的に生成するラベルをArrayList()に追加し、移動させる際はリスト内の全ラベルに対して座標変更を行っています。  
動的配列にクラス・インスタンスを格納する例として見て下さい。  

(2016/9)
