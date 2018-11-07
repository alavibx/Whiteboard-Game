package applications;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import app.JApplication;
import io.ResourceFinder;
import visual.ScaledVisualizationRenderer;
import visual.Visualization;
import visual.VisualizationView;
import visual.dynamic.described.Stage;
import visual.dynamic.sampled.Screen;
import visual.statik.SimpleContent;
import visual.statik.sampled.Content;
import visual.statik.sampled.ContentFactory;

/**
 * A game.
 * 
 * @author Behan Alavi, Jonathon Kent, Cayleigh Verhaalen
 * @version 11/6/2018
 */
public class BernstdhBoard extends JApplication
{
  private SimpleContent[] bernstein_left;
  private SimpleContent[] bernstein_right;
  private SimpleContent[] bernstein_erase;

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
    SwingUtilities.invokeAndWait(new BernstdhBoard(1190, 600));
  }

  /**
   * This method is called just before the main window is first made visible and initializes all of
   * the components of the window.
   */
  @Override
  public void init()
  {
    JPanel contentPane = (JPanel) this.getContentPane();
    ResourceFinder finder = ResourceFinder.createInstance(resources.Marker.class);

    // the stage
    Stage stage = new Stage(75);
    stage.setBackground(Color.BLUE);
    VisualizationView stageView = stage.getView();
    stageView.setBounds(0, 0, width, height);

    ContentFactory factory = new ContentFactory(finder);

    // Add the words to the board
    // Content word = factory.createContent("word.png", 4);
    // word.setLocation(0, 480 - 144);
    // stage.add(word);

    // add the back ground
    Content bkgd = factory.createContent("maingame_background.png", 4);
    bkgd.setScale(1, 1);
    stage.add(bkgd);

    // Add the player's character (i.e., Bernstein)
    BernsteinSprite bernstdh = new BernsteinSprite();
    stage.add(bernstdh);
    stage.addKeyListener(bernstdh);

    // add the foreground
    Content frgd = factory.createContent("maingame_foreground.png", 4);
    frgd.setScale(1, 1);
    stage.add(frgd);

    stageView.setBounds(0, 0, width, height);
    contentPane.add(stageView);
    stage.start();
  }

}
