package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

// 部屋にいるクライアントとの通信用スレッド
public class ClientThread extends Thread{
	private String name;
	private Socket incoming;
	private BufferedReader in;
	private PrintWriter out;

	private int playerNum;  // プレイヤーの番号
	private int roomNum; // 部屋番号
	private int charaNum;  // キャラ番号  -1はセットされていない状態
	private int win;  // 勝利した回数

	// コンストラクタ
	ClientThread(String name, Socket s, BufferedReader in, PrintWriter out, int rn, int pn){
		this.name = name;
		this.in= in;
		this.out = out;
		roomNum = rn;
		playerNum = pn;
		incoming = s;
		win = 0;
		charaNum = -1;
	}

	// プレイヤー番号を返す
	public int getPlayerNum(){
		return playerNum;
	}

	// 名前を返す
	public String getClientName(){
		return name;
	}

	// 勝利回数をプラスする
	public void addWin(){
		win++;
	}

	// 勝利回数を返す
	public int getWin(){
		return win;
	}

	// キャラ番号をセットする
	public void setCharaNum(int cn){
		charaNum = cn;
	}

	// キャラ番号を返す
	public int getCharaNum(){
		return charaNum;
	}

	// ソケットを返す
	public Socket getSocket(){
		return incoming;
	}

	// 通信終了時の処理
	public void exitRoom(){
		RoomList.exitRoom(roomNum, playerNum);
	}


	// メッセージ送信
	public synchronized void send(String str){
		out.println(str);
		out.flush();
		System.out.println("Send client No."+playerNum + ", Messages: "+str);
	}

	// クライアントからのメッセージを受信
	public void run() {
		try {
			while(true) {//無限ループで，ソケットへの入力を監視する
				String str = in.readLine();
				System.out.println("RoomThread client No."+playerNum+"("+name+"), Messages: "+str);
				if(str != null) {

					String[] inputTokens = str.split(" ");

					// 通信終了
					if(inputTokens[0].equals("close")) {
						break;
					}

					// 通信メンバー決定中
					else if(inputTokens[0].equals("DECIDEMEMBER")) {
						RoomList.sendAll(roomNum, str);
						RoomList.roomInfoSendAll(roomNum);
						RoomList.sendOne(roomNum, playerNum, "PLAYERNUM "+playerNum); // プレイヤー番号を送る
						RoomList.sendInitChara(roomNum);     // 初期化のためにキャラ情報を送る
					}

					// コメント
					else if(inputTokens[0].equals("COMMENT")){
						RoomList.sendAll(roomNum, str + " " + name); // コメントを送る
						RoomList.roomInfoSendAll(roomNum);
					}

					// アイテムスイッチ画面選択にうつる
					else if(inputTokens[0].equals("SETITEMSWITCH")){
						RoomList.sendItemSwitch(roomNum); // アイテムスイッチ情報を送る
					}

					// ステージセレクト画面に移る
					else if(inputTokens[0].equals("GOSTAGESEL")){
						RoomList.sendAll(roomNum, str);
						RoomList.roomInfoSendAll(roomNum);
					}

					// アイテムスイッチ中
					else if(inputTokens[0].equals("ITEMSWITCH")){
						if(inputTokens[1].equals("DECIDE")){  // 決定キーが押されたとき
							int blockNum = Integer.parseInt(inputTokens[2]);
							int itemNums[] = new int[inputTokens.length-3];
							for(int i=0; i<inputTokens.length-3; i++){
								itemNums[i] = Integer.parseInt(inputTokens[i+3]);
							}
							RoomList.setItemSwitch(roomNum, itemNums); // アイテムスイッチ情報をセットする
							RoomList.setBlockNum(roomNum, blockNum);   // ブロックの数を変更する
							RoomList.sendAll(roomNum, "GOSTAGESEL"); // ステージセレクト画面に移動
							RoomList.roomInfoSendAll(roomNum);
						}
						else if(inputTokens[1].equals("ONE")){  // アイテムスイッチの情報を一つだけかえる
							RoomList.sendAll(roomNum, str);
						}
						else if(inputTokens[1].equals("BLOCK")){  // ブロック情報変更
							RoomList.sendAll(roomNum, str);
						}
					}

					// キャラ選択中
					else if(inputTokens[0].equals("CHARASELECTMOVE")){
						str += " " + playerNum;
						if(inputTokens[1].equals("DECIDE")){  // 決定キーが押されたとき
							int cn = Integer.parseInt(inputTokens[2]);
							RoomList.setCharaNum(roomNum, playerNum, cn);
							RoomList.sendAll(roomNum, str);

							if(RoomList.isDecidedChara(roomNum)){
								str = "CHARASELECTMOVE END";
								RoomList.sendAll(roomNum, str);
							}
						}
						else if(inputTokens[1].equals("CANCEL")){  // キャンセルキーが押されたとき
							RoomList.setCharaNum(roomNum, playerNum, -1);
							RoomList.sendAll(roomNum, str);
						}
						else{   // 十字キーなどが押されたとき
							RoomList.sendAll(roomNum, str);
						}
						RoomList.roomInfoSendAll(roomNum);
					}

					// ステージ選択中
					else if(inputTokens[0].equals("STAGESELECTMOVE")){
						if(inputTokens[1].equals("DECIDE")){
							int sn = Integer.parseInt(inputTokens[2]);

							RoomList.startGame(roomNum, sn);
						}
						else{
							RoomList.sendAll(roomNum, str);
							RoomList.roomInfoSendAll(roomNum);
						}
					}

					// プレイヤーナンバーを送る
					else if(inputTokens[0].equals("PLAYERNUM")){
						str += " " + playerNum;
						RoomList.sendOne(roomNum, playerNum, str);
					}

					// ゲーム中
					else if(inputTokens[0].equals("GAMEMOVE")){

						// 爆弾いた時の処理
						if(inputTokens[1].equals("DECIDE")){
							if(RoomList.setBombCheck(roomNum,playerNum))
								RoomList.setBombInfoSendAll(roomNum, playerNum);
						}
						else{
							// 移動時の処理
							int nloop = Integer.parseInt(inputTokens[2]);
							if(inputTokens[1].equals("UP")){
								if(RoomList.moveChara(roomNum,playerNum,0,-1,1,nloop))
									RoomList.charaInfoSendAll(roomNum, playerNum);
							}
							else if(inputTokens[1].equals("DOWN")){
								if(RoomList.moveChara(roomNum,playerNum,0,1,2,nloop))
									RoomList.charaInfoSendAll(roomNum, playerNum);
							}
							else if(inputTokens[1].equals("RIGHT")){
								if(RoomList.moveChara(roomNum,playerNum,1,0,3,nloop))
									RoomList.charaInfoSendAll(roomNum, playerNum);
							}
							else if(inputTokens[1].equals("LEFT")){
								if(RoomList.moveChara(roomNum,playerNum,-1,0,4,nloop))
									RoomList.charaInfoSendAll(roomNum, playerNum);
							}
						}
						RoomList.roomInfoSendAll(roomNum);
					}
				}
				else break;
			}
			// 部屋から出る処理
			RoomList.exitRoom(roomNum, playerNum);

		} catch(IOException e) {
			//ここにプログラムが到達するときは，接続が切れたとき
			System.out.println("error; " + e);
			RoomList.exitRoom(roomNum, playerNum);
		}
	}
}
