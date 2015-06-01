package stage;


public class Chara{

	private final int BOMB_NUM_MAX = 5;
	private final int BOMB_POWER_MAX = 10;
	private final int SPEED_MAX = 3;
	private int status;   // キャラクターのステータス
	private int bombNum;  // ボム数
	private int bombCurrent; // 今現在使用中のボム数
	private int bombPower;  // ボム威力
	private int speed;   // 移動速度
	private Bomb[] bomb;  // ボムクラス
	private int x, y;     // キャラの座標

	public Chara(){
		bomb = new Bomb[BOMB_NUM_MAX];
		status = 0;
		bombNum = 1;
		bombCurrent = 0;
		bombPower = 1;
		x = 0;
		y = 0;
	}

	// 座標の設定
	public void setPosition(int x, int y){
		this.x = x;
		this.y = y;
	}

	// x座標の取得
	public int getXPosition(){
		return x;
	}

	// y座標の取得
	public int getYPosition(){
		return y;
	}

	// アイテムを拾った時の処理
	public void setItem(int itn){
		switch(itn){
		case 21:  // ボム威力+1
			bombPower++;
			if(bombPower > BOMB_POWER_MAX) bombPower = BOMB_POWER_MAX;
			break;
		case 22:  // ボム数+1
			bombNum++;
			if(bombNum > BOMB_NUM_MAX) bombNum = BOMB_NUM_MAX;
			break;
		case 23:  // ボム威力MAX
			bombPower = BOMB_POWER_MAX;
			break;
		case 24:  // ボム数MAX
			bombNum = BOMB_NUM_MAX;
			break;
		case 25:  // speedUP
			speed++;
			if(speed > SPEED_MAX) speed = SPEED_MAX;
			break;
		default:
		}
	}

	// 状態のセット
	public void setStatus(int f){
		status = f;
	}

	// フラグの取得
	public int getStatus(){
		return status;
	}

	// スピードを返す
	public int getSpeed(){
		return speed;
	}

	// ボムパワーを返す
	public int getBombPower(){
		return bombPower;
	}

	// 設置したボムパワーを返す
	public int getSetPower(int n){
		return bomb[n].getPower();
	}

	// ボム所持可能個数のセット
	public void setBombNum(int n){
		if(n <= BOMB_NUM_MAX) bombNum = n;
	}

	// ボム所持可能個数を返す
	public int getBombNum(){
		return bombNum;
	}

	// ボム使用個数を返す
	public int getBombCurrent(){
		return bombCurrent;
	}

	// ボムを使用する
	public boolean useBomb(int nn){
		if(bombNum > bombCurrent){
			bomb[bombCurrent] = new Bomb(nn, (x+12)/25, (y+12)/25, bombPower);
			bombCurrent++;
			return true;
		}
		else return false;
	}

	// すぐ爆発するボムを使用する
	public boolean useExBomb(int nn){
		if(bombNum > bombCurrent){
			bomb[bombCurrent] = new Bomb(nn, (x+12)/25, (y+12)/25, bombPower);
			bomb[bombCurrent].setExTime(-1);
			bombCurrent++;
			return true;
		}
		else return false;
	}

	// ボムパワーのセット
	public void setBombPower(int p){
		if(p <= BOMB_POWER_MAX) bombPower = p;
	}

	// ボムの爆発時間経過
	public boolean minusBombTime(int n, int ts){
		return bomb[n].minusBombTime(ts);
	}

	// ボムが連鎖爆発したとき
	public void doBombChain(int n){
		bomb[n].setExTime(-1);
	}

	// ボムの所持更新
	public void updateBomb(){
		Bomb[] bombtmp = new Bomb[BOMB_NUM_MAX];;
		int count=0;
		for(int j=0; j<bombCurrent; j++){
			if(bomb[j].getExTime() > 0){
				bombtmp[count] = bomb[j];
				count++;
			}
		}
		bombCurrent = count;
		bomb = bombtmp;
	}

	// ボムのX座標を返す
	public int getBombX(int n){
		return bomb[n].getBombX();
	}

	// ボムのY座標を返す
	public int getBombY(int n){
		return bomb[n].getBombY();
	}

}
