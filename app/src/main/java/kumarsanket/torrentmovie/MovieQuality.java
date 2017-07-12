package kumarsanket.torrentmovie;

import java.io.Serializable;

/**
 * Created by sanketkumar on 22/05/17.
 */

// this is for movie quality  720p ,1080p and 3D
public class MovieQuality implements Serializable{

    public String url;
    public String hash;
    public String quality;
    public String seeds;
    public String peers;
    public String size;

    public MovieQuality(String url,String hash,String quality,String seeds ,String peers,String size)
    {
        this.url = url;
        this.hash = hash;
        this.quality = quality;
        this.seeds = seeds;
        this.peers = peers;
        this.size = size;
    }






    /**
     * -------------------- getter method --------------------------
     * @return
     */

    public String getHash() {
        return hash;
    }

    public String getPeers() {
        return peers;
    }

    public String getQuality() {
        return quality;
    }

    public String getSeeds() {
        return seeds;
    }

    public String getSize() {
        return size;
    }

    public String getUrl() {
        return url;
    }

}
