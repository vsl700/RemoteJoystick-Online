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

	public static final float GUI_SCALE = 1;

	
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

		joystick = new Stick(primaryColor, this);
		btnA = new Button("A", font, primaryColor, true, false, true, this);
		btnB = new Button("B", font, primaryColor, true, false, true, this);
		btnC = new Button("C", font, primaryColor, true, false, true, this);
		btnP = new Button("P", font, primaryColor, true, false, true, this);
		btnN = new Button("N", font, primaryColor, true, false, true, this);
		btnLightDark = new Button("L/D", font, primaryColor, true, false, this);
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
	}

	@Override
	public void resize(int width, int height){
		super.resize(width, height);

		cam.setToOrtho(false, width, height);
		joystick.setPosAndSize(80, 50, 120, 120);
		btnA.setPosAndSize(width - 160, 130, 60, 60);
		btnB.setPosAndSize(width - 230, 80, 60, 60);
		btnC.setPosAndSize(width - 150, 50, 60, 60);
		btnP.setPosAndSize(width / 2.0f - 120, 30, 45, 45);
		btnN.setPosAndSize(width / 2.0f + 70, 30, 45, 45);
		btnLightDark.setSize(60, 30);
		btnLightDark.setPos(width / 2 - btnLightDark.getWidth() / 2, 60);
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
