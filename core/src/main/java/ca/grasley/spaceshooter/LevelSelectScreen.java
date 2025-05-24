package ca.grasley.spaceshooter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class LevelSelectScreen implements Screen {
    private MyGame game;
    private SpriteBatch batch;
    private BitmapFont titleFont, levelFont, lockedFont;
    private GlyphLayout layout;

    private OrthographicCamera camera;
    private Viewport viewport;

    private float[] levelYPositions;
    private float levelXPosition;

    private Vector3 touchPos;

    private static final float WORLD_WIDTH = 800;
    private static final float WORLD_HEIGHT = 480;

    public LevelSelectScreen(MyGame game) {
        this.game = game;
        batch = new SpriteBatch();

        camera = new OrthographicCamera();
        viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        viewport.apply();

        camera.position.set(WORLD_WIDTH / 2f, WORLD_HEIGHT / 2f, 0);
        camera.update();

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("VeniceClassic.ttf"));

        FreeTypeFontGenerator.FreeTypeFontParameter titleParams = new FreeTypeFontGenerator.FreeTypeFontParameter();
        titleParams.size = 72;
        titleFont = generator.generateFont(titleParams);

        FreeTypeFontGenerator.FreeTypeFontParameter levelParams = new FreeTypeFontGenerator.FreeTypeFontParameter();
        levelParams.size = 50;
        levelFont = generator.generateFont(levelParams);

        FreeTypeFontGenerator.FreeTypeFontParameter lockedParams = new FreeTypeFontGenerator.FreeTypeFontParameter();
        lockedParams.size = 50;
        lockedParams.color = Color.GRAY;
        lockedFont = generator.generateFont(lockedParams);

        generator.dispose();

        levelYPositions = new float[3];
        levelXPosition = WORLD_WIDTH / 2f;
        layout = new GlyphLayout();

        touchPos = new Vector3();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        batch.setProjectionMatrix(camera.combined);

        batch.begin();

        // Desenha título centralizado
        String title = "Selecione a Fase";
        layout.setText(titleFont, title);
        titleFont.draw(batch, title, levelXPosition - layout.width / 2f, WORLD_HEIGHT - 80);

        float startY = WORLD_HEIGHT / 2f + 50;

        for (int i = 0; i < 3; i++) {
            int levelNum = i + 1;
            float yPos = startY - i * 100;
            levelYPositions[i] = yPos;

            String text;
            BitmapFont fontToUse;

            if (game.isLevelUnlocked(levelNum)) {
                text = "Fase " + levelNum;
                fontToUse = levelFont;
            } else {
                text = "Fase " + levelNum + " (Bloqueada)";
                fontToUse = lockedFont;
            }

            layout.setText(fontToUse, text);
            fontToUse.draw(batch, text, levelXPosition - layout.width / 2f, yPos);
        }

        batch.end();

        if (Gdx.input.justTouched()) {
            // Pega posição do toque e converte para mundo virtual
            touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos);

            float touchX = touchPos.x;
            float touchY = touchPos.y;

            for (int i = 0; i < 3; i++) {
                int levelNum = i + 1;
                if (!game.isLevelUnlocked(levelNum)) continue;

                float yPos = levelYPositions[i];
                String text = "Fase " + levelNum;

                layout.setText(levelFont, text);

                float leftX = levelXPosition - layout.width / 2f;
                float rightX = levelXPosition + layout.width / 2f;
                float topY = yPos + 20;
                float bottomY = yPos - 40;

                if (touchX >= leftX && touchX <= rightX && touchY >= bottomY && touchY <= topY) {
                    game.setScreen(new GameScreen(game, levelNum));
                    break;
                }
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(new MenuScreen(game));
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void dispose() {
        batch.dispose();
        titleFont.dispose();
        levelFont.dispose();
        lockedFont.dispose();
    }

    @Override public void show() {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
}
