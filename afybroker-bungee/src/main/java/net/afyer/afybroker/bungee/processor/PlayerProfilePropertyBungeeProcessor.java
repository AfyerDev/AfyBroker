package net.afyer.afybroker.bungee.processor;


import com.alipay.remoting.BizContext;
import com.alipay.remoting.rpc.protocol.SyncUserProcessor;
import net.afyer.afybroker.core.message.PlayerProfilePropertyMessage;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.connection.InitialHandler;
import net.md_5.bungee.connection.LoginResult;
import net.md_5.bungee.protocol.Property;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.util.*;

/**
 * @author Nipuru
 * @since 2024/12/03 10:20
 */
public class PlayerProfilePropertyBungeeProcessor extends SyncUserProcessor<PlayerProfilePropertyMessage> {
    @Override
    public Object handleRequest(BizContext bizCtx, PlayerProfilePropertyMessage request) throws Exception {
        ProxiedPlayer player = ProxyServer.getInstance().getPlayer(request.getUniqueId());
        if (player == null) {
            return false;
        }
        InitialHandler connection = (InitialHandler) player.getPendingConnection();
        LoginResult loginProfile = connection.getLoginProfile();
        if (loginProfile == null) {
            loginProfile = new LoginResult(player.getUniqueId().toString(), player.getName(), new Property[0]);
            loginResultField.set(connection, loginProfile);
        }
        Map<String, Property> properties = new HashMap<>();
        if (loginProfile.getProperties() != null) {
            for (Property property : loginProfile.getProperties()) {
                properties.put(property.getName(), property);
            }
        }
        if (request.getRemoveList() != null) {
            for (String name : request.getRemoveList()) {
                properties.remove(name);
            }
        }
        if (request.getUpdateMap() != null) {
            for (Map.Entry<String, String[]> entry : request.getUpdateMap().entrySet()) {
                String name = entry.getKey();
                String value = entry.getValue()[0];
                String signature = entry.getValue()[1];
                properties.put(name, new Property(name, value, signature));
            }
        }
        loginProfile.setProperties(properties.values().toArray(new Property[0]));
        return true;
    }

    @Override
    public String interest() {
        return PlayerProfilePropertyMessage.class.getName();
    }

    private static final Field loginResultField;
    static {
        try {
            Field field = InitialHandler.class.getDeclaredField("loginProfile");
            field.setAccessible(true);
            loginResultField = field;
        } catch (NoSuchFieldException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

}
