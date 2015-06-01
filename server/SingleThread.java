package server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

// ルーム選択時のスレッド
public class SingleThread extends Thread{
	private Socket incoming;   //受付用のソケット
	private BufferedReader in;   //バッファリングをによりテキスト読み込み用の配列
	private PrintWriter out;   //出力ストリーム用の配列
	private String name;    // 接続しているメンバーの名前

	SingleThread(String name, Socket i) throws Exception{
		//必要な配列を確保する
		this.name = name;
		incoming = i;

		//必要な入出力ストリームを作成する
		in = new BufferedReader(new InputStreamReader(incoming.getInputStream()));
		out = new PrintWriter(incoming.getOutputStream(), true);
	}

	public void run() {
		try {
			System.out.println("Connecting thread is successful!");
			getRoomInfo();
			while (true) {//無限ループで，ソケットへの入力を監視する

				String str = in.readLine();
				if (str != null) {
					String[] inputTokens = str.split(" ");

					// 部屋情報の更新
					if (inputTokens[0].equals("UPDATEINFO")) {
						getRoomInfo();
					}

					// 部屋に入る
					else if(inputTokens[0].equals("ENTERROOM")){
						int n = Integer.parseInt(inputTokens[1]);
						setInfo(n);
						break;
					}
				}
				else break;
			}
		} catch (Exception e) {
			//ここにプログラムが到達するときは，接続が切れたとき
			System.out.println("ERROR3 CONNECTION");
		}
	}

	// ルーム情報の取得
	public void getRoomInfo(){
		for(int j= 0; j<4; j++){
			Room room = RoomList.getRoomInfo(j);

			if(room == null || room.getClientNum() == 0){
				out.println("ROOMINFO " + j + " empty 0");
			}
			else{
				String str = room.getRoomName();
				int n = room.getClientNum();

				out.println("ROOMINFO "+ j + " " + str + " " + n);
			}
		}
	}

	// 新しい部屋メンバー情報を設定
	public void setInfo(int rn){
		RoomList.addRoomMember(name, incoming, in, out, rn);
		System.out.println("addRoomMember!!");

	}
}
