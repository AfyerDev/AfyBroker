package net.afyer.afybroker.core.message;

import java.io.Serializable;

/**
 * @author Nipuru
 * @since 2022/8/11 8:55
 */
public class SendPlayerTitleMessage implements Serializable {
    private static final long serialVersionUID = 2892294285923120071L;

    /**
     * 玩家名
     */
    private String name;

    /**
     * title
     */
    private String title;

    /**
     * subtitle
     */
    private String subtitle;

    /**
     * 淡入
     */
    private int fadein;

    /** 停留时间 */
    private int stay;

    /** 淡出 */
    private int fadeout;

    public String getName() {
        return name;
    }

    public SendPlayerTitleMessage setName(String name) {
        this.name = name;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public SendPlayerTitleMessage setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public SendPlayerTitleMessage setSubtitle(String subtitle) {
        this.subtitle = subtitle;
        return this;
    }

    public int getFadein() {
        return fadein;
    }

    public SendPlayerTitleMessage setFadein(int fadein) {
        this.fadein = fadein;
        return this;
    }

    public int getStay() {
        return stay;
    }

    public SendPlayerTitleMessage setStay(int stay) {
        this.stay = stay;
        return this;
    }

    public int getFadeout() {
        return fadeout;
    }

    public SendPlayerTitleMessage setFadeout(int fadeout) {
        this.fadeout = fadeout;
        return this;
    }

    @Override
    public String toString() {
        return "SendPlayerTitleMessage{" +
                "name='" + name + '\'' +
                ", title='" + title + '\'' +
                ", subtitle='" + subtitle + '\'' +
                ", fadein=" + fadein +
                ", stay=" + stay +
                ", fadeout=" + fadeout +
                '}';
    }
}
