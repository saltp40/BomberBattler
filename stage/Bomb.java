package stage;

public class Bomb {
	private int x, y;  // ボムを置く座標
	private int exTime;  // 爆発するまでの時間
	private int power;  // 爆発威力
	private int player; // 誰が置いたか

	public Bomb(int n, int x, int y, int p){
		this.x = x;
		this.y = y;
		power = p;
		exTime = 200;
		player = n;
	}

	// 爆発タイムを進める
	// exTimeが0になったらfalseを返す
	public boolean minusBombTime(int ts){
		exTime -= ts;
		if(exTime < 1) return false;
		else return true;
	}

	// 爆発タイムの取得
	public int getExTime(){
		return exTime;
	}

	// 爆発タイムのセット
	public void setExTime(int s){
		exTime = s;
	}

	// ボムのX座標の取得
	public int getBombX(){
		return x;
	}

	// ボムのY座標の取得
	public int getBombY(){
		return y;
	}

	// ボムの威力取得
	public int getPower(){
		return power;
	}

	// ボムを置いたプレイヤーの番号を取得
	public int getPlayerNum(){
		return player;
	}
}
