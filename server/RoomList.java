package server;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;

// 複数の部屋を管理するクラス
public class RoomList {
	private static final int MAX_ROOM_NUM = 4;
	private static Room[] room = new Room[MAX_ROOM_NUM];  // 部屋情報

	// 新しい部屋を作る
	public static boolean makeRoom(String roomName,
			String name, Socket incoming, BufferedReader in, PrintWriter out){
		int k, roomNum=0;
		for(k=0; k<4; k++){
			if(room[k] == null){
				roomNum=k;
				break;
			}
		}
		if(k != 4){
			room[roomNum] = new Room(roomName, roomNum);
			int pn = room[roomNum].getInsertNum();
			ClientThread c = new ClientThread(name, incoming, in, out, roomNum, pn);
			room[roomNum].add(c);
			return true;
		}
		else return false;
	}

	// 初期化のためにキャラ情報を送る
	public static void sendInitChara(int n){
		room[n].sendInitChara();
	}

	// 部屋情報の取得
	public static Room getRoomInfo(int n){
		return room[n];
	}

	// 部屋メンバーの追加
	public static void addRoomMember(String name, Socket s, BufferedReader in, PrintWriter out, int n){
		int pn = room[n].getInsertNum();
		ClientThread c = new ClientThread(name, s, in, out, n, pn);
		room[n].add(c);
	}

	// 部屋メンバーの削除
	public static void exitRoom(int n, int pn){
		room[n].remove(pn);
		if(room[n].getClientNum() == 0) room[n] = null;
	}

	// 部屋メンバーにメッセージを送る
	public static void sendAll(int n, String str){
		room[n].sendAll(str);
	}

	// 部屋にいる一人にメッセージを送る
	public static void sendOne(int n, int pn, String str){
		room[n].sendOne(pn, str);
	}

	// 部屋メンバーにルーム情報を送る
	public static void roomInfoSendAll(int n){
		room[n].roomInfoSendAll();
	}

	// 部屋メンバーのキャラ画像番号を送る
	public static void charaImageNumSendAll(int n){
		room[n].charaImageNumSendAll();
	}

	// 部屋メンバーのキャラ座標を送る
	public static void charaInfoSendAll(int n){
		room[n].charaInfoSendAll();
	}

	// 部屋メンバー一人のキャラ座標を送る
	public static void charaInfoSendAll(int n, int pn){
		room[n].charaInfoSendAll(pn);
	}

	// ボムを設置したことを送る
	public static void setBombInfoSendAll(int n, int pn){
		room[n].setBombInfoSendAll(pn);
	}

	// 部屋メンバーのキャラ情報をセット
	public static void setCharaNum(int n, int pn, int cn){
		room[n].setCharaNum(pn, cn);
	}

	// 部屋メンバーのキャラ情報が決定したかの判別
	public static boolean isDecidedChara(int n){
		return room[n].isDecidedChara();
	}

	// キャラクターが移動できるかどうか
	public static boolean moveChara(int n, int pn, int x, int y, int dir, int loop){
		return room[n].moveChara(pn, x, y, dir, loop);
	}

	// ボムを設置できるかどうか
	public static boolean setBombCheck(int n, int pn){
		return room[n].setBombCheck(pn);
	}

	// アイテムスイッチを設定
	public static void setItemSwitch(int n, int[] nums){
		room[n].setItemSwitch(nums);
	}

	// アイテムスイッチ情報を送る
	public static void sendItemSwitch(int n){
		room[n].sendItemSwitch();
	}

	// 生成するブロックの数を設定する
	public static void setBlockNum(int n, int bn){
		room[n].setBlockNum(bn);
	}

	// 生成するブロックの数を送る
		public static void sendBlockNum(int n){
			room[n].sendBlockNum();
		}

	// ゲームを開始する
	public static void startGame(int n, int sn){
		room[n].startGame(sn);
	}

}
