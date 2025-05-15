package api;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseHelper {

    public static final String URL3 = "jdbc:postgresql://10.125.20.200:5432/scmain3";
    public static final String DB_USER3 = "scadmin3";
    public static final String DB_PASSWORD3 = "Al8sWbbZ14ZR";
    public static final String DB_USERML = "okr_admin";
    public static final String DB_PASSWORDML = "ajs98fwj-*32fFQ";
    public static final String URL1ML = "jdbc:postgresql://10.125.20.230:5432/postgres";

    /**
     * Проверяет статус записи в таблице task с ретраями.
     *
     * @param taskId ID задачи
     * @param desiredStatus Статус, которого ожидаем
     * @param maxRetries Максимальное число попыток
     * @param delayMillis Задержка между попытками в миллисекундах
     * @return true, если статус стал нужным, иначе false
     */
    public boolean waitForTaskStatusInDB(int taskId, String desiredStatus, int maxRetries, long delayMillis) {
        int attempt = 0;

        while (attempt < maxRetries) {
            String currentStatus = getTaskStatus(taskId);
            System.out.println("Попытка " + (attempt + 1) + ": текущий статус = " + currentStatus);

            if (desiredStatus.equals(currentStatus)) {
                return true;
            }

            attempt++;
            try {
                Thread.sleep(delayMillis);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
        }
        return false; // Не достигли нужного статуса за все попытки
    }

    /**
     * Получает текущий статус задачи из базы данных.
     */
    private String getTaskStatus(int taskId) {
        String status = null;
        try (Connection conn = DriverManager.getConnection(URL1ML, DB_USERML, DB_PASSWORDML);
             PreparedStatement stmt = conn.prepareStatement("SELECT status FROM task WHERE id = ?")) {
            stmt.setInt(1, taskId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                status = rs.getString("status");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return status;
    }
}