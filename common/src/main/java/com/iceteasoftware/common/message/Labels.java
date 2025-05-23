package com.iceteasoftware.common.message;

import com.iceteasoftware.common.constant.Constants;
import com.iceteasoftware.common.util.GetterUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Component
@Slf4j
public class Labels {

    public interface Language {
        public static final String EN = "en";

        public static final String VI = "vi";
    }

    public interface Country {
        public static final String VN = "VN";

        public static final String US = "us";
    }

    /**
     * The Constant US.
     */
    public static final Locale US = new Locale(Language.EN, Country.US);


    /**
     * The Constant VN.
     */
    public static final Locale VN = new Locale(Language.VI, Country.VN);
    /** The Constant _log. */

    /**
     * The c available locale list.
     */
    private static List<Locale> cAvailableLocaleList;

    /**
     * The message source.
     */
    private static MessageSource messageSource;

    @Autowired
    public Labels(MessageSource messageSource) {
        Labels.messageSource = messageSource;
    }

    /**
     * Available locale list.
     *
     * @return the list
     */
    public static List<Locale> availableLocaleList() {

        if (cAvailableLocaleList == null) {

            cAvailableLocaleList = new ArrayList<>();

            cAvailableLocaleList.add(US);
            cAvailableLocaleList.add(VN);
        }

        return cAvailableLocaleList;

    }

    /**
     * Gets the default locale.
     *
     * @return the default locale
     */
    public static Locale getDefaultLocale() {
        return US;
    }

    /**
     * Gets the labels.
     *
     * @param key the key
     * @return the labels
     */
    public static String getLabels(String key) {
        return getLabels(key, null, getRequestLocale());
    }

    /**
     * Gets the labels.
     *
     * @param key    the key
     * @param locale the locale
     * @return the labels
     */
    public static String getLabels(String key, Locale locale) {
        return getLabels(key, null, locale);
    }

    /**
     * Gets the labels.
     *
     * @param key  the key
     * @param objs the objs
     * @return the labels
     */
    public static String getLabels(String key, Object[] objs) {
        return getLabels(key, objs, getRequestLocale());
    }

    /**
     * Gets the labels.
     *
     * @param key    the key
     * @param objs   the objs
     * @param locale the locale
     * @return the labels
     */
    public static String getLabels(String key, Object[] objs, Locale locale) {
        String ms = null;

        try {
            if (locale == null) {
                locale = getDefaultLocale();
            }

            ms = messageSource.getMessage(key, objs, locale);
        } catch (NoSuchMessageException ex) {
//            log.error("Can not get label for key {} , return default value.", key, ex);
        }

        return ms;
    }

    /**
     * Gets the labels.
     *
     * @param key      the key
     * @param objs     the objs
     * @param language the language
     * @return the labels
     */
    public static String getLabels(String key, Object[] objs, String language) {
        String ms = null;

        try {
            Locale locale = new Locale(language);

            ms = messageSource.getMessage(key, objs, locale);
        } catch (NoSuchMessageException ex) {
//            log.error("Can not get label for key {} , return default value.", key, ex);
        }

        return ms;
    }

    /**
     * Gets the labels.
     *
     * @param key      the key
     * @param objs     the objs
     * @param language the language
     * @param country  the country
     * @return the labels
     */
    public static String getLabels(String key, Object[] objs, String language, String country) {
        String ms = null;

        try {
            Locale locale = new Locale(language, country);

            ms = messageSource.getMessage(key, objs, locale);
        } catch (NoSuchMessageException ex) {
//            log.error("Can not get label for key {} , return default value.", key, ex);
        }

        return ms;
    }

    /**
     * Gets the labels.
     *
     * @param key      the key
     * @param language the language
     * @return the labels
     */
    public static String getLabels(String key, String language) {
        return getLabels(key, null, language);
    }

    /**
     * Gets the locale.
     *
     * @param locale the locale
     * @return the locale
     */
    public static Locale getLocale(Locale locale) {
        if (locale == null || !isAvailableLocale(locale)) {
            locale = getDefaultLocale();
        }

        return locale;
    }

    public static Locale getRequestLocale() {
        String language = getLanguageFromRequest();

        switch (language) {
            case Language.VI:
                return VN;
            case Language.EN:
                return US;
            default:
                return US;
        }
    }

    public static String getLanguageFromRequest() {
        HttpServletRequest request = null;

        RequestAttributes requestAttr = RequestContextHolder.getRequestAttributes();

        if (requestAttr instanceof ServletRequestAttributes) {
            request = ((ServletRequestAttributes) requestAttr).getRequest();
        }

        if (request == null) {
            return Language.EN;
        }
        return GetterUtil.getString(request.getHeader(Constants.DEFAULT_LOCALE), Language.EN);

    }

    /**
     * Gets the locale.
     *
     * @param language the language
     * @return the locale
     */
    public static Locale getLocale(String language) {
        Locale locale = null;

        if (language != null) {
            locale = new Locale(language);
        }

        return getLocale(locale);
    }


    /**
     * Gets the locale.
     *
     * @param language the language
     * @param country  the country
     * @return the locale
     */
    public static Locale getLocale(String language, String country) {
        Locale locale = language != null && country != null ? new Locale(language, country) : getLocale(language);

        return getLocale(locale);
    }

    /**
     * Gets the message source.
     *
     * @return the message source
     */
    public static MessageSource getMessageSource() {
        return messageSource;
    }

    /**
     * Checks if is available locale.
     *
     * @param locale the locale
     * @return true, if is available locale
     */
    public static boolean isAvailableLocale(Locale locale) {

        return availableLocaleList().contains(locale);

    }

}
