import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class main {

    public static final int widthHeightOverall = 600;  //px
    private static int w = 10;
    public static final int TILE_SIZE = widthHeightOverall / w;
    private static double fps = 0.5;
    private static double msPerUpdate = 1000d / fps;

    private static String[] possibleFiles = {"p1.jpeg", "p2.jpeg", "p3.jpeg", "p4.jpeg", "p5.jpeg", "p6.jpeg", "p7.jpeg", "p8.jpeg", "p9.jpeg", "p10.jpg", "IMG_4855.JPG", "IMG_4856.JPG", "IMG_4857.JPG", "IMG_4858.JPG", "IMG_4859.JPG", "IMG_4860.JPG", "IMG_4861.JPG", "IMG_4862.JPG", "IMG_4863.JPG", "IMG_4864.JPG", "IMG_4865.JPG", "IMG_4866.JPG", "IMG_4867.JPG", "IMG_4868.JPG", "IMG_4869.JPG", "IMG_4870.JPG", "nicePic.png"};

    private JFrame frame;
    private Dimension wh;
    private canvas comps;

    private Dimension wack;

    public main ()
    {
        frame = new JFrame("Graphics Tester");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setResizable(false);
        wh = new Dimension(widthHeightOverall, widthHeightOverall);
        wack = new Dimension(wh.width + 1, wh.height + 1);
        frame.setSize(wh);



        comps = new canvas(w, w, getImgs(w));
        frame.add(comps);

        framePack();

        frame.setVisible(true);

    }

    public void setImgComp (String fn, int x, int y){
        comps.setFN(x, y, fn);
    }

    public void framePack () {
        frame.pack();
        frame.setPreferredSize(wh);


        frame.setSize(wack);
        frame.setSize(wh);
    }

    public static void main(String[] args) {
        main m = new main();
        Random r = new Random();

        long current = System.currentTimeMillis();

        while (true)
        {
            if(System.currentTimeMillis() - current > msPerUpdate)
            {
                String[][] files = getImgs(w);
                for (int x = 0; x < w; x++) {
                    for (int y = 0; y < w; y++) {
                        int rnd = r.nextInt(possibleFiles.length);
                        String f = possibleFiles[rnd];

                        m.setImgComp(f, x, y);
                    }
                }

                current = System.currentTimeMillis();
                m.framePack();
            }
        }
    }

    private static String[][] getImgs (int w ) {
        String[][] files = new String[w][w];
        Random rnd = new Random();

        for (int x = 0; x < w; x++) {
            for (int y = 0; y < w; y++) {
                int index = rnd.nextInt(possibleFiles.length);
                files[x][y] = possibleFiles[index];
            }
        }

        return files;
    }

}


// Sources: images.google.com, reddit.com/r/aww