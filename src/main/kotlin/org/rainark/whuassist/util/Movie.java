package org.rainark.whuassist.util;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * Legacy class for movie info storage.
 *
 * Contains the information of a movie.
 * Including the URL of poster image, movie name, description, basic information and
 * ranking on a particular movie site.
 *
 * @author Zhou Jingsen
 * */
public class Movie {
    public URL image;
    public String name;
    public String info;
    public String description;
    public double rank;
    public String detailPage;
    /**
     * Get the poster image of the movie.
     */
    public BufferedImage downloadImage() throws IOException{
        return ImageIO.read(image);
    }
    @Override
    public String toString() {
        return name+":"+description;
    }
}
