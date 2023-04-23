import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;




public class Snake extends GameEngine implements KeyListener, ActionListener{

    public static void main (String args[]){
        //add a music player
        try {
            //import the music file
            File soundFile = new File("resources/GameMusic.wav");

            AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFile);
            //get the music format

            AudioFormat format = audioIn.getFormat();

            DataLine.Info info = new DataLine.Info(Clip.class, format);

            Clip clip = (Clip) AudioSystem.getLine(info);

            clip.open(audioIn);

            clip.start();
        }
        catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
        //create new Snake Game
        createGame(new Snake());
    }
    //init the value
    int snake_Length = 3;
    int score = 0;
    //boolean the left,right,up,down keyboard
    boolean left, right, up, down;
    boolean gameOver;
    boolean paused;
    double snake_speed;
    Image snake_head, snake_body_image, apple;
    //update the apple location
    double apple_positionX, apple_positionY;
    List<Point> snake_body;
    List<Point> gamescore;

    double snake_positionX, snake_positionY;

    Timer timer = new Timer(100, this);

    enum Direction {
        LEFT, RIGHT, UP, DOWN
    }
    //Direction
    Direction direction;

    //apple location
    public void randomApple () {
        apple_positionX = rand(490);
        apple_positionY = rand(490);
    }

    @Override
    public void update ( long elapsedMs){

    }
    //boolean the initial value
    public void init () {
        gameOver = false;
        paused = false;
        left = false;
        right = false;
        up = false;
        down = false;
        snake_Length = 3;
        score = 0;
//create a ArrayList for snake_body
        snake_body = new ArrayList<>();
        for (int i = 0; i < snake_Length; i++) {
            snake_body.add(new Point((int) snake_positionX - i * 10, (int) snake_positionY));
        }
        //while snake_Length +1 > 3, score ++
        if (snake_Length > 3){
            score++;
        }
        //load the image at resources folder
        apple = loadImage("resources/apple.png");
        snake_head = loadImage("resources/head.png");
        snake_body_image = loadImage("resources/dot.png");
//init the snake location
        direction = Direction.RIGHT;
        snake_positionX = 100;
        snake_positionY = 100;
        snake_speed = 10;

        randomApple();

    }
    //update
    public void update ( double dt){
        if (!gameOver && !paused) {
            switch (direction) {
                case LEFT -> snake_positionX -= snake_speed;
                case RIGHT -> snake_positionX += snake_speed;
                case UP -> snake_positionY -= snake_speed;
                case DOWN -> snake_positionY += snake_speed;
            }
            snake_body.add(0, new Point((int) snake_positionX, (int) snake_positionY));
            snake_body.remove(snake_body.size() - 1);

            if (Math.abs(snake_positionX - apple_positionX) < 10 && Math.abs(snake_positionY - apple_positionY) < 10) {
                randomApple();
                snake_Length++;
                score++;
                snake_body.add(new Point((int) apple_positionX, (int) apple_positionY));
            }
            // Check if snake touch wall
            if (snake_positionX < 0 || snake_positionX > 500 || snake_positionY < 0 || snake_positionY > 500) {
                gameOver = true;
            }
        }
        if (!gameOver) {
            if (check_touch()) {
                gameOver = true;
            }
            if (restart()){
                init();
            }
        }
    }


    public void paintComponent () {
        if (!gameOver) {
            changeBackgroundColor(Color.pink);
            clearBackground(500, 500);
            drawImage(apple, apple_positionX, apple_positionY);
            drawImage(snake_head, snake_positionX, snake_positionY);
            for (int i = 1; i < Math.min(snake_body.size(), 20); i++) {
                Point p = snake_body.get(i);
                drawImage(snake_body_image, p.x, p.y);
            }
        } else {
            changeColor(Color.black);
            drawText(80, 200, "Game Over!", "Arial", 20);
            drawText(80,220,"Your final score is "+ score,"Arial",20);
            drawText(80,240,"Click ENTER to try again!","Arial",20);
        }
        if (!paused) {
            for (int i = 1; i < Math.min(snake_body.size(), 20); i++) {
                Point p = snake_body.get(i);
                drawImage(snake_body_image, p.x, p.y);
            }
        } else {
            changeColor(Color.red);
            drawText(120, 70, "Game Pause!", "Arial", 30);
        }
        if (gameOver){
            for (int i = 1; i < Math.min(snake_body.size(), 20); i++) {
                Point p = snake_body.get(i);
                drawImage(snake_body_image, p.x, p.y);
            }
        }else {
            changeColor(Color.green);
            drawText(30,30,"Game score : "+score,"Arial",20);
            changeColor(Color.black);
            drawText(30,480,"Tip:When your Snake_Length >= 20 , the game will be restart!!!","Arial",10);
        }
    }

    //Check if the snake's head touches the body part
    public boolean check_touch () {
        Point head = snake_body.get(0);
        for (int i = 1; i < Math.min(snake_body.size(), 20); i++) {
            if (head.equals(snake_body.get(i))) {
                return true;
            }
        }
        return false;
    }
    //init the restart, when the Length >= 20,restart the game
    public boolean restart () {
        if (snake_Length >= 20){
            snake_body.clear();
            for (int i = 0; i < snake_Length; i++) {
                snake_body.add(new Point((int) snake_positionX - i * 10, (int) snake_positionY));
            }
            direction = Direction.RIGHT;
            snake_speed = 10;
            randomApple();
            return true;
        } else {
            return false;
        }
    }

    //keyboard control
    public void keyPressed (KeyEvent e){
        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            direction = Direction.LEFT;
        }

        if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            direction = Direction.RIGHT;
        }

        if (e.getKeyCode() == KeyEvent.VK_UP) {
            direction = Direction.UP;
        }

        if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            direction = Direction.DOWN;
        }
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            if (!gameOver) {
                if (paused) {
                    paused = false;
                    timer.start();
                } else {
                    paused = true;
                    timer.stop();
                }
            }
        }
        if (e.getKeyCode() == KeyEvent.VK_ENTER){
            if (gameOver){
                init();
                timer.start();
            }else {
                timer.stop();;
            }
        }
    }

    public void actionPerformed (ActionEvent e){
        if (paused) {
            if (!gameOver) {
                switch (direction) {
                    case LEFT -> snake_positionX -= snake_speed;
                    case RIGHT -> snake_positionX += snake_speed;
                    case UP -> snake_positionY -= snake_speed;
                    case DOWN -> snake_positionY += snake_speed;
                }
                snake_body.add(0, new Point((int) snake_positionX, (int) snake_positionY));
                snake_body.remove(snake_body.size() - 1);

                if (Math.abs(snake_positionX - apple_positionX) < 10 && Math.abs(snake_positionY - apple_positionY) < 10) {
                    randomApple();
                    snake_Length++;
                    snake_body.add(new Point((int) apple_positionX, (int) apple_positionY));
                }
            }
            timer.stop();
        }
        if (restart()) {
            init();
        } else {
            clearBackground(500, 500);
        }
    }
}
