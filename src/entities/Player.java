package entities;

import main.Game;
import utils.Constants.SpriteAtlas;
import utils.LoadStuff;

import java.awt.*;
import java.util.ArrayList;

import static main.Game.TILE_SIZE;

public class Player extends Entity{
    private boolean up, down, left, right;
    private ArrayList<SubPlayer> subPlayers = new ArrayList<>();
    public Player(Game game, int tilePosX, int tilePosY) {
        super(game, tilePosX * TILE_SIZE, tilePosY * TILE_SIZE, SpriteAtlas.PLAYER);
//        subPlayers.add(new SubPlayer(this, 1, 0));
//        subPlayers.add(new SubPlayer(this, 5, 0));
    }

    public void render(Graphics g) {
        g.drawImage(sprite, (int) x, (int) y, TILE_SIZE, TILE_SIZE, null);
        for (SubPlayer subplayer : subPlayers) {
            subplayer.render(g);
        }
    }

    public void reset() {
        Point spawn = game.getLevelManager().getCurrentLevel().playerSpawn;
        subPlayers.clear();
        x = spawn.x * TILE_SIZE;
        y = spawn.y * TILE_SIZE;
    }

    public void addSubPlayer(Point deltas) {
        if (deltas != null){
            subPlayers.add(new SubPlayer(this, deltas.x, deltas.y));
        }
    }

    public void cancelMovement() {
        right = down = left = up = false;
    }
    public void moveSubPlayers(int deltaX, int deltaY) {
        for (SubPlayer subPlayer : subPlayers) {
            subPlayer.move(deltaX, deltaY);
        }
    }

    private boolean okayToMove(int xDelta, int yDelta) {
        boolean move = true;
        boolean playerMove = game.getGameGrid().isOkayToMove((int) x + (xDelta * TILE_SIZE), (int) y + (yDelta * TILE_SIZE));
        if (playerMove) {
            for (SubPlayer subPlayer : subPlayers) {
                if (!subPlayer.checkCollision(xDelta, yDelta)) {
                    move = false;
                }
            }
        } else {
            move = false;
        }
        return move;
    }

    private void checkForNewSubPlayers() {
        addSubPlayer(game.getGameGrid().checkForNewSubPlayers((int) x, (int) y, new Point(0, 0)));
        for (int i = 0; i < subPlayers.size(); i++) {
            addSubPlayer(subPlayers.get(i).checkForNewSubPlayers());
        }

    }

//    TODO optimise this
    private void checkForWin() {
        if (game.getGameGrid().checkWin((int) x, (int) y)) {
            sprite = LoadStuff.Sprite(SpriteAtlas.PLAYER_CORRECT);
        } else {
            sprite = LoadStuff.Sprite(SpriteAtlas.PLAYER);
        }
    }

    public void move(Point delta) {
        if (okayToMove(delta.x, delta.y)) {
            x += delta.x * TILE_SIZE;
            y += delta.y * TILE_SIZE;
            moveSubPlayers(delta.x * TILE_SIZE, delta.y * TILE_SIZE);
            checkForNewSubPlayers();
            checkForWin();
        }
    }
}
