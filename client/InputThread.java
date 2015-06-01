package client;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import stage.Time;
import client.MainFrame.RecieveThread;


//特定のキーが押されたかどうかを判定する
public class InputThread implements Runnable, KeyListener{

	private int keyC[] = {0,0,0,0,0,0,0};
	private boolean key_t[] = {false, false, false, false, false, false, false};   //UP, RIGHT, DOWN, LEFT, Z, SPACE, ENTER
	private boolean isDoing;   // 親プログラムが実行中かどうか
	private int ptype;     // パネルタイプを取得
	private RecieveThread rcvt;    // 受信用スレッド

	// デフォルトコンストラクタ
	InputThread(){
		isDoing = true;
		ptype = 0;
	}

	// どのキーが押されているかを返す
	public boolean[] getKey(){
		return key_t;
	}

	// キーが押されたときの処理
	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_UP: key_t[0] = true; break;  // 上キーが押されたとき
		case KeyEvent.VK_RIGHT: key_t[1] = true; break;  // 右キーが押されたとき
		case KeyEvent.VK_DOWN: key_t[2] = true; break;  // 下キーが押されたとき
		case KeyEvent.VK_LEFT: key_t[3] = true; break;  // 左キーが押されたとき
		case KeyEvent.VK_Z: key_t[4] = true; break;  // Zキーが押されたとき
		case KeyEvent.VK_SPACE: key_t[5] = true; break;  // スペースキーが押されたとき
		case KeyEvent.VK_ENTER: key_t[6] = true; break;  // エンターキーが押されたとき
		default:
		}
	}

	// キーが離されたときの処理
	public void keyReleased(KeyEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_UP: key_t[0] = false; break;  // 上キーが離されたとき
		case KeyEvent.VK_RIGHT: key_t[1] = false; break;  // 右キーが離されたとき
		case KeyEvent.VK_DOWN: key_t[2] = false; break;  // 下キーが離されたとき
		case KeyEvent.VK_LEFT: key_t[3] = false; break;  // 左キーが離されたとき
		case KeyEvent.VK_Z: key_t[4] = false; break;  // Zキーが離されたとき
		case KeyEvent.VK_SPACE: key_t[5] = false; break;  // スペースキーが離されたとき
		case KeyEvent.VK_ENTER: key_t[6] = false; break;  // エンターキーが離されたとき
		default:
		}
	}

	// キーがタイプされたときの処理
	public void keyTyped(KeyEvent e) {}

	public void run(){
		try{

			Time t = new Time();
			while(isDoing){
				t.updateTime();
				actionKey();  // キーの入力状況を調べる
				Thread.sleep(t.getSleepTime()); // 休止
			}
		}
		catch(Exception e){
			System.out.println("入力スレッドでエラーが発生" + e);
		}
	}

	// 画面移動などのキー入力を管理
	public void actionKey(){
		key_t = getKey();
		keyCheck();

		switch(ptype){
		case 1:
			break;

		case 2:
			break;

		case 3:
			break;

		case 4:
			break;

		case 5:  // ステージ選択中の処理
			if(keyC[1] == 1){                 // 右
				//				System.out.println("5: 入力キー: 右");
				rcvt.sendString("STAGESELECTMOVE RIGHT");
			}
			else if(keyC[3] == 1){                 // 左
				//				System.out.println("5: 入力キー: 左");
				rcvt.sendString("STAGESELECTMOVE LEFT");
			}
			else if(keyC[4] == 1){                 // 左
				//				System.out.println("5: 入力キー: Z");
				rcvt.sendStageNum("STAGESELECTMOVE DECIDE");
			}
			else if(keyC[5] == 1){         // SPACE
				//				System.out.println("入力キー: SPACE");
				rcvt.sendString("SETITEMSWITCH");
			}
			break;

		case 6:  // ゲーム中の処理
			rcvt.updatePaint();  // ゲームの時間更新による処理をする

			if(keyC[0] >= 1){                 // 上
				//				System.out.println("入力キー: 上");
				String str = "GAMEMOVE UP 1";
				rcvt.sendString(str);
			}
			else if(keyC[2] >= 1){                 // 下
				//				System.out.println("入力キー: 下");
				String str = "GAMEMOVE DOWN 1";
				rcvt.sendString(str);
			}
			else if(keyC[1] >= 1){                 // 右
				//				System.out.println("入力キー: 右");
				String str = "GAMEMOVE RIGHT 1";
				rcvt.sendString(str);
			}
			else if(keyC[3] >= 1){                 // 左
				//				System.out.println("入力キー: 左");
				String str = "GAMEMOVE LEFT 1";
				rcvt.sendString(str);
			}
			else if(keyC[4] == 1){                 // Z
				//				System.out.println("入力キー: Z");
				rcvt.sendString("GAMEMOVE DECIDE");
			}
			else if(keyC[6] == 1){                 // ENTER
				System.out.println("入力キー: ENTER");
				rcvt.pgStartComment();
			}
			break;

		case 7:  // キャラ選択中の処理
			if(keyC[0] == 1){                 // 上
				//				System.out.println("入力キー: 上");
				rcvt.sendSelectChara("CHARASELECTMOVE UP", -6);
			}
			if(keyC[2] == 1){                 // 下
				//				System.out.println("入力キー: 下");
				rcvt.sendSelectChara("CHARASELECTMOVE DOWN", 6);
			}
			if(keyC[1] == 1){                 // 右
				//				System.out.println("入力キー: 右");
				rcvt.sendSelectChara("CHARASELECTMOVE RIGHT", 1);
			}
			if(keyC[3] == 1){                 // 左
				//				System.out.println("入力キー: 左");
				rcvt.sendSelectChara("CHARASELECTMOVE LEFT", -1);
			}
			if(keyC[4] == 1){                 // Z
				//				System.out.println("入力キー: Z");
				rcvt.sendSelectChara("CHARASELECTMOVE DECIDE", 100);
			}
			if(keyC[5] == 1){                 // SPACE
				// System.out.println("入力キー: SPACE");
				rcvt.sendSelectChara("CHARASELECTMOVE CANCEL", -100);
			}
			break;
		case 8:   // アイテムスイッチでの処理
			break;

		case 9:   // リザルト画面での処理
			if(keyC[6] == 1){                 // ENTER
				System.out.println("入力キー: ENTER");
				rcvt.sendString("GOSTAGESEL");
			}
			break;

		default:
		}
	}

	// どのキーが押されたかをチェック
	public void keyCheck(){
		for(int i=0; i<key_t.length; i++){
			if(key_t[i]) keyC[i]++;
			else keyC[i] = 0;
		}
	}

	// 通信スレッドを設定
	public void setRecieveThread(RecieveThread m){
		rcvt = m;
	}

	// パネルタイプを設定
	public void setPanelType(int t){
		ptype = t;
	}

	// スレッドを終了させるためのフラグセット
	public void setFin(){
		isDoing = false;
	}

}
