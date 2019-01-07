package honbab.voltage.com.data;

import java.io.Serializable;
import java.util.ArrayList;

public class RestLikeData implements Serializable {
    private String feed_time;
    private ArrayList<FeedData> feedList;

    public RestLikeData() {

    }

    public RestLikeData(String feed_time,
                        ArrayList<FeedData> feedList) {
        //피드
        this.feed_time = feed_time;
        //신청자
        this.feedList = feedList;
    }

    public String getFeed_time() {
        return feed_time;
    }

    public void setFeed_time(String feed_time) {
        this.feed_time = feed_time;
    }

    public ArrayList<FeedData> getFeedList() {
        return feedList;
    }

    public void setFeedList(ArrayList<FeedData> feedList) {
        this.feedList = feedList;
    }
}