/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sailgear;

import com.jme3.renderer.Camera;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import jme3tools.navigation.InvalidPositionException;
import jme3tools.navigation.Position;

/**
 *
 * @author dakk
 */
public class GameState implements java.io.Serializable {
	public Position position;
	public double time = 0.0;
	public double speed = 0.0;
	public double heading = 0.0;
	public String boat = "Default";	
        public Camera camera = null;
	
    public GameState () {
        try {
            this.position = new Position (39, 9);
        } catch (InvalidPositionException ex) {
            Logger.getLogger(GameState.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void save (String statePath) {
       try {
           FileOutputStream fileOut = new FileOutputStream(statePath);
           ObjectOutputStream out = new ObjectOutputStream(fileOut);
           out.writeObject(this);
           out.close();
           fileOut.close();
           System.out.printf("Serialized data is saved in /tmp/employee.ser");
       } catch(IOException i) {
           i.printStackTrace();
       } 
    }
    
    static public GameState load (String statePath) {
        GameState gs = null;
		try {
		     FileInputStream fileIn = new FileInputStream(statePath);
		     ObjectInputStream in = new ObjectInputStream(fileIn);
		     gs = (GameState) in.readObject();
		     in.close();
		     fileIn.close();
		     return gs;
		} catch (IOException i) {
		     i.printStackTrace();
		     return null;
		} catch (ClassNotFoundException c) {
		     System.out.println("GameState not found");
		     c.printStackTrace();
		     return null;
		}
    }
}
