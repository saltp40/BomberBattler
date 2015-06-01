package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

// クライアントと接続用
public class ConnectServer{

	private static Socket incoming;//受付用のソケット
	private static InputStreamReader isr;//入力ストリーム用の配列
	private static BufferedReader in;//バッファリングをによりテキスト読み込み用の配列
	private static PrintWriter out;//出力ストリーム用の配列

	//mainプログラム
	public static void main(String[] args) {

		int n = 0;

		// サーバ側のソケットを作成
		System.out.println("The server has launched!");
		ServerSocket server = null;
		try {
			server = new ServerSocket(50000);//50000番ポートを利用する
		} catch (IOException e1) {
			e1.printStackTrace();
			System.exit(-1);
		}

		// クライアントとの接続処理
		while (true) {
			try {
				// クライアントの接続待ち
				incoming = server.accept();
				System.out.println("Accept client No." + (n++));

				//必要な入出力ストリームを作成する
				isr = new InputStreamReader(incoming.getInputStream());
				in = new BufferedReader(isr);
				out = new PrintWriter(incoming.getOutputStream(), true);
				String str = in.readLine();

				if (str != null) {   //このソケット（バッファ）に入力があるかをチェック

					String[] inputTokens = str.split(" ");

					// 新しい部屋を作る
					if(inputTokens[0].equals("NEW")) {
						String name = inputTokens[1];
						String roomName = inputTokens[2];

						if(!RoomList.makeRoom(roomName, name, incoming, in, out)) out.println("ERROR100");

					}
					// 入れる部屋を探す
					if (inputTokens[0].equals("ENTER")) {
						String name = inputTokens[1];
						(new SingleThread(name, incoming)).start();
					}
				}
			} catch (Exception e) {
				System.err.println("ソケット作成時にエラーが発生しました: " + e);
			}
		}
	}
}
