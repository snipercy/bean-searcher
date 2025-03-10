package com.ejlchina.searcher.convertor;


import com.ejlchina.searcher.ParamResolver;
import com.ejlchina.searcher.bean.DbType;
import com.ejlchina.searcher.util.StringUtils;

import java.sql.Timestamp;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.util.Date;
import java.util.TimeZone;
import java.util.regex.Pattern;

import static java.time.temporal.ChronoField.EPOCH_DAY;

/**
 * [String | java.util.Date | LocalDate to java.sql.Date] 参数值转换器
 *
 * @author Troy.Zhou @ 2022-06-14
 * @since v3.8.0
 */
public class DateTimeParamConvertor implements ParamResolver.Convertor {

    static final Pattern DATETIME_PATTERN = Pattern.compile("[0-9]{4}-[0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2}:[0-9]{2}");

    static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * 时区
     */
    private TimeZone timeZone = TimeZone.getDefault();

    @Override
    public boolean supports(DbType dbType, Class<?> valueType) {
        return dbType == DbType.DATETIME && (String.class == valueType || Date.class == valueType || LocalDate.class == valueType || LocalDateTime.class == valueType);
    }

    @Override
    public Object convert(DbType dbType, Object value) {
        if (value instanceof String) {
            String s = ((String) value).trim().replaceAll("/", "-");
            if (StringUtils.isBlank(s)) {
                return null;
            }
            String datetime = normalize(s);
            if (DATETIME_PATTERN.matcher(datetime).matches()) {
                TemporalAccessor accessor = FORMATTER.parse(datetime);
                LocalDateTime dateTime = LocalDate.ofEpochDay(accessor.getLong(EPOCH_DAY))
                        .atTime(LocalTime.ofSecondOfDay(accessor.getLong(ChronoField.SECOND_OF_DAY)));
                return toTimestamp(dateTime);
            }
        }
        if (value instanceof Date) {
            return new Timestamp(((Date) value).getTime());
        }
        if (value instanceof LocalDate) {
            return toTimestamp(((LocalDate) value).atTime(0, 0, 0));
        }
        if (value instanceof LocalDateTime) {
            return toTimestamp((LocalDateTime) value);
        }
        return null;
    }

    private Timestamp toTimestamp(LocalDateTime dateTime) {
        ZoneOffset offset = ZoneOffset.ofTotalSeconds(timeZone.getRawOffset() / 1000);
        return new Timestamp(dateTime.toInstant(offset).toEpochMilli());
    }

    private String normalize(String datetime) {
        int len = datetime.length();
        if (len == 16) {
            return datetime + ":00";
        }
        if (len == 13) {
            return datetime + ":00:00";
        }
        if (len == 10) {
            return datetime + " 00:00:00";
        }
        return datetime;
    }

    public ZoneId getZoneId() {
        return timeZone.toZoneId();
    }

    public void setZoneId(ZoneId zoneId) {
        if (zoneId != null) {
            timeZone = TimeZone.getTimeZone(zoneId);
        }
    }

}
