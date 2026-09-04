package de.muenchen.ehrenamtjustiz.eai.personeninfo.util;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("PMD.ExcessivePublicCount")
public enum CountryCode {
    AD("Andorra", "AND", 20),
    AE("Vereinigte Arabische Emirate", "ARE", 784),
    AF("Afghanistan", "AFG", 4),
    AG("Antigua und Barbuda", "ATG", 28),
    AI("Anguilla", "AIA", 660),
    AL("Albanien", "ALB", 8),
    AM("Armenien", "ARM", 51),
    AO("Angola", "AGO", 24),
    AQ("Antarktis", "ATA", 10),
    AR("Argentinien", "ARG", 32),
    AS("Amerikanisch-Samoa", "ASM", 16),
    AT("Österreich", "AUT", 40),
    AU("Australien", "AUS", 36),
    AW("Aruba", "ABW", 533),
    AX("Åland", "ALA", 248),
    AZ("Aserbaidschan", "AZE", 31),
    BA("Bosnien und Herzegowina", "BIH", 70),
    BB("Barbados", "BRB", 52),
    BD("Bangladesch", "BGD", 50),
    BE("Belgien", "BEL", 56),
    BF("Burkina Faso", "BFA", 854),
    BG("Bulgarien", "BGR", 100),
    BH("Bahrain", "BHR", 48),
    BI("Burundi", "BDI", 108),
    BJ("Benin", "BEN", 204),
    BL("Saint-Barthélemy", "BLM", 652),
    BM("Bermuda", "BMU", 60),
    BN("Brunei Darussalam", "BRN", 96),
    BO("Bolivien", "BOL", 68),
    BQ("Bonaire, Sint Eustatius und Saba", "BES", 535),
    BR("Brasilien", "BRA", 76),
    BS("Bahamas", "BHS", 44),
    BT("Bhutan", "BTN", 64),
    BV("Bouvetinsel", "BVT", 74),
    BW("Botswana", "BWA", 72),
    BY("Belarus", "BLR", 112),
    BZ("Belize", "BLZ", 84),
    CA("Kanada", "CAN", 124),
    CC("Kokosinseln", "CCK", 166),
    CD("Kongo, Demokratische Republik", "COD", 180),
    CF("Zentralafrikanische Republik", "CAF", 140),
    CG("Kongo", "COG", 178),
    CH("Schweiz", "CHE", 756),
    CI("Côte d'Ivoire", "CIV", 384),
    CK("Cookinseln", "COK", 184),
    CL("Chile", "CHL", 152),
    CM("Kamerun", "CMR", 120),
    CN("China", "CHN", 156),
    CO("Kolumbien", "COL", 170),
    CR("Costa Rica", "CRI", 188),
    CU("Kuba", "CUB", 192),
    CV("Cabo Verde", "CPV", 132),
    CW("Curaçao", "CUW", 531),
    CX("Weihnachtsinsel", "CXR", 162),
    CY("Zypern", "CYP", 196),
    CZ("Tschechien", "CZE", 203),
    DE("Deutschland", "DEU", 276),
    DJ("Dschibuti", "DJI", 262),
    DK("Dänemark", "DNK", 208),
    DM("Dominica", "DMA", 212),
    DO("Dominikanische Republik", "DOM", 214),
    DZ("Algerien", "DZA", 12),
    EC("Ecuador", "ECU", 218),
    EE("Estland", "EST", 233),
    EG("Ägypten", "EGY", 818),
    EH("Westsahara", "ESH", 732),
    ER("Eritrea", "ERI", 232),
    ES("Spanien", "ESP", 724),
    ET("Äthiopien", "ETH", 231),
    FI("Finnland", "FIN", 246),
    FJ("Fidschi", "FJI", 242),
    FK("Falklandinseln", "FLK", 238),
    FM("Mikronesien", "FSM", 583),
    FO("Färöer", "FRO", 234),
    FR("Frankreich", "FRA", 250),
    GA("Gabun", "GAB", 266),
    GB("Vereinigtes Königreich", "GBR", 826),
    GD("Grenada", "GRD", 308),
    GE("Georgien", "GEO", 268),
    GF("Französisch-Guayana", "GUF", 254),
    GG("Guernsey", "GGY", 831),
    GH("Ghana", "GHA", 288),
    GI("Gibraltar", "GIB", 292),
    GL("Grönland", "GRL", 304),
    GM("Gambia", "GMB", 270),
    GN("Guinea", "GIN", 324),
    GP("Guadeloupe", "GLP", 312),
    GQ("Äquatorialguinea", "GNQ", 226),
    GR("Griechenland", "GRC", 300),
    GS("Südgeorgien und die Südlichen Sandwichinseln", "SGS", 239),
    GT("Guatemala", "GTM", 320),
    GU("Guam", "GUM", 316),
    GW("Guinea-Bissau", "GNB", 624),
    GY("Guyana", "GUY", 328),
    HK("Hongkong", "HKG", 344),
    HM("Heard und McDonaldinseln", "HMD", 334),
    HN("Honduras", "HND", 340),
    HR("Kroatien", "HRV", 191),
    HT("Haiti", "HTI", 332),
    HU("Ungarn", "HUN", 348),
    ID("Indonesien", "IDN", 360),
    IE("Irland", "IRL", 372),
    IL("Israel", "ISR", 376),
    IM("Insel Man", "IMN", 833),
    IN("Indien", "IND", 356),
    IO("Britisches Territorium im Indischen Ozean", "IOT", 86),
    IQ("Irak", "IRQ", 368),
    IR("Iran", "IRN", 364),
    IS("Island", "ISL", 352),
    IT("Italien", "ITA", 380),
    JE("Jersey", "JEY", 832),
    JM("Jamaika", "JAM", 388),
    JO("Jordanien", "JOR", 400),
    JP("Japan", "JPN", 392),
    KE("Kenia", "KEN", 404),
    KG("Kirgisistan", "KGZ", 417),
    KH("Kambodscha", "KHM", 116),
    KI("Kiribati", "KIR", 296),
    KM("Komoren", "COM", 174),
    KN("St. Kitts und Nevis", "KNA", 659),
    KP("Nordkorea", "PRK", 408),
    KR("Südkorea", "KOR", 410),
    KW("Kuwait", "KWT", 414),
    KY("Kaimaninseln", "CYM", 136),
    KZ("Kasachstan", "KAZ", 398),
    LA("Laos", "LAO", 418),
    LB("Libanon", "LBN", 422),
    LC("St. Lucia", "LCA", 662),
    LI("Liechtenstein", "LIE", 438),
    LK("Sri Lanka", "LKA", 144),
    LR("Liberia", "LBR", 430),
    LS("Lesotho", "LSO", 426),
    LT("Litauen", "LTU", 440),
    LU("Luxemburg", "LUX", 442),
    LV("Lettland", "LVA", 428),
    LY("Libyen", "LBY", 434),
    MA("Marokko", "MAR", 504),
    MC("Monaco", "MCO", 492),
    MD("Republik Moldau", "MDA", 498),
    ME("Montenegro", "MNE", 499),
    MF("Saint-Martin", "MAF", 663),
    MG("Madagaskar", "MDG", 450),
    MH("Marshallinseln", "MHL", 584),
    MK("Nordmazedonien", "MKD", 807),
    ML("Mali", "MLI", 466),
    MM("Myanmar", "MMR", 104),
    MN("Mongolei", "MNG", 496),
    MO("Macau", "MAC", 446),
    MP("Nördliche Marianen", "MNP", 580),
    MQ("Martinique", "MTQ", 474),
    MR("Mauretanien", "MRT", 478),
    MS("Montserrat", "MSR", 500),
    MT("Malta", "MLT", 470),
    MU("Mauritius", "MUS", 480),
    MV("Malediven", "MDV", 462),
    MW("Malawi", "MWI", 454),
    MX("Mexiko", "MEX", 484),
    MY("Malaysia", "MYS", 458),
    MZ("Mosambik", "MOZ", 508),
    NA("Namibia", "NAM", 516),
    NC("Neukaledonien", "NCL", 540),
    NE("Niger", "NER", 562),
    NF("Norfolkinsel", "NFK", 574),
    NG("Nigeria", "NGA", 566),
    NI("Nicaragua", "NIC", 558),
    NL("Niederlande", "NLD", 528),
    NO("Norwegen", "NOR", 578),
    NP("Nepal", "NPL", 524),
    NR("Nauru", "NRU", 520),
    NU("Niue", "NIU", 570),
    NZ("Neuseeland", "NZL", 554),
    OM("Oman", "OMN", 512),
    PA("Panama", "PAN", 591),
    PE("Peru", "PER", 604),
    PF("Französisch-Polynesien", "PYF", 258),
    PG("Papua-Neuguinea", "PNG", 598),
    PH("Philippinen", "PHL", 608),
    PK("Pakistan", "PAK", 586),
    PL("Polen", "POL", 616),
    PM("Saint-Pierre und Miquelon", "SPM", 666),
    PN("Pitcairninseln", "PCN", 612),
    PR("Puerto Rico", "PRI", 630),
    PS("Staat Palästina", "PSE", 275),
    PT("Portugal", "PRT", 620),
    PW("Palau", "PLW", 585),
    PY("Paraguay", "PRY", 600),
    QA("Katar", "QAT", 634),
    RE("Réunion", "REU", 638),
    RO("Rumänien", "ROU", 642),
    RS("Serbien", "SRB", 688),
    RU("Russische Föderation", "RUS", 643),
    RW("Ruanda", "RWA", 646),
    SA("Saudi-Arabien", "SAU", 682),
    SB("Salomonen", "SLB", 90),
    SC("Seychellen", "SYC", 690),
    SD("Sudan", "SDN", 729),
    SE("Schweden", "SWE", 752),
    SG("Singapur", "SGP", 702),
    SH("St. Helena, Ascension und Tristan da Cunha", "SHN", 654),
    SI("Slowenien", "SVN", 705),
    SJ("Svalbard und Jan Mayen", "SJM", 744),
    SK("Slowakei", "SVK", 703),
    SL("Sierra Leone", "SLE", 694),
    SM("San Marino", "SMR", 674),
    SN("Senegal", "SEN", 686),
    SO("Somalia", "SOM", 706),
    SR("Suriname", "SUR", 740),
    SS("Südsudan", "SSD", 728),
    ST("São Tomé und Príncipe", "STP", 678),
    SV("El Salvador", "SLV", 222),
    SX("Sint Maarten", "SXM", 534),
    SY("Syrien", "SYR", 760),
    SZ("Eswatini", "SWZ", 748),
    TC("Turks- und Caicosinseln", "TCA", 796),
    TD("Tschad", "TCD", 148),
    TF("Französische Süd- und Antarktisgebiete", "ATF", 260),
    TG("Togo", "TGO", 768),
    TH("Thailand", "THA", 764),
    TJ("Tadschikistan", "TJK", 762),
    TK("Tokelau", "TKL", 772),
    TL("Timor-Leste", "TLS", 626),
    TM("Turkmenistan", "TKM", 795),
    TN("Tunesien", "TUN", 788),
    TO("Tonga", "TON", 776),
    TR("Türkei", "TUR", 792),
    TT("Trinidad und Tobago", "TTO", 780),
    TV("Tuvalu", "TUV", 798),
    TW("Taiwan", "TWN", 158),
    TZ("Tansania", "TZA", 834),
    UA("Ukraine", "UKR", 804),
    UG("Uganda", "UGA", 800),
    UM("United States Minor Outlying Islands", "UMI", 581),
    US("Vereinigte Staaten von Amerika", "USA", 840),
    UY("Uruguay", "URY", 858),
    UZ("Usbekistan", "UZB", 860),
    VA("Vatikanstadt", "VAT", 336),
    VC("St. Vincent und die Grenadinen", "VCT", 670),
    VE("Venezuela", "VEN", 862),
    VG("Britische Jungferninseln", "VGB", 92),
    VI("Amerikanische Jungferninseln", "VIR", 850),
    VN("Vietnam", "VNM", 704),
    VU("Vanuatu", "VUT", 548),
    WF("Wallis und Futuna", "WLF", 876),
    WS("Samoa", "WSM", 882),
    YE("Jemen", "YEM", 887),
    YT("Mayotte", "MYT", 175),
    ZA("Südafrika", "ZAF", 710),
    ZM("Sambia", "ZMB", 894),
    ZW("Simbabwe", "ZWE", 716);
    // @formatter:on

    private static final Map<String, CountryCode> ALPHA3MAP = new HashMap<>();
    private static final Map<Integer, CountryCode> NUMERICMAP = new HashMap<>();

    static {
        for (final CountryCode cc : values()) {
            ALPHA3MAP.put(cc.getAlpha3(), cc);
            NUMERICMAP.put(cc.getNumeric(), cc);
        }
    }

    private final String name;
    private final String alpha3;
    private final int numeric;

    CountryCode(final String name, final String alpha3, final int numeric) {
        this.name = name;
        this.alpha3 = alpha3;
        this.numeric = numeric;
    }

    /// Get a CountryCode that corresponds to a given ISO 3166-1 <a
    /// href="http://en.wikipedia.org/wiki/ISO_3166-1_alpha-2">alpha-2</a> or <a
    /// href="http://en.wikipedia.org/wiki/ISO_3166-1_alpha-3">alpha-3</a> code.
    ///
    /// @param code An ISO 3166-1 <a href="http://en.wikipedia.org/wiki/ISO_3166-1_alpha-2" >alpha-2</a>
    ///            or <a href="http://en.wikipedia.org/wiki/ISO_3166-1_alpha-3" >alpha-3</a> code.
    ///
    /// @return A CountryCode instance, or null if not found.
    public static CountryCode getByCode(final String code) {
        if (code == null) {
            return null;
        }

        switch (code.length()) {
        case 2:
            return getByAlpha2Code(code);

        case 3:
            return getByAlpha3Code(code);

        default:
            return null;
        }
    }

    private static CountryCode getByAlpha2Code(final String code) {
        try {
            return Enum.valueOf(CountryCode.class, code);
        } catch (final IllegalArgumentException e) {
            return null;
        }
    }

    private static CountryCode getByAlpha3Code(final String code) {
        return ALPHA3MAP.get(code);
    }

    /// Get a CountryCode that corresponds to a given <a
    /// href="http://en.wikipedia.org/wiki/ISO_3166-1_numeric">ISO 3166-1 numeric</a> code.
    ///
    /// @param code An <a href="http://en.wikipedia.org/wiki/ISO_3166-1_numeric" >ISO 3166-1 numeric</a>
    ///            code.
    ///
    /// @return A CountryCode instance, or null if not found.
    public static CountryCode getByCode(final int code) {
        return NUMERICMAP.get(code);
    }

    /// Get the country name.
    ///
    /// @return The country name.
    public String getName() {
        return name;
    }

    /// Get the <a href="http://en.wikipedia.org/wiki/ISO_3166-1_alpha-2" >ISO 3166-1 alpha-2</a> code.
    ///
    /// @return The <a href="http://en.wikipedia.org/wiki/ISO_3166-1_alpha-2" >ISO 3166-1
    ///         alpha-2</a> code.
    public String getAlpha2() {
        return name();
    }

    /// Get the <a href="http://en.wikipedia.org/wiki/ISO_3166-1_alpha-3" >ISO 3166-1 alpha-3</a> code.
    ///
    /// @return The <a href="http://en.wikipedia.org/wiki/ISO_3166-1_alpha-3" >ISO 3166-1
    ///         alpha-3</a> code.
    public String getAlpha3() {
        return alpha3;
    }

    /// Get the <a href="http://en.wikipedia.org/wiki/ISO_3166-1_numeric" >ISO 3166-1 numeric</a> code.
    ///
    /// @return The <a href="http://en.wikipedia.org/wiki/ISO_3166-1_numeric" >ISO 3166-1
    ///         numeric</a> code.
    public int getNumeric() {
        return numeric;
    }
}
