import repository.DatabaseManager;

public class Main {
    public static void main(String[] args) {
        // Uygulama başlarken tabloları otomatik oluşturur/kontrol eder
        DatabaseManager.initializeDatabase();
        // Test verileri veya konsol denemeleri yerine artık arayüzü başlatıyoruz.
        ui.App.main(args);
    }
}