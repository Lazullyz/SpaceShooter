package ca.grasley.spaceshooter;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.Input;


public class CutsceneScreen implements Screen {
    private final MyGame game;
    private SpriteBatch batch;
    private Animation<TextureRegion> cutsceneAnimation;
    private float elapsedTime = 0f;
    private Sound finalSound, seaSound;

    private static final int NUM_FRAMES = 3;

    private float fadeTime = 2f; // duração do fade out/in (segundos)
    private float fadeElapsed = 0f;
    private boolean fadeCompleted = false;

    private Texture whitePixel; // textura 1x1 branca para desenhar o retângulo preto

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

        Pixmap pixmap = new Pixmap(1,1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        whitePixel = new Texture(pixmap);
        pixmap.dispose();
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

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();

        if (fadeCompleted) {

            TextureRegion currentFrame = cutsceneAnimation.getKeyFrame(elapsedTime);
            batch.setColor(1, 1, 1, 1f);
            batch.draw(currentFrame, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        } else {

            float alphaCutscene = fadeElapsed / fadeTime;   // 0 -> 1
            TextureRegion currentFrame = cutsceneAnimation.getKeyFrame(0); // frame inicial
            batch.setColor(1, 1, 1, alphaCutscene);
            batch.draw(currentFrame, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());


            float alphaBlack = 1 - alphaCutscene;
            batch.setColor(0, 0, 0, alphaBlack);
            batch.draw(whitePixel, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        }

        batch.setColor(1, 1, 1, 1);

        batch.end();

        if (Gdx.input.isTouched() || Gdx.input.isKeyJustPressed(Input.Keys.ANY_KEY)) {
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
        for (TextureRegion frame : cutsceneAnimation.getKeyFrames()) {
            frame.getTexture().dispose();
        }
    }
}
