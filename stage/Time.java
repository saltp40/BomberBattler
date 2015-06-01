package stage;

// 時間を計るクラス
// 時間は1024倍しておき、小数点以下を考えて処理が遅くなっても60FPSに近い動きにする
public class Time {
	
	private final int FPS = 60;  
	private final long BASE_TIME = (1000 << 16) / FPS;  
	private long newTime, oldTime;
	long error = 0;  
	
	// デフォルトコンストラクター
	public Time(){
		newTime = System.currentTimeMillis() << 16;
		oldTime = System.currentTimeMillis() << 16;
	}

	// 現在時間の更新
	public void updateTime(){
		oldTime = newTime;
	}
	
	// 休止時間の取得
	public long getSleepTime(){
		newTime = System.currentTimeMillis() << 16;
		long sleepTime = BASE_TIME - (newTime - oldTime) - error; // 休止できる時間  
		if (sleepTime < 0x20000) sleepTime = 0x20000; // 最低でも2msは休止  
		error = newTime - oldTime - sleepTime;
		return sleepTime >> 16;
	}
}
