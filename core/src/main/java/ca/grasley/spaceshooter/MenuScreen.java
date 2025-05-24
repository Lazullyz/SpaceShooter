package ca.grasley.spaceshooter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.utils.Align;
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

    // Animação do fundo
    private Animation<TextureRegion> backgroundAnimation;
    private float elapsedTime = 0f;
    private static final int NUM_FRAMES = 11; // <-- Quantidade de frames da animação

    public MenuScreen(MyGame game) {
        this.game = game;

        // Câmera e viewport
        camera = new OrthographicCamera();
        viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        viewport.apply();
        camera.position.set(WORLD_WIDTH / 2, WORLD_HEIGHT / 2, 0);
        camera.update();

        batch = new SpriteBatch();

        // Fonte Venice
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("VeniceClassic.ttf"));

        FreeTypeFontGenerator.FreeTypeFontParameter titleParams = new FreeTypeFontGenerator.FreeTypeFontParameter();
        titleParams.size = 100;
        titleParams.color = Color.WHITE;
        titleFont = generator.generateFont(titleParams);

        FreeTypeFontGenerator.FreeTypeFontParameter menuParams = new FreeTypeFontGenerator.FreeTypeFontParameter();
        menuParams.size = 50;
        menuParams.color = Color.WHITE;
        menuFont = generator.generateFont(menuParams);

        generator.dispose();

        // Animação do fundo
        TextureRegion[] frames = new TextureRegion[NUM_FRAMES];
        for (int i = 1; i <= NUM_FRAMES; i++) {
            String filename = String.format("menu_bg/frame-%02d.gif", i);
            Texture texture = new Texture(Gdx.files.internal(filename));
            frames[i - 1] = new TextureRegion(texture);
        }

        backgroundAnimation = new Animation<>(0.2f, frames);
        backgroundAnimation.setPlayMode(Animation.PlayMode.LOOP);
    }

    @Override
    public void render(float delta) {
        elapsedTime += delta;

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(camera.combined);

        TextureRegion currentFrame = backgroundAnimation.getKeyFrame(elapsedTime);

        batch.begin();
        // Desenha fundo animado
        batch.draw(currentFrame, 0, 0, WORLD_WIDTH, WORLD_HEIGHT);

        // Título
        titleFont.draw(batch, "Water Guardians",
            0,
            WORLD_HEIGHT / 2 + 100,
            WORLD_WIDTH,
            Align.center,
            false);

        // Texto do menu
        menuFont.draw(batch, "Pressione para Jogar",
            0,
            WORLD_HEIGHT / 2 - 50,
            WORLD_WIDTH,
            Align.center,
            false);
        batch.end();

        // Verifica entrada
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) || Gdx.input.justTouched()) {
            game.setScreen(new LevelSelectScreen(game));
            dispose();
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

        // Libera os frames da animação
        for (TextureRegion frame : backgroundAnimation.getKeyFrames()) {
            frame.getTexture().dispose();
        }
    }

    @Override public void show() {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
}
