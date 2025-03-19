import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PostArticle {
    public static void main(String[] args) throws Exception {
        if (args.length < 3) {
            System.out.println("Usage: java PostArticle <email> <title> <content>");
            return;
        }

        String email = args[0];
        String title = args[1];
        StringBuilder content = new StringBuilder();
        for (int i = 2; i < args.length; i++) {
            content.append(args[i]).append(" ");
        }

        // Ambil waktu saat ini
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDate = now.format(formatter);

        // Koneksi ke SQLite
        Class.forName("org.sqlite.JDBC");
        Connection c = DriverManager.getConnection("jdbc:sqlite:db2.sqlite3");

        // Query SQL untuk menyimpan artikel
        String insertArticleSql = "INSERT INTO articles(author_email, created_at, title, content) VALUES (?, ?, ?, ?)";
        PreparedStatement articleStmt = c.prepareStatement(insertArticleSql, Statement.RETURN_GENERATED_KEYS);
        articleStmt.setString(1, email);
        articleStmt.setString(2, formattedDate);
        articleStmt.setString(3, title);
        articleStmt.setString(4, content.toString().trim());
        articleStmt.executeUpdate();

        // Dapatkan ID artikel yang baru saja dibuat
        ResultSet generatedKeys = articleStmt.getGeneratedKeys();
        int articleId = -1;
        if (generatedKeys.next()) {
            articleId = generatedKeys.getInt(1);
        }
        generatedKeys.close();
        articleStmt.close();

        // Kirim artikel ke semua subscriber yang terdaftar untuk blog ini
        if (articleId != -1) {
            // Menyiapkan query untuk mendapatkan email subscriber berdasarkan blog author email
            String selectSubscribersSql = "SELECT subscriber_email FROM subscriptions WHERE blog_author_email = ?";
            PreparedStatement subscribersStmt = c.prepareStatement(selectSubscribersSql);
            subscribersStmt.setString(1, email);
            ResultSet subscribersResult = subscribersStmt.executeQuery();

            // Mengirim notifikasi ke setiap subscriber (simulasi pub/sub)
            while (subscribersResult.next()) {
                String subscriberEmail = subscribersResult.getString("subscriber_email");
                // Pada titik ini, Anda dapat mengirimkan pemberitahuan ke subscriber,
                // bisa menggunakan notifikasi push, email, atau mekanisme lainnya.
                sendNotification(subscriberEmail, title, content.toString());
            }

            subscribersResult.close();
            subscribersStmt.close();
        }

        // Tutup koneksi
        c.close();

        System.out.println("✅ Artikel berhasil diposting dan dikirim ke subscriber!");
    }

    // Simulasi mengirim notifikasi ke subscriber
    private static void sendNotification(String subscriberEmail, String title, String content) {
        // Di dunia nyata, Anda bisa mengganti ini dengan sistem notifikasi push, email, dll.
        System.out.println("Notifikasi dikirim ke " + subscriberEmail + ": Artikel baru diposting - " + title);
        System.out.println("Konten Artikel: " + content);
    }
}
