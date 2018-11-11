package applications;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import app.JApplication;
import io.ResourceFinder;
import visual.VisualizationView;
import visual.dynamic.described.Stage;
import visual.statik.sampled.Content;
import visual.statik.sampled.ContentFactory;

/**
 * A game.
 * 
 * @author Behan Alavi, Jonathon Kent, Cayleigh Verhaalen
 * @version 11/6/2018
 */
public class BernstdhBoard extends JApplication implements KeyListener
{
  private Content[] boardContents;
  private JPanel contentPane;
  private Content bb;
  private boolean isPaused, gameStarted;

  Stage stage;

  /**
   * Contructs a new BernstdhBoard of the size width x height.
   * 
   * @param width
   *          Width of the GUI
   * @param height
   *          Height of the GUI
   */
  public BernstdhBoard(int width, int height)
  {
    super(width, height);
  }

  /**
   * The entry-point into the application.
   * 
   * @param args
   *          NOT USED
   * @throws InvocationTargetException
   * @throws InterruptedException
   */
  public static void main(String[] args) throws InvocationTargetException, InterruptedException
  {
    SwingUtilities.invokeAndWait(new BernstdhBoard(1200, 600));
  }

  /**
   * This method is called just before the main window is first made visible and initializes all of
   * the components of the window.
   */
  @Override
  public void init()
  {
    contentPane = (JPanel) this.getContentPane();
    ResourceFinder finder = ResourceFinder.createInstance(resources.Marker.class);
    ContentFactory factory = new ContentFactory(finder);

    // Add the words to the board
    // Content word = factory.createContent("word.png", 4);
    // word.setLocation(0, 480 - 144);
    // stage.add(word);

    // Get the contents that will display on the whiteboard
    String[] files = finder.loadResourceNames("content.txt");
    boardContents = factory.createContents(files, 4);

    // Create and add the stage
    stage = new Stage(75);
    stage.setBackground(Color.WHITE);
    VisualizationView stageView = stage.getView();
    stageView.setBounds(0, 0, width, height);
    contentPane.add(stageView);
    
    Content bkgd = factory.createContent("background.png", 4);
    bkgd.setScale(1.0, 1.0);
    bkgd.setLocation(0, 0);
    stage.add(bkgd);

    // Add the mainscreen display
     bb = factory.createContent("bernstdh-mainscreen.png", 4);
    bb.setScale(1.0, 1.0);
    bb.setLocation(0, 0);
    stage.add(bb);

    stage.addKeyListener(this);

    stageView.setBounds(0, 0, width, height);

    stage.start();
    isPaused = false;
    gameStarted = false;
  }

  /**
   * 
   * @param ke
   *          A key event
   */
  public void keyPressed(KeyEvent ke)
  {
    int keyCode;
    keyCode = ke.getKeyCode();

    if ((keyCode == KeyEvent.VK_ENTER) && isPaused == false && gameStarted == true)
    {
      stage.stop();
      isPaused = true;
    }

    if ((keyCode == KeyEvent.VK_ENTER) && isPaused == true && gameStarted == true)
    {
      stage.start();
      isPaused = false;
    }

    if ((keyCode == KeyEvent.VK_ENTER) && isPaused == false && gameStarted == false)
    {
      // Add the player's character (i.e., Bernstein)
      BernsteinSprite bernstdh = new BernsteinSprite();
      bernstdh.setScale(1.5);
      stage.add(bernstdh);
      stage.addKeyListener(bernstdh);

      gameStarted = true;
      
      stage.remove(bb);
    }
  }

  /**
   * 
   * @param arg0
   */
  public void keyReleased(KeyEvent arg0)
  {
    // TODO Auto-generated method stub

  }

  /**
   * 
   * @param arg0
   */
  public void keyTyped(KeyEvent arg0)
  {
    // TODO Auto-generated method stub

  }

}
