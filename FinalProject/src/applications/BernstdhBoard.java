package applications;

import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;

import app.JApplication;

/**
 * A game.
 * 
 * @author Behan Alavi, Jonathon Kent, Cayleigh Verhaalen
 * @version 11/6/2018
 */
public class BernstdhBoard extends JApplication
{
  public BernstdhBoard(int width, int height)
  {
    super(width, height);
  }
  
  public static void main(String[] args) throws InvocationTargetException, InterruptedException
  {
    SwingUtilities.invokeAndWait(new BernstdhBoard(1200, 600));
  }

  @Override
  public void init()
  {

  }

}
