/* ConformalMapping java applet to demonstrate real-time complex mapping */
/* Handcrafted in Emacs by Jonathan Foote */
/* Original version c. 2002, rescued 2012, compile with
javac  -Xlint:deprecation -g  -classpath ../conformal/  ConformalMapping.java Complex.java Sfun.java
*/

/* Complex.java and Sfun.java borrowed from Visual Numerics, Inc, thanks!*/

package conformal;

import java.applet.Applet;
import java.awt.Button;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Scrollbar;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;



public class ConformalMapping extends Applet
  implements MouseMotionListener, AdjustmentListener, ActionListener
{

  
  Graphics bufferGraphics;
  Image offscreen;
  Dimension dim;
  int curX;
  int curY;
  Scrollbar zoom;
  Button nextB;
  Button axisB;
  Button sourceB;
  String buttonpressed;
  String status = "drag the mouse";
  int scale = 100;
  int cx;
  int cy;
  int fpointer = 1;
  boolean showaxis = true;
  boolean showsource = false;

  public void init()
  {
    this.dim = getSize();

    addMouseMotionListener(this);
    setBackground(Color.darkGray);

    this.offscreen = createImage(this.dim.width, this.dim.height);

    this.bufferGraphics = this.offscreen.getGraphics();

    // center of applet screen
    this.cx = (this.dim.width / 2);
    this.cy = (this.dim.height / 2);

    // current location of mouse pointer
    this.curX = this.cx;
    this.curY = this.cy;

    this.zoom = new Scrollbar(0, 50, 1, 50, 300);
    this.zoom.setValue(this.scale);
    this.zoom.addAdjustmentListener(this);
    add(this.zoom);

    this.axisB = new Button("Axis Off");
    this.axisB.addActionListener(this);
    add(this.axisB);

    this.sourceB = new Button("Source On");
    this.sourceB.addActionListener(this);
    add(this.sourceB);

    this.nextB = new Button("Next Map");
    this.nextB.addActionListener(this);
    add(this.nextB);
  }

  public void paint(Graphics paramGraphics)
  {
    this.bufferGraphics.clearRect(0, 0, this.dim.width, this.dim.width);

    paintme(this.bufferGraphics, this.curX, this.curY);

    paramGraphics.drawImage(this.offscreen, 0, 0, this);
  }

  public void update(Graphics paramGraphics)
  {
    paint(paramGraphics);
  }

  public void mouseMoved(MouseEvent paramMouseEvent)
  {
    repaint();
  }

  public void mouseDragged(MouseEvent paramMouseEvent)
  {
    this.curX = paramMouseEvent.getX();
    this.curY = paramMouseEvent.getY();
    repaint();
  }

  public void paintme(Graphics paramGraphics, int paramInt1, int paramInt2)
  {
    paramGraphics.setColor(Color.lightGray);
    paramGraphics.drawString("Zoom control:", 110, 22);

    paramGraphics.drawString(this.status, 500, 22);

    if (this.showaxis) paintaxis(paramGraphics);

    if (this.showsource) {
	paramGraphics.setColor(Color.red);
	/* unit square is 2*scale x 2*scale */
	paramGraphics.drawRect(paramInt1 - this.scale, paramInt2 - this.scale, 2 * this.scale, 2 * this.scale);
    }


    paramGraphics.setColor(Color.green);
    /* make grid of complex points going from -1, -i to +1, +i (width = 2 = 2*scale) */
    for (double d1 = -1.0D; d1 <= 1.0D; d1 += 0.1D)
      for (double d2 = -1.0D; d2 <= 1.0D; d2 += 0.1D) {
	 
        Complex localComplex1 = new Complex(d1 + (paramInt1 - this.cx)/(double)this.scale, 
					      d2 + (paramInt2 - this.cy)/(double)this.scale);
	  /* map every point in grid to new coordinates */ 
        Complex localComplex2 = xform(localComplex1);
	  /* unscale and center to plot */ 
        paramGraphics.fillRect(this.cx + (int)(this.scale * localComplex2.real()), 
				 this.cy + (int)(this.scale * localComplex2.imag()), 2, 2);
      }
  }
  /* Transform the complex argument using the complex function selected by this.fpointer */
  public Complex xform(Complex paramComplex)
  {
    Complex localComplex1 = new Complex(1.0D);
    Complex localComplex2 = new Complex(-1.0D);

    if (this.fpointer == 0) {
      this.status = "9/9: f(z) = z";
      return paramComplex;
    }

    if (this.fpointer == 1) {
      this.status = "1/9: f(z) = z + 1/z";
      return paramComplex.plus(Complex.over(localComplex1, paramComplex));
    }

    if (this.fpointer == 2) {
      this.status = "2/9: f(z) = sqrt(z)";
      return Complex.sqrt(paramComplex);
    }
    if (this.fpointer == 3) {
      this.status = "3/9: f(z) = tanh(z)";
      return Complex.tanh(paramComplex);
    }
    Complex localComplex3;
    if (this.fpointer == 4) {
      this.status = "4/9: f(z)=log(z)+z^2";
      localComplex3 = Complex.log(paramComplex);
      paramComplex = paramComplex.times(paramComplex);
      return paramComplex.plus(localComplex3);
    }
    if (this.fpointer == 5) {
      this.status = "5/9: f(z) = 1/z";
      return paramComplex = Complex.over(localComplex1, paramComplex);
    }
    if (this.fpointer == 6) {
      this.status = "6/9: f(z) = (z^2 + 1) / (z^2 - 1)";
      localComplex3 = paramComplex.times(paramComplex);
      paramComplex = localComplex3.plus(localComplex2);
      localComplex3 = localComplex3.plus(localComplex1);
      paramComplex = Complex.over(localComplex1, paramComplex);
      return Complex.times(paramComplex, localComplex3);
    }
    if (this.fpointer == 7) {
      this.status = "7/9: f(z) = (z - .5) / (1 - .5z) ";
      localComplex3 = paramComplex.minus(0.5D);
      Complex localComplex4 = Complex.minus(localComplex1, paramComplex.times(0.5D));
      return localComplex3.over(localComplex4);
    }
    if (this.fpointer == 8) {
      this.status = "8/9: tanh(Z)";
      return Complex.tanh(paramComplex);
    }
    return paramComplex;
  }

  public void paintaxis(Graphics paramGraphics)
  {
    this.bufferGraphics.setColor(Color.black);
    paramGraphics.fillOval(this.cx - 2, this.cy - 2, 4, 4);
    /* unit circle goes from -scale/2 to scale/2 */
    paramGraphics.drawOval(this.cx - this.scale/2, this.cy - this.scale/2, this.scale, this.scale);
    paramGraphics.drawLine(this.cx, 0, this.cx, 2 * this.cy);
    paramGraphics.drawLine(0, this.cy, 2 * this.cx, this.cy);
  }

  public void adjustmentValueChanged(AdjustmentEvent paramAdjustmentEvent) {
    this.scale = this.zoom.getValue();
    repaint();
  }

  public void actionPerformed(ActionEvent paramActionEvent) {
    this.buttonpressed = paramActionEvent.getActionCommand();
    if (this.buttonpressed.equals("Next Map")) {
      this.fpointer += 1;
      if (this.fpointer > 8)
        this.fpointer = 0;
      paintme(this.bufferGraphics, this.cx, this.cy);
    }
    if (this.buttonpressed.equals("Axis On")) {
      this.axisB.setLabel("Axis Off");
      this.showaxis = true;
    }
    if (this.buttonpressed.equals("Axis Off")) {
      this.axisB.setLabel("Axis On");
      this.showaxis = false;
    }
    if (this.buttonpressed.equals("Source On")) {
      this.sourceB.setLabel("Source Off");
      this.showsource = true;
    }
    if (this.buttonpressed.equals("Source Off")) {
      this.sourceB.setLabel("Source On");
      this.showsource = false;
    }
    repaint();
  }
}

