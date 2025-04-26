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
import java.util.LinkedList;
import java.util.ListIterator;


public class GameScreen implements Screen {
    private final float WORLD_WIDTH = 1280;
    private final float WORLD_HEIGHT = 720;
    private final int TRASH_TO_WIN = 30;
    private final boolean DEBUG_MODE = false;

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
    private Music musicaDeFundo;
    private Sound coletaLixoSound;
    private Sound danoSound;
    private Sound gameOverSound;
    private Sound playerWonSound;

    private final float TEMPO_LIMITE = 60f;  // Tempo máximo para coletar os lixos
    private float tempoRestante = TEMPO_LIMITE;



    public GameScreen() {
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
            boatWidth, boatHeight,
            300,
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
                resetGame();
            }
            return; // Sai do update para não atualizar mais nada
        }

        // Jogo rolando normalmente:
        gameTime += delta;
        tempoRestante -= delta;

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
                Math.max(0, playerBoat.boundingBox.x - speed),
                playerBoat.boundingBox.y
            );
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            playerBoat.updatePosition(
                Math.min(WORLD_WIDTH - playerBoat.boundingBox.width, playerBoat.boundingBox.x + speed),
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
        if (gameTime % 1f < delta) {
            float size = WORLD_WIDTH / 12f;
            trashList.add(new Trash(
                (float)Math.random() * (WORLD_WIDTH - size),
                WORLD_HEIGHT,
                size, size,
                textureAtlas.findRegion(Math.random() > 0.5 ? "Lixo01" : "Lixo02")
            ));
        }

        //if (gameTime % 1.5f < delta) {
        float spawnInterval = Math.max(0.5f, 2f - (gameTime / 30f));
        if (gameTime % spawnInterval < delta) {
            float size = WORLD_WIDTH / 9f;
            String[] types = {"Tronco", "Metal", "RedePesca"};
            String type = types[(int)(Math.random() * 3)];
            obstacleList.add(new Obstacle(
                (float)Math.random() * (WORLD_WIDTH - size),
                WORLD_HEIGHT,
                size, size,
                textureAtlas.findRegion(type),
                type
            ));
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
                coletaLixoSound.play();
            }
        }

        ListIterator<Obstacle> obstacleIter = obstacleList.listIterator();
        while (obstacleIter.hasNext()) {
            Obstacle obstacle = obstacleIter.next();
            if (playerBoat.intersects(obstacle.collisionBox)) {
                obstacleIter.remove();
                lives--;
                danoSound.play();
            }
        }
    }

    private void checkGameEnd() {
        if (tempoRestante <= 0) {
            gameOver = true;
            musicaDeFundo.stop();
            gameOverSound.play();
        }
        if (lives <= 0){
            gameOver = true;
            musicaDeFundo.stop();
            gameOverSound.play();
        }
        if (collectedTrash >= TRASH_TO_WIN) {
            playerWon = true;
            gameOver = true;
            playerWonSound.play();
        }
        if (playerWon) {
            musicaDeFundo.stop();
        }
    }

    private void resetGame() {
        gameOver = false;
        playerWon = false;
        lives = 3;
        collectedTrash = 0;
        gameTime = 0;
        tempoRestante = TEMPO_LIMITE;

        trashList.clear();
        obstacleList.clear();

        playerBoat.updatePosition(WORLD_WIDTH / 2 - playerBoat.boundingBox.width / 2, WORLD_HEIGHT / 10);

        musicaDeFundo.play();
    }


    private void draw() {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        for (int i = 0; i < 4; i++) {
            float offset = (gameTime * (i + 1) * 50) % WORLD_HEIGHT;
            batch.draw(textureAtlas.findRegion("fase 3"),
                0, -offset, WORLD_WIDTH, WORLD_HEIGHT * 2);
        }
        batch.end();

        batch.begin();
        for (Trash trash : trashList) trash.draw(batch);
        for (Obstacle obstacle : obstacleList) obstacle.draw(batch);
        playerBoat.draw(batch);
        batch.end();

        batch.begin();
        font.draw(batch, "TEMPO:" + (int)tempoRestante, WORLD_WIDTH / 2, WORLD_HEIGHT - 20, 0, Align.center, false);

        font.draw(batch, "Lixo: " + collectedTrash + "/" + TRASH_TO_WIN, 20, WORLD_HEIGHT - 20);
        font.draw(batch, "Vidas: " + lives, WORLD_WIDTH - 20, WORLD_HEIGHT - 20, 0, Align.right, false);
        if (gameOver) {
            String Mainmsg = playerWon ? "VITORIA!" : "GAME OVER";
            font.draw(batch, Mainmsg, WORLD_WIDTH/2, WORLD_HEIGHT/2 + 90, 0, Align.center, false);
        if (gameOver) {
            String restartmsg = "click to restart";
            font.draw(batch, restartmsg, WORLD_WIDTH/2, WORLD_HEIGHT/2 -50, 0, Align.center, false);
        }
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
    }

    @Override public void pause() {
    }
    @Override public void resume() {
    }
    @Override public void hide() {
    }
    @Override public void show() {
            musicaDeFundo = Gdx.audio.newMusic(Gdx.files.internal("Wiz Khalifa - Black and Yellow (Instrumental).mp3"));
            musicaDeFundo.setVolume(0.2f);
            musicaDeFundo.setLooping(true);
            musicaDeFundo.play();
            coletaLixoSound= Gdx.audio.newSound(Gdx.files.internal("som.coleta.wav"));
            danoSound = Gdx.audio.newSound(Gdx.files.internal("som.dano.wav"));
            gameOverSound = Gdx.audio.newSound(Gdx.files.internal("som.gameover.wav"));
            playerWonSound = Gdx.audio.newSound(Gdx.files.internal("som.vitoria.mp3"));
        }

    }
