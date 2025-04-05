package pcd.ass01;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import java.awt.*;
import java.util.Hashtable;

public class BoidsView implements ChangeListener {

	private JFrame frame;
	private BoidsPanel boidsPanel;
	private JSlider cohesionSlider, separationSlider, alignmentSlider;
	private JSpinner boidSpinner;
	private BoidsModel model;
	private int width, height;

	public BoidsView(BoidsModel model, int width, int height) {
		this.model = model;
		this.width = width;
		this.height = height;

		frame = new JFrame("Boids Simulation");
		frame.setSize(width, height);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JPanel cp = new JPanel();
		LayoutManager layout = new BorderLayout();
		cp.setLayout(layout);

		boidsPanel = new BoidsPanel(this, model);
		cp.add(BorderLayout.CENTER, boidsPanel);

		JPanel slidersPanel = new JPanel();

		slidersPanel.setLayout(new GridLayout(2,2));

		cohesionSlider = makeSlider();
		separationSlider = makeSlider();
		alignmentSlider = makeSlider();
		boidSpinner = makeBoidSpinner();

		slidersPanel.add(new JLabel("Separation"));
		slidersPanel.add(separationSlider);
		slidersPanel.add(new JLabel("Alignment"));
		slidersPanel.add(alignmentSlider);
		slidersPanel.add(new JLabel("Cohesion"));
		slidersPanel.add(cohesionSlider);
		slidersPanel.add(new JLabel("Boids"));
		slidersPanel.add(boidSpinner);

		cp.add(BorderLayout.SOUTH, slidersPanel);



		JButton toggleSimulation = new JButton("Resume");
		toggleSimulation.addActionListener((e) -> {
			model.toggleSimulationPause();
			toggleSimulation.setText(model.isModelPaused() ? "Resume" : "Play");
		} );

		cp.add(BorderLayout.NORTH, toggleSimulation);

		frame.setContentPane(cp);

		frame.setVisible(true);
	}

	private JSlider makeSlider() {
		var slider = new JSlider(JSlider.HORIZONTAL, 0, 20, 10);
		slider.setMajorTickSpacing(10);
		slider.setMinorTickSpacing(1);
		slider.setPaintTicks(true);
		slider.setPaintLabels(true);
		Hashtable labelTable = new Hashtable<>();
		labelTable.put( 0, new JLabel("0") );
		labelTable.put( 10, new JLabel("1") );
		labelTable.put( 20, new JLabel("2") );
		slider.setLabelTable( labelTable );
		slider.setPaintLabels(true);
		slider.addChangeListener(this);
		return slider;
	}

	private JSpinner makeBoidSpinner() {
		int initialValue = 1500;
		int min = 1500;
		int max = 16500;
		int step = 1500;

		SpinnerNumberModel spinnerModel = new SpinnerNumberModel(initialValue, min, max, step);
		JSpinner spinner = new JSpinner(spinnerModel);

		spinner.addChangeListener(e -> {
			if (!model.isBoidsNumberChanged()) {
				int val = (int) spinner.getValue();
				model.regenerateBoids(val);
				System.out.println("Boids number: " + val);
			}
		});

		return spinner;
	}

	public void update(int frameRate) {
		boidsPanel.setFrameRate(frameRate);
		boidsPanel.repaint();
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		if (e.getSource() == separationSlider) {
			var val = separationSlider.getValue();
			model.setSeparationWeight(0.1*val);
		} else if (e.getSource() == cohesionSlider) {
			var val = cohesionSlider.getValue();
			model.setCohesionWeight(0.1*val);
		} else if(e.getSource() == alignmentSlider){
			var val = alignmentSlider.getValue();
			model.setAlignmentWeight(0.1*val);
		}
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

}