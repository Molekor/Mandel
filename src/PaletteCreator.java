import java.awt.Color;
import java.util.Random;

public class PaletteCreator {
	
    public static Color[] erzeugeFarben(int count, boolean grayscale) {
    	int farbe=0;
    	Random myRandom = new Random();
    	Color[] meineFarben = new Color[count];
        for (int i = 0; i < count; i++) {
            if (!grayscale) {
                meineFarben[i] = new Color(myRandom.nextInt(255),myRandom.nextInt(255),myRandom.nextInt(255));        
            } else {
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
                meineFarben[i]=new Color(farbe,farbe,farbe);
                meineFarben[i]=new Color(farbe,farbe,farbe);
            }
        }
        meineFarben[count-1] = new Color(80,0,0);
        return meineFarben;
    }
}
