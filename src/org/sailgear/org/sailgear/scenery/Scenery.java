/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sailgear.scenery;

import com.jme3.asset.AssetManager;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.BloomFilter;
import com.jme3.post.filters.DepthOfFieldFilter;
import com.jme3.post.filters.LightScatteringFilter;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.texture.Texture2D;
import com.jme3.util.SkyFactory;
import com.jme3.water.WaterFilter;

/**
 *
 * @author dakk
 */
public class Scenery {
    private Node sceneryNode;
    private DirectionalLight sun;
    private WaterFilter water;
    
    public Node getSceneNode () {
        return sceneryNode;
    }
            
    public Scenery (Node rootNode, AssetManager assetManager, ViewPort viewPort) {
        sceneryNode = new Node ("Main Scene");
        rootNode.attachChild (sceneryNode);
        
        /* Create the sun */
        sun = new DirectionalLight();
        sun.setDirection (new Vector3f (-4.9236743f, -1.27054665f, 5.896916f));
        sun.setColor (ColorRGBA.White.clone().multLocal(1f));
        sceneryNode.addLight (sun);
        
        AmbientLight al = new AmbientLight();
        al.setColor (new ColorRGBA (0.1f, 0.1f, 0.1f, 1.0f));
        sceneryNode.addLight(al);
        
        
        Spatial sky = SkyFactory.createSky(assetManager, "Scenes/Beach/FullskiesSunset0068.dds", false);
        sky.setLocalScale(350);
        sceneryNode.attachChild(sky);
        
        
        //Water Filter
        water = new WaterFilter(rootNode, new Vector3f (-4.9236743f, -1.27054665f, 5.896916f));
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
        water.setWaterHeight(90f);
        
        FilterPostProcessor fpp = new FilterPostProcessor(assetManager);
        
        fpp.addFilter(water);
        viewPort.addProcessor(fpp);
        
        
    }
    
}
