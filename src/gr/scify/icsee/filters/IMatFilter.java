package gr.scify.icsee.filters;

import org.opencv.core.Mat;

public interface IMatFilter {

	public Mat getMat();
	public void setMat(Mat mToUse);
	public IMatFilter thenApply(IMatFilter f);
	public IMatFilter applyfilter();
	
	public void increaseParameter();
	public void decreaseParameter();
}
