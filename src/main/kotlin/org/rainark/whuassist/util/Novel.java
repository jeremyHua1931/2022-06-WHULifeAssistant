package org.rainark.whuassist.util;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

/**
 * Legacy class for novel info storage
 * <p>
 * Contains the information of a novel
 * Including the URLof novel, the URL of poster image, novel name, author, category, subcategory
 * completionStatus, basic introduction, recent updated chapter
 * ranking on a particular novel site
 *
 * @author Hua Zhangzhao
 */
public class Novel {

    public int rank;
    public String name;
    public String author;
    public URL novelURL;
    public URL image;
    public String category;
    public String subcategory;
    public String completionStatus;
    public String updatedChapter;
    public String introduction;

    public BufferedImage downloadImage() throws IOException {
        return ImageIO.read(image);
    }

    public Novel(int rank, String name, String author, URL novelURL, URL image, String category, String subcategory, String completionStatus, String updatedChapter, String introduction) {
        this.rank = rank;
        this.name = name;
        this.author = author;
        this.novelURL = novelURL;
        this.image = image;
        this.category = category;
        this.subcategory = subcategory;
        this.completionStatus = completionStatus;
        this.updatedChapter = updatedChapter;
        this.introduction = introduction;
    }

    public URL getNovelURL() {
        return novelURL;
    }

    public void setNovelURL(URL novelURL) {
        this.novelURL = novelURL;
    }

    public URL getImage() {
        return image;
    }

    public void setImage(URL image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSubcategory() {
        return subcategory;
    }

    public void setSubcategory(String subcategory) {
        this.subcategory = subcategory;
    }

    public String getCompletionStatus() {
        return completionStatus;
    }

    public void setCompletionStatus(String completionStatus) {
        this.completionStatus = completionStatus;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public String getUpdatedChapter() {
        return updatedChapter;
    }

    public void setUpdatedChapter(String updatedChapter) {
        this.updatedChapter = updatedChapter;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }
}
