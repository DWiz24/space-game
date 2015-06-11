import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.*;
public class SpaceGame extends JPanel implements KeyListener, Runnable {
  static SpaceGame pan=new SpaceGame();
  static boolean wDown=false;
  static boolean aDown=false;
  static boolean sDown=false;
  static boolean dDown=false;
  ArrayList<Bullet> bullets=new ArrayList<Bullet>();
  static int width=0;
  static int height=0;
  Ship ship=new Ship();
  public static void main(String[] a) {
    JFrame j=new JFrame("Space Game");
    j.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    j.setSize(400,400);
    pan.setFocusable(true);
    pan.addKeyListener(pan);
    j.add(pan);
    j.setVisible(true);
    pan.ship.x=80;
    pan.ship.y=80;
    Thread t=new Thread(pan);
    t.start();
  }
  public void run() {
    while (true) {
    for (int i=0; i<bullets.size(); i++) {
      objs.get(i).tick();
    }
    repaint();
    try {
      Thread.sleep(30);
    } catch (Exception e) {
    }
    }
  }
  public void keyPressed(KeyEvent e) {
    char key=e.getKeyChar();
    if (key=='w') wDown=true;
    if (key=='a') aDown=true;
    if (key=='s') sDown=true;
    if (key=='d') dDown=true;
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
    for (int i=0; i<bullets.size(); i++) {
      (bullets.get(i)).render(graf);
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
  public double getChangeX(int len, int direction) {
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
  public double getChangeY(int len, int direction) {
    direction= (direction+360)%360;
    if (direction <90 && direction !=0) return -(Math.cos(Math.toRadians(direction))*(double)len);
    if (direction ==90 || direction==270) return 0;
    if (direction ==180) return -len;
    if (direction ==0) return len;
    if (direction >90 && direction<180) return (Math.sin(Math.toRadians(direction-90))*(double)len);
    if (direction >180 && direction<270) return (Math.cos(Math.toRadians(direction-180))*(double)len);
    if (direction >270 && direction!=360) return -(Math.sin(Math.toRadians(direction-270))*(double)len);
    return 5;
  }
}
class Ship extends SpaceThing {
  public void tick() {
    updateLoc();
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
    if (SpaceGame.aDown) direction -= 2;
    if (SpaceGame.dDown) direction += 2;
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
    graf.setColor(Color.GREEN);
    AffineTransform af=new AffineTransform();
    Rectangle2D r=new Rectangle2D(x,y,4,xVel+yVel);
    af.rotate(Math.toRadians(direction));
    hit=hit=af.createTransformedShape(r).getBounds2D();
    graf.setTransform
  }
  public void tick() {
    updateLoc();
    if (y<-10 || x<-10 || x>SpaceGame.width+10 || y>SpaceGame.height+10) {
      SpaceGame.pan.bullets.remove(this);
      this.dispose();
    }
  }
}
