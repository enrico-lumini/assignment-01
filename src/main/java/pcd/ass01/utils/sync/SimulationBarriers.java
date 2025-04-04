package pcd.ass01.utils.sync;

public class SimulationBarriers {
    public final SimpleBarrier neighbors;
    public final SimpleBarrier velocity;
    public final SimpleBarrier position;

    public SimulationBarriers(int parties) {
        neighbors = new SimpleBarrier(parties);
        velocity = new SimpleBarrier(parties);
        position = new SimpleBarrier(parties);
    }
}

