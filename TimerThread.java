package legoEv3;

//�R�����g���͊J�n����I���܂ł̎��s���Ԃ��v������
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
					System.out.println("���s����: "+executeTime+"ms");
					this.stop();
				}
				
			}catch(Exception e){
				System.out.println(e.getMessage());
			}
			
		}
	
	}
	
}
