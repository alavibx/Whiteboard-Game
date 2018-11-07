package applications;

import java.awt.image.BufferedImage;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import app.JApplication;
import io.ResourceFinder;
import visual.Visualization;
import visual.VisualizationView;
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

  @Override
  public void init()
  {
    JPanel contentPane = (JPanel) this.getContentPane();
    ResourceFinder finder = ResourceFinder.createInstance(resources.Marker.class);
    Visualization visual = new Visualization();
    VisualizationView view = visual.getView();
    view.setBounds(0, 0, width, height);
    
    ContentFactory factory = new ContentFactory(finder);
    Content bkgd = factory.createContent("maingame_background.png", 4);
    Content frgd = factory.createContent("maingame_foreground.png", 4);
    bkgd.setScale(1, 1);
    frgd.setScale(1, 1);
    
    visual.add(bkgd);
    visual.add(frgd);
    contentPane.add(view);
  }

}
