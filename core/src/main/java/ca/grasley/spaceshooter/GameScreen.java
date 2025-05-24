package ca.grasley.spaceshooter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.awt.geom.Arc2D;
import java.util.LinkedList;
import java.util.ListIterator;

public class GameScreen implements Screen {
    private final float WORLD_WIDTH = 1280;
    private final float WORLD_HEIGHT = 720;
    private float LEFT_LIMIT;
    private float RIGHT_LIMIT;
    private int TRASH_TO_WIN;
    private float TIME_LIMIT;
    private final boolean DEBUG_MODE = false;

    private MyGame game;
    private int currentLevel;
    private OrthographicCamera camera;
    private Viewport viewport;
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private TextureAtlas textureAtlas;

    private Boat playerBoat;
    private LinkedList<Trash> trashList;
    private LinkedList<Obstacle> obstacleList;

    private int lives = 3;
    private int collectedTrash = 0;
    private float gameTime = 0;
    private boolean gameOver = false;
    private boolean playerWon = false;
    private BitmapFont font;
    private Music backgroundMusic;
    private Sound collectSound, damageSound, gameOverSound, victorySound;

    private float remainingTime;

    public GameScreen(MyGame game, int level) {
        this.game = game;
        this.currentLevel = level;

        // Configurações por fase
        switch(level) {
            case 1:
                TRASH_TO_WIN = 15;
                TIME_LIMIT = 30f;
                LEFT_LIMIT = WORLD_WIDTH * 0.20F;
                RIGHT_LIMIT = WORLD_WIDTH * 0.80F;
                break;
            case 2:
                TRASH_TO_WIN = 25;
                TIME_LIMIT = 40f;
                LEFT_LIMIT = WORLD_WIDTH * 0.15F;
                RIGHT_LIMIT = WORLD_WIDTH * 0.85F;
                break;
            case 3:
                TRASH_TO_WIN = 35;
                TIME_LIMIT = 40f;
                LEFT_LIMIT = 0;
                RIGHT_LIMIT = WORLD_WIDTH;
                break;
        }

        remainingTime = TIME_LIMIT;
        camera = new OrthographicCamera();
        viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        textureAtlas = new TextureAtlas("arts.atlas");

        initializeGameObjects();
        setupHUD();
    }

    private void initializeGameObjects() {
        float boatWidth = WORLD_WIDTH / 8f;
        float boatHeight = WORLD_HEIGHT / 5f;
        playerBoat = new Boat(
            WORLD_WIDTH / 2 - boatWidth / 2,
            WORLD_HEIGHT / 10,
            boatWidth, boatHeight, 450,
            textureAtlas.findRegion("SpriteBarco")
        );

        trashList = new LinkedList<>();
        obstacleList = new LinkedList<>();
    }

    private void setupHUD() {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("EdgeOfTheGalaxyRegular-OVEa6.otf"));
        FreeTypeFontGenerator.FreeTypeFontParameter params = new FreeTypeFontGenerator.FreeTypeFontParameter();
        params.size = 72;
        params.color = Color.WHITE;
        params.borderWidth = 3;
        params.borderColor = Color.BLACK;
        font = generator.generateFont(params);
        generator.dispose();
    }

    @Override
    public void render(float delta) {
        update(delta);
        draw();
    }

    private void update(float delta) {
        if (gameOver) {
            if (Gdx.input.justTouched()) {
                game.setScreen(new LevelSelectScreen(game));
            }
            return;
        }

        gameTime += delta;
        remainingTime -= delta;

        handleInput();
        spawnObjects(delta);
        updateObjects(delta);
        checkCollisions();
        checkGameEnd();
    }

    private void handleInput() {
        float speed = playerBoat.getMovementSpeed() * Gdx.graphics.getDeltaTime();

        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            playerBoat.updatePosition(
                Math.max(LEFT_LIMIT, playerBoat.boundingBox.x - speed),
                playerBoat.boundingBox.y
            );
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            playerBoat.updatePosition(
                Math.min(RIGHT_LIMIT - playerBoat.boundingBox.width, playerBoat.boundingBox.x + speed),
                playerBoat.boundingBox.y
            );
        }

        if (Gdx.input.isTouched()) {
            Vector2 touchPos = new Vector2(Gdx.input.getX(), Gdx.input.getY());
            viewport.unproject(touchPos);

            float targetX = touchPos.x - playerBoat.boundingBox.width / 2;
            targetX = Math.max(0, Math.min(WORLD_WIDTH - playerBoat.boundingBox.width, targetX));

            float currentX = playerBoat.boundingBox.x;
            float direction = Math.signum(targetX - currentX);

            if (Math.abs(targetX - currentX) > 5f) {
                playerBoat.updatePosition(
                    currentX + (speed * direction),
                    playerBoat.boundingBox.y
                );
            }
        }
    }

    private void spawnObjects(float delta) {
        float trashSpawnRate = 1f - (currentLevel * 0.1f);
        float obstacleSpawnRate = Math.max(0.5f, 2f - (gameTime / (30f - (currentLevel * 5f))));
        float obstacleX = 0;
        float trashX = 0;

        if (gameTime % obstacleSpawnRate < delta) {
            float size = WORLD_WIDTH / 9f;
            if (currentLevel == 1 || currentLevel == 2) {
                obstacleX = LEFT_LIMIT + (float) Math.random() * (RIGHT_LIMIT - LEFT_LIMIT - size);
            } else {
                obstacleX = (float) Math.random() * (WORLD_WIDTH - size);
            }
        }

        if (gameTime % trashSpawnRate < delta) {
            float size = WORLD_WIDTH / 12f;
            trashList.add(new Trash(
                trashX = LEFT_LIMIT + (float)Math.random() * (RIGHT_LIMIT -LEFT_LIMIT - size),
                WORLD_HEIGHT,
                size, size,
                textureAtlas.findRegion(Math.random() > 0.5 ? "Lixo01" : "Lixo02")
            ));
        }

        if (gameTime % obstacleSpawnRate < delta) {
            float size = WORLD_WIDTH / 9f;
            String type;

            switch(currentLevel) {
                case 1:
                    type = "Tronco";
                    break;
                case 2:
                    type = "Metal";
                    break;
                case 3:
                    String[] types = {"Tronco", "Metal", "RedePesca"};
                    type = types[(int)(Math.random() * types.length)];
                    break;
                default:
                    type = "Tronco";
            }

            Obstacle obstacle = new Obstacle(
                obstacleX,
                WORLD_HEIGHT,
                size, size,
                textureAtlas.findRegion(type),
                type
            );

            // Velocidade por fase: 250, 350, 450
            obstacle.setSpeed(250 + (currentLevel-1)*100);

            obstacleList.add(obstacle);
        }
    }

    private void updateObjects(float delta) {
        ListIterator<Trash> trashIter = trashList.listIterator();
        while (trashIter.hasNext()) {
            Trash trash = trashIter.next();
            trash.update(delta);
            if (trash.boundingBox.y + trash.boundingBox.height < 0) {
                trashIter.remove();
            }
        }

        ListIterator<Obstacle> obstacleIter = obstacleList.listIterator();
        while (obstacleIter.hasNext()) {
            Obstacle obstacle = obstacleIter.next();
            obstacle.update(delta);
            if (obstacle.boundingBox.y + obstacle.boundingBox.height < 0) {
                obstacleIter.remove();
            }
        }
    }

    private void checkCollisions() {
        ListIterator<Trash> trashIter = trashList.listIterator();
        while (trashIter.hasNext()) {
            Trash trash = trashIter.next();
            if (playerBoat.intersects(trash.collisionBox)) {
                trashIter.remove();
                collectedTrash++;
                collectSound.play();
            }
        }

        ListIterator<Obstacle> obstacleIter = obstacleList.listIterator();
        while (obstacleIter.hasNext()) {
            Obstacle obstacle = obstacleIter.next();
            if (playerBoat.intersects(obstacle.collisionBox)) {
                obstacleIter.remove();
                lives--;
                damageSound.play();
            }
        }
    }

    private void checkGameEnd() {
        if (remainingTime <= 0 || lives <= 0) {
            gameOver = true;
            backgroundMusic.stop();
            gameOverSound.play();
        }
        if (collectedTrash >= TRASH_TO_WIN) {
            playerWon = true;
            gameOver = true;
            victorySound.play();
            game.unlockNextLevel(currentLevel);
            backgroundMusic.stop();
        }
    }

    private void draw() {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        for (int i = 0; i < 4; i++) {
            float offset = (gameTime * (i + 1) * 50) % WORLD_HEIGHT;
            batch.draw(textureAtlas.findRegion("fase " + currentLevel),
                0, -offset, WORLD_WIDTH, WORLD_HEIGHT * 2);
        }
        batch.end();

        batch.begin();
        for (Trash trash : trashList) trash.draw(batch);
        for (Obstacle obstacle : obstacleList) obstacle.draw(batch);
        playerBoat.draw(batch);
        batch.end();

        batch.begin();
        font.draw(batch, "TEMPO: " + (int)remainingTime, WORLD_WIDTH / 2, WORLD_HEIGHT - 20, 0, Align.center, false);
        font.draw(batch, "Lixo: " + collectedTrash + "/" + TRASH_TO_WIN, 20, WORLD_HEIGHT - 20);
        font.draw(batch, "Vidas: " + lives, WORLD_WIDTH - 20, WORLD_HEIGHT - 20, 0, Align.right, false);

        if (gameOver) {
            String mainMsg = playerWon ? "VITORIA!" : "GAME OVER";
            font.draw(batch, mainMsg, WORLD_WIDTH/2, WORLD_HEIGHT/2 + 90, 0, Align.center, false);
            font.draw(batch, "Toque para voltar", WORLD_WIDTH/2, WORLD_HEIGHT/2 -50, 0, Align.center, false);
        }
        batch.end();

        if (DEBUG_MODE) drawDebug();
    }

    private void drawDebug() {
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.YELLOW);
        playerBoat.drawBoundingBox(shapeRenderer);
        shapeRenderer.setColor(Color.GREEN);
        playerBoat.drawDebug(shapeRenderer);

        for (Trash trash : trashList) {
            shapeRenderer.setColor(Color.YELLOW);
            trash.drawBoundingBox(shapeRenderer);
            shapeRenderer.setColor(Color.BLUE);
            trash.drawDebug(shapeRenderer);
        }

        for (Obstacle obstacle : obstacleList) {
            shapeRenderer.setColor(Color.YELLOW);
            obstacle.drawBoundingBox(shapeRenderer);
            shapeRenderer.setColor(Color.RED);
            obstacle.drawDebug(shapeRenderer);
        }
        shapeRenderer.end();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        camera.position.set(WORLD_WIDTH/2, WORLD_HEIGHT/2, 0);
        camera.update();
        float scale = Math.min(
            viewport.getWorldWidth() / WORLD_WIDTH,
            viewport.getWorldHeight() / WORLD_HEIGHT
        );
        font.getData().setScale(scale);
    }

    @Override
    public void dispose() {
        batch.dispose();
        shapeRenderer.dispose();
        textureAtlas.dispose();
        font.dispose();
        backgroundMusic.dispose();
        collectSound.dispose();
        damageSound.dispose();
        gameOverSound.dispose();
        victorySound.dispose();
    }

    @Override
    public void show() {
        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("Wiz Khalifa - Black and Yellow (Instrumental).mp3"));
        backgroundMusic.setVolume(0.2f);
        backgroundMusic.setLooping(true);
        backgroundMusic.play();

        collectSound = Gdx.audio.newSound(Gdx.files.internal("som.coleta.wav"));
        damageSound = Gdx.audio.newSound(Gdx.files.internal("som.dano.wav"));
        gameOverSound = Gdx.audio.newSound(Gdx.files.internal("som.gameover.wav"));
        victorySound = Gdx.audio.newSound(Gdx.files.internal("som.vitoria.mp3"));
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
}
