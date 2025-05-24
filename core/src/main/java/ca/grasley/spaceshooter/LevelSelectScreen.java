package ca.grasley.spaceshooter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

public class LevelSelectScreen implements Screen {
    private MyGame game;
    private SpriteBatch batch;
    private BitmapFont titleFont, levelFont, lockedFont;

    public LevelSelectScreen(MyGame game) {
        this.game = game;
        batch = new SpriteBatch();
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("EdgeOfTheGalaxyRegular-OVEa6.otf"));

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
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.2f, 1);

        batch.begin();
        titleFont.draw(batch, "Selecione a Fase", Gdx.graphics.getWidth()/2 - 180, Gdx.graphics.getHeight() - 100);

        for (int i = 1; i <= 3; i++) {
            float yPos = Gdx.graphics.getHeight()/2 - (i-1)*100;

            if (game.isLevelUnlocked(i)) {
                levelFont.draw(batch, "Fase " + i, Gdx.graphics.getWidth()/2 - 50, yPos);

                if (Gdx.input.justTouched()) {
                    float touchY = Gdx.graphics.getHeight() - Gdx.input.getY();
                    if (touchY > yPos - 30 && touchY < yPos + 30) {
                        game.setScreen(new GameScreen(game, i));
                    }
                }
            } else {
                lockedFont.draw(batch, "Fase " + i + " (Bloqueada)", Gdx.graphics.getWidth()/2 - 120, yPos);
            }
        }
        batch.end();

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(new MenuScreen(game));
        }
    }

    @Override
    public void dispose() {
        batch.dispose();
        titleFont.dispose();
        levelFont.dispose();
        lockedFont.dispose();
    }

    @Override public void show() {}
    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
}
