package voorbeeld;

import java.util.Random;

import gui.GUI;

public class Voorbeeld { 
	public static void main(String[] args){
		GUI gui = new GUI();
		Thread t = new Thread(gui);
		t.start();  
		showARandomAnt(gui);
		//showSpinningAnt(gui);
	}
	
	public static void showSpinningAnt(GUI gui){
		gui.setSpeedSlow();
		gui.setGridDimensions(1,1);
		gui.setGridToLargeSize();
		gui.setStartXY(0,0);
		gui.setStartOrientationSouth();

		gui.rotateRight();
		gui.rotateRight();
		gui.rotateRight();
		gui.rotateRight();
		gui.rotateRight();
		gui.rotateRight();
		gui.rotateRight();
	}
	
	public static void showARandomAnt(GUI gui){
		gui.setSpeedMedium();
		gui.setGridDimensions(32,32);
		gui.setGridToSmallSize();
		gui.setStartXY(0, 0);
		gui.setStartOrientationSouth();
		Random r = new Random();
		for(int i = 0; i < 32; i++){
			gui.addFood(r.nextInt(32),r.nextInt(32));
		}
		for(int i = 0; i < 100; i++){
			int x = r.nextInt(100);
			if(x < 10) gui.rotateLeft();
			else if(x < 20) gui.rotateRight();
			else gui.move();
		}
	}
}
