package stage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Random;

// ２次元配列に格納されているintの値により、ステージを作る
public class Stage{
	private final int MAXROW = 15;    // ステージの行数
	private final int MAXCOLUMN = 15;  // ステージの列数
	private int[][] stageField;   // ステージ情報を格納する２次配列(ブロック)
	private int[][] stageFuncField;  // ステージ情報を格納する２次元配列(ギミック)
	private int[][] stageBackField;   // ステージ情報を格納する２次配列(爆発カウント)
	private int[][] itemField;  // アイテム情報を格納する２次配列
	private int stageType;  // ステージの種類
	private ArrayList<int[][]> stageInfo;  // ステージ情報の格納

	private int[] bpos; // 誘爆した爆弾の座標 偶数にx, 奇数にy座標を格納
	private int bnum; // 誘爆した爆弾の個数
	private int itemSwitch[] = {30, 30, 4, 4, 20};  // アイテムスイッチ
	private int blockNum = 100;  // 設置するブロック数

	public Stage(){
		// ステージ２次元配列の初期化
		stageField = new int[MAXROW][MAXCOLUMN];
		stageFuncField = new int[MAXROW][MAXCOLUMN];
		stageBackField = new int[MAXROW][MAXCOLUMN];
		itemField = new int[MAXROW][MAXCOLUMN];
		for(int i=0; i<MAXROW; i++){
			for(int j=0; j<MAXCOLUMN; j++){
				stageField[i][j] = 0;
				if(i==0 || i==MAXROW-1) stageField[i][j] = -1;
				if(j==0 || j==MAXCOLUMN-1) stageField[i][j] = -1;
			}
		}
		stageInfo = new ArrayList<int[][]>();
		init();
	}

	// ステージ情報の取得
	public void init(){
		try{
			File file = new File("stage.ini");

			BufferedReader br = new BufferedReader(new FileReader(file));

			String str;
			while((str = br.readLine()) != null){
				int[][] s = new int[MAXROW][MAXCOLUMN];
				for(int i=0; i<15; i++){
					str = br.readLine();
					String num[] = str.split("[\\s]+");
					for(int j=0; j<num.length; j++){
						s[i][j] = Integer.parseInt(num[j]);
					}
				}
				stageInfo.add(s);
			}
			br.close();

		}catch(Exception e){
			System.out.println(e);
		}
	}

	// ステージタイプを返す
	public int getStageType(){
		return stageType;
	}

	// ステージ情報を返す
	public int[][] getStageField(){
		return stageField;
	}

	// ステージ機能情報を返す
	public int[][] getStageFuncField(){
		return stageFuncField;
	}

	// 新しいステージを作る
	// 引数 t: ステージタイプ、seed: ランダムの順番を固定
	public boolean newStage(int t, long seed){

		Random rand = new Random(seed);
		int bcount=0;  // ブロックの生成した数をカウント
		int[][] tmp;

		stageType = t;


		switch(t){
		case 0:   // ステージ１
			for(int i=0; i<MAXROW; i++){
				for(int j=0; j<MAXCOLUMN; j++){
					if(i%2==0 && j%2==0) stageField[i][j] = -1;
				}
			}
			// 	ブロックのランダム生成
			while(bcount < blockNum){
				int bx = rand.nextInt(13)+1;
				int by = rand.nextInt(13)+1;
				if(stageField[by][bx] == 0 && !isStartPosi(bx, by)){
					stageField[by][bx] = -21;
					bcount++;
				}
			}
			// アイテムのランダム生成
			setRandomItem(rand);

			break;
		case 1:
			tmp = stageInfo.get(t-1);

			for(int i=0; i<MAXROW; i++){
				for(int j=0; j<MAXCOLUMN; j++){
					if(tmp[i][j] == -1) stageField[i][j] = -1;
					else stageFuncField[i][j] = tmp[i][j];
				}
			}

			// ブロックのランダム生成
			while(bcount < blockNum){
				int bx = rand.nextInt(13)+1;
				int by = rand.nextInt(13)+1;
				if(stageField[by][bx] == 0 && !isStartPosi(bx, by)){
					stageField[by][bx] = -21;
					bcount++;
				}
			}
			// アイテムのランダム生成
			setRandomItem(rand);

			break;
		case 2:
			tmp = stageInfo.get(t-1);

			for(int i=0; i<MAXROW; i++){
				for(int j=0; j<MAXCOLUMN; j++){
					if(tmp[i][j] == -1) stageField[i][j] = -1;
					else stageFuncField[i][j] = tmp[i][j];
				}
			}

			// ブロックのランダム生成
			while(bcount < blockNum){
				int bx = rand.nextInt(13)+1;
				int by = rand.nextInt(13)+1;
				if(stageField[by][bx] == 0 && !isStartPosi(bx, by)){
					stageField[by][bx] = -21;
					bcount++;
				}
			}
			// アイテムのランダム生成
			setRandomItem(rand);

			break;
		default:
			return false;
		}
		return true;
	}

	// アイテムスイッチからアイテムのランダム生成
	public void setRandomItem(Random rand){

		int sum  = 0;  // 生成したアイテムの合計数を保持

		for(int i=0; i<itemSwitch.length; i++){
			int itemcount=0;  // それぞれのアイテムの生成した数をカウント
			while(itemcount < itemSwitch[i] && (sum+itemcount)<blockNum){
				int bx = rand.nextInt(13)+1;
				int by = rand.nextInt(13)+1;
				if(stageField[by][bx] == -21 && itemField[by][bx] == 0){
					itemcount++;
					itemField[by][bx] = 21 + i;
				}
			}
			sum+=itemcount;
		}
	}

	// アイテムスイッチのセット
	public void setItemSwitch(int item[], int bn){
		itemSwitch = item;
		blockNum = bn;
	}

	// プレイヤーのスタート位置座標に入っているか
	public boolean isStartPosi(int bx, int by){
		// player1
		if( (bx == 1 && by == 1) || (bx == 2 && by == 1) || (bx == 1 && by == 2) ){
			return true;
		}
		// player2
		else if( (bx == 13 && by == 13) || (bx == 12 && by == 13) || (bx == 13 && by == 12) ){
			return true;
		}
		// player3
		else if( (bx == 1 && by == 13) || (bx == 2 && by == 13) || (bx == 1 && by == 12) ){
			return true;
		}
		// player4
		else if( (bx == 13 && by == 1) || (bx == 12 && by == 1) || (bx == 13 && by == 2) ){
			return true;
		}
		return false;
	}

	// 指定した座標のギミックを返す
	public int isGimmick(int xp, int yp){
		return stageFuncField[(12+yp)/25][(12+xp)/25];
	}

	// 移動できるかどうかの判定
	public boolean isMove(int xp, int yp, int dir){
		int x1 = xp/25;
		int x2 = (xp+24)/25;
		int y1 = yp/25;
		int y2 = (yp+24)/25;

		if(y1 < 0 || y2 >= MAXROW || x1 < 0 || x2 >= MAXCOLUMN) return false;

		if(!isThanZero(xp, yp, dir))
			if(isMoveBomb(xp, yp, dir)) return true;

		switch(dir){

		case 0:   // 初期化のとき
			return true;
		case 1:   // 上移動
			if(stageField[y1][x1] >= 0 && stageField[y1][x2] >= 0) return true;
			break;
		case 2:   // 下移動
			if(stageField[y2][x1] >= 0 && stageField[y2][x2] >= 0) return true;
			break;
		case 3:   // 右移動
			if(stageField[y1][x2] >= 0 && stageField[y2][x2] >= 0) return true;
			break;
		case 4:   // 左移動
			if(stageField[y1][x1] >= 0 && stageField[y2][x1] >= 0) return true;
			break;
		default:
		}
		return false;
	}

	// 下が動けるフィールドにあるかどうか
	public boolean isThanZero(int x, int y, int dir){

		switch(dir){
		case 0:   // 初期化のとき
			return true;
		case 1:   // 上移動
			for(int i=1; i<26; i++){
				for(int j=0; j<25; j++){
					if(stageField[(y+i)/25][(x+j)/25] < 0) return false;
				}
			}
			break;
		case 2:   // 下移動
			for(int i=-1; i<24; i++){
				for(int j=0; j<25; j++){
					if(stageField[(y+i)/25][(x+j)/25] < 0) return false;
				}
			}
			break;
		case 3:   // 右移動
			for(int i=0; i<25; i++){
				for(int j=-1; j<24; j++){
					if(stageField[(y+i)/25][(x+j)/25] < 0) return false;
				}
			}
			break;
		case 4:   // 左移動
			for(int i=0; i<25; i++){
				for(int j=1; j<26; j++){
					if(stageField[(y+i)/25][(x+j)/25] < 0) return false;
				}
			}
			break;
		default:
		}
		return true;
	}

	// 下にボムがある場合
	public boolean isMoveBomb(int x, int y, int dir){

		for(int i=0; i<25; i++){
			for(int j=0; j<25; j++){
				if(stageField[(y+i)/25][(x+j)/25] != -51 && stageField[(y+i)/25][(x+j)/25] < 0) return false;
			}
		}
		return true;
	}

	// アイテムを取得したかどうかを返す
	public int getItem(int xp, int yp){
			int x1 = xp/25;
			int x2 = (xp+24)/25;
			int y1 = yp/25;
			int y2 = (yp+24)/25;
			int itn=0;

			if(stageField[y1][x1] >= 21){
				itn=stageField[y1][x1];
				stageField[y1][x1]=0;
			}
			else if(stageField[y1][x2] >= 21){
				itn=stageField[y1][x2];
				stageField[y1][x2]=0;
			}
			else if(stageField[y2][x1] >= 21){
				itn=stageField[y2][x1];
				stageField[y2][x1]=0;
			}
			else if(stageField[y2][x2] >= 21){
				itn=stageField[y2][x2];
				stageField[y2][x2]=0;
			}

			return itn;
		}

	// 爆発場所にいるかどうか
	public boolean isExplosion(int x, int y){

		// 爆弾の判定を緩めに判定
		for(int i=11; i<15; i++){
			for(int j=11; j<15; j++){
				if(0 < stageField[(y+i)/25][(x+j)/25]
						&& stageField[(y+i)/25][(x+j)/25] < 21) return true;
			}
		}
		return false;
	}

	// 爆弾を置けるかどうか
	public boolean isPutBomb(int x, int y){
		return stageField[(y+12)/25][(x+12)/25] >= 0;
	}

	// 爆風があるかどうか
	public boolean isExBomb(int x, int y){
		return stageField[(y+12)/25][(x+12)/25] > 0 && stageField[(y+12)/25][(x+12)/25] < 21;
	}

	// 爆弾を置く処理
	public void putBomb(int x, int y){
		stageField[(y+12)/25][(x+12)/25] = -51;
	}

	// 爆発処理
	public int[] doExplosion(int x, int y, int p){
		System.out.println("EXPLOSION!!");
		int time = -50;

		bpos = new int[25];
		bnum=0;

		stageField[y][x] = 1;
		if(stageBackField[y][x] <= 0){
			stageBackField[y][x] = time;
		}

		int i;
		// 上
		for(i=1; i<p; i++){
			if(!doExplosion(x, y-i, time, 2)) break;
		}
		if(i == p){
			doExplosion(x, y-i, time, 4);
		}

		// 下
		for(i=1; i<p; i++){
			if(!doExplosion(x, y+i, time, 2)) break;
		}
		if(i == p){
			doExplosion(x, y+i, time, 5);
		}

		// 右
		for(i=1; i<p; i++){
			if(!doExplosion(x+i, y, time, 3)) break;
		}
		if(i == p){
			doExplosion(x+i, y, time, 6);
		}

		// 左
		for(i=1; i<p; i++){
			if(!doExplosion(x-i, y, time, 3)) break;
		}
		if(i == p){
			doExplosion(x-i, y, time, 7);
		}

		if(bnum == 0) return null;

		int[] ppos = new int[bnum];
		for(i=0; i<bnum; i++)
			ppos[i] = bpos[i];
		return ppos;
	}

	// 座標ごとの爆発処理
	public boolean doExplosion(int x, int y, int time, int type){
		// 壁、ブロック、爆弾以外
		if(stageField[y][x] >= 0){
			stageField[y][x] = type;
			if(stageBackField[y][x] <= 0) stageBackField[y][x] = time;
			return true;
		}
		// 爆弾のとき
		else if(stageField[y][x] == -51){
			bpos[bnum] = x; bpos[bnum+1] = y;
			bnum+=2;
			return false;
		}
		// ブロックのとき
		else if(isDestroyBlock(x, y)){
			stageField[y][x] = -200+type;  // 複数の爆弾で一気に壊れるのを防ぐ
			if(stageBackField[y][x] <= 0) stageBackField[y][x] = time;
			return false;
		}
		// 壁のとき
		else return false;
	}

	// ブロックを破壊したかどうかの判定
	public boolean isDestroyBlock(int x, int y){
		if( -50 < stageField[y][x] && stageField[y][x] < -20) return true;
		else return false;
	}

	// 爆発状態の処理&ステージの更新
	public void updateStage(int ts){
		for(int i=0; i<MAXROW; i++){
			for(int j=0; j<MAXCOLUMN; j++){
				if(stageField[i][j] < -100){
					stageField[i][j] += 200;
				}
				if(stageBackField[i][j] < 0){
					stageBackField[i][j]+=ts;
					if(stageBackField[i][j] >= 0){
						// アイテムの表示
						if(itemField[i][j] != 0){
							stageField[i][j] = itemField[i][j];
							itemField[i][j] = 0;
						}
						else stageField[i][j] = 0;
						stageBackField[i][j] = 0;
					}
				}
			}
		}
	}

	// ステージ状態を表示(デバッグ用)
	public void showStage(){
		for(int i=0; i<MAXROW; i++){
			for(int j=0; j<MAXCOLUMN; j++){
				System.out.print(stageField[i][j] + " ");
			}
			System.out.println();
		}
	}
	public void showBackStage(){
		for(int i=0; i<MAXROW; i++){
			for(int j=0; j<MAXCOLUMN; j++){
				System.out.print(stageBackField[i][j] + " ");
			}
			System.out.println();
		}
	}
}

