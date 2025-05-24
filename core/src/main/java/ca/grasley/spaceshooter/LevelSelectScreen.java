package ca.grasley.spaceshooter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;

public class LevelSelectScreen implements Screen {
    private MyGame game;
    private SpriteBatch batch;
    private BitmapFont titleFont, levelFont, lockedFont;
    private GlyphLayout titleLayout;
    private GlyphLayout[] levelLayouts = new GlyphLayout[3];
    private GlyphLayout[] lockedLayouts = new GlyphLayout[3];

    public LevelSelectScreen(MyGame game) {
        this.game = game;
        batch = new SpriteBatch();
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

        titleLayout = new GlyphLayout(titleFont, "Selecione a Fase");

        for (int i = 0; i < 3; i++) {
            levelLayouts[i] = new GlyphLayout(levelFont, "Fase " + (i+1));
            lockedLayouts[i] = new GlyphLayout(lockedFont, "Fase " + (i+1) + " (Bloqueada)");
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.2f, 1);

        batch.begin();

        float titleX = Gdx.graphics.getWidth() / 2f - titleLayout.width / 2;
        float titleY = Gdx.graphics.getHeight() - 100;
        titleFont.draw(batch, titleLayout, titleX, titleY);

        for (int i = 0; i < 3; i++) {
            float yPos = Gdx.graphics.getHeight() / 2f - i * 100;

            if (game.isLevelUnlocked(i+1)) {
                float levelX = Gdx.graphics.getWidth() / 2f - levelLayouts[i].width / 2;
                levelFont.draw(batch, levelLayouts[i], levelX, yPos);
            } else {
                float lockedX = Gdx.graphics.getWidth() / 2f - lockedLayouts[i].width / 2;
                lockedFont.draw(batch, lockedLayouts[i], lockedX, yPos);
            }
        }

        batch.end();

        if (Gdx.input.justTouched()) {
            float touchX = Gdx.input.getX();
            float touchY = Gdx.graphics.getHeight() - Gdx.input.getY();

            for (int i = 0; i < 3; i++) {
                if (game.isLevelUnlocked(i+1)) {
                    float levelX = Gdx.graphics.getWidth() / 2f - levelLayouts[i].width / 2;
                    float levelY = Gdx.graphics.getHeight() / 2f - i * 100;

                    if (touchX >= levelX && touchX <= levelX + levelLayouts[i].width &&
                        touchY >= levelY - levelLayouts[i].height && touchY <= levelY) {
                        game.setScreen(new GameScreen(game, i+1));
                    }
                }
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(new MenuScreen(game));
        }
    }

    @Override
    public void resize(int width, int height) {
        titleLayout.setText(titleFont, "Selecione a Fase");
        for (int i = 0; i < 3; i++) {
            levelLayouts[i].setText(levelFont, "Fase " + (i+1));
            lockedLayouts[i].setText(lockedFont, "Fase " + (i+1) + " (Bloqueada)");
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
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
}
