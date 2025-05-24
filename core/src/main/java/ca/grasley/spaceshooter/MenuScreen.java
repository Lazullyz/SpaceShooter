package ca.grasley.spaceshooter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class MenuScreen implements Screen {
    private MyGame game;
    private SpriteBatch batch;
    private BitmapFont titleFont, menuFont;

    private OrthographicCamera camera;
    private Viewport viewport;

    private final float WORLD_WIDTH = 1280;
    private final float WORLD_HEIGHT = 720;

    public MenuScreen(MyGame game) {
        this.game = game;

        // Configura câmera e viewport
        camera = new OrthographicCamera();
        viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        viewport.apply();
        camera.position.set(WORLD_WIDTH / 2, WORLD_HEIGHT / 2, 0);
        camera.update();

        batch = new SpriteBatch();
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("EdgeOfTheGalaxyRegular-OVEa6.otf"));

        FreeTypeFontGenerator.FreeTypeFontParameter titleParams = new FreeTypeFontGenerator.FreeTypeFontParameter();
        titleParams.size = 100;
        titleParams.color = Color.WHITE;
        titleFont = generator.generateFont(titleParams);

        FreeTypeFontGenerator.FreeTypeFontParameter menuParams = new FreeTypeFontGenerator.FreeTypeFontParameter();
        menuParams.size = 50;
        menuParams.color = Color.WHITE;
        menuFont = generator.generateFont(menuParams);

        generator.dispose();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        titleFont.draw(batch, "Water Guardians",
            0,
            WORLD_HEIGHT / 2 + 100,
            WORLD_WIDTH,
            com.badlogic.gdx.utils.Align.center,
            false);

        menuFont.draw(batch, "Pressione para Jogar",
            0,
            WORLD_HEIGHT / 2 - 50,
            WORLD_WIDTH,
            com.badlogic.gdx.utils.Align.center,
            false);
        batch.end();

        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) || Gdx.input.justTouched()) {
            game.setScreen(new LevelSelectScreen(game));
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        camera.position.set(viewport.getWorldWidth() / 2, viewport.getWorldHeight() / 2, 0);
        camera.update();
    }

    @Override
    public void dispose() {
        batch.dispose();
        titleFont.dispose();
        menuFont.dispose();
    }

    @Override public void show() {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
}
