/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sailgear.scenery;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Node;
import com.jme3.terrain.geomipmap.TerrainGrid;
import com.jme3.terrain.geomipmap.TerrainGridListener;
import com.jme3.terrain.geomipmap.TerrainGridLodControl;
import com.jme3.terrain.geomipmap.TerrainLodControl;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.jme3.terrain.geomipmap.lodcalc.DistanceLodCalculator;
import com.jme3.terrain.heightmap.AbstractHeightMap;
import com.jme3.terrain.heightmap.RawHeightMap;
import com.jme3.texture.Texture;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.sailgear.GameState;

/**
 *
 * @author dakk
 */
public class Terrain extends Node {
    private static final Logger logger = Logger.getLogger(Terrain.class.getName());
    private TerrainGrid terrain;
    private TerrainTileLoader tileLoader;
    
    public Terrain () {
        super ("SceneryTerrainNode");
    }
    
    public void load (AssetManager assetManager, GameState gameState) {
        this.tileLoader = new TerrainTileLoader (assetManager, gameState.position);
        this.terrain = new TerrainGrid ("terrainGrid", 1025, 2049, tileLoader);
        
        this.terrain.addListener(new TerrainGridListener() {
            @Override
            public void gridMoved(Vector3f newCenter) {
                logger.log(Level.INFO, "Grid moved: " + newCenter.toString());
            }

            @Override
            public void tileAttached(Vector3f cell, TerrainQuad quad) {
                logger.log(Level.INFO, "Grid tileAttached: " + cell.toString());                
            }

            @Override
            public void tileDetached(Vector3f cell, TerrainQuad quad) {
                logger.log(Level.INFO, "Grid tileDetached: " + cell.toString());
                
            }
        });
        
        this.attachChild (terrain); 
        
        TerrainLodControl control = new TerrainGridLodControl(this.terrain, gameState.camera);
        control.setLodCalculator( new DistanceLodCalculator(257, 2.7f) );
        this.terrain.addControl(control);
        this.terrain.setLocalScale (new Vector3f(5, 0.2f, 5));
    }
}
