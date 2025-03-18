import java.sql.*;

public class Inbox {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Gunakan: java Inbox <email>");
            return;
        }

        String receiverEmail = args[0];

        Connection c = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            // Koneksi ke SQLite
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:db2.sqlite3");

            // Query untuk mendapatkan artikel di inbox pengguna
            String sql = "SELECT a.id, a.author_email, a.created_at, a.title, a.content, i.received_at " +
                         "FROM inbox i " +
                         "JOIN articles a ON i.article_id = a.id " +
                         "WHERE i.receiver_email = ? " +
                         "ORDER BY i.received_at DESC";

            stmt = c.prepareStatement(sql);
            stmt.setString(1, receiverEmail);
            rs = stmt.executeQuery();

            System.out.println("📥 Kotak Masuk Artikel untuk: " + receiverEmail);
            System.out.println("------------------------------------------------------");

            boolean hasArticles = false;
            while (rs.next()) {
                hasArticles = true;
                int articleId = rs.getInt("id");
                String authorEmail = rs.getString("author_email");
                String createdAt = rs.getString("created_at");
                String title = rs.getString("title");
                String content = rs.getString("content");
                String receivedAt = rs.getString("received_at");

                System.out.println("📌 [" + receivedAt + "] Artikel #" + articleId);
                System.out.println("✍️  Penulis: " + authorEmail);
                System.out.println("📅 Diposting: " + createdAt);
                System.out.println("📝 Judul: " + title);
                System.out.println("📖 Konten: " + content);
                System.out.println("------------------------------------------------------");
            }

            if (!hasArticles) {
                System.out.println("❌ Tidak ada artikel di kotak masuk Anda.");
            }

        } catch (Exception e) {
            System.err.println("❌ Terjadi kesalahan: " + e.getMessage());
        } finally {
            // Menutup koneksi
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
