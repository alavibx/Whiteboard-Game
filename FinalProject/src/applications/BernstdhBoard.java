package applications;

import java.awt.image.BufferedImage;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import app.JApplication;
import io.ResourceFinder;
import visual.Visualization;
import visual.VisualizationView;
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
    Visualization visual = new Visualization();
    VisualizationView view = visual.getView();
    view.setBounds(0, 0, width, height);
    
    ContentFactory factory = new ContentFactory(finder);
    Content bkgd = factory.createContent("maingame_background.png", 4);
    Content bernstein = factory.createContent("bernstein_default.png", 4);
    Content frgd = factory.createContent("maingame_foreground.png", 4);
    
    String[] filesleft = finder.loadResourceNames("bernstein_walkingleft.txt");
    String[] filesright = finder.loadResourceNames("bernstein_walkingright.txt");
    String[] fileserase = finder.loadResourceNames("bernstein_erasing.txt");
    bernstein_left = factory.createContents(filesleft, 4);
    bernstein_right = factory.createContents(filesright, 4);
    bernstein_erase = factory.createContents(fileserase, 4);
    
    bkgd.setScale(1, 1);
    bernstein.setScale(1, 1);
    bernstein.setLocation(300, 115);
    frgd.setScale(1, 1);
    
    visual.add(bkgd);
    visual.add(bernstein);
    visual.add(frgd);
    contentPane.add(view);
  }

}
