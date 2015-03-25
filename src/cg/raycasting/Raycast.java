package cg.raycasting;

import java.util.Random;

import cg.scene.Camera;
import cg.scene.Scene;
import cg.utils.Color;
import cg.utils.ImageBuffer;

public class Raycast {
	private final static int BUCKET_SIZE = 32;

	private final Scene scene;
	private final ImageBuffer buffer;
	private int previousPercentage = 0;
	private final int[] renderCoords;
	private int renderedBoxes;
	private final int totalBoxes;
	private final int samples;
	private final int width, height;
	private final float iSamples;
	private final float stepX, stepY;
	private final Camera camera;
	private final boolean showProgress;
	private final Random random = new Random();
	private final Thread[] renderThreads;

	public Raycast(final boolean showProgress, final int nCores, final Scene scene, final ImageBuffer buffer) {
		this.showProgress = showProgress;

		renderThreads = new Thread[nCores];

		this.scene = scene;

		samples = scene.getImageData().getSamples();
		iSamples = 1.0f / samples;

		camera = scene.getCamera();

		this.buffer = buffer;

		// Build worklog to render buckets from left to right, up to bottom
		final int coordsX = (int) Math.ceil(buffer.getWidth() / (float) Raycast.BUCKET_SIZE);
		final int coordsY = (int) Math.ceil(buffer.getHeight() / (float) Raycast.BUCKET_SIZE);
		totalBoxes = 2 * coordsX * coordsY;
		renderCoords = new int[totalBoxes];

		for (int j = 0; j < coordsY; j++) {
			for (int i = 0; i < coordsX; i++) {
				renderCoords[i * 2 + j * coordsX * 2] = i;
				renderCoords[i * 2 + j * coordsX * 2 + 1] = j;
			}
		}

		width = buffer.getWidth();
		height = buffer.getHeight();

		stepX = 1.0f / (width - 1);
		stepY = 1.0f / (height - 1);
	}

	void printPercent() {

		int percentage = Math.round(renderedBoxes / (float) totalBoxes * 100);
		if (previousPercentage != percentage) {

			previousPercentage = percentage;
			System.out.print ("\r");

			if (percentage < 100) {
				System.out.print(" ");

				if (percentage < 10) {
					System.out.print(" ");
				}
			}

		    System.out.print(percentage);
		    System.out.print("%");
		}
	}

	public boolean render() {
		renderedBoxes = 0;

		for (int i = 0; i < renderThreads.length; i++) {
			if (samples == 1) {
				renderThreads[i] = new RenderPart();
			} else {
				renderThreads[i] = new RenderPartMultiSample();
			}

            renderThreads[i].start();
        }

        for (int i = 0; i < renderThreads.length; i++) {
            try {
                renderThreads[i].join();
            } catch (final InterruptedException e) {
                System.err.println("Thread "+ (i+1) + " of " + renderThreads.length + " was interrupted");
            }
        }

		return true;
	}

	private class RenderPart extends Thread {

		@Override
		public void run() {
			int bucketX, bucketY;

			while (true) {
				synchronized (Raycast.this) {
					if (renderedBoxes == totalBoxes) {
						return;
					}

					bucketX = renderCoords[renderedBoxes++];
					bucketY = renderCoords[renderedBoxes++];
				}

				renderPart(bucketX, bucketY);

				if (showProgress) {
					printPercent();
				}
			}
		}
	}

	private class RenderPartMultiSample extends Thread {

		@Override
		public void run() {
			int bucketX, bucketY;

			while (true) {
				synchronized (Raycast.this) {
					if (renderedBoxes == totalBoxes) {
						return;
					}

					bucketX = renderCoords[renderedBoxes++];
					bucketY = renderCoords[renderedBoxes++];
				}

				renderPartMultiSample(bucketX, bucketY);

				if (showProgress) {
					printPercent();
				}
			}
		}
	}

	void renderPart(final int bucketX, final int bucketY) {
		float auxX, auxY;

		Collision collision;
		Ray r;
		Color c;
		int i, j;

		final int limitX = width < (bucketX + 1) * BUCKET_SIZE ? width : (bucketX + 1) * BUCKET_SIZE;
		final int limitY = height < (bucketY + 1) * BUCKET_SIZE ? height : (bucketY + 1) * BUCKET_SIZE;

		auxX = bucketX * BUCKET_SIZE * stepX - 0.5f;
		for (i = bucketX * BUCKET_SIZE; i < limitX; i++) {
			auxX += stepX;

			auxY = -bucketY * BUCKET_SIZE * stepY + 0.5f;
			for (j = bucketY * BUCKET_SIZE; j < limitY; j++) {
				c = new Color();

				auxY -= stepY;

				r = camera.getRay(auxX, auxY);

				if ((collision = scene.castRay(r)) != null) {
					c.add(collision.object.getColor(collision));
				}

				// TODO : Sunflow hace el toNonLinear.... lo dejamos???
				buffer.setRgbColor(i, j, c.toNonLinear().toRGB());
			}
		}
	}

	void renderPartMultiSample(final int bucketX, final int bucketY) {
		float auxX, auxY;

		Collision collision;
		Ray r;
		Color c;
		int i, j, k;

		final int limitX = width < (bucketX + 1) * BUCKET_SIZE ? width : (bucketX + 1) * BUCKET_SIZE;
		final int limitY = height < (bucketY + 1) * BUCKET_SIZE ? height : (bucketY + 1) * BUCKET_SIZE;

		auxX = bucketX * BUCKET_SIZE * stepX - 0.5f;
		for (i = bucketX * BUCKET_SIZE; i < limitX; i++) {
			auxX += stepX;

			auxY = -bucketY * BUCKET_SIZE * stepY + 0.5f;
			for (j = bucketY * BUCKET_SIZE; j < limitY; j++) {
				c = new Color();

				auxY -= stepY;

				for (k = 0; k < samples; k++) {
					r = camera.getRay(auxX + random.nextFloat() * stepX, auxY + random.nextFloat() * stepY);

					if ((collision = scene.castRay(r)) != null) {
						c.add( collision.object.getColor(collision) );
					}
				}

				c.mul(iSamples);

				// TODO : Sunflow hace el toNonLinear.... lo dejamos???
				buffer.setRgbColor(i, j, c.toNonLinear().toRGB());
			}
		}
	}
}
