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
package org.sailgear;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.plugins.FileLocator;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.material.Material;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import com.jme3.terrain.geomipmap.TerrainQuad;
import java.util.logging.Logger;
import org.sailgear.boat.Boat;
import org.sailgear.boat.BoatFactory;
import org.sailgear.boat.controls.FloatControl;
import org.sailgear.scenery.Scenery;

/**
 *
 * @author dakk
 */
public class Sailgear extends SimpleApplication {
    private TerrainQuad terrain;
    private Material matRock;
    private Scenery scene;
    private GameState gameState;
    private static final Logger logger = Logger.getLogger(Sailgear.class.getName());
    private BulletAppState bulletAppState;
    
    @Override
    public void simpleInitApp() {
        setDisplayFps(true);
        setDisplayStatView(false);
        
        /* Create or load the gamestate */
        gameState = new GameState ();
        gameState.camera = cam;
        
        /* Load the scenery */
        scene = new Scenery (assetManager, viewPort);
        scene.load (gameState);
        rootNode.attachChild (scene);
        rootNode.addControl(scene);

        
        /* Camera setup */
        flyCam.setMoveSpeed(500);
        cam.setLocation(new Vector3f(-370.31592f, 182.04016f, 196.81192f));
        cam.setRotation(new Quaternion(0.015302252f, 0.9304095f, -0.039101653f, 0.3641086f));
        cam.setFrustumFar(40000);
        
        assetManager.registerLocator("/home/dakk/Repositories/MyRepos/Sailgear/assets", FileLocator.class);
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
        
        BoatFactory boatFactory = new BoatFactory (assetManager, bulletAppState, scene);
        Boat b = boatFactory.addStaticBoat ("Default");
        b.setLocalTranslation(new Vector3f(-370.31592f, 20.0f, 196.81192f));
        scene.attachChild (b);
    }
    
    public Spatial teapot;
    
    @Override
    public void simpleUpdate(float tpf) {
        super.simpleUpdate(tpf);   
        //scene.update (tpf);        
    }
}
