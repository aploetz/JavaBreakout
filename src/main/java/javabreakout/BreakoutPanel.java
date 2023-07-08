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
	private final int brickBuffer = 128;
	private final int ballSize = 11;

	private boolean ballIsDead;
	private boolean ballIsPlayable;
	
	private int panelHeight;
	private int panelWidth;
		
	private Ball ball;
	private KeyHandler keyHandler;
	private List<Brick> bricks;
	private List<Color> colorList;
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
		paddle = new Paddle((panelWidth / 2) - 64, panelHeight - 200, 128, 16);
		ballIsDead = true;
		ballIsPlayable = false;
		panelThread = new Thread(this);
	}

	private void generateColors() {
		colorList = new ArrayList<>();
		
		colorList.add(Color.BLUE);
		colorList.add(Color.GREEN);
		colorList.add(Color.CYAN);
		colorList.add(Color.RED);
		colorList.add(Color.MAGENTA);
		colorList.add(Color.PINK);
		colorList.add(Color.GRAY);
		colorList.add(Color.YELLOW);
	}

	private List<Brick> generateBricks() {
	
		generateColors();
		List<Brick> returnVal = new ArrayList<>();
		int brickRow = 0;
		int brickCol = 0;
		
		while (brickCol < 16) {
			
			while (brickRow < 8) {
				
				Brick newBrick = new Brick(brickCol * brickWidth, 
						(brickRow * brickHeight) + brickBuffer,
						brickCol + brickWidth, brickRow + brickHeight,
						colorList.get(brickRow));
				returnVal.add(newBrick);
				brickRow++;
			}
			brickRow = 0;
			brickCol++;
		}
		
		return returnVal;
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
		if (!ballIsDead) {
			
			checkCollision();

			if (ballIsPlayable) {
				// checkCollision method could render the ball uplayable
				ball.update();
			}
		}
	}
	
	private void checkCollision() {

		int ballX = ball.getBallX();
		int ballY = ball.getBallY();
		int paddleX = paddle.getPaddleX();
		int paddleY = paddle.getPaddleY();
		int paddleWidth = paddle.getPaddleWidth();
		
		if (ballY  > panelHeight) {
			// bottom "pit"
			ballIsDead = true;
			ballIsPlayable = false;
			// destroy ball
			ball = null;
		} else if (ballY >= paddleY && !ball.isMovingUp()) {
			// paddle
			// check X axis
			if (ballX >= paddleX &&
					ballX <= paddleX + paddleWidth) {

				ball.setMovingUp(true);
				
				// check for ball angle adjustment
				if (keyHandler.isLeftPressed()) {
					if (ball.isMovingLeft()) {
						ball.increaseAngle();
					} else {
						ball.decreaseAngle();
					}
				} else if (keyHandler.isRightPressed()) {
					if (ball.isMovingLeft()) {
						ball.decreaseAngle();
					} else {
						ball.increaseAngle();
					}				
				}
			}

		} else if (ballX <= 0 && ball.isMovingLeft()) {
			// left wall
			ball.setMovingLeft(false);
		} else if (ballX >= panelWidth && !ball.isMovingLeft()) {
			// right wall
			ball.setMovingLeft(true);
		} else if (ballY <= (brickHeight * 8) + brickBuffer + brickHeight &&
				ballY > brickBuffer) {
			// bricks
			for (int brickCounter = bricks.size() - 1; brickCounter >= 0; brickCounter--) {
				// more likely to hit lower bricks first and more often,
				// so check them from the bottom-up.
				if (!bricks.get(brickCounter).isBroken()) {
					// only check for collision if it is not broken
					int brickX = bricks.get(brickCounter).getBrickX();
					int brickY = bricks.get(brickCounter).getBrickY();
					int brickMaxX = bricks.get(brickCounter).getBrickMaxX();
					int brickMaxY = bricks.get(brickCounter).getBrickMaxY();
					
					if (ballX >= brickX && ballX <= brickMaxX
							&& ballY >= brickY && ballY <= brickMaxY) {
						// break brick!
						bricks.get(brickCounter).setBroken(true);
						bricks.get(brickCounter).setColor(Color.BLACK);

						// for now, just flip the ball's vertical direction on a brick break
						ball.flipVerticalDirection();
					}
				}
			}
		
		} else if (ball.getBallY() <= 1) {
			// top wall
			ball.setMovingUp(false);
		}
		
		// otherwise, no collision
	}
	
	public void paintComponent(Graphics g) {
		
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D)g;
		
		// bricks
		for (Brick brick : bricks) {
		
			int brickX = brick.getBrickX();
			int brickY = brick.getBrickY();
			g2.setColor(brick.getColor());
			g2.fillRect(brickX, brickY, brickWidth, brickHeight);
		}
		
		// paddle
		g2.setColor(Color.WHITE);
		g2.fillRect(paddle.getPaddleX(), paddle.getPaddleY(),
				paddle.getPaddleWidth(), paddle.getPaddleHeight());
		
		// ball
		if (ball != null) {
			// SILVER
			g2.setColor(new Color(192,192,192));
			int centerOffset = ball.getBallSizeOffset();
			g2.fillRect(ball.getBallX() - centerOffset, ball.getBallY() - centerOffset,
					ball.getBallSize(), ball.getBallSize());
		
			// DEBUG - output ballX and ballY on screen
			g2.setColor(Color.white);
			StringBuilder output = new StringBuilder("X:");
			output.append(ball.getBallX());
			output.append("  Y:");
			output.append(ball.getBallY());
			g2.drawString(output.toString(), 50, 50);
		}
		
		g2.dispose();
	}
	
	public void start() {
		panelThread.start();		
	}
	
	public void stop() {
		panelThread = null;
	}
	
	public void releaseBall() {
		ball = new Ball(ballSize, panelWidth, brickHeight * 8, brickBuffer);
		ballIsDead = false;
		ballIsPlayable = true;
	}
	
	public boolean getBallIsDead() {
		return this.ballIsDead;
	}
}
