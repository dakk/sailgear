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
package org.sailgear.scenery;

import com.jme3.asset.AssetManager;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.BloomFilter;
import com.jme3.renderer.Camera;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.util.SkyFactory;
import java.util.logging.Logger;
import jme3utilities.sky.SkyControl;
import jme3utilities.sky.SunAndStars;
import org.sailgear.GameState;
import org.sailgear.Sailgear;

/**
 * Handle sun, moon, sky, fog, clouds, rain getting real data from weather
 * @author dakk
 */
public class Atmosphere extends SkyControl {
    private static final Logger logger = Logger.getLogger(Atmosphere.class.getName());
    private DirectionalLight sun;
    private AssetManager assetManager;
    private ViewPort viewPort;
    private Node rootNode;
    
    public Atmosphere (AssetManager assetManager, Camera cam, Node rootNode) {
        super (assetManager, cam, 0.9f, true, true);
        this.rootNode = rootNode;

        FilterPostProcessor fpp = new FilterPostProcessor(assetManager);

        sun = new DirectionalLight();
        sun.setColor (ColorRGBA.White.clone().multLocal(1f));
        rootNode.addLight (sun);
       
        SunAndStars sns = this.getSunAndStars();
        sns.setHour(24f);
        sns.setObserverLatitude(37.4046f * FastMath.DEG_TO_RAD);
        sns.setSolarLongitude(2, 10);
        this.setCloudiness(1f);
        this.setCloudRate(5.0f);
        //this.setEnabled(true);
        sun.setDirection(this.getSunAndStars().getSunDirection());
        
        rootNode.addLight (sun);
        this.getUpdater().setMainLight(sun);
    }
    
    public void load (GameState gameState) {
        this.setEnabled(true);
    }    
}
