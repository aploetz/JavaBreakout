package javabreakout;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.JPanel;

public class BreakoutPanel extends JPanel implements Runnable {

	private static final long serialVersionUID = -7279076888542180135L;

	private final int fPS = 60; // frames per second
	private final int brickWidth = 64;
	private final int brickHeight = 32;
	private final int buffer = 128;

	private boolean ballIsDead;
	
	private int panelHeight;
	private int panelWidth;
		
	private Ball ball;
	private KeyHandler keyHandler;
	private List<Brick> bricks;
	private Paddle paddle;
	private Thread panelThread;
	
	public BreakoutPanel() {
		this(1024,1024);
	}
	
	public BreakoutPanel(int width, int height) {
		
		panelWidth = width;
		panelHeight = height; 
		
		this.setPreferredSize(new Dimension(panelWidth, panelHeight));
		this.setBackground(Color.black);
		this.setDoubleBuffered(true);
		this.setFocusable(true);

		keyHandler = new KeyHandler(this);
		this.addKeyListener(keyHandler);
		
		bricks = generateBricks();
		paddle = new Paddle((panelWidth / 2) - 64, panelHeight - 100, 128, 16);
		ballIsDead = true;
		panelThread = new Thread(this);
	}

	private List<Brick> generateBricks() {
	
		List<Brick> returnVal = new ArrayList<>();
		int brickRow = 0;
		int brickCol = 0;
		
		while (brickCol < 16) {
			
			while (brickRow < 8) {
				
				Brick newBrick = new Brick(brickCol, brickRow, generateRandomColor());
				returnVal.add(newBrick);
				brickRow++;
			}
			brickRow = 0;
			brickCol++;
		}
		
		return returnVal;
	}
	
	private Color generateRandomColor() {
		Random rndColor = new Random();

		int red = rndColor.nextInt(255) + 1;
		int green = rndColor.nextInt(255) + 1;
		int blue = rndColor.nextInt(255) + 1;
		
		return new Color(red, green, blue);
	}
	
	public void run() {
		
		while (panelThread != null) {
			update();
			repaint();
			
			// compute pauses based on frames per second
			try {
				Thread.sleep(1000 / fPS);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private void update() {
		
		// paddle
		if (keyHandler.isLeftPressed() || keyHandler.isRightPressed()) {
			if (keyHandler.isLeftPressed()) {
				if (paddle.getPaddleX() - paddle.getPaddleSpeed() > 0) {
					paddle.moveLeft();
				}
			} else {
				if (paddle.getPaddleX() + paddle.getPaddleSpeed() < panelWidth)
				paddle.moveRight();
			}
		}
		
		// ball
		
	}
	
	public void paintComponent(Graphics g) {
		
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D)g;
		
		// bricks
		for (Brick brick : bricks) {
		
			int brickX = brick.getBrickX() * brickWidth;
			int brickY = (brick.getBrickY() * brickHeight) + buffer;
			g2.setColor(brick.getColor());
			g2.fillRect(brickX, brickY, brickWidth, brickHeight);
		}
		
		// paddle
		g2.setColor(Color.WHITE);
		g2.fillRect(paddle.getPaddleX(), paddle.getPaddleY(), paddle.getPaddleWidth(),
				paddle.getPaddleHeight());
		
		// ball
	}
	
	public void start() {
		panelThread.start();		
	}
	
	public void stop() {
		panelThread = null;
	}
	
	public void releaseBall() {
		ball = new Ball();
	}
	
	public boolean getBallIsDead() {
		return this.ballIsDead;
	}
}
