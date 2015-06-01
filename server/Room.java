package server;

import java.util.Random;

import stage.Chara;
import stage.Stage;
import stage.Time;

// 部屋に接続しているメンバー情報を格納するクラス
public class Room{
	private final int MAX_NUM = 4;  // 部屋に入れる人の数
	private boolean[] cflag;   // 部屋に接続しているかのフラグ
	private ClientThread[] clientList;  // 部屋に接続している人のリスト
	private GameThread gameThread;  // ゲームプレイ時に使うスレッド
	private String roomName;   // 部屋の名前
	private int roomNum;     // 部屋番号
	private int[] itemNums = {30, 30, 4, 4, 20};   // アイテムスイッチ用
	private int blockNum = 100;  // 生成するブロックの数

	// コンストラクタ
	Room(String name, int num){
		roomName = name;
		roomNum = num;
		cflag = new boolean[MAX_NUM];
		clientList = new ClientThread[MAX_NUM];

		for(int i=0; i<MAX_NUM; i++){
			cflag[i] = false;
		}
	}

	// 初期化のためにキャラ情報を送る
	public void sendInitChara(){
		for(int i=0; i<MAX_NUM; i++){
			if(cflag[i]){
				if(clientList[i].getCharaNum() !=-1){
					String str = "CHARASELECTMOVE DECIDE " + clientList[i].getCharaNum() + " " + i;
					sendAll(str);
				}
			}
		}
	}

	// クライアント情報を追加
	public void add(ClientThread c){
		int pn = c.getPlayerNum();
		clientList[pn] = c;
		cflag[pn] = true;
		c.start();  // スレッドのスタート
		roomConnectSendAll();// 部屋にいるルームメンバー情報の更新
	}

	// クライアント情報を削除
	public void remove(int pn){
		clientList[pn] = null;
		cflag[pn] = false;
	}

	// フラグがfalseの番号を返す
	public int getInsertNum(){
		int i;
		for(i=0; i<MAX_NUM; i++){
			if(!cflag[i]) break;
		}
		if(i==MAX_NUM) return 0;
		else return i;
	}

	// リストに入っている数を返す
	public int getClientNum(){
		int count=0;
		for(int i=0; i<MAX_NUM; i++){
			if(cflag[i]) count++;
		}
		return count;
	}

	// 部屋の名前を返す
	public String getRoomName(){
		return roomName;
	}

	// 部屋番号を返す
	public int getRoomNum(){
		return roomNum;
	}

	// アイテムスイッチを設定
	public void setItemSwitch(int[] nums){
		int sum = 0;
		for(int i=0; i<nums.length; i++) sum += nums[i];
		if(sum <= blockNum) itemNums = nums;  // 設置するアイテム数がブロック数以下なら更新
	}

	// アイテムスイッチ情報を送る
	public void sendItemSwitch(){
		String str = "SETITEMSWITCH ";
		str += blockNum + " ";
		for(int i=0; i<itemNums.length; i++) str += itemNums[i] + " ";
		sendAll(str);
	}

	// ブロック生成数を設定
	public void setBlockNum(int bn){
		int sum = 0;
		for(int i=0; i<itemNums.length; i++) sum += itemNums[i];
		if(sum <= bn) blockNum = bn;  // 設置するブロック数がアイテム数以上なら更新
	}

	// ブロック生成数を送る
	public void sendBlockNum(){
		String str = "ITEMSWITCH BLOCK ";
		str += blockNum;
		sendAll(str);
	}

	// 部屋に接続している人すべてにメッセージを送る
	// 全員にメッセージを送る (引数が送りたいメッセージのみ)
	public void sendAll(String str){
		for(int i=0;i<MAX_NUM;i++){
			if(cflag[i]){
				clientList[i].send(str);
			}
		}
	}

	// str: メッセージ, name: 送り主の名前, number: プレイヤー番号
	public void sendAll(String str, String name, int number){
		for(int i=0; i<MAX_NUM; i++){
			if(cflag[i]){
				str += " " + number + " " + name;
				clientList[i].send(str);
			}
		}
	}

	// 部屋にいる一人にメッセージを送る
	public void sendOne(int pn, String str){
		if(cflag[pn]) clientList[pn].send(str);
	}

	// 部屋にいる人の情報を全員に送信 +++++
	public void roomConnectSendAll(){
		for(int j=0; j<MAX_NUM; j++){
			if(cflag[j]) sendAll("CONNECTROOM", clientList[j].getClientName(), j);
		}
	}

	// 部屋にいる人の情報を全員に送信
	public void roomInfoSendAll(){
		String str="ROOMMEMBER ";

		// 接続している人の情報
		for(int i=0; i<MAX_NUM; i++){
			if(cflag[i] == true){
				str += i + " " + clientList[i].getClientName() + " " + clientList[i].getWin() + " ";
			}
			else str += i + " NULL 0 ";
		}
		sendAll(str);
	}

	// 部屋にいる人のキャラ座標情報を全員に送信
	public void charaInfoSendAll(){
		gameThread.charaInfoSendAll();
	}

	// 一人のキャラ座標情報を全員に送信
	public void charaInfoSendAll(int n){
		gameThread.charaInfoSendAll(n);
	}

	// キャラクターの画像番号を全員に送信
	public void charaImageNumSendAll(){
		String str="CHARAIMAGE ";

		// 接続している人のキャラ情報
		for(int i=0;i<MAX_NUM;i++){
			if(cflag[i] == true){
				str += clientList[i].getCharaNum() + " ";
			}
			else str += "-1 ";
		}
		sendAll(str);
	}

	// ボム設置したことを全員に送信
	public void setBombInfoSendAll(int n){
		gameThread.setBombInfoSendAll(n);
	}

	// キャラが移動できるかどうか
	public boolean moveChara(int pn, int x, int y, int dir, int loop){
		return gameThread.moveChara(pn, x, y, dir, loop);
	}

	// ボムを置けるかどうか
	public boolean setBombCheck(int pn){
		return gameThread.setBombCheck(pn);
	}

	// 部屋にいる人のキャラ情報をセット
	public void setCharaNum(int pn, int cn){
		clientList[pn].setCharaNum(cn);
	}

	// 部屋にいる人のキャラが決定したか
	public boolean isDecidedChara(){
		for(int i=0;i<MAX_NUM;i++){
			if(cflag[i]){
				if(clientList[i].getCharaNum() == -1) return false;
			}
		}
		return true;
	}

	// ゲームを開始する
	public void startGame(int sn){
		gameThread = new GameThread();

		// ステージ生成に必要な情報を送る
		long l = gameThread.createStage(sn);
		String str = "STAGESELECTMOVE DECIDE " + sn + " " + l + " " + blockNum + " ";
		for(int i=0; i<itemNums.length; i++) str += itemNums[i] + " ";
		sendAll(str);

		// キャラ情報を送る
		roomInfoSendAll();
		charaImageNumSendAll();
		gameThread.setCharaPosi();
		charaInfoSendAll();

		// スレッドのスタート
		gameThread.start();
	}


	// ゲーム中の時間処理を行うスレッド
	class GameThread extends Thread{
		int newUpdateTime, updateTime;
		private Chara[] chara; // キャラクター情報
		private Stage stage;    // 使用するステージ情報を格納

		GameThread(){
			chara = new Chara[MAX_NUM];
			for(int i=0; i<MAX_NUM; i++)
				chara[i] = new Chara();
			clearUpdateTime();
		}

		// ゲーム中の時間経過による判断をする
		public void run() {
			try {
				System.out.println("GameThread is successful!");
				Time t = new Time();

				while (true) {
					t.updateTime();
					if(!updateGame()) break;
					Thread.sleep(t.getSleepTime()); // 休止
				}

				// ルームサーバ終了
				System.out.println("END GameThread");

			} catch (Exception e) {
				System.out.println("ERROR2 CONNECTION " + e);
			}
		}

		// 部屋にいる人のキャラ座標情報を全員に送信
		public void charaInfoSendAll(){
			String str="CHARAINFO ";

			// 接続している人のキャラ座標情報
			for(int i=0;i<MAX_NUM;i++){
				if(cflag[i] == true){
					str += chara[i].getXPosition() + " " + chara[i].getYPosition() + " ";
				}
				else str += "0 0 ";
			}
			sendAll(str);
		}

		// 一人のキャラ座標情報を全員に送信
		public void charaInfoSendAll(int n){
			String str="ONECHARAINFO ";

			// 接続している人のキャラ情報
			if(cflag[n]){
				str += n + " " + chara[n].getXPosition() + " " + chara[n].getYPosition();
				sendAll(str);
			}
		}

		// ボム設置したことを全員に送信
		public void setBombInfoSendAll(int n){
			String str="SETBOMB ";

			// 接続している人のキャラ情報
			if(cflag[n]){
				str += n;
				sendAll(str);
			}
		}

		// 部屋にいるキャラの座標を初期化
		public void setCharaPosi(){
			chara[0].setPosition(25,25);
			chara[1].setPosition(13*25,13*25);
			chara[2].setPosition(25,13*25);
			chara[3].setPosition(13*25,25);
		}

		// ステージ情報の初期化
		public long createStage(int sn){
			Random rand = new Random();
			long seed = rand.nextLong();

			stage = new Stage();
			stage.setItemSwitch(itemNums, blockNum); // アイテムスイッチの設定
			stage.newStage(sn, seed);

			return seed;
		}

		// 更新時間の初期化
		public void clearUpdateTime(){
			updateTime = 0;
			newUpdateTime = 0;
		}

		// ボムが置けるかのチェック
		public boolean setBombCheck(int n){
			int x = chara[n].getXPosition();
			int y = chara[n].getYPosition();

			if(stage.isPutBomb(x, y)){
				if(stage.isExBomb(x,y) && chara[n].useExBomb(n)){
					stage.putBomb(x, y);
					return true;
				}
				else if(chara[n].useBomb(n)){
					stage.putBomb(x, y);
					return true;
				}
			}
			return false;
		}

		// キャラクターが移動できるかどうか *ループなし
		public boolean moveChara(int n, int x, int y, int dir){

			int tmpx = x + chara[n].getXPosition();
			int tmpy = y + chara[n].getYPosition();
			if(stage.isMove(tmpx, tmpy, dir)){
				chara[n].setPosition(tmpx, tmpy);
				charaGetItem(n, tmpx, tmpy);
				charaInfoSendAll(n);
				return true;
			}
			else return moveCharaS(n,x,y,dir, false);
		}

		// キャラクターが移動できるかどうか *ループなし
		// ギミックフラグ付き
		public boolean moveChara(int n, int x, int y, int dir, boolean f){

			int tmpx = x + chara[n].getXPosition();
			int tmpy = y + chara[n].getYPosition();
			if(stage.isMove(tmpx, tmpy, dir)){
				chara[n].setPosition(tmpx, tmpy);
				charaGetItem(n, tmpx, tmpy);
				charaInfoSendAll(n);
				return true;
			}
			else return moveCharaS(n,x,y,dir, f);
		}


		// キャラクターが移動できるかどうか
		public boolean moveChara(int n, int x, int y, int dir, int nloop){

			nloop += chara[n].getSpeed(); // スピード調整

			int count=0;
			while(nloop > count){
				int tmpx = x + chara[n].getXPosition();
				int tmpy = y + chara[n].getYPosition();
				if(stage.isMove(tmpx, tmpy, dir)){
					chara[n].setPosition(tmpx, tmpy);
					charaGetItem(n, tmpx, tmpy);
					count++;
					charaInfoSendAll(n);
				}
				else{
					return moveCharaS(n,x,y,dir, false);
				}
			}
			return true;
		}

		// 少し他に移動すれば目的の移動ができるか
		public boolean moveCharaS(int n, int x, int y, int dir, boolean isGimmick){
			int nloop = 9 + chara[n].getSpeed()*3;  // 9pixel+α分だけ配慮
			if(isGimmick) nloop = 6;   // ギミックで移動のとき

			switch(dir){
			case 1:    // 上移動
			case 2:    // 下移動
				// 右半分
				int slidex = 1;
				int tmpy = chara[n].getYPosition();

				while(nloop > slidex){
					int tmpx = slidex + chara[n].getXPosition();
					if(stage.isMove(tmpx, tmpy, 3)){
						if(stage.isMove(tmpx, tmpy+y, dir)){
							chara[n].setPosition(tmpx, tmpy+y);
							charaGetItem(n, tmpx, tmpy+y);
							return true;
						}
						slidex++;
					}
					else break;
				}

				// 左半分
				slidex = 1;
				tmpy = chara[n].getYPosition();

				while(nloop > slidex){
					int tmpx = - slidex + chara[n].getXPosition();
					if(stage.isMove(tmpx, tmpy, 4)){
						if(stage.isMove(tmpx, tmpy+y, dir)){
							chara[n].setPosition(tmpx, tmpy+y);
							charaGetItem(n, tmpx, tmpy+y);
							return true;
						}
						slidex++;
					}
					else break;
				}
				break;
			case 3: // 右移動
			case 4: // 左移動
				// 上半分
				int slidey = 1;
				int tmpx = chara[n].getXPosition();

				while(nloop > slidey){
					tmpy = - slidey + chara[n].getYPosition();
					if(stage.isMove(tmpx, tmpy, 1)){
						if(stage.isMove(tmpx+x, tmpy, dir)){
							chara[n].setPosition(tmpx+x, tmpy);
							charaGetItem(n, tmpx+x, tmpy);
							return true;
						}
						slidey++;
					}
					else break;
				}

				// 下半分
				slidey = 1;
				tmpx = chara[n].getXPosition();

				while(nloop > slidey){
					tmpy = slidey + chara[n].getYPosition();
					if(stage.isMove(tmpx, tmpy, 2)){
						if(stage.isMove(tmpx+x, tmpy, dir)){
							chara[n].setPosition(tmpx+x, tmpy);
							charaGetItem(n, tmpx+x, tmpy);
							return true;
						}
						slidey++;
					}
					else break;
				}
				break;

			default:
			}
			return false;
		}

		// ステージギミック処理
		public void doStageGimmick(){
			for(int i=0; i<MAX_NUM; i++){
				int x = chara[i].getXPosition();
				int y = chara[i].getYPosition();

				switch(stage.isGimmick(x,y)){

				// 1~4までは移動床の処理
				case 1: // 左
					moveChara(i, -1, 0, 4, true);
					break;
				case 2: // 右
					moveChara(i, 1, 0, 3, true);
					break;
				case 3: // 下
					moveChara(i, 0, 1, 2, true);
					break;
				case 4: // 上
					moveChara(i, 0, -1, 1, true);
					break;
				default:
				}
			}
		}

		// ボムの更新
		public void updateBomb(int ts){

			// ボム爆発時間更新
			for(int i=0; i<MAX_NUM; i++){
				for(int j=0; j<chara[i].getBombCurrent(); j++){
					if(!chara[i].minusBombTime(j, ts)){
						int[] inf = stage.doExplosion(chara[i].getBombX(j),
								chara[i].getBombY(j), chara[i].getSetPower(j));
						if(inf != null){
							for(int k=0; k<inf.length; k+=2){

								// ボムを置いた持ち主を検索
								for(int l=i; l<MAX_NUM; l++)
									for(int m=0; m<chara[l].getBombCurrent(); m++)
										if(inf[k]==chara[l].getBombX(m) && inf[k+1]==chara[i].getBombY(m)){
											chara[l].doBombChain(m);
											break;
										}
							}
						}
					}
				}
			}

			// ボムの更新
			for(int i=0; i<MAX_NUM; i++){
				chara[i].updateBomb();
			}
		}

		// 更新経過時間を送信
		public void updateTimeSendAll(){
			String str="UPDATETIME ";

			// 6カウント進める
			str += 6;
			sendAll(str);

			clearUpdateTime();
		}

		// アイテムをゲットしたかの判定
		public void charaGetItem(int n, int x, int y){
			int itn = stage.getItem(x, y);
			if(itn == 0) return;

			chara[n].setItem(itn);
		}

		// ゲームの更新
		// ゲーム中ならばtrue, それ以外はfalseを返す
		public boolean updateGame(){

			// ゲームの終了判定
			int count=0, winner=-1;
			for(int j=0; j<MAX_NUM; j++){
				if(cflag[j] == true){
					if(chara[j].getStatus() != -1){
						count++;
						winner = j;
					}
				}
			}
			if(count==1){
				sendAll("GAMESET WINNER " +winner+ " " + clientList[winner].getClientName());
				clientList[winner].addWin();
				roomInfoSendAll();
				return false;
			}
			else if(count==0){
				sendAll("GAMESET DRAW");
				roomInfoSendAll();
				return false;
			}

			// ボム情報の更新
			newUpdateTime++;
			if(newUpdateTime > 9){
				updateTimeSendAll();

				updateBomb(6);
				stage.updateStage(6);
			}

			// ステージギミックの処理
			if(newUpdateTime%5 == 0){
				doStageGimmick();
			}

			// キャラクターの状態判定
			for(int j=0; j<MAX_NUM; j++){
				if(cflag[j] == true){
					int x = chara[j].getXPosition();
					int y = chara[j].getYPosition();

					// 爆発したか
					if(stage.isExplosion(x, y)){
						chara[j].setStatus(-1);
						sendAll("CHARAFLAG " + j + " -1");
					}
				}
			}

			return true;
		}
	}


}
