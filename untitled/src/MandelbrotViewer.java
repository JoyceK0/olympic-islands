import javax.swing.*;
import java.awt.*;
public class MandelbrotViewer extends JPanel {
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        int width = getWidth();
        int height = getHeight();
        int maxIterations = 100;
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                double x0 = (col - width / 2.0) * 4.0 / width;
                double y0 = (row - height / 2.0) * 4.0 / width;
                double x = 0;
                double y = 0;
                int iteration = 0;
                while (x * x + y * y <= 4 && iteration < maxIterations) {
                    double xTemp = x * x - y * y + x0;
                    y = 2 * x * y + y0;
                    x = xTemp;
                    iteration++;
                }
                if (iteration == maxIterations) {
                    g.setColor(Color.BLACK);
                } else {
                    g.setColor(new Color(iteration * 255 / maxIterations, 0, 255));
                }
                g.drawLine(col, row, col, row);
            }
        }
    }
}
