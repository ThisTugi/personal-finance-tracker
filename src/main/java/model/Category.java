package model;

public class Category {
    public static final String STOCK = "Hisse Senedi";
    public static final String COMMODITY = "Emtia / Altın";
    public static final String CRYPTO = "Kripto Varlık";
    public static final String CASH = "Nakit / Döviz";

    public static final String FOOD = "Gıda / Market";
    public static final String TRANSPORT = "Ulaşım";
    public static final String HOUSING = "Kira / Barınma";
    public static final String BILLS = "Faturalar";
    public static final String EDUCATION = "Eğitim";
    public static final String ENTERTAINMENT = "Eğlence";
    public static final String OTHER = "Diğer";

    public static String[] getInvestmentCategories() {
        return new String[] { STOCK, COMMODITY, CRYPTO, CASH };
    }

    public static String[] getExpenseCategories() {
        return new String[] { FOOD, TRANSPORT, HOUSING, BILLS, EDUCATION, ENTERTAINMENT, OTHER };
    }
}