package com.vsl700.joyremote;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.vsl700.joyremote.gui.Button;
import com.vsl700.joyremote.gui.GUIRenderer;
import com.vsl700.joyremote.gui.Stick;
import com.vsl700.joyremote.gui.TextPanel;

public class MyGdxGame extends ApplicationAdapter implements GUIRenderer {
	SpriteBatch batch;
	ShapeRenderer shape;
	OrthographicCamera cam;
	BitmapFont font;

	Color primaryColor, secondaryColor;

	Stick joystick;
	Button btnA, btnB, btnC;
	Button btnP, btnN;
	Button btnLightDark;
	TextPanel connectionPortText;
	Button connectButton;

	public static final float GUI_SCALE = 2;

	
	@Override
	public void create () {
		primaryColor = Color.WHITE.cpy();
		secondaryColor = Color.BLACK.cpy();

		batch = new SpriteBatch();
		shape = new ShapeRenderer();
		shape.setAutoShapeType(true);
		cam = new OrthographicCamera();

		font = new BitmapFont();
		font.setColor(primaryColor);
		font.getData().setScale(2);

		joystick = new Stick(font, primaryColor, this);
		btnA = new Button("A", font, primaryColor, true, false, true, this);
		btnB = new Button("B", font, primaryColor, true, false, true, this);
		btnC = new Button("C", font, primaryColor, true, false, true, this);
		btnP = new Button("P", font, primaryColor, true, false, true, this);
		btnN = new Button("N", font, primaryColor, true, false, true, this);
		btnLightDark = new Button("L/D", font, primaryColor, true, false, this);
		connectionPortText = new TextPanel(font, primaryColor, primaryColor, 1000, 9999, this);
		connectButton = new Button("Connect", font, primaryColor, true, false, this);
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(secondaryColor.r, secondaryColor.g, secondaryColor.b, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		joystick.update();
		btnA.update();
		btnB.update();
		btnC.update();
		btnP.update();
		btnN.update();
		// btnLightDark.update();
		connectionPortText.update();

		if(btnLightDark.justTouched()){
			Color temp = primaryColor;
			primaryColor = secondaryColor;
			secondaryColor = temp;
		}

		joystick.draw();
		btnA.draw();
		btnB.draw();
		btnC.draw();
		btnP.draw();
		btnN.draw();
		btnLightDark.draw();
		connectionPortText.draw();
	}

	@Override
	public void resize(int width, int height){
		super.resize(width, height);

		cam.setToOrtho(false, width, height);
		joystick.setPosAndSize(80, 50, 480, 480);
		btnA.setPosAndSize(width - 320, 320, 120, 120);
		btnB.setPosAndSize(width - 450, 180, 120, 120);
		btnC.setPosAndSize(width - 260, 130, 120, 120);
		btnP.setPosAndSize(width / 2.0f - 240, 30, 90, 90);
		btnN.setPosAndSize(width / 2.0f + 120 + 45, 30, 90, 90);
		btnLightDark.setSize(60, 30);
		btnLightDark.setPos(width / 2.0f - btnLightDark.getWidth() / 2, 60);
		connectionPortText.setSize(200, 100);
		connectionPortText.setPos(width / 2.0f - connectionPortText.getWidth() / 2, height - connectionPortText.getHeight() - 10);
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		shape.dispose();
	}

	@Override
	public SpriteBatch getSpriteBatch() {
		return batch;
	}

	@Override
	public ShapeRenderer getShapeRenderer() {
		return shape;
	}

	@Override
	public OrthographicCamera getCam() {
		return cam;
	}
}
