package pcd.ass01;

import pcd.ass01.utils.sync.CyclicBarrier;
import pcd.ass01.workers.BatchBoidsUpdater;

import java.util.Arrays;
import java.util.List;

public class ParallelBoidsSimulator extends BoidsSimulator {

    private List<BatchBoidsUpdater> updaters;

    private final CyclicBarrier startUpdateBarrier; // Barrier for starting the update process
    private final CyclicBarrier endFetchBoidsBarrier; // Barrier for ending the fetch process (fetch boids)
    private final CyclicBarrier endUpdateBarrier; // Barrier for ending the update process (velocity and position update)

    public ParallelBoidsSimulator(BoidsModel model) {
        super(model);

        int availableProcessors = Runtime.getRuntime().availableProcessors();

        updaters = Arrays.asList(new BatchBoidsUpdater[availableProcessors]);

        startUpdateBarrier = new CyclicBarrier(availableProcessors + 1);
        endFetchBoidsBarrier = new CyclicBarrier(availableProcessors);
        endUpdateBarrier = new CyclicBarrier(availableProcessors + 1);

        setupUpdaters();
    }

    private void setupUpdaters() {
        var boids = this.model.getBoids();
        for (int id = 0; id < updaters.size(); id++) {
            List<Boid> boidsToUpdate = getUpdaterBoids(boids, id);
            BatchBoidsUpdater updater = new BatchBoidsUpdater(
                id,
                model,
                boidsToUpdate,
                startUpdateBarrier,
                endFetchBoidsBarrier,
                endUpdateBarrier
            );
            System.out.println("Creating updater " + id + " with boids: " + boidsToUpdate.size());
            updaters.set(id, updater);
            updater.start();
        }
    }

    @Override
    public void updateBoids(BoidsModel model) {
        startUpdate();
        System.out.println("Starting update for all updaters");

        waitUpdateEnd();
        System.out.println("All updaters finished updating");
    }

    private List<Boid> getUpdaterBoids(List<Boid> boids, int updaterId) {
        int totalBoids = boids.size();
        int boidsPerUpdater = totalBoids / updaters.size();
        int start = updaterId * boidsPerUpdater;
        int end = (updaterId == updaters.size() - 1) ? totalBoids : start + boidsPerUpdater;
        return boids.subList(start, end);
    }

    /**
     * Waits for all updaters to start the update process.
     * This method is called before updating the velocity of the boids.
     */
    private void startUpdate() {
        try {
            startUpdateBarrier.await();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Waits for all updaters to finish updating the position of the boids.
     * This method is called after updating the position of the boids.
     */
    private void waitUpdateEnd() {
        try {
            endUpdateBarrier.await();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
