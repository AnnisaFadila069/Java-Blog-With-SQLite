import java.sql.*;

public class Home {
    public static void main(String[] args) {
        Connection c = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            // Koneksi ke database
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:db2.sqlite3");

            // Query untuk mendapatkan artikel dengan jumlah like terbanyak
            String sql = "SELECT a.id, a.title, a.author_email, a.created_at, " +
                         "       IFNULL((SELECT COUNT(*) FROM likes WHERE article_id = a.id), 0) AS like_count, " +
                         "       IFNULL((SELECT COUNT(*) FROM comments WHERE article_id = a.id), 0) AS comment_count " +
                         "FROM articles a " +
                         "ORDER BY like_count DESC, a.created_at DESC " + 
                         "LIMIT 10";

            stmt = c.createStatement();
            rs = stmt.executeQuery(sql);

            boolean hasResults = false;
            System.out.println("🔥 Artikel Terpopuler:");
            System.out.println("=======================================");
            
            while (rs.next()) {
                hasResults = true;
                System.out.println("📝 Artikel #" + rs.getInt("id"));
                System.out.println("✍️  Penulis  : " + rs.getString("author_email"));
                System.out.println("📅 Tanggal   : " + rs.getString("created_at"));
                System.out.println("📌 Judul     : " + rs.getString("title"));
                System.out.println("❤️ Like      : " + rs.getInt("like_count"));
                System.out.println("💬 Komentar  : " + rs.getInt("comment_count"));
                System.out.println("=======================================\n");
            }

            if (!hasResults) {
                System.out.println("❌ Tidak ada artikel terpopuler.");
            }

        } catch (Exception e) {
            System.err.println("❌ Terjadi kesalahan: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (c != null) c.close();
            } catch (SQLException ex) {
                System.err.println("❌ Gagal menutup koneksi: " + ex.getMessage());
            }
        }
    }
}
