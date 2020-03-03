package legoEv3;

//コメント入力開始から終了までの実行時間を計測する
public class TimerThread extends Thread{
	long executeTime;

	public TimerThread() {

	}

	@SuppressWarnings("deprecation")
	@Override
	public void run() {
		long startTime = System.currentTimeMillis();

		while(!NicoLiveManager.endSW){
			try{				
				NicoLiveManager.commentState = NicoLiveManager.brCs.read();	
				//System.out.println(NicoLiveManager.commentState);
				if(NicoLiveManager.commentState == -1 ||  
						NicoLiveManager.brCs == null){
					NicoLiveManager.endSW = true;
					long endTime = System.currentTimeMillis();
					executeTime = endTime - startTime;
					System.out.println("実行時間: "+executeTime+"ms");
					this.stop();
				}
				
			}catch(Exception e){
				System.out.println(e.getMessage());
			}
			
		}
	
	}
	
}
