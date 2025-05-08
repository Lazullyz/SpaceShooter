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

public class MenuScreen implements Screen {
    private MyGame game;
    private SpriteBatch batch;
    private BitmapFont titleFont, menuFont;
    private GlyphLayout titleLayout, menuLayout;

    public MenuScreen(MyGame game) {
        this.game = game;
        batch = new SpriteBatch();
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("VeniceClassic.ttf"));

        FreeTypeFontGenerator.FreeTypeFontParameter titleParams = new FreeTypeFontGenerator.FreeTypeFontParameter();
        titleParams.size = 100;
        titleFont = generator.generateFont(titleParams);

        FreeTypeFontGenerator.FreeTypeFontParameter menuParams = new FreeTypeFontGenerator.FreeTypeFontParameter();
        menuParams.size = 50;
        menuFont = generator.generateFont(menuParams);

        generator.dispose();

        titleLayout = new GlyphLayout();
        menuLayout = new GlyphLayout();

        titleLayout.setText(titleFont, "Water Guardians");
        menuLayout.setText(menuFont, "Pressione para Jogar");
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.2f, 1);

        batch.begin();

        float titleX = Gdx.graphics.getWidth() / 2f - titleLayout.width / 2;
        float titleY = Gdx.graphics.getHeight() / 2f + 100;
        titleFont.draw(batch, titleLayout, titleX, titleY);

        float menuX = Gdx.graphics.getWidth() / 2f - menuLayout.width / 2;
        float menuY = Gdx.graphics.getHeight() / 2f - 50;
        menuFont.draw(batch, menuLayout, menuX, menuY);

        batch.end();

        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) || Gdx.input.justTouched()) {
            game.setScreen(new LevelSelectScreen(game));
        }
    }

    @Override
    public void resize(int width, int height) {
        titleLayout.setText(titleFont, "Water Guardians");
        menuLayout.setText(menuFont, "Pressione para Jogar");
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
