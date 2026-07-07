package net.afyer.afybroker.core.serializer;

import com.caucho.hessian.io.AbstractSerializerFactory;
import com.caucho.hessian.io.Deserializer;
import com.caucho.hessian.io.Serializer;

import java.net.URI;
import java.net.URL;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.MonthDay;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.Period;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Currency;
import java.util.HashMap;
import java.util.Map;

/**
 * 用于处理 Hessian 4 在新版 JDK 上无法正确反射序列化的常见不可变 JDK 值类型。
 */
final class JdkValueSerializerFactory extends AbstractSerializerFactory {
    private static final Map<Class<?>, Serializer> SERIALIZERS = new HashMap<>();
    private static final Map<Class<?>, Deserializer> DESERIALIZERS = new HashMap<>();

    static {
        register(LocalDate.class, new LocalDateSerializer(), new LocalDateDeserializer());
        register(LocalTime.class, new LocalTimeSerializer(), new LocalTimeDeserializer());
        register(LocalDateTime.class, new LocalDateTimeSerializer(), new LocalDateTimeDeserializer());
        register(Instant.class, new InstantSerializer(), new InstantDeserializer());
        register(Duration.class, new DurationSerializer(), new DurationDeserializer());
        register(Period.class, new PeriodSerializer(), new PeriodDeserializer());
        register(Year.class, new YearSerializer(), new YearDeserializer());
        register(YearMonth.class, new YearMonthSerializer(), new YearMonthDeserializer());
        register(MonthDay.class, new MonthDaySerializer(), new MonthDayDeserializer());
        register(ZoneId.class, new ZoneIdSerializer(), new ZoneIdDeserializer());
        register(ZoneOffset.class, new ZoneOffsetSerializer(), new ZoneOffsetDeserializer());
        register(OffsetTime.class, new OffsetTimeSerializer(), new OffsetTimeDeserializer());
        register(OffsetDateTime.class, new OffsetDateTimeSerializer(), new OffsetDateTimeDeserializer());
        register(ZonedDateTime.class, new ZonedDateTimeSerializer(), new ZonedDateTimeDeserializer());
        register(Currency.class, new CurrencySerializer(), new CurrencyDeserializer());
        register(URI.class, new UriSerializer(), new UriDeserializer());
        register(URL.class, new UrlSerializer(), new UrlDeserializer());
    }

    @Override
    public Serializer getSerializer(Class cl) {
        Serializer serializer = SERIALIZERS.get(cl);
        if (serializer != null) {
            return serializer;
        }
        if (cl != null && ZoneId.class.isAssignableFrom(cl) && !ZoneOffset.class.equals(cl)) {
            return SERIALIZERS.get(ZoneId.class);
        }
        return null;
    }

    @Override
    public Deserializer getDeserializer(Class cl) {
        Deserializer deserializer = DESERIALIZERS.get(cl);
        if (deserializer != null) {
            return deserializer;
        }
        if (cl != null && ZoneId.class.isAssignableFrom(cl) && !ZoneOffset.class.equals(cl)) {
            return DESERIALIZERS.get(ZoneId.class);
        }
        return null;
    }

    private static void register(Class<?> type, Serializer serializer, Deserializer deserializer) {
        SERIALIZERS.put(type, serializer);
        DESERIALIZERS.put(type, deserializer);
    }
}
