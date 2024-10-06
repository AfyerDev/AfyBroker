package net.afyer.afybroker.core.message;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

/**
 * @author Nipuru
 * @since 2022/8/11 8:55
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SendPlayerTitleMessage implements Serializable {
    private static final long serialVersionUID = 2892294285923120071L;

    /**
     * 玩家名
     */
    String name;

    /**
     * title
     */
    String title;

    /**
     * subtitle
     */
    String subtitle;

    /**
     * 淡入
     */
    int fadein;

    /** 停留时间 */
    int stay;

    /** 淡出 */
    int fadeout;
}
