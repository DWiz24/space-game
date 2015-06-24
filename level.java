import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.*;
import java.io.*;
import javax.imageio.ImageIO;
import java.awt.image.*;
class LSpaceGame extends JPanel implements KeyListener, Runnable {
  static short ticks=0;
  static LSpaceGame pan=new LSpaceGame();
  static BufferedImage back=null;
  static boolean running=true;
  static boolean wDown=false;
  static boolean aDown=false;
  static boolean sDown=false;
  static boolean dDown=false;
  static boolean spaceDown=false;
  static boolean won=false;
  static int lives=100;
  static int movingCof=12;
  static int scrnX=0;
  static int scrnY=0;
  static int level=0;
  ArrayList<LBullet> bullets=new ArrayList<LBullet>();
  ArrayList<LRock> rocks=new ArrayList<LRock>();
  ArrayList<LBullet> enemyBullets=new ArrayList<LBullet>();
  static int width=400;
  static int height=400;
  static int scrnWid=20;
  static int scrnHei=20;
  static double dif=1;
  LShip ship=new LShip();
  public static void levelSetup() {
    switch (level) {
      case 1:
        dif=0.7;
        for (int i=0; i<40; i++) {
          pan.rocks.add(new LRock());
        }
        break;
      case 2:
        dif=1.2;
        for (int i=0; i<100; i++) {
          pan.rocks.add(new LRock());
        }
        break;
        case 3:
          dif=0.8;
          for (int i=0; i<4; i++) {
            pan.rocks.add(new LaserRock());
          }
          break;
      case 13:
        dif=1.8;
        for (int i=0; i<200; i++) {
          pan.rocks.add(new LRock());
        }
        break;
    }
  }
  public static void gameStart(int l) {
    level=l;
    won=false;
    try {
      back=ImageIO.read(new File("starBackground.jpg"));
    } catch (Exception e) {
      System.out.println("Failed to load background");
    }
    pan=new LSpaceGame();
    lives=100;
    running=true;
    dDown=false;
    sDown=false;
    aDown=false;
    wDown=false;
    spaceDown=false;
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
    ticks=0;
    pan.ship.x=80;
    pan.ship.y=80;
    while (running && !won) {
      if (rocks.size()==0 && ticks>310) won=true;
      ticks++;
      if (ticks==300) levelSetup();
      ship.tick();
    for (int i=0; i<bullets.size(); i++) {
      bullets.get(i).tick();
    }
    for (int i=0; i<rocks.size(); i++) {
      rocks.get(i).tick();
    }
    for (int i=0; i<enemyBullets.size(); i++) {
      enemyBullets.get(i).tick();
    }
    repaint();
    for (int i=0; i<rocks.size(); i++) {
      LRock r=rocks.get(i);
      if (r.hit.intersects(ship.hit)) {
        lives -=20;
        rocks.remove(r);
      }
      for (int j=0; j<bullets.size(); j++) {
        LBullet b=bullets.get(j);
      if (r.hit.intersects(b.hit)) {
        bullets.remove(b);
        rocks.remove(r);
        Launcher.killed++;
      }
      }
    }
      for (int i=0; i<enemyBullets.size(); i++) {
        if (enemyBullets.get(i).hit.intersects(ship.hit)) {
          enemyBullets.remove(i);
          lives -=5;
        }
      }
    try {
      Thread.sleep(20);
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
      spaceDown=true;
    }
  }
  public void keyReleased(KeyEvent e) {
    char key=e.getKeyChar();
    if (key=='w') wDown=false;
    if (key=='a') aDown=false;
    if (key=='s') sDown=false;
    if (key=='d') dDown=false;
    if (key==' ') spaceDown=false;
  }
  public void keyTyped(KeyEvent e) {
  }
  public void paintComponent(Graphics g) {
    scrnWid=getSize().width;
    scrnHei=getSize().height;
    Graphics2D graf=(Graphics2D) g;
    width=2000;
    height=2000;
    scrnX=Math.max(0,Math.min(ship.x-scrnWid/2,width-scrnWid));
    scrnY=Math.max(0,Math.min(ship.y-scrnHei/2,height-scrnHei));
    AffineTransform trans=new AffineTransform();
    trans.scale(1.2,1.2);
    graf.setTransform(trans);
    graf.drawImage(back,-scrnX,-scrnY,null);
    ship.render(graf);
    graf.setTransform(new AffineTransform());
    graf.setFont(new Font("Times New Roman", Font.BOLD,20));
    graf.drawString("Health: " + lives,180,20);
    for (int i=0; i<bullets.size(); i++) {
      (bullets.get(i)).render(graf);
    }
    for (int i=0; i<rocks.size(); i++) {
      rocks.get(i).render(graf);
    }
    for (int i=0; i<enemyBullets.size(); i++) {
      enemyBullets.get(i).render(graf);
    }
    if (lives<=0) {
      running=false;
      graf.setTransform(new AffineTransform());
      graf.setFont(new Font("Times New Roman", Font.BOLD,60));
      graf.setColor(Color.BLUE);
      graf.drawString("Diagnosis: You're dead!",scrnWid/3,scrnHei/3);
    }
    if (won) {
      graf.setTransform(new AffineTransform());
      graf.setFont(new Font("Times New Roman", Font.BOLD,80));
      graf.setColor(Color.BLUE);
      graf.drawString("You won!",scrnWid/3,scrnHei/3);
    }
    graf.setTransform(new AffineTransform());
    graf.setColor(Color.GREEN);
    graf.draw(new Rectangle2D.Float(0,0,170,170));
    graf.setColor(Color.BLUE);
    graf.fill(new Rectangle2D.Float(ship.x*170/width,ship.y*170/height,5,5));
    graf.setColor(Color.RED);
    for (int i=0; i<rocks.size();i++) {
    graf.fill(new Rectangle2D.Float(rocks.get(i).x*170/width,rocks.get(i).y*170/height,4,4));
    }
  }
}
class LSpaceThing {
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
    double xChange=xVel/((double)LSpaceGame.movingCof)+xCarry;
    x+=(int)xChange;
    xCarry=xChange%1;
    double yChange=yVel/((double)LSpaceGame.movingCof)+yCarry;
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
  public int calcDirection(LSpaceThing t) {
    int xChan=t.x-x;
    int yChan=-(t.y-y);
    int degs=(int)Math.toDegrees(Math.atan2(xChan,yChan));
    return (degs+360)%360;
  }
}
class LShip extends LSpaceThing {
  int toFire=0;
  public void tick() {
    updateLoc();
    toFire=Math.max(toFire-1,0);
    if (LSpaceGame.spaceDown && toFire<=0) {
      LSpaceGame.pan.bullets.add(new LBullet(x+(int)getChangeX(20,direction),y+(int)getChangeY(20,direction),direction,80));
      toFire=12;
      }
    if (LSpaceGame.wDown) {
      xVel = xVel + getChangeX(1,direction);
      yVel = yVel + getChangeY(1,direction);
    }
    if (LSpaceGame.sDown) {
      xVel = xVel -getChangeX(1,direction);
      yVel = yVel -getChangeY(1,direction);
    }
    xVel *= 0.98;
    yVel *= 0.98;
    if (LSpaceGame.aDown) direction -= 3;
    if (LSpaceGame.dDown) direction += 3;
    if (x<0) x=LSpaceGame.width;
    if (y<0) y=LSpaceGame.height;
    if (x>LSpaceGame.width) x=0;
    if (y>LSpaceGame.height) y=0;
    direction = (direction +360)%360;
  }
  public void render(Graphics2D g) {
    GeneralPath p=new GeneralPath();
    int sX=LSpaceGame.scrnX;
    int sY=LSpaceGame.scrnY;
    p.moveTo(x-10-sX,y-8-sY);
    p.lineTo(x+10-sX,y-sY);
    p.lineTo(x-10-sX,y+8-sY);
    p.lineTo(x-4-sX,y-sY);
    p.closePath();
    AffineTransform af=new AffineTransform();
    g.setTransform(af);
    g.rotate(Math.toRadians(direction-90),x-sX,y-sY);
    hit=af.createTransformedShape(p).getBounds2D();
    g.setColor(Color.RED);
    g.fill(p);
  }
}
class LBullet extends LSpaceThing {
  public LBullet(int sx, int sy, int sdirection, int vel) {
    x=sx;
    y=sy;
    direction=sdirection;
    xVel=getChangeX(vel,direction);
    yVel=getChangeY(vel,direction);
  }
  public void render(Graphics2D g) {
    int sX=LSpaceGame.scrnX;
    int sY=LSpaceGame.scrnY;
    g.setColor(Color.GREEN);
    AffineTransform af=new AffineTransform();
    Rectangle2D.Double r=new Rectangle2D.Double(x-sX,y-sY,4,20);
    af.rotate(Math.toRadians(direction),x+2-sX,y-sY);
    hit=hit=af.createTransformedShape(r).getBounds2D();
    g.setTransform(af);
    g.fill(r);
  }
  public void tick() {
    updateLoc();
    if (y<-10 || x<-10 || x>LSpaceGame.width+10 || y>LSpaceGame.height+10) {
      SpaceGame.pan.bullets.remove(this);
    }
  }
}
class LRock extends LSpaceThing {
  GeneralPath hit=new GeneralPath();
  Point[] shape=new Point[16];
  public void tick() {
    updateLoc();
    if (x<0) x=LSpaceGame.width;
    if (y<0) y=LSpaceGame.height;
    if (x>LSpaceGame.width) x=0;
    if (y>LSpaceGame.height) y=0;
  }
  public LRock() {
    double i=Math.random();
    if (i<=0.25) {
      x=-9;
      y=(int)Math.floor(Math.random()*LSpaceGame.height);
      xVel=Math.abs(Math.floor(Math.random()*(30*LSpaceGame.dif+2)));
      yVel=Math.floor((Math.random()-0.5)*2*(2+30*LSpaceGame.dif));
    }
    if (i<=0.5 && i>0.25) {
      x=LSpaceGame.width+9;
      y=(int)Math.floor(Math.random()*LSpaceGame.height);
      xVel=-Math.abs(Math.floor(Math.random()*(2+30*LSpaceGame.dif)));
      yVel=Math.floor((Math.random()-0.5)*2*(2+30*LSpaceGame.dif));
    }
    if (i<=0.75 && i>0.5) {
      y=-9;
      x=(int)Math.floor(Math.random()*LSpaceGame.width);
      yVel=Math.abs(Math.floor(Math.random()*(2+30*LSpaceGame.dif)));
      xVel=Math.floor((Math.random()-0.5)*2*(2+30*LSpaceGame.dif));
    }
    if (i>0.75) {
      y=LSpaceGame.height+9;
      x=(int)Math.floor(Math.random()*LSpaceGame.width);
      yVel=-Math.abs(Math.floor(Math.random()*(2+30*LSpaceGame.dif)));
      xVel=Math.floor((Math.random()-0.5)*2*(2+30*LSpaceGame.dif));
    }
    for (float deg=0; deg<360; deg +=22.5) {
      double j=Math.random()*10;
      int px=(int)getChangeX(20+j,deg);
      int py=(int)getChangeY(20+j,deg);
      shape[(int)(deg/22.5)]=new Point(px,py);
    }
  }
  public void render(Graphics2D g) {
    int sX=LSpaceGame.scrnX;
    int sY=LSpaceGame.scrnY;
    GeneralPath p=new GeneralPath();
    p.moveTo(shape[0].x+x-sX,shape[0].y+y-sY);
    for (int i=0; i<16; i++) {
      p.lineTo(shape[i].x+x-sX,shape[i].y+y-sY);
    }
    p.closePath();
    hit=p;
    g.setColor(new Color(153,92,21));
    g.setTransform(new AffineTransform());
    g.fill(p);
  }
}
class LaserRock extends LRock {
  int toFire=0;
  public void render(Graphics2D g) {
    super.render(g);
    g.setTransform(new AffineTransform());
    g.setColor(Color.GREEN);
    g.fill(new Rectangle2D.Float(x-2-LSpaceGame.scrnX,y-2-LSpaceGame.scrnY,4,4));
  }
  public LaserRock() {
    super();
    xVel *=0.75;
    yVel*=0.75;
  }
  public void tick() {
    super.tick();
    toFire=Math.max(0,toFire-1);
    if (toFire==0) {
      toFire=(int)Math.floor(Math.random()*20)+37;
      int direction=calcDirection(LSpaceGame.pan.ship);
      LSpaceGame.pan.enemyBullets.add(new LBullet(x+(int)getChangeX(20,direction),y+(int)getChangeY(20,direction),direction,60));
    }
  }
}
