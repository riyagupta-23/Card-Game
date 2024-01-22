import java.util.ArrayList;
import java.util.Arrays;
import tester.*;

import javalib.impworld.*;
import java.awt.Color;
import javalib.worldimages.*;
import java.util.Random;

//represents a Card
class Card {
  int rank;
  String suit;
  boolean faceUp;
  int flippedTime;
  String theme;

  //constructs a card
  Card(int rank, String suit, boolean faceUp, int flippedTime, String theme) {
    this.rank = rank;
    this.suit = suit;
    this.faceUp = faceUp;
    this.flippedTime = flippedTime;
    this.theme = theme;
  }

  //purpose: returns the rank of a card as a string
  public String getRankString(int rank) {
    if (rank == 1) {
      return "A";
    } else if (rank == 11) {
      return "J";
    } else if (rank == 12) {
      return "Q";
    } else if (rank == 13) {
      return "K";
    } else {
      return Integer.toString(rank);
    }
  }

  //purpose: turns a card into an image. If the card is upside down, this image will be the back 
  //of the card if the card is rightside up then the image will be the face of the card.
  public WorldImage getImage() {
    if (faceUp) {

      WorldImage rankImage = new TextImage(getRankString(rank), 20, Color.BLACK);
      WorldImage suitImage;

      if (suit.equals("♣") || suit.equals("♠")) {
        suitImage = new TextImage(suit, 30, Color.BLACK);
      } else {
        suitImage = new TextImage(suit, 30, Color.RED);
      }

      WorldImage cardImage = new RectangleImage(75, 108, OutlineMode.OUTLINE, Color.BLACK);

      WorldImage overlay1 = new OverlayOffsetImage(
          new OverlayImage(cardImage, suitImage), 25, 32, rankImage);
      WorldImage overlay2 = new OverlayOffsetImage(overlay1, -25, -32, rankImage);
      return overlay2;
    } else {
      String imagePath = "Images/" + theme + "-card-back.png";
      WorldImage back = new FromFileImage(imagePath);
      return back;
    }
  }
} 

//represents a world that displays our concentration game
class ConcentrationWorld extends World {
  ArrayList<ArrayList<Card>> gameBoard;
  boolean startScreen;
  boolean themeSelectionScreen;
  boolean gameplayScreen;
  int score;
  Card prevCard;
  int prevRow;
  int prevColumn;
  Random rand;
  boolean gameStarted;
  boolean gameOver;
  int time;
  int faceUpCount;
  boolean valueOnlyMode;
  boolean colorAndValueMode;
  int stepsTaken;
  int maxSteps;
  String theme;

  //constructs a beginner concentration world which will later have stuff done on it
  ConcentrationWorld() {
    this.startScreen = true;
    this.themeSelectionScreen = false;
    this.gameplayScreen = false; 
    this.gameBoard = new ArrayList<>();
    this.score = 26; // Start with the total number of pairs
    this.gameOver = false;
    this.time = 0;
    this.faceUpCount = 0;
    this.valueOnlyMode = false;
    this.colorAndValueMode = false;
    this.stepsTaken = 0;
    this.maxSteps = 60;
  }

  //constructs a concentration world with a thematic background the user chooses 
  ConcentrationWorld(String theme) {
    this.startScreen = true;
    this.themeSelectionScreen = false;
    this.gameplayScreen = false; 
    this.gameBoard = new ArrayList<>();
    this.score = 26; // Start with the total number of pairs
    this.gameOver = false;
    this.time = 0;
    this.faceUpCount = 0;
    this.valueOnlyMode = false;
    this.colorAndValueMode = false;
    this.stepsTaken = 0;
    this.maxSteps = 60;
    this.theme = theme;


    ArrayList<Card> deck = new ArrayList<>();
    ArrayList<String> suits = new ArrayList<String>(Arrays.asList("♣", "♦", "♥", "♠"));

    for (int rank = 1; rank <= 13; rank++) {
      for (String suit : suits) {
        deck.add(new Card(rank, suit, false, 0, theme));
      }
    }
    for (int i = 0; i < 4; i++) {
      ArrayList<Card> row = new ArrayList<>();
      for (int j = 0; j < 13; j++) {
        row.add(deck.remove(0));
      }
      this.gameBoard.add(row);
    }
  }

  //constructs a concentration world with randomly placed cards 
  ConcentrationWorld(Random rand) {
    this.startScreen = true;
    this.themeSelectionScreen = false;
    this.gameplayScreen = false; 
    this.gameBoard = new ArrayList<>();
    this.score = 26; // Start with the total number of pairs
    this.gameOver = false;
    this.time = 0;
    this.faceUpCount = 0;
    this.valueOnlyMode = false;
    this.colorAndValueMode = false;
    this.stepsTaken = 0;
    this.maxSteps = 60;
    
    this.rand = rand;
    

    ArrayList<Card> deck = new ArrayList<>();
    ArrayList<String> suits = new ArrayList<String>(Arrays.asList("♣", "♦", "♥", "♠"));

     
    for (int rank = 1; rank <= 13; rank++) {
      for (String suit : suits) {
        deck.add(new Card(rank, suit, false, 0, "classic"));
      }
    }

    for (int i = deck.size() - 1; i > 0; i--) {
      int j = rand.nextInt(i + 1);
      Card c = deck.get(i);
      deck.set(i, deck.get(j));
      deck.set(j, c); // Add to random constructor
    }

    for (int i = 0; i < 4; i++) {
      ArrayList<Card> row = new ArrayList<>();
      for (int j = 0; j < 13; j++) {
        row.add(deck.remove(0));
      }
      this.gameBoard.add(row);
    }
  } 

  //makes a scene
  //the scene is the start screen, customization screen, the gameplay scene, or the end screen
  public WorldScene makeScene() {
    WorldScene scene = new WorldScene(1500, 800);

    if (this.startScreen && !this.gameOver) {
      //  the background image
      WorldImage background = new FromFileImage("Images/background-startScreen.jpg");


      WorldImage title1 = new TextImage("Concentration Game", 70, FontStyle.BOLD, Color.BLACK);
      WorldImage titleLeft = new TextImage("♣ ♦", 60, FontStyle.BOLD, Color.BLACK);
      WorldImage titleRight = new TextImage("♥ ♠", 60, FontStyle.BOLD, Color.BLACK);


      WorldImage sign = new TextImage("Designed by Riya Gupta and Ananya Kumar",
          17, FontStyle.BOLD, Color.RED);
      WorldImage title2 = new BesideImage(title1, titleRight);
      WorldImage button1Text = new TextImage("Value Only Mode", 30, Color.BLACK);
      WorldImage button2Text = new TextImage("Color and Value Mode", 30, Color.BLACK);
      WorldImage button1 = new OverlayImage(button1Text, new RectangleImage(300, 50, 
          OutlineMode.SOLID, Color.LIGHT_GRAY));
      WorldImage button2 = new OverlayImage(button2Text, new RectangleImage(400, 50, 
          OutlineMode.SOLID, Color.LIGHT_GRAY));

      // the elements on the scene
      scene.placeImageXY(background, 1500 / 2, 600 / 2 );  
      scene.placeImageXY(title2, 1500 / 2, 200);
      scene.placeImageXY(titleLeft, 1500 / 6, 200);
      scene.placeImageXY(sign, 1200, 650);
      scene.placeImageXY(button1, 1500 / 2, 375);
      scene.placeImageXY(button2, 1500 / 2, 500);

      return scene;


    } else if (this.themeSelectionScreen) {
      WorldImage theme = new FrameImage(new TextImage("Theme Selection", 40, 
          FontStyle.BOLD, Color.BLACK));
      WorldImage instruction = new TextImage("Press the respective theme number:", 
          45, FontStyle.BOLD, Color.BLACK);
      WorldImage sign = new TextImage("Designed by Riya Gupta and Ananya Kumar", 
          17, FontStyle.BOLD, Color.RED);


      WorldImage background = new FromFileImage("Images/middle-back.jpeg");

      WorldImage button1Text = new TextImage("1. Classic", 30, Color.BLACK);
      WorldImage button2Text = new TextImage("2. Star Wars", 30, Color.BLACK);
      WorldImage button3Text = new TextImage("3. Spider-Man", 30, Color.BLACK);
      WorldImage button4Text = new TextImage("4. Turtle", 30, Color.BLACK);


      WorldImage theme1 = new FromFileImage("Images/classic-card-back.png");
      WorldImage theme2 = new FromFileImage("Images/starwars-card-back.png");
      WorldImage theme3 = new FromFileImage("Images/spiderman-card-back.png");
      WorldImage theme4 = new FromFileImage("Images/turtle-card-back.png");

      WorldImage button1 = new OverlayImage(button1Text, new RectangleImage(200, 
          50, OutlineMode.SOLID, Color.LIGHT_GRAY));
      WorldImage button2 = new OverlayImage(button2Text, new RectangleImage(200, 
          50, OutlineMode.SOLID, Color.LIGHT_GRAY));
      WorldImage button3 = new OverlayImage(button3Text, new RectangleImage(210,
          50, OutlineMode.SOLID, Color.LIGHT_GRAY));
      WorldImage button4 = new OverlayImage(button4Text, new RectangleImage(200, 
          50, OutlineMode.SOLID, Color.LIGHT_GRAY));

      scene.placeImageXY(background, 1500 / 2, 600 / 2);
      scene.placeImageXY(theme, 1500 / 2, 100);
      scene.placeImageXY(instruction, 1500 / 2, 200);
      scene.placeImageXY(sign, 1200, 650);
      scene.placeImageXY(button1, 150, 300);
      scene.placeImageXY(theme1, 150, 400);
      scene.placeImageXY(button2, 450 , 300);
      scene.placeImageXY(theme2, 450, 400);
      scene.placeImageXY(button3, 800, 300);
      scene.placeImageXY(theme3, 800, 400);
      scene.placeImageXY(button4, 1200, 300);
      scene.placeImageXY(theme4, 1200, 400);

      return scene;
    } else if (this.gameplayScreen) {

      WorldImage background = new FromFileImage("Images/middle-back.jpeg");
      scene.placeImageXY(background, 1500 / 2, 600 / 2);
      for (int row = 0; row < 4; row++) {
        for (int column = 0; column < 13; column++) {
          Card card = gameBoard.get(row).get(column);


          // Only get image if the card is not null
          if (card != null) {
            WorldImage image = card.getImage();
            int cardX = 54 + column * 90; 
            int cardY = 60 + row * 122; 

            scene.placeImageXY(image, cardX, cardY);
          }
        }
      }

      WorldImage scoreText = new TextImage("Score: " + score, 30, Color.BLACK);
      WorldImage timeText = new TextImage("Time: " + time, 30, Color.BLACK);
      WorldImage stepsLeftText = new TextImage("Steps Left: " + 
          (maxSteps - stepsTaken), 30, Color.BLACK);
      scene.placeImageXY(stepsLeftText, 1200, 650);
      scene.placeImageXY(scoreText, 1200, 600);
      scene.placeImageXY(timeText, 900, 650);

      return scene;
    } 
    else {
      WorldImage background = new FromFileImage("Images/middle-back.jpeg");
      WorldImage gameOverText = new FrameImage(new TextImage("Game Over",
          70, FontStyle.BOLD, Color.BLACK));
      WorldImage scoreText = new TextImage("Final Score: " + score, 
          40, Color.BLACK);
      WorldImage stepsText = new TextImage("Steps Taken: " + stepsTaken, 
          40, Color.BLACK);
      WorldImage timeTaken = new TextImage("Time Taken:" + time, 40, 
          Color.BLACK);


      scene.placeImageXY(background, 1500 / 2, 600 / 2);
      scene.placeImageXY(timeTaken, 1500 / 2, 500);
      scene.placeImageXY(gameOverText, 1500 / 2, 200);
      scene.placeImageXY(scoreText, 1500 / 2, 300);
      scene.placeImageXY(stepsText, 1500 / 2, 400);
    }

    return scene;
  }
  
  //alters our on-screen timer and updates it
  //adds one to the time and calls updatestate
  public void onTick() {
    if (this.gameplayScreen && !this.gameOver) {
      time++;
      updateState();
    }
  }

  //allows user to click on button which reroutes to the level of difficulty they prefer
  public void onMouseClicked(Posn pos) {
    if (this.startScreen && !this.gameOver) {
      int button1X = 1500 / 2;
      int button1Y = 375;
      int button2X = 1500 / 2;
      int button2Y = 500;

      int row = pos.y;
      int column = pos.x;

      // Check if the click is within the bounds of Button 1
      if (Math.abs(row - button1Y) <= 50 && Math.abs(column - button1X) <= 300) {
        // Button 1 (Color and Value) clicked
        this.gameStarted = true;
        this.valueOnlyMode = true;
        startScreen = false;
        themeSelectionScreen = true;
      }

      // Check if the click is within the bounds of Button 2
      if (Math.abs(row - button2Y) <= 50 && Math.abs(column - button2X) <= 400) {
        // Button 2 (Value Only) clicked
        this.gameStarted = true;
        this.colorAndValueMode = false;
        startScreen = false;
        themeSelectionScreen = true;
      }
    } else if (this.gameplayScreen && !this.gameOver) { 

      // Do not allow clicking a third card
      if (faceUpCount >= 2) {
        return;
      }

      int row = (pos.y - 55) / 122;
      int column = (pos.x - 50) / 90;

      if (row >= 0 && row < 4 && column >= 0 && column < 13) {
        Card firstClick = gameBoard.get(row).get(column);

        if (firstClick != null && !firstClick.faceUp) {
          firstClick.faceUp = true;
          firstClick.flippedTime = time; 
          faceUpCount++; 
          stepsTaken++;


          if (prevCard == null) {
            prevCard = firstClick;
            prevRow = row; 
            prevColumn = column;
          }
        }
      }

      if (maxSteps - stepsTaken <= 0 || score == 0) {
        // Steps left are 0, show end screen
        this.gameplayScreen = false;
        gameOver = true; 
        score -= 1;
      }
    } 
  }

  //updates our game screen for the score and time and when a card is clicked it determines if it
  //matched or not and acts accordingly
  void updateState() {
    for (ArrayList<Card> row : gameBoard) {
      for (Card card : row) {
        if (card != null && card.faceUp && card != prevCard) {
          if (prevCard != null && prevCard.faceUp) {

            // Cases when two cards are face up
            if (this.valueOnlyMode) { 
              if (card.rank == prevCard.rank) {

                // Matching cards
                gameBoard.get(prevRow).set(prevColumn, null);
                row.set(row.indexOf(card), null);
                score -= 1;
                faceUpCount -= 2; 
              }  
              else {

                // Mismatching cards
                prevCard.faceUp = false;
                card.faceUp = false;
                faceUpCount -= 2; 
              } }

            else { 
              if (card.rank == prevCard.rank &&
                  ((card.suit.equals("♥") || card.suit.equals("♦"))
                      &&
                      (prevCard.suit.equals("♥") || prevCard.suit.equals("♦")) 
                      ||
                      (card.suit.equals("♣") || card.suit.equals("♠"))
                      &&
                      (prevCard.suit.equals("♣") || prevCard.suit.equals("♠"))))  {

                gameBoard.get(prevRow).set(prevColumn, null);
                row.set(row.indexOf(card), null);
                score -= 1;
                faceUpCount -= 2; // Two cards have been removed
              } else {
                // Mismatching cards

                prevCard.faceUp = false;
                card.faceUp = false;
                faceUpCount -= 2; // Two cards have been turned face-down
              }
            }

            prevCard = null; // Reset the last card

          } else {
            // One card is currently faced up
            if (time - card.flippedTime >= 20) {

              card.faceUp = false;
            }
          }
        }

      }
    }
    endOfGame();
  }

  //determines if the game is over and ends the game!
  public void endOfGame() {
    boolean allCardsMatched = true;

    for (ArrayList<Card> row : gameBoard) {
      for (Card card : row) {
        if (card != null) {
          allCardsMatched = false;
          break;
        }
      }
      if (!allCardsMatched) {
        break;
      }
    }

    if (score <= 0 || allCardsMatched || maxSteps - stepsTaken <= 0) {
      this.gameplayScreen = false;
      this.gameOver = true;
    }
  }

  public void onKeyEvent(String key) {

    if (this.themeSelectionScreen) {
      if (key.equals("1")) {
        this.theme = "classic";
        this.themeSelectionScreen = false;
        this.gameplayScreen = true;
        this.gameBoard = new ConcentrationWorld("classic").gameBoard; 
      }
      else if (key.equals("2")) {
        this.theme = "starwars";
        this.themeSelectionScreen = false;
        this.gameplayScreen = true;
        this.gameBoard = new ConcentrationWorld("starwars").gameBoard;
      }
      else if (key.equals("3")) {
        this.theme = "spiderman";
        this.themeSelectionScreen = false;
        this.gameplayScreen = true;
        this.gameBoard = new ConcentrationWorld("spiderman").gameBoard;
      }
      else if (key.equals("4")) {
        this.theme = "turtle";
        this.themeSelectionScreen = false;
        this.gameplayScreen = true;
        this.gameBoard = new ConcentrationWorld("turtle").gameBoard;
      }
    }

    if (this.gameplayScreen) {
      if (key.equals("r")) {
        // Reset the game
        this.gameBoard = new ArrayList<>();
        this.score = 26;
        this.gameOver = false;
        this.time = 0;
        this.faceUpCount = 0;

        ArrayList<Card> deck = new ArrayList<>();
        ArrayList<String> suits = new ArrayList<String>(Arrays.asList("♣", "♦", "♥", "♠"));

        for (int rank = 1; rank <= 13; rank++) {
          for (String suit : suits) {
            deck.add(new Card(rank, suit, false, 0, this.theme));
          }
        }

        // Shuffle the deck using the provided random object (if available)
        if (rand != null) {
          for (int i = deck.size() - 1; i > 0; i--) {
            int j = rand.nextInt(i + 1);
            Card temp = deck.get(i);
            deck.set(i, deck.get(j));
            deck.set(j, temp);
          }
        }

        for (int i = 0; i < 4; i++) {
          ArrayList<Card> row = new ArrayList<>();
          for (int j = 0; j < 13; j++) {
            row.add(deck.remove(0));
          }
          this.gameBoard.add(row); 
        }
      } 
    }
  } 
}


class ExamplesConcentration {
  ExamplesConcentration() {}

  //applicable examples that are implemented in this game
  Card aceOfSpade = new Card(1, "♠", false, 0, "classic");
  Card queenOfDiamond = new Card(12, "♦", true, 0, "spiderman");
  Card tenOfHeart = new Card(10, "♥", false, 0, "spiderman");
  Card kingOfClub = new Card(13, "♣", true, 0, "starwars");
  WorldImage classicBack = new FromFileImage("Images/classic-card-back.png");
  WorldImage spiderBack = new FromFileImage("Images/spiderman-card-back.png");
  WorldImage starwarsBack = new FromFileImage("Images/starwars-card-back.png");
  WorldImage cardImage = new RectangleImage(75, 108, OutlineMode.OUTLINE, Color.BLACK);
  ConcentrationWorld classicWorld = new ConcentrationWorld("classic");
  ConcentrationWorld spiderWorld = new ConcentrationWorld("spiderman");
  ConcentrationWorld starwarsWorld = new ConcentrationWorld("starwars");
  ConcentrationWorld emptyWorld = new ConcentrationWorld("");
  WorldScene scene = new WorldScene(1500, 800);

  //had to make this method in order to be able to represent and test any locations with the 
  //startScreen
  //purpose: to modify a starting empty scene so it looks like the start scene
  public WorldScene startScreen() {
    WorldImage title = new TextImage("Concentration Game", 70, FontStyle.BOLD, Color.BLACK);
    WorldImage sign = new TextImage("Designed by Riya Gupta", 17, FontStyle.BOLD, Color.RED);
    scene.placeImageXY(title, 1500 / 2, 200);
    scene.placeImageXY(sign, 1250, 650);


    WorldImage button1Text = new TextImage("Value Only Mode", 30, Color.BLACK);
    WorldImage button2Text = new TextImage("Color and Value Mode", 30, Color.BLACK);

    WorldImage button1 = new OverlayImage(button1Text, 
        new RectangleImage(300, 50, OutlineMode.SOLID, Color.LIGHT_GRAY));
    WorldImage button2 = new OverlayImage(button2Text, 
        new RectangleImage(400, 50, OutlineMode.SOLID, Color.LIGHT_GRAY));

    scene.placeImageXY(button1, 1500 / 2, 375);
    scene.placeImageXY(button2, 1500 / 2, 500);

    return scene;
  }

  //had to make this method in order to be able to represent and test any locations or methods with 
  //the customization Screen
  //purpose: to modify a starting empty scene so it looks like the start scene
  public WorldScene customScreen() {
    WorldImage theme = new TextImage("Theme Selection", 55, FontStyle.BOLD, Color.BLACK);
    WorldImage instruction = new TextImage("Type the respective theme number:", 
        55, FontStyle.BOLD, Color.BLACK);
    WorldImage sign = new TextImage("Designed by Riya Gupta", 17, FontStyle.BOLD, Color.RED);
    scene.placeImageXY(theme, 1500 / 2, 200);
    scene.placeImageXY(instruction, 1500 / 2, 300);
    scene.placeImageXY(sign, 1250, 650);

    WorldImage button1Text = new TextImage("1. Classic", 20, Color.BLACK);
    WorldImage button2Text = new TextImage("2. Star Wars", 30, Color.BLACK);
    WorldImage button3Text = new TextImage("3. Spider-Man", 30, Color.BLACK);

    WorldImage button1 = new OverlayImage(button1Text, 
        new RectangleImage(300, 50, OutlineMode.SOLID, Color.LIGHT_GRAY));
    WorldImage button2 = new OverlayImage(button2Text, 
        new RectangleImage(400, 50, OutlineMode.SOLID, Color.LIGHT_GRAY));
    WorldImage button3 = new OverlayImage(button3Text,
        new RectangleImage(400, 50, OutlineMode.SOLID, Color.LIGHT_GRAY));

    scene.placeImageXY(button1, 1500 / 2, 375);
    scene.placeImageXY(button2, 1500 / 2, 500);
    scene.placeImageXY(button3, 1500 / 2, 600);

    return scene;
  }

  //Due to our data definition of a Card, we can have the rank typed out as any string 
  //(not just "♣", "♦", "♥", "♠"). However, we cannot use this card in the actual game
  //This is simply an example to show all the possibilities :)
  Card aceOfClub = new Card(1, "Club", true, 0, "starwars");

  //tests the getrankstring method
  void testGetRankString(Tester t) {
    t.checkExpect(aceOfSpade.getRankString(1), "A");
    t.checkExpect(queenOfDiamond.getRankString(queenOfDiamond.rank), "Q");
    t.checkExpect(tenOfHeart.getRankString(tenOfHeart.rank), "10");
    t.checkExpect(kingOfClub.getRankString(kingOfClub.rank), "K");
  }

  //tests the getimage method
  void testGetImage(Tester t) {
    t.checkExpect(aceOfSpade.getImage(), classicBack);
    t.checkExpect(tenOfHeart.getImage(), spiderBack);
    t.checkExpect(queenOfDiamond.getImage(), new OverlayOffsetImage(new OverlayOffsetImage(
        new OverlayImage(cardImage, new TextImage("♦", 30, Color.RED)), 25, 32, 
        new TextImage("Q", 20, Color.BLACK)), -25, -32, new TextImage("Q", 20, Color.BLACK)));
    t.checkExpect(kingOfClub.getImage(), new OverlayOffsetImage(new OverlayOffsetImage(
        new OverlayImage(cardImage, new TextImage("♣", 30, Color.BLACK)), 25, 32, 
        new TextImage("K", 20, Color.BLACK)), -25, -32, new TextImage("K", 20, Color.BLACK)));
  }

  //runs the program
  void testBigBang(Tester t) {
    ConcentrationWorld world = new ConcentrationWorld();
    world.bigBang(1400, 700, 1);
  }


  ConcentrationWorld emptyWorld2;
  ConcentrationWorld classicWorld2;
  ConcentrationWorld customWorld;

  void initData() {
    emptyWorld2 = new ConcentrationWorld();
    classicWorld2 = new ConcentrationWorld("classic");
    customWorld = new ConcentrationWorld("custom");
  }

  void testOnTick(Tester t) {
    initData();
    t.checkExpect(emptyWorld.time, 0);
    emptyWorld.onTick();
    t.checkExpect(emptyWorld.time, 0);

    t.checkExpect(classicWorld.time, 0);
    classicWorld.onTick();
    t.checkExpect(classicWorld.time, 0);

    t.checkExpect(customWorld.time, 0);
    customWorld.onTick();
    t.checkExpect(customWorld.time, 0);
  }

  void testOnMouseClicked(Tester t) {
    initData();
    Posn pos1 = new Posn(100, 100);
    Posn pos2 = new Posn(80, 120);
    Posn pos3 = new Posn(130, 160);

    t.checkExpect(emptyWorld.faceUpCount, 0);
    emptyWorld.onMouseClicked(pos1);
    t.checkExpect(emptyWorld.faceUpCount, 0); // One card at the clicked position

    t.checkExpect(classicWorld.faceUpCount, 0);
    classicWorld.onMouseClicked(pos2);
    t.checkExpect(classicWorld.faceUpCount, 0); // One card turned face up

    t.checkExpect(customWorld.faceUpCount, 0);
    customWorld.onMouseClicked(pos3);
    t.checkExpect(customWorld.faceUpCount, 0); // One card turned face up
  }

  void testUpdateState(Tester t) {
    initData();
   
    // Test case 1: No previous card
    t.checkExpect(emptyWorld2.prevCard, null);
    emptyWorld2.updateState();
    t.checkExpect(emptyWorld2.prevCard, null); // No previous card to compare

    // Test case 2: One card turned face up (matching)
    classicWorld2.gameBoard.get(0).get(0).faceUp = true;
    classicWorld2.prevCard = classicWorld2.gameBoard.get(0).get(0);
    classicWorld2.faceUpCount = 1;
    classicWorld2.updateState();
    t.checkExpect(classicWorld2.prevCard, new Card(1, "♣", true, 0, "classic")); 
    t.checkExpect(classicWorld2.faceUpCount, 1); // Face-up count should be reset

    // Test case 3: One card turned face up (mismatching)
    classicWorld2.gameBoard.get(0).get(0).faceUp = true;
    classicWorld2.prevCard = classicWorld2.gameBoard.get(0).get(1); 
    classicWorld2.faceUpCount = 1;
    classicWorld2.updateState();
    t.checkExpect(classicWorld2.prevCard.faceUp, false); // Previous card should be turned face dow
    t.checkExpect(classicWorld2.gameBoard.get(0).get(0).faceUp, true); // Card
    t.checkExpect(classicWorld2.faceUpCount, 1); // Face-up count should be reset

    // Test case 4: Two cards turned face up (matching)
    classicWorld2.gameBoard.get(0).get(0).faceUp = true;
    classicWorld2.gameBoard.get(0).get(1).faceUp = true;
    classicWorld2.prevCard = classicWorld2.gameBoard.get(0).get(0);
    classicWorld2.faceUpCount = 2;
    classicWorld2.updateState();
    t.checkExpect(classicWorld2.prevCard, null); // Previous card should be reset
    t.checkExpect(classicWorld2.gameBoard.get(0).get(0), new Card(1, "♣", false, 0, "classic")); 
    t.checkExpect(classicWorld2.gameBoard.get(0).get(1), new Card(1, "♦", false, 0, "classic")); 
    t.checkExpect(classicWorld2.faceUpCount, 0); // Face-up count should be reset

    // Test case 5: Two cards turned face up (mismatching)
    classicWorld2.gameBoard.get(0).get(0).faceUp = true;
    classicWorld2.gameBoard.get(0).get(1).faceUp = true;
    classicWorld2.prevCard = classicWorld2.gameBoard.get(1).get(0); 
    classicWorld2.faceUpCount = 2;
    classicWorld2.updateState();
    t.checkExpect(classicWorld2.prevCard.faceUp, false); // Previous card should be turned face dow
    t.checkExpect(classicWorld2.gameBoard.get(0).get(0).faceUp, true); 
    t.checkExpect(classicWorld2.gameBoard.get(0).get(1).faceUp, true); 
    t.checkExpect(classicWorld2.faceUpCount, 2); // Face-up count should be reset
  }

  void testEndOfGame(Tester t) {
    initData();
    t.checkExpect(emptyWorld.gameOver, false);
    emptyWorld.score = 0;
    emptyWorld.endOfGame();
    t.checkExpect(emptyWorld.gameOver, true); // No cards left, game over

    t.checkExpect(classicWorld.gameOver, false);
    classicWorld.score = 0;
    classicWorld.endOfGame();
    t.checkExpect(classicWorld.gameOver, true); // No cards left, game over

    t.checkExpect(customWorld.gameOver, false);
    customWorld.score = 0;
    customWorld.endOfGame();
    t.checkExpect(customWorld.gameOver, true); // No cards left, game over
  }

  void testOnKeyEvent(Tester t) {
    initData();
    t.checkExpect(emptyWorld.themeSelectionScreen, false);
    t.checkExpect(emptyWorld.gameplayScreen, false);

    emptyWorld.onKeyEvent("1");
    t.checkExpect(emptyWorld.themeSelectionScreen, false); // Theme selection not started
    t.checkExpect(emptyWorld.gameplayScreen, false);

    emptyWorld.startScreen = false;
    emptyWorld.themeSelectionScreen = true;
    emptyWorld.onKeyEvent("1");
    t.checkExpect(emptyWorld.themeSelectionScreen, false); // Theme selection started
    t.checkExpect(emptyWorld.gameplayScreen, true);

    t.checkExpect(classicWorld.themeSelectionScreen, false);
    t.checkExpect(classicWorld.gameplayScreen, false);

    classicWorld.onKeyEvent("2");
    t.checkExpect(classicWorld.themeSelectionScreen, false); // Theme selection not started
    t.checkExpect(classicWorld.gameplayScreen, false);

    classicWorld.startScreen = false;
    classicWorld.themeSelectionScreen = true;
    classicWorld.onKeyEvent("2");
    t.checkExpect(classicWorld.themeSelectionScreen, false); // Theme selection started
    t.checkExpect(classicWorld.gameplayScreen, true);
  }

  void testOnKeyEventReset(Tester t) {
    initData();
    classicWorld.score = 0;
    classicWorld.maxSteps = 10;

    classicWorld.onKeyEvent("r");
    t.checkExpect(classicWorld.score, 0); // Score reset
    t.checkExpect(classicWorld.maxSteps, 10); // Max steps reset
    t.checkExpect(classicWorld.gameBoard.size(), 4); // Game board not reset

    classicWorld.gameplayScreen = true;
    classicWorld.onKeyEvent("r");
    t.checkExpect(classicWorld.score, 26); // Score reset
    t.checkExpect(classicWorld.maxSteps, 10); // Max steps reset
    t.checkExpect(classicWorld.gameBoard.size(), 4); // Game board reset

    classicWorld.gameplayScreen = true;
    classicWorld.rand = new Random();
    classicWorld.onKeyEvent("r");
    t.checkExpect(classicWorld.score, 26); // Score reset
    t.checkExpect(classicWorld.maxSteps, 10); // Max steps reset
    t.checkExpect(classicWorld.gameBoard.size(), 4); // Game board reset
  }

  void testConcentrationWorld(Tester t) {
    ConcentrationWorld world = new ConcentrationWorld();
    t.checkExpect(world.startScreen, true);
    t.checkExpect(world.themeSelectionScreen, false);
    t.checkExpect(world.gameplayScreen, false);
    t.checkExpect(world.gameBoard.size(), 0);
    t.checkExpect(world.score, 26);
    t.checkExpect(world.gameOver, false);
    t.checkExpect(world.time, 0);
    t.checkExpect(world.faceUpCount, 0);
    t.checkExpect(world.valueOnlyMode, false);
    t.checkExpect(world.colorAndValueMode, false);
    t.checkExpect(world.stepsTaken, 0);
    t.checkExpect(world.maxSteps, 60);
  }
}










