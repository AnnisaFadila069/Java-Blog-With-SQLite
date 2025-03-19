import java.sql.*;

public class Timeline {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Gunakan: java Timeline <user_email>");
            return;
        }

        String userEmail = args[0];
        Connection c = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            // Koneksi ke database
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:db2.sqlite3");

            // Query untuk mendapatkan artikel terbaru dari user yang di-subscribe
            String sql = "SELECT a.id, a.title, a.author_email, a.created_at, " +
             "       (SELECT COUNT(*) FROM likes WHERE article_id = a.id) AS like_count, " +
             "       (SELECT COUNT(*) FROM comments WHERE article_id = a.id) AS comment_count " +
             "FROM articles a " +
             "JOIN subscriptions s ON a.author_email = s.blog_author_email " +  // Perbaikan di sini
             "WHERE s.subscriber_email = ? " +
             "ORDER BY a.created_at DESC LIMIT 10";


            stmt = c.prepareStatement(sql);
            stmt.setString(1, userEmail);
            rs = stmt.executeQuery();

            boolean hasResults = false;
            System.out.println("📰 Timeline Artikel Terbaru:");
            while (rs.next()) {
                hasResults = true;
                System.out.println("=======================================");
                System.out.println("📝 Artikel #" + rs.getInt("id"));
                System.out.println("✍️  Penulis  : " + rs.getString("author_email"));
                System.out.println("📅 Tanggal   : " + rs.getString("created_at"));
                System.out.println("📌 Judul     : " + rs.getString("title"));
                System.out.println("❤️ Like      : " + rs.getInt("like_count"));
                System.out.println("💬 Komentar  : " + rs.getInt("comment_count"));
                System.out.println("=======================================\n");
            }

            if (!hasResults) {
                System.out.println("❌ Tidak ada artikel terbaru dari penulis yang Anda ikuti.");
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
