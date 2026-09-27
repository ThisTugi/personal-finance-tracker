import repository.DatabaseManager;

public class Main {
    public static void main(String[] args) {
        DatabaseManager.initializeDatabase();
        ui.App.main(args);
    }
}