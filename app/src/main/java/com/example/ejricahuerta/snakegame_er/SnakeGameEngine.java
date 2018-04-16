package com.map524s1a.snakegame_er;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.Random;

/**
 * Created by ejricahuerta on 4/15/2018.
 */

public class SnakeGameEngine extends SurfaceView implements Runnable {


    private Thread _Thread = null;
    private volatile boolean _Playing;
    private Canvas _Canvas;
    private SurfaceHolder _Holder;
    private Paint _food_Paint;
    private Paint _snake_Paint;
    private Paint _bg_Paint;
    private Context _context;

    public enum Direction {UP, RIGHT, DOWN, LEFT}

    private Direction _Direction = Direction.RIGHT;
    private int _ScreenWidth;
    private int _ScreenHeight;
    private final long FPS = 10;
    private final long MILLIS_IN_A_SECOND = 1000;
    private int _Score;
    private int[] _SnakeXs;
    private int[] _SnakeYs;
    private int _SnakeLength;
    private int _FoodX;
    private int _FoodY;
    private long _NextFrameTime;
    private int _BlockSize;
    private final int NU_BLOCKS_WIDE = 40;
    private int _NumBlocksHigh = 0;

    public SnakeGameEngine(Context context, Point point) {
        super(context);
        _context = context;
        _ScreenWidth = point.x;
        _ScreenHeight = point.y;
        _BlockSize = _ScreenWidth / NU_BLOCKS_WIDE;
        _NumBlocksHigh = _ScreenHeight / _BlockSize;
        _Holder = getHolder();
        _food_Paint = new Paint();
        _bg_Paint = new Paint();
        _snake_Paint = new Paint();
        _SnakeXs = new int[200];
        _SnakeYs = new int[200];
        startGame();
    }

    public void startGame() {
        _SnakeLength = 1;
        _SnakeXs[0] = NU_BLOCKS_WIDE / 2;
        _SnakeYs[0] = _NumBlocksHigh / 2;

        SpawnFood();
        _Score = 0;
        _NextFrameTime = System.currentTimeMillis();
    }

    public void SpawnFood() {
        Random random = new Random();
        _FoodX = random.nextInt(NU_BLOCKS_WIDE - 1) + 1;
        _FoodY = random.nextInt(_NumBlocksHigh - 1) + 1;
    }

    private void eatMouse() {
        _SnakeLength++;
        SpawnFood();
        _Score = _Score + 1;
    }

    private void moveSnake() {

        for (int i = _SnakeLength; i > 0; i--) {
            _SnakeXs[i] = _SnakeXs[i - 1];
            _SnakeYs[i] = _SnakeYs[i - 1];
        }
        switch (_Direction) {
            case UP:
                _SnakeYs[0]--;
                break;

            case RIGHT:
                _SnakeXs[0]++;
                break;

            case DOWN:
                _SnakeYs[0]++;
                break;

            case LEFT:
                _SnakeXs[0]--;
                break;
        }
    }

    private boolean IsDead() {
        boolean dead = false;

        if (_SnakeXs[0] == -1) dead = true;
        if (_SnakeXs[0] >= NU_BLOCKS_WIDE) dead = true;
        if (_SnakeYs[0] == -1) dead = true;
        if (_SnakeYs[0] == _NumBlocksHigh) dead = true;
        for (int i = _SnakeLength - 1; i > 0; i--) {
            if ((i > 4) && (_SnakeXs[0] == _SnakeXs[i]) && (_SnakeYs[0] == _SnakeYs[i])) {
                dead = true;
            }
        }
        return dead;
    }


    public void updateGame() {
        if (_SnakeXs[0] == _FoodX && _SnakeYs[0] == _FoodY) {
            eatMouse();
        }

        moveSnake();

        if (IsDead()) {
            startGame();
        }
    }

    public void pause() {
        _Playing = false;
        try {
            _Thread.join();
        } catch (InterruptedException e) {
            // Error
        }
    }

    public void resume() {
        _Playing = true;
        _Thread = new Thread(this);
        _Thread.start();
    }

    public void drawGame() {
        if (_Holder.getSurface().isValid()) {
            _Canvas = _Holder.lockCanvas();

            _Canvas.drawColor(getResources().getColor(R.color.bg_color, _context.getTheme()));
            _snake_Paint.setColor(getResources().getColor(R.color.snake_color, _context.getTheme()));
            _bg_Paint.setColor(getResources().getColor(R.color.bg_color, _context.getTheme()));
            _food_Paint.setColor(getResources().getColor(R.color.food_color, _context.getTheme()));
            _food_Paint.setTextSize(100);
            _Canvas.drawText("Score:" + _Score, 100, 100, _food_Paint);

            for (int i = 0; i < _SnakeLength; i++) {
                _Canvas.drawRect(_SnakeXs[i] * _BlockSize,
                        (_SnakeYs[i] * _BlockSize),
                        (_SnakeXs[i] * _BlockSize) + _BlockSize,
                        (_SnakeYs[i] * _BlockSize) + _BlockSize,
                        _snake_Paint);
            }

            _Canvas.drawRect(_FoodX * _BlockSize,
                    (_FoodY * _BlockSize),
                    (_FoodX * _BlockSize) + _BlockSize,
                    (_FoodY * _BlockSize) + _BlockSize,
                    _food_Paint);

            _Holder.unlockCanvasAndPost(_Canvas);
        }
    }

    public boolean checkForUpdate() {

        if (_NextFrameTime <= System.currentTimeMillis()) {
            _NextFrameTime = System.currentTimeMillis() + MILLIS_IN_A_SECOND / FPS;
            return true;
        }
        return false;
    }

    @Override
    public void run() {
        while (_Playing) {
            if (checkForUpdate()) {
                updateGame();
                drawGame();
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_UP:
                if (motionEvent.getX() >= _ScreenWidth / 2) {
                    switch (_Direction) {
                        case UP:
                            _Direction = Direction.RIGHT;
                            break;
                        case RIGHT:
                            _Direction = Direction.DOWN;
                            break;
                        case DOWN:
                            _Direction = Direction.LEFT;
                            break;
                        case LEFT:
                            _Direction = Direction.UP;
                            break;
                    }
                } else {
                    switch (_Direction) {
                        case UP:
                            _Direction = Direction.LEFT;
                            break;
                        case LEFT:
                            _Direction = Direction.DOWN;
                            break;
                        case DOWN:
                            _Direction = Direction.RIGHT;
                            break;
                        case RIGHT:
                            _Direction = Direction.UP;
                            break;
                    }
                }
        }
        return true;
    }
}
