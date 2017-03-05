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
import com.jme3.bounding.BoundingVolume;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.material.Material;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.terrain.geomipmap.TerrainGridTileLoader;
import com.jme3.terrain.geomipmap.TerrainQuad;
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
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import jme3tools.navigation.InvalidPositionException;
import jme3tools.navigation.MapModel3D;
import jme3tools.navigation.Position;

/**
 *
 * @author dakk
 */
public class TerrainTileLoader implements TerrainGridTileLoader {
    private static final Logger logger = Logger.getLogger(TerrainTileLoader.class.getName());
    private AssetManager assetManager;
    private int patchSize;
    private int quadSize;
    private MapModel3D mapModel;
    
    protected float[] loadSRTM (String filePath) throws FileNotFoundException, IOException {
        logger.log(Level.INFO, "Loading SRTM: " + filePath);
        
        File file = new File(filePath);
	DataInputStream dis = new DataInputStream(new FileInputStream(file));
	
	byte[] bytes = new byte[(int)file.length()];
	int bytesRead = dis.read(bytes);
	
	ByteBuffer byteBuffer = ByteBuffer.allocate(bytes.length);
        byteBuffer.put(bytes);

        float[] data = new float[1201 * 1201];
	int currentValue = 0;
	ByteBuffer convertBuffer = ByteBuffer.allocate(2);
	convertBuffer.order(ByteOrder.BIG_ENDIAN);

	byteBuffer.rewind();
	while (byteBuffer.hasRemaining()) {
	    short bigEndianValue = byteBuffer.getShort();
	    convertBuffer.putShort(bigEndianValue);
	    convertBuffer.order(ByteOrder.LITTLE_ENDIAN);

	    data[currentValue] = ((float) convertBuffer.getShort(0));

            if (data[currentValue] == -32768)
                data[currentValue] = -200;
            if (data[currentValue] == 32768)
                data[currentValue] = -200;
            if (data[currentValue] <= 0)
                data[currentValue] = -200;
                        
	    currentValue++;
            
	    convertBuffer.clear();
	    convertBuffer.rewind();
	}

        return data;
    }
    
    protected float[] scaleGeometry (float [] oldGeom, int oldSize, int size) {
        float[] newGeom = new float [size * size];
        
        float scalingFactor = (float) oldSize / (float) size;
        
        float jold;
        float iold = 0.0f;
        
        for (int i = 0; i < size; i++) {
            jold = 0.0f;
            for (int j = 0; j < size; j++) {
                float nval = oldGeom [(int) (iold * size) + (int) jold];
                //System.console().printf("%d %d %f\n", i * size + j, (int) (iold * oldSize) + (int) jold, nval);
                
                jold += scalingFactor;
                
                newGeom [i * size + j] = nval;
                
                if (j == size - 1) {
                    newGeom [i * size + j] = newGeom [i * size + j - 1];
                }
                
            }
            iold += scalingFactor;
           // System.console().printf("%d %f\n", i, iold);                
        }
        
        return newGeom;
    }
    
    protected static String getSRTMName (Position pos) {
        /* Evalute the tile name */
        int lat = (int) pos.getLatitude();
        int lon = (int) pos.getLongitude();
        String n = "";
        
        if (lat > 0)
            n += "N" + String.format("%02d", lat);
        else
            n += "S" + String.format("%03d", Math.abs (lat));
        
        if (lon > 0)
            n += "E" + String.format("%03d", lon);
        else
            n += "W" + String.format("%03d", Math.abs (lon));

        return n;
    }
    
    
    public TerrainTileLoader (AssetManager assetManager, Position center) {
        this.assetManager = assetManager;
        this.mapModel = new MapModel3D (1201 * 2);
        this.mapModel.setCentre (center);
    }
    
    
    @Override
    public TerrainQuad getTerrainQuadAt(Vector3f location) {
        Position gpsCoordinates = mapModel.toPosition (location);
        
        try {
            gpsCoordinates = new Position (39. - location.z, 9. + location.x);
        } catch (InvalidPositionException ex) {
            Logger.getLogger(TerrainTileLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
        String tileName = getSRTMName (gpsCoordinates);
        
        logger.log(Level.INFO, "Loading TILE: ".concat(location.toString()).concat(gpsCoordinates.toStringDec()));
        logger.log(Level.INFO, "Loading TILE: " + tileName);
        
        Material matRock;
        
        matRock = new Material(assetManager, "Common/MatDefs/Terrain/TerrainLighting.j3md");
        matRock.setBoolean("useTriPlanarMapping", false);
        matRock.setBoolean("WardIso", true);
        matRock.setTexture("AlphaMap", assetManager.loadTexture("Textures/Terrain/splat/alphamap2.png"));
        Texture grass = assetManager.loadTexture("Textures/Terrain/splat/grass.jpg");
        grass.setWrap(Texture.WrapMode.Repeat);
        matRock.setTexture("DiffuseMap", grass);
        matRock.setFloat("DiffuseMap_0_scale", 64);
        Texture dirt = assetManager.loadTexture("Textures/Terrain/splat/dirt.jpg");
        dirt.setWrap(Texture.WrapMode.Repeat);
        matRock.setTexture("DiffuseMap_1", dirt);
        matRock.setFloat("DiffuseMap_1_scale", 16);
        
        Texture normalMap0 = assetManager.loadTexture("Textures/Terrain/splat/grass_normal.jpg");
        normalMap0.setWrap(Texture.WrapMode.Repeat);
        Texture normalMap1 = assetManager.loadTexture("Textures/Terrain/splat/dirt_normal.png");
        normalMap1.setWrap(Texture.WrapMode.Repeat);
        matRock.setTexture("NormalMap", normalMap0);
        
        AbstractHeightMap heightmap = null;
        try {
            float[] srtmData = loadSRTM (("/home/dakk/Repositories/MyRepos/Sailgear/assets/Terrain/"+tileName+".hgt"));
            heightmap = new RawHeightMap (scaleGeometry(srtmData, 1201, 1024));
            //heightmap.smooth(24.0f, 5);
        } catch (Exception e) {
            float[] test = new float[1024*1024];
            Arrays.fill (test, -200.0f);
            heightmap = new RawHeightMap (test);
        }
        
        TerrainQuad tile;
        tile = new TerrainQuad("terrain", 1025, 1025, heightmap.getHeightMap());
        tile.setMaterial(matRock);
        //tile.setLocalScale(new Vector3f(1, 0.05f, 1));
        /*Quaternion q = new Quaternion();
        q.fromAngles(0.0f, 1.5708f * 2, 0.0f);
        tile.setLocalRotation(q);*/
        
        //tile.setLocalTranslation(new Vector3f(0, 0, 0));
        tile.setLocked (false); // unlock it so we can edit the height

        tile.setShadowMode(RenderQueue.ShadowMode.Receive);
        return tile;
    }

    @Override
    public void setPatchSize(int patchSize) {
        this.patchSize = patchSize;
    }

    @Override
    public void setQuadSize(int quadSize) {
        this.quadSize = quadSize;
    }

    @Override
    public void write(JmeExporter ex) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void read(JmeImporter im) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
