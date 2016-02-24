package mx.itesm.plataformas;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import mx.itesm.plataformas.Plataforma;

public class AndroidLauncher extends AndroidApplication {
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		// Deshabilita el acelerómetro y brújula para ahorrar batería
		config.useAccelerometer = false;
		config.useCompass = false;
		initialize(new Plataforma(), config);
	}
}
