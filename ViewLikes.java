import java.sql.*;

public class ViewLikes {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Gunakan: java ViewLikes <article_id>");
            return;
        }

        int articleId;
        try {
            articleId = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            System.out.println("❌ Error: ID artikel harus berupa angka.");
            return;
        }

        Connection c = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            // Koneksi ke SQLite
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:db2.sqlite3");

            // Cek apakah artikel ada
            String checkArticleSql = "SELECT title FROM articles WHERE id = ?";
            stmt = c.prepareStatement(checkArticleSql);
            stmt.setInt(1, articleId);
            rs = stmt.executeQuery();

            if (!rs.next()) {
                System.out.println("❌ Artikel dengan ID " + articleId + " tidak ditemukan.");
                return;
            }

            String articleTitle = rs.getString("title");

            // Menghitung jumlah like pada artikel
            String countLikesSql = "SELECT COUNT(*) FROM likes WHERE article_id = ?";
            stmt = c.prepareStatement(countLikesSql);
            stmt.setInt(1, articleId);
            rs = stmt.executeQuery();

            int likeCount = 0;
            if (rs.next()) {
                likeCount = rs.getInt(1);
            }

            // Menampilkan hasil
            System.out.println("=======================================");
            System.out.println("👍 Jumlah Like untuk Artikel: " + articleTitle + " (ID: " + articleId + ")");
            System.out.println("=======================================");
            System.out.println("❤️ " + likeCount + " like(s)");

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
