package pcd.ass01.workers;

import pcd.ass01.Boid;
import pcd.ass01.BoidsModel;
import pcd.ass01.utils.sync.SimpleBarrier;
import pcd.ass01.utils.sync.SimulationBarriers;

import java.util.List;

public class BatchBoidsUpdater extends Thread {

    private final int id;
    private BoidsModel model;

    private List<Boid> boidsToUpdate;

    private final SimulationBarriers barriers;

    public BatchBoidsUpdater(
            int id,
            BoidsModel model,
            List<Boid> boidsToUpdate,
            SimulationBarriers barriers
    ) {
        super("BatchBoidUpdater-" + id);
        this.id = id;
        this.model = model;
        this.boidsToUpdate = boidsToUpdate;
        this.barriers = barriers;
    }

    @Override
    public void run() {
        while (true) {
            SimulationBarriers.awaitBarrier(barriers.boidsNumberChanged);

            computeNearbyBoids();
            SimulationBarriers.awaitBarrier(barriers.neighbors);

            updateVelocity();
            SimulationBarriers.awaitBarrier(barriers.velocity);

            updatePosition();
            SimulationBarriers.awaitBarrier(barriers.position);
        }
    }

    public int getUpdaterId() {
        return id;
    }

    public void setBoidsToUpdate(List<Boid> boidsToUpdate) {
        this.boidsToUpdate = boidsToUpdate;
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
}
