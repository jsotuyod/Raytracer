package cg.ui.components;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JComponent;

public class ImageLoad extends JComponent {
	private static final long serialVersionUID = 1L;
	
	private File file;
	private ImageIcon thumbnail = null;

	public ImageIcon getThumbnail() {
		return thumbnail;
	}

    public File getFile() {
		return file;
	}

	public void loadImage(Image image) {
    	
    	if ( image == null ) {
    		thumbnail = null;
            return;
    	}
    	
        setImage( new ImageIcon( image ) );
    }
    
    public void loadImage(File imageFile) {
    	file = imageFile;
    	
        if (file == null) {
            thumbnail = null;
            return;
        }

        //Don't use createImageIcon (which is a wrapper for getResource)
        //because the image we're trying to load is probably not one
        //of this program's own resources.
        
		try {
			Image image = ImageIO.read(new File (file.getPath()));
	        setImage( new ImageIcon(image));
		} catch (IOException e) {
			e.printStackTrace();
		}

    }
    
    private void setImage( ImageIcon tmpIcon ) {
    	
    	if (tmpIcon != null) {
            thumbnail = tmpIcon;
            
            Dimension s = this.getPreferredSize();
            s.width = thumbnail.getIconWidth();
            s.height = thumbnail.getIconHeight();
            this.setPreferredSize(s);
            
            // Force repaint
            this.repaint();
        }
    }
    
    public Image getImage() {
    	
    	if ( thumbnail == null ) {
    		return null;
    	}
    	
    	return thumbnail.getImage();
    }

    protected void paintComponent(Graphics g) {
        if (thumbnail == null) {
            loadImage(file);
        }
        if (thumbnail != null) {
            int x = getWidth()/2 - thumbnail.getIconWidth()/2;
            int y = getHeight()/2 - thumbnail.getIconHeight()/2;

            if (y < 0) {
                y = 0;
            }

            if (x < 5) {
                x = 5; //TODO: comente esto para q quede centrada, descomentar y hacer scroll
            }
            thumbnail.paintIcon(this, g, x, y);
        }
    }
}
