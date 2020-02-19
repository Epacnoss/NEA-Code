import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;


public class canvas extends JComponent {
	private getImg[][] imageComps;
	private String[][] fileNames;
	private int w, h;
	private BufferedImage canvas;
	private Font f;

	private getImgScaledIndep money, hearts;

	public canvas (int w_, int h_, String[][] fileNames_) {
		w = w_;
		h = h_;

		f = new Font("Roboto", Font.BOLD, 18);
		
		imageComps = new getImg[w][h];
		fileNames = new String[w][h];

		money = new getImgScaledIndep("Money.jpg", 50, 50);
		hearts = new getImgScaledIndep("Heart.png", 50, 50);
		
		for(int i = 0; i < w; i++){
			for(int j = 0; j < w; j++){
				fileNames[i][j] = fileNames_[i][j];
				imageComps[i][j] = new getImg(fileNames[i][j]);
			}
		}

		int size = main.TILE_SIZE;

		canvas = new BufferedImage(w * size, h * size, BufferedImage.TYPE_INT_ARGB);
	}
	
	public void setFN (int x, int y, String newFile)
	{
		if(x > fileNames.length || y > fileNames[0].length){
			return;
		}
		
		fileNames[x][y] = newFile;
		imageComps[x][y] = new getImg(fileNames[x][y]);
		
	}


	@Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);


        for (int row = 0; row < imageComps.length; row++) {
            for (int col = 0; col < imageComps[0].length; col++) {
                int size = main.TILE_SIZE;
                int offsetX = row * size;
                int offsetY = col * size;
                BufferedImage temp = imageComps[row][col];

                for (int x = 0; x < size; x++) {
                    for (int y = 0; y < size; y++) {
                        canvas.setRGB(offsetX + x, offsetY + y, temp.getRGB(x, y));
                    }
                }

            }

            g.drawRect(100, 100, 100, 100);
        }


//        canvas.getGraphics().drawString("HP: 50; Kredits: 1000", 20, 20);
        canvas.getGraphics().drawImage(money, 10, 10, null);
        canvas.getGraphics().drawString("50", 70, 35);

        canvas.getGraphics().drawImage(hearts, 10, 70, null);
        canvas.getGraphics().drawString("5", 70, 95);

        g.drawImage(canvas, 0, 0, null);
    }


}
