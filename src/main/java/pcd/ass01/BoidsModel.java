package pcd.ass01;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class BoidsModel {
    
    private final List<Boid> boids;
    private double separationWeight; 
    private double alignmentWeight; 
    private double cohesionWeight; 
    private final double width;
    private final double height;
    private final double maxSpeed;
    private final double perceptionRadius;
    private final double avoidRadius;

    private boolean isModelPaused;

    private boolean boidsNumberChanged;
    private List<Boid> newBoids;

    public BoidsModel(int nboids,  
    						double initialSeparationWeight, 
    						double initialAlignmentWeight, 
    						double initialCohesionWeight,
    						double width, 
    						double height,
    						double maxSpeed,
    						double perceptionRadius,
    						double avoidRadius){
        this.separationWeight = initialSeparationWeight;
        this.alignmentWeight = initialAlignmentWeight;
        this.cohesionWeight = initialCohesionWeight;
        this.width = width;
        this.height = height;
        this.maxSpeed = maxSpeed;
        this.perceptionRadius = perceptionRadius;
        this.avoidRadius = avoidRadius;

        this.isModelPaused = true;
        this.boidsNumberChanged = false;
        this.newBoids = new ArrayList<>();
        
    	this.boids = initBoid(nboids);
    }

    private List<Boid> initBoid(int nboids) {
        var b = new ArrayList<Boid>();
        for (int i = 0; i < nboids; i++) {
            P2d pos = new P2d(-width/2 + Math.random() * width, -height/2 + Math.random() * height);
            V2d vel = new V2d(Math.random() * maxSpeed/2 - maxSpeed/4, Math.random() * maxSpeed/2 - maxSpeed/4);
            b.add(new Boid(pos, vel));
        }
        return b;
    }

//    public synchronized List<Boid> getBoids(){
//    	return boids;
//    }

    public List<Boid> getBoids() {
        return Collections.unmodifiableList(boids);
    }

    public void regenerateBoids(int nboids) {
        newBoids.clear();
        newBoids.addAll(initBoid(nboids));
        boidsNumberChanged = true;
    }

    public boolean isBoidsNumberChanged() {
        return boidsNumberChanged;
    }

    public void updateBoids() {
        this.boids.clear();
        this.boids.addAll(newBoids);
        boidsNumberChanged = false;
    }

    public /*synchronized*/ double getMinX() {
    	return -width/2;
    }

    public /*synchronized*/ double getMaxX() {
    	return width/2;
    }

    public /*synchronized*/ double getMinY() {
    	return -height/2;
    }

    public /*synchronized*/ double getMaxY() {
    	return height/2;
    }
    
    public /*synchronized*/ double getWidth() {
    	return width;
    }
 
    public /*synchronized*/ double getHeight() {
    	return height;
    }

    public synchronized void setSeparationWeight(double value) {
    	this.separationWeight = value;
    }

    public synchronized void setAlignmentWeight(double value) {
    	this.alignmentWeight = value;
    }

    public synchronized void setCohesionWeight(double value) {
    	this.cohesionWeight = value;
    }

    public /*synchronized*/ double getSeparationWeight() {
    	return separationWeight;
    }

    public /*synchronized*/ double getCohesionWeight() {
    	return cohesionWeight;
    }

    public /*synchronized*/ double getAlignmentWeight() {
    	return alignmentWeight;
    }
    
    public /*synchronized*/ double getMaxSpeed() {
    	return maxSpeed;
    }

    public /*synchronized*/ double getAvoidRadius() {
    	return avoidRadius;
    }

    public /*synchronized*/ double getPerceptionRadius() {
    	return perceptionRadius;
    }

    public synchronized boolean isModelPaused(){
        return this.isModelPaused;
    }

    public synchronized void toggleSimulationPause(){
        this.isModelPaused = !this.isModelPaused;
    }
}
