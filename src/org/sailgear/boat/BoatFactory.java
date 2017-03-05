package org.sailgear.boat;

import com.jme3.app.state.AppState;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import java.util.ArrayList;
import org.sailgear.boat.controls.BoatControl;
import org.sailgear.boat.controls.FloatControl;
import org.sailgear.boat.controls.StaticBoatControl;
import org.sailgear.scenery.Scenery;

/*
 * The MIT License
 *
 * Copyright 2017 dakk.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

/**
 *
 * @author dakk
 */
public class BoatFactory {
    private final AssetManager assetManager;
    private final BulletAppState appState;
    private final Scenery scene;
    private ArrayList<Boat> boatList = new ArrayList<Boat> ();
    
    public BoatFactory(AssetManager assetManager, BulletAppState bulletAppState, Scenery scene) {
        this.assetManager = assetManager;
        this.appState = bulletAppState;
        this.scene = scene;
    }
    
    public Boat addStaticBoat (String name) {
        Boat boat = new Boat (assetManager, name);
        appState.getPhysicsSpace ().add (boat.getBody ());
        
        BoatControl bc = new StaticBoatControl ();
        boat.addControl (bc);
        
        FloatControl fl = new FloatControl ();
        fl.setWater (scene.water);
        fl.setVerticalOffset(3.7f);
	fl.setWidth(50);
        fl.setLength(100);
        fl.setHeight(4);
        boat.addControl (fl);

        boatList.add (boat);
        
        return boat;
    }
}
