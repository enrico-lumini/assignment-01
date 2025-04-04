package pcd.ass01.workers;

import pcd.ass01.Boid;
import pcd.ass01.BoidsModel;
import pcd.ass01.utils.sync.SimpleBarrier;
import pcd.ass01.utils.sync.SimulationBarriers;

import java.util.List;

public class BatchBoidsUpdater extends Thread {

    private BoidsModel model;

    private List<Boid> boidsToUpdate;
    private List<Boid> allBoids;

    private final SimulationBarriers barriers;

    public BatchBoidsUpdater(
            int id,
            BoidsModel model,
            List<Boid> allBoids,
            List<Boid> boidsToUpdate,
            SimulationBarriers barriers
    ) {
        super("BatchBoidUpdater-" + id);
        this.model = model;
        this.allBoids = allBoids;
        this.boidsToUpdate = boidsToUpdate;
        this.barriers = barriers;
    }

    public void setAllBoids(List<Boid> allBoids) {
        this.allBoids = allBoids;
    }

    @Override
    public void run() {
        while (true) {
            updateBoidsList();

            computeNearbyBoids();
            awaitBarrier(barriers.neighbors);

            updateVelocity();
            awaitBarrier(barriers.velocity);

            updatePosition();
            awaitBarrier(barriers.position);
        }
    }

    private void updateBoidsList() {
        boidsToUpdate.forEach(boid -> boid.setAllBoids(allBoids));
    }

    private void computeNearbyBoids() {
        boidsToUpdate.forEach(boid -> boid.computeNearbyBoids(model));
    }

    private void updateVelocity() {
        for (Boid boid : boidsToUpdate) {
            boid.updateVelocity(model);
        }
    }

    private void updatePosition() {
        for (Boid boid : boidsToUpdate) {
            boid.updatePos(model);
        }
    }

    private void awaitBarrier(SimpleBarrier barrier) {
        try {
            barrier.await();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
