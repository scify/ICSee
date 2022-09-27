package gr.scify.icsee.filters.cpu;

import gr.scify.icsee.filters.IBitmapFilter;

public class IdentityFilter extends GrayScaleFilter {
	int iTimeDelay = 0;
	
	public IdentityFilter() {
		super();
	}
	
	public IdentityFilter(int iFilterDelayMillis) {
		super();
	}
	
	@Override
	public IBitmapFilter applyfilter() {
		if (iTimeDelay > 0)
		{
			for (int iCnt = 0; iCnt < 50; iCnt ++)
			{
				try {
					Thread.sleep(iTimeDelay / iCnt);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				setProgressTitle("Identity filter");
				setProgress(1.0 * iCnt / 50);
			}
			setProgressTitle("Identity filter complete.");			
		}
		
		return this;
	}
}
