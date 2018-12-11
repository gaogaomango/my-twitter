package jp.co.mo.mysns;

import android.text.TextUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 日付操作ユーティリティー
 *
 */
public final class DateUtil {

    /**
     * 日付フォーマット：YYYY/MM/DD/ HH:MM:SS形式
     */
    public static final String  DATE_FORMAT_YYYM_MD_DH_H_M_SS = "yyyy/MM/dd HH:mm:ss";

    /**
     * 日付フォーマット：YYYYMMDDHHMMSS形式
     */
    public static final String  DATE_FORMAT_YYYMMDDHHMSS = "yyyyMMddHHmmss";

    /**
     * 日付フォーマット：YYYYMMDDHHMMSS形式
     */
    public static final String  DATE_FORMAT_YYYMMDDHHMM = "yyyyMMddHHmm";

    /**
     * 日付フォーマット：YYYYMMDDHHMMSS形式
     */
    public static final String  DATE_FORMAT_YYYMMDD = "yyyyMMdd";


    /**
     * コンストラクタ
     */
    private DateUtil() {
        // コンストラクタ生成防止
    }

    /**
     * 指定された日付を0時0分0秒0ミリ秒に初期化する
     *
     * @param date 日付
     * @return 0時0分0秒0ミリ秒に初期化した日付
     */
    public static Date date(final Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }

    /**
     * 現在日時を取得する
     *
     * @return　現在日時
     */
    public static Date now() {
        Calendar c = Calendar.getInstance();
        return c.getTime();
    }

    /**
     * 本日の日付(0時0分0秒0ミリ秒)を返却する
     *
     * @return　本日の日付
     */
    public static Date today() {
        return date(now());
    }

    /**
     * 指定日から指定日数加算する
     *
     * @param date
     * @param days
     * @return
     */
    public static Date addDays(final Date date, final int days) {
        Calendar c = Calendar.getInstance();

        // 0時0分0秒0ミリ秒に初期化しないとずれるため初期化する
        c.setTime(date);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);

        c.add(Calendar.DATE, days);
        return c.getTime();
    }

    /**
     *
     * @return
     */
    public static Date calcTargetDate(final Date date, final int hour, final int minute) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        return (cal.get(Calendar.HOUR_OF_DAY) * 60 + cal.get(Calendar.MINUTE))
                < (hour * 60 + minute) ? date(addDays(date, -1)) : date(date);
    }

    /**
     * 指定された日付文字列をDate型に変換する
     *
     * @param format フォーマット
     * @param date   日付文字列
     * @return 変換したDate
     */
    public static Date parse(final String format, final String date) {
        if (TextUtils.isEmpty(format)) {
            return null;
        }

        SimpleDateFormat sdf = new SimpleDateFormat(format);

        try {
            return sdf.parse(date);
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * 指定されたDateを基にして指定されたフォーマットで日付文字列
     * を返却する
     *
     * @param format フォーマット
     * @param date   日付
     * @return　指定されたフォーマットで日付文字列
     */
    public static String format(final String format, final Date date) {
        if (TextUtils.isEmpty(format)) {
            return "";
        }

        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }

}


