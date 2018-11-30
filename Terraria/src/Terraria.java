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
		super("땅따먹기");
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
	
	public class Ghost
	{
		public Point Pos = new Point(0, 0);

		int dir = 0;			// 1 위, 2 아래, 3 좌, 4 우

		public boolean GoUp = false;
		public boolean GoDown = false;
		public boolean GoLeft = false;
		public boolean GoRight = false;
		
		public boolean CanUp = false;
		public boolean CanDown = false;
		public boolean CanLeft = false;
		public boolean CanRight = false;
		
		public Ghost(int x, int y)
		{
			Pos.x = x;
			Pos.y = y;
		}
		
		public void movecheck()
		{
			int i;

			for(i=Pos.y; i<Pos.y+30; i++)			// 왼쪽 체크
			{
				if(checked[i][(2*Pos.x+30)/2 - 16] != 3)			
					CanLeft = true;
				else
				{
					CanLeft = false;
					break;
				}
			}
		
			for(i=Pos.y; i<Pos.y+30; i++)			// 오른쪽 체크
			{
				if(checked[i][(2*Pos.x+30)/2 + 15] != 3)	
					CanRight = true;
				else
				{
					CanRight = false;
					break;
				}
			}
			
			for(i=Pos.x; i<Pos.x+30; i++)			// 위쪽 체크
			{
				if(checked[(2*Pos.y+30)/2 - 16][i] != 3)
					CanUp = true;
				else
				{
					CanUp = false;
					break;
				}
			}
			for(i=Pos.x; i<Pos.x+30; i++)			// 아래쪽 체크
			{
				if(checked[(2*Pos.y+30)/2 + 15][i] != 3)	
					CanDown = true;
				else
				{
					CanDown = false;
					break;
				}
			}
		
		}		
		public void randDir()
		{
			int[] state = new int[4]; // 0 못감, 1 갈수있음, 2 가고있는중
										// [0] 위, [1] 아래, [2] 좌, [3] 우
			int temp;
			
			for(int i=0; i<4; i++)
				state[i] = 0;
			
			if(GoUp)
				state[0] = 2;
			if(GoDown)
				state[1] = 2;
			if(GoLeft)
				state[2] = 2;
			if(GoLeft)
				state[3] = 2;
			
			if(CanUp)
				state[0] = 1;
			if(CanDown)
				state[1] = 1;
			if(CanLeft)
				state[2] = 1;
			if(CanRight)
				state[3] = 1;
			
			do
			{
				temp = rand.nextInt(4);
				
			}while(state[temp] != 1);
			
			dir = temp+1;
			
			switch(dir)
			{
			case 1:
				GoUp = true;
				break;
			case 2:
				GoDown = true;
				break;
			case 3:
				GoLeft = true;
				break;
			case 4:
				GoRight = true;
				break;
			}
 		}
		public void crush()
		{
			int gapX, gapY;
			
			/** p1 */
			gapX = p1.Pos.x - Pos.x;
			gapY = p1.Pos.y - Pos.y;
			
			if((gapX < 15 && gapX > -15) && (gapY < 15 && gapY > -15))
			{
				if(!p1.touched)
				{
					p1.touchtime = System.currentTimeMillis();
					p1.touched = true;
				}
			}
			
			/** p2 */
			gapX = p2.Pos.x - Pos.x;
			gapY = p2.Pos.y - Pos.y;
			
			if((gapX < 15 && gapX > -15) && (gapY < 15 && gapY > -15))
			{
				if(!p2.touched)
				{
					p2.touchtime = System.currentTimeMillis();
					p2.touched = true;
				}
			}
			
		}
		public void move()
		{
			if(!gamestart)
				return ;

			movecheck();
			
			if(!GoUp && !GoDown && !GoLeft && !GoRight)
				randDir();
			
			if(GoUp)
			{
				if(CanUp)
					Pos.y -= 5;
				else
				{
					randDir();
					GoUp = false;
				}
			}
			if(GoDown)
			{
				if(CanDown)
					Pos.y += 5;
				else
				{
					randDir();
					GoDown = false;
				}
			}
			if(GoLeft)
			{
				if(CanLeft)
				{
					Pos.x -= 5;
					outOfMap(Pos);
				}
				else
				{
					randDir();
					GoLeft = false;
				}
			}
			if(GoRight)
			{
				if(CanRight)
				{
					Pos.x += 5;
					outOfMap(Pos);
				}
				else
				{
					randDir();
					GoRight = false;
				}
			}
			crush();
		}
	}
	
	public class Player
	{
		Point Pos = new Point(0, 0);
		int tile = 0;		// 4 red 5 blue
		String color = null;
		int territory = 1;
		public long touchtime;
		public boolean touched = false;
		
		public boolean KeyUp = false;
		public boolean KeyDown = false;
		public boolean KeyLeft = false;
		public boolean KeyRight = false;
		
		public boolean CanUp = false;
		public boolean CanDown = false;
		public boolean CanLeft = false;
		public boolean CanRight = false;
		
		public Player(int x, int y, String color)
		{
			Pos.x = x;
			Pos.y = y;
			this.color = color;
			
			if(color == "red.png")
				tile = 4;
			else if(color == "blue.png")
				tile = 5;
		}
		
		public void setKeyUp(boolean state)
		{	
			KeyUp = state; 
		}
		public void setKeyDown(boolean state)
		{	
			KeyDown = state; 
		}
		public void setKeyLeft(boolean state)
		{	
			KeyLeft = state; 
		}
		public void setKeyRight(boolean state)
		{	
			KeyRight = state; 
		}
		
		public void movecheck()
		{
			int i;

			for(i=Pos.y; i<Pos.y+30; i++)			// 왼쪽 체크
			{
				if(checked[i][(2*Pos.x+30)/2 - 16] != 3)			
					CanLeft = true;
				else
				{
					CanLeft = false;
					break;
				}
			}
		
			for(i=Pos.y; i<Pos.y+30; i++)			// 오른쪽 체크
			{
				if(checked[i][(2*Pos.x+30)/2 + 15] != 3)	
					CanRight = true;
				else
				{
					CanRight = false;
					break;
				}
			}
			
			for(i=Pos.x; i<Pos.x+30; i++)			// 위쪽 체크
			{
				if(checked[(2*Pos.y+30)/2 - 16][i] != 3)
					CanUp = true;
				else
				{
					CanUp = false;
					break;
				}
			}
			for(i=Pos.x; i<Pos.x+30; i++)			// 아래쪽 체크
			{
				if(checked[(2*Pos.y+30)/2 + 15][i] != 3)	
					CanDown = true;
				else
				{
					CanDown = false;
					break;
				}
			}
		
		}			
		public void move()
		{
			if(!gamestart)
				return ;
			
			if (KeyUp && CanUp)
				Pos.y -= 5;
			if (KeyDown && CanDown)
				Pos.y += 5;
			if (KeyLeft && CanLeft)
			{
				Pos.x -= 5;
				outOfMap(Pos);
			}
			if (KeyRight && CanRight)
			{
				Pos.x += 5;
				outOfMap(Pos);
			}
		}
		
		public void occupy()
		{
			if(!gamestart)
				return ;
			if((Pos.x%30 == 25) || (Pos.y%30 == 16))			// 반틈될때만 작동
				coloring();
		}
		public void coloring()
		{
			int direction = 0;
			int firstX, lastX, firstY, lastY;
			int tempX, tempY;
			
			direction = checkBlock();
			
			switch(direction)
			{
			case 1:			// 위로갈때
				firstX = Pos.x;
				lastX = firstX + 30;
				lastY = Pos.y + 15;
				firstY = lastY - 30;
				
				for(tempY = firstY; tempY<lastY; tempY++)
				{
					for(tempX = firstX; tempX<lastX; tempX++)
					{
						checked[tempY][tempX] = tile;
					}
				}
				territory++;
				break;
				
			case 2:			// 아래로갈때
				firstX = Pos.x;
				lastX = firstX + 30;
				firstY = Pos.y + 15;
				lastY = firstY + 30;
				
				for(tempY = firstY; tempY<lastY; tempY++)
				{
					for(tempX = firstX; tempX<lastX; tempX++)
					{
						checked[tempY][tempX] = tile;
					}
				}
				territory++;
				break;
				
			case 3:			// 왼쪽으로갈때
				lastX = Pos.x + 15;
				firstX = lastX -30;
				firstY = Pos.y;
				lastY = firstY + 30;
				
				for(tempY = firstY; tempY<lastY; tempY++)
				{
					for(tempX = firstX; tempX<lastX; tempX++)
					{
						checked[tempY][tempX] = tile;
					}
				}
				territory++;
				break;
				
			case 4:			// 오른쪽으로 갈때
				firstX = Pos.x + 15;
				lastX = firstX + 30;
				firstY = Pos.y;
				lastY = firstY + 30;
				
				for(tempY = firstY; tempY<lastY; tempY++)
				{
					for(tempX = firstX; tempX<lastX; tempX++)
					{
						checked[tempY][tempX] = tile;
					}
				}
				territory++;
				break;
			
			default :
				break;		
			}		
		}
		public int checkBlock()
		{
			int sort = 0;			// 0은 오류, 1은 up, 2는 down, 3은 left, 4는 right 체크
			
			int firstX, lastX, firstY, lastY;
			int tempX, tempY;
			
			/** 위로 가는건지 체크*/
			firstX = Pos.x;
			lastX = firstX + 30;
			lastY = Pos.y +  15;
			firstY = lastY - 30;
			
			for(tempY = firstY; tempY<lastY; tempY++)
			{
				for(tempX = firstX; tempX<lastX; tempX++)
				{
					if(checked[tempY][tempX] == 1)
						sort = 1;
					else
					{
						sort = 0;
						break;
					}					
				}
			}
			if(sort == 1)
				return sort;
			
			/** 아래로 가는건지 체크*/
			firstX = Pos.x;
			lastX = firstX + 30;
			firstY = Pos.y + 15;
			lastY = firstY + 30;
			
			for(tempY = firstY; tempY<lastY; tempY++)
			{
				for(tempX = firstX; tempX<lastX; tempX++)
				{
					if(checked[tempY][tempX] == 1)
						sort = 2;
					else
					{
						sort = 0;
						break;
					}				
				}
			}
			if(sort == 2)
				return sort;
			
			/** 왼쪽으로 가는건지 체크*/
			lastX = Pos.x + 15;
			firstX = lastX -30;
			firstY = Pos.y;
			lastY = firstY + 30;
			
			for(tempY = firstY; tempY<lastY; tempY++)
			{
				for(tempX = firstX; tempX<lastX; tempX++)
				{
					if(checked[tempY][tempX] == 1)
						sort = 3;
					else
					{
						sort = 0;
						break;
					}			
				}
			}

			if(sort == 3)
				return sort;
			
			/** 오른쪽으로 가는건지 체크*/
			firstX = Pos.x + 15;
			lastX = firstX + 30;
			firstY = Pos.y;
			lastY = firstY + 30;
			
			for(tempY = firstY; tempY<lastY; tempY++)
			{
				for(tempX = firstX; tempX<lastX; tempX++)
				{
					if(checked[tempY][tempX] == 1)
						sort = 4;
					else
					{
						sort = 0;
						break;
					}		
				}
			}
			if(sort == 4)
				return sort;
			
			return sort;
		}
	
		public void checkTime()
		{
			if(System.currentTimeMillis() - touchtime >= 2000)
				touched = false;
		}
		public void Do()
		{
			if(!touched)
			{
				movecheck();
				move();
				occupy();
			}
			else
				checkTime();
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