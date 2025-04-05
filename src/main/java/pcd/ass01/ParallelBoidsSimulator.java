package pcd.ass01;

import pcd.ass01.utils.sync.SimpleBarrier;
import pcd.ass01.utils.sync.SimulationBarriers;
import pcd.ass01.workers.BatchBoidsUpdater;

import java.util.Arrays;
import java.util.List;

public class ParallelBoidsSimulator extends BoidsSimulator {

    private List<BatchBoidsUpdater> updaters;

    private final SimulationBarriers barriers;

    public ParallelBoidsSimulator(BoidsModel model) {
        super(model);

        int availableProcessors = Runtime.getRuntime().availableProcessors();

        updaters = Arrays.asList(new BatchBoidsUpdater[availableProcessors]);
        barriers = new SimulationBarriers(availableProcessors + 1);

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
                barriers
            );
            updaters.set(id, updater); // updaters[id] = updater;
            updater.start();
        }
    }

    public void runSimulation() {
        while (true) {
            if(!model.isModelPaused()) {
                var t0 = System.currentTimeMillis();

                updateBoids();

                draw(t0);
            }
        }
    }

    @Override
    public void updateBoids() {
        if (model.isBoidsNumberChanged()) {
            model.updateBoids();
            var newBoids = model.getBoids();
            updaters.forEach(updater -> {
                updater.setBoidsToUpdate(getUpdaterBoids(newBoids, updater.getUpdaterId()));
            });
        }
        SimulationBarriers.awaitBarrier(barriers.boidsNumberChanged);

        SimulationBarriers.awaitBarrier(barriers.neighbors);

        SimulationBarriers.awaitBarrier(barriers.velocity);

        SimulationBarriers.awaitBarrier(barriers.position);
    }

    private List<Boid> getUpdaterBoids(List<Boid> boids, int updaterId) {
        int totalBoids = boids.size();
        int boidsPerUpdater = totalBoids / updaters.size();
        int start = updaterId * boidsPerUpdater;
        int end = (updaterId == updaters.size() - 1) ? totalBoids : start + boidsPerUpdater;
        return boids.subList(start, end);
    }
}
