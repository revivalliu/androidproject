package com.yichen.procrasinationX.paint.view;

public class FingerMatrix {
	
	private float maxX = 0;
	private float maxY = 0;
	private float minX = 0;
	private float minY = 0;
	
	
	
	
	/**
	 * 
	 * @param x
	 * @param y
	 */
	public void init(float x,float y){
		maxX=x;
		minX=x;
		maxY=y;
		minY=y;
	}
	
	public void setX(float x){
		if(x<0)
			return;
		if(maxX == minX){ //第一次赋值
			if(x>maxX){
				maxX=x;
			}else if(x<minX){
				minX=x;
			}
		}else{  //第二次赋值
			if(x > maxX && x > minX){
				maxX=x;
			}else if(x < maxX && x < minX){
				minX=x;
			}
		}
	}
	
	public void setY(float y){
		if(y<0)
			return;
		if(maxY == minY){ //第一次赋值
			if(y > maxY){
				maxY= y;
			}else if(y < minY){
				minY= y ;
			}
		}else{  //第二次赋值
			if(y > maxY && y > minY){
				maxY=y;
			}else if(y < maxY && y < minY){
				minY=y;
			}
		}
	}
	
	public float getMaxX() {
		return maxX;
	}

	public float getMaxY() {
		return maxY;
	}

	public float getMinX() {
		return minX;
	}

	public float getMinY() {
		return minY;
	}

}
