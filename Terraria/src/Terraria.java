import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.io.*;
import java.awt.event.*;

public class Terraria extends JFrame implements KeyListener, Runnable
{
	private static final int WIDTH = 800;
	private static final int HEIGHT = 940;
	
	Random rand = new Random();
	Toolkit tk = Toolkit.getDefaultToolkit();
	private Image Character1 = tk.getImage("character1.png");
	private Image Character2 = tk.getImage("character2.png");
	
	private Image Ghost1 = tk.getImage("ghost1.png");
	private Image Ghost2 = tk.getImage("ghost2.png");
	private Image Ghost3 = tk.getImage("ghost3.png");
	private Image Ghost4 = tk.getImage("ghost4.png");
	private Image Ghost5 = tk.getImage("ghost5.png");
	
	private Image Background = tk.getImage("background.png");
	private Image Start = tk.getImage("start.png");
	private Image ReStart = tk.getImage("restart.png");
	private Image win_p1 = tk.getImage("win_p1.png");
	private Image win_p2 = tk.getImage("win_p2.png");
	private Image draw = tk.getImage("draw.png");

	private final Image wallimg = tk.getImage("black.png");
	private final Image roadimg = tk.getImage("white.png");
	private final Image passageimg = tk.getImage("gray.png");
	private final Image redimg= tk.getImage("red.png");
	private final Image blueimg = tk.getImage("blue.png");

	public static int[][] checked = new int[940][800];
	int area;
	
	public static boolean gamestart;
	public static boolean gameend;
	public static boolean gamerestart;
	
	Map m;
	Player p1, p2;
	Ghost g1, g2, g3, g4, g5;
	Thread th;
	
	Image buff_img;
	Graphics buff_g;
	
	public Terraria() 
	{
		super("¶¥µû¸Ô±â");
		setSize(WIDTH, HEIGHT);
		setResizable(false);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		gamestart = false;
		gameend = false;
		gamerestart = false;
		area = 2;
		
		m = new Map();
		
		addKeyListener(this);
		
		p1 = new Player(40, 691, "red.png");
		p2 = new Player(730, 691, "blue.png");
		
		g1 = new Ghost(70, 181);
		g2 = new Ghost(520, 781);
		g3 = new Ghost(400, 421);
		g4 = new Ghost(250, 781);
		g5 = new Ghost(700, 181);
		
		th = new Thread(this);
		th.start();
	}

	public class Map
	{
		Scanner inputStream = null;
		public Map()
		{
			try 
			{
				inputStream = new Scanner(new FileInputStream("map.txt"));
			} 
			catch (FileNotFoundException e)
			{
				System.out.println("Problem opening files.");
				System.exit(0);
			}
			
			setMap();
		}
		
		public void setMap()
		{
			int num = 0;
			int i = 0, j = 0;
	
			int firstX = 10, lastX = 40, firstY = 31, lastY = 61;
			
			while (inputStream.hasNextInt()) 
			{
				num = inputStream.nextInt();
	
				if(num == 1)
					area++;
				if (j == 26) 
				{
					i++;
					j = 0;
				}
				
				for( ; firstX < lastX; firstX++)
				{
					for(int tempY = firstY; tempY < lastY; tempY++)
					{
						checked[tempY][firstX] = num;
					}
				}
				if(lastX >= 790)
				{
					firstX = 10;
					lastX = 40;
					firstY = lastY;
					lastY += 30;
				}
				else
				{
					firstX = lastX;
					lastX += 30;
				}
			}
		}
	}
	
	
	
	public void keyPressed(KeyEvent e)
	{
		switch (e.getKeyCode()) 
		{
		case KeyEvent.VK_UP:
			p2.setKeyUp(true);
			break;
		case KeyEvent.VK_DOWN:
			p2.setKeyDown(true);
			break;
		case KeyEvent.VK_LEFT:
			p2.setKeyLeft(true);
			break;
		case KeyEvent.VK_RIGHT:
			p2.setKeyRight(true);
			break;
		case KeyEvent.VK_W:
			p1.setKeyUp(true);
			break;
		case KeyEvent.VK_S:
			p1.setKeyDown(true);
			break;
		case KeyEvent.VK_A:
			p1.setKeyLeft(true);
			break;
		case KeyEvent.VK_D:
			p1.setKeyRight(true);
			break;
		case KeyEvent.VK_ENTER:
			gamestart = true;
			break;
		case KeyEvent.VK_R:
			gamerestart = true;
			break;
		}
	}
	public void keyReleased(KeyEvent e)
	{
		switch (e.getKeyCode()) 
		{
		case KeyEvent.VK_UP:
			p2.setKeyUp(false);
			break;
		case KeyEvent.VK_DOWN:
			p2.setKeyDown(false);
			break;
		case KeyEvent.VK_LEFT:
			p2.setKeyLeft(false);
			break;
		case KeyEvent.VK_RIGHT:
			p2.setKeyRight(false);
			break;
		case KeyEvent.VK_W:
			p1.setKeyUp(false);
			break;
		case KeyEvent.VK_S:
			p1.setKeyDown(false);
			break;
		case KeyEvent.VK_A:
			p1.setKeyLeft(false);
			break;
		case KeyEvent.VK_D:
			p1.setKeyRight(false);
			break;
		}
	}
	public void keyTyped(KeyEvent e)
	{
	}
	
	private void outOfMap(Point p) 
	{
		if (p.x <= 5)
			p.x = 765;

		if (p.x >= 770)
			p.x = 5;
	}
	
	public void paint(Graphics g) 
	{
		buff_img = createImage(WIDTH, HEIGHT);
		buff_g = buff_img.getGraphics();
		update(g);
	}
	public void update(Graphics g)
	{
		drawMap(buff_g);
		drawBar(buff_g);
		
		if(gamestart)
			drawGhost(buff_g);
		drawCharacter1(buff_g);
		drawCharacter2(buff_g);
		
		drawMessage(buff_g);
		
		g.drawImage(buff_img, 0, 0, this);
	}
	public void drawMap(Graphics g)
	{
		int x, y;
		
		for(y=61; y<=901; y+=30)
		{
			for(x=10; x<=760; x+=30)
			{
				switch(checked[y][x])
				{
				case 0:
					g.drawImage(passageimg, x, y, this);
					break;
				case 1:
				case 2:
					g.drawImage(roadimg, x, y, this);
					break;
				case 3:
					g.drawImage(wallimg, x, y, this);
					break;
				case 4:
					g.drawImage(redimg, x, y, this);
					break;
				case 5:
					g.drawImage(blueimg, x, y, this);
					break;
				}
			}
		}
		
		g.drawImage(redimg, 40, 691, this);
		g.drawImage(blueimg, 730, 691, this);
		
		if(!gamestart)
			g.drawImage(Background, 10, 31, this);
	}
	public void drawMessage(Graphics g)
	{
		g.setFont(new Font("SnasSerif", Font.BOLD, 24));
		g.setColor(Color.YELLOW);
		
		if(!gamestart)
			g.drawImage(Start, 130, 331, this);
		else if(gameend)
		{
			g.drawImage(Background, 10, 31, this);
			if(p1.territory > p2.territory)
				g.drawImage(win_p1, 100, 331, this);
			else if(p1.territory < p2.territory)
				g.drawImage(win_p2, 100, 331, this);
			else
				g.drawImage(draw, 100, 331, this);
			g.drawImage(ReStart, 100, 521, this);
		}
	}
	public void drawCharacter1(Graphics g)
	{
		g.drawImage(Character1, p1.Pos.x, p1.Pos.y, this);
		repaint();
	}
	public void drawCharacter2(Graphics g)
	{
		g.drawImage(Character2, p2.Pos.x, p2.Pos.y, this);
	}
	public void drawGhost(Graphics g)
	{
		if(!gameend)
		{
			g.drawImage(Ghost1, g1.Pos.x, g1.Pos.y, this);
			g.drawImage(Ghost2, g2.Pos.x, g2.Pos.y, this);
			g.drawImage(Ghost3, g3.Pos.x, g3.Pos.y, this);
			g.drawImage(Ghost4, g4.Pos.x, g4.Pos.y, this);
			g.drawImage(Ghost5, g5.Pos.x, g5.Pos.y, this);
		}
			repaint();
	}
	private void drawBar(Graphics g)
	{
		int i;
		double border;
		
		border = (double)p1.territory/(p1.territory+p2.territory);
		
		for(i = 10; i<760*border; i+=30)
			g.drawImage(redimg, i, 31, this);
		for(; i<=760; i+=30)
			g.drawImage(blueimg, i, 31, this);
		
	}
	
	public void run() 
	{           
		try 
		{
			while (true) 
			{
				if(!gameend)
				{
					p1.Do();
					p2.Do();
					g1.move();
					g2.move();
					g3.move();
					g4.move();
					g5.move();
				}
				repaint(); 			
				Thread.sleep(30);
				
				if(gameend && gamerestart)
					reset();
				
				if(p1.territory + p2.territory == area)
					gameend = true;

			}
		}
		catch (Exception e) 
		{
		}
	}
	
	public void reset()
	{
		gamestart = false;
		gameend = false;
		gamerestart = false;
		area = 2;
		
		m = new Map();
		
		p1 = new Player(40, 691, "red.png");
		p2 = new Player(730, 691, "blue.png");
		
		g1 = new Ghost(70, 181);
		g2 = new Ghost(520, 781);
		g3 = new Ghost(400, 421);
		g4 = new Ghost(250, 781);
		g5 = new Ghost(700, 181);
	}

	public static void main(String[] args)
	{
		Terraria game = new Terraria();
		game.setVisible(true);
	}	
		

}