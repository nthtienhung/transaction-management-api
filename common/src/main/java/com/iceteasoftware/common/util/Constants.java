package com.iceteasoftware.common.util;

public interface Constants {
    public interface Regex {
        public static final String ACCENTED_SPACE =
                "^[_a-zA-Z0-9-ÀÁÂÃÈÉÊÌÍÒÓÔÕÙÚĂĐĨŨƠàáâãèéêìíòóôõùúăđĩũơƯĂẠẢẤẦẨẪẬẮẰẲẴẶẸẺẼỀỀỂẾưăạảấầẩẫậắằẳẵặẹẻẽềềểếỄỆỈỊỌỎỐỒỔỖỘỚỜỞỠỢỤỦỨỪễệỉịọỏốồổỗộớờởỡợụủứừỬỮỰỲỴÝỶỸửữựỳỵỷỹ\\s\\W]*$";

        public static final String CODE = "[_a-zA-Z0-9]*";

        public static final String EMAIL =
                "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,35})$";

        public static final String HOUR = "([01]?[0-9]|2[0-3])";

        public static final String IPV4 = "\\b" + "((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\."
                + "((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\."
                + "((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\." + "((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])"
                + "\\b";

        public static final String IPV6 = "(([0-9a-fA-F]{1,4}:){7,7}[0-9a-fA-F]{1,4}|([0-9a-fA-F]{1,4}:){1,7}:"
                + "|([0-9a-fA-F]{1,4}:){1,6}:[0-9a-fA-F]{1,4}|([0-9a-fA-F]{1,4}:){1,5}(:[0-9a-fA-F]{1,4}){1,2}"
                + "|([0-9a-fA-F]{1,4}:){1,4}(:[0-9a-fA-F]{1,4}){1,3}|([0-9a-fA-F]{1,4}:){1,3}(:[0-9a-fA-F]{1,4}){1,4}"
                + "|([0-9a-fA-F]{1,4}:){1,2}(:[0-9a-fA-F]{1,4}){1,5}|[0-9a-fA-F]{1,4}:((:[0-9a-fA-F]{1,4}){1,6})"
                + "|:((:[0-9a-fA-F]{1,4}){1,7}|:)|fe80:(:[0-9a-fA-F]{0,4}){0,4}%[0-9a-zA-Z]{1,}"
                + "|::(ffff(:0{1,4}){0,1}:){0,1}((25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])\\.){3,3}(25[0-5]|(2[0-4]"
                + "|1{0,1}[0-9]){0,1}[0-9])|([0-9a-fA-F]{1,4}:){1,4}:((25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])\\."
                + "){3,3}(25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9]))";

        public static final String MINUTE = "[0-5][0-9]";

        public static final String NUMBER = "[+-]?\\d*(\\.\\d+)?";

        public static final String PHONE_REGEX = "^((\\s){0,}(0))((9|8|7|3|5|4|2)[0-9]{8,9}(\\s){0,})$";

        public static final String PORT =
                "^([0-9]{1,4}|[1-5][0-9]{4}|6[0-4][0-9]{3}|65[0-4][0-9]{2}|655[0-2][0-9]|6553[0-5])$";

        public static final String USERNAME = "^[a-zA-Z0-9]([._](?![._])|[a-zA-Z0-9]){1,72}[a-zA-Z0-9]$";

        public static final String VERSION = "^(\\d+\\.){1,3}(\\d+)";

        public static final String CAPITALIZED_FULLNAME_WITHOUT_DIACRITICS = "[A-Z ]*";

        public static final String OTP_REGEX = "^\\d{8}$";

    }

    public interface PhoneNumber {
        public static final String VN_COUNTRY_CODE = "84";

        public static final String LA_COUNTRY_CODE = "856";

        public static final String PREFIX = "0";
    }

    public interface AnalyzerDefName {
        public static final String EDGE_NGRAM_QUERY = "edgeNGram_query";

        public static final String EDGE_NGRAM = "edgeNGram";

        public static final String LOWERCASE = "lowercase";

        public static final String MIN_GRAM_SIZE = "minGramSize";

        public static final String MAX_GRAM_SIZE = "maxGramSize";
    }

    public interface HibernateDefName {
        public final static String STRING_ENCRYPTOR = "hibernateStringEncryptor";

        public final static String ENCRYPTED_STRING = "encryptedString";

        public final static String ENCRYPTOR_REGISTERED_NAME = "encryptorRegisteredName";
    }
}
