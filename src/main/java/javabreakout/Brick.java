package javabreakout;

import java.awt.Color;

public class Brick {

	private int brickX;
	private int brickY;
	private Color color;
	private boolean broken;
	
	public Brick(int brickX, int brickY, Color color) {
		
		this.color = color;
		this.brickX = brickX;
		this.brickY = brickY;
		this.broken = false;
	}

	public int getBrickX() {
		return brickX;
	}

	public int getBrickY() {
		return brickY;
	}

	public Color getColor() {
		return color;
	}

	public boolean isBroken() {
		return broken;
	}

	public void setBroken(boolean broken) {
		this.broken = broken;
	}
}
