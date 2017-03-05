/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sailgear.scenery;

import com.jme3.asset.AssetManager;
import com.jme3.asset.plugins.FileLocator;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.BloomFilter;
import com.jme3.post.filters.DepthOfFieldFilter;
import com.jme3.post.filters.FXAAFilter;
import com.jme3.post.filters.LightScatteringFilter;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;
import com.jme3.texture.Texture2D;
import com.jme3.util.SkyFactory;
import com.jme3.water.WaterFilter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import jme3utilities.Misc;
import jme3utilities.sky.SkyControl;
import jme3utilities.sky.SunAndStars;
import org.sailgear.GameState;
import org.sailgear.Sailgear;

/**
 *
 * @author dakk
 */
public class Scenery extends Node implements Control {
    private SkyControl sky;
    private static final Logger logger = Logger.getLogger(Scenery.class.getName());
    public WaterFilter water;
    private AssetManager assetManager;
    private ViewPort viewPort;
    private Terrain terrain;
    private Atmosphere atmosphere;
                
    public Scenery (AssetManager assetManager, ViewPort viewPort) {
        super ("SceneryNode");
        this.viewPort = viewPort;
        this.assetManager = assetManager;
    }

    public void load (GameState gameState) {        
        logger.log(Level.INFO, "Loading scenery");
        
        FilterPostProcessor fpp = new FilterPostProcessor(assetManager);
        
        /* Create the atmosphere */
        atmosphere = new Atmosphere (assetManager, gameState.camera, this);
        this.addControl(atmosphere);
        atmosphere.load (gameState);
                
        /* Load the terrain */
        terrain = new Terrain (assetManager, viewPort);
        terrain.load (gameState);
        this.attachChild (terrain);
        
        /* Create the water */
        water = new WaterFilter(this, Vector3f.ZERO);
        water.setWaterColor(new ColorRGBA().setAsSrgb(0.0078f, 0.3176f, 0.5f, 1.0f));
        water.setDeepWaterColor(new ColorRGBA().setAsSrgb(0.0039f, 0.00196f, 0.145f, 1.0f));
        water.setUnderWaterFogDistance(80);
        water.setWaterTransparency(0.12f);
        water.setFoamIntensity(0.4f);        
        water.setFoamHardness(0.3f);
        water.setFoamExistence(new Vector3f(0.8f, 8f, 1f));
        water.setReflectionDisplace(50);
        water.setRefractionConstant(0.25f);
        water.setColorExtinction(new Vector3f(30, 50, 70));
        water.setCausticsIntensity(0.4f);        
        water.setWaveScale(0.003f);
        water.setMaxAmplitude(2f);
        water.setFoamTexture((Texture2D) assetManager.loadTexture("Common/MatDefs/Water/Textures/foam2.jpg"));
        water.setRefractionStrength(0.2f);
        water.setWaterHeight(1f);
        
        
        /*BloomFilter bloom = new BloomFilter();        
        bloom.setExposurePower(55);
        bloom.setBloomIntensity(1.0f);*/
        
        //Light Scattering Filter
        LightScatteringFilter lsf = new LightScatteringFilter(new Vector3f (-4.9236743f, -1.27054665f, 5.896916f).mult(-300));
        lsf.setLightDensity(0.5f);   
        
        //Depth of field Filter
        DepthOfFieldFilter dof = new DepthOfFieldFilter();
        dof.setFocusDistance(0);
        dof.setFocusRange(100);
        
        
        BloomFilter bloom = new BloomFilter(BloomFilter.GlowMode.Objects);
        bloom.setBlurScale(2.5f);
        bloom.setExposurePower(1f);
        atmosphere.getUpdater().addBloomFilter(bloom);
        
        fpp.addFilter(water);
        fpp.addFilter(bloom);
        //fpp.addFilter(dof);
        fpp.addFilter(lsf);
        fpp.addFilter(new FXAAFilter());
        viewPort.addProcessor(fpp);    
    }
    
    private float hour = 0.0f;
    
    @Override
    public void update (float tpf) {
        water.setLightDirection(atmosphere.getSunAndStars().getSunDirection());
        SunAndStars sns = atmosphere.getSunAndStars();
        sns.setHour(hour);
        
        hour += 0.01;
        if (hour >= 24.0)
            hour = 0.0f;
    }

    @Override
    public Control cloneForSpatial(Spatial spatial) {
        return null;
    }

    @Override
    public void setSpatial(Spatial spatial) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void render(RenderManager rm, ViewPort vp) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
