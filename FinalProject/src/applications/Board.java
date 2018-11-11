package applications;

import io.ResourceFinder;
import visual.dynamic.described.AbstractSprite;
import visual.dynamic.described.RuleBasedSprite;
import visual.dynamic.described.Stage;
import visual.statik.TransformableContent;
import visual.statik.sampled.Content;
import visual.statik.sampled.ContentFactory;

public class Board extends AbstractSprite
{

  private Content[] boardContents;
  private Stage stage;
  ResourceFinder finder = ResourceFinder.createInstance(resources.Marker.class);
  ContentFactory factory = new ContentFactory(finder);

  public Board(Stage stage)
  {
    String[] files = finder.loadResourceNames("content.txt");
    boardContents = factory.createContents(files, 4);
    this.stage = stage;
  }

  public Content randomWord()
  {

    int randCont = (int) (Math.random() * 14 + 1);
    int randX = (int) (Math.random() * (1100 - boardContents[randCont].getBounds2D().getWidth())
        + 50);
    int randY = 275;

    boardContents[randCont].setLocation(randX, randY);
    // boardContents[randCont].setScale(1);

    return boardContents[randCont];

  }

  public void handleTick(int time)
  {
    if (time % 3000 == 0)
    {
      stage.add(randomWord());
    }

  }

  @Override
  protected TransformableContent getContent()
  {
    // TODO Auto-generated method stub
    return randomWord();
  }
}
