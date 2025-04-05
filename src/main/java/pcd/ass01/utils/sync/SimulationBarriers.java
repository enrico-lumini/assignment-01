package pcd.ass01.utils.sync;

public class SimulationBarriers {
    public final SimpleBarrier neighbors;
    public final SimpleBarrier velocity;
    public final SimpleBarrier position;
    public final SimpleBarrier boidsNumberChanged;

    public SimulationBarriers(int parties) {
        neighbors = new SimpleBarrier(parties);
        velocity = new SimpleBarrier(parties);
        position = new SimpleBarrier(parties);
        boidsNumberChanged = new SimpleBarrier(parties);
    }

    public static void awaitBarrier(SimpleBarrier barrier) {
        try {
            barrier.await();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

