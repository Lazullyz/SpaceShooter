package ca.grasley.spaceshooter;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.Input;

import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;

public class CutsceneScreen implements Screen {
    private final MyGame game;
    private SpriteBatch batch;
    private Animation<TextureRegion> cutsceneAnimation;
    private float elapsedTime = 0f;
    private Sound finalSound, seaSound;

    private static final int NUM_FRAMES = 3;

    private float fadeTime = 2f;
    private float fadeElapsed = 0f;
    private boolean fadeCompleted = false;

    private Texture whitePixel;

    private BitmapFont infoFont;

    private float minWatchTime = 5f;
    private boolean canExit = false;

    public CutsceneScreen(MyGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        finalSound = Gdx.audio.newSound(Gdx.files.internal("final_song.wav"));
        seaSound = Gdx.audio.newSound(Gdx.files.internal("som_mar.wav"));
        finalSound.play();
        seaSound.play();

        TextureRegion[] frames = new TextureRegion[NUM_FRAMES];
        for (int i = 0; i < NUM_FRAMES; i++) {
            Texture texture = new Texture(Gdx.files.internal("cutscenes_frames/cutscene_" + i + ".png"));
            frames[i] = new TextureRegion(texture);
        }

        cutsceneAnimation = new Animation<>(0.5f, frames);
        cutsceneAnimation.setPlayMode(Animation.PlayMode.LOOP);

        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        whitePixel = new Texture(pixmap);
        pixmap.dispose();

        // Fonte estilizada Venice
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("VeniceClassic.ttf"));
        FreeTypeFontParameter params = new FreeTypeFontParameter();
        params.size = 48;
        params.color = Color.WHITE;
        params.borderWidth = 2;
        params.borderColor = Color.BLACK;
        infoFont = generator.generateFont(params);
        generator.dispose();

        elapsedTime = 0;
        fadeElapsed = 0;
        fadeCompleted = false;
        canExit = false;
    }

    @Override
    public void render(float delta) {
        if (!fadeCompleted) {
            fadeElapsed += delta;
            if (fadeElapsed >= fadeTime) {
                fadeElapsed = fadeTime;
                fadeCompleted = true;
                elapsedTime = 0;
            }
        } else {
            elapsedTime += delta;
        }

        if (!canExit && elapsedTime >= minWatchTime) {
            canExit = true;
        }

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();

        if (fadeCompleted) {
            TextureRegion currentFrame = cutsceneAnimation.getKeyFrame(elapsedTime);
            batch.setColor(1, 1, 1, 1f);
            batch.draw(currentFrame, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        } else {
            float alphaCutscene = fadeElapsed / fadeTime;
            TextureRegion currentFrame = cutsceneAnimation.getKeyFrame(0);
            batch.setColor(1, 1, 1, alphaCutscene);
            batch.draw(currentFrame, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

            float alphaBlack = 1 - alphaCutscene;
            batch.setColor(0, 0, 0, alphaBlack);
            batch.draw(whitePixel, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        }

        if (canExit) {
            String msg = "Clique para sair";
            GlyphLayout layout = new GlyphLayout(infoFont, msg);
            float x = (Gdx.graphics.getWidth() - layout.width) / 2;
            float y = 850;
            infoFont.draw(batch, msg, x, y);
        }

        batch.setColor(1, 1, 1, 1);
        batch.end();

        if (canExit && (Gdx.input.justTouched() || Gdx.input.isKeyJustPressed(Input.Keys.ANY_KEY))) {
            game.setScreen(new LevelSelectScreen(game));
            dispose();
            finalSound.stop();
            seaSound.stop();
        }
    }

    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        batch.dispose();
        whitePixel.dispose();
        infoFont.dispose();
        for (TextureRegion frame : cutsceneAnimation.getKeyFrames()) {
            frame.getTexture().dispose();
        }
        finalSound.dispose();
        seaSound.dispose();
    }
}
