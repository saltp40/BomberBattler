package client;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import stage.DrawStageInfo;
import stage.LoadImages;

public class MainFrame extends JFrame{

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private Image back;
	private LoadImages loadImage;

	Dimension size;

	// 表示するパネル
	private Container contentPane;
	private MenuPanel menuPane;
	private MultiPanel multiPane;
	private ConnectPanel connectPane;
	private ConnectPanel2 connectPane2;
	private CharaSelectPanel charaSelPane;
	private StageSelectPanel stageSelPane;
	private PlayGamePanel pgPane;
	private ItemSwitchPanel itemSwitchPane;
	private ResultPanel resultPane;

	private int currentPanel;  // 現在表示しているパネル番号
    private InputThread ipt;

	// 通信用
	private RecieveThread rcvt;
	private boolean connecting;
	PrintWriter out; //出力用のライター
	BufferedReader br; // 受信用のリーダー

	// デフォルトコンストラクタ
	MainFrame(){

		// フレーム仕様の決定
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setTitle("BomberBattler");
		setSize(640, 480);
		setLocationRelativeTo(null);
		setVisible(false);
		setResizable(false);       // サイズの固定
		addWindowListener(new MyWindowListener());   // ウィンドウイベントを監視

		// 初期化
		init();
	}

	// フレームの初期化処理
	public void init(){
		size = getSize();
		contentPane = getContentPane();
		loadImage = new LoadImages();
		loadImage.init();

		currentPanel = 0;
		connecting = false;

		setFocusable(true);

		// 入力スレッドの起動
		ipt = new InputThread();
		addKeyListener(ipt);
		new Thread(ipt).start();
	}

	// ウィンドウが閉じられるときの処理
	class MyWindowListener extends WindowAdapter {
	    public void windowClosing(WindowEvent e) {
	    	try{
	    		ipt.setFin();  // 入力スレッドの終了
	    	}
	    	catch(Exception exe){
	    		System.err.println("入力スレッド終了時にエラー: " + exe);
	    	}

	    	try{
	    		rcvt.finishConnect();  // 通信の終了
	    	}
	    	catch(Exception exe){
	    		System.err.println("ソケット通信はすでに終了しています: " + exe);
	    	}
	    }
	}

	// サーバに接続する
	// 引数; int 部屋を作るがわか、入るがわか
	public boolean connectServer(int type){

		String myIP;
		String myName;
		String myRoom="";

		try {

			//名前の入力ダイアログを開く
			myName = JOptionPane.showInputDialog(null,"名前を入力してください","名前の入力",JOptionPane.QUESTION_MESSAGE);
			if(myName.equals("")){
				myName = "Noname";//名前がないときは，"Noname"とする
			}
			else{  // nameに空白が出ないようにする
				String[] inputTokens = myName.split(" ");
				myName = inputTokens[0];
			}

			//部屋名入力ダイアログを開く
			if(type == 0){
				myRoom = JOptionPane.showInputDialog(null,"部屋の名前を入力してください","名前の入力",JOptionPane.QUESTION_MESSAGE);
				if(myRoom.equals("")){
					myRoom = "Noname";//名前がないときは，"Noname"とする
				}
				else{  // roomに空白が出ないようにする
					String[] inputTokens = myRoom.split(" ");
					myRoom = inputTokens[0];
				}
			}

			myIP = "localhost";

			// サーバへのIPアドレスをファイルから取得
			try{
				BufferedReader br = new BufferedReader(new FileReader("serverIP.ini"));

				/*ファイルを読み込みます。*/
				String line = "";
				if(br!=null && (line = br.readLine()) != null){
					myIP = line;
					myIP.trim();   // 空白が合った場合、空白を削除
					System.out.println(line);
				}
				br.close();
			}catch(IOException e){
				System.err.println("ファイル取得時エラー" + e);
			}

		} catch (Exception e) {
			System.err.println("入力エラー" + e);
			return false;
		}

		//サーバに接続する
		Socket socket = null;
		try {
			//"localhost"は，自分内部への接続．localhostを接続先のIP Address（"133.42.155.201"形式）に設定すると他のPCのサーバと通信できる
			// 50000はポート番号．IP Addressで接続するPCを決めて，ポート番号でそのPC上動作するプログラムを特定する
			socket = new Socket(myIP, 50000);
		} catch (UnknownHostException e) {
			System.err.println("ホストの IP アドレスが判定できません: " + e);
			JOptionPane.showMessageDialog(null, "ホストの IP アドレスが判定できません");
			return false;
		} catch (Exception e) {
			System.err.println("サーバ接続中にエラーが発生しました: " + e);
			JOptionPane.showMessageDialog(null, " サーバ接続中にエラーが発生しました" + e);
			return false;
		}

		rcvt = new RecieveThread(socket, myName, type, myRoom);//受信用のスレッドを作成する
		ipt.setRecieveThread(rcvt);

		return true;
	}

	// メッセージ受信のためのスレッド
	public class RecieveThread extends Thread {

		Socket socket;
		String myName;
		String myRoom;
		int myType;
		InputStreamReader sisr;

		public RecieveThread(Socket s, String n, int t, String mr){
			socket = s;
			myName = n;
			myType = t;
			myRoom = mr;
		}

		//通信状況を監視し，受信データによって動作する
		public void run() {
			try{
				connecting = true;  // サーバと通信しているか
				sisr = new InputStreamReader(socket.getInputStream());
				br = new BufferedReader(sisr);
				out = new PrintWriter(socket.getOutputStream(), true);

				// 最初に送るメッセージ
				if(myType == 0) sendString("NEW " + myName + " " + myRoom);
				else if(myType == 1) sendString("ENTER " + myName + " " + 0);

				while(true) {

					String inputLine = br.readLine();//データを一行分だけ読み込んでみる
					if (inputLine != null) {//読み込んだときにデータが読み込まれたかどうかをチェックする
						System.out.println(inputLine);//デバッグ（動作確認用）にコンソールに出力する

						String[] inputTokens = inputLine.split(" ");	//入力データを判断するために、スペースで切り分ける

						String cmd = inputTokens[0];//コマンドの取り出し．１つ目の要素を取り出す
						if(cmd.equals("CONNECTROOM")){
							//CONNECTの時の処理
							int x = Integer.parseInt(inputTokens[1]);//数値に変換する
							String stName = inputTokens[2];
							connectPane.setLabel(x, stName);
						}

						// 部屋接続時の情報を受け取る
						else if(cmd.equals("ROOMINFO")){
							int x = Integer.parseInt(inputTokens[1]);//数値に変換する
							String stName = inputTokens[2];
							int stN = Integer.parseInt(inputTokens[3]);
							connectPane2.setLabelButton(x, stName, stN);
						}

						// ルームメンバーの情報を受け取る
						else if(cmd.equals("ROOMMEMBER")){
							for(int j=0; j<4; j++){
								int mn = Integer.parseInt(inputTokens[3*j+1]);
								String mname= inputTokens[3*j+2];
								int win = Integer.parseInt(inputTokens[3*j+3]);

								// パネルなどの作成
								if(currentPanel == 5) stageSelPane.setMemberLabel(mn, mname, win);
								else if(currentPanel == 6) pgPane.setMemberLabel(mn, mname);
								else if(currentPanel == 7) charaSelPane.setMember(mn, mname);
							}
						}

						// プレイヤーナンバーを受け取る
						else if(cmd.equals("PLAYERNUM")){
							int playerNum = Integer.parseInt(inputTokens[1]);
							if(currentPanel == 5) pgPane.setPlayerNum(playerNum);
						}

						// コメントを受け取る
						else if(cmd.equals("COMMENT")){
							String comment = inputTokens[1];
							String playerName= inputTokens[2];

							if(currentPanel == 5) stageSelPane.setComment(comment, playerName);
							if(currentPanel == 6) pgPane.setComment(comment, playerName);
						}

						// アイテムスイッチ画面に移動する
						else if(inputTokens[0].equals("SETITEMSWITCH")){
							String bn = inputTokens[1];
							String itemNums[] = new String[inputTokens.length-2];
							for(int i=2; i<inputTokens.length; i++){  // 設定されているアイテムの数を取得
								itemNums[i-2] = inputTokens[i];
							}
							newPanel2(8);
							if(currentPanel == 8) itemSwitchPane.setItemSwitch(itemNums, bn);
						}

						// アイテムスイッチ情報を変更する
						else if(inputTokens[0].equals("ITEMSWITCH")){
							if(inputTokens[1].equals("ONE")){
								int n = Integer.parseInt(inputTokens[2]);
								String itemNum = inputTokens[3];
								if(currentPanel == 8) itemSwitchPane.setOneItemSwitch(n, itemNum);
							}
							else if(inputTokens[1].equals("BLOCK")){
								String bn = inputTokens[2];
								if(currentPanel == 8) itemSwitchPane.setBlockNum(bn);
							}
						}


						// ステージセレクト画面で十字キーが押されたことを受け取る
						else if(cmd.equals("STAGESELECTMOVE")){
							if(inputTokens[1].equals("LEFT"))
								stageSelPane.setStageImage(-1);
							else if(inputTokens[1].equals("RIGHT"))
								stageSelPane.setStageImage(1);
							else if(inputTokens[1].equals("DECIDE")){  // ステージが決まったとき
								int sn = Integer.parseInt(inputTokens[2]);
								long l = Long.parseLong(inputTokens[3]);

								// アイテムスイッチ情報
								int blocknum = Integer.parseInt(inputTokens[4]);
								int itemNums[] = new int[inputTokens.length-5];
								for(int i=0; i<inputTokens.length-5; i++){
									itemNums[i] = Integer.parseInt(inputTokens[i+5]);
								}
								newPanel3(6, l, sn, itemNums, blocknum);
								sendString("PLAYERNUM");
							}
						}

						// キャラクターセレクト画面での処理
						else if(cmd.equals("CHARASELECTMOVE")){
							if(inputTokens[1].equals("END")){
								newPanel2(5);
							}
							else if(inputTokens[1].equals("DECIDE")){
								int cn = Integer.parseInt(inputTokens[2]);
								int n = Integer.parseInt(inputTokens[3]);
								charaSelPane.setMemberChara(n, cn);
								charaSelPane.setIsDecide(n, true);
							}
							else if(inputTokens[1].equals("CANCEL")){
								int n = Integer.parseInt(inputTokens[3]);
								charaSelPane.setIsDecide(n, false);
							}
							else{   // 方向キー移動のとき
								int cn = Integer.parseInt(inputTokens[2]);
								int n = Integer.parseInt(inputTokens[3]);
								charaSelPane.setMemberChara(n, cn);
							}
						}

						// キャラ画像番号を受け取る
						else if(cmd.equals("CHARAIMAGE")){
							for(int j=0; j<4; j++){
								int cn = Integer.parseInt(inputTokens[j+1]);

								// パネルなどの作成
								if(currentPanel == 6) pgPane.setCharaImage(j, cn);
							}
						}

						// キャラクターの位置を受け取る
						else if(cmd.equals("CHARAINFO")){
							for(int j=0; j<4; j++){
								int xp = Integer.parseInt(inputTokens[2*j+1]);
								int yp = Integer.parseInt(inputTokens[2*j+2]);

								// パネルなどの作成
								if(currentPanel == 6) pgPane.setChara(j, xp, yp);
							}
						}

						// キャラクター一人の位置を受け取る
						else if(cmd.equals("ONECHARAINFO")){
							int n = Integer.parseInt(inputTokens[1]);
							int xp = Integer.parseInt(inputTokens[2]);
							int yp = Integer.parseInt(inputTokens[3]);

							// パネルなどの作成
							if(currentPanel == 6) pgPane.setChara(n, xp, yp);
						}

						// キャラクターの状態を受け取る
						else if(cmd.equals("CHARAFLAG")){
							int n = Integer.parseInt(inputTokens[1]);
							int f = Integer.parseInt(inputTokens[2]);
							if(currentPanel == 6) pgPane.setCharaFlag(n, f);
						}

						// ボムが置かれたことを受け取る
						else if(cmd.equals("SETBOMB")){
								int n = Integer.parseInt(inputTokens[1]);
								if(currentPanel == 6) pgPane.setBomb(n);
						}

						// 通信時間を更新する
						else if(cmd.equals("UPDATETIME")){
							int t = Integer.parseInt(inputTokens[1]);
							if(currentPanel == 6) pgPane.updateGame(t);
						}

						// ゲーム終了を受け取る
						else if(cmd.equals("GAMESET")){
							if(inputTokens[1].equals("WINNER")){
								int winnum = Integer.parseInt(inputTokens[2]);
								String winname = inputTokens[3];
								if(currentPanel == 6) newPanel2(9); //リザルト画面へ移動
								if(currentPanel == 9) resultPane.setWinner(winnum+1, winname);
							}
							else if(inputTokens[1].equals("DRAW")){
								if(currentPanel == 6) newPanel2(9);  //リザルト画面へ移動
							}
						}

						// 部屋メンバーが決定したことを受け取る
						else if(cmd.equals("DECIDEMEMBER")){
							newPanel2(7);
						}

						// ステージセレクトにいく
						else if(cmd.equals("GOSTAGESEL")){
							newPanel2(5);
						}

						// エラーメッセージのを受け取る
						else if(cmd.equals("ERROR100")){
							JOptionPane.showMessageDialog(null, "これ以上部屋を作れません。戻るを押してください");
							break;
						}

					}else{
						break;
					}
				}
				finishConnect();

			} catch (IOException e) {
				System.err.println("受信用スレッドでエラーが発生しました: " + e);
			}
		}
		// 通信を終了する
		public void finishConnect(){
			try{
				connecting = false;
				out.println("close");
				socket.close();
				System.out.println("通信終了");
			} catch (IOException e) {
				System.err.println("通信終了時にエラーが発生しました: " + e);
			}
		}

		// 時間経過による処理
		public void updatePaint(){
			if(currentPanel == 6) pgPane.updateDraw();
		}

		// 	自分のキャラ選択状態を送信
		public void sendSelectChara(String str, int t){
			int tmp;
			if(t == -100){ // キャンセルしたとき
				charaSelPane.setScnFlag(false);
				tmp = charaSelPane.getSelectCharaNum();
			}
			else if(t == 100){  // 決定したとき
				charaSelPane.setScnFlag(true);
				tmp = charaSelPane.getSelectCharaNum();
			}
			else{   // 方向キーを押したとき
				tmp = charaSelPane.selectChara(t);
			}

			// キャラが選ばれているときの番号を送る
			if(tmp >= 0)
				sendString(str + " " + tmp);
		}

		// コメントができるように変更
		public void pgStartComment(){
			if(currentPanel == 6) pgPane.startComment();
		}

		// ステージ番号を送信
		public void sendStageNum(String str){
			str += " " + stageSelPane.getStageNum();
			out.println(str);
			out.flush();
		}

		// 送信したい文字列strを送信
		public void sendString(String str){
			out.println(str);
			out.flush();
		}

	}

	// レシーブスレッドを返す
	public RecieveThread getRecieveThread(){
		return rcvt;
	}

	// 表示するパネルを変える関数
	public void newPanel(int t){
		currentPanel = t;

		// パネルの生成
		contentPane.removeAll();

		// 通信終了
		if(connecting){
			rcvt.finishConnect();
		}

		switch(t){
		case 1:    // single画面
			//Pane = new MenuPanel(size, back, initMenu);
			break;
		case 2:    // multi画面
			multiPane = new MultiPanel(size, back);
			contentPane.add(multiPane);
			break;
		case 3:    // 部屋を作る画面
			if(connectServer(0)){
				connectPane = new ConnectPanel(size, back);
				contentPane.add(connectPane);
				rcvt.start();
			}
			else{
				multiPane = new MultiPanel(size, back);
				contentPane.add(multiPane);
				currentPanel = 2;
			}
			break;
		case 4:    // 部屋に入る画面
			if(connectServer(1)){
				connectPane2 = new ConnectPanel2(size, back);
				contentPane.add(connectPane2);
				rcvt.start();
			}
			else{
				multiPane = new MultiPanel(size, back);
				contentPane.add(multiPane);
				currentPanel = 2;
			}
			break;
		default : // メニュー画面
			menuPane = new MenuPanel(size, back);
			contentPane.add(menuPane);
			currentPanel = 0;
		}

		ipt.setPanelType(currentPanel);
		contentPane.validate();

	}

	// オンライン接続中に移動するときに使用
	public void newPanel2(int t){
		currentPanel = t;

		// パネルの生成
		contentPane.removeAll();

		switch(t){
		case 3:    // ConnectPanel
			connectPane = new ConnectPanel(size, back);
			contentPane.add(connectPane);
			break;

		case 5:    // ステージ選択画面
			stageSelPane = new StageSelectPanel(size, back);
			contentPane.add(stageSelPane);
			break;

		case 7:    // キャラ選択画面
			charaSelPane = new CharaSelectPanel(size, back);
			contentPane.add(charaSelPane);
			break;

		case 8:   // アイテムスイッチ画面
			itemSwitchPane = new ItemSwitchPanel(size, back);
			contentPane.add(itemSwitchPane);
			break;

		case 9:   // リザルト画面
			resultPane = new ResultPanel(size, back);
			contentPane.add(resultPane);
			break;

		default:
			System.err.println("パネルチェンジエラー");
		}

		ipt.setPanelType(currentPanel);
		contentPane.validate();
	}

	// 対戦するときに使用するパネルに移動
	public void newPanel3(int t, long l, int sn, int[] itemnums, int blocknum){
		currentPanel = t;

		// パネルの生成
		contentPane.removeAll();

		switch(t){
		case 6:  // ゲームプレイ画面
			DrawStageInfo dsi = new DrawStageInfo();
			dsi.newStage(l, sn, itemnums, blocknum);
			pgPane = new PlayGamePanel(size, back, dsi);
			contentPane.add(pgPane);
			break;

		default:
			System.err.println("パネルチェンジエラー");
		}

		ipt.setPanelType(currentPanel);
		contentPane.validate();
	}

	// フレームを表示する関数
	public void visibleMenu(){
		setVisible(true);

		back = createImage(640, 480);
		if (back==null) System.err.print("createImage Error");

		// パネルの生成
		menuPane = new MenuPanel(size, back);
		getContentPane().add(menuPane);
		contentPane.validate();
	}

	// フレームを非表示にする関数
	public void invisibleMenu(){
		setVisible(false);
	}

	// 画像の描写など
	public void paintComponent(Graphics g){
		super.paintComponents(g);
	}

	// メイン関数
	public static void main(String[] ars){
		new MainFrame().visibleMenu();
	}
}
