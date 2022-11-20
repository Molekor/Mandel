import java.awt.Color;
import java.util.Random;

public class PaletteCreator {
	
    public static Color[] erzeugeFarben(int count, boolean grayscale) {
    	int farbe=0;
    	Random myRandom = new Random();
    	Color[] meineFarben = new Color[count];
        for (int i = 0; i < count; i++) {
            	if(i<10) {
            		farbe += 5;
            	} else if(i<100) {
            		farbe += 2;
            	} else {
            		if(i%4==0) {
            			farbe++;
            		}
            	}
                farbe++;
                if (farbe > 255)
                {
                    farbe=0;
                }
                if(grayscale) {
                	// meineFarben[i]=(i%2)==0?Color.BLACK:Color.white; // black/white intermittend
                	meineFarben[i]= new Color(farbe,farbe,farbe);
                } else {
                	meineFarben[i]=new Color(Math.abs((150-farbe*1) % 255),Math.abs((128 - farbe*2) % 255),Math.abs((200-farbe*3) % 255));
                }
        }
        meineFarben[count-1] = new Color(80,0,0);
        return meineFarben;
    }
}
