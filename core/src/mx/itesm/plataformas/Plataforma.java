package mx.itesm.plataformas;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Plataforma extends Game
{
	public static final float ANCHO_CAMARA = 640;
	public static final float ALTO_CAMARA = 480;

	private final AssetManager assetManager = new AssetManager();

	@Override
	public void create() {

		setScreen(new PantallaJuego(this));
	}

	public AssetManager getAssetManager() {
		return assetManager;
	}
}
