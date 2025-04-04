package pcd.ass01.workers;

import pcd.ass01.Boid;
import pcd.ass01.BoidsModel;
import pcd.ass01.utils.sync.CyclicBarrier;

import java.util.List;

public class BatchBoidsUpdater extends Thread {

    private BoidsModel model;

    private List<Boid> boidsToUpdate;

    private final CyclicBarrier startUpdateBarrier;
    private final CyclicBarrier endFetchBoidsBarrier;
    private final CyclicBarrier endUpdateBarrier;

    public BatchBoidsUpdater(
            int id,
            BoidsModel model,
            List<Boid> boidsToUpdate,
            CyclicBarrier startUpdateBarrier,
            CyclicBarrier endFetchBoidsBarrier,
            CyclicBarrier endUpdateBarrier
    ) {
        super("BatchBoidUpdater-" + id);
        this.model = model;
        this.boidsToUpdate = boidsToUpdate;
        this.startUpdateBarrier = startUpdateBarrier;
        this.endFetchBoidsBarrier = endFetchBoidsBarrier;
        this.endUpdateBarrier = endUpdateBarrier;
    }

    @Override
    public void run() {
        while (true) {
            waitStart();

            fetchBoids();

            waitFetchBoids();

            updateBoids();

            endUpdate();
        }
    }

    private void fetchBoids() {
        boidsToUpdate.forEach(boid -> boid.fetchNearbyBoids(model));
    }

    private void updateBoids() {
        for (Boid boid : boidsToUpdate) {
            boid.updateVelocity(model);
            boid.updatePos(model);
        }
    }

    private void waitStart() {
        try {
            System.out.println("Waiting for start barrier: " + this.getName());
            startUpdateBarrier.await();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void waitFetchBoids() {
        try {
            System.out.println("Waiting for end velocity update: " + this.getName());
            endFetchBoidsBarrier.await();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void endUpdate() {
        try {
            System.out.println("Notify for end update: " + this.getName());
            endUpdateBarrier.await();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
