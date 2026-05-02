package ap1.dayron.casas.model;

public enum Language {

    AFRIKAANS("af"),
    ALBANES("sq"),
    ARABE("ar"),
    ARMENIO("hy"),
    AZERBAIYANO("az"),
    BENGALI("bn"),
    BOSNIO("bs"),
    BULGARO("bg"),
    CATALAN("ca"),
    CHINO_SIMPLIFICADO("zh-CN"),
    CHINO_TRADICIONAL("zh-TW"),
    CROATA("hr"),
    CHECO("cs"),
    DANES("da"),
    NEERLANDES("nl"),
    INGLES("en"),
    ESTONIO("et"),
    FILIPINO("tl"),
    FINLANDES("fi"),
    FRANCES("fr"),
    GALLEGO("gl"),
    GEORGIANO("ka"),
    ALEMAN("de"),
    GRIEGO("el"),
    GUJARATI("gu"),
    HEBREO("iw"),
    HINDI("hi"),
    HUNGARO("hu"),
    ISLANDES("is"),
    INDONESIO("id"),
    IRLANDES("ga"),
    ITALIANO("it"),
    JAPONES("ja"),
    JAVANES("jw"),
    KANNADA("kn"),
    KAZAJO("kk"),
    COREANO("ko"),
    LATIN("la"),
    LETON("lv"),
    LITUANO("lt"),
    MACEDONIO("mk"),
    MALAYO("ms"),
    MALTES("mt"),
    MAORI("mi"),
    MARATHI("mr"),
    MONGOL("mn"),
    NEPALES("ne"),
    NORUEGO("no"),
    PERSA("fa"),
    POLACO("pl"),
    PORTUGUES("pt"),
    RUMANO("ro"),
    RUSO("ru"),
    SERBIO("sr"),
    ESLOVACO("sk"),
    ESLOVENO("sl"),
    SOMALI("so"),
    ESPANOL("es"),
    SWAHILI("sw"),
    SUECO("sv"),
    TAMIL("ta"),
    TELUGU("te"),
    TAILANDES("th"),
    TURCO("tr"),
    UCRANIANO("uk"),
    URDU("ur"),
    UZBEKO("uz"),
    VIETNAMITA("vi"),
    GALES("cy"),
    YIDDISH("yi"),
    ZULU("zu"),
    AUTO("auto");

    private final String code;

    Language(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static Language fromName(String name) {
        for (Language lang : values()) {
            if (lang.name().equalsIgnoreCase(name)) return lang;
        }
        throw new IllegalArgumentException("Idioma no soportado: '" + name + "'. Ejemplos validos: ESPANOL, INGLES, FRANCES, ALEMAN, PORTUGUES, ITALIANO, JAPONES, RUSO, AUTO");
    }
}
