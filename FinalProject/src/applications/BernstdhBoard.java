package applications;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowListener;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import app.JApplication;
import auditory.sampled.BoomBox;
import auditory.sampled.BufferedSound;
import auditory.sampled.BufferedSoundFactory;
import event.MetronomeListener;
import io.ResourceFinder;
import visual.Visualization;
import visual.VisualizationView;
import visual.dynamic.described.Stage;
import visual.statik.sampled.Content;
import visual.statik.sampled.ContentFactory;

/**
 * A game.
 * 
 * @author Behan Alavi, Jonathon Kent, Cayleigh Verhaalen
 * @version 11/13/2018
 */
public class BernstdhBoard extends JApplication implements KeyListener, MetronomeListener, ComponentListener
{
  private Content bkgd, bb;
  private JPanel contentPane;
  private boolean isPaused, gameStarted;
  private JLabel score;
  private Board board;
  private JFrame mainWindow;
  private ResourceFinder finder;
  private Clip mainClip, gameClip;
  private VisualizationView stageView;

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
    // SET THE VISUAL COMPONENTS OF THE GUI
    // Get the content pane of the window
    contentPane = (JPanel) this.getContentPane();
    contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.PAGE_AXIS));

    // Get the main window from the content pane (originally set in JApplication)
    mainWindow = (JFrame) SwingUtilities.getWindowAncestor(contentPane);
    mainWindow.setResizable(true);
    mainWindow.setTitle("Bernstdh-Board");
    mainWindow.setMinimumSize(new Dimension(width, height));

    finder = ResourceFinder.createInstance(resources.Marker.class);
    ContentFactory factory = new ContentFactory(finder);

    JPanel topPanel = new JPanel(new GridLayout(0, 1));
    topPanel.setBackground(Color.WHITE);
    score = new JLabel("Welcome to ISAT 236", SwingConstants.CENTER);
    score.setFont(new Font(score.getFont().getName(), Font.ITALIC, 20));
    topPanel.add(score);
    contentPane.add(topPanel);

    // Create and add the stage
    JPanel centerPanel = new JPanel(new BorderLayout());
    centerPanel.setBackground(Color.WHITE);
    stage = new Stage(75);
    stage.setBackground(Color.WHITE);
    stageView = stage.getView();
    stageView.addComponentListener(this);

    centerPanel.add(stageView, BorderLayout.CENTER);
    contentPane.add(centerPanel);

    // Set the background image
    bkgd = factory.createContent("background.png", 4);
    bkgd.setScale(1.0, 1.0);
    bkgd.setLocation(0, 0);
    stage.add(bkgd);

    // Add the mainscreen display
    bb = factory.createContent("bernstdh-mainscreen.png", 4);
    bb.setScale(1.0, 1.0);
    bb.setLocation(0, 0);
    stage.add(bb);

    stage.addKeyListener(this);
    stage.getMetronome().addListener(this);

    // Play main menu music, if the file is available. Otherwise, continue with no musi
    try
    {
      BufferedInputStream bis = new BufferedInputStream(finder.findInputStream("main.wav"));
      AudioInputStream ais = AudioSystem.getAudioInputStream(bis);

      mainClip = AudioSystem.getClip();
      mainClip.open(ais);
      mainClip.start();
      mainClip.loop(Clip.LOOP_CONTINUOUSLY);
    }
    catch (IOException | UnsupportedAudioFileException | LineUnavailableException e)
    {
      // Do nothing
    }

    // Make the view "visible"
    stageView.setBounds(0, 0, width, height);

    stage.start();
    isPaused = false;
    gameStarted = false;
  }

  /**
   * Responds to events when the "Enter" key is pressed. Different events occur under different
   * conditions.
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
      gameStarted = true;
      board = new Board(stage);

      stage.getMetronome().addListener(this);

      // Play game music, if the file is available. Otherwise, continue with no music.
      try
      {
        BufferedInputStream bis = new BufferedInputStream(finder.findInputStream("game.wav"));
        AudioInputStream ais = AudioSystem.getAudioInputStream(bis);
        gameClip = AudioSystem.getClip();
        gameClip.open(ais);
        gameClip.start();

        mainClip.stop();
        mainClip.close();

        gameClip.loop(Clip.LOOP_CONTINUOUSLY);
      }
      catch (IOException | UnsupportedAudioFileException | LineUnavailableException e)
      {
        // Do nothing
      }

      stage.remove(bb);
      stage.add(board);
    }
  }

  /**
   * 
   * @param arg0
   */
  public void keyReleased(KeyEvent arg0)
  {
    // Unimplemented
  }

  /**
   * 
   * @param arg0
   */
  public void keyTyped(KeyEvent arg0)
  {
    // Unimplemented
  }

  /**
   * 
   */
  @Override
  public void handleTick(int arg0)
  {
    if (board != null)
      score.setText("SCORE: " + board.getTotalPoints());
  }

  @Override
  public void componentHidden(ComponentEvent arg0)
  {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void componentMoved(ComponentEvent arg0)
  {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void componentResized(ComponentEvent arg0)
  {
    // Set the location of the "main screen" to be in the center of the window at all times
    stage.getView().setLocation((mainWindow.getWidth() - width)/2, (mainWindow.getHeight() - height)/2);
    stage.repaint();
  }

  @Override
  public void componentShown(ComponentEvent arg0)
  {
    // TODO Auto-generated method stub
    
  }
}
