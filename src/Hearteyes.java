import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import javax.imageio.ImageIO;

import javafx.util.Pair;

public class Hearteyes
{
	private BufferedImage image;
	private File f;
	
	public Hearteyes(String filename)
	{
		File src = new File("hearteyessource.png");
		//File src = new File("kisssource.png");
		f = new File(filename+".png");
		/*ImageInputStream is = ImageIO.createImageInputStream(f);
		ImageReader ir = ImageIO.getImageReaders(is).next();
		ImageWriter iw = ImageIO.getImageWriter(ir);
		BufferedImage bi = ir.read(0);*/
		try {
			Files.copy(src.toPath(), f.toPath(), StandardCopyOption.REPLACE_EXISTING);
			image = ImageIO.read(f);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public BufferedImage getImage()
	{
		return image;
	}
	
	public void updateImage(BufferedImage image)
	{
		try {
			ImageIO.write(image, "png", f);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args)
	{
		Scanner s = new Scanner(System.in);
		int rl=-1,gl=-1,bl=-1,rr=-1,gr=-1,br=-1;
		System.out.println("Specify left heart colors:");
		while(rl<0||rl>255)
		{
			System.out.print("Red value: ");
			rl = s.nextInt();
		}
		while(gl<0||gl>255)
		{
			System.out.print("Green value: ");
			gl = s.nextInt();
		}
		while(bl<0||bl>255)
		{
			System.out.print("Blue value: ");
			bl = s.nextInt();
		}
		System.out.println("Specify right heart colors:");
		while(rr<0||rr>255)
		{
			System.out.print("Red value: ");
			rr = s.nextInt();
		}
		while(gr<0||gr>255)
		{
			System.out.print("Green value: ");
			gr = s.nextInt();
		}
		while(br<0||br>255)
		{
			System.out.print("Blue value: ");
			br = s.nextInt();
		}
		Hearteyes he = new Hearteyes("hearteyes");
		//Hearteyes he = new Hearteyes("kiss");
		BufferedImage image = he.getImage();
		int w = image.getWidth(), h = image.getHeight();
		ArrayList<Pair<Integer, Integer> > left = new ArrayList<>(), right = new ArrayList<>();
		for(int i = 0; i < w; i++)
		{
			for(int j = 0; j < h; j++)
			{
				int rgb = image.getRGB(i, j);
				if(isRed(rgb))
				{
					//System.out.println((0xFF & (rgb >> 16)) + " " + (0xFF & (rgb >> 8)) + " " + (0xFF & rgb));
					if(i<=w/2) left.add(new Pair<Integer, Integer>(i,j));
					else right.add(new Pair<Integer, Integer>(i,j));
				}
			}
		}
		editHeart(image, left, rl, gl, bl);
		editHeart(image, right, rr, gr, br);
		he.updateImage(image);
		s.close();
	}
	
	public static boolean isRed(int rgb)
	{
		int b = 0xFF & rgb;
		int g = 0xFF & (rgb >> 8);
		int r = 0xFF & (rgb >> 16);
		return r>g && r>b && (g<200 && b<100 && (r-g>60 || r>1.5*g)); //hearteyes settings
		//return r>g && r>b && (g<165 && b<100 && (r-g>60 || r>1.5*g)); //kiss settings
	}
	
	public static void editHeart(BufferedImage image, ArrayList<Pair<Integer, Integer> > pixels, int targetR, int targetG, int targetB)
	{
		HashMap<Pair<Integer, Integer>, Integer> colors = new HashMap<>();
		int w = image.getWidth(), h = image.getHeight();
		for(Pair<Integer, Integer> pixel : pixels)
		{
			int a = 0xFF & (image.getRGB(pixel.getKey(), pixel.getValue()) >> 24);
			image.setRGB(pixel.getKey(), pixel.getValue(), a<<24 | targetR<<16 | targetG<<8 | targetB);
		}
		for(Pair<Integer, Integer> pixel : pixels)
		{
			int r=0, g=0, b=0, numPix=0;
			for(int i = -1; i <= 1; i++)
			{
				for(int j = -1; j <= 1; j++)
				{
					int nx = pixel.getKey()+i, ny=pixel.getValue()+j;
					if(nx>=0 && nx<w && ny>=0 && ny<h)
					{
						int rgb = image.getRGB(nx, ny);
						numPix++;
						r += 0xFF & (rgb >> 16);
						g += 0xFF & (rgb >> 8);
						b += 0xFF & rgb;
					}
				}
			}
			int a = 0xFF & (image.getRGB(pixel.getKey(), pixel.getValue()) >> 24);
			r=(int)((double)r/numPix+0.5);
			g=(int)((double)g/numPix+0.5);
			b=(int)((double)b/numPix+0.5);
			colors.put(pixel, a<<24 | r<<16 | g<<8 | b);
		}
		for(Pair<Integer, Integer> pixel : pixels)
		{
			image.setRGB(pixel.getKey(), pixel.getValue(), colors.get(pixel));
		}
	}
}
