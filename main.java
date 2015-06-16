import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.*;
import java.io.*;
public class Launcher extends JFrame implements ActionListener {
  static Launcher launch;
  JButton[] buttons=new JButton[16];
  static boolean[] lvlComplete=new boolean[16];
  static long killed=0;
  static long highScore=0;
  public void actionPerformed(ActionEvent e) {
    Object s=e.getSource();
    if (s==buttons[0]) {
      new SpaceGame().gameStart();
    }
    setVisible(false);
  }
  public static void main(String[] a) {
    for (int i=0; i<lvlComplete.length; i++) {
      lvlComplete[i]=false;
    }
    try {
      FileInputStream f=new FileInputStream("data.dat");
      BufferedInputStream b=new BufferedInputStream(f);
      DataInputStream d=new DataInputStream(f);
      killed=d.readLong();
      highScore=d.readLong();
      for (int i=1; i<16; i++) {
        lvlComplete[i]=d.readBoolean();
      }
    } catch (Exception e) {
      System.out.println("Unable to load data");
    }
    launch=new Launcher();
  }
  public Launcher() {
    super("Space Game Launcher");
    buttons[0]=new JButton("Play Classic Asteroids");
    buttons[1]=new JButton("Play Level 1" + (lvlComplete[1] ? "-Complete":""));
    buttons[2]=new JButton("Play Level 2" + (lvlComplete[2] ? "-Complete":""));
    buttons[3]=new JButton("Play Level 3" + (lvlComplete[3] ? "-Complete":""));
    buttons[4]=new JButton("Play Level 4" + (lvlComplete[4] ? "-Complete":""));
    buttons[5]=new JButton("Play Level 5" + (lvlComplete[5] ? "-Complete":""));
    buttons[6]=new JButton("Play Level 6" + (lvlComplete[6] ? "-Complete":""));
    buttons[7]=new JButton("Play Level 7" + (lvlComplete[7] ? "-Complete":""));
    buttons[8]=new JButton("Play Level 8" + (lvlComplete[8] ? "-Complete":""));
    buttons[9]=new JButton("Play Level 9" + (lvlComplete[9] ? "-Complete":""));
    buttons[10]=new JButton("Play Level 10" + (lvlComplete[10] ? "-Complete":""));
    buttons[11]=new JButton("Play Level 11" + (lvlComplete[11] ? "-Complete":""));
    buttons[12]=new JButton("Play Level 12" + (lvlComplete[12] ? "-Complete":""));
    buttons[13]=new JButton("Play Level 13" + (lvlComplete[13] ? "-Complete":""));
    buttons[14]=new JButton("Play Level 14" + (lvlComplete[14] ? "-Complete":""));
    buttons[15]=new JButton("Play Level 15" + (lvlComplete[15] ? "-Complete":""));
    for (int i=0; i<16; i++) {
      buttons[i].addActionListener(this);
    }
    JLabel[] l=new JLabel[16];
    l[0]=new JLabel("Classic asteroids! Goes on forever.");
    l[1]=new JLabel("Destroy all the asteroids");
    l[2]=new JLabel("Destroy even more asteroids");
    l[3]=new JLabel("Classic asteroids!");
    l[4]=new JLabel("Classic asteroids!");
    l[5]=new JLabel("Classic asteroids!");
    l[6]=new JLabel("Classic asteroids!");
    l[7]=new JLabel("Classic asteroids!");
    l[8]=new JLabel("Classic asteroids!");
    l[9]=new JLabel("Classic asteroids!");
    l[10]=new JLabel("Classic asteroids!");
    l[11]=new JLabel("Classic asteroids!");
    l[12]=new JLabel("Classic asteroids!");
    l[13]=new JLabel("Classic asteroids!");
    l[14]=new JLabel("Classic asteroids!");
    l[15]=new JLabel("Classic asteroids!");
    JPanel content=new JPanel();
    content.setLayout(new GridLayout(9,4));
    content.add(new JPanel());
    content.add(new JLabel("Total Stuff Killed: " + killed));
    content.add(new JLabel("Classic high Score: " + highScore));
    content.add(new JLabel("<html><p>WASD to move. Space to fire. Kill all enimies to win.</p></html>"));
    for (int i=0; i<16; i+=4) {
      content.add(buttons[i]);
      content.add(buttons[i+1]);
      content.add(buttons[i+2]);
      content.add(buttons[i+3]);
      content.add(l[i]);
      content.add(l[i+1]);
      content.add(l[i+2]);
      content.add(l[i+3]);
    }
    add(content);
    pack();
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setVisible(true);
  }
}
class SpaceGame extends JPanel implements KeyListener, Runnable {
  static SpaceGame pan=new SpaceGame();
  static boolean running=true;
  static boolean wDown=false;
  static boolean aDown=false;
  static boolean sDown=false;
  static boolean dDown=false;
  static int score=0;
  static int lives=3;
  static double dif=0;
  ArrayList<Bullet> bullets=new ArrayList<Bullet>();
  ArrayList<Rock> rocks=new ArrayList<Rock>();
  static int width=0;
  static int height=0;
  Ship ship=new Ship();
  public static void gameStart() {
    pan=new SpaceGame();
    lives=3;
    score=0;
    running=true;
    dDown=false;
    sDown=false;
    aDown=false;
    wDown=false;
    dif=0;
    JFrame j=new JFrame("Space Game");
    j.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    j.setSize(400,400);
    pan.setFocusable(true);
    pan.addKeyListener(pan);
    j.add(pan);
    j.setVisible(true);
    j.setExtendedState(JFrame.MAXIMIZED_BOTH);
    Thread t=new Thread(pan);
    t.start();
  }
  public void run() {
    pan.ship.x=80;
    pan.ship.y=80;
    while (running) {
      dif += 0.00006;
      ship.tick();
    for (int i=0; i<bullets.size(); i++) {
      bullets.get(i).tick();
    }
    for (int i=0; i<rocks.size(); i++) {
      rocks.get(i).tick();
    }
    repaint();
    if (Math.random()<Math.sqrt(dif/50D)) {
      rocks.add(new Rock());
    } 
    for (int i=0; i<rocks.size(); i++) {
      Rock r=rocks.get(i);
      if (r.hit.intersects(ship.hit)) {
        lives--;
        rocks.remove(r);
      }
      for (int j=0; j<bullets.size(); j++) {
        Bullet b=bullets.get(j);
      if (r.hit.intersects(b.hit)) {
        bullets.remove(b);
        rocks.remove(r);
        if (score%50==49) lives++;
        score++;
        Launcher.killed++;
      }
      }
    }
    try {
      Thread.sleep(25);
    } catch (Exception e) {
    }
    }
  }
  public void keyPressed(KeyEvent e) {
    char key=e.getKeyChar();
    if (key=='w') wDown=true;
    if (e.getKeyCode()==KeyEvent.VK_ESCAPE) {
      running=false;
      setVisible(false);
      ((JFrame)SwingUtilities.getWindowAncestor(this)).dispose();
      Launcher.launch.setVisible(true);
    }
    if (key=='a') aDown=true;
    if (key=='s') sDown=true;
    if (key=='d') dDown=true;
    if (e.getKeyChar()==' ') {
      if (ship.toFire<=0) {
      bullets.add(new Bullet(ship.x+(int)ship.getChangeX(20,ship.direction),ship.y+(int)ship.getChangeY(20,ship.direction),ship.direction,40));
      ship.toFire=8;
      }
    }
  }
  public void keyReleased(KeyEvent e) {
    char key=e.getKeyChar();
    if (key=='w') wDown=false;
    if (key=='a') aDown=false;
    if (key=='s') sDown=false;
    if (key=='d') dDown=false;
  }
  public void keyTyped(KeyEvent e) {
  }
  public void paintComponent(Graphics g) {
    width=getSize().width;
    height=getSize().height;
    Graphics2D graf=(Graphics2D) g;
    int hei=getSize().height;
    int wid=getSize().width;
    graf.setColor(Color.BLACK);
    graf.fill(new Rectangle(0,0,wid,hei));
    ship.render(graf);
    graf.setTransform(new AffineTransform());
    graf.drawString("Score: " + score,10,10);
    graf.drawString("Lives: " + lives,10,30);
    for (int i=0; i<bullets.size(); i++) {
      (bullets.get(i)).render(graf);
    }
    for (int i=0; i<rocks.size(); i++) {
      rocks.get(i).render(graf);
    }
    if (lives<=0) {
      graf.setTransform(new AffineTransform());
      graf.setFont(new Font("Times New Roman", Font.BOLD,40));
      graf.setColor(Color.BLUE);
      graf.drawString("Diagnosis: You're dead!",wid/3,hei/3);
    }
  }
}
class SpaceThing {
  int x=10;
  int y=10;
  int direction=0;
  Rectangle2D hit=new Rectangle(0,0,5,5);
  double xVel=0;
  double yVel=0;
  double xCarry=0;
  double yCarry=0;
  public void tick() {
  }
  public void render(Graphics2D g) {
  }
  public void updateLoc() {
    double xChange=(xVel/4)+xCarry;
    x+=(int)xChange;
    xCarry=xChange%1;
    double yChange=(yVel/4)+yCarry;
    y+=(int)yChange;
    yCarry=yChange%1;
  }
  public double getChangeX(double len, float direction) {
    direction= (direction+360)%360;
    if (direction <90 && direction!=0) return (Math.sin(Math.toRadians(direction))*(double)len);
    if (direction ==90) return len;
    if (direction ==180 || direction==0) return 0;
    if (direction ==270) return -len;
    if (direction >90 && direction<180) return (Math.cos(Math.toRadians(direction-90))*(double)len);
    if (direction >180 && direction<270) return -(Math.sin(Math.toRadians(direction-180))*(double)len);
    if (direction >270 && direction!=360) return -(Math.cos(Math.toRadians(direction-270))*(double)len);
    return 5;
  }
  public double getChangeY(double len, float direction) {
    direction= (direction+360)%360;
    if (direction <90 && direction !=0) return -(Math.cos(Math.toRadians(direction))*(double)len);
    if (direction ==90 || direction==270) return 0;
    if (direction ==180) return len;
    if (direction ==0) return -len;
    if (direction >90 && direction<180) return (Math.sin(Math.toRadians(direction-90))*(double)len);
    if (direction >180 && direction<270) return (Math.cos(Math.toRadians(direction-180))*(double)len);
    if (direction >270 && direction!=360) return -(Math.sin(Math.toRadians(direction-270))*(double)len);
    return 5;
  }
}
class Ship extends SpaceThing {
  int toFire=0;
  public void tick() {
    updateLoc();
    toFire--;
    if (SpaceGame.wDown) {
      xVel = xVel + getChangeX(1,direction);
      yVel = yVel + getChangeY(1,direction);
    }
    if (SpaceGame.sDown) {
      xVel = xVel -getChangeX(1,direction);
      yVel = yVel -getChangeY(1,direction);
    }
    xVel *= 0.98;
    yVel *= 0.98;
    if (SpaceGame.aDown) direction -= 3;
    if (SpaceGame.dDown) direction += 3;
    if (x<0) x=SpaceGame.width;
    if (y<0) y=SpaceGame.height;
    if (x>SpaceGame.width) x=0;
    if (y>SpaceGame.height) y=0;
    direction = (direction +360)%360;
  }
  public void render(Graphics2D g) {
    GeneralPath p=new GeneralPath();
    p.moveTo(x-10,y-8);
    p.lineTo(x+10,y);
    p.lineTo(x-10,y+8);
    p.lineTo(x-4,y);
    p.closePath();
    AffineTransform af=new AffineTransform();
    g.setTransform(af);
    g.rotate(Math.toRadians(direction-90),x,y);
    hit=af.createTransformedShape(p).getBounds2D();
    g.setColor(Color.RED);
    g.fill(p);
  }
}
class Bullet extends SpaceThing {
  public Bullet(int sx, int sy, int sdirection, int vel) {
    x=sx;
    y=sy;
    direction=sdirection;
    xVel=getChangeX(vel,direction);
    yVel=getChangeY(vel,direction);
  }
  public void render(Graphics2D g) {
    g.setColor(Color.GREEN);
    AffineTransform af=new AffineTransform();
    Rectangle2D.Double r=new Rectangle2D.Double(x,y,4,20);
    af.rotate(Math.toRadians(direction),x+2,y);
    hit=hit=af.createTransformedShape(r).getBounds2D();
    g.setTransform(af);
    g.fill(r);
  }
  public void tick() {
    updateLoc();
    if (y<-10 || x<-10 || x>SpaceGame.width+10 || y>SpaceGame.height+10) {
      SpaceGame.pan.bullets.remove(this);
    }
  }
}
class Rock extends SpaceThing {
  GeneralPath hit=new GeneralPath();
  Point[] shape=new Point[16];
  public void tick() {
    updateLoc();
    if (y<-10 || x<-10 || x>SpaceGame.width+10 || y>SpaceGame.height+10) {
      SpaceGame.pan.rocks.remove(this);
    }
  }
  public Rock() {
    double i=Math.random();
    if (i<=0.25) {
      x=-9;
      y=(int)Math.floor(Math.random()*SpaceGame.height);
      xVel=Math.abs(Math.floor(Math.random()*(30*SpaceGame.dif+2)));
      yVel=Math.floor(Math.random()*(2+30*SpaceGame.dif));
    }
    if (i<=0.5 && i>0.25) {
      x=SpaceGame.width+9;
      y=(int)Math.floor(Math.random()*SpaceGame.height);
      xVel=-Math.abs(Math.floor(Math.random()*(2+30*SpaceGame.dif)));
      yVel=Math.floor(Math.random()*(2+30*SpaceGame.dif));
    }
    if (i<=0.75 && i>0.5) {
      y=-9;
      x=(int)Math.floor(Math.random()*SpaceGame.width);
      yVel=Math.abs(Math.floor(Math.random()*(2+30*SpaceGame.dif)));
      xVel=Math.floor(Math.random()*(2+30*SpaceGame.dif));
    }
    if (i>0.75) {
      y=SpaceGame.height+9;
      x=(int)Math.floor(Math.random()*SpaceGame.width);
      yVel=-Math.abs(Math.floor(Math.random()*(2+30*SpaceGame.dif)));
      xVel=Math.floor(Math.random()*(2+30*SpaceGame.dif));
    }
    for (float deg=0; deg<360; deg +=22.5) {
      double j=Math.random()*15;
      int px=(int)getChangeX(20+j,deg);
      int py=(int)getChangeY(20+j,deg);
      shape[(int)(deg/22.5)]=new Point(px,py);
    }
  }
  public void render(Graphics2D g) {
    GeneralPath p=new GeneralPath();
    p.moveTo(shape[0].x+x,shape[0].y+y);
    for (int i=0; i<16; i++) {
      p.lineTo(shape[i].x+x,shape[i].y+y);
    }
    p.closePath();
    hit=p;
    g.setColor(new Color(153,92,21));
    g.setTransform(new AffineTransform());
    g.fill(p);
  }
}
