package net.afyer.afybroker.client;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

/**
 * @author Nipuru
 * @since 2022/7/31 16:09
 */
@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BrokerAddress {

    final String host;
    final int port;

    public String getAddress() {
        return host + ":" + port;
    }

}
